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
package org.jclouds.softlayer.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import java.util.Set;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.PowerState;
import org.jclouds.softlayer.domain.SoftwareDescription;
import org.jclouds.softlayer.domain.SoftwareLicense;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;

/**
 * Tests the function that transforms SoftLayer VirtualGuest to NodeMetadata.
 */
@Test(groups = "unit", testName = "VirtualGuestToNodeMetadataTest")
public class VirtualGuestToNodeMetadataTest {

   VirtualGuestToImage virtualGuestToImage = Guice.createInjector().getInstance(VirtualGuestToImage.class);
   VirtualGuestToHardware virtualGuestToHardware = Guice.createInjector().getInstance(VirtualGuestToHardware.class);
   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

   Location location = new LocationBuilder().id("test")
                                            .description("example")
                                            .scope(LocationScope.ZONE)
                                            .build();
   Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.of(location));

   @Test
   public void testVirtualGuestToNodeMetadata() {

      VirtualGuest virtualGuest = createVirtualGuest();
      NodeMetadata nodeMetadata = new VirtualGuestToNodeMetadata(locationSupplier, namingConvention,
              virtualGuestToImage, virtualGuestToHardware).apply(virtualGuest);
      assertNotNull(nodeMetadata);
      assertEquals(nodeMetadata.getName(), virtualGuest.getHostname());
      assertNotNull(nodeMetadata.getLocation());
      assertEquals(nodeMetadata.getLocation().getId(), location.getId());
      assertEquals(nodeMetadata.getHostname(), virtualGuest.getFullyQualifiedDomainName());
      assertEquals(nodeMetadata.getHardware().getRam(), virtualGuest.getMaxMemory());
      assertTrue(nodeMetadata.getHardware().getProcessors().size() == 1);
      assertEquals(Iterables.get(nodeMetadata.getHardware().getProcessors(), 0).getCores(), (double) virtualGuest.getStartCpus());
      assertEquals(nodeMetadata.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(nodeMetadata.getOperatingSystem().getVersion(), "12.04");
      assertEquals(nodeMetadata.getOperatingSystem().is64Bit(), true);
   }

   private VirtualGuest createVirtualGuest() {
      return VirtualGuest.builder()
              .domain("example.com")
              .hostname("host1")
              .fullyQualifiedDomainName("host1.example.com")
              .id(1301396)
              .maxMemory(1024)
              .startCpus(1)
              .localDiskFlag(true)
              .operatingSystem(OperatingSystem.builder().id("UBUNTU_LATEST")
                      .operatingSystemReferenceCode("UBUNTU_LATEST")
                      .softwareLicense(SoftwareLicense.builder()
                              .softwareDescription(SoftwareDescription.builder()
                                      .version("12.04-64 Minimal for CCI")
                                      .referenceCode("UBUNTU_12_64")
                                      .longDescription("Ubuntu Linux 12.04 LTS Precise Pangolin - Minimal Install (64 bit)")
                                      .build())
                              .build())
                      .build())
              .datacenter(Datacenter.builder().name("test").build())
              .powerState(PowerState.builder().keyName(VirtualGuest.State.RUNNING).build())
              .build();
   }

}
