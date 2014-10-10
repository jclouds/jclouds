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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.chef.config.ChefHttpApiModule;
import org.jclouds.chef.domain.CookbookDefinition;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.chef.options.SearchOptions;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ConfiguresHttpApi;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * Expect tests for the {@link ChefApi} class.
 */
@Test(groups = "unit", testName = "ChefApiExpectTest")
public class ChefApiExpectTest extends BaseChefApiExpectTest<ChefApi> {
   public ChefApiExpectTest() {
     provider = "chef";
   }

   private HttpRequest.Builder<?> getHttpRequestBuilder(String method, String endPoint) {
      return HttpRequest.builder() //
                  .method(method) //
                  .endpoint("http://localhost:4000" + endPoint) //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON);
   }

   public void testListClientsReturnsValidSet() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/clients").build()),
            HttpResponse.builder().statusCode(200) //
                        .payload(payloadFromResourceWithContentType("/clients_list.json", MediaType.APPLICATION_JSON)) //
                        .build());
      Set<String> nodes = api.listClients();
      assertEquals(nodes.size(), 3);
      assertTrue(nodes.contains("adam"), String.format("Expected nodes to contain 'adam' but was: %s", nodes));
   }

   public void testListClientsReturnsEmptySetOn404() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/clients").build()),
            HttpResponse.builder().statusCode(404)
                  .build());
      Set<String> clients = api.listClients();
      assertTrue(clients.isEmpty(), String.format("Expected clients to be empty but was: %s", clients));
   }

   public void testListNodesReturnsValidSet() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/nodes").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/nodes_list.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> nodes = api.listNodes();
      assertEquals(nodes.size(), 3);
      assertTrue(nodes.contains("blah"), String.format("Expected nodes to contain 'blah' but was: %s", nodes));
   }

   public void testListNodesReturnsEmptySetOn404() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/nodes").build()),
            HttpResponse.builder().statusCode(404).build());
      Set<String> nodes = api.listNodes();
      assertTrue(nodes.isEmpty(), String.format("Expected nodes to be empty but was: %s", nodes));
   }

   public void testListRecipesInEnvironmentReturnsValidSet() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/environments/dev/recipes").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/environment_recipes.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> recipes = api.listRecipesInEnvironment("dev");
      assertEquals(recipes.size(), 3);
      assertTrue(recipes.contains("apache2"), String.format("Expected recipes to contain 'apache2' but was: %s", recipes));
   }

   public void testListRecipesInEnvironmentReturnsEmptySetOn404() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/environments/dev/recipes").build()),
            HttpResponse.builder().statusCode(404).build());
      Set<String> recipes = api.listRecipesInEnvironment("dev");
      assertTrue(recipes.isEmpty(), String.format("Expected recipes to be empty but was: %s", recipes));
   }

   public void testListNodesInEnvironmentReturnsValidSet() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/environments/dev/nodes").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/nodes_list.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> nodes = api.listNodesInEnvironment("dev");
      assertEquals(nodes.size(), 3);
      assertTrue(nodes.contains("blah"), String.format("Expected nodes to contain 'blah' but was: %s", nodes));
   }

   public void testListNodesInEnvironmentReturnsEmptySetOn404() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/environments/dev/nodes").build()),
            HttpResponse.builder().statusCode(404).build());
      Set<String> nodes = api.listNodesInEnvironment("dev");
      assertTrue(nodes.isEmpty(), String.format("Expected nodes to be empty but was: %s", nodes));
   }

   public void testListCookbooksReturnsValidSet() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/cookbooks").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/env_cookbooks.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> cookbooks = api.listCookbooks();
      assertEquals(cookbooks.size(), 2);
      assertTrue(cookbooks.contains("apache2"), String.format("Expected cookbooks to contain 'apache2' but was: %s", cookbooks));
   }

   public void testListCookbooksReturnsEmptySetOn404() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/cookbooks").build()),
            HttpResponse.builder().statusCode(404).build());
      Set<String> cookbooks = api.listCookbooks();
      assertTrue(cookbooks.isEmpty(), String.format("Expected cookbooks to be empty but was: %s", cookbooks));
   }

   public void testListCookbooksInEnvironmentReturnsValidSet() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/environments/dev/cookbooks").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/env_cookbooks.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<CookbookDefinition> cookbooks = api.listCookbooksInEnvironment("dev");
      assertEquals(cookbooks.size(), 2);
   }

   public void testListCookbooksInEnvironmentReturnsEmptySetOn404() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/environments/dev/cookbooks").build()),
            HttpResponse.builder().statusCode(404).build());
      Set<CookbookDefinition> cookbooks = api.listCookbooksInEnvironment("dev");
      assertTrue(cookbooks.isEmpty(), String.format("Expected cookbooks to be empty but was: %s", cookbooks));
   }

   public void testListCookbooksInEnvironmentWithNumVersionReturnsEmptySetOn404() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/environments/dev/cookbooks").addQueryParam("num_versions", "2").build()),
            HttpResponse.builder().statusCode(404).build());
      Set<CookbookDefinition> cookbooks = api.listCookbooksInEnvironment("dev", "2");
      assertTrue(cookbooks.isEmpty(), String.format("Expected cookbooks to be empty but was: %s", cookbooks));
   }

   public void testSearchRolesReturnsValidResult() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/search/role").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/search_role.json", MediaType.APPLICATION_JSON)) //
                  .build());
      SearchResult<? extends Role> result = api.searchRoles();
      assertEquals(result.size(), 1);
      assertEquals(result.iterator().next().getName(), "webserver");
   }

   public void testSearchRolesReturnsEmptyResult() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/search/role").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/search_role_empty.json", MediaType.APPLICATION_JSON)) //
                  .build());
      SearchResult<? extends Role> result = api.searchRoles();
      assertTrue(result.isEmpty(), String.format("Expected search result to be empty but was: %s", result));
   }

   public void testSearchRolesWithOptionsReturnsValidResult() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/search/role").addQueryParam("q", "name:webserver").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/search_role.json", MediaType.APPLICATION_JSON)) //
                  .build());
      SearchOptions options = SearchOptions.Builder.query("name:webserver");
      SearchResult<? extends Role> result = api.searchRoles(options);
      assertEquals(result.size(), 1);
      assertEquals(result.iterator().next().getName(), "webserver");
   }

   public void testSearchRolesWithOptionsReturnsEmptyResult() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/search/role").addQueryParam("q", "name:dummy").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/search_role_empty.json", MediaType.APPLICATION_JSON)) //
                  .build());
      SearchOptions options = SearchOptions.Builder.query("name:dummy");
      SearchResult<? extends Role> result = api.searchRoles(options);
      assertTrue(result.isEmpty(), String.format("Expected search result to be empty but was: %s", result));
   }

   public void testListRolesReturnsValidSet() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/roles").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/roles_list.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> roles = api.listRoles();
      assertEquals(roles.size(), 2);
      assertTrue(roles.contains("webserver"), String.format("Expected roles to contain 'websever' but was: %s", roles));
   }

   public void testListRolesReturnsEmptySetOn404() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/roles").build()),
            HttpResponse.builder().statusCode(404).build());
      Set<String> roles = api.listRoles();
      assertTrue(roles.isEmpty(), String.format("Expected roles to be empty but was: %s", roles));
   }

   public void testListDatabagsReturnsValidSet() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/data").build()),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/data_list.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> databags = api.listDatabags();
      assertEquals(databags.size(), 2);
      assertTrue(databags.contains("applications"), String.format("Expected databags to contain 'applications' but was: %s", databags));
   }

   public void testListDatabagsReturnsEmptySetOn404() {
      ChefApi api = requestSendsResponse(
            signed(getHttpRequestBuilder("GET", "/data").build()),
            HttpResponse.builder().statusCode(404).build());
      Set<String> databags = api.listDatabags();
      assertTrue(databags.isEmpty(), String.format("Expected databags to be empty but was: %s", databags));
   }

   @Override
   protected Module createModule() {
      return new TestChefHttpApiModule();
   }

   @ConfiguresHttpApi
   static class TestChefHttpApiModule extends ChefHttpApiModule {
      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "timestamp";
      }
   }

   @Override
   protected ChefApiMetadata createApiMetadata() {
      return new ChefApiMetadata();
   }

}
