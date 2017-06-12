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
package org.jclouds.packet.compute.strategy;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
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
import org.jclouds.logging.Logger;
import org.jclouds.packet.PacketApi;
import org.jclouds.packet.compute.options.PacketTemplateOptions;
import org.jclouds.packet.domain.SshKey;
import org.jclouds.ssh.SshKeyPairGenerator;
import org.jclouds.ssh.SshKeys;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;

@Singleton
public class CreateSshKeysThenCreateNodes extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final PacketApi api;
    private final SshKeyPairGenerator keyGenerator;

    @Inject
    protected CreateSshKeysThenCreateNodes(
            CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
            ListNodesStrategy listNodesStrategy,
            GroupNamingConvention.Factory namingConvention,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            PacketApi api, SshKeyPairGenerator keyGenerator) {
        super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
                customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
        this.api = api;
        this.keyGenerator = keyGenerator;
    }

    @Override
    public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
                                                  Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
                                                  Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

        PacketTemplateOptions options = template.getOptions().as(PacketTemplateOptions.class);
        Set<String> generatedSshKeyIds = Sets.newHashSet();

        // If no key has been configured, generate a key pair
        if (Strings.isNullOrEmpty(options.getPublicKey())) {
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

        Map<?, ListenableFuture<Void>> responses = super.execute(group, count, template, goodNodes, badNodes,
                customizationResponses);

        // Key pairs in Packet are only required to create the devices. They aren't used anymore so it is better
        // to delete the auto-generated key pairs at this point where we know exactly which ones have been
        // auto-generated by jclouds.
        registerAutoGeneratedKeyPairCleanupCallbacks(responses, generatedSshKeyIds);

        return responses;
    }

    private void createKeyPairForPublicKeyInOptionsAndAddToSet(PacketTemplateOptions options,
                                                               Set<String> generatedSshKeyIds) {
        logger.debug(">> checking if the key pair already exists...");

        PublicKey userKey;
        Iterable<String> parts = Splitter.on(' ').split(options.getPublicKey());
        checkArgument(size(parts) >= 2, "bad format, should be: ssh-rsa AAAAB3...");
        String type = get(parts, 0);

        try {
            if ("ssh-rsa".equals(type)) {
                RSAPublicKeySpec spec = SshKeys.publicKeySpecFromOpenSSH(options.getPublicKey());
                userKey = KeyFactory.getInstance("RSA").generatePublic(spec);
            } else {
                throw new IllegalArgumentException("bad format, ssh-rsa is only supported");
            }
        } catch (InvalidKeySpecException ex) {
            throw propagate(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw propagate(ex);
        }
      String label = computeFingerprint(userKey);
      SshKey key = api.sshKeyApi().get(label);

      if (key == null) {
         logger.debug(">> key pair not found. creating a new key pair %s ...", label);
         SshKey newKey = api.sshKeyApi().create(label, options.getPublicKey());
         logger.debug(">> key pair created! %s", newKey);
         generatedSshKeyIds.add(newKey.id());
      } else {
         logger.debug(">> key pair found! %s", key);
         generatedSshKeyIds.add(key.id());
      }
    }

    private void generateKeyPairAndAddKeyToSet(PacketTemplateOptions options, Set<String> generatedSshKeyIds, String prefix) {
        logger.debug(">> creating default keypair for node...");

        Map<String, String> defaultKeys = keyGenerator.get();

        SshKey sshKey = api.sshKeyApi().create(prefix + System.getProperty("user.name"), defaultKeys.get("public"));
        generatedSshKeyIds.add(sshKey.id());
        logger.debug(">> keypair created! %s", sshKey);

        // If a private key has not been explicitly set, configure the generated one
        if (Strings.isNullOrEmpty(options.getLoginPrivateKey())) {
            options.overrideLoginPrivateKey(defaultKeys.get("private"));
        }
    }

    private void registerAutoGeneratedKeyPairCleanupCallbacks(Map<?, ListenableFuture<Void>> responses,
                                                              final Set<String> generatedSshKeyIds) {
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

            private void cleanupAutoGeneratedKeyPairs(Set<String> generatedSshKeyIds) {
                logger.debug(">> cleaning up auto-generated key pairs...");
                for (String sshKeyId : generatedSshKeyIds) {
                    try {
                        api.sshKeyApi().delete(sshKeyId);
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
        } else {
            throw new IllegalArgumentException("Only RSA keys are supported");
        }
    }

}
