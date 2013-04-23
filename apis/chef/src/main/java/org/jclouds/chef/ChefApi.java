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

import java.io.Closeable;
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
import org.jclouds.chef.binders.EnvironmentName;
import org.jclouds.chef.binders.NodeName;
import org.jclouds.chef.binders.RoleName;
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
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.functions.ParseCookbookDefinitionCheckingChefVersion;
import org.jclouds.chef.functions.ParseCookbookDefinitionFromJsonv10;
import org.jclouds.chef.functions.ParseCookbookDefinitionListFromJsonv10;
import org.jclouds.chef.functions.ParseCookbookVersionsCheckingChefVersion;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.chef.functions.ParseSearchClientsFromJson;
import org.jclouds.chef.functions.ParseSearchDatabagFromJson;
import org.jclouds.chef.functions.ParseSearchEnvironmentsFromJson;
import org.jclouds.chef.functions.ParseSearchNodesFromJson;
import org.jclouds.chef.functions.ParseSearchRolesFromJson;
import org.jclouds.chef.functions.UriForResource;
import org.jclouds.chef.options.CreateClientOptions;
import org.jclouds.chef.options.SearchOptions;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides synchronous access to Chef.
 * 
 * @author Adrian Cole
 * @author Ignasi Barrera
 */
@RequestFilters(SignedHeaderAuth.class)
@Headers(keys = "X-Chef-Version", values = "{" + Constants.PROPERTY_API_VERSION + "}")
@Consumes(MediaType.APPLICATION_JSON)
public interface ChefApi extends Closeable {
   /**
    * The target Chef Server version.
    */
   public static final String VERSION = "0.10.8";

   /**
    * Creates a new sandbox. It accepts a list of checksums as input and returns
    * the URLs against which to PUT files that need to be uploaded.
    * 
    * @param md5s
    *           raw md5s; uses {@code Bytes.asList()} and
    *           {@code Bytes.toByteArray()} as necessary
    * @return The URLs against which to PUT files that need to be uploaded.
    */
   @Named("sandbox:upload")
   @POST
   @Path("/sandboxes")
   UploadSandbox getUploadSandboxForChecksums(@BinderParam(BindChecksumsToJsonPayload.class) Set<List<Byte>> md5s);

   /**
    * Uploads the given content to the sandbox at the given URI.
    * <p/>
    * The URI must be obtained, after uploading a sandbox, from the
    * {@link UploadSandbox#getUri()}.
    */
   @Named("content:upload")
   @PUT
   @Produces("application/x-binary")
   void uploadContent(@EndpointParam URI location, Payload content);

   /**
    * Confirms if the sandbox is completed or not.
    * <p/>
    * This method should be used after uploading contents to the sandbox.
    * 
    * @return The sandbox
    */
   @Named("sandbox:commit")
   @PUT
   @Path("/sandboxes/{id}")
   Sandbox commitSandbox(@PathParam("id") String id,
         @BinderParam(BindIsCompletedToJsonPayload.class) boolean isCompleted);

