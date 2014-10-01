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

package org.jclouds.openstack.neutron.v2.extensions.lbaas.v1;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.jclouds.logging.Logger;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.NetworkType;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.HealthMonitor;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.HealthMonitors;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.HttpMethod;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.LBaaSStatus;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Member;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Members;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Pool;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Pools;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.ProbeType;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Protocol;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.SessionPersistence;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.VIP;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.VIPs;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.SubnetApi;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiLiveTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * Tests parsing and Guice wiring of RouterApi
 */
@Test(groups = "live", testName = "LBaaSApiLiveTest")
public class LBaaSApiLiveTest extends BaseNeutronApiLiveTest {

   private Logger logger = getLoggingModule().createLoggerFactory().getLogger(LBaaSApiLiveTest.class.getName());

   private Map<String, Network> networks;
   private Map<String, Subnet> subnets;

   public void testLBaaSPresence() throws Exception {
      for (String region : api.getConfiguredRegions()) {
         Optional<LBaaSApi> lBaaSv1Api = api.getLBaaSApi(region);

         /*
          * Check response
          */
         assertNotNull(lBaaSv1Api);
         if (lBaaSv1Api.isPresent()) {
            logger.info("LBaaS API Version 1 is available");
         } else {
            logger.info("LBaaS API Version 1 is unavailable");
         }
      }
   }

   @BeforeClass
   public void createSubnets() {
      networks = new HashMap<>();
      subnets = new HashMap<>();
      for (String region : api.getConfiguredRegions()) {
         Optional<LBaaSApi> lbaasApiExtension = api.getLBaaSApi(region);
         if (!lbaasApiExtension.isPresent()) {
            continue;
         }
         NetworkApi networkApi = api.getNetworkApi(region);
         SubnetApi subnetApi = api.getSubnetApi(region);

         Network network = networkApi.create(Network.createBuilder("jclouds-lbaas-test-network").networkType(NetworkType.LOCAL).build());
         assertNotNull(network);
         networks.put(region, network);

         Subnet subnet = subnetApi.create(Subnet.createBuilder(network.getId(), "10.0.0.0/24").ipVersion(4).name("jclouds-lbaas-test-subnet").build());
         assertNotNull(subnet);
         subnets.put(region, subnet);
      }
   }

   @AfterClass
   public void deleteSubnets() {
      for (String region : api.getConfiguredRegions()) {
         NetworkApi networkApi = api.getNetworkApi(region);
         SubnetApi subnetApi = api.getSubnetApi(region);

         try {
            Subnet subnet = subnets.get(region);
            if (subnet != null) {
               assertTrue(subnetApi.delete(subnet.getId()));
            }
         } finally {
            Network network = networks.get(region);
            if (network != null) {
               assertTrue(networkApi.delete(network.getId()));
            }
         }
      }
      networks = null;
      subnets = null;
   }

