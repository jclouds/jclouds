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
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ConfiguresRestClient;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * Expect tests for the {@link ChefApi} class.
 * 
 * @author Noorul Islam K M
 */
@Test(groups = "unit", testName = "ChefApiExpectTest")
public class ChefApiExpectTest extends BaseChefApiExpectTest<ChefApi> {
   public ChefApiExpectTest() {
     provider = "chef";
   }

   public void testListClientsReturns2xx() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/clients") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/clients_list.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> nodes = api.listClients();
      assertEquals(nodes.size(), 3);
      assertTrue(nodes.contains("adam"), String.format("Expected nodes to contain 'adam' but was: %s", nodes));
   }

   public void testListClientsReturns404() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/clients") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(404)
                  .build());
      Set<String> clients = api.listClients();
      assertTrue(clients.isEmpty(), String.format("Expected clients to be empty but was: %s", clients));
   }

   public void testListNodesReturns2xx() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/nodes") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/nodes_list.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> nodes = api.listNodes();
      assertEquals(nodes.size(), 3);
      assertTrue(nodes.contains("blah"), String.format("Expected nodes to contain 'blah' but was: %s", nodes));
   }

   public void testListNodesReturns404() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/nodes") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(404)
                  .build());
      Set<String> nodes = api.listNodes();
      assertTrue(nodes.isEmpty(), String.format("Expected nodes to be empty but was: %s", nodes));
   }
   
   public void testListRecipesInEnvironmentReturns2xx() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/environments/dev/recipes") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/environment_recipes.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> recipes = api.listRecipesInEnvironment("dev");
      assertEquals(recipes.size(), 3);
      assertTrue(recipes.contains("apache2"), String.format("Expected recipes to contain 'apache2' but was: %s", recipes));
   }

   public void testListRecipesInEnvironmentReturns404() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/environments/dev/recipes") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(404)
                  .build());
      Set<String> recipes = api.listRecipesInEnvironment("dev");
      assertTrue(recipes.isEmpty(), String.format("Expected recipes to be empty but was: %s", recipes));
   }

   public void testListNodesInEnvironmentReturns2xx() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/environments/dev/nodes") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/nodes_list.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> nodes = api.listNodesInEnvironment("dev");
      assertEquals(nodes.size(), 3);
      assertTrue(nodes.contains("blah"), String.format("Expected nodes to contain 'blah' but was: %s", nodes));
   }

   public void testListNodesInEnvironmentReturns404() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/environments/dev/nodes") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(404)
                  .build());
      Set<String> nodes = api.listNodesInEnvironment("dev");
      assertTrue(nodes.isEmpty(), String.format("Expected nodes to be empty but was: %s", nodes));
   }

   public void testListCookbooksInEnvironmentReturnsValidSet() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/environments/dev/cookbooks") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/env_cookbooks.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<CookbookDefinition> cookbooks = api.listCookbooksInEnvironment("dev");
      assertEquals(cookbooks.size(), 2);
   }

   @Override
   protected Module createModule() {
      return new TestChefRestClientModule();
   }

   @ConfiguresRestClient
   static class TestChefRestClientModule extends ChefHttpApiModule {
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
