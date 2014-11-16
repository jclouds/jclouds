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
package org.jclouds.ec2.suppliers;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.features.AvailabilityZoneAndRegionApi;
import org.jclouds.location.Region;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public final class DescribeAvailabilityZonesInRegion implements RegionIdToZoneIdsSupplier {
   private final EC2Api api;
   private final Supplier<Set<String>> regions;

   @Inject
   DescribeAvailabilityZonesInRegion(EC2Api api, @Region Supplier<Set<String>> regions) {
      this.api = api;
      this.regions = regions;
   }

   @Override
   public Map<String, Supplier<Set<String>>> get() {
      AvailabilityZoneAndRegionApi zoneApi = api.getAvailabilityZoneAndRegionApi().get();
      Builder<String, Supplier<Set<String>>> map = ImmutableMap.builder();
      for (String region : regions.get()) {
         ImmutableSet.Builder<String> zoneBuilder = ImmutableSet.builder();
         for (AvailabilityZoneInfo zone : zoneApi.describeAvailabilityZonesInRegion(region)) {
            zoneBuilder.add(zone.getZone());
         }
         map.put(region, Suppliers.<Set<String>>ofInstance(zoneBuilder.build()));
      }
      return map.build();
   }
}
