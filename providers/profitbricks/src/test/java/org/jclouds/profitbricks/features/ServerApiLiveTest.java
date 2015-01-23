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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusAware;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusPollingPredicate;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.util.Predicates2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test( groups = "live", testName = "ServerApiLiveTest", singleThreaded = true )
public class ServerApiLiveTest extends BaseProfitBricksLiveTest {

   private Predicate<String> waitUntilAvailable;
   private DataCenter dataCenter;
   private String createdServerId;

   @Override
   protected void initialize() {
      super.initialize();
      List<DataCenter> dataCenters = api.dataCenterApi().getAllDataCenters();
      assertFalse( dataCenters.isEmpty(), "Must atleast have 1 datacenter available for server testing." );

      this.dataCenter = Iterables.getFirst( dataCenters, null );

      this.waitUntilAvailable = Predicates2.retry(
              new ProvisioningStatusPollingPredicate( api, ProvisioningStatusAware.SERVER, ProvisioningState.AVAILABLE ),
              2l * 60l, 2l, TimeUnit.SECONDS );
   }

   @Test
   public void testCreateServer() {
      String serverId = api.serverApi().createServer(
              Server.Request.CreatePayload.create( dataCenter.id(), "jclouds-node", 1, 1024 ) );

      assertNotNull( serverId );
      this.createdServerId = serverId;
   }

   @Test( dependsOnMethods = "testCreateServer" )
   public void testGetServer() {
      Server server = api.serverApi().getServer( createdServerId );

      assertNotNull( server );
      assertEquals( server.id(), createdServerId );
   }

   @Test( dependsOnMethods = "testCreateServer" )
   public void testGetAllServers() {
      List<Server> servers = api.serverApi().getAllServers();

      assertNotNull( servers );
      assertFalse( servers.isEmpty() );
   }

   @Test( dependsOnMethods = "testCreateServer" )
   public void testWaitUntilAvailable() {
      boolean available = waitUntilAvailable.apply( createdServerId );

      assertTrue( available );
   }

   @Test( dependsOnMethods = "testWaitUntilAvailable" )
   public void testUpdateServer() {
      String requestId = api.serverApi().updateServer(
              Server.Request.updatingBuilder()
              .id( createdServerId )
              .name( "apache-node" )
              .cores( 2 )
              .ram( 2 * 1024 )
              .build() );

      assertNotNull( requestId );
      waitUntilAvailable.apply( createdServerId );

      Server server = api.serverApi().getServer( createdServerId );
      assertEquals( server.state(), ProvisioningState.AVAILABLE );
   }

   @Test( dependsOnMethods = "testUpdateServer" )
   public void testStopServer() {
      String requestId = api.serverApi().stopServer( createdServerId );
      assertNotNull( requestId );

      Predicate<String> waitUntilInactive = Predicates2.retry( new ProvisioningStatusPollingPredicate(
              api, ProvisioningStatusAware.SERVER, ProvisioningState.INACTIVE ), 2l * 60l, 2l, TimeUnit.SECONDS );

      waitUntilInactive.apply( createdServerId );
      Server server = api.serverApi().getServer( createdServerId );
      assertEquals( server.status(), Server.Status.SHUTOFF );
   }

   @Test( dependsOnMethods = "testStopServer" )
   public void testStartServer() {
      String requestId = api.serverApi().startServer( createdServerId );
      assertNotNull( requestId );

      waitUntilAvailable.apply( createdServerId );

      Server server = api.serverApi().getServer( createdServerId );
      assertEquals( server.status(), Server.Status.RUNNING );
   }

   @AfterClass( alwaysRun = true )
   public void testDeleteServer() {
      if ( createdServerId != null ) {
         boolean result = api.serverApi().deleteServer( createdServerId );

         assertTrue( result, "Created test server was not deleted." );
      }
   }

}
