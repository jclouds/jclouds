/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
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
import org.jclouds.chef.strategy.ListNodes;
import org.jclouds.chef.strategy.UpdateAutomaticAttributesOnNode;
import org.jclouds.domain.JsonBall;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.RSADecryptingPayload;
import org.jclouds.io.payloads.RSAEncryptingPayload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BaseChefService implements ChefService {

   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ChefContext chefContext;
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

   @Inject
   protected BaseChefService(ChefContext chefContext, CleanupStaleNodesAndClients cleanupStaleNodesAndClients,
         CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes,
         DeleteAllNodesInList deleteAllNodesInList, ListNodes listNodes, DeleteAllClientsInList deleteAllClientsInList,
         ListClients listClients, ListCookbookVersions listCookbookVersions,
         UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode, Supplier<PrivateKey> privateKey,
         @Named(CHEF_BOOTSTRAP_DATABAG) String databag, GroupToBootScript groupToBootScript,
         BootstrapConfigForGroup bootstrapConfigForGroup, RunListForGroup runListForGroup) {
      this.chefContext = checkNotNull(chefContext, "chefContext");
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
   public void deleteAllNodesInList(Iterable<String> names) {
      deleteAllNodesInList.execute(names);
   }

   @Override
   public Iterable<? extends Node> listNodes() {
      return listNodes.execute();
   }

   @Override
   public Iterable<? extends Node> listNodesMatching(Predicate<String> nodeNameSelector) {
      return listNodes.execute(nodeNameSelector);
   }

   @Override
   public Iterable<? extends Node> listNodesNamed(Iterable<String> names) {
      return listNodes.execute(names);
   }

   @Override
   public void deleteAllClientsInList(Iterable<String> names) {
      deleteAllClientsInList.execute(names);
   }

   @Override
   public Iterable<? extends Client> listClientsDetails() {
      return listClients.execute();
   }

   @Override
   public Iterable<? extends Client> listClientsDetailsMatching(Predicate<String> clientNameSelector) {
      return listClients.execute(clientNameSelector);
   }

   @Override
   public Iterable<? extends Client> listClientsNamed(Iterable<String> names) {
      return listClients.execute(names);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersions() {
      return listCookbookVersions.execute();
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsMatching(Predicate<String> cookbookNameSelector) {
      return listCookbookVersions.execute(cookbookNameSelector);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsNamed(Iterable<String> names) {
      return listCookbookVersions.execute(names);
   }

   @Override
   public void updateAutomaticAttributesOnNode(String nodeName) {
      updateAutomaticAttributesOnNode.execute(nodeName);
   }

   @Override
   public ChefContext getContext() {
      return chefContext;
   }

   @Override
   public Statement createBootstrapScriptForGroup(String group) {
      return groupToBootScript.apply(group);
   }

   @Override
   @Deprecated
   public void updateRunListForGroup(Iterable<String> runList, String group) {
      updateBootstrapConfigForGroup(runList, null, group);
   }

   @Override
   public void updateBootstrapConfigForGroup(Iterable<String> runList, @Nullable JsonBall jsonAttributes, String group) {
      try {
         chefContext.getApi().createDatabag(databag);
      } catch (IllegalStateException e) {

      }

      String bootstrapConfig = buildBootstrapConfiguration(runList, Optional.fromNullable(jsonAttributes));
      DatabagItem runlist = new DatabagItem(group, bootstrapConfig);

      if (chefContext.getApi().getDatabagItem(databag, group) == null) {
         chefContext.getApi().createDatabagItem(databag, runlist);
      } else {
         chefContext.getApi().updateDatabagItem(databag, runlist);
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
   public byte[] decrypt(InputSupplier<? extends InputStream> supplier) throws IOException {
      return ByteStreams.toByteArray(new RSADecryptingPayload(Payloads.newPayload(supplier.getInput()), privateKey
            .get()));
   }

   @Override
   public byte[] encrypt(InputSupplier<? extends InputStream> supplier) throws IOException {
      return ByteStreams.toByteArray(new RSAEncryptingPayload(Payloads.newPayload(supplier.getInput()), privateKey
            .get()));
   }

   @VisibleForTesting
   String buildBootstrapConfiguration(Iterable<String> runList, Optional<JsonBall> jsonAttributes) {
      checkNotNull(runList, "runList must not be null");
      checkNotNull(jsonAttributes, "jsonAttributes must not be null");

      Json json = chefContext.utils().getJson();
      Map<String, Object> bootstrapConfig = Maps.newHashMap();
      bootstrapConfig.put("run_list", Lists.newArrayList(runList));
      if (jsonAttributes.isPresent()) {
         Map<String, Object> attributes = json.fromJson(jsonAttributes.get().toString(),
               BootstrapConfigForGroup.BOOTSTRAP_CONFIG_TYPE);
         bootstrapConfig.putAll(attributes);
      }
      return json.toJson(bootstrapConfig);
   }

}
