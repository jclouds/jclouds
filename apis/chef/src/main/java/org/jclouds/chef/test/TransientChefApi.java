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
package org.jclouds.chef.test;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.config.LocalBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookDefinition;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Environment;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Resource;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.domain.Sandbox;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.chef.options.CreateClientOptions;
import org.jclouds.chef.options.SearchOptions;
import org.jclouds.io.Payload;
import org.jclouds.lifecycle.Closer;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;

/**
 * In-memory chef simulator.
 * 
 * @author Adrian Cole
 */
public class TransientChefApi implements ChefApi {
   @Singleton
   private static class StorageMetadataToName implements Function<PageSet<? extends StorageMetadata>, Set<String>> {
      @Override
      public Set<String> apply(PageSet<? extends StorageMetadata> from) {
         return newLinkedHashSet(transform(from, new Function<StorageMetadata, String>() {

            @Override
            public String apply(StorageMetadata from) {
               return from.getName();
            }
         }));
      }
   }

   @Singleton
   private static class BlobToDatabagItem implements Function<Blob, DatabagItem> {
      @Override
      public DatabagItem apply(Blob from) {
         try {
            return from == null ? null : new DatabagItem(from.getMetadata().getName(), Strings2.toStringAndClose(from
                  .getPayload().getInput()));
         } catch (IOException e) {
            propagate(e);
            return null;
         }
      }
   }

   private final LocalBlobStore databags;
   private final BlobToDatabagItem blobToDatabagItem;
   private final StorageMetadataToName storageMetadataToName;
   private final Closer closer;

   @Inject
   TransientChefApi(@Named("databags") LocalBlobStore databags, StorageMetadataToName storageMetadataToName,
         BlobToDatabagItem blobToDatabagItem, Closer closer) {
      this.databags = checkNotNull(databags, "databags");
      this.storageMetadataToName = checkNotNull(storageMetadataToName, "storageMetadataToName");
      this.blobToDatabagItem = checkNotNull(blobToDatabagItem, "blobToDatabagItem");
      this.closer = checkNotNull(closer, "closer");
   }

   @Override
   public boolean clientExists(String clientname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Sandbox commitSandbox(String id, boolean isCompleted) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Client createClient(String clientname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Client createClient(String clientname, CreateClientOptions options) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void createDatabag(String databagName) {
      databags.createContainerInLocation(null, databagName);
   }

   @Override
   public DatabagItem createDatabagItem(String databagName, DatabagItem databagItem) {
      Blob blob = databags.blobBuilder(databagItem.getId()).payload(databagItem.toString()).build();
      databags.putBlob(databagName, blob);
      return databagItem;
   }

   @Override
   public void createNode(Node node) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void createRole(Role role) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean databagExists(String databagName) {
      return databags.containerExists(databagName);
   }

   @Override
   public boolean databagItemExists(String databagName, String databagItemId) {
      return databags.blobExists(databagName, databagItemId);
   }

   @Override
   public Client deleteClient(String clientname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CookbookVersion deleteCookbook(String cookbookName, String version) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void deleteDatabag(String databagName) {
      databags.deleteContainer(databagName);
   }

   @Override
   public DatabagItem deleteDatabagItem(String databagName, String databagItemId) {
      DatabagItem item = blobToDatabagItem.apply(databags.getBlob(databagName, databagItemId));
      databags.removeBlob(databagName, databagItemId);
      return item;
   }

   @Override
   public Node deleteNode(String nodename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Role deleteRole(String rolename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Client generateKeyForClient(String clientname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Client getClient(String clientname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CookbookVersion getCookbook(String cookbookName, String version) {
      throw new UnsupportedOperationException();
   }

   @Override
   public DatabagItem getDatabagItem(String databagName, String databagItemId) {
      return blobToDatabagItem.apply(databags.getBlob(databagName, databagItemId));
   }

   @Override
   public Node getNode(String nodename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Role getRole(String rolename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public UploadSandbox getUploadSandboxForChecksums(Set<List<Byte>> md5s) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> getVersionsOfCookbook(String cookbookName) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> listClients() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> listCookbooks() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> listDatabagItems(String databagName) {
      return storageMetadataToName.apply(databags.list(databagName));
   }

   @Override
   public Set<String> listDatabags() {
      return storageMetadataToName.apply(databags.list());
   }

   @Override
   public Set<String> listNodes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> listRoles() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> listSearchIndexes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean nodeExists(String nodename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean roleExists(String rolename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends Client> searchClients() {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends Client> searchClients(SearchOptions options) {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends DatabagItem> searchDatabag(String databagName) {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends DatabagItem> searchDatabag(String databagName, SearchOptions options) {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends Node> searchNodes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends Node> searchNodes(SearchOptions options) {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends Role> searchRoles() {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends Role> searchRoles(SearchOptions options) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CookbookVersion updateCookbook(String cookbookName, String version, CookbookVersion cookbook) {
      throw new UnsupportedOperationException();
   }

   @Override
   public DatabagItem updateDatabagItem(String databagName, DatabagItem item) {
      return createDatabagItem(databagName, item);
   }

   @Override
   public Node updateNode(Node node) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Role updateRole(Role role) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void uploadContent(URI location, Payload content) {
      throw new UnsupportedOperationException();
   }

   @Override
   public InputStream getResourceContents(Resource resource) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> listEnvironments() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void createEnvironment(Environment environment) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Environment deleteEnvironment(String environmentname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Environment getEnvironment(String environmentname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Environment updateEnvironment(Environment environment) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<CookbookDefinition> listEnvironmentCookbooks(String environmentname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<CookbookDefinition> listEnvironmentCookbooks(String environmentname, String numversions) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CookbookDefinition getEnvironmentCookbook(String environmentname, String cookbookname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CookbookDefinition getEnvironmentCookbook(String environmentname, String cookbookname, String numversions) {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends Environment> searchEnvironments() {
      throw new UnsupportedOperationException();
   }

   @Override
   public SearchResult<? extends Environment> searchEnvironments(SearchOptions options) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void close() throws IOException {
      closer.close();
   }
}
