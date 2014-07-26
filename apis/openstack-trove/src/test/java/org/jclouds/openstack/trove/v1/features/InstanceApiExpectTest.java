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
import java.net.URI;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.trove.v1.domain.Instance;
import org.jclouds.openstack.trove.v1.internal.BaseTroveApiExpectTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * Tests InstanceApi Guice wiring and parsing
 */
@Test(groups = "unit", testName = "InstanceApiExpectTest")
public class InstanceApiExpectTest extends BaseTroveApiExpectTest {
    
    public void testCreateInstance() {
        URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances");
        InstanceApi api = requestsSendResponses(
                keystoneAuthWithUsernameAndPasswordAndTenantName,
                responseWithKeystoneAccess,
                authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
                .method("POST")
                .payload(payloadFromResourceWithContentType("/instance_create_request.json", MediaType.APPLICATION_JSON))
                .build(),
                HttpResponse.builder().statusCode(200).payload(payloadFromResource("/instance_create.json")).build() // response
                ).getInstanceApi("RegionOne");

        Instance instance = api.create("1", 2, "json_rack_instance");
        assertEquals(instance.getSize(), 2);
        assertEquals(instance.getName(), "json_rack_instance");  
    }
    
    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testCreateInstanceFail() {
        URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances");
        InstanceApi api = requestsSendResponses(
                keystoneAuthWithUsernameAndPasswordAndTenantName,
                responseWithKeystoneAccess,
                authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
                .method("POST")
                .payload(payloadFromResourceWithContentType("/instance_create_request.json", MediaType.APPLICATION_JSON))
                .build(),
                HttpResponse.builder().statusCode(404).payload(payloadFromResource("/instance_create.json")).build() // response
                ).getInstanceApi("RegionOne");

        api.create("1", 2, "json_rack_instance");
    }
    
    public void testDeleteInstance() {
        URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/098653ba-218b-47ce-936a-e0b749101f81");
        InstanceApi api = requestsSendResponses(
                keystoneAuthWithUsernameAndPasswordAndTenantName,
                responseWithKeystoneAccess,
                authenticatedGET().endpoint(endpoint).method("DELETE").build(),
                HttpResponse.builder().statusCode(202).build() // response
                ).getInstanceApi("RegionOne");

        assertTrue( api.delete("098653ba-218b-47ce-936a-e0b749101f81") );
    }
    
    public void testDeleteInstanceFail() {
        URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/098653ba-218b-47ce-936a-e0b749101f81");
        InstanceApi api = requestsSendResponses(
                keystoneAuthWithUsernameAndPasswordAndTenantName,
                responseWithKeystoneAccess,
                authenticatedGET().endpoint(endpoint).method("DELETE").build(),
                HttpResponse.builder().statusCode(404).build() // response
                ).getInstanceApi("RegionOne");

        assertTrue( !api.delete("098653ba-218b-47ce-936a-e0b749101f81") );
    }

   
   public void testListInstances() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances");
      InstanceApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/instance_list.json")).build()
      ).getInstanceApi("RegionOne");

      Set<? extends Instance> instances = api.list().toSet();
      assertEquals(instances.size(), 2);
      assertEquals(instances.iterator().next().getSize(), 2);
   }

   public void testListInstancesFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances");
      InstanceApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getInstanceApi("RegionOne");

      Set<? extends Instance> instances = api.list().toSet();
      assertTrue(instances.isEmpty());
   }   

   public void testGetInstance() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/44b277eb-39be-4921-be31-3d61b43651d7");
      InstanceApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/instance_get.json")).build()
      ).getInstanceApi("RegionOne");

      Instance instance = api.get("44b277eb-39be-4921-be31-3d61b43651d7");
      assertEquals(instance.getName(), "json_rack_instance");
      assertEquals(instance.getId(), "44b277eb-39be-4921-be31-3d61b43651d7");
      assertEquals(instance.getLinks().size(), 2);
      assertEquals(instance.getHostname(), "e09ad9a3f73309469cf1f43d11e79549caf9acf2.rackspaceclouddb.com");
   }
   
   public void testGetInstanceFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/12312");
      InstanceApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getInstanceApi("RegionOne");

      assertNull(api.get("12312"));
   }  
   
   public void testEnableRootOnInstance() {
       URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/44b277eb-39be-4921-be31-3d61b43651d7/root");
       InstanceApi api = requestsSendResponses(
             keystoneAuthWithUsernameAndPasswordAndTenantName,
             responseWithKeystoneAccess,
             authenticatedGET().method("POST").endpoint(endpoint).build(),
             HttpResponse.builder().statusCode(200).payload(payloadFromResource("/instance_root.json")).build()
       ).getInstanceApi("RegionOne");

       String password = api.enableRoot("44b277eb-39be-4921-be31-3d61b43651d7");
       assertEquals(password, "12345");
    }
   
   public void testEnableRootOnInstanceFail() {
       URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/44b277eb-39be-4921-be31-3d61b43651d7/root");
       InstanceApi api = requestsSendResponses(
             keystoneAuthWithUsernameAndPasswordAndTenantName,
             responseWithKeystoneAccess,
             authenticatedGET().method("POST").endpoint(endpoint).build(),
             HttpResponse.builder().statusCode(404).payload(payloadFromResource("/instance_root.json")).build()
       ).getInstanceApi("RegionOne");

       String password = api.enableRoot("44b277eb-39be-4921-be31-3d61b43651d7");
       assertEquals(password, null);
    }
   
   public void testIsRootInstance() {
       URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/44b277eb-39be-4921-be31-3d61b43651d7/root");
       InstanceApi api = requestsSendResponses(
             keystoneAuthWithUsernameAndPasswordAndTenantName,
             responseWithKeystoneAccess,
             authenticatedGET().endpoint(endpoint).build(),
             HttpResponse.builder().statusCode(200).payload(payloadFromResource("/instance_is_rooted.json")).build()
       ).getInstanceApi("RegionOne");

       boolean rooted = api.isRooted("44b277eb-39be-4921-be31-3d61b43651d7");
       assertEquals(rooted, true);
    }
   
   public void testIsRootInstanceFalse() {
       URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/44b277eb-39be-4921-be31-3d61b43651d7/root");
       InstanceApi api = requestsSendResponses(
             keystoneAuthWithUsernameAndPasswordAndTenantName,
             responseWithKeystoneAccess,
             authenticatedGET().endpoint(endpoint).build(),
             HttpResponse.builder().statusCode(200).payload(payloadFromResource("/instance_is_rooted_false.json")).build()
       ).getInstanceApi("RegionOne");

       Boolean rooted = api.isRooted("44b277eb-39be-4921-be31-3d61b43651d7");
       assertEquals(rooted.booleanValue(), false);
    }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)  
   public void testIsRootInstanceFail() {
       URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/44b277eb-39be-4921-be31-3d61b43651d7/root");
       InstanceApi api = requestsSendResponses(
             keystoneAuthWithUsernameAndPasswordAndTenantName,
             responseWithKeystoneAccess,
             authenticatedGET().endpoint(endpoint).build(),
             HttpResponse.builder().statusCode(404).payload(payloadFromResource("/instance_is_rooted.json")).build()
       ).getInstanceApi("RegionOne");

       Boolean rooted = api.isRooted("44b277eb-39be-4921-be31-3d61b43651d7");
       assertNull(rooted);
    }
}
