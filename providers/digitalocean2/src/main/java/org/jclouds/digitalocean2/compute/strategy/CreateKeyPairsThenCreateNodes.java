/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.digitalocean2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.compute.options.DigitalOcean2TemplateOptions;
import org.jclouds.digitalocean2.domain.Key;
import org.jclouds.digitalocean2.ssh.DSAKeys;
import org.jclouds.digitalocean2.ssh.ECDSAKeys;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshKeyPairGenerator;
import org.jclouds.ssh.SshKeys;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

@Singleton
public class CreateKeyPairsThenCreateNodes extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DigitalOcean2Api api;
   private final SshKeyPairGenerator keyGenerator;
   private final Function<String, PublicKey> sshKeyToPublicKey;

   @Inject
   protected CreateKeyPairsThenCreateNodes(
         CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
         ListNodesStrategy listNodesStrategy,
         GroupNamingConvention.Factory namingConvention,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
         DigitalOcean2Api api, SshKeyPairGenerator keyGenerator, Function<String, PublicKey> sshKeyToPublicKey) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
            customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = checkNotNull(api, "api cannot be null");
      this.keyGenerator = checkNotNull(keyGenerator, "keyGenerator cannot be null");
      checkNotNull(userExecutor, "userExecutor cannot be null");
      this.sshKeyToPublicKey = checkNotNull(sshKeyToPublicKey, "sshKeyToPublicKey cannot be null");
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
         Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
         Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      DigitalOcean2TemplateOptions options = template.getOptions().as(DigitalOcean2TemplateOptions.class);
      Set<Integer> generatedSshKeyIds = Sets.newHashSet();

      // If no key has been configured and the auto-create option is set, then generate a key pair
      if (options.getSshKeyIds().isEmpty() && options.getAutoCreateKeyPair()
            && Strings.isNullOrEmpty(options.getPublicKey())) {
         generateKeyPairAndAddKeyToSet(options, generatedSshKeyIds, group);
      }

      // If there is a script to run in the node, make sure a private key has
      // been configured so jclouds will be able to access the node
      if (options.getRunScript() != null && Strings.isNullOrEmpty(options.getLoginPrivateKey())) {
         logger.warn(">> A runScript has been configured but no SSH key has been provided."
               + " Authentication will delegate to the ssh-agent");
      }

      // If there is a key configured, then make sure there is a key pair for it
      if (!Strings.isNullOrEmpty(options.getPublicKey())) {
         createKeyPairForPublicKeyInOptionsAndAddToSet(options, generatedSshKeyIds);
      }

      // Set all keys (the provided and the auto-generated) in the options object so the
      // DigitalOceanComputeServiceAdapter adds them all
      options.sshKeyIds(Sets.union(generatedSshKeyIds, options.getSshKeyIds()));

      Map<?, ListenableFuture<Void>> responses = super.execute(group, count, template, goodNodes, badNodes,
            customizationResponses);

      // Key pairs in DigitalOcean are only required to create the Droplets. They aren't used anymore so it is better
      // to delete the auto-generated key pairs at this point where we know exactly which ones have been
      // auto-generated by jclouds.
      registerAutoGeneratedKeyPairCleanupCallbacks(responses, generatedSshKeyIds);

      return responses;
   }

   private void createKeyPairForPublicKeyInOptionsAndAddToSet(DigitalOcean2TemplateOptions options,
         Set<Integer> generatedSshKeyIds) {
      logger.debug(">> checking if the key pair already exists...");

      PublicKey userKey = sshKeyToPublicKey.apply(options.getPublicKey());
      String userFingerprint = computeFingerprint(userKey);
      Key key = api.keyApi().get(userFingerprint);  

      if (key == null) {
         logger.debug(">> key pair not found. creating a new one...");

         Key newKey = api.keyApi().create(userFingerprint, options.getPublicKey());

         generatedSshKeyIds.add(newKey.id());
         logger.debug(">> key pair created! %s", newKey);
      } else {
         logger.debug(">> key pair found! %s", key);
         generatedSshKeyIds.add(key.id());
      }
   }

   private void generateKeyPairAndAddKeyToSet(DigitalOcean2TemplateOptions options, Set<Integer> generatedSshKeyIds, String prefix) {
      logger.debug(">> creating default keypair for node...");

      Map<String, String> defaultKeys = keyGenerator.get();

      Key defaultKey = api.keyApi().create(prefix + "-" + System.getProperty("user.name"), defaultKeys.get("public"));
      generatedSshKeyIds.add(defaultKey.id());

      logger.debug(">> keypair created! %s", defaultKey);

      // If a private key has not been explicitly set, configure the auto-generated one
      if (Strings.isNullOrEmpty(options.getLoginPrivateKey())) {
         options.overrideLoginPrivateKey(defaultKeys.get("private"));
      }
   }

   private void registerAutoGeneratedKeyPairCleanupCallbacks(Map<?, ListenableFuture<Void>> responses,
         final Set<Integer> generatedSshKeyIds) {
      // The Futures.allAsList fails immediately if some of the futures fail. The Futures.successfulAsList, however,
      // returns a list containing the results or 'null' for those futures that failed. We want to wait for all them
      // (even if they fail), so better use the latter form.
      ListenableFuture<List<Void>> aggregatedResponses = Futures.successfulAsList(responses.values());

      // Key pairs must be cleaned up after all futures completed (even if some failed).
      Futures.addCallback(aggregatedResponses, new FutureCallback<List<Void>>() {
         @Override
         public void onSuccess(List<Void> result) {
            cleanupAutoGeneratedKeyPairs(generatedSshKeyIds);
         }

         @Override
         public void onFailure(Throwable t) {
            cleanupAutoGeneratedKeyPairs(generatedSshKeyIds);
         }

         private void cleanupAutoGeneratedKeyPairs(Set<Integer> generatedSshKeyIds) {
            logger.debug(">> cleaning up auto-generated key pairs...");
            for (Integer sshKeyId : generatedSshKeyIds) {
               try {
                  api.keyApi().delete(sshKeyId);
               } catch (Exception ex) {
                  logger.warn(">> could not delete key pair %s: %s", sshKeyId, ex.getMessage());
               }
            }
         }

      }, userExecutor);
   }
   
   private static String computeFingerprint(PublicKey key) {
      if (key instanceof RSAPublicKey) {
         RSAPublicKey rsaKey = (RSAPublicKey) key;
         return SshKeys.fingerprint(rsaKey.getPublicExponent(), rsaKey.getModulus());
      } else if (key instanceof DSAPublicKey) {
         DSAPublicKey dsaKey = (DSAPublicKey) key;
         return DSAKeys.fingerprint(dsaKey.getParams().getP(), dsaKey.getParams().getQ(), dsaKey.getParams().getG(),
               dsaKey.getY());
      } else if (key instanceof ECPublicKey) {
         ECPublicKey ecdsaKey = (ECPublicKey) key;
         return ECDSAKeys.fingerprint(ecdsaKey);
      } else {
         throw new IllegalArgumentException("Only RSA and DSA keys are supported");
      }
   }

}
