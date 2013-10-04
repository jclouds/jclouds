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
import org.jclouds.chef.functions.RunListForGroup;
import org.jclouds.chef.strategy.CleanupStaleNodesAndClients;
import org.jclouds.chef.strategy.CreateNodeAndPopulateAutomaticAttributes;
import org.jclouds.chef.strategy.DeleteAllClientsInList;
import org.jclouds.chef.strategy.DeleteAllNodesInList;
import org.jclouds.chef.strategy.ListClients;
import org.jclouds.chef.strategy.ListCookbookVersions;
import org.jclouds.chef.strategy.ListCookbookVersionsInEnvironment;
import org.jclouds.chef.strategy.ListNodesInEnvironment;
import org.jclouds.chef.strategy.ListEnvironments;
import org.jclouds.chef.strategy.ListNodes;
import org.jclouds.chef.strategy.UpdateAutomaticAttributesOnNode;
import org.jclouds.domain.JsonBall;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.RSADecryptingPayload;
import org.jclouds.io.payloads.RSAEncryptingPayload;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

/**
 * @author Adrian Cole
 */
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
   private final RunListForGroup runListForGroup;
   private final ListCookbookVersions listCookbookVersions;
   private final ListCookbookVersionsInEnvironment listCookbookVersionsInEnvironment;
   private final ListEnvironments listEnvironments;
   private final ListNodesInEnvironment listNodesInEnvironment;
   private final Json json;
   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   protected BaseChefService(ChefContext chefContext, ChefApi api,
         CleanupStaleNodesAndClients cleanupStaleNodesAndClients,
         CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes,
         DeleteAllNodesInList deleteAllNodesInList, ListNodes listNodes, DeleteAllClientsInList deleteAllClientsInList,
         ListClients listClients, ListCookbookVersions listCookbookVersions,
         UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode, Supplier<PrivateKey> privateKey,
         @Named(CHEF_BOOTSTRAP_DATABAG) String databag, GroupToBootScript groupToBootScript,
         BootstrapConfigForGroup bootstrapConfigForGroup, RunListForGroup runListForGroup,
         ListEnvironments listEnvironments, ListNodesInEnvironment listNodesInEnvironment,
         ListCookbookVersionsInEnvironment listCookbookVersionsInEnvironment, Json json) {
      this.chefContext = checkNotNull(chefContext, "chefContext");
      this.api = checkNotNull(api, "api");
      this.cleanupStaleNodesAndClients = checkNotNull(cleanupStaleNodesAndClients, "cleanupStaleNodesAndClients");
      this.createNodeAndPopulateAutomaticAttributes = checkNotNull(createNodeAndPopulateAutomaticAttributes,
            "createNodeAndPopulateAutomaticAttributes");
      this.deleteAllNodesInList = checkNotNull(deleteAllNodesInList, "deleteAllNodesInList");
      this.listNodes = checkNotNull(listNodes, "listNodes");
      this.deleteAllClientsInList = checkNotNull(deleteAllClientsInList, "deleteAllClientsInList");
      this.listClients = checkNotNull(listClients, "listClients");
      this.listCookbookVersions = checkNotNull(listCookbookVersions, "listCookbookVersions");
      this.updateAutomaticAttributesOnNode = checkNotNull(updateAutomaticAttributesOnNode,
            "updateAutomaticAttributesOnNode");
      this.privateKey = checkNotNull(privateKey, "privateKey");
      this.groupToBootScript = checkNotNull(groupToBootScript, "groupToBootScript");
      this.databag = checkNotNull(databag, "databag");
      this.bootstrapConfigForGroup = checkNotNull(bootstrapConfigForGroup, "bootstrapConfigForGroup");
      this.runListForGroup = checkNotNull(runListForGroup, "runListForGroup");
      this.listEnvironments = checkNotNull(listEnvironments, "listEnvironments");
      this.listNodesInEnvironment = checkNotNull(listNodesInEnvironment, "listNodesInEnvironment");
      this.listCookbookVersionsInEnvironment = checkNotNull(listCookbookVersionsInEnvironment, "listCookbookVersionsInEnvironment");
      this.json = checkNotNull(json, "json");
   }

   @Override
   public ChefContext getContext() {
      return chefContext;
   }

   @Override
   public byte[] encrypt(InputSupplier<? extends InputStream> supplier) throws IOException {
      return ByteStreams.toByteArray(new RSAEncryptingPayload(Payloads.newPayload(supplier.getInput()), privateKey
            .get()));
   }

   @Override
   public byte[] decrypt(InputSupplier<? extends InputStream> supplier) throws IOException {
      return ByteStreams.toByteArray(new RSADecryptingPayload(Payloads.newPayload(supplier.getInput()), privateKey
            .get()));
   }

   @VisibleForTesting
   String buildBootstrapConfiguration(BootstrapConfig bootstrapConfig) {
      checkNotNull(bootstrapConfig, "bootstrapConfig must not be null");

      Map<String, Object> configMap = Maps.newHashMap();
      configMap.put("run_list", bootstrapConfig.getRunList());

      if (bootstrapConfig.getEnvironment().isPresent()) {
         configMap.put("environment", bootstrapConfig.getEnvironment().get());
      }

      if (bootstrapConfig.getAttribtues().isPresent()) {
         Map<String, Object> attributes = json.fromJson(bootstrapConfig.getAttribtues().get().toString(),
               BootstrapConfigForGroup.BOOTSTRAP_CONFIG_TYPE);
         configMap.putAll(attributes);
      }

      return json.toJson(configMap);
   }

   @Override
   public Statement createBootstrapScriptForGroup(String group) {
      return groupToBootScript.apply(group);
   }

   @Override
   public void updateBootstrapConfigForGroup(String group, BootstrapConfig bootstrapConfig) {
      try {
         api.createDatabag(databag);
      } catch (IllegalStateException e) {

      }

      String jsonConfig = buildBootstrapConfiguration(bootstrapConfig);
      DatabagItem runlist = new DatabagItem(group, jsonConfig);

      if (api.getDatabagItem(databag, group) == null) {
         api.createDatabagItem(databag, runlist);
      } else {
         api.updateDatabagItem(databag, runlist);
      }
   }

   @Override
   public List<String> getRunListForGroup(String group) {
      return runListForGroup.apply(group);
   }

   @Override
   public JsonBall getBootstrapConfigForGroup(String group) {
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
   public Iterable<? extends Client> listClients() {
      return listClients.execute();
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersions() {
      return listCookbookVersions.execute();
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName) {
      return listCookbookVersionsInEnvironment.execute(environmentName);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsInEnvironment(String environmentName, String numVersions) {
      return listCookbookVersionsInEnvironment.execute(environmentName, numVersions);
   }

   @Override
   public Iterable<? extends Environment> listEnvironments() {
      return listEnvironments.execute();
   }

   @Override
   public Iterable<? extends Node> listNodesInEnvironment(String environmentName) {
      return listNodesInEnvironment.execute(environmentName);
   }

}
