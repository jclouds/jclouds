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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Constants;
import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.chef.binders.BindChecksumsToJsonPayload;
import org.jclouds.chef.binders.BindCreateClientOptionsToJsonPayload;
import org.jclouds.chef.binders.BindGenerateKeyForClientToJsonPayload;
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
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.inject.Provides;

/**
 * Provides synchronous access to Chef.
 */
@RequestFilters(SignedHeaderAuth.class)
@Headers(keys = "X-Chef-Version", values = "{" + Constants.PROPERTY_API_VERSION + "}")
@Consumes(MediaType.APPLICATION_JSON)
public interface ChefApi extends Closeable {
   
   /**
    * Provides access to high level Chef features.
    */
   @Provides
   ChefService chefService();

   // Clients

   /**
    * Lists the names of the existing clients.
    * 
    * @return The names of the existing clients.
    */
   @Named("client:list")
   @GET
   @Path("/clients")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listClients();

   /**
    * Gets the details of existing client.
    * 
    * @param clientname The name of the client to get.
    * @return The details of the given client.
    */
   @Named("client:get")
   @GET
   @Path("/clients/{clientname}")
   @Fallback(NullOnNotFoundOr404.class)
   Client getClient(@PathParam("clientname") String clientName);

   /**
    * Creates a new client.
    * 
    * @param clientname The name of the new client
    * @return The client with the generated private key. This key should be
    *         stored so client can be properly authenticated .
    */
   @Named("client:create")
   @POST
   @Path("/clients")
   @MapBinder(BindToJsonPayload.class)
   Client createClient(@PayloadParam("name") String clientName);

   /**
    * Creates a new client with custom options.
    * 
    * @param clientname The name of the new client
    * @param options The options to customize the client creation.
    * @return The client with the generated private key. This key should be
    *         stored so client can be properly authenticated .
    */
   @Named("client:create")
   @POST
   @Path("/clients")
   @MapBinder(BindCreateClientOptionsToJsonPayload.class)
   Client createClient(@PayloadParam("name") String clientName, CreateClientOptions options);

   /**
    * Generates a new key-pair for this client, and return the new private key in
    * the response body.
    * 
    * @param clientname The name of the client.
    * @return The details of the client with the new private key.
    */
   @Named("client:generatekey")
   @PUT
   @Path("/clients/{clientname}")
   Client generateKeyForClient(
         @PathParam("clientname") @BinderParam(BindGenerateKeyForClientToJsonPayload.class) String clientName);

   /**
    * Deletes the given client.
    * 
    * @param clientname The name of the client to delete.
    * @return The deleted client.
    */
   @Named("client:delete")
   @DELETE
   @Path("/clients/{clientname}")
   @Fallback(NullOnNotFoundOr404.class)
   Client deleteClient(@PathParam("clientname") String clientName);

   // Cookbooks

