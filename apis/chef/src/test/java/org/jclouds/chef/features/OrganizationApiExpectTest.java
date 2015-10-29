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
package org.jclouds.chef.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.chef.BaseChefApiExpectTest;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.config.ChefHttpApiModule;
import org.jclouds.chef.domain.Group;
import org.jclouds.chef.domain.User;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * Expect tests for the {@link OrganizationApi} class.
 */
@Test(groups = "unit", testName = "OrganizationApiExpectTest")
public class OrganizationApiExpectTest extends BaseChefApiExpectTest<ChefApi> {

   public void testGetUserReturns2xx() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/users/nacx") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/user.json", MediaType.APPLICATION_JSON)) //
                  .build());

      User user = api.organizationApi().get().getUser("nacx");
      assertEquals(user.getUsername(), "nacx");
      assertEquals(user.getDisplayName(), "Ignasi Barrera");
   }

   public void testGetUserReturns404() {
      ChefApi api = requestSendsResponse(signed(HttpRequest.builder() //
            .method("GET") //
            .endpoint("http://localhost:4000/users/foo") //
            .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
            .addHeader("Accept", MediaType.APPLICATION_JSON) //
            .build()), //
            HttpResponse.builder().statusCode(404).build());

      assertNull(api.organizationApi().get().getUser("foo"));
   }

   public void testListGroups() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/groups") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/groups.json", MediaType.APPLICATION_JSON)) //
                  .build());

      Set<String> groups = api.organizationApi().get().listGroups();
      assertEquals(groups.size(), 5);
      assertTrue(groups.contains("admins"));
   }

   public void testGetGroupReturns2xx() {
      ChefApi api = requestSendsResponse(
            signed(HttpRequest.builder() //
                  .method("GET") //
                  .endpoint("http://localhost:4000/groups/admins") //
                  .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
                  .addHeader("Accept", MediaType.APPLICATION_JSON).build()), //
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/group.json", MediaType.APPLICATION_JSON)) //
                  .build());

      Group group = api.organizationApi().get().getGroup("admins");
      assertEquals(group.getName(), "admins");
      assertEquals(group.getGroupname(), "admins");
   }

   public void testGetGroupReturns404() {
      ChefApi api = requestSendsResponse(signed(HttpRequest.builder() //
            .method("GET") //
            .endpoint("http://localhost:4000/groups/foo") //
            .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
            .addHeader("Accept", MediaType.APPLICATION_JSON) //
            .build()), //
            HttpResponse.builder().statusCode(404).build());

      assertNull(api.organizationApi().get().getGroup("foo"));
   }

   public void testCreateGroupReturns2xx() {
      ChefApi api = requestSendsResponse(signed(HttpRequest.builder() //
            .method("POST") //
            .endpoint("http://localhost:4000/groups") //
            .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
            .addHeader("Accept", MediaType.APPLICATION_JSON) //
            .payload(payloadFromStringWithContentType("{\"groupname\":\"foo\"}", MediaType.APPLICATION_JSON)) //
            .build()), //
            HttpResponse.builder().statusCode(201).build());

      api.organizationApi().get().createGroup("foo");
   }

   public void testDeleteGroupReturns2xx() {
      ChefApi api = requestSendsResponse(signed(HttpRequest.builder() //
            .method("DELETE") //
            .endpoint("http://localhost:4000/groups/foo") //
            .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
            .addHeader("Accept", MediaType.APPLICATION_JSON) //
            .build()), //
            HttpResponse.builder().statusCode(200).build());

      api.organizationApi().get().deleteGroup("foo");
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDeleteGroupFailsOn404() {
      ChefApi api = requestSendsResponse(signed(HttpRequest.builder() //
            .method("DELETE") //
            .endpoint("http://localhost:4000/groups/foo") //
            .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
            .addHeader("Accept", MediaType.APPLICATION_JSON) //
            .build()), //
            HttpResponse.builder().statusCode(404).build());

      api.organizationApi().get().deleteGroup("foo");
   }

   public void testUpdateGroupReturns2xx() {
      ChefApi api = requestSendsResponse(signed(HttpRequest.builder() //
            .method("PUT") //
            .endpoint("http://localhost:4000/groups/admins") //
            .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
            .addHeader("Accept", MediaType.APPLICATION_JSON) //
            .payload(payloadFromResourceWithContentType("/group-update.json", MediaType.APPLICATION_JSON)) //
            .build()), //
            HttpResponse.builder().statusCode(200).build());

      Group group = Group.builder("admins").client("abiquo").group("admins").user("nacx").build();
      api.organizationApi().get().updateGroup(group);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUpdateGroupFailsOn404() {
      ChefApi api = requestSendsResponse(signed(HttpRequest.builder() //
            .method("PUT") //
            .endpoint("http://localhost:4000/groups/admins") //
            .addHeader("X-Chef-Version", ChefApiMetadata.DEFAULT_API_VERSION) //
            .addHeader("Accept", MediaType.APPLICATION_JSON) //
            .payload(payloadFromResourceWithContentType("/group-update.json", MediaType.APPLICATION_JSON)) //
            .build()), //
            HttpResponse.builder().statusCode(404).build());

      Group group = Group.builder("admins").client("abiquo").group("admins").user("nacx").build();
      api.organizationApi().get().updateGroup(group);
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
   protected ApiMetadata createApiMetadata() {
      return new ChefApiMetadata();
   }

}
