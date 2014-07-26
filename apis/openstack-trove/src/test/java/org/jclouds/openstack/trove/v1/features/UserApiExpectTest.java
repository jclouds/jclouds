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
package org.jclouds.openstack.trove.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.trove.v1.domain.User;
import org.jclouds.openstack.trove.v1.internal.BaseTroveApiExpectTest;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import org.testng.internal.annotations.Sets;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableSortedSet.Builder;

/**
 * Tests UserApi Guice wiring and parsing
 */
@Test(groups = "unit", testName = "UserApiExpectTest")
public class UserApiExpectTest extends BaseTroveApiExpectTest {

   public void testCreateUserSimple() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.create("dbuser1", "password", "databaseA");
      assertTrue(result);
   }

   public void testCreateUserSimpleFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.create("dbuser1", "password", "databaseA");
      assertFalse(result);
   }
   
   public void testCreateUserSimpleWithHost() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_with_host_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.create("dbuser1", "password", "192.168.64.64", "databaseA");
      assertTrue(result);
   }
   
   public void testCreateUserSimpleWithHostFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_with_host_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.create("dbuser1", "password", "192.168.64.64", "databaseA");
      assertFalse(result);
   }

   public void testCreateUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      Set<String> databases1 = Sets.newHashSet();
      databases1.add( "databaseA" );      
      Builder<String> databases2builder = ImmutableSortedSet.<String>naturalOrder();
      databases2builder.add( "databaseB" );
      databases2builder.add( "databaseC" );
      Set<String> databases2 = databases2builder.build();
      Set<String> databases3 = Sets.newHashSet();
      databases3.add( "databaseD" );
      User user1 = User.builder().databases( databases1 ).name("dbuser1").password("password").build();
      User user2 = User.builder().databases( databases2 ).name("dbuser2").password("password").build();
      User user3 = User.builder().databases( databases3 ).name("dbuser3").password("password").host("192.168.64.64").build();
      Set<User> users = Sets.newHashSet();
      users.add(user1);
      users.add(user2);
      users.add(user3);
      
      boolean result = api.create(ImmutableSortedSet.<User>naturalOrder().addAll(users).build());
      assertTrue(result);
   }

   public void testCreateUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      Set<String> databases1 = Sets.newHashSet();
      databases1.add( "databaseA" );
      Builder<String> databases2builder = ImmutableSortedSet.<String>naturalOrder();
      databases2builder.add( "databaseB" );
      databases2builder.add( "databaseC" );
      Set<String> databases2 = databases2builder.build();
      Set<String> databases3 = Sets.newHashSet();
      databases3.add( "databaseD" );
      User user1 = User.builder().databases( databases1 ).name("dbuser1").password("password").build();
      User user2 = User.builder().databases( databases2 ).name("dbuser2").password("password").build();
      User user3 = User.builder().databases( databases3 ).name("dbuser3").password("password").host("192.168.64.64").build();
      Set<User> users = Sets.newHashSet();
      users.add(user1);
      users.add(user2);
      users.add(user3);
      
      boolean result = api.create( ImmutableSortedSet.<User>naturalOrder().addAll(users).build());
      assertFalse(result);
   }

   public void testGrantUserSimple() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("PUT")
            .payload(payloadFromResourceWithContentType("/user_grant_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.grant("dbuser1", "databaseZ");
      assertTrue(result);
   }

   public void testGrantUserSimpleFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("PUT")
            .payload(payloadFromResourceWithContentType("/user_grant_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.grant("dbuser1", "databaseZ");
      assertFalse(result);
   }

   public void testGrantUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("PUT")
            .payload(payloadFromResourceWithContentType("/user_grant_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      List<String> databases = Lists.newArrayList();
      databases.add( "databaseC" );
      databases.add( "databaseD" );
      
      boolean result = api.grant("dbuser1", databases);
      assertTrue(result);
   }

   public void testGrantUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("PUT")
            .payload(payloadFromResourceWithContentType("/user_grant_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      List<String> databases = Lists.newArrayList();
      databases.add( "databaseC" );
      databases.add( "databaseD" );
      
      boolean result = api.grant("dbuser1", databases);
      assertFalse(result);
   }
   
   public void testRevokeUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases/databaseA");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      Set<String> databases = Sets.newHashSet();
      databases.add( "database" );
      databases.add( "database" );
      boolean result = api.revoke("dbuser1", "databaseA");
      assertTrue(result);
   }
   
   public void testRevokeUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases/databaseA");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      Set<String> databases = Sets.newHashSet();
      databases.add( "database" );
      databases.add( "database" );
      boolean result = api.revoke("dbuser1", "databaseA");
      assertFalse(result);
   }
   
   public void testDeleteUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      Set<String> databases = Sets.newHashSet();
      databases.add( "database" );
      databases.add( "database" );
      boolean result = api.delete("dbuser1");
      assertTrue(result);
   }
   
   public void testDeleteUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApi("RegionOne", "instanceId-1234-5678");

      Set<String> databases = Sets.newHashSet();
      databases.add( "database" );
      databases.add( "database" );
      boolean result = api.delete("dbuser1");
      assertFalse(result);
   }
   
   public void testListUsers() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/trove_user_list.json")).build()
      ).getUserApi("RegionOne", "instanceId-1234-5678");

      Set<User> users = api.list().toSet();
      assertEquals(users.size(), 4);
      assertTrue(users.iterator().next().getDatabases().isEmpty());
      assertEquals(users.iterator().next().getName(), "dbuser1");
   }
   
   public void testListUsersFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/trove_user_list.json")).build()
      ).getUserApi("RegionOne", "instanceId-1234-5678");

      Set<User> users = api.list().toSet();
      assertTrue(users.isEmpty());
   }
   
   public void testUserGetDatabaseList() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/user_list_access.json")).build()
      ).getUserApi("RegionOne", "instanceId-1234-5678");

      List<String> databases = api.getDatabaseList("dbuser1").toList();
      assertEquals(databases.size(), 2);
      assertEquals(databases.iterator().next(), "databaseA");
   }
   
   public void testUserGetDatabaseListFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/user_list_access.json")).build()
      ).getUserApi("RegionOne", "instanceId-1234-5678");

      Set<String> databases = api.getDatabaseList("dbuser1").toSet();
      assertTrue(databases.isEmpty());
   }
   
   public void testGetUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/exampleuser");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/user_get.json")).build()
      ).getUserApi("RegionOne", "instanceId-1234-5678");

      User user = api.get("exampleuser");
      assertEquals(user.getName(), "exampleuser");
      assertEquals(user.getHost(), "%");
      assertEquals(user.getDatabases().size(), 2);
      assertEquals(user.getDatabases().iterator().next(), "databaseA");
   }
   
   public void testGetUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/exampleuser");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/user_get.json")).build()
      ).getUserApi("RegionOne", "instanceId-1234-5678");

      User user = api.get("exampleuser");
      assertNull(user);
   }
   
   public void testGetUserWithHostname() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/example%2euser%40192%2e168%2e64%2e64");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/user_get_withhost.json")).build()
      ).getUserApi("RegionOne", "instanceId-1234-5678");

      User user = api.get("example.user", "192.168.64.64");
      assertEquals(user.getName(), "example.user");
      assertEquals(user.getHost(), "192.168.64.64");
      assertEquals(user.getIdentifier(), "example.user@192.168.64.64");
      assertEquals(user.getDatabases().size(), 2);
      assertEquals(user.getDatabases().iterator().next(), "databaseA");
   }
   
   public void testGetUserWithHostnameFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/example%2euser%40192%2e168%2e64%2e64");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/user_get_withhost.json")).build()
      ).getUserApi("RegionOne", "instanceId-1234-5678");

      User user = api.get("example.user", "192.168.64.64");
      assertNull(user);
   }
}
