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
package org.jclouds.googlecomputeengine.compute;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.GCE_BOOT_DISK_SUFFIX;
import static org.jclouds.googlecomputeengine.features.GlobalOperationApiExpectTest.GET_GLOBAL_OPERATION_REQUEST;
import static org.jclouds.googlecomputeengine.features.GlobalOperationApiExpectTest.GET_GLOBAL_OPERATION_RESPONSE;
import static org.jclouds.googlecomputeengine.features.ImageApiExpectTest.LIST_DEBIAN_IMAGES_REQUEST;
import static org.jclouds.googlecomputeengine.features.ImageApiExpectTest.LIST_DEBIAN_IMAGES_RESPONSE;
import static org.jclouds.googlecomputeengine.features.ImageApiExpectTest.LIST_CENTOS_IMAGES_REQUEST;
import static org.jclouds.googlecomputeengine.features.ImageApiExpectTest.LIST_CENTOS_IMAGES_RESPONSE;
import static org.jclouds.googlecomputeengine.features.ImageApiExpectTest.LIST_PROJECT_IMAGES_REQUEST;
import static org.jclouds.googlecomputeengine.features.ImageApiExpectTest.LIST_PROJECT_IMAGES_RESPONSE;
import static org.jclouds.googlecomputeengine.features.InstanceApiExpectTest.LIST_CENTRAL1B_INSTANCES_REQUEST;
import static org.jclouds.googlecomputeengine.features.InstanceApiExpectTest.LIST_CENTRAL1B_INSTANCES_RESPONSE;
import static org.jclouds.googlecomputeengine.features.InstanceApiExpectTest.LIST_INSTANCES_REQUEST;
import static org.jclouds.googlecomputeengine.features.InstanceApiExpectTest.LIST_INSTANCES_RESPONSE;
import static org.jclouds.googlecomputeengine.features.MachineTypeApiExpectTest.LIST_CENTRAL1B_MACHINE_TYPES_REQUEST;
import static org.jclouds.googlecomputeengine.features.MachineTypeApiExpectTest.LIST_CENTRAL1B_MACHINE_TYPES_RESPONSE;
import static org.jclouds.googlecomputeengine.features.MachineTypeApiExpectTest.LIST_MACHINE_TYPES_REQUEST;
import static org.jclouds.googlecomputeengine.features.MachineTypeApiExpectTest.LIST_MACHINE_TYPES_RESPONSE;
import static org.jclouds.googlecomputeengine.features.NetworkApiExpectTest.GET_NETWORK_REQUEST;
import static org.jclouds.googlecomputeengine.features.ProjectApiExpectTest.GET_PROJECT_REQUEST;
import static org.jclouds.googlecomputeengine.features.ProjectApiExpectTest.GET_PROJECT_RESPONSE;
import static org.jclouds.googlecomputeengine.features.ZoneApiExpectTest.LIST_ZONES_REQ;
import static org.jclouds.googlecomputeengine.features.ZoneApiExpectTest.LIST_ZONES_RESPONSE;
import static org.jclouds.googlecomputeengine.features.ZoneApiExpectTest.LIST_ZONES_SHORT_RESPONSE;
import static org.jclouds.googlecomputeengine.features.ZoneOperationApiExpectTest.GET_ZONE_OPERATION_REQUEST;
import static org.jclouds.googlecomputeengine.features.ZoneOperationApiExpectTest.GET_ZONE_OPERATION_RESPONSE;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.features.InstanceApiExpectTest;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineServiceExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;


@Test(groups = "unit")
public class GoogleComputeEngineServiceExpectTest extends BaseGoogleComputeEngineServiceExpectTest {


   private HttpRequest INSERT_NETWORK_REQUEST = HttpRequest
           .builder()
           .method("POST")
           .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/global/networks")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN)
           .payload(payloadFromStringWithContentType("{\"name\":\"jclouds-test\",\"IPv4Range\":\"10.0.0.0/8\"}",
                   MediaType.APPLICATION_JSON))
           .build();

