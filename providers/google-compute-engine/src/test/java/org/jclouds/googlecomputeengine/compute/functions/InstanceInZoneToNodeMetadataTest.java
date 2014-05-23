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

import static org.easymock.EasyMock.createMock;
import static org.testng.Assert.assertEquals;
import static org.jclouds.compute.domain.Image.Status.AVAILABLE;

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
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceInZone;
import org.jclouds.googlecomputeengine.domain.Metadata;
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
      instance = Instance.builder()
         .id("13051190678907570425")
         .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-11-25T23:48:20.758"))
         .selfLink(URI.create("https://www.googleapis"
            + ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-0"))
         .description("desc")
         .name("test-0")
         .machineType(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/"
               + "machineTypes/n1-standard-1"))
         .status(Instance.Status.RUNNING)
         .zone(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a"))
         .addNetworkInterface(
               Instance.NetworkInterface.builder()
                  .name("nic0")
                  .networkIP("10.240.121.115")
                  .network(URI.create("https://www.googleapis"
                        + ".com/compute/v1/projects/myproject/global/networks/default"))
                  .build())
         .addDisk(
               Instance.PersistentAttachedDisk.builder()
               .index(0)
               .mode(Instance.PersistentAttachedDisk.Mode.READ_WRITE)
               .deviceName("test")
               .source(URI.create("https://www.googleapis"
                  + ".com/compute/v1/projects/myproject/zones/us-central1-a/disks/test"))
               .boot(true)
               .build())
         .tags(Instance.Tags.builder().fingerprint("abcd").addItem("aTag").addItem("Group-port-42").build())
         .metadata(Metadata.builder()
               .items(ImmutableMap.of("aKey", "aValue",
                  "jclouds-image",
                  "https://www.googleapis.com/compute/v1/projects/centos-cloud/global/images/gcel-12-04-v20121106",
                  "jclouds-delete-boot-disk", "true"))
               .fingerprint("efgh")
               .build())
               .addServiceAccount(Instance.ServiceAccount.builder().email("default").addScopes("myscope").build())
               .build();

      images = ImmutableSet.of(new ImageBuilder()
         .id("1")
         .uri(URI.create("https://www.googleapis.com/compute/v1/projects/centos-cloud/global/images/"
               + "gcel-12-04-v20121106"))
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
      Supplier<Map<URI, ? extends Location>> locationSupplier = new Supplier<Map<URI, ? extends Location>>() {
         @Override
         public Map<URI, ? extends Location> get() {
            return Maps.uniqueIndex(locations, new Function<Location, URI>() {
               @Override
               public URI apply(final Location input) {
                  return URI.create(input.getDescription());
               }
            });
         }
      };

      Supplier<Map<URI, ? extends Hardware>> hardwareSupplier = new Supplier<Map<URI, ? extends Hardware>>() {
         @Override
         public Map<URI, ? extends Hardware> get() {
            return Maps.uniqueIndex(hardware, new Function<Hardware, URI>() {
               @Override
               public URI apply(final Hardware input) {
                  return input.getUri();
               }
            });
         }
      };

      Supplier<Map<URI, ? extends Image>> imageSupplier = new Supplier<Map<URI, ? extends Image>>() {
         @Override
         public Map<URI, ? extends Image> get() {
            return Maps.uniqueIndex(images, new Function<Image, URI>() {
               @Override
               public URI apply(final Image input) {
                  return input.getUri();
               }
            });
         }
      };

      Supplier<String> userProjectSupplier = new Supplier<String>() {
         @Override
         public String get() {
            return "userProject";
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
            new FirewallTagNamingConvention.Factory(namingConventionFactory),
            createMock(GoogleComputeEngineApi.class),
            userProjectSupplier);
   }

   @Test
   public final void testTagFilteringWorks() {
      InstanceInZone instanceInZone = new InstanceInZone(instance, "zoneId");
      NodeMetadata nodeMetadata = groupGroupNodeParser.apply(instanceInZone);
      assertEquals(nodeMetadata.getId(), "id/test-0");
      assertEquals(nodeMetadata.getTags(), ImmutableSet.<String>of(
            "aTag"  // "aTag" kept as a non firewall tag.
            // "Group-port-42" filtered out as a firewall tag.
      ));
   }

   @Test
   public final void testInstanceWithGroupNull() {
      InstanceInZone instanceInZone = new InstanceInZone(instance, "zoneId");
      NodeMetadata nodeMetadata = groupNullNodeParser.apply(instanceInZone);
      assertEquals(nodeMetadata.getId(), "id/test-0");
      assertEquals(nodeMetadata.getTags(), ImmutableSet.<String>of("aTag", "Group-port-42"));
   }
}
