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
package org.jclouds.chef.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.chef.config.ChefProperties.CHEF_BOOTSTRAP_DATABAG;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.domain.BootstrapConfig;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Environment;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.functions.BootstrapConfigForGroup;
import org.jclouds.chef.functions.GroupToBootScript;
import org.jclouds.chef.strategy.CleanupStaleNodesAndClients;
import org.jclouds.chef.strategy.CreateNodeAndPopulateAutomaticAttributes;
import org.jclouds.chef.strategy.DeleteAllClientsInList;
import org.jclouds.chef.strategy.DeleteAllNodesInList;
import org.jclouds.chef.strategy.ListClients;
import org.jclouds.chef.strategy.ListCookbookVersions;
import org.jclouds.chef.strategy.ListCookbookVersionsInEnvironment;
import org.jclouds.chef.strategy.ListEnvironments;
import org.jclouds.chef.strategy.ListNodes;
import org.jclouds.chef.strategy.ListNodesInEnvironment;
import org.jclouds.chef.strategy.UpdateAutomaticAttributesOnNode;
import org.jclouds.crypto.Crypto;
import org.jclouds.io.ByteStreams2;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.RSADecryptingPayload;
import org.jclouds.io.payloads.RSAEncryptingPayload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.io.InputSupplier;

@Singleton
public class BaseChefService implements ChefService {

   private final ChefContext chefContext;
   private final ChefApi api;
   private final CleanupStaleNodesAndClients cleanupStaleNodesAndClients;
   private final CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes;
   private final DeleteAllNodesInList deleteAllNodesInList;
   private final ListNodes listNodes;
   private final DeleteAllClientsInList deleteAllClientsInList;
   private final ListClients listClients;
   private final UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode;
   private final Supplier<PrivateKey> privateKey;
   private final GroupToBootScript groupToBootScript;
   private final String databag;
   private final BootstrapConfigForGroup bootstrapConfigForGroup;
   private final ListCookbookVersions listCookbookVersions;
   private final ListCookbookVersionsInEnvironment listCookbookVersionsInEnvironment;
   private final ListEnvironments listEnvironments;
   private final ListNodesInEnvironment listNodesInEnvironment;
   private final Json json;
   private final Crypto crypto;

   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   BaseChefService(ChefContext chefContext, ChefApi api, CleanupStaleNodesAndClients cleanupStaleNodesAndClients,
         CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes,
         DeleteAllNodesInList deleteAllNodesInList, ListNodes listNodes, DeleteAllClientsInList deleteAllClientsInList,
         ListClients listClients, ListCookbookVersions listCookbookVersions,
         UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode, Supplier<PrivateKey> privateKey,
         @Named(CHEF_BOOTSTRAP_DATABAG) String databag, GroupToBootScript groupToBootScript,
         BootstrapConfigForGroup bootstrapConfigForGroup, ListEnvironments listEnvironments,
         ListNodesInEnvironment listNodesInEnvironment,
         ListCookbookVersionsInEnvironment listCookbookVersionsInEnvironment, Json json, Crypto crypto) {
      this.chefContext = chefContext;
      this.api = api;
      this.cleanupStaleNodesAndClients = cleanupStaleNodesAndClients;
      this.createNodeAndPopulateAutomaticAttributes = createNodeAndPopulateAutomaticAttributes;
      this.deleteAllNodesInList = deleteAllNodesInList;
      this.listNodes = listNodes;
      this.deleteAllClientsInList = deleteAllClientsInList;
      this.listClients = listClients;
      this.listCookbookVersions = listCookbookVersions;
      this.updateAutomaticAttributesOnNode = updateAutomaticAttributesOnNode;
      this.privateKey = privateKey;
      this.groupToBootScript = groupToBootScript;
      this.databag = databag;
      this.bootstrapConfigForGroup = bootstrapConfigForGroup;
      this.listEnvironments = listEnvironments;
      this.listNodesInEnvironment = listNodesInEnvironment;
      this.listCookbookVersionsInEnvironment = listCookbookVersionsInEnvironment;
      this.json = json;
      this.crypto = crypto;
   }

   @Override
   public ChefContext getContext() {
      return chefContext;
   }

   @Override
   public byte[] encrypt(InputSupplier<? extends InputStream> supplier) throws IOException {
      return ByteStreams2.toByteArrayAndClose(new RSAEncryptingPayload(crypto,
            Payloads.newPayload(supplier.getInput()), privateKey.get()).openStream());
   }

