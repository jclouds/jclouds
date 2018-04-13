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
package org.jclouds.location.suppliers.all;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "ZoneToRegionToProviderOrJustProviderTest")
public class ZoneToRegionToProviderOrJustProviderTest {
   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("provider").description("provider")
            .iso3166Codes(ImmutableSet.of("US")).build();
   JustProvider justProvider = new JustProvider("provider", Suppliers.ofInstance(URI.create("http://localhost")), ImmutableSet.of("US"));
   
   Supplier<Map<String, Supplier<Set<String>>>> regionToZones = Suppliers.<Map<String, Supplier<Set<String>>>>ofInstance(
         ImmutableMap.of(
               "region1", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("zone1")),
               "region2", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("zone2", "zone3"))
         ));
   Supplier<Map<String, Supplier<Set<String>>>> locationToIsoCodes = Suppliers.<Map<String, Supplier<Set<String>>>>ofInstance(
         ImmutableMap.of(
               "region1", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("US")),
               "region2", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("US")),
               "zone1", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("US-CA")),
               "zone2", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("US-VA")),
               "zone3", Suppliers.<Set<String>>ofInstance(ImmutableSet.of("US-AK"))
               ));
   
   Location region1 = new LocationBuilder().scope(LocationScope.REGION).id("region1").description("region1").iso3166Codes(ImmutableSet.of("US")).parent(provider).build();
   Location region2 = new LocationBuilder().scope(LocationScope.REGION).id("region2").description("region2").iso3166Codes(ImmutableSet.of("US")).parent(provider).build();
   Location zone1 = new LocationBuilder().scope(LocationScope.ZONE).id("zone1").description("zone1").iso3166Codes(ImmutableSet.of("US-CA")).parent(region1).build();
   Location zone2 = new LocationBuilder().scope(LocationScope.ZONE).id("zone2").description("zone2").iso3166Codes(ImmutableSet.of("US-VA")).parent(region2).build();
   Location zone3 = new LocationBuilder().scope(LocationScope.ZONE).id("zone3").description("zone3").iso3166Codes(ImmutableSet.of("US-AK")).parent(region2).build();

   @Test
   public void testGetAll() {
      Supplier<Set<String>> regionIdsSupplier = Suppliers.<Set<String>> ofInstance(ImmutableSet.of("region1", "region2"));
      Supplier<Set<String>> zoneIdsSupplier = Suppliers.<Set<String>> ofInstance(ImmutableSet.of("zone1", "zone2", "zone3"));
      
      RegionToProviderOrJustProvider regionToProviderOrJustProvider = new RegionToProviderOrJustProvider(justProvider, regionIdsSupplier, locationToIsoCodes);
      ZoneToRegionToProviderOrJustProvider fn = new ZoneToRegionToProviderOrJustProvider(regionToProviderOrJustProvider, zoneIdsSupplier, locationToIsoCodes, regionToZones);
      
      assertEquals(fn.get(), ImmutableSet.of(region1, region2, zone1, zone2, zone3));
   }
   
   @Test
   public void testRegionAndZoneFilter() {
      Supplier<Set<String>> regionIdsSupplier = Suppliers.<Set<String>> ofInstance(ImmutableSet.of("region2"));
      Supplier<Set<String>> zoneIdsSupplier = Suppliers.<Set<String>> ofInstance(ImmutableSet.<String> of("zone2"));
      
      RegionToProviderOrJustProvider regionToProviderOrJustProvider = new RegionToProviderOrJustProvider(justProvider, regionIdsSupplier, locationToIsoCodes);
      ZoneToRegionToProviderOrJustProvider fn = new ZoneToRegionToProviderOrJustProvider(regionToProviderOrJustProvider, zoneIdsSupplier, locationToIsoCodes, regionToZones);
      
      assertEquals(fn.get(), ImmutableSet.of(region2, zone2));
   }
}
