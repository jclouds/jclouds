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
package org.jclouds.location.suppliers.derived;

import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public final class ZoneIdToURIFromJoinOnRegionIdToURI implements ZoneIdToURISupplier {

   private final Supplier<Map<String, Supplier<URI>>> regionIdToURIs;
   private final Supplier<Map<String, Supplier<Set<String>>>> regionIdToZoneIds;

   @Inject
   ZoneIdToURIFromJoinOnRegionIdToURI(@Region Supplier<Map<String, Supplier<URI>>> regionIdToURIs,
         @Zone Supplier<Map<String, Supplier<Set<String>>>> regionIdToZoneIds) {
      this.regionIdToURIs = regionIdToURIs;
      this.regionIdToZoneIds = regionIdToZoneIds;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      Map<String, Supplier<Set<String>>> regionIdToZoneIds = this.regionIdToZoneIds.get();
      Builder<String, Supplier<URI>> builder = ImmutableMap.builder();
      for (Entry<String, Supplier<URI>> regionToURI : regionIdToURIs.get().entrySet()) {
         Supplier<Set<String>> zoneIds = regionIdToZoneIds.get(regionToURI.getKey());
         checkState(zoneIds != null, "region %s is not in the configured region to zone mappings: %s",
               regionToURI.getKey(), regionIdToZoneIds);
         for (String zone : zoneIds.get()) {
            builder.put(zone, regionToURI.getValue());
         }
      }
      return builder.build();
   }
}