   public void testCreateUpdateAndDeletePool() {
      for (String region : api.getConfiguredRegions()) {
         Optional<LBaaSApi> lbaasApiExtension = api.getLBaaSApi(region);
         if (!lbaasApiExtension.isPresent()) {
            continue;
         }
         LBaaSApi lbaasApi = lbaasApiExtension.get();

         Subnet subnet = subnets.get(region);
         Pool pool = null;

         try {
            // Create
            Pool.CreatePool createPool = Pool.createBuilder(subnet.getId(), Protocol.HTTP, Pool.ROUND_ROBIN)
                  .name("jclouds-lbaas-test-pool").description(null).healthMonitors(null).provider(null).adminStateUp(null).build();
            pool = lbaasApi.createPool(createPool);
            assertNotNull(pool);
            assertNotNull(pool.getId());
            assertEquals(pool.getTenantId(), subnet.getTenantId());
            assertNull(pool.getVIPId());
            assertEquals(pool.getName(), "jclouds-lbaas-test-pool");
            assertEquals(pool.getDescription(), "");
            assertEquals(pool.getSubnetId(), subnet.getId());
            assertEquals(pool.getProtocol(), Protocol.HTTP);
            assertNotNull(pool.getProvider());
            assertEquals(pool.getLBMethod(), Pool.ROUND_ROBIN);
            assertNotNull(pool.getHealthMonitors());
            assertTrue(pool.getHealthMonitors().isEmpty());
            assertNotNull(pool.getHealthMonitorsStatus());
            assertTrue(pool.getHealthMonitorsStatus().isEmpty());
            assertNotNull(pool.getMembers());
            assertTrue(pool.getMembers().isEmpty());
            assertEquals(pool.getAdminStateUp(), Boolean.TRUE);
            assertTrue(pool.getStatus() == LBaaSStatus.PENDING_CREATE || pool.getStatus() == LBaaSStatus.ACTIVE);
            assertNull(pool.getStatusDescription());

            // List and Get
            Pools pools = lbaasApi.listPools(PaginationOptions.Builder.queryParameters(ImmutableMap.of("name", "jclouds-lbaas-test-pool").asMultimap()));
            assertNotNull(pools);
            assertFalse(pools.isEmpty());
            Pool poolList = pools.first().get();
            Pool poolGet = lbaasApi.getPool(poolList.getId());
            assertNotNull(poolGet);
            assertEquals(poolGet, poolList);

            poolGet = lbaasApi.getPool(pool.getId());
            assertNotNull(poolGet);
            assertEquals(poolGet.getName(), pool.getName());
            assertEquals(poolGet.getId(), pool.getId());

            // Update
            Pool.UpdatePool updatePool = Pool.updateBuilder().name("jclouds-lbaas-test-pool-renamed").description("new description").lbMethod(Pool.ROUND_ROBIN)
                  .healthMonitors(null).adminStateUp(Boolean.FALSE).build();
            Pool poolUpdate = lbaasApi.updatePool(pool.getId(), updatePool);
            assertNotNull(poolUpdate);
            assertEquals(poolUpdate.getName(), "jclouds-lbaas-test-pool-renamed");
            assertEquals(poolUpdate.getLBMethod(), Pool.ROUND_ROBIN);
            assertNotNull(poolUpdate.getHealthMonitors());
            assertTrue(poolUpdate.getHealthMonitors().isEmpty());
            assertNotNull(poolUpdate.getHealthMonitorsStatus());
            assertTrue(poolUpdate.getHealthMonitorsStatus().isEmpty());
            assertEquals(poolUpdate.getAdminStateUp(), Boolean.FALSE);

            poolGet = lbaasApi.getPool(pool.getId());
            assertNotNull(poolGet);
            assertEquals(poolGet.getId(), pool.getId());
            assertEquals(poolGet.getTenantId(), subnet.getTenantId());
            assertNull(poolGet.getVIPId());
            assertEquals(poolGet.getName(), "jclouds-lbaas-test-pool-renamed");
            assertEquals(poolGet.getDescription(), "new description");
            assertEquals(poolGet.getSubnetId(), subnet.getId());
            assertEquals(poolGet.getProtocol(), Protocol.HTTP);
            assertNotNull(poolGet.getProvider());
            assertEquals(poolGet.getLBMethod(), Pool.ROUND_ROBIN);
            assertNotNull(poolGet.getHealthMonitors());
            assertTrue(poolGet.getHealthMonitors().isEmpty());
            assertNotNull(poolGet.getHealthMonitorsStatus());
            assertTrue(poolGet.getHealthMonitorsStatus().isEmpty());
            assertNotNull(poolGet.getMembers());
            assertTrue(poolGet.getMembers().isEmpty());
            assertEquals(poolGet.getAdminStateUp(), Boolean.FALSE);
            assertTrue(poolGet.getStatus() == LBaaSStatus.PENDING_UPDATE || poolGet.getStatus() == LBaaSStatus.ACTIVE);
            assertNull(poolGet.getStatusDescription());
         } finally {
            if (pool != null) {
               // Delete
               assertTrue(lbaasApi.deletePool(pool.getId()));
               Pool poolGet = lbaasApi.getPool(pool.getId());
               assertNull(poolGet);
            }
         }
      }
   }

