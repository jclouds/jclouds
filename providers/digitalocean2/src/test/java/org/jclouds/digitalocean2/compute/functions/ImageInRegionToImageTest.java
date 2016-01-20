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

import static org.jclouds.compute.domain.Image.Status.AVAILABLE;
import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.Set;

import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.digitalocean2.compute.internal.ImageInRegion;
import org.jclouds.digitalocean2.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@Test(groups = "unit", testName = "ImageToImageTest")
public class ImageInRegionToImageTest {

   private Set<Location> locations;

   private ImageInRegionToImage function;

   @BeforeMethod
   public void setup() {
      locations = ImmutableSet.of(
            new LocationBuilder()
                  .id("sfo1")
                  .description("sfo1/San Francisco 1")
                  .scope(LocationScope.REGION)
                  .parent(
                        new LocationBuilder().id("0").description("mock parent location").scope(LocationScope.PROVIDER)
                              .build()).build(),
            new LocationBuilder()
                  .id("lon1")
                  .description("lon1/London 1")
                  .scope(LocationScope.REGION)
                  .parent(
                        new LocationBuilder().id("0").description("mock parent location").scope(LocationScope.PROVIDER)
                              .build()).build());

      function = new ImageInRegionToImage(new Supplier<Set<? extends Location>>() {
         @Override
         public Set<? extends Location> get() {
            return locations;
         }
      });
   }

   @Test
   public void testConvertImage() {
      Image image = Image.create(1, "14.04 x64", "distribution", "Ubuntu", "ubuntu-1404-x86", true,
            ImmutableList.of("sfo1", "lon1"), new Date());
      org.jclouds.compute.domain.Image expected = new ImageBuilder()
            .id("lon1/ubuntu-1404-x86") // Location scoped images have the location encoded in the id
            .providerId("1")
            .name("14.04 x64")
            .description("Ubuntu 14.04 x64")
            .status(AVAILABLE)
            .operatingSystem(
                  OperatingSystem.builder().name("Ubuntu").description("Ubuntu 14.04 x64").family(OsFamily.UBUNTU)
                        .version("14.04").arch("x64").is64Bit(true).build())
            .location(Iterables.get(locations, 1))
            .userMetadata(ImmutableMap.of("publicImage", "true")).build();

      org.jclouds.compute.domain.Image result = function.apply(ImageInRegion.create(image, "lon1"));
      assertEquals(result, expected);
      assertEquals(result.getDescription(), expected.getDescription());
      assertEquals(result.getOperatingSystem(), expected.getOperatingSystem());
      assertEquals(result.getStatus(), expected.getStatus());
      assertEquals(result.getLocation(), Iterables.get(locations, 1));
   }
}
