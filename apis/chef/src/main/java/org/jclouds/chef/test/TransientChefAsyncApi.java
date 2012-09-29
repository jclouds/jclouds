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
import static org.jclouds.concurrent.Futures.compose;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.LocalAsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Resource;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.domain.Sandbox;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.chef.options.CreateClientOptions;
import org.jclouds.chef.options.SearchOptions;
import org.jclouds.io.Payload;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * In-memory chef simulator.
 * 
 * @author Adrian Cole
 */

public class TransientChefAsyncApi implements ChefAsyncApi {
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

   private final LocalAsyncBlobStore databags;
   private final ExecutorService executor;
   private final BlobToDatabagItem blobToDatabagItem;
   private final StorageMetadataToName storageMetadataToName;

   @Inject
   TransientChefAsyncApi(@Named("databags") LocalAsyncBlobStore databags,
         StorageMetadataToName storageMetadataToName, BlobToDatabagItem blobToDatabagItem,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.databags = checkNotNull(databags, "databags");
      this.storageMetadataToName = checkNotNull(storageMetadataToName, "storageMetadataToName");
      this.blobToDatabagItem = checkNotNull(blobToDatabagItem, "blobToDatabagItem");
      this.executor = checkNotNull(executor, "executor");
   }

   @Override
   public ListenableFuture<Boolean> clientExists(String clientname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Sandbox> commitSandbox(String id, boolean isCompleted) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Client> createClient(String clientname) {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public ListenableFuture<Client> createClient(String clientname, CreateClientOptions options) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Void> createDatabag(String databagName) {
      return Futures.transform(databags.createContainerInLocation(null, databagName), new Function<Boolean, Void>(){

         @Override
         public Void apply(Boolean input) {
            return null;
         }
         
      });
   }

   @Override
   public ListenableFuture<DatabagItem> createDatabagItem(String databagName, DatabagItem databagItem) {
      Blob blob = databags.blobBuilder(databagItem.getId()).payload(databagItem.toString()).build();
      databags.putBlob(databagName, blob);
      return Futures.immediateFuture(databagItem);
   }

   @Override
   public ListenableFuture<Void> createNode(Node node) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Void> createRole(Role role) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Boolean> databagExists(String databagName) {
      return databags.containerExists(databagName);
   }

   @Override
   public ListenableFuture<Boolean> databagItemExists(String databagName, String databagItemId) {
      return databags.blobExists(databagName, databagItemId);
   }

   @Override
   public ListenableFuture<Client> deleteClient(String clientname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<CookbookVersion> deleteCookbook(String cookbookName, String version) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Void> deleteDatabag(String databagName) {
      return databags.deleteContainer(databagName);
   }

   @Override
   public ListenableFuture<DatabagItem> deleteDatabagItem(String databagName, String databagItemId) {
      return Futures.immediateFuture(blobToDatabagItem.apply(databags.getContext().createBlobMap(databagName).remove(databagItemId)));
   }

   @Override
   public ListenableFuture<Node> deleteNode(String nodename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Role> deleteRole(String rolename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Client> generateKeyForClient(String clientname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Client> getClient(String clientname) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<CookbookVersion> getCookbook(String cookbookName, String version) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<DatabagItem> getDatabagItem(String databagName, String databagItemId) {
      return compose(databags.getBlob(databagName, databagItemId), blobToDatabagItem, executor);
   }

   @Override
   public ListenableFuture<Node> getNode(String nodename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Role> getRole(String rolename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<UploadSandbox> getUploadSandboxForChecksums(Set<List<Byte>> md5s) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Set<String>> getVersionsOfCookbook(String cookbookName) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Set<String>> listClients() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Set<String>> listCookbooks() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Set<String>> listDatabagItems(String databagName) {
      return compose(databags.list(databagName), storageMetadataToName, executor);
   }

   @Override
   public ListenableFuture<Set<String>> listDatabags() {
      return compose(databags.list(), storageMetadataToName, executor);
   }

   @Override
   public ListenableFuture<Set<String>> listNodes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Set<String>> listRoles() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Set<String>> listSearchIndexes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Boolean> nodeExists(String nodename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Boolean> roleExists(String rolename) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<? extends SearchResult<? extends Client>> searchClients() {
      throw new UnsupportedOperationException();
   }
   
   @Override
	public ListenableFuture<? extends SearchResult<? extends Client>> searchClients(
			SearchOptions options) {
	   throw new UnsupportedOperationException();
	}

   @Override
   public ListenableFuture<? extends SearchResult<? extends DatabagItem>> searchDatabag(String databagName) {
      throw new UnsupportedOperationException();
   }
   
   @Override
	public ListenableFuture<? extends SearchResult<? extends DatabagItem>> searchDatabag(
			String databagName, SearchOptions options) {
	   throw new UnsupportedOperationException();
	}

   @Override
   public ListenableFuture<? extends SearchResult<? extends Node>> searchNodes() {
      throw new UnsupportedOperationException();
   }

   @Override
	public ListenableFuture<? extends SearchResult<? extends Node>> searchNodes(
			SearchOptions options) {
	   throw new UnsupportedOperationException();
	}

   @Override
   public ListenableFuture<? extends SearchResult<? extends Role>> searchRoles() {
      throw new UnsupportedOperationException();
   }
   
   @Override
	public ListenableFuture<? extends SearchResult<? extends Role>> searchRoles(
			SearchOptions options) {
	   throw new UnsupportedOperationException();
	}

   @Override
   public ListenableFuture<CookbookVersion> updateCookbook(String cookbookName, String version, CookbookVersion cookbook) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<DatabagItem> updateDatabagItem(String databagName, DatabagItem item) {
      return createDatabagItem(databagName, item);
   }

   @Override
   public ListenableFuture<Node> updateNode(Node node) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Role> updateRole(Role role) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Void> uploadContent(URI location, Payload content) {
       throw new UnsupportedOperationException();
   }

    @Override
    public ListenableFuture<InputStream> getResourceContents(Resource resource) {
        throw new UnsupportedOperationException();
    }

}