   public void testCreateUpdateAndDeleteMember() {
      for (String region : api.getConfiguredRegions()) {
         Optional<LBaaSApi> lbaasApiExtension = api.getLBaaSApi(region);
         if (!lbaasApiExtension.isPresent()) {
            continue;
         }
         LBaaSApi lbaasApi = lbaasApiExtension.get();

         Subnet subnet = subnets.get(region);
         Pool pool1 = null;
         Pool pool2 = null;
         Member member = null;

         try {
            // Create pools
            Pool.CreateBuilder createBuilder = Pool.createBuilder(subnet.getId(), Protocol.HTTP, Pool.ROUND_ROBIN)
                  .name("jclouds-lbaas-test-member-pool-1").description(null).healthMonitors(null).provider(null).adminStateUp(null);
            pool1 = lbaasApi.createPool(createBuilder.build());
            assertNotNull(pool1);
            createBuilder.name("jclouds-lbaas-test-member-pool-2");
            pool2 = lbaasApi.createPool(createBuilder.build());
            assertNotNull(pool2);

            // Create
            Member.CreateMember createMember = Member.createBuilder(pool1.getId(), "10.0.0.100", 80)
                  .weight(null).adminStateUp(null).build();
            member = lbaasApi.createMember(createMember);
            assertNotNull(member);
            assertNotNull(member.getId());
            assertEquals(member.getTenantId(), subnet.getTenantId());
            assertEquals(member.getPoolId(), pool1.getId());
            assertEquals(member.getAddress(), "10.0.0.100");
            assertEquals(member.getProtocolPort(), Integer.valueOf(80));
            assertEquals(member.getWeight(), Integer.valueOf(1));
            assertEquals(member.getAdminStateUp(), Boolean.TRUE);
            assertTrue(member.getStatus() == LBaaSStatus.PENDING_CREATE || member.getStatus() == LBaaSStatus.ACTIVE);
            assertNull(member.getStatusDescription());

            // List and Get
            Members members = lbaasApi.listMembers(PaginationOptions.Builder.queryParameters(ImmutableMap.of("tenant_id", subnet.getTenantId()).asMultimap()));
            assertNotNull(members);
            assertFalse(members.isEmpty());
            Member memberList = members.first().get();
            Member memberGet = lbaasApi.getMember(memberList.getId());
            assertNotNull(memberGet);
            assertEquals(memberGet, memberList);

            memberGet = lbaasApi.getMember(member.getId());
            assertNotNull(memberGet);
            assertEquals(memberGet.getId(), member.getId());

            // Verify member appears in pool1 and not in pool2
            Pool pool1Get = lbaasApi.getPool(pool1.getId());
            assertNotNull(pool1Get);
            assertNotNull(pool1Get.getMembers());
            assertFalse(pool1Get.getMembers().isEmpty());
            assertEquals(pool1Get.getMembers().iterator().next(), member.getId());
            Pool pool2Get = lbaasApi.getPool(pool2.getId());
            assertNotNull(pool2Get);
            assertNotNull(pool2Get.getMembers());
            assertTrue(pool2Get.getMembers().isEmpty());

            // Update
            Member.UpdateMember updateMember = Member.updateBuilder()
                  .poolId(pool2.getId()).weight(2).adminStateUp(Boolean.FALSE).build();
            Member memberUpdate = lbaasApi.updateMember(member.getId(), updateMember);
            assertNotNull(memberUpdate);
            assertEquals(memberUpdate.getPoolId(), pool2.getId());
            assertEquals(memberUpdate.getWeight(), Integer.valueOf(2));
            assertEquals(memberUpdate.getAdminStateUp(), Boolean.FALSE);

            memberGet = lbaasApi.getMember(member.getId());
            assertNotNull(memberGet);
            assertNotNull(memberGet.getId());
            assertEquals(memberGet.getTenantId(), subnet.getTenantId());
            assertEquals(memberGet.getPoolId(), pool2.getId());
            assertEquals(member.getAddress(), "10.0.0.100");
            assertEquals(memberGet.getProtocolPort(), Integer.valueOf(80));
            assertEquals(memberGet.getWeight(), Integer.valueOf(2));
            assertEquals(memberGet.getAdminStateUp(), Boolean.FALSE);
            assertTrue(memberGet.getStatus() == LBaaSStatus.PENDING_UPDATE || memberGet.getStatus() == LBaaSStatus.ACTIVE);
            assertNull(memberGet.getStatusDescription());

            // Verify member appears in pool2 and not in pool1
            pool1Get = lbaasApi.getPool(pool1.getId());
            assertNotNull(pool1Get);
            assertNotNull(pool1Get.getMembers());
            assertTrue(pool1Get.getMembers().isEmpty());
            pool2Get = lbaasApi.getPool(pool2.getId());
            assertNotNull(pool2Get);
            assertNotNull(pool2Get.getMembers());
            assertFalse(pool2Get.getMembers().isEmpty());
            assertEquals(pool2Get.getMembers().iterator().next(), member.getId());
         } finally {
            if (member != null) {
               // Delete
               assertTrue(lbaasApi.deleteMember(member.getId()));
               Member memberGet = lbaasApi.getMember(member.getId());
               assertNull(memberGet);

               // Verify member does not appear in pool1 and in pool2
               Pool pool1Get = lbaasApi.getPool(pool1.getId());
               assertNotNull(pool1Get);
               assertNotNull(pool1Get.getMembers());
               assertTrue(pool1Get.getMembers().isEmpty());
               Pool pool2Get = lbaasApi.getPool(pool2.getId());
               assertNotNull(pool2Get);
               assertNotNull(pool2Get.getMembers());
               assertTrue(pool2Get.getMembers().isEmpty());
            }
            if (pool2 != null) {
               assertTrue(lbaasApi.deletePool(pool2.getId()));
            }
            if (pool1 != null) {
               assertTrue(lbaasApi.deletePool(pool1.getId()));
            }
         }
      }
   }

