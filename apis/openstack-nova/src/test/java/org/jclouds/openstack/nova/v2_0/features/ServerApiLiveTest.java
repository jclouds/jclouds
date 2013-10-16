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

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.predicates.LinkPredicates;
import org.testng.annotations.Test;

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
