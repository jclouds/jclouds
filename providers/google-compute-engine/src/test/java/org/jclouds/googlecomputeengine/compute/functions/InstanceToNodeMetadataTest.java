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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume.Type;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.parse.ParseImageTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.uniqueIndex;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.googlecomputeengine.compute.functions.InstanceToNodeMetadata.isCustomMachineTypeURI;
import static org.jclouds.googlecomputeengine.compute.functions.InstanceToNodeMetadata.machineTypeURIToCustomHardware;
import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "InstanceToNodeMetadataTest", singleThreaded = true) // BeforeMethod = singleThreaded
public class InstanceToNodeMetadataTest {

   /**
    * GroupNamingConvention that always returns the same name provided in the constructor.
    * The predicates returned always evaluate to true.
    *
    */
   static class FixedGroupNamingConvention implements GroupNamingConvention {
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
   private URI imageUrl = new ParseImageTest().expected().selfLink();
   private Set<Location> locations;
   private InstanceToNodeMetadata groupNullNodeParser;

   @BeforeMethod
   public final void setup() {
      instance = new ParseInstanceTest().expected();

      hardwares = ImmutableSet.of(new HardwareBuilder().id("my_id")
         .uri(URI.create("https://www.googleapis.com/compute/v1/projects/party/zones/us-central1-a/machineTypes/"
               + "n1-standard-1"))
         .providerId("1")
         .name("mock hardware").processor(new Processor(1.0, 1.0)).ram(2048)
         .volume(new VolumeBuilder().size(20f).type(Type.LOCAL).build()).build());

      locations = ImmutableSet.of(new LocationBuilder()
         .id("id")
         .description("https://www.googleapis.com/compute/v1/projects/party/zones/us-central1-a")
         .scope(LocationScope.REGION)
         .parent(
               new LocationBuilder().id("0").description("mock parent location").scope(LocationScope.PROVIDER)
               .build()).build());

      groupNullNodeParser = createNodeParser(hardwares, locations, null);
   }

   private InstanceToNodeMetadata createNodeParser(final Set<Hardware> hardware, final Set<Location> locations,
         final String groupName) {
      Supplier<Map<URI, Location>> locationSupplier = new Supplier<Map<URI, Location>>() {
         @Override
         public Map<URI, Location> get() {
            return uniqueIndex(locations, new Function<Location, URI>() {
               @Override
               public URI apply(final Location input) {
                  return URI.create(input.getDescription());
               }
            });
         }
      };

      Supplier<Map<URI, Hardware>> hardwareSupplier = Suppliers
            .<Map<URI, Hardware>>ofInstance(uniqueIndex(hardware, new Function<Hardware, URI>() {
               @Override
               public URI apply(final Hardware input) {
                  return input.getUri();
               }
            }));

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

      Map<URI, Optional<Image>> imageMap = ImmutableMap.of(instance.disks().get(0).source(),
            Optional.of(new ParseImageTest().expected()));

      return new InstanceToNodeMetadata(
         ImmutableMap.<Instance.Status, NodeMetadata.Status>builder()
            .put(Instance.Status.RUNNING, NodeMetadata.Status.PENDING).build(),
            namingConventionFactory,
            CacheBuilder.newBuilder().build(CacheLoader.from(Functions.forMap(imageMap))),
            hardwareSupplier,
            locationSupplier);
   }

   @Test
   public void imageUrl() {
      NodeMetadata nodeMetadata = groupNullNodeParser.apply(instance);
      assertEquals(nodeMetadata.getImageId(), imageUrl.toString());
   }

   @Test
   public final void testInstanceWithGroupNull() {
      NodeMetadata nodeMetadata = groupNullNodeParser.apply(instance);
      assertEquals(nodeMetadata.getId(), instance.selfLink().toString());
      assertEquals(nodeMetadata.getTags(), ImmutableSet.of("aTag", "Group-port-42"));
   }

   @Test
   public void isCustomMachineTypeTest() {
      URI uri = URI.create("https://www.googleapis.com/compute/v1/projects/jclouds-dev/zones/asia-east1-a/machineTypes/custom-1-1024");
      assertThat(isCustomMachineTypeURI(uri)).isTrue();

      URI uri2 = URI.create("https://www.googleapis.com/compute/v1/projects/jclouds-dev/");
      assertThat(isCustomMachineTypeURI(uri2)).isFalse();
   }

   @Test
   public void machineTypeParserTest() {
      URI uri = URI.create("https://www.googleapis.com/compute/v1/projects/jclouds-dev/zones/asia-east1-a/machineTypes/custom-1-1024");
      Hardware hardware = machineTypeURIToCustomHardware(uri);
      assertThat(hardware.getRam()).isEqualTo(1024);
      assertThat(hardware.getProcessors().get(0).getCores()).isEqualTo(1);
      assertThat(hardware.getUri())
            .isEqualTo(URI.create("https://www.googleapis.com/compute/v1/projects/jclouds-dev/zones/asia-east1-a/machineTypes/custom-1-1024"));
      assertThat(hardware.getId())
            .isEqualTo("https://www.googleapis.com/compute/v1/projects/jclouds-dev/zones/asia-east1-a/machineTypes/custom-1-1024");
   }

}
