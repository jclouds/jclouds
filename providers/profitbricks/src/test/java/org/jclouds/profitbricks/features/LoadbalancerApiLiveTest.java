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

import com.google.common.collect.Iterables;
import java.util.List;
import org.assertj.core.util.Lists;
import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.domain.LoadBalancer.Algorithm;
import org.jclouds.profitbricks.domain.Server;
import org.testng.Assert;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "LoadbalancerApiLiveTest")
public class LoadbalancerApiLiveTest extends BaseProfitBricksLiveTest {

   private String dataCenterId;
   private String loadBalancerID;
   private String serverId;

   @Override
   protected void initialize() {
      super.initialize();
      List<DataCenter> dataCenters = api.dataCenterApi().getAllDataCenters();
      assertFalse(dataCenters.isEmpty(), "At least 1 datacenter has to be available for loadbalancer testing.");

      dataCenterId = Iterables.getFirst(dataCenters, null).id();

      List<Server> servers = api.serverApi().getAllServers();
      assertFalse(servers.isEmpty(), "At least 1 server has to be available for loadbalancer testing.");

      serverId = Iterables.getFirst(servers, null).id();
   }

   @Test
   public void testCreateLoadBalancer() {
      List<String> serverIds = com.google.common.collect.Lists.newArrayList();
      serverIds.add("server-ids");

      LoadBalancer.Request.CreatePayload payload = LoadBalancer.Request.creatingBuilder()
              .dataCenterId(dataCenterId)
              .loadBalancerName("testName")
              .loadBalancerAlgorithm(Algorithm.ROUND_ROBIN)
              .ip("0.0.0.1")
              .lanId("1")
              .serverIds(serverIds)
              .build();

      String requestId = api.loadBalancerApi().createLoadBalancer(payload);

      assertNotNull(requestId);
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetAllLoadBalancers() {
      List<LoadBalancer> loadBalancers = api.loadBalancerApi().getAllLoadBalancers();

      assertFalse(loadBalancers.isEmpty());
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetLoadBalancer() {
      LoadBalancer loadBalancer = api.loadBalancerApi().getLoadBalancer(loadBalancerID);

      assertNotNull(loadBalancer);
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testRegisterLoadBalancer() {
      List<String> serverIds = Lists.newArrayList();
      serverIds.add(serverId);

      LoadBalancer.Request.RegisterPayload payload = LoadBalancer.Request.registerBuilder()
              .id(loadBalancerID)
              .serverIds(serverIds)
              .build();

      LoadBalancer loadBalancer = api.loadBalancerApi().registerLoadBalancer(payload);

      assertNotNull(loadBalancer);
   }

   @Test(dependsOnMethods = "testRegisterLoadBalancer")
   public void testDeregisterLoadBalancer() {
      List<String> serverIds = Lists.newArrayList();
      serverIds.add(serverId);

      LoadBalancer.Request.DeregisterPayload payload = LoadBalancer.Request.deregisterBuilder()
              .id(loadBalancerID)
              .serverIds(serverIds)
              .build();

      LoadBalancer loadBalancer = api.loadBalancerApi().deregisterLoadBalancer(payload);

      assertNotNull(loadBalancer);
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testUpdateLoadBalancer() {
      LoadBalancer.Request.UpdatePayload payload = LoadBalancer.Request.updatingBuilder()
              .id(loadBalancerID)
              .loadBalancerName("whatever")
              .build();

      LoadBalancer loadBalancer = api.loadBalancerApi().updateLoadBalancer(payload);

      assertNotNull(loadBalancer);
   }

   @AfterClass(alwaysRun = true)
   public void testDeleteLoadBalancer() {
      boolean result = api.loadBalancerApi().deleteLoadbalancer(loadBalancerID);

      Assert.assertTrue(result);
   }
}
