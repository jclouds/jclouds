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
package org.jclouds.profitbricks.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.domain.LoadBalancer.Algorithm;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "LoadBalancerApiMockTest")
public class LoadBalancerApiMockTest extends BaseProfitBricksMockTest {

   @Test
   public void testGetAllLoadBalancers() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/loadbalancer/loadbalancers.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      LoadBalancerApi api = pbApi.loadBalancerApi();

      try {
         List<LoadBalancer> loadBalancerList = api.getAllLoadBalancers();

         assertRequestHasCommonProperties(server.takeRequest(), "<ws:getAllLoadBalancers/>");
         assertNotNull(loadBalancerList);
         assertTrue(loadBalancerList.size() == 2);

      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetAllLoadBalancersReturning404() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      LoadBalancerApi api = pbApi.loadBalancerApi();

      try {
         List<LoadBalancer> loadBalancerList = api.getAllLoadBalancers();

         assertRequestHasCommonProperties(server.takeRequest());
         assertTrue(loadBalancerList.isEmpty());

      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/loadbalancer/loadbalancer.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      LoadBalancerApi api = pbApi.loadBalancerApi();

      String id = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

      String content = "<ws:getLoadBalancer><loadBalancerId>" + id + "</loadBalancerId></ws:getLoadBalancer>";
      try {
         LoadBalancer loadBalancer = api.getLoadBalancer(id);

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(loadBalancer);
         assertEquals(loadBalancer.id(), id);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetNonExistingLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      LoadBalancerApi api = pbApi.loadBalancerApi();

      String id = "random-non-existing-id";

      try {
         LoadBalancer loadBalancer = api.getLoadBalancer(id);

         assertRequestHasCommonProperties(server.takeRequest());
         assertNull(loadBalancer);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testCreateLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/loadbalancer/loadbalancer-create.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      LoadBalancerApi api = pbApi.loadBalancerApi();

      String content = "<ws:createLoadBalancer>"
              + "<request>"
              + "<dataCenterId>aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeeee</dataCenterId>"
              + "<loadBalancerName>load-balancer-name</loadBalancerName>"
              + "<loadBalancerAlgorithm>ROUND_ROBIN</loadBalancerAlgorithm>"
              + "<ip>192.168.0.1</ip>"
              + "<lanId>3</lanId>"
              + "<serverIds>server-ids</serverIds>"
              + "</request>"
              + "</ws:createLoadBalancer>";

      try {
         List<String> serverIds = Lists.newArrayList();
         serverIds.add("server-ids");
         String loadBalancerId = api.createLoadBalancer(LoadBalancer.Request.creatingBuilder()
                 .dataCenterId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeeee")
                 .name("load-balancer-name")
                 .algorithm(Algorithm.ROUND_ROBIN)
                 .ip("192.168.0.1")
                 .lanId(3)
                 .serverIds(serverIds)
                 .build());

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertEquals(loadBalancerId, "1234-1234-1234-1234");
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testUpdateLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/loadbalancer/loadbalancer-update.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      LoadBalancerApi api = pbApi.loadBalancerApi();

      String id = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
      String newName = "Apache";

      String content = "<ws:updateLoadBalancer>"
              + "<request>"
              + "<loadBalancerId>" + id + "</loadBalancerId>"
              + "<loadBalancerName>load-balancer-name</loadBalancerName>"
              + "<loadBalancerAlgorithm>ROUND_ROBIN</loadBalancerAlgorithm>"
              + "<ip>192.168.0.1</ip>"
              + "</request>"
              + "</ws:updateLoadBalancer>";

      try {
         LoadBalancer.Request.UpdatePayload toUpdate = LoadBalancer.Request.updatingBuilder()
                 .id(id)
                 .name("load-balancer-name")
                 .algorithm(Algorithm.ROUND_ROBIN)
                 .ip("192.168.0.1")
                 .build();

         String requestId = api.updateLoadBalancer(toUpdate);

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertEquals(requestId, "request-id");
      } finally {
         pbApi.close();
         server.shutdown();
      }

   }

   @Test
   public void testRegisterLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/loadbalancer/loadbalancer-register.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      LoadBalancerApi api = pbApi.loadBalancerApi();

      String content = "<ws:registerServersOnLoadBalancer>"
              + "<loadBalancerId>1234</loadBalancerId>"
              + "<serverIds>1</serverIds>"
              + "<serverIds>2</serverIds>"
              + "</ws:registerServersOnLoadBalancer>";

      try {
         List<String> serverIds = Lists.newArrayList();
         serverIds.add("1");
         serverIds.add("2");
         LoadBalancer.Request.RegisterPayload payload = LoadBalancer.Request
                 .createRegisteringPaylod("1234", serverIds);

         LoadBalancer loadBalancer = api.registerLoadBalancer(payload);

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(loadBalancer);
         assertEquals(loadBalancer.id(), "load-balancer-id");
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeregisterLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/loadbalancer/loadbalancer-deregister.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      LoadBalancerApi api = pbApi.loadBalancerApi();

      String content = "<ws:deregisterServersOnLoadBalancer>"
              + "<serverIds>1</serverIds>"
              + "<serverIds>2</serverIds>"
              + "<loadBalancerId>load-balancer-id</loadBalancerId>"
              + "</ws:deregisterServersOnLoadBalancer>";

      try {
         LoadBalancer.Request.DeregisterPayload payload = LoadBalancer.Request
                 .createDeregisteringPayload("load-balancer-id", ImmutableList.of("1", "2"));

         String requestId = api.deregisterLoadBalancer(payload);

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertEquals(requestId, "request-id");
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/loadbalancer/loadbalancer-delete.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      LoadBalancerApi api = pbApi.loadBalancerApi();

      String loadBalancerId = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";

      String content = "<ws:deleteLoadBalancer><loadBalancerId>" + loadBalancerId + "</loadBalancerId></ws:deleteLoadBalancer>";

      try {
         boolean done = api.deleteLoadBalancer(loadBalancerId);

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertTrue(done);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }
}
