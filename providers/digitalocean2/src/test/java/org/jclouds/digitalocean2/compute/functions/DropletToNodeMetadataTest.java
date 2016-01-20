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
import static org.jclouds.compute.domain.Image.Status.AVAILABLE;
import static org.jclouds.compute.domain.NodeMetadata.Status.RUNNING;
import static org.jclouds.digitalocean2.domain.Droplet.Status.ACTIVE;
import static org.testng.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume.Type;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.Networks;
import org.jclouds.digitalocean2.domain.Networks.Address;
import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Guice;

@Test(groups = "unit", testName = "DropletToNodeMetadataTest")
public class DropletToNodeMetadataTest {

   private org.jclouds.digitalocean2.domain.Image image;
   
   private Region region;
   
   private Set<Hardware> hardwares;

   private Set<Image> images;

   private Set<Location> locations;

   private LoginCredentials credentials;

   private DropletToNodeMetadata function;

   @BeforeMethod
   public void setup() {
      image = org.jclouds.digitalocean2.domain.Image.create(1, "14.04 x64",
            "distribution", "Ubuntu", "ubuntu-1404-x86", true, ImmutableList.of("sfo1"), new Date());
      region = Region.create("sfo1", "San Francisco 1", ImmutableList.of("2gb"), true, ImmutableList.<String> of());
      
      images = ImmutableSet.of(new ImageBuilder()
            .id("sfo1/ubuntu-1404-x86")
            .providerId("1")
            .name("mock image")
            .status(AVAILABLE)
            .operatingSystem(
                  OperatingSystem.builder().name("Ubuntu 14.04 x86_64").description("Ubuntu").family(OsFamily.UBUNTU)
                        .version("10.04").arch("x86_64").is64Bit(true).build()).build());

      hardwares = ImmutableSet.of(new HardwareBuilder().id("2gb").providerId("2gb").name("mock hardware")
            .processor(new Processor(1.0, 1.0)).ram(2048)
            .volume(new VolumeBuilder().size(20f).type(Type.LOCAL).build()).build());

      locations = ImmutableSet.of(new LocationBuilder()
            .id("sfo1")
            .description("sfo1/San Francisco 1")
            .scope(LocationScope.REGION)
            .parent(
                  new LocationBuilder().id("0").description("mock parent location").scope(LocationScope.PROVIDER)
                        .build()).build());

      credentials = LoginCredentials.builder().user("foo").password("bar").build();

      function = createNodeParser(hardwares, images, locations, ImmutableMap.of("node#1", (Credentials) credentials));
   }

   @Test
   public void testConvertDroplet() throws ParseException {
      Droplet droplet = Droplet.create(
            1,
            "mock-droplet",
            1,
            1,
            1,
            false,
            new Date(),
            Droplet.Status.ACTIVE,
            ImmutableList.<Integer> of(),
            ImmutableList.<Integer> of(),
            ImmutableList.<String> of(),
            region,
            image,
            null,
            "2gb",
            Networks.create(
                  ImmutableList.of(Address.create("84.45.69.3", "255.255.255.0", "84.45.69.1", "public"),
                        Address.create("192.168.2.5", "255.255.255.0", "192.168.2.1", "private")),
                  ImmutableList.<Networks.Address> of()), null);

      NodeMetadata expected = new NodeMetadataBuilder().ids("1").hardware(getOnlyElement(hardwares))
            .imageId("sfo1/ubuntu-1404-x86").status(RUNNING).location(getOnlyElement(locations)).name("mock-droplet")
            .hostname("mock-droplet").group("mock").credentials(credentials)
            .publicAddresses(ImmutableSet.of("84.45.69.3")).privateAddresses(ImmutableSet.of("192.168.2.5"))
            .providerId("1").backendStatus(ACTIVE.name()).operatingSystem(getOnlyElement(images).getOperatingSystem())
            .build();

      NodeMetadata actual = function.apply(droplet);
      assertNodeEquals(actual, expected);
   }

