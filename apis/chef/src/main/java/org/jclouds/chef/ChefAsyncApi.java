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
package org.jclouds.chef;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Constants;
import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.chef.binders.BindChecksumsToJsonPayload;
import org.jclouds.chef.binders.BindCreateClientOptionsToJsonPayload;
import org.jclouds.chef.binders.BindGenerateKeyForClientToJsonPayload;
import org.jclouds.chef.binders.BindIsCompletedToJsonPayload;
import org.jclouds.chef.binders.BindNameToJsonPayload;
import org.jclouds.chef.binders.DatabagItemId;
import org.jclouds.chef.binders.NodeName;
import org.jclouds.chef.binders.RoleName;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Resource;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.domain.Sandbox;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.functions.ParseCookbookDefinitionCheckingChefVersion;
import org.jclouds.chef.functions.ParseCookbookVersionsCheckingChefVersion;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.chef.functions.ParseSearchClientsFromJson;
import org.jclouds.chef.functions.ParseSearchDatabagFromJson;
import org.jclouds.chef.functions.ParseSearchNodesFromJson;
import org.jclouds.chef.functions.ParseSearchRolesFromJson;
import org.jclouds.chef.functions.UriForResource;
import org.jclouds.chef.options.CreateClientOptions;
import org.jclouds.chef.options.SearchOptions;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Chef via their REST API.
 * <p/>
 * 
 * @see ChefApi
 * @author Adrian Cole
 */
@RequestFilters(SignedHeaderAuth.class)
@Headers(keys = "X-Chef-Version", values = "{" + Constants.PROPERTY_API_VERSION + "}")
@Consumes(MediaType.APPLICATION_JSON)
public interface ChefAsyncApi {
   public static final String VERSION = "0.10.8";

   /**
    * @see ChefApi#getUploadSandboxForChecksums(Set)
    */
   @Named("sandbox:upload")
   @POST
   @Path("/sandboxes")
   ListenableFuture<UploadSandbox> getUploadSandboxForChecksums(
         @BinderParam(BindChecksumsToJsonPayload.class) Set<List<Byte>> md5s);

   /**
    * @see ChefApi#uploadContent(URI, Payload)
    */
   @Named("content:upload")
   @PUT
   @Produces("application/x-binary")
   ListenableFuture<Void> uploadContent(@EndpointParam URI location, Payload content);

   /**
    * @see ChefApi#commitSandbox(String, boolean)
    */
   @Named("sandbox:commit")
   @PUT
   @Path("/sandboxes/{id}")
   ListenableFuture<Sandbox> commitSandbox(@PathParam("id") String id,
         @BinderParam(BindIsCompletedToJsonPayload.class) boolean isCompleted);