   public void testCreateUpdateAndDeleteVIP() {
      for (String region : api.getConfiguredRegions()) {
         Optional<LBaaSApi> lbaasApiExtension = api.getLBaaSApi(region);
         if (!lbaasApiExtension.isPresent()) {
            continue;
         }
         LBaaSApi lbaasApi = lbaasApiExtension.get();

         Subnet subnet = subnets.get(region);
         Pool pool1 = null;
         Pool pool2 = null;
         VIP vip = null;

         try {
            // Create pools
            Pool.CreateBuilder createBuilder = Pool.createBuilder(subnet.getId(), Protocol.HTTP, Pool.ROUND_ROBIN)
                  .name("jclouds-lbaas-test-vip-pool-1").description(null).healthMonitors(null).provider(null).adminStateUp(null);
            pool1 = lbaasApi.createPool(createBuilder.build());
            assertNotNull(pool1);
            createBuilder.name("jclouds-lbaas-test-vip-pool-2");
            pool2 = lbaasApi.createPool(createBuilder.build());
            assertNotNull(pool2);

            // Create
            VIP.CreateVIP createVIP = VIP.createBuilder(subnet.getId(), Protocol.HTTP, 80, pool1.getId())
                  .name("jclouds-lbaas-test-vip").description(null).address(null).sessionPersistence(null).connectionLimit(null).build();
            vip = lbaasApi.createVIP(createVIP);
            assertNotNull(vip);
            assertNotNull(vip.getId());
            assertEquals(vip.getTenantId(), subnet.getTenantId());
            assertEquals(vip.getName(), "jclouds-lbaas-test-vip");
            assertEquals(vip.getDescription(), "");
            assertEquals(vip.getSubnetId(), subnet.getId());
            assertNotNull(vip.getAddress());
            assertNotNull(vip.getPortId());
            assertEquals(vip.getProtocol(), Protocol.HTTP);
            assertEquals(vip.getProtocolPort(), Integer.valueOf(80));
            assertEquals(vip.getPoolId(), pool1.getId());
            assertNull(vip.getSessionPersistence());
            assertEquals(vip.getConnectionLimit(), Integer.valueOf(-1));
            assertEquals(vip.getAdminStateUp(), Boolean.TRUE);
            assertTrue(vip.getStatus() == LBaaSStatus.PENDING_CREATE || vip.getStatus() == LBaaSStatus.ACTIVE);
            assertNull(vip.getStatusDescription());

            // List and Get
            VIPs vips = lbaasApi.listVIPs(PaginationOptions.Builder.queryParameters(ImmutableMap.of("tenant_id", subnet.getTenantId()).asMultimap()));
            assertNotNull(vips);
            assertFalse(vips.isEmpty());
            VIP vipList = vips.first().get();
            VIP vipGet = lbaasApi.getVIP(vipList.getId());
            assertNotNull(vipGet);
            assertEquals(vipGet, vipList);

            vipGet = lbaasApi.getVIP(vip.getId());
            assertNotNull(vipGet);
            assertEquals(vipGet.getId(), vip.getId());

            // Verify VIP appears in pool1 and not in pool2
            Pool pool1Get = lbaasApi.getPool(pool1.getId());
            assertNotNull(pool1Get);
            assertEquals(pool1Get.getVIPId(), vip.getId());
            Pool pool2Get = lbaasApi.getPool(pool2.getId());
            assertNotNull(pool2Get);
            assertNotEquals(pool2Get.getVIPId(), vip.getId());

            // Update
            SessionPersistence sessionPersistence = SessionPersistence.builder().type(SessionPersistence.Type.HTTP_COOKIE).cookieName(null).build();
            VIP.UpdateVIP updateVIP = VIP.updateBuilder()
                  .name("jclouds-lbaas-test-vip-renamed").description("new description").poolId(pool2.getId())
                  .sessionPersistence(sessionPersistence).connectionLimit(2).adminStateUp(Boolean.FALSE).build();
            VIP vipUpdate = lbaasApi.updateVIP(vip.getId(), updateVIP);
            assertNotNull(vipUpdate);
            assertEquals(vipUpdate.getName(), "jclouds-lbaas-test-vip-renamed");
            assertEquals(vipUpdate.getDescription(), "new description");
            assertEquals(vipUpdate.getPoolId(), pool2.getId());
            assertEquals(vipUpdate.getSessionPersistence(), sessionPersistence);
            assertEquals(vipUpdate.getConnectionLimit(), Integer.valueOf(2));
            assertEquals(vipUpdate.getAdminStateUp(), Boolean.FALSE);

            vipGet = lbaasApi.getVIP(vip.getId());
            assertNotNull(vipGet);
            assertNotNull(vipGet.getId());
            assertEquals(vipGet.getTenantId(), subnet.getTenantId());
            assertEquals(vipGet.getName(), "jclouds-lbaas-test-vip-renamed");
            assertEquals(vipGet.getDescription(), "new description");
            assertEquals(vipGet.getSubnetId(), subnet.getId());
            assertNotNull(vipGet.getAddress());
            assertNotNull(vipGet.getPortId());
            assertEquals(vipGet.getProtocol(), Protocol.HTTP);
            assertEquals(vipGet.getProtocolPort(), Integer.valueOf(80));
            assertEquals(vipGet.getPoolId(), pool2.getId());
            assertEquals(vipGet.getSessionPersistence(), sessionPersistence);
            assertEquals(vipGet.getConnectionLimit(), Integer.valueOf(2));
            assertEquals(vipGet.getAdminStateUp(), Boolean.FALSE);
            assertTrue(vipGet.getStatus() == LBaaSStatus.PENDING_UPDATE || vipGet.getStatus() == LBaaSStatus.ACTIVE);
            assertNull(vipGet.getStatusDescription());

            // Verify VIP appears in pool2 and not in pool1
            pool1Get = lbaasApi.getPool(pool1.getId());
            assertNotNull(pool1Get);
            assertNotEquals(pool1Get.getVIPId(), vip.getId());
            pool2Get = lbaasApi.getPool(pool2.getId());
            assertNotNull(pool2Get);
            assertEquals(pool2Get.getVIPId(), vip.getId());
         } finally {
            if (vip != null) {
               // Delete
               assertTrue(lbaasApi.deleteVIP(vip.getId()));
               VIP vipGet = lbaasApi.getVIP(vip.getId());
               assertNull(vipGet);

               // Verify VIP does not appear in pool1 and in pool2
               Pool pool1Get = lbaasApi.getPool(pool1.getId());
               assertNotNull(pool1Get);
               assertNotEquals(pool1Get.getVIPId(), vip.getId());
               Pool pool2Get = lbaasApi.getPool(pool2.getId());
               assertNotNull(pool2Get);
               assertNotEquals(pool2Get.getVIPId(), vip.getId());
            }
            if (pool2 != null) {
               assertTrue(lbaasApi.deletePool(pool2.getId()));
            }
            if (pool1 != null) {
               assertTrue(lbaasApi.deletePool(pool1.getId()));
            }
         }
      }
   }