   /**
    * Lists the names of the existing cookbooks.
    * 
    * @return The names of the exsisting cookbooks.
    */
   @Named("cookbook:list")
   @GET
   @Path("/cookbooks")
   @ResponseParser(ParseCookbookDefinitionCheckingChefVersion.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listCookbooks();

   /**
    * Lists the cookbooks that are available in the given environment.
    * 
    * @param environmentname The name of the environment to get the cookbooks
    *        from.
    * @return The definitions of the cookbooks (name, URL and versions) available in
    *         the given environment.
    */
   @SinceApiVersion("0.10.0")
   @Named("cookbook:list")
   @GET
   @ResponseParser(ParseCookbookDefinitionListFromJsonv10.class)
   @Path("/environments/{environmentname}/cookbooks")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<CookbookDefinition> listCookbooksInEnvironment(@PathParam("environmentname") String environmentName);

   /**
    * Lists the cookbooks that are available in the given environment, limiting
    * the number of versions returned for each cookbook.
    * 
    * @param environmentname The name of the environment to get the cookbooks
    *        from.
    * @param numversions The number of cookbook versions to include in the
    *        response, where n is the number of cookbook versions.
    * @return The definitions of the cookbooks (name, URL and versions) available in
    *         the given environment.
    */
   @SinceApiVersion("0.10.0")
   @Named("cookbook:list")
   @GET
   @ResponseParser(ParseCookbookDefinitionListFromJsonv10.class)
   @Path("/environments/{environmentname}/cookbooks?num_versions={numversions}")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<CookbookDefinition> listCookbooksInEnvironment(@PathParam("environmentname") String environmentName,
         @PathParam("numversions") String numVersions);

   /**
    * Lists the available versions of the given cookbook.
    * 
    * @param cookbookName The name of the cookbook.
    * @return The available versions of the given cookbook.
    */
   @Named("cookbook:versions")
   @GET
   @Path("/cookbooks/{cookbookname}")
   @ResponseParser(ParseCookbookVersionsCheckingChefVersion.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listVersionsOfCookbook(@PathParam("cookbookname") String cookbookName);

   /**
    * Gets the details of the given cookbook, with the links to each resource
    * such as recipe files, attributes, etc.
    * 
    * @param cookbookName The name of the cookbook.
    * @param version The version of the cookbook to get.
    * @return The details of the given cookbook.
    */
   @Named("cookbook:get")
   @GET
   @Path("/cookbooks/{cookbookname}/{version}")
   @Fallback(NullOnNotFoundOr404.class)
   CookbookVersion getCookbook(@PathParam("cookbookname") String cookbookName, @PathParam("version") String version);

   /**
    * Gets the definition of the cookbook in the given environment.
    * 
    * @param environmentname The name of the environment.
    * @param cookbookname The name of the cookbook.
    * @return The definition of the cookbook (URL and versions) of the cookbook
    *         in the given environment.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:cookbook")
   @GET
   @ResponseParser(ParseCookbookDefinitionFromJsonv10.class)
   @Path("/environments/{environmentname}/cookbooks/{cookbookname}")
   CookbookDefinition getCookbookInEnvironment(@PathParam("environmentname") String environmentName,
         @PathParam("cookbookname") String cookbookName);

   /**
    * Gets the definition of the cookbook in the given environment.
    * 
    * @param environmentname The name of the environment.
    * @param cookbookname The name of the cookbook.
    * @param numversions The number of cookbook versions to include in the
    *        response, where n is the number of cookbook versions.
    * @return The definition of the cookbook (URL and versions) of the cookbook
    *         in the given environment.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:cookbook")
   @GET
   @ResponseParser(ParseCookbookDefinitionFromJsonv10.class)
   @Path("/environments/{environmentname}/cookbooks/{cookbookname}?num_versions={numversions}")
   CookbookDefinition getCookbookInEnvironment(@PathParam("environmentname") String environmentName,
         @PathParam("cookbookname") String cookbookName, @PathParam("numversions") String numVersions);

   /**
    * Lists the names of the recipes in the given environment.
    * 
    * @param environmentname The name of the environment.
    * @return The names of the recipes in the given environment.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:recipelist")
   @GET
   @Path("/environments/{environmentname}/recipes")
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listRecipesInEnvironment(@PathParam("environmentname") String environmentName);

   /**
    * Creates or updates the given cookbook.
    * 
    * @param cookbookName The name of the cookbook to create or update.
    * @param version The version of the cookbook to create or update.
    * @param cookbook The contents of the cookbook to create or update.
    * @return The details of the created or updated cookbook.
    */
   @Named("cookbook:update")
   @PUT
   @Path("/cookbooks/{cookbookname}/{version}")
   CookbookVersion updateCookbook(@PathParam("cookbookname") String cookbookName, @PathParam("version") String version,
         @BinderParam(BindToJsonPayload.class) CookbookVersion cookbook);

   /**
    * Deletes the given cookbook.
    * 
    * @param cookbookName The name of the cookbook to delete.
    * @param version The version of the cookbook to delete.
    * @return The details of the deleted cookbook.
    */
   @Named("cookbook:delete")
   @DELETE
   @Path("/cookbooks/{cookbookname}/{version}")
   @Fallback(NullOnNotFoundOr404.class)
   CookbookVersion deleteCookbook(@PathParam("cookbookname") String cookbookName, @PathParam("version") String version);

   // Data bags

   /**
    * Lists the names of the existing data bags.
    * 
    * @return The names of the existing data bags.
    */
   @Named("databag:list")
   @GET
   @Path("/data")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listDatabags();

   /**
    * Creates a new data bag.
    * 
    * @param databagName The name for the new data bag.
    */
   @Named("databag:create")
   @POST
   @Path("/data")
   void createDatabag(@WrapWith("name") String databagName);

   /**
    * Deletes a data bag, including its items.
    * 
    * @param databagName The name of the data bag to delete.
    */
   @Named("databag:delete")
   @DELETE
   @Path("/data/{name}")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteDatabag(@PathParam("name") String databagName);

   /**
    * Lists the names of the items in a data bag.
    * 
    * @param databagName The name of the data bag.
    * @return The names of the items in the given data bag.
    */
   @Named("databag:listitems")
   @GET
   @Path("/data/{name}")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listDatabagItems(@PathParam("name") String databagName);

   /**
    * Gets an item in a data bag.
    * 
    * @param databagName The name of the data bag.
    * @param databagItemId The identifier of the item to get.
    * @return The details of the item in the given data bag.
    */
   @Named("databag:getitem")
   @GET
   @Path("/data/{databagName}/{databagItemId}")
   @Fallback(NullOnNotFoundOr404.class)
   DatabagItem getDatabagItem(@PathParam("databagName") String databagName,
         @PathParam("databagItemId") String databagItemId);

   /**
    * Adds an item in a data bag.
    * 
    * @param databagName The name of the data bag.
    * @param The item to add to the data bag.
    * @param The item just added to the data bag.
    */
   @Named("databag:createitem")
   @POST
   @Path("/data/{databagName}")
   DatabagItem createDatabagItem(@PathParam("databagName") String databagName,
         @BinderParam(BindToJsonPayload.class) DatabagItem databagItem);

   /**
    * Updates an item in a data bag.
    * 
    * @param databagName The name of the data bag.
    * @param item The new contents for the item in the data bag.
    * @return The details for the updated item in the data bag.
    */
   @Named("databag:updateitem")
   @PUT
   @Path("/data/{databagName}/{databagItemId}")
   DatabagItem updateDatabagItem(
         @PathParam("databagName") String databagName,
         @PathParam("databagItemId") @ParamParser(DatabagItemId.class) @BinderParam(BindToJsonPayload.class) DatabagItem item);

   /**
    * Deletes an item from a data bag.
    * 
    * @param databagName The name of the data bag.
    * @param databagItemId The identifier of the item to delete.
    * @return The item deleted from the data bag.
    */
   @Named("databag:deleteitem")
   @DELETE
   @Path("/data/{databagName}/{databagItemId}")
   @Fallback(NullOnNotFoundOr404.class)
   @SelectJson("raw_data")
   DatabagItem deleteDatabagItem(@PathParam("databagName") String databagName,
         @PathParam("databagItemId") String databagItemId);

   // Environments

   /**
    * Lists the names of the existing environments.
    * 
    * @return The names of the existing environments.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:list")
   @GET
   @Path("/environments")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listEnvironments();

   /**
    * Gets the details of an existing environment.
    * 
    * @param environmentname The name of the environment to get.
    * @return The details of the given environment.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:get")
   @GET
   @Path("/environments/{environmentname}")
   @Fallback(NullOnNotFoundOr404.class)
   Environment getEnvironment(@PathParam("environmentname") String environmentName);

   /**
    * Creates a new environment.
    * 
    * @param environment The environment to create.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:create")
   @POST
   @Path("/environments")
   void createEnvironment(@BinderParam(BindToJsonPayload.class) Environment environment);

   /**
    * Updates the given environment.
    * 
    * @param environment The new details for the environment.
    * @return The details of the updated environment.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:update")
   @PUT
   @Path("/environments/{environmentname}")
   Environment updateEnvironment(
         @PathParam("environmentname") @ParamParser(EnvironmentName.class) @BinderParam(BindToJsonPayload.class) Environment environment);

   /**
    * Deletes the given environment.
    * 
    * @param environmentname The name of the environment to delete.
    * @return The details of the deleted environment.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:delete")
   @DELETE
   @Path("/environments/{environmentname}")
   @Fallback(NullOnNotFoundOr404.class)
   Environment deleteEnvironment(@PathParam("environmentname") String environmentName);

   // Nodes

   /**
    * Lists the names of the existing nodes.
    * 
    * @return The names of the existing nodes.
    */
   @Named("node:list")
   @GET
   @Path("/nodes")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listNodes();

   /**
    * Lists the names of the nodes in the given environment.
    * 
    * @param environmentname The name of the environment.
    * @return The names of the existing nodes in the given environment.
    */
   @SinceApiVersion("0.10.0")
   @Named("environment:nodelist")
   @GET
   @Path("/environments/{environmentname}/nodes")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listNodesInEnvironment(@PathParam("environmentname") String environmentName);

   /**
    * Gets the details of the given node.
    * 
    * @param nodename The name of the node to get.
    * @return The details of the given node.
    */
   @Named("node:get")
   @GET
   @Path("/nodes/{nodename}")
   @Fallback(NullOnNotFoundOr404.class)
   Node getNode(@PathParam("nodename") String nodeName);

   /**
    * Creates a new node.
    * 
    * @param node The details of the node to create.
    */
   @Named("node:create")
   @POST
   @Path("/nodes")
   void createNode(@BinderParam(BindToJsonPayload.class) Node node);

   /**
    * Updates an existing node.
    * 
    * @param node The new details for the node.
    * @return The details of the updated node.
    */
   @Named("node:update")
   @PUT
   @Path("/nodes/{nodename}")
   Node updateNode(@PathParam("nodename") @ParamParser(NodeName.class) @BinderParam(BindToJsonPayload.class) Node node);

   /**
    * Deletes the given node.
    * 
    * @param nodename The name of the node to delete.
    * @return The details of the deleted node.
    */
   @Named("node:delete")
   @DELETE
   @Path("/nodes/{nodename}")
   @Fallback(NullOnNotFoundOr404.class)
   Node deleteNode(@PathParam("nodename") String nodeName);

   // Roles

   /**
    * Lists the names of the existing roles.
    * 
    * @return The names of the existing roles.
    */
   @Named("role:list")
   @GET
   @Path("/roles")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listRoles();

   /**
    * Gets the details of the given role.
    * 
    * @param rolename The name of the role to get.
    * @return The details of the given role.
    */
   @Named("role:get")
   @GET
   @Path("/roles/{rolename}")
   @Fallback(NullOnNotFoundOr404.class)
   Role getRole(@PathParam("rolename") String roleName);

   /**
    * Creates a new role.
    * 
    * @param role The details for the new role.
    */
   @Named("role:create")
   @POST
   @Path("/roles")
   void createRole(@BinderParam(BindToJsonPayload.class) Role role);

   /**
    * Updates the given role.
    * 
    * @param role The new details for the role.
    * @return The details of the updated role.
    */
   @Named("role:update")
   @PUT
   @Path("/roles/{rolename}")
   Role updateRole(@PathParam("rolename") @ParamParser(RoleName.class) @BinderParam(BindToJsonPayload.class) Role role);

   /**
    * Deletes the given role.
    * 
    * @param rolename The name of the role to delete.
    * @return The details of the deleted role.
    */
   @Named("role:delete")
   @DELETE
   @Path("/roles/{rolename}")
   @Fallback(NullOnNotFoundOr404.class)
   Role deleteRole(@PathParam("rolename") String roleName);

   // Sandboxes

   /**
    * Creates a new sandbox.
    * <p>
    * It accepts a list of checksums as input and returns the URLs against which
    * to PUT files that need to be uploaded.
    * 
    * @param md5s The raw md5 sums. Uses {@code Bytes.asList()} and
    *        {@code Bytes.toByteArray()} as necessary
    * @return The upload sandbox with the URLs against which to PUT files that
    *         need to be uploaded.
    */
   @Named("sandbox:upload")
   @POST
   @Path("/sandboxes")
   UploadSandbox createUploadSandboxForChecksums(@BinderParam(BindChecksumsToJsonPayload.class) Set<List<Byte>> md5s);

   /**
    * Uploads the given content to the sandbox at the given URI.
    * <p>
    * The URI must be obtained, after uploading a sandbox, from the
    * {@link UploadSandbox#getUri()}.
    * 
    * @param location The URI where the upload must be performed.
    * @param content The contents to upload.
    */
   @Named("content:upload")
   @PUT
   @Produces("application/x-binary")
   void uploadContent(@EndpointParam URI location, Payload content);

   /**
    * Gets the contents of the given resource.
    * 
    * @param resource The resource to get.
    * @return An input stream for the content of the requested resource.
    */
   @Named("content:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @SkipEncoding({ '+', ' ', '/', '=', ':', ';' })
   InputStream getResourceContents(@EndpointParam(parser = UriForResource.class) Resource resource);

   /**
    * Confirms if the sandbox is completed or not.
    * <p>
    * This method should be used after uploading contents to the sandbox.
    * 
    * @param id The id of the sandbox to commit.
    * @param isCompleted Flag to set if the sandbox is completed or not.
    * @return The details of the sandbox.
    */
   @Named("sandbox:commit")
   @PUT
   @Path("/sandboxes/{id}")
   Sandbox commitSandbox(@PathParam("id") String id, @WrapWith("is_completed") boolean isCompleted);

   // Search

   /**
    * Lists the names of the available search indexes.
    * <p>
    * By default, the "role", "node" and "api" indexes will always be available.
    * <p>
    * Note that the search indexes may lag behind the most current data by at
    * least 10 seconds at any given time - so if you need to write data and
    * immediately query it, you likely need to produce an artificial delay (or
    * simply retry until the data is available).
    * 
    * @return The names of the available search indexes.
    */
   @Named("search:indexes")
   @GET
   @Path("/search")
   @ResponseParser(ParseKeySetFromJson.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<String> listSearchIndexes();

   /**
    * Searches all clients.
    * <p>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched the
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:clients")
   @GET
   @Path("/search/client")
   @ResponseParser(ParseSearchClientsFromJson.class)
   SearchResult<? extends Client> searchClients();

   /**
    * Searches all clients that match the given options.
    * 
    * @return The response contains the total number of rows that matched the
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:clients")
   @GET
   @Path("/search/client")
   @ResponseParser(ParseSearchClientsFromJson.class)
   SearchResult<? extends Client> searchClients(SearchOptions options);

   /**
    * Searches all items in a data bag.
    * <p>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched the
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:databag")
   @GET
   @Path("/search/{databagName}")
   @ResponseParser(ParseSearchDatabagFromJson.class)
   SearchResult<? extends DatabagItem> searchDatabagItems(@PathParam("databagName") String databagName);

   /**
    * Searches all items in a data bag that match the given options.
    * 
    * @return The response contains the total number of rows that matched the
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:databag")
   @GET
   @Path("/search/{databagName}")
   @ResponseParser(ParseSearchDatabagFromJson.class)
   SearchResult<? extends DatabagItem> searchDatabagItems(@PathParam("databagName") String databagName,
         SearchOptions options);

   /**
    * Searches all environments.
    * <p>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched the
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
    * Searches all environments that match the given options.
    * 
    * @return The response contains the total number of rows that matched the
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
    * Searches all nodes.
    * <p>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched the
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:nodes")
   @GET
   @Path("/search/node")
   @ResponseParser(ParseSearchNodesFromJson.class)
   SearchResult<? extends Node> searchNodes();

   /**
    * Searches all nodes that match the given options.
    * 
    * @return The response contains the total number of rows that matched the
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:nodes")
   @GET
   @Path("/search/node")
   @ResponseParser(ParseSearchNodesFromJson.class)
   SearchResult<? extends Node> searchNodes(SearchOptions options);

   /**
    * Searches all roles.
    * <p>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched the
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:roles")
   @GET
   @Path("/search/role")
   @ResponseParser(ParseSearchRolesFromJson.class)
   SearchResult<? extends Role> searchRoles();

   /**
    * Searches all roles that match the given options.
    * 
    * @return The response contains the total number of rows that matched the
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   @Named("search:roles")
   @GET
   @Path("/search/role")
   @ResponseParser(ParseSearchRolesFromJson.class)
   SearchResult<? extends Role> searchRoles(SearchOptions options);

}
