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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.trove.v1.internal.BaseTroveApiExpectTest;
import org.testng.annotations.Test;

/**
 * Tests DatabaseApi Guice wiring and parsing
 */
@Test(groups = "unit", testName = "DatabaseApiExpectTest")
public class DatabaseApiExpectTest extends BaseTroveApiExpectTest {

   public void testCreateDatabaseSimple() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/databases");
      DatabaseApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
            .method("POST")
            .payload(payloadFromResourceWithContentType("/database_create_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getDatabaseApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.create("testingdb");
      assertTrue(result);
   }

   public void testCreateDatabaseSimpleFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/databases");
      DatabaseApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
            .method("POST")
            .payload(payloadFromResourceWithContentType("/database_create_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getDatabaseApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.create("testingdb");
      assertFalse(result);
   }

   public void testCreateDatabase() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/databases");
      DatabaseApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
            .method("POST")
            .payload(payloadFromResourceWithContentType("/database_create_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getDatabaseApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.create("testingdb", "utf8", "utf8_general_ci");
      assertTrue(result);
   }

   public void testCreateDatabaseFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/databases");
      DatabaseApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
            .method("POST")
            .payload(payloadFromResourceWithContentType("/database_create_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getDatabaseApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.create("testingdb", "utf8", "utf8_general_ci");
      assertFalse(result);
   }
   
   public void testDeleteDatabase() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/databases/db1");
      DatabaseApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) 
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getDatabaseApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.delete("db1");
      assertTrue(result);
   }
   
   public void testDeleteDatabaseFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/databases/db1");
      DatabaseApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) 
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getDatabaseApi("RegionOne", "instanceId-1234-5678");

      boolean result = api.delete("db1");
      assertFalse(result);
   }
   
   public void testListDatabases() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/databases");
      DatabaseApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/database_list.json")).build()
      ).getDatabaseApi("RegionOne", "instanceId-1234-5678");

      List<String> databases = api.list().toList();
      assertEquals(databases.size(), 5);
      assertEquals(databases.iterator().next(), "anotherdb");
   }
   
   public void testListDatabasesFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/databases");
      DatabaseApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/database_list.json")).build()
      ).getDatabaseApi("RegionOne", "instanceId-1234-5678");

      Set<String> databases = api.list().toSet();
      assertTrue(databases.isEmpty());
   }
}