   private HttpRequest INSERT_FIREWALL_REQUEST = HttpRequest
           .builder()
           .method("POST")
           .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/global/firewalls")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN)
           .payload(payloadFromStringWithContentType("{\"name\":\"jclouds-test\",\"network\":\"https://www.googleapis" +
                   ".com/compute/v1/projects/myproject/global/networks/jclouds-test\"," +
                   "\"sourceRanges\":[\"10.0.0.0/8\",\"0.0.0.0/0\"],\"sourceTags\":[\"aTag\"],\"allowed\":[{\"IPProtocol\":\"tcp\"," +
                   "\"ports\":[\"22\"]}," +
                   "{\"IPProtocol\":\"udp\",\"ports\":[\"22\"]}]}",
                   MediaType.APPLICATION_JSON))
           .build();

   private HttpResponse GET_NETWORK_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(payloadFromStringWithContentType("{\n" +
                   " \"kind\": \"compute#network\",\n" +
                   " \"id\": \"13024414170909937976\",\n" +
                   " \"creationTimestamp\": \"2012-10-24T20:13:19.967\",\n" +
                   " \"selfLink\": \"https://www.googleapis" +
                   ".com/compute/v1/projects/myproject/global/networks/jclouds-test\",\n" +
                   " \"name\": \"jclouds-test\",\n" +
                   " \"description\": \"test network\",\n" +
                   " \"IPv4Range\": \"10.0.0.0/8\",\n" +
                   " \"gatewayIPv4\": \"10.0.0.1\"\n" +
                   "}", MediaType.APPLICATION_JSON)).build();

   private HttpResponse SUCESSFULL_OPERATION_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(payloadFromResource("/operation.json")).build();

   private HttpRequest SET_TAGS_REQUEST = HttpRequest.builder()
           .method("POST")
           .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/setTags")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN)
           .payload(payloadFromStringWithContentType("{\"items\":[\"aTag\"],\"fingerprint\":\"abcd\"}",
                   MediaType.APPLICATION_JSON))
           .build();

   private HttpResponse SET_TAGS_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(payloadFromResource("/operation.json")).build();

   private HttpResponse getInstanceResponseForInstanceAndNetworkAndStatus(String instanceName, String networkName,
                                                                          String status) throws
           IOException {
      return HttpResponse.builder().statusCode(200)
              .payload(payloadFromStringWithContentType(
                      replaceInstanceNameNetworkAndStatusOnResource("/instance_get.json",
                              instanceName, networkName, status),
                      "application/json")).build();
   }

   private HttpResponse getListInstancesResponseForSingleInstanceAndNetworkAndStatus(String instanceName,
                                                                                     String networkName,
                                                                                     String status) {
      return HttpResponse.builder().statusCode(200)
              .payload(payloadFromStringWithContentType(
                      replaceInstanceNameNetworkAndStatusOnResource("/instance_list.json",
                              instanceName, networkName, status),
                      "application/json")).build();
   }

   private HttpResponse getDiskResponseForInstance(String instanceName) {
      return HttpResponse.builder().statusCode(200)
                         .payload(payloadFromStringWithContentType(
                         replaceDiskNameOnResource("/disk_get.json", instanceName + "-" + GCE_BOOT_DISK_SUFFIX),
                         "application/json")).build();
   }

