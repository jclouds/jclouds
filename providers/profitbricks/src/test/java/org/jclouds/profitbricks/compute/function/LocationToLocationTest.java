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

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.profitbricks.ProfitBricksProviderMetadata;
import org.jclouds.profitbricks.domain.Location;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@Test(groups = "unit", testName = "LocationToLocationTest")
public class LocationToLocationTest {

   private LocationToLocation fnRegion;
   private JustProvider justProvider;

   @BeforeTest
   public void setup() {
      ProfitBricksProviderMetadata metadata = new ProfitBricksProviderMetadata();
      this.justProvider = new JustProvider(metadata.getId(), Suppliers.<URI>ofInstance(
              URI.create(metadata.getEndpoint())), ImmutableSet.<String>of());
      this.fnRegion = new LocationToLocation(justProvider);
   }

   @Test
   public void testLocationToLocation() {
      Location[] locations = Location.values();
      for (Location loc : locations) {
         org.jclouds.domain.Location actual = fnRegion.apply(loc);
         org.jclouds.domain.Location expected = new LocationBuilder()
                 .id(loc.getId()).description(loc.getDescription()).scope(LocationScope.REGION)
                 .parent(Iterables.getOnlyElement(justProvider.get())).build();

         assertEquals(actual, expected);
      }

   }
}
