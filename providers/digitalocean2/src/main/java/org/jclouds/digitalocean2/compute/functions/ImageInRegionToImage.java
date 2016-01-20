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

import static com.google.common.collect.Iterables.find;
import static org.jclouds.compute.domain.OperatingSystem.builder;
import static org.jclouds.digitalocean2.compute.internal.ImageInRegion.encodeId;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.digitalocean2.compute.internal.ImageInRegion;
import org.jclouds.digitalocean2.domain.OperatingSystem;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * Transforms an {@link ImageInRegion} to the jclouds portable model.
 */
@Singleton
public class ImageInRegionToImage implements Function<ImageInRegion, Image> {

   private final Supplier<Set<? extends Location>> locations;

   @Inject ImageInRegionToImage(@Memoized Supplier<Set<? extends Location>> locations) {
      this.locations = locations;
   }

   @Override
   public Image apply(final ImageInRegion input) {
      String description = input.image().distribution() + " " + input.image().name();
      ImageBuilder builder = new ImageBuilder();
      // Private images don't have a slug
      builder.id(encodeId(input));
      builder.providerId(String.valueOf(input.image().id()));
      builder.name(input.image().name());
      builder.description(description);
      builder.status(Status.AVAILABLE);
      builder.location(getLocation(input.region()));

      OperatingSystem os = OperatingSystem.create(input.image().name(), input.image().distribution());

      builder.operatingSystem(builder()
            .name(os.distribution().value())
            .family(os.distribution().osFamily())
            .description(description)
            .arch(os.arch())
            .version(os.version())
            .is64Bit(os.is64bit())
            .build());

      ImmutableMap.Builder<String, String> metadata = ImmutableMap.builder();
      metadata.put("publicImage", String.valueOf(input.image().isPublic()));
      builder.userMetadata(metadata.build());

      return builder.build();
   }

   protected Location getLocation(final String region) {
      return find(locations.get(), new Predicate<Location>() {
         @Override
         public boolean apply(Location location) {
            return region.equals(location.getId());
         }
      });
   }

}
