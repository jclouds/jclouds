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

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

@Test
public class ZoneIdToURIFromJoinOnRegionIdToURITest {

   public void zoneToRegionMappingsValid() {
      Map<String, Supplier<URI>> regionIdToURIs = Maps.newLinkedHashMap();
      regionIdToURIs.put("us-east-1", Suppliers.ofInstance(URI.create("ec2.us-east-1.amazonaws.com")));
      regionIdToURIs.put("eu-central-1", Suppliers.ofInstance(URI.create("ec2.eu-central-1.amazonaws.com")));
      Map<String, Supplier<Set<String>>> regionIdToZoneIds = Maps.newLinkedHashMap();
      regionIdToZoneIds.put("us-east-1", supplyZoneIds("us-east-1a", "us-east-1b"));
      regionIdToZoneIds.put("eu-central-1", supplyZoneIds("eu-central-1a"));

      Map<String, Supplier<URI>> result = new ZoneIdToURIFromJoinOnRegionIdToURI(Suppliers.ofInstance(regionIdToURIs),
            Suppliers.ofInstance(regionIdToZoneIds)).get();

      assertEquals(result.size(), 3);
      assertEquals(result.get("us-east-1a"), regionIdToURIs.get("us-east-1"));
      assertEquals(result.get("us-east-1b"), regionIdToURIs.get("us-east-1"));
      assertEquals(result.get("eu-central-1a"), regionIdToURIs.get("eu-central-1"));
   }

   @Test(expectedExceptions = IllegalStateException.class,
         expectedExceptionsMessageRegExp = "region eu-central-1 is not in the configured region to zone mappings: .*")
   public void zoneToRegionMappingsInconsistentOnKeys() {
      Map<String, Supplier<URI>> regionIdToURIs = Maps.newLinkedHashMap();
      regionIdToURIs.put("us-east-1", Suppliers.ofInstance(URI.create("ec2.us-east-1.amazonaws.com")));
      regionIdToURIs.put("eu-central-1", Suppliers.ofInstance(URI.create("ec2.eu-central-1.amazonaws.com")));
      Map<String, Supplier<Set<String>>> regionIdToZoneIds = Maps.newLinkedHashMap();
      regionIdToZoneIds.put("us-east-1", supplyZoneIds("us-east-1a", "us-east-1b"));
      // missing regionIdToZoneIds mapping for eu-central-1

      new ZoneIdToURIFromJoinOnRegionIdToURI(Suppliers.ofInstance(regionIdToURIs),
            Suppliers.ofInstance(regionIdToZoneIds)).get();
   }

   private static Supplier<Set<String>> supplyZoneIds(String... zoneIds) {
      return Suppliers.<Set<String>>ofInstance(ImmutableSet.copyOf(zoneIds));
   }
}