   public void testCreateUpdateAndDeleteHealthMonitor() {
      for (String region : api.getConfiguredRegions()) {
         Optional<LBaaSApi> lbaasApiExtension = api.getLBaaSApi(region);
         if (!lbaasApiExtension.isPresent()) {
            continue;
         }
         LBaaSApi lbaasApi = lbaasApiExtension.get();

         Subnet subnet = subnets.get(region);
         HealthMonitor healthMonitor = null;

         try {
            // Create
            HealthMonitor.CreateHealthMonitor createHealthMonitor = HealthMonitor.createBuilder(ProbeType.HTTP, Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1))
                  .httpMethod(null).urlPath(null).expectedCodes(null).adminStateUp(null).build();
            healthMonitor = lbaasApi.createHealthMonitor(createHealthMonitor);
            assertNotNull(healthMonitor);
            assertNotNull(healthMonitor.getId());
            assertEquals(healthMonitor.getTenantId(), subnet.getTenantId());
            assertEquals(healthMonitor.getType(), ProbeType.HTTP);
            assertEquals(healthMonitor.getDelay(), Integer.valueOf(1));
            assertEquals(healthMonitor.getTimeout(), Integer.valueOf(1));
            assertEquals(healthMonitor.getMaxRetries(), Integer.valueOf(1));
            assertEquals(healthMonitor.getHttpMethod(), HttpMethod.GET);
            assertEquals(healthMonitor.getUrlPath(), "/");
            assertEquals(healthMonitor.getExpectedCodes(), "200");
            assertNotNull(healthMonitor.getPools());
            assertTrue(healthMonitor.getPools().isEmpty());
            assertEquals(healthMonitor.getAdminStateUp(), Boolean.TRUE);
            //assertEquals(healthMonitor.getStatus(), LBaaSStatus.PENDING_CREATE);
            assertNull(healthMonitor.getStatus());
            assertNull(healthMonitor.getStatusDescription());

            // List and Get
            HealthMonitors healthMonitors = lbaasApi.listHealthMonitors(PaginationOptions.Builder.queryParameters(ImmutableMap.of("tenant_id", subnet.getTenantId()).asMultimap()));
            assertNotNull(healthMonitors);
            assertFalse(healthMonitors.isEmpty());
            HealthMonitor healthMonitorList = healthMonitors.first().get();
            HealthMonitor healthMonitorGet = lbaasApi.getHealthMonitor(healthMonitorList.getId());
            assertNotNull(healthMonitorGet);
            assertEquals(healthMonitorGet, healthMonitorList);

            healthMonitorGet = lbaasApi.getHealthMonitor(healthMonitor.getId());
            assertNotNull(healthMonitorGet);
            assertEquals(healthMonitorGet.getId(), healthMonitor.getId());

            // Update
            HealthMonitor.UpdateHealthMonitor updateHealthMonitor = HealthMonitor.updateBuilder().delay(Integer.valueOf(2)).timeout(Integer.valueOf(2)).maxRetries(Integer.valueOf(2))
                  .httpMethod(HttpMethod.HEAD).urlPath("/index.html").expectedCodes("201").adminStateUp(Boolean.FALSE).build();
            HealthMonitor healthMonitorUpdate = lbaasApi.updateHealthMonitor(healthMonitor.getId(), updateHealthMonitor);
            assertNotNull(healthMonitorUpdate);
            assertEquals(healthMonitorUpdate.getDelay(), Integer.valueOf(2));
            assertEquals(healthMonitorUpdate.getTimeout(), Integer.valueOf(2));
            assertEquals(healthMonitorUpdate.getMaxRetries(), Integer.valueOf(2));
            assertEquals(healthMonitorUpdate.getHttpMethod(), HttpMethod.HEAD);
            assertEquals(healthMonitorUpdate.getUrlPath(), "/index.html");
            assertEquals(healthMonitorUpdate.getExpectedCodes(), "201");
            assertEquals(healthMonitorUpdate.getAdminStateUp(), Boolean.FALSE);

            healthMonitorGet = lbaasApi.getHealthMonitor(healthMonitor.getId());
            assertNotNull(healthMonitorGet);
            assertNotNull(healthMonitorGet.getId());
            assertEquals(healthMonitorGet.getTenantId(), subnet.getTenantId());
            assertEquals(healthMonitorGet.getType(), ProbeType.HTTP);
            assertEquals(healthMonitorGet.getDelay(), Integer.valueOf(2));
            assertEquals(healthMonitorGet.getTimeout(), Integer.valueOf(2));
            assertEquals(healthMonitorGet.getMaxRetries(), Integer.valueOf(2));
            assertEquals(healthMonitorGet.getHttpMethod(), HttpMethod.HEAD);
            assertEquals(healthMonitorGet.getUrlPath(), "/index.html");
            assertEquals(healthMonitorGet.getExpectedCodes(), "201");
            assertNotNull(healthMonitorGet.getPools());
            assertTrue(healthMonitorGet.getPools().isEmpty());
            assertEquals(healthMonitorGet.getAdminStateUp(), Boolean.FALSE);
            //assertEquals(healthMonitorGet.getStatus(), LBaaSStatus.PENDING_UPDATE);
            assertNull(healthMonitorGet.getStatus());
            assertNull(healthMonitorGet.getStatusDescription());
         } finally {
            if (healthMonitor != null) {
               // Delete
               assertTrue(lbaasApi.deleteHealthMonitor(healthMonitor.getId()));
               HealthMonitor healthMonitorGet = lbaasApi.getHealthMonitor(healthMonitor.getId());
               assertNull(healthMonitorGet);
            }
         }
      }
   }

   public void testAssociateAndDisassociateHealthMonitorWithPool() {
      for (String region : api.getConfiguredRegions()) {
         Optional<LBaaSApi> lbaasApiExtension = api.getLBaaSApi(region);
         if (!lbaasApiExtension.isPresent()) {
            continue;
         }
         LBaaSApi lbaasApi = lbaasApiExtension.get();

         Subnet subnet = subnets.get(region);
         Pool pool = null;
         HealthMonitor healthMonitor = null;

         try {
            // Create pool
            Pool.CreatePool poolCreateOptions = Pool.createBuilder(subnet.getId(), Protocol.TCP, Pool.ROUND_ROBIN)
                  .name("jclouds-lbaas-test-pool-association").build();
            pool = lbaasApi.createPool(poolCreateOptions);
            assertNotNull(pool);
            assertNotNull(pool.getId());
            assertNotNull(pool.getHealthMonitors());
            assertTrue(pool.getHealthMonitors().isEmpty());
            assertNotNull(pool.getHealthMonitorsStatus());
            assertTrue(pool.getHealthMonitorsStatus().isEmpty());

            // Create health monitor
            HealthMonitor.CreateHealthMonitor healthMonitorCreateOptions = HealthMonitor.createBuilder(ProbeType.HTTP, Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1)).build();
            healthMonitor = lbaasApi.createHealthMonitor(healthMonitorCreateOptions);
            assertNotNull(healthMonitor);
            assertNotNull(healthMonitor.getId());
            assertNotNull(healthMonitor.getPools());
            assertTrue(healthMonitor.getPools().isEmpty());

            // Associate health monitor with pool
            HealthMonitor healthMonitorAssociated = lbaasApi.associateHealthMonitor(pool.getId(), healthMonitor.getId());
            assertNotNull(healthMonitorAssociated);

            // Verify health monitor is associated with pool
            Pool poolGet = lbaasApi.getPool(pool.getId());
            assertNotNull(poolGet);
            assertNotNull(poolGet.getHealthMonitors());
            assertEquals(poolGet.getHealthMonitors().size(), 1);
            assertEquals(poolGet.getHealthMonitors().iterator().next(), healthMonitor.getId());
            assertNotNull(poolGet.getHealthMonitorsStatus());
            assertEquals(poolGet.getHealthMonitorsStatus().size(), 1);
            assertEquals(poolGet.getHealthMonitorsStatus().iterator().next().getId(), healthMonitor.getId());

            HealthMonitor healthMonitorGet = lbaasApi.getHealthMonitor(healthMonitor.getId());
            assertNotNull(healthMonitorGet);
            assertNotNull(healthMonitorGet.getPools());
            assertEquals(healthMonitorGet.getPools().size(), 1);
            assertEquals(healthMonitorGet.getPools().iterator().next().getId(), pool.getId());

            // Disassociate health monitor from pool
            assertTrue(lbaasApi.disassociateHealthMonitor(pool.getId(), healthMonitor.getId()));

            // Verify health monitor is disassociated from pool
            poolGet = lbaasApi.getPool(pool.getId());
            assertNotNull(poolGet);
            assertNotNull(poolGet.getHealthMonitors());
            assertTrue(poolGet.getHealthMonitors().isEmpty());
            assertNotNull(poolGet.getHealthMonitorsStatus());
            assertTrue(poolGet.getHealthMonitorsStatus().isEmpty());

            healthMonitorGet = lbaasApi.getHealthMonitor(healthMonitor.getId());
            assertNotNull(healthMonitorGet);
            assertNotNull(healthMonitorGet.getPools());
            assertTrue(healthMonitorGet.getPools().isEmpty());
         } finally {
            if (healthMonitor != null) {
               assertTrue(lbaasApi.deleteHealthMonitor(healthMonitor.getId()));
            }
            if (pool != null) {
               assertTrue(lbaasApi.deletePool(pool.getId()));
            }
         }
      }
   }

}
