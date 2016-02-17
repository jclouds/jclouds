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
package org.jclouds.profitbricks.compute.function;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.profitbricks.domain.Location.DE_FRA;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.ProfitBricksApiMetadata;
import org.jclouds.profitbricks.domain.AvailabilityZone;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.features.DataCenterApi;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

@Test(groups = "unit", testName = "ServerToNodeMetadataTest")
public class ServerToNodeMetadataTest {

   private ServerToNodeMetadata fnNodeMetadata;
   
   private ProfitBricksApi api;
   
   private DataCenterApi dataCenterApi;

   @BeforeMethod
   public void setup() {
      Supplier<Set<? extends Location>> locationsSupply = new Supplier<Set<? extends Location>>() {
         @Override
         public Set<? extends Location> get() {
            return ImmutableSet.of(
                    new LocationBuilder()
                    .id("de/fra")
                    .description("de/fra")
                    .scope(LocationScope.ZONE)
                    .parent(new LocationBuilder()
                            .id("de")
                            .description("de")
                            .scope(LocationScope.REGION)
                            .build())
                    .build());
         }
      };

      GroupNamingConvention.Factory namingConvention = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new ProfitBricksApiMetadata().getDefaultProperties());
         }
      }).getInstance(GroupNamingConvention.Factory.class);
      
      dataCenterApi = EasyMock.createMock(DataCenterApi.class);
      api = EasyMock.createMock(ProfitBricksApi.class);

      expect(dataCenterApi.getDataCenter("mock")).andReturn(
            DataCenter.builder().id("mock").version(10).location(DE_FRA).build());
      expect(api.dataCenterApi()).andReturn(dataCenterApi);
      
      replay(dataCenterApi, api);

      this.fnNodeMetadata = new ServerToNodeMetadata(new StorageToVolume(), locationsSupply, api, namingConvention);
   }
   
   @AfterMethod
   public void tearDown() {
      verify(api, dataCenterApi);
   }

   @Test
   public void testServerToNodeMetadata() {
      Server server = Server.builder()
              .dataCenter(DataCenter.builder()
                      .id("mock")
                      .version(10)
                      .location(org.jclouds.profitbricks.domain.Location.DE_FRA)
                      .build())
              .id("qwertyui-qwer-qwer-qwer-qwertyyuiiop")
              .name("mock-facebook-node")
              .cores(4)
              .ram(4096)
              .hasInternetAccess(true)
              .state(ProvisioningState.AVAILABLE)
              .status(Server.Status.RUNNING)
              .osType(OsType.LINUX)
              .availabilityZone(AvailabilityZone.AUTO)
              .isCpuHotPlug(true)
              .isRamHotPlug(true)
              .isNicHotPlug(true)
              .isNicHotUnPlug(true)
              .isDiscVirtioHotPlug(true)
              .isDiscVirtioHotUnPlug(true)
              .storages(ImmutableList.<Storage>of(
                              Storage.builder()
                              .bootDevice(true)
                              .busType(Storage.BusType.VIRTIO)
                              .deviceNumber(1)
                              .size(40f)
                              .id("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
                              .name("facebook-storage")
                              .build()
                      )
              )
              .nics(ImmutableList.<Nic>of(
                              Nic.builder()
                              .id("qwqwqwqw-wewe-erer-rtrt-tytytytytyty")
                              .lanId(1)
                              .dataCenterId("12345678-abcd-efgh-ijkl-987654321000")
                              .internetAccess(true)
                              .serverId("qwertyui-qwer-qwer-qwer-qwertyyuiiop")
                              .macAddress("02:01:09:cd:f0:b0")
                              .ips( ImmutableList.<String>of("173.252.120.6"))
                              .build()
                      )
              )
              .build();

      NodeMetadata expected = fnNodeMetadata.apply(server);
      assertNotNull(expected);

      NodeMetadata actual = new NodeMetadataBuilder()
              .group("mock")
              .ids(server.id())
              .name(server.name())
              .backendStatus("AVAILABLE")
              .status(NodeMetadata.Status.RUNNING)
              .hardware(new HardwareBuilder()
                      .ids("cpu=4,ram=4096,disk=40")
                      .name("cpu=4,ram=4096,disk=40")
                      .ram(server.ram())
                      .processor(new Processor(server.cores(), 1d))
                      .hypervisor("kvm")
                      .volume(new VolumeBuilder()
                              .bootDevice(true)
                              .size(40f)
                              .id("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
                              .durable(true)
                              .type(Volume.Type.LOCAL)
                              .build())
                      .build())
              .operatingSystem(new OperatingSystem.Builder()
                      .description(OsFamily.LINUX.value())
                      .family(OsFamily.LINUX)
                      .build())
              .location(new LocationBuilder()
                      .id("de/fra")
                      .description("de/fra")
                      .scope(LocationScope.ZONE)
                      .parent(new LocationBuilder()
                              .id("de")
                              .description("de")
                              .scope(LocationScope.REGION)
                              .build())
                      .build())
              .publicAddresses(ImmutableList.<String>of("173.252.120.6"))
              .build();

      assertEquals(actual, expected);
   }
}
