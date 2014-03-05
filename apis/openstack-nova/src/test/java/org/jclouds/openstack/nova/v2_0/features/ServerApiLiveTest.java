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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.nova.v2_0.domain.Network;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.options.RebuildServerOptions;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.predicates.LinkPredicates;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@link ServerApi}
 * 
 * @author Adrian Cole
 * @author Inbar Stolberg
 */
@Test(groups = "live", testName = "ServerApiLiveTest")
public class ServerApiLiveTest extends BaseNovaApiLiveTest {

   @Test(description = "GET /v${apiVersion}/{tenantId}/servers")
   public void testListServers() throws Exception {
      for (String zoneId : zones) {
         ServerApi serverApi = api.getServerApiForZone(zoneId);
         for (Resource server : serverApi.list().concat()) {
            checkResource(server);
         }
      }
   }

   @Test(description = "GET /v${apiVersion}/{tenantId}/servers/detail")
   public void testListServersInDetail() throws Exception {
      for (String zoneId : zones) {
         ServerApi serverApi = api.getServerApiForZone(zoneId);
         for (Server server : serverApi.listInDetail().concat()) {
            checkServer(server);
         }
      }
   }

   @Test(description = "GET /v${apiVersion}/{tenantId}/servers/{id}", dependsOnMethods = { "testListServersInDetail" })
   public void testGetServerById() throws Exception {
      for (String zoneId : zones) {
         ServerApi serverApi = api.getServerApiForZone(zoneId);
         for (Resource server : serverApi.list().concat()) {
            Server details = serverApi.get(server.getId());
            assertEquals(details.getId(), server.getId());
            assertEquals(details.getName(), server.getName());
            assertEquals(details.getLinks(), server.getLinks());
            checkServer(details);
         }
      }
   }

   @Test
   public void testCreateInAvailabilityZone() {
      String serverId = null;
      for (String zoneId : zones) {
         ServerApi serverApi = api.getServerApiForZone(zoneId);
         try {
            serverId = createServer(zoneId, "nova", Server.Status.ACTIVE).getId();
            Server server = serverApi.get(serverId);
            assertEquals(server.getStatus(), Server.Status.ACTIVE);
         } finally {
            serverApi.delete(serverId);
         }
      }
   }

   /**
    * This needs to be supported by the provider, and is usually not supported.
    * However this can be tested on devstack:
    * In apis/openstack-nova:
    * mvn -Plive clean install "-Dtest.openstack-nova.endpoint=http://localhost:5000/v2.0" "-Dtest.openstack-nova.identity=demo:demo" "-Dtest.openstack-nova.credential=devstack" "-Dtest=org.jclouds.openstack.nova.v2_0.features.ServerApiLiveTest#testCreateWithNetworkOptions"
    */
   @Test(enabled = false)
   public void testCreateWithNetworkOptions() {
      String serverId = null;
      for (String zoneId : zones) {
         ServerApi serverApi = api.getServerApiForZone(zoneId);
         try {
            CreateServerOptions options = CreateServerOptions.Builder.novaNetworks(
                  // This network UUID must match an existing network.
                  ImmutableSet.of(Network.builder().networkUuid("bc4cfa2b-2b27-4671-8e8f-73009623def0").fixedIp("192.168.55.56").build())
                  );
            ServerCreated server = serverApi.create(hostName, imageIdForZone(zoneId), "1", options);
            serverId = server.getId();

            blockUntilServerInState(server.getId(), serverApi, Server.Status.ACTIVE);
            Server serverCheck = serverApi.get(serverId);
            assertEquals(serverCheck.getStatus(), Server.Status.ACTIVE);
         } finally {
            if (serverId != null) {
               serverApi.delete(serverId);
            }
         }
      }
   }

   @Test
   public void testCreateInWrongAvailabilityZone() {
      String serverId = null;
      for (String zoneId : zones) {
         ServerApi serverApi = api.getServerApiForZone(zoneId);
         try {
            serverId = createServer(zoneId, "err", Server.Status.ERROR).getId();
            Server server = serverApi.get(serverId);
            assertEquals(server.getStatus(), Server.Status.ERROR);
         } finally {
            serverApi.delete(serverId);
         }
      }
   }

   @Test
   public void testRebuildServer() {

      String serverId = null;

      for (String zoneId : zones) {
         ServerApi serverApi = api.getServerApiForZone(zoneId);
         try {
            serverId = createServer(zoneId, Server.Status.ACTIVE).getId();

            Server server = serverApi.get(serverId);

            assertEquals(server.getStatus(), Server.Status.ACTIVE);

            RebuildServerOptions options = new RebuildServerOptions().
                  withImage(server.getImage().getId()).
                  name("newName").
                  adminPass("password").
                  ipv4Address("1.1.1.1").
                  ipv6Address("fe80::100");

            serverApi.rebuild(serverId, options);

            Server rebuiltServer = serverApi.get(serverId);

            assertEquals("newName", rebuiltServer.getName());
            assertEquals("1.1.1.1", rebuiltServer.getAccessIPv4());
            assertEquals("fe80::100", rebuiltServer.getAccessIPv6());

         } finally {
            serverApi.delete(serverId);
         }
      }
   }

   private Server createServer(String regionId, Server.Status serverStatus) {
      ServerApi serverApi = api.getServerApiForZone(regionId);
      CreateServerOptions options = new CreateServerOptions();
      ServerCreated server = serverApi.create(hostName, imageIdForZone(regionId), flavorRefForZone(regionId), options);

      blockUntilServerInState(server.getId(), serverApi, serverStatus);

      return serverApi.get(server.getId());
   }

   private Server createServer(String regionId, String availabilityZoneId, Server.Status serverStatus) {
      ServerApi serverApi = api.getServerApiForZone(regionId);
      CreateServerOptions options = new CreateServerOptions();
      options = options.availabilityZone(availabilityZoneId);
      ServerCreated server = serverApi.create(hostName, imageIdForZone(regionId), flavorRefForZone(regionId), options);
      blockUntilServerInState(server.getId(), serverApi, serverStatus);
      return serverApi.get(server.getId());
   }

   private void checkResource(Resource resource) {
      assertNotNull(resource.getId());
      assertNotNull(resource.getName());
      assertNotNull(resource.getLinks());
      assertTrue(Iterables.any(resource.getLinks(), LinkPredicates.relationEquals(Relation.SELF)));
   }

   private void checkServer(Server server) {
      checkResource(server);
      assertFalse(server.getAddresses().isEmpty());
   }
}
