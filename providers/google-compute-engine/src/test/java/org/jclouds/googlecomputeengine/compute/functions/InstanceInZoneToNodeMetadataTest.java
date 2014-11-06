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
package org.jclouds.googlecomputeengine.compute.functions;

import static org.jclouds.compute.domain.Image.Status.AVAILABLE;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume.Type;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.googlecomputeengine.compute.domain.InstanceInZone;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.parse.ParseInstanceTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

@Test(groups = "unit", testName = "InstanceInZoneToNodeMetadataTest")
public class InstanceInZoneToNodeMetadataTest {

   /**
    * GroupNamingConvention that always returns the same name provided in the constructor.
    * The predicates returned always evaluate to true.
    *
    */
   class FixedGroupNamingConvention implements GroupNamingConvention {
      private final String name;

      public FixedGroupNamingConvention(final String name) {
         this.name = name;
      }

      @Override
      public String sharedNameForGroup(final String group) {
         return name;
      }

      @Override
      public String uniqueNameForGroup(final String group) {
         return name;
      }

      @Override
      public String groupInUniqueNameOrNull(final String encoded) {
         return name;
      }

      @Override
      public String groupInSharedNameOrNull(final String encoded) {
         return name;
      }

      @Override
      public Predicate<String> containsGroup(final String group) {
         return new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
               return true;
            }
         };
      }

      @Override
      public Predicate<String> containsAnyGroup() {
         return new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
               return true;
            }
         };
      }

      @Override
      public String extractGroup(final String encoded) {
         return name;
      }
   }

   private Instance instance;

   private Set<Hardware> hardwares;

   private Set<Image> images;

   private Set<Location> locations;

   private InstanceInZoneToNodeMetadata groupGroupNodeParser;
   private InstanceInZoneToNodeMetadata groupNullNodeParser;

   @BeforeMethod
   public final void setup() {
      instance = new ParseInstanceTest().expected();

      images = ImmutableSet.of(new ImageBuilder()
         .id("1")
         .uri(URI.create("https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20140718"))
         .providerId("1")
         .name("mock image")
         .status(AVAILABLE)
         .operatingSystem(
            OperatingSystem.builder().name("Ubuntu 14.04 x86_64").description("Ubuntu").family(OsFamily.UBUNTU)
            .version("10.04").arch("x86_64").is64Bit(true).build()).build());

      hardwares = ImmutableSet.of(new HardwareBuilder().id("my_id")
         .uri(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/machineTypes/"
               + "n1-standard-1"))
         .providerId("1")
         .name("mock hardware").processor(new Processor(1.0, 1.0)).ram(2048)
         .volume(new VolumeBuilder().size(20f).type(Type.LOCAL).build()).build());

      locations = ImmutableSet.of(new LocationBuilder()
         .id("id")
         .description("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a")
         .scope(LocationScope.REGION)
         .parent(
               new LocationBuilder().id("0").description("mock parent location").scope(LocationScope.PROVIDER)
               .build()).build());

      groupGroupNodeParser = createNodeParser(hardwares, images, locations, "Group");
      groupNullNodeParser = createNodeParser(hardwares, images, locations, null);
   }

   private InstanceInZoneToNodeMetadata createNodeParser(final Set<Hardware> hardware, final Set<Image> images,
         final Set<Location> locations, final String groupName) {
      Supplier<Map<URI, Location>> locationSupplier = new Supplier<Map<URI, Location>>() {
         @Override
         public Map<URI, Location> get() {
            return Maps.uniqueIndex(locations, new Function<Location, URI>() {
               @Override
               public URI apply(final Location input) {
                  return URI.create(input.getDescription());
               }
            });
         }
      };

      Supplier<Map<URI, Hardware>> hardwareSupplier = new Supplier<Map<URI, Hardware>>() {
         @Override
         public Map<URI, Hardware> get() {
            return Maps.uniqueIndex(hardware, new Function<Hardware, URI>() {
               @Override
               public URI apply(final Hardware input) {
                  return input.getUri();
               }
            });
         }
      };

      Supplier<Map<URI, Image>> imageSupplier = new Supplier<Map<URI, Image>>() {
         @Override
         public Map<URI, Image> get() {
            return Maps.uniqueIndex(images, new Function<Image, URI>() {
               @Override
               public URI apply(final Image input) {
                  return input.getUri();
               }
            });
         }
      };

      GroupNamingConvention.Factory namingConventionFactory =
         new GroupNamingConvention.Factory() {
            @Override
            public GroupNamingConvention createWithoutPrefix() {
               return new FixedGroupNamingConvention(groupName);
            }

            @Override
            public GroupNamingConvention create() {
               return new FixedGroupNamingConvention(groupName);
            }
         };

      return new InstanceInZoneToNodeMetadata(
         ImmutableMap.<Instance.Status, NodeMetadata.Status>builder()
            .put(Instance.Status.RUNNING, NodeMetadata.Status.PENDING).build(),
            namingConventionFactory,
            imageSupplier,
            hardwareSupplier,
            locationSupplier,
            new FirewallTagNamingConvention.Factory(namingConventionFactory));
   }

   @Test
   public final void testTagFilteringWorks() {
      InstanceInZone instanceInZone = InstanceInZone.create(instance, "zoneId");
      NodeMetadata nodeMetadata = groupGroupNodeParser.apply(instanceInZone);
      assertEquals(nodeMetadata.getId(), "id/test-0");
      assertEquals(nodeMetadata.getTags(), ImmutableSet.<String>of(
            "aTag"  // "aTag" kept as a non firewall tag.
            // "Group-port-42" filtered out as a firewall tag.
      ));
   }

   @Test
   public final void testInstanceWithGroupNull() {
      InstanceInZone instanceInZone = InstanceInZone.create(instance, "zoneId");
      NodeMetadata nodeMetadata = groupNullNodeParser.apply(instanceInZone);
      assertEquals(nodeMetadata.getId(), "id/test-0");
      assertEquals(nodeMetadata.getTags(), ImmutableSet.<String>of("aTag", "Group-port-42"));
   }
}