   /**
    * @see ChefApi#listCookbooks()
    */
   @Named("cookbook:list")
   @GET
   @Path("/cookbooks")
   @ResponseParser(ParseCookbookDefinitionCheckingChefVersion.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listCookbooks();

   /**
    * @see ChefApi#updateCookbook(String, String, CookbookVersion)
    */
   @Named("cookbook:update")
   @PUT
   @Path("/cookbooks/{cookbookname}/{version}")
   ListenableFuture<CookbookVersion> updateCookbook(@PathParam("cookbookname") String cookbookName,
         @PathParam("version") String version, @BinderParam(BindToJsonPayload.class) CookbookVersion cookbook);

   /**
    * @see ChefApi#deleteCookbook(String, String)
    */
   @Named("cookbook:delete")
   @DELETE
   @Path("/cookbooks/{cookbookname}/{version}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<CookbookVersion> deleteCookbook(@PathParam("cookbookname") String cookbookName,
         @PathParam("version") String version);

   /**
    * @see ChefApi#getVersionsOfCookbook(String)
    */
   @Named("cookbook:versions")
   @GET
   @Path("/cookbooks/{cookbookname}")
   @ResponseParser(ParseCookbookVersionsCheckingChefVersion.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> getVersionsOfCookbook(@PathParam("cookbookname") String cookbookName);

   /**
    * @see ChefApi#getCookbook(String, String)
    */
   @Named("cookbook:get")
   @GET
   @Path("/cookbooks/{cookbookname}/{version}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<CookbookVersion> getCookbook(@PathParam("cookbookname") String cookbookName,
         @PathParam("version") String version);

   /**
    * @see ChefApi#createClient(String)
    */
   @Named("client:create")
   @POST
   @Path("/clients")
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Client> createClient(@PayloadParam("name") String clientname);

   /**
    * @see ChefApi#createClient(String, CreateApiOptions)
    */
   @Named("client:create")
   @POST
   @Path("/clients")
   @MapBinder(BindCreateClientOptionsToJsonPayload.class)
   ListenableFuture<Client> createClient(@PayloadParam("name") String clientname, CreateClientOptions options);

   /**
    * @see ChefApi#generateKeyForClient(String)
    */
   @Named("client:generatekey")
   @PUT
   @Path("/clients/{clientname}")
   ListenableFuture<Client> generateKeyForClient(
         @PathParam("clientname") @BinderParam(BindGenerateKeyForClientToJsonPayload.class) String clientname);

   /**
    * @see ChefApi#clientExists(String)
    */
   @Named("client:exists")
   @HEAD
   @Path("/clients/{clientname}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> clientExists(@PathParam("clientname") String clientname);

   /**
    * @see ChefApi#getClient(String)
    */
   @Named("client:get")
   @GET
   @Path("/clients/{clientname}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Client> getClient(@PathParam("clientname") String clientname);

   /**
    * @see ChefApi#deleteClient(String)
    */
   @Named("client:delete")
   @DELETE
   @Path("/clients/{clientname}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Client> deleteClient(@PathParam("clientname") String clientname);

   /**
    * @see ChefApi#listClients()
    */
   @Named("client:list")
   @GET
   @Path("/clients")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listClients();

   /**
    * @see ChefApi#createNode(Node)
    */
   @Named("node:create")
   @POST
   @Path("/nodes")
   ListenableFuture<Void> createNode(@BinderParam(BindToJsonPayload.class) Node node);

   /**
    * @see ChefApi#updateNode(Node)
    */
   @Named("node:update")
   @PUT
   @Path("/nodes/{nodename}")
   ListenableFuture<Node> updateNode(
         @PathParam("nodename") @ParamParser(NodeName.class) @BinderParam(BindToJsonPayload.class) Node node);

   /**
    * @see ChefApi#nodeExists(String)
    */
   @Named("node:exists")
   @HEAD
   @Path("/nodes/{nodename}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> nodeExists(@PathParam("nodename") String nodename);

   /**
    * @see ChefApi#getNode(String)
    */
   @Named("node:get")
   @GET
   @Path("/nodes/{nodename}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Node> getNode(@PathParam("nodename") String nodename);

   /**
    * @see ChefNode#deleteNode
    */
   @Named("node:delete")
   @DELETE
   @Path("/nodes/{nodename}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Node> deleteNode(@PathParam("nodename") String nodename);

   /**
    * @see ChefApi#listNodes()
    */
   @Named("node:list")
   @GET
   @Path("/nodes")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listNodes();

   /**
    * @see ChefApi#createRole(Role)
    */
   @Named("role:create")
   @POST
   @Path("/roles")
   ListenableFuture<Void> createRole(@BinderParam(BindToJsonPayload.class) Role role);

   /**
    * @see ChefApi#updateRole(Role)
    */
   @Named("role:update")
   @PUT
   @Path("/roles/{rolename}")
   ListenableFuture<Role> updateRole(
         @PathParam("rolename") @ParamParser(RoleName.class) @BinderParam(BindToJsonPayload.class) Role role);

   /**
    * @see ChefApi#roleExists(String)
    */
   @Named("role:exists")
   @HEAD
   @Path("/roles/{rolename}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> roleExists(@PathParam("rolename") String rolename);

   /**
    * @see ChefApi#getRole(String)
    */
   @Named("role:get")
   @GET
   @Path("/roles/{rolename}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Role> getRole(@PathParam("rolename") String rolename);

   /**
    * @see ChefApi#deleteRole(String)
    */
   @Named("role:delete")
   @DELETE
   @Path("/roles/{rolename}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Role> deleteRole(@PathParam("rolename") String rolename);

   /**
    * @see ChefApi#listRoles()
    */
   @Named("role:list")
   @GET
   @Path("/roles")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listRoles();

   /**
    * @see ChefApi#listDatabags()
    */
   @Named("databag:list")
   @GET
   @Path("/data")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listDatabags();

   /**
    * @see ChefApi#createDatabag(String)
    */
   @Named("databag:create")
   @POST
   @Path("/data")
   ListenableFuture<Void> createDatabag(@BinderParam(BindNameToJsonPayload.class) String databagName);

   /**
    * @see ChefApi#databagExists(String)
    */
   @Named("databag:exists")
   @HEAD
   @Path("/data/{name}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> databagExists(@PathParam("name") String databagName);

   /**
    * @see ChefApi#deleteDatabag(String)
    */
   @Named("databag:delete")
   @DELETE
   @Path("/data/{name}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteDatabag(@PathParam("name") String databagName);

   /**
    * @see ChefApi#listDatabagItems(String)
    */
   @Named("databag:listitems")
   @GET
   @Path("/data/{name}")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listDatabagItems(@PathParam("name") String databagName);

   /**
    * @see ChefApi#createDatabagItem(String, DatabagItem)
    */
   @Named("databag:createitem")
   @POST
   @Path("/data/{databagName}")
   ListenableFuture<DatabagItem> createDatabagItem(@PathParam("databagName") String databagName,
         @BinderParam(BindToJsonPayload.class) DatabagItem databagItem);

   /**
    * @see ChefApi#updateDatabagItem(String, DatabagItem)
    */
   @Named("databag:updateitem")
   @PUT
   @Path("/data/{databagName}/{databagItemId}")
   ListenableFuture<DatabagItem> updateDatabagItem(
         @PathParam("databagName") String databagName,
         @PathParam("databagItemId") @ParamParser(DatabagItemId.class) @BinderParam(BindToJsonPayload.class) DatabagItem item);

   /**
    * @see ChefApi#databagItemExists(String, String)
    */
   @Named("databag:itemexists")
   @HEAD
   @Path("/data/{databagName}/{databagItemId}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> databagItemExists(@PathParam("databagName") String databagName,
         @PathParam("databagItemId") String databagItemId);

   /**
    * @see ChefApi#getDatabagItem(String, String)
    */
   @Named("databag:getitem")
   @GET
   @Path("/data/{databagName}/{databagItemId}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<DatabagItem> getDatabagItem(@PathParam("databagName") String databagName,
         @PathParam("databagItemId") String databagItemId);

   /**
    * @see ChefApi#deleteDatabagItem(String, String)
    */
   @Named("databag:deleteitem")
   @DELETE
   @Path("/data/{databagName}/{databagItemId}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<DatabagItem> deleteDatabagItem(@PathParam("databagName") String databagName,
         @PathParam("databagItemId") String databagItemId);

   /**
    * @see ChefApi#listSearchIndexes()
    */
   @Named("search:indexes")
   @GET
   @Path("/search")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listSearchIndexes();

   /**
    * @see ChefApi#searchRoles()
    */
   @Named("search:roles")
   @GET
   @Path("/search/role")
   @ResponseParser(ParseSearchRolesFromJson.class)
   ListenableFuture<? extends SearchResult<? extends Role>> searchRoles();

   /**
    * @see ChefApi#searchRoles(SearchOptions)
    */
   @Named("search:roles")
   @GET
   @Path("/search/role")
   @ResponseParser(ParseSearchRolesFromJson.class)
   ListenableFuture<? extends SearchResult<? extends Role>> searchRoles(SearchOptions options);

   /**
    * @see ChefApi#searchClients()
    */
   @Named("search:clients")
   @GET
   @Path("/search/client")
   @ResponseParser(ParseSearchClientsFromJson.class)
   ListenableFuture<? extends SearchResult<? extends Client>> searchClients();

   /**
    * @see ChefApi#searchClients(SearchOptions)
    */
   @Named("search:clients")
   @GET
   @Path("/search/client")
   @ResponseParser(ParseSearchClientsFromJson.class)
   ListenableFuture<? extends SearchResult<? extends Client>> searchClients(SearchOptions options);

   /**
    * @see ChefApi#searchNodes()
    */
   @Named("search:nodes")
   @GET
   @Path("/search/node")
   @ResponseParser(ParseSearchNodesFromJson.class)
   ListenableFuture<? extends SearchResult<? extends Node>> searchNodes();

   /**
    * @see ChefApi#searchNodes(SearchOptions)
    */
   @Named("search:nodes")
   @GET
   @Path("/search/node")
   @ResponseParser(ParseSearchNodesFromJson.class)
   ListenableFuture<? extends SearchResult<? extends Node>> searchNodes(SearchOptions options);

   /**
    * @see ChefApi#searchDatabag(String)
    */
   @Named("search:databag")
   @GET
   @Path("/search/{databagName}")
   @ResponseParser(ParseSearchDatabagFromJson.class)
   ListenableFuture<? extends SearchResult<? extends DatabagItem>> searchDatabag(
         @PathParam("databagName") String databagName);

   /**
    * @see ChefApi#searchDatabag(String, SearchOptions)
    */
   @Named("search:databag")
   @GET
   @Path("/search/{databagName}")
   @ResponseParser(ParseSearchDatabagFromJson.class)
   ListenableFuture<? extends SearchResult<? extends DatabagItem>> searchDatabag(
         @PathParam("databagName") String databagName, SearchOptions options);

   /**
    * @see ChefApi#getResourceContents(Resource)
    */
   @Named("content:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<InputStream> getResourceContents(@EndpointParam(parser = UriForResource.class) Resource resource);
}
