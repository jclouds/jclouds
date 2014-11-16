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

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.ec2.EC2Api;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.fromconfig.RegionIdsFromConfiguration;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * Uses the {@code DescribeRegions} call to return the regions endpoints, subject to any whitelist present in the
 * property {@link org.jclouds.location.reference.LocationConstants#PROPERTY_REGIONS}.
 */
public final class DescribeRegionsForRegionURIs implements RegionIdToURISupplier {
   private final EC2Api api;
   private final Set<String> whitelistedRegionIds;

   @Inject DescribeRegionsForRegionURIs(EC2Api api, RegionIdsFromConfiguration regionIdsFromConfiguration) {
      this.api = api;
      this.whitelistedRegionIds = regionIdsFromConfiguration.get();
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      ImmutableMap.Builder<String, Supplier<URI>> result = ImmutableMap.builder();
      for (Entry<String, URI> regionUrl : api.getAvailabilityZoneAndRegionApi().get().describeRegions().entrySet()) {
         if (whitelistedRegionIds.isEmpty() || whitelistedRegionIds.contains(regionUrl.getKey())) {
            result.put(regionUrl.getKey(), Suppliers.ofInstance(regionUrl.getValue()));
         }
      }
      return result.build();
   }
}
