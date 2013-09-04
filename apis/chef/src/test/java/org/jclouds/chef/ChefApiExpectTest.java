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

import org.jclouds.chef.BaseChefApiExpectTest;
import org.jclouds.chef.ChefApi;
import org.jclouds.date.TimeStamp;
import org.jclouds.chef.config.ChefHttpApiModule;
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

   public void testListEnvironmentRecipesReturns2xx() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/environments/dev/recipes") //
                  .addHeader("X-Chef-Version", ChefApi.VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/environment_recipes.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> recipes = api.listEnvironmentRecipes("dev");
      assertEquals(recipes.size(), 3);
      assertTrue(recipes.contains("apache2"));
   }

   public void testListEnvironmentRecipesReturns404() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/environments/dev/recipes") //
                  .addHeader("X-Chef-Version", ChefApi.VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(404)
                  .build());

      assertTrue(api.listEnvironmentRecipes("dev").isEmpty());
   }

   public void testListEnvironmentNodesReturns2xx() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/environments/dev/nodes") //
                  .addHeader("X-Chef-Version", ChefApi.VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/environment_nodes.json", MediaType.APPLICATION_JSON)) //
                  .build());
      Set<String> nodes = api.listEnvironmentNodes("dev");
      assertEquals(nodes.size(), 3);
      assertTrue(nodes.contains("blah"));
   }

   public void testListEnvironmentNodesReturns404() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/environments/dev/nodes") //
                  .addHeader("X-Chef-Version", ChefApi.VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(404)
                  .build());

      assertTrue(api.listEnvironmentNodes("dev").isEmpty());
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
