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
package org.jclouds.digitalocean2.compute.functions;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.digitalocean2.DigitalOcean2ProviderMetadata;
import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "RegionToLocationTest")
public class RegionToLocationTest {

   @Test
   public void testConvertRegion() {
      DigitalOcean2ProviderMetadata metadata = new DigitalOcean2ProviderMetadata();
      JustProvider locationsSupplier = new JustProvider(metadata.getId(), Suppliers.<URI> ofInstance(URI
            .create(metadata.getEndpoint())), ImmutableSet.<String> of());

      Region region = Region.create("reg1", "Region1", ImmutableList.<String> of(), true,
            ImmutableList.<String> of("virtio", "metadata"));
      Location expected = new LocationBuilder().id("reg1").description("reg1/Region 1")
            .parent(getOnlyElement(locationsSupplier.get())).scope(LocationScope.REGION).build();

      Location location = new RegionToLocation(locationsSupplier).apply(region);

      assertEquals(location, expected);
      assertEquals(location.getMetadata().get("available"), true);
      assertEquals(location.getMetadata().get("features"), ImmutableList.of("virtio", "metadata"));
   }
}