   private String replaceDiskNameOnResource(String resourceName, String diskName) {
      try {
         return Strings2.toStringAndClose(this.getClass().getResourceAsStream(resourceName))
                        .replace("testimage1", diskName);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   private String replaceInstanceNameNetworkAndStatusOnResource(String resourceName, String instanceName,
                                                                String networkName, String status) {
      try {
         return Strings2.toStringAndClose(this.getClass().getResourceAsStream(resourceName)).replace("test-0",
                                                                                                     instanceName).replace("default", networkName).replace("RUNNING", status);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   private HttpRequest createDiskRequestForInstance(String instanceName) {
      return HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks"
                                + "?sourceImage=https%3A//www.googleapis.com/compute/v1/projects/centos-cloud/global/images/gcel-12-04-v20121106")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromStringWithContentType("{\"name\":\"" + instanceName + "-" + GCE_BOOT_DISK_SUFFIX + "\","
                                                        + "\"sizeGb\":10}",
                                                        MediaType.APPLICATION_JSON)).build();
   }

   private HttpRequest getDiskRequestForInstance(String instanceName) {
      return HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                        ".com/compute/v1/projects/myproject/zones/us-central1-a/disks/"
                        + instanceName + "-" + GCE_BOOT_DISK_SUFFIX)
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();
   }



   private HttpRequest createInstanceRequestForInstance(String instanceName, String groupName,
                                                        String networkName, String publicKey) {
      return HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/instances")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromStringWithContentType("{\"name\":\"" + instanceName + "\"," +
                                                        "\"machineType\":\"https://www.googleapis" +
                                                        ".com/compute/v1/projects/myproject/zones/us-central1-a/machineTypes/n1-standard-1\"," +
                                                        "\"serviceAccounts\":[]," +
                                                        "\"networkInterfaces\":[{\"network\":\"https://www.googleapis" +
                                                        ".com/compute/v1/projects/myproject/global/networks/" + networkName + "\"," +
                                                        "\"accessConfigs\":[{\"type\":\"ONE_TO_ONE_NAT\"}]}]," +
                                                        "\"disks\":[{\"mode\":\"READ_WRITE\",\"source\":\"https://www.googleapis.com/" +
                                                        "compute/v1/projects/myproject/zones/us-central1-a/disks/" + instanceName +
                                                        "-" + GCE_BOOT_DISK_SUFFIX + "\",\"deleteOnTerminate\":true,\"boot\":true,\"type\":\"PERSISTENT\"}]," +
                                                        "\"metadata\":{\"kind\":\"compute#metadata\",\"items\":[{\"key\":\"sshKeys\"," +
                                                        "\"value\":\"jclouds:" +
                                                        publicKey + " jclouds@localhost\"},{\"key\":\"jclouds-group\"," +
                                                        "\"value\":\"" + groupName + "\"},{\"key\":\"jclouds-image\",\"value\":\"https://www.googleapis" +
                                                        ".com/compute/v1/projects/centos-cloud/global/images/gcel-12-04-v20121106\"}," +
                                                        "{\"key\":\"jclouds-delete-boot-disk\",\"value\":\"true\"}]}}",
                                                        MediaType.APPLICATION_JSON)).build();
   }