   /**
    * @return a list of all the cookbook names
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have permission to see the
    *            cookbook list.
    */
   @Named("cookbook:list")
   @GET
   @Path("/cookbooks")
   @ResponseParser(ParseCookbookDefinitionCheckingChefVersion.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listCookbooks();

   /**
    * Creates or updates (uploads) a cookbook
    * 
    * @param cookbookName
    * @throws HttpResponseException
    *            "409 Conflict" if the cookbook already exists
    */
   @Named("cookbook:update")
   @PUT
   @Path("/cookbooks/{cookbookname}/{version}")
   CookbookVersion updateCookbook(@PathParam("cookbookname") String cookbookName, @PathParam("version") String version,
         @BinderParam(BindToJsonPayload.class) CookbookVersion cookbook);

   /**
    * deletes an existing cookbook.
    * 
    * @return last state of the api you deleted or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the
    *            cookbook.
    */
   @Named("cookbook:delete")
   @DELETE
   @Path("/cookbooks/{cookbookname}/{version}")
   @Fallback(NullOnNotFoundOr404.class)
   CookbookVersion deleteCookbook(@PathParam("cookbookname") String cookbookName, @PathParam("version") String version);

   /**
    * @return the versions of a cookbook or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to view the
    *            cookbook.
    */
   @Named("cookbook:versions")
   @GET
   @Path("/cookbooks/{cookbookname}")
   @ResponseParser(ParseCookbookVersionsCheckingChefVersion.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> getVersionsOfCookbook(@PathParam("cookbookname") String cookbookName);

   /**
    * Returns a description of the cookbook, with links to all of its component
    * parts, and the metadata.
    * 
    * @return the cookbook or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to view the
    *            cookbook.
    */
   @Named("cookbook:get")
   @GET
   @Path("/cookbooks/{cookbookname}/{version}")
   @Fallback(NullOnNotFoundOr404.class)
   CookbookVersion getCookbook(@PathParam("cookbookname") String cookbookName, @PathParam("version") String version);

   /**
    * creates a new client
    * 
    * @return the private key of the client. You can then use this client name
    *         and private key to access the Opscode API.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a
    *            client.
    * @throws HttpResponseException
    *            "409 Conflict" if the client already exists
    */
   @Named("client:create")
   @POST
   @Path("/clients")
   @MapBinder(BindToJsonPayload.class)
   Client createClient(@PayloadParam("name") String clientname);

   /**
    * creates a new administrator client
    * 
    * @return the private key of the client. You can then use this client name
    *         and private key to access the Opscode API.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a
    *            client.
    * @throws HttpResponseException
    *            "409 Conflict" if the client already exists
    */
   @Named("client:create")
   @POST
   @Path("/clients")
   @MapBinder(BindCreateClientOptionsToJsonPayload.class)
   Client createClient(@PayloadParam("name") String clientname, CreateClientOptions options);

   /**
    * generate a new key-pair for this client, and return the new private key in
    * the response body.
    * 
    * @return the new private key
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to modify the
    *            client.
    */
   @Named("client:generatekey")
   @PUT
   @Path("/clients/{clientname}")
   Client generateKeyForClient(
         @PathParam("clientname") @BinderParam(BindGenerateKeyForClientToJsonPayload.class) String clientname);

   /**
    * @return list of client names.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list clients.
    */
   @Named("client:list")
   @GET
   @Path("/clients")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listClients();

   /**
    * @return true if the specified client name exists.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to view the client.
    */
   @Named("client:exists")
   @HEAD
   @Path("/clients/{clientname}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean clientExists(@PathParam("clientname") String clientname);

   /**
    * deletes an existing client.
    * 
    * @return last state of the client you deleted or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the client.
    */
   @Named("client:delete")
   @DELETE
   @Path("/clients/{clientname}")
   @Fallback(NullOnNotFoundOr404.class)
   Client deleteClient(@PathParam("clientname") String clientname);

   /**
    * gets an existing client.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the client.
    */
   @Named("client:get")
   @GET
   @Path("/clients/{clientname}")
   @Fallback(NullOnNotFoundOr404.class)
   Client getClient(@PathParam("clientname") String clientname);

   /**
    * creates a new node
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a
    *            node.
    * @throws HttpResponseException
    *            "409 Conflict" if the node already exists
    */
   @Named("node:create")
   @POST
   @Path("/nodes")
   void createNode(@BinderParam(BindToJsonPayload.class) Node node);

   /**
    * Creates or updates (uploads) a node
    * 
    * @param node
    *           updated node
    * @throws HttpResponseException
    *            "409 Conflict" if the node already exists
    */
   @Named("node:update")
   @PUT
   @Path("/nodes/{nodename}")
   Node updateNode(@PathParam("nodename") @ParamParser(NodeName.class) @BinderParam(BindToJsonPayload.class) Node node);

   /**
    * @return list of node names.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list nodes.
    */
   @Named("node:list")
   @GET
   @Path("/nodes")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listNodes();

   /**
    * @return true if the specified node name exists.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to view the node.
    */
   @Named("node:exists")
   @HEAD
   @Path("/nodes/{nodename}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean nodeExists(@PathParam("nodename") String nodename);

   /**
    * deletes an existing node.
    * 
    * @return last state of the node you deleted or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the node.
    */
   @Named("node:delete")
   @DELETE
   @Path("/nodes/{nodename}")
   @Fallback(NullOnNotFoundOr404.class)
   Node deleteNode(@PathParam("nodename") String nodename);

   /**
    * gets an existing node.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the node.
    */
   @Named("node:get")
   @GET
   @Path("/nodes/{nodename}")
   @Fallback(NullOnNotFoundOr404.class)
   Node getNode(@PathParam("nodename") String nodename);

   /**
    * creates a new role
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a
    *            role.
    * @throws HttpResponseException
    *            "409 Conflict" if the role already exists
    */
   @Named("role:create")
   @POST
   @Path("/roles")
   void createRole(@BinderParam(BindToJsonPayload.class) Role role);

   /**
    * Creates or updates (uploads) a role
    * 
    * @param roleName
    * @throws HttpResponseException
    *            "409 Conflict" if the role already exists
    */
   @Named("role:update")
   @PUT
   @Path("/roles/{rolename}")
   Role updateRole(@PathParam("rolename") @ParamParser(RoleName.class) @BinderParam(BindToJsonPayload.class) Role role);

   /**
    * @return list of role names.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list roles.
    */
   @Named("role:list")
   @GET
   @Path("/roles")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listRoles();

   /**
    * @return true if the specified role name exists.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to view the role.
    */
   @Named("role:exists")
   @HEAD
   @Path("/roles/{rolename}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean roleExists(@PathParam("rolename") String rolename);

   /**
    * deletes an existing role.
    * 
    * @return last state of the role you deleted or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the role.
    */
   @Named("role:delete")
   @DELETE
   @Path("/roles/{rolename}")
   @Fallback(NullOnNotFoundOr404.class)
   Role deleteRole(@PathParam("rolename") String rolename);

   /**
    * gets an existing role.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the role.
    */
   @Named("role:get")
   @GET
   @Path("/roles/{rolename}")
   @Fallback(NullOnNotFoundOr404.class)
   Role getRole(@PathParam("rolename") String rolename);

   /**
    * lists databags available to the api
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("databag:list")
   @GET
   @Path("/data")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listDatabags();

   /**
    * creates a databag.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("databag:create")
   @POST
   @Path("/data")
   void createDatabag(@BinderParam(BindNameToJsonPayload.class) String databagName);

   /**
    * true is a databag exists
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("databag:exists")
   @HEAD
   @Path("/data/{name}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean databagExists(@PathParam("name") String databagName);

   /**
    * Delete a data bag, including its items
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("databag:delete")
   @DELETE
   @Path("/data/{name}")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteDatabag(@PathParam("name") String databagName);

   /**
    * Show the items in a data bag.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("databag:listitems")
   @GET
   @Path("/data/{name}")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listDatabagItems(@PathParam("name") String databagName);

   /**
    * Create a data bag item in the data bag
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    *            <p/>
    * @throws IllegalStateException
    *            if the item already exists
    */
   @Named("databag:createitem")
   @POST
   @Path("/data/{databagName}")
   DatabagItem createDatabagItem(@PathParam("databagName") String databagName,
         @BinderParam(BindToJsonPayload.class) DatabagItem databagItem);

   /**
    * Update (or create if not exists) a data bag item
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("databag:updateitem")
   @PUT
   @Path("/data/{databagName}/{databagItemId}")
   DatabagItem updateDatabagItem(
         @PathParam("databagName") String databagName,
         @PathParam("databagItemId") @ParamParser(DatabagItemId.class) @BinderParam(BindToJsonPayload.class) DatabagItem item);

   /**
    * determines if a databag item exists
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("databag:itemexists")
   @HEAD
   @Path("/data/{databagName}/{databagItemId}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean databagItemExists(@PathParam("databagName") String databagName,
         @PathParam("databagItemId") String databagItemId);

   /**
    * gets an existing databag item.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("databag:getitem")
   @GET
   @Path("/data/{databagName}/{databagItemId}")
   @Fallback(NullOnNotFoundOr404.class)
   DatabagItem getDatabagItem(@PathParam("databagName") String databagName,
         @PathParam("databagItemId") String databagItemId);

   /**
    * Delete a data bag item
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("databag:deleteitem")
   @DELETE
   @Path("/data/{databagName}/{databagItemId}")
   @Fallback(NullOnNotFoundOr404.class)
   DatabagItem deleteDatabagItem(@PathParam("databagName") String databagName,
         @PathParam("databagItemId") String databagItemId);

   /**
    * Show indexes you can search on
    * <p/>
    * By default, the "role", "node" and "api" indexes will always be available.
    * <p/>
    * Note that the search indexes may lag behind the most current data by at
    * least 10 seconds at any given time - so if you need to write data and
    * immediately query it, you likely need to produce an artificial delay (or
    * simply retry until the data is available.)
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   @Named("search:indexes")
   @GET
   @Path("/search")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listSearchIndexes();

   /**
    * search all roles.
    * <p/>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:roles")
   @GET
   @Path("/search/role")
   @ResponseParser(ParseSearchRolesFromJson.class)
   SearchResult<? extends Role> searchRoles();

   /**
    * search all roles that match the given options.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:roles")
   @GET
   @Path("/search/role")
   @ResponseParser(ParseSearchRolesFromJson.class)
   SearchResult<? extends Role> searchRoles(SearchOptions options);

   /**
    * search all clients.
    * <p/>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:clients")
   @GET
   @Path("/search/client")
   @ResponseParser(ParseSearchClientsFromJson.class)
   SearchResult<? extends Client> searchClients();

   /**
    * search all clients that match the given options.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:clients")
   @GET
   @Path("/search/client")
   @ResponseParser(ParseSearchClientsFromJson.class)
   SearchResult<? extends Client> searchClients(SearchOptions options);

   /**
    * search all nodes.
    * <p/>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:nodes")
   @GET
   @Path("/search/node")
   @ResponseParser(ParseSearchNodesFromJson.class)
   SearchResult<? extends Node> searchNodes();

   /**
    * search all nodes that match the given options.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:nodes")
   @GET
   @Path("/search/node")
   @ResponseParser(ParseSearchNodesFromJson.class)
   SearchResult<? extends Node> searchNodes(SearchOptions options);

   /**
    * search all items in a databag.
    * <p/>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:databag")
   @GET
   @Path("/search/{databagName}")
   @ResponseParser(ParseSearchDatabagFromJson.class)
   SearchResult<? extends DatabagItem> searchDatabag(@PathParam("databagName") String databagName);

   /**
    * search all items in a databag that match the given options.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:databag")
   @GET
   @Path("/search/{databagName}")
   @ResponseParser(ParseSearchDatabagFromJson.class)
   SearchResult<? extends DatabagItem> searchDatabag(@PathParam("databagName") String databagName, SearchOptions options);

   /**
    * search all items in a environment that match the given options.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @SinceApiVersion("0.10.0")
   @Named("search:environments")
   @GET
   @Path("/search/environment")
   @ResponseParser(ParseSearchEnvironmentsFromJson.class)
   SearchResult<? extends Environment> searchEnvironments();

   /**
    * search all environments that match the given options.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @SinceApiVersion("0.10.0")
   @Named("search:environments")
   @GET
   @Path("/search/environment")
   @ResponseParser(ParseSearchEnvironmentsFromJson.class)
   SearchResult<? extends Environment> searchEnvironments(SearchOptions options);

   /**
    * Get the contents of the given resource.
    * 
    * @param resource
    *           The resource to get.
    * @return An input stream for the content of the requested resource.
    */
   @Named("content:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   InputStream getResourceContents(@EndpointParam(parser = UriForResource.class) Resource resource);

   /**
    * @return list of environments names.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list environments.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:list")
   @GET
   @Path("/environments")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listEnvironments();

   /**
    * creates a new environment
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a
    *            client.
    * @throws HttpResponseException
    *            "409 Conflict" if the client already exists
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:create")
   @POST
   @Path("/environments")
   void createEnvironment(@BinderParam(BindToJsonPayload.class) Environment environment);

   /**
    * Creates or updates (uploads) a environment
    * 
    * @param environment
    * @throws HttpResponseException
    *            "409 Conflict" if the node already exists
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:update")
   @PUT
   @Path("/environments/{environmentname}")
   Environment updateEnvironment(
         @PathParam("environmentname") @ParamParser(EnvironmentName.class) @BinderParam(BindToJsonPayload.class) Environment environment);

   /**
    * deletes an existing environment.
    * 
    * @return last state of the environment you deleted or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the node.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:delete")
   @DELETE
   @Path("/environments/{environmentname}")
   @Fallback(NullOnNotFoundOr404.class)
   Environment deleteEnvironment(@PathParam("environmentname") String environmentname);

   /**
    * gets an existing environment.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the node.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:get")
   @GET
   @Path("/environments/{environmentname}")
   @Fallback(NullOnNotFoundOr404.class)
   Environment getEnvironment(@PathParam("environmentname") String environmentname);

   /**
    * gets an environment cookbook list, show only latest cookbook version
    * 
    * @return List of environment cookbooks
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the node.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:cookbooklist")
   @GET
   @ResponseParser(ParseCookbookDefinitionListFromJsonv10.class)
   @Path("/environments/{environmentname}/cookbooks")
   @Fallback(NullOnNotFoundOr404.class)
   Set<CookbookDefinition> listEnvironmentCookbooks(@PathParam("environmentname") String environmentname);

   /**
    * gets an environment cookbook list
    * 
    * @param environmentname
    *           environment name that you looking for
    * @param numversions
    *           how many versions you want to see: 3 returns 3 latest versions,
    *           in descending order (high to low); all returns all available
    *           versions in this environment, in descending order (high to low);
    *           0 is a valid input that returns an empty array for the versions
    *           of each cookbooks.up
    * @return List of environment cookbooks
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the node.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:cookbooklist")
   @GET
   @ResponseParser(ParseCookbookDefinitionListFromJsonv10.class)
   @Path("/environments/{environmentname}/cookbooks?num_versions={numversions}")
   @Fallback(NullOnNotFoundOr404.class)
   Set<CookbookDefinition> listEnvironmentCookbooks(@PathParam("environmentname") String environmentname,
         @PathParam("numversions") String numversions);

   @SinceApiVersion("0.10.0")
   @Named("environment:cookbook")
   @GET
   @ResponseParser(ParseCookbookDefinitionFromJsonv10.class)
   @Path("/environments/{environmentname}/cookbooks/{cookbookname}")
   CookbookDefinition getEnvironmentCookbook(@PathParam("environmentname") String environmentname,
         @PathParam("cookbookname") String cookbookname);

   @SinceApiVersion("0.10.0")
   @Named("environment:cookbook")
   @GET
   @ResponseParser(ParseCookbookDefinitionFromJsonv10.class)
   @Path("/environments/{environmentname}/cookbooks/{cookbookname}?num_versions={numversions}")
   CookbookDefinition getEnvironmentCookbook(@PathParam("environmentname") String environmentname,
         @PathParam("cookbookname") String cookbookname, @PathParam("numversions") String numversions);
}