   @Override
   public byte[] decrypt(InputSupplier<? extends InputStream> supplier) throws IOException {
      return ByteStreams2.toByteArrayAndClose(new RSADecryptingPayload(crypto,
            Payloads.newPayload(supplier.getInput()), privateKey.get()).openStream());
   }

   private static void putIfPresent(Map<String, Object> configMap, Optional<?> configProperty, String name) {
      if (configProperty.isPresent()) {
         configMap.put(name, configProperty.get().toString());
      }
   }

   @Override
   public Statement createBootstrapScriptForGroup(String group, @Nullable String nodeName) {
      return groupToBootScript.apply(group, nodeName);
   }

   @Override
   public Statement createBootstrapScriptForGroup(String group) {
      return groupToBootScript.apply(group, null);
   }

   @Override
   public void updateBootstrapConfigForGroup(String group, BootstrapConfig bootstrapConfig) {
      checkNotNull(bootstrapConfig, "bootstrapConfig cannot be null");
      try {
         api.createDatabag(databag);
      } catch (IllegalStateException e) {

      }

      String jsonConfig = buildBootstrapConfiguration(bootstrapConfig);
      DatabagItem config = new DatabagItem(group, jsonConfig);

      if (api.getDatabagItem(databag, group) == null) {
         api.createDatabagItem(databag, config);
      } else {
         api.updateDatabagItem(databag, config);
      }
   }
   
   @VisibleForTesting
   String buildBootstrapConfiguration(BootstrapConfig config) {
      return json.toJson(config);
   }

   /**
    * @deprecated Use {{@link #getBootstrapConfigForGroup(String)}.
    */
   @Override
   @Deprecated
   public List<String> getRunListForGroup(String group) {
      return getBootstrapConfigForGroup(group).getRunList();
   }

   @Override
   public BootstrapConfig getBootstrapConfigForGroup(String group) {
      return bootstrapConfigForGroup.apply(group);
   }

   @Override
   public void cleanupStaleNodesAndClients(String prefix, int secondsStale) {
      cleanupStaleNodesAndClients.execute(prefix, secondsStale);
   }

   @Override
   public Node createNodeAndPopulateAutomaticAttributes(String nodeName, Iterable<String> runList) {
      return createNodeAndPopulateAutomaticAttributes.execute(nodeName, runList);
   }

   @Override
   public void updateAutomaticAttributesOnNode(String nodeName) {
      updateAutomaticAttributesOnNode.execute(nodeName);
   }

   @Override
   public void deleteAllNodesInList(Iterable<String> names) {
      deleteAllNodesInList.execute(names);
   }

   @Override
   public void deleteAllClientsInList(Iterable<String> names) {
      deleteAllClientsInList.execute(names);
   }

   @Override
   public Iterable<? extends Node> listNodes() {
      return listNodes.execute();
   }

   @Override
   public Iterable<? extends Node> listNodes(ExecutorService executorService) {
      return listNodes.execute(executorService);
   }

   @Override
   public Iterable<? extends Client> listClients() {
      return listClients.execute();
   }

   @Override
   public Iterable<? extends Client> listClients(ExecutorService executorService) {
      return listClients.execute(executorService);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersions() {
      return listCookbookVersions.execute();
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersions(ExecutorService executorService) {
      return listCookbookVersions.execute(executorService);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName) {
      return listCookbookVersionsInEnvironment.execute(environmentName);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName,
         ExecutorService executorService) {
      return listCookbookVersionsInEnvironment.execute(executorService, environmentName);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName,
         String numVersions) {
      return listCookbookVersionsInEnvironment.execute(environmentName, numVersions);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName,
         String numVersions, ExecutorService executorService) {
      return listCookbookVersionsInEnvironment.execute(executorService, environmentName, numVersions);
   }

   @Override
   public Iterable<? extends Environment> listEnvironments() {
      return listEnvironments.execute();
   }

   @Override
   public Iterable<? extends Node> listNodesInEnvironment(String environmentName) {
      return listNodesInEnvironment.execute(environmentName);
   }

   @Override
   public Iterable<? extends Node> listNodesInEnvironment(String environmentName, ExecutorService executorService) {
      return listNodesInEnvironment.execute(executorService, environmentName);
   }

}