   private HttpRequest getInstanceRequestForInstance(String instanceName) {
      return HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/" + instanceName)
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();
   }


   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.put("google-compute-engine.identity", "myproject");
      try {
         overrides.put("google-compute-engine.credential", toStringAndClose(getClass().getResourceAsStream("/testpk.pem")));
      } catch (IOException e) {
         Throwables.propagate(e);
      }
      return overrides;
   }

   @Test(enabled = false)
   public void testThrowsAuthorizationException() throws Exception {

      Properties properties = new Properties();
      properties.setProperty("oauth.identity", "MOMMA");
      properties.setProperty("oauth.credential", "MiA");

      ComputeService client = requestsSendResponses(ImmutableMap.<HttpRequest, HttpResponse>of(), createModule(),
              properties);
      Template template = client.templateBuilder().build();
      Template toMatch = client.templateBuilder().imageId(template.getImage().getId()).build();
      assertEquals(toMatch.getImage(), template.getImage());
   }

   @Test
   public void testTemplateMatch() throws Exception {
      ImmutableMap<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.
              <HttpRequest, HttpResponse>builder()
              .put(requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE)
              .put(GET_PROJECT_REQUEST, GET_PROJECT_RESPONSE)
              .put(LIST_ZONES_REQ, LIST_ZONES_RESPONSE)
              .put(LIST_PROJECT_IMAGES_REQUEST, LIST_PROJECT_IMAGES_RESPONSE)
              .put(LIST_DEBIAN_IMAGES_REQUEST, LIST_DEBIAN_IMAGES_RESPONSE)
              .put(LIST_CENTOS_IMAGES_REQUEST, LIST_CENTOS_IMAGES_RESPONSE)
              .put(LIST_MACHINE_TYPES_REQUEST, LIST_MACHINE_TYPES_RESPONSE)
              .put(LIST_CENTRAL1B_MACHINE_TYPES_REQUEST, LIST_CENTRAL1B_MACHINE_TYPES_RESPONSE)
              .build();

      ComputeService client = requestsSendResponses(requestResponseMap);
      Template template = client.templateBuilder().build();
      Hardware defaultSize = client.templateBuilder().build().getHardware();

      Hardware smallest = client.templateBuilder().smallest().build().getHardware();
      assertEquals(defaultSize, smallest);

      Hardware fastest = client.templateBuilder().fastest().build().getHardware();
      assertNotNull(fastest);

      assertEquals(client.listHardwareProfiles().size(), 5);

      Template toMatch = client.templateBuilder()
              .imageId(template.getImage().getId())
              .build();
      assertEquals(toMatch.getImage(), template.getImage());
   }

   @Test
   public void testNetworksAndFirewallDeletedWhenAllGroupNodesAreTerminated() throws IOException {

      HttpRequest deleteNodeRequest = HttpRequest.builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-delete-networks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest deleteFirewallRequest = HttpRequest.builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/firewalls/jclouds-test-delete")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest getNetworkRequest = HttpRequest.builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/networks/jclouds-test-delete")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse getNetworkResponse = HttpResponse.builder().statusCode(200)
              .payload(staticPayloadFromResource("/GoogleComputeEngineServiceExpectTest/network_get.json")).build();

      HttpRequest listFirewallsRequest = HttpRequest.builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                        ".com/compute/v1/projects/myproject/global/firewalls")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse listFirewallsResponse = HttpResponse.builder().statusCode(200)
              .payload(staticPayloadFromResource("/GoogleComputeEngineServiceExpectTest/firewall_list.json")).build();

      HttpRequest deleteNetworkReqquest = HttpRequest.builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                        ".com/compute/v1/projects/myproject/global/networks/jclouds-test-delete")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest deleteDiskRequest = HttpRequest.builder()
            .method("DELETE")
            .endpoint("https://www.googleapis" +
                  ".com/compute/v1/projects/myproject/zones/us-central1-a/disks/test")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN).build();

      List<HttpRequest> orderedRequests = ImmutableList.<HttpRequest>builder()
              .add(requestForScopes(COMPUTE_READONLY_SCOPE))
              .add(GET_PROJECT_REQUEST)
              .add(getInstanceRequestForInstance("test-delete-networks"))
              .add(LIST_ZONES_REQ)
              .add(LIST_MACHINE_TYPES_REQUEST)
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_DEBIAN_IMAGES_REQUEST)
              .add(LIST_CENTOS_IMAGES_REQUEST)
              .add(getInstanceRequestForInstance("test-delete-networks"))
              .add(requestForScopes(COMPUTE_SCOPE))
              .add(deleteNodeRequest)
              .add(GET_ZONE_OPERATION_REQUEST)
              .add(deleteDiskRequest)
              .add(GET_ZONE_OPERATION_REQUEST)
              .add(getInstanceRequestForInstance("test-delete-networks"))
              .add(LIST_INSTANCES_REQUEST)
              .add(getNetworkRequest)
              .add(listFirewallsRequest)
              .add(deleteFirewallRequest)
              .add(GET_GLOBAL_OPERATION_REQUEST)
              .add(deleteNetworkReqquest)
              .add(GET_GLOBAL_OPERATION_REQUEST)
              .build();


      List<HttpResponse> orderedResponses = ImmutableList.<HttpResponse>builder()
              .add(TOKEN_RESPONSE)
              .add(GET_PROJECT_RESPONSE)
              .add(getInstanceResponseForInstanceAndNetworkAndStatus("test-delete-networks", "test-network", Instance
                      .Status.RUNNING.name()))
              .add(LIST_ZONES_SHORT_RESPONSE)
              .add(LIST_MACHINE_TYPES_RESPONSE)
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_DEBIAN_IMAGES_RESPONSE)
              .add(LIST_CENTOS_IMAGES_RESPONSE)
              .add(getInstanceResponseForInstanceAndNetworkAndStatus("test-delete-networks", "test-network", Instance
                                                                                                             .Status.RUNNING.name()))
              .add(TOKEN_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_ZONE_OPERATION_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_ZONE_OPERATION_RESPONSE)
              .add(getInstanceResponseForInstanceAndNetworkAndStatus("test-delete-networks", "test-network", Instance
                      .Status.TERMINATED.name()))
              .add(getListInstancesResponseForSingleInstanceAndNetworkAndStatus("test-delete-networks",
                      "test-network", Instance
                      .Status.TERMINATED.name()))
              .add(getNetworkResponse)
              .add(listFirewallsResponse)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_GLOBAL_OPERATION_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_GLOBAL_OPERATION_RESPONSE)
              .build();

      ComputeService client = orderedRequestsSendResponses(orderedRequests, orderedResponses);
      client.destroyNode("us-central1-a/test-delete-networks");

   }

   public void testListLocationsWhenResponseIs2xx() throws Exception {

      ImmutableMap<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.
              <HttpRequest, HttpResponse>builder()
              .put(requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE)
              .put(GET_PROJECT_REQUEST, GET_PROJECT_RESPONSE)
              .put(LIST_ZONES_REQ, LIST_ZONES_RESPONSE)
              .put(LIST_INSTANCES_REQUEST, LIST_INSTANCES_RESPONSE)
              .put(LIST_CENTRAL1B_INSTANCES_REQUEST, LIST_CENTRAL1B_INSTANCES_RESPONSE)
              .put(LIST_PROJECT_IMAGES_REQUEST, LIST_PROJECT_IMAGES_RESPONSE)
              .put(LIST_DEBIAN_IMAGES_REQUEST, LIST_DEBIAN_IMAGES_RESPONSE)
              .put(LIST_CENTOS_IMAGES_REQUEST, LIST_CENTOS_IMAGES_RESPONSE)
              .put(LIST_MACHINE_TYPES_REQUEST, LIST_MACHINE_TYPES_RESPONSE)
              .put(LIST_CENTRAL1B_MACHINE_TYPES_REQUEST, LIST_CENTRAL1B_MACHINE_TYPES_RESPONSE)
              .build();

      ComputeService apiWhenServersExist = requestsSendResponses(requestResponseMap);

      Set<? extends Location> locations = apiWhenServersExist.listAssignableLocations();

      assertNotNull(locations);
      assertEquals(locations.size(), 2);
      assertEquals(locations.iterator().next().getId(), "us-central1-a");

      assertNotNull(apiWhenServersExist.listNodes());
      assertEquals(apiWhenServersExist.listNodes().size(), 1);
      assertEquals(apiWhenServersExist.listNodes().iterator().next().getId(), "us-central1-a/test-0");
      assertEquals(apiWhenServersExist.listNodes().iterator().next().getName(), "test-0");
   }

   @Test(dependsOnMethods = "testListLocationsWhenResponseIs2xx")
   public void testCreateNodeWhenNetworkNorFirewallExistDoesNotExist() throws RunNodesException, IOException {


      String payload = Strings2.toStringAndClose(InstanceApiExpectTest.class.getResourceAsStream("/instance_get.json"));
      payload = payload.replace("test-0", "test-1");

      HttpResponse getInstanceResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromStringWithContentType(payload, "application/json")).build();

      HttpRequest getFirewallRequest = HttpRequest
                 .builder()
                 .method("GET")
                 .endpoint("https://www.googleapis" +
                         ".com/compute/v1/projects/myproject/global/firewalls/jclouds-test-port-22")
                 .addHeader("Accept", "application/json")
                 .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest insertFirewallRequest = HttpRequest
                 .builder()
                 .method("POST")
                 .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/global/firewalls")
                 .addHeader("Accept", "application/json")
                 .addHeader("Authorization", "Bearer " + TOKEN)
                 .payload(payloadFromStringWithContentType("{\"name\":\"jclouds-test-port-22\",\"network\":\"https://www.googleapis" +
                         ".com/compute/v1/projects/myproject/global/networks/jclouds-test\"," +
                         "\"sourceRanges\":[\"10.0.0.0/8\",\"0.0.0.0/0\"],\"sourceTags\":[\"aTag\"],\"targetTags\":[\"jclouds-test-port-22\"],\"allowed\":[{\"IPProtocol\":\"tcp\"," +
                         "\"ports\":[\"22\"]}," +
                         "{\"IPProtocol\":\"udp\",\"ports\":[\"22\"]}]}",
                         MediaType.APPLICATION_JSON))
                 .build();

      HttpRequest setTagsRequest = HttpRequest
                 .builder()
                 .method("POST")
                 .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/setTags")
                 .addHeader("Accept", "application/json")
                 .addHeader("Authorization", "Bearer " + TOKEN)
                 .payload(payloadFromStringWithContentType("{\"items\":[\"jclouds-test-port-22\"],\"fingerprint\":\"abcd\"}",
                         MediaType.APPLICATION_JSON))
                 .build();

      List<HttpRequest> orderedRequests = ImmutableList.<HttpRequest>builder()
              .add(requestForScopes(COMPUTE_READONLY_SCOPE))
              .add(GET_PROJECT_REQUEST)
              .add(LIST_ZONES_REQ)
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_DEBIAN_IMAGES_REQUEST)
              .add(LIST_CENTOS_IMAGES_REQUEST)
              .add(LIST_ZONES_REQ)
              .add(LIST_MACHINE_TYPES_REQUEST)
              .add(GET_NETWORK_REQUEST)
              .add(GET_NETWORK_REQUEST)
              .add(requestForScopes(COMPUTE_SCOPE))
              .add(INSERT_NETWORK_REQUEST)
              .add(GET_GLOBAL_OPERATION_REQUEST)
              .add(GET_NETWORK_REQUEST)
              .add(getFirewallRequest)
              .add(insertFirewallRequest)
              .add(GET_GLOBAL_OPERATION_REQUEST)
              .add(LIST_INSTANCES_REQUEST)
              .add(LIST_MACHINE_TYPES_REQUEST)
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_DEBIAN_IMAGES_REQUEST)
              .add(LIST_CENTOS_IMAGES_REQUEST)
              .add(createDiskRequestForInstance("test-1"))
              .add(GET_ZONE_OPERATION_REQUEST)
              .add(getDiskRequestForInstance("test-1"))
              .add(createInstanceRequestForInstance("test-1", "test", "jclouds-test", openSshKey))
              .add(GET_ZONE_OPERATION_REQUEST)
              .add(getInstanceRequestForInstance("test-1"))
              .add(SET_TAGS_REQUEST)
              .add(GET_ZONE_OPERATION_REQUEST)
              .add(getInstanceRequestForInstance("test-1"))
              .add(setTagsRequest)
              .add(LIST_PROJECT_IMAGES_REQUEST)
              .add(LIST_DEBIAN_IMAGES_REQUEST)
              .add(LIST_CENTOS_IMAGES_REQUEST)
              .add(setTagsRequest)
              .build();

      List<HttpResponse> orderedResponses = ImmutableList.<HttpResponse>builder()
              .add(TOKEN_RESPONSE)
              .add(GET_PROJECT_RESPONSE)
              .add(LIST_ZONES_SHORT_RESPONSE)
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_DEBIAN_IMAGES_RESPONSE)
              .add(LIST_CENTOS_IMAGES_RESPONSE)
              .add(LIST_ZONES_SHORT_RESPONSE)
              .add(LIST_MACHINE_TYPES_RESPONSE)
              .add(HttpResponse.builder().statusCode(404).build())
              .add(HttpResponse.builder().statusCode(404).build())
              .add(TOKEN_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_GLOBAL_OPERATION_RESPONSE)
              .add(GET_NETWORK_RESPONSE)
              .add(HttpResponse.builder().statusCode(404).build())
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_GLOBAL_OPERATION_RESPONSE)
              .add(LIST_INSTANCES_RESPONSE)
              .add(LIST_MACHINE_TYPES_RESPONSE)
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_DEBIAN_IMAGES_RESPONSE)
              .add(LIST_CENTOS_IMAGES_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_ZONE_OPERATION_RESPONSE)
              .add(getDiskResponseForInstance("test-1"))
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(GET_ZONE_OPERATION_RESPONSE)
              .add(getInstanceResponse)
              .add(SET_TAGS_RESPONSE)
              .add(GET_ZONE_OPERATION_RESPONSE)
              .add(getInstanceResponse)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .add(LIST_PROJECT_IMAGES_RESPONSE)
              .add(LIST_DEBIAN_IMAGES_RESPONSE)
              .add(LIST_CENTOS_IMAGES_RESPONSE)
              .add(SUCESSFULL_OPERATION_RESPONSE)
              .build();


      ComputeService computeService = orderedRequestsSendResponses(orderedRequests, orderedResponses);

      GoogleComputeEngineTemplateOptions options = computeService.templateOptions().as(GoogleComputeEngineTemplateOptions.class);
      options.tags(ImmutableSet.of("aTag"));
      NodeMetadata node = getOnlyElement(computeService.createNodesInGroup("test", 1, options));
      assertEquals(node.getImageId(), "gcel-12-04-v20121106");
   }
}