   @Test
   public void testConvertDropletOldImage() throws ParseException {
      // Use an image id that is not in the list of images
      org.jclouds.digitalocean2.domain.Image image = org.jclouds.digitalocean2.domain.Image.create(2, "14.04 x64",
            "distribution", "Ubuntu", "ubuntu2-1404-x86", true, ImmutableList.of("sfo1"), new Date());
      
      Droplet droplet = Droplet.create(
            1,
            "mock-droplet",
            1,
            1,
            1,
            false,
            new Date(),
            Droplet.Status.ACTIVE,
            ImmutableList.<Integer> of(),
            ImmutableList.<Integer> of(),
            ImmutableList.<String> of(),
            region,
            image,
            null,
            "2gb",
            Networks.create(
                  ImmutableList.of(Address.create("84.45.69.3", "255.255.255.0", "84.45.69.1", "public"),
                        Address.create("192.168.2.5", "255.255.255.0", "192.168.2.1", "private")),
                  ImmutableList.<Networks.Address> of()), null);

      NodeMetadata expected = new NodeMetadataBuilder().ids("1").hardware(getOnlyElement(hardwares)).imageId(null)
            .status(RUNNING).location(getOnlyElement(locations)).name("mock-droplet").hostname("mock-droplet")
            .group("mock").credentials(credentials).publicAddresses(ImmutableSet.of("84.45.69.3"))
            .privateAddresses(ImmutableSet.of("192.168.2.5")).providerId("1").backendStatus(ACTIVE.name())
            .operatingSystem(null).build();

      NodeMetadata actual = function.apply(droplet);
      assertNodeEquals(actual, expected);
   }

   private static void assertNodeEquals(NodeMetadata actual, NodeMetadata expected) {
      assertEquals(actual, expected);
      // NodeMetadata equals method does not use all fields in equals. It assumes that same ids in same locations
      // determine the equivalence
      assertEquals(actual.getStatus(), expected.getStatus());
      assertEquals(actual.getBackendStatus(), expected.getBackendStatus());
      assertEquals(actual.getLoginPort(), expected.getLoginPort());
      assertEquals(actual.getPublicAddresses(), expected.getPublicAddresses());
      assertEquals(actual.getPrivateAddresses(), expected.getPrivateAddresses());
      assertEquals(actual.getCredentials(), expected.getCredentials());
      assertEquals(actual.getGroup(), expected.getGroup());
      assertEquals(actual.getImageId(), expected.getImageId());
      assertEquals(actual.getHardware(), expected.getHardware());
      assertEquals(actual.getOperatingSystem(), expected.getOperatingSystem());
      assertEquals(actual.getHostname(), expected.getHostname());
   }

   private DropletToNodeMetadata createNodeParser(final Set<Hardware> hardware, final Set<Image> images,
         final Set<Location> locations, Map<String, Credentials> credentialStore) {
      Supplier<Set<? extends Location>> locationSupplier = new Supplier<Set<? extends Location>>() {
         @Override
         public Set<? extends Location> get() {
            return locations;
         }
      };

      Supplier<Map<String, ? extends Hardware>> hardwareSupplier = new Supplier<Map<String, ? extends Hardware>>() {
         @Override
         public Map<String, ? extends Hardware> get() {
            return Maps.uniqueIndex(hardware, new Function<Hardware, String>() {
               @Override
               public String apply(Hardware input) {
                  return input.getId();
               }
            });
         }
      };

      Supplier<Map<String, ? extends Image>> imageSupplier = new Supplier<Map<String, ? extends Image>>() {
         @Override
         public Map<String, ? extends Image> get() {
            return Maps.uniqueIndex(images, new Function<Image, String>() {
               @Override
               public String apply(Image input) {
                  return input.getId();
               }
            });
         }
      };

      GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

      return new DropletToNodeMetadata(imageSupplier, hardwareSupplier, locationSupplier, new DropletStatusToStatus(),
            namingConvention, credentialStore);
   }
}
