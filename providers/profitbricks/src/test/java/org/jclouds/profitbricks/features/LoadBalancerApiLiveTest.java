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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.domain.LoadBalancer.Algorithm;
import org.jclouds.profitbricks.domain.Server;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "LoadBalancerApiLiveTest")
public class LoadBalancerApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Server server;

   private String loadBalancerId;

   @BeforeClass
   public void setupTest() {
      dataCenter = findOrCreateDataCenter("loadBalancerApiLiveTest" + System.currentTimeMillis());
      server = findOrCreateServer(dataCenter);
   }

   @Test
   public void testCreateLoadBalancer() {
      assertDataCenterAvailable(dataCenter);
      String createdId = api.loadBalancerApi().createLoadBalancer(
              LoadBalancer.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("testName")
              .algorithm(Algorithm.ROUND_ROBIN)
              .ip("192.168.0.200")
              .lanId(1)
              .build()
      );

      assertNotNull(createdId);
      assertDataCenterAvailable(dataCenter);
      this.loadBalancerId = createdId;
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetAllLoadBalancers() {
      List<LoadBalancer> loadBalancers = api.loadBalancerApi().getAllLoadBalancers();

      assertFalse(loadBalancers.isEmpty());
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetLoadBalancer() {
      LoadBalancer loadBalancer = api.loadBalancerApi().getLoadBalancer(loadBalancerId);

      assertNotNull(loadBalancer);
      assertEquals(loadBalancer.id(), loadBalancerId);
   }

   @Test(dependsOnMethods = "testGetLoadBalancer")
   public void testRegisterLoadBalancer() {
      assertDataCenterAvailable(dataCenter);
      LoadBalancer loadBalancer = api.loadBalancerApi().registerLoadBalancer(
              LoadBalancer.Request
              .createRegisteringPaylod(loadBalancerId, ImmutableList.of(server.id()))
      );

      assertNotNull(loadBalancer);
      assertDataCenterAvailable(dataCenter);
      Optional<Server> balancedServer = Iterables.tryFind(loadBalancer.balancedServers(), new Predicate<Server>() {

         @Override
         public boolean apply(Server t) {
            return Objects.equals(t.id(), server.id());
         }
      });
      assertTrue(balancedServer.isPresent(), "Server input wasn't registered to loadbalancer");
   }

   @Test(dependsOnMethods = "testRegisterLoadBalancer")
   public void testDeregisterLoadBalancer() {
      assertDataCenterAvailable(dataCenter);
      String requestId = api.loadBalancerApi().deregisterLoadBalancer(
              LoadBalancer.Request
              .createDeregisteringPayload(loadBalancerId, ImmutableList.of(server.id()))
      );

      assertNotNull(requestId);
      assertDataCenterAvailable(dataCenter);
      LoadBalancer loadBalancer = api.loadBalancerApi().getLoadBalancer(loadBalancerId);
      Optional<Server> balancedServer = Iterables.tryFind(loadBalancer.balancedServers(), new Predicate<Server>() {

         @Override
         public boolean apply(Server t) {
            return Objects.equals(t.id(), loadBalancerId);
         }
      });
      assertFalse(balancedServer.isPresent(), "Server input wasn't deregistered from loadbalancer");
   }

   @Test(dependsOnMethods = "testDeregisterLoadBalancer")
   public void testUpdateLoadBalancer() {
      assertDataCenterAvailable(dataCenter);
      String newName = "whatever";
      String requestId = api.loadBalancerApi().updateLoadBalancer(
              LoadBalancer.Request.updatingBuilder()
              .id(loadBalancerId)
              .name(newName)
              .build()
      );

      assertNotNull(requestId);
      assertDataCenterAvailable(dataCenter);
      LoadBalancer loadBalancer = api.loadBalancerApi().getLoadBalancer(loadBalancerId);
      assertEquals(loadBalancer.name(), newName);
   }

   @Test(dependsOnMethods = "testUpdateLoadBalancer")
   public void testDeleteLoadBalancer() {
      assertDataCenterAvailable(dataCenter);
      boolean result = api.loadBalancerApi().deleteLoadBalancer(loadBalancerId);
      assertTrue(result, "Test load balancer wasn't deleted");
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      destroyDataCenter(dataCenter);
   }
}
