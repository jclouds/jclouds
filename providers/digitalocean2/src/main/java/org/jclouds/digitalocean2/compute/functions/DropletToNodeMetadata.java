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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.digitalocean2.compute.internal.ImageInRegion.encodeId;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.digitalocean2.compute.internal.ImageInRegion;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.Networks;
import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * Transforms an {@link Droplet} to the jclouds portable model.
 */
@Singleton
public class DropletToNodeMetadata implements Function<Droplet, NodeMetadata> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<Map<String, ? extends Image>> images;
   private final Supplier<Map<String, ? extends Hardware>> hardwares;
   private final Supplier<Set<? extends Location>> locations;
   private final Function<Droplet.Status, Status> toPortableStatus;
   private final GroupNamingConvention groupNamingConvention;
   private final Map<String, Credentials> credentialStore;

   @Inject
   DropletToNodeMetadata(Supplier<Map<String, ? extends Image>> images,
         Supplier<Map<String, ? extends Hardware>> hardwares, @Memoized Supplier<Set<? extends Location>> locations,
         Function<Droplet.Status, Status> toPortableStatus, GroupNamingConvention.Factory groupNamingConvention,
         Map<String, Credentials> credentialStore) {
      this.images = checkNotNull(images, "images cannot be null");
      this.hardwares = checkNotNull(hardwares, "hardwares cannot be null");
      this.locations = checkNotNull(locations, "locations cannot be null");
      this.toPortableStatus = checkNotNull(toPortableStatus, "toPortableStatus cannot be null");
      this.groupNamingConvention = checkNotNull(groupNamingConvention, "groupNamingConvention cannot be null")
            .createWithoutPrefix();
      this.credentialStore = checkNotNull(credentialStore, "credentialStore cannot be null");
   }

   @Override
   public NodeMetadata apply(Droplet input) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(String.valueOf(input.id()));
      builder.name(input.name());
      builder.hostname(input.name());
      builder.group(groupNamingConvention.extractGroup(input.name()));

      builder.hardware(getHardware(input.sizeSlug()));
      builder.location(getLocation(input.region()));

      Optional<? extends Image> image = findImage(input.image(), input.region().slug());
      if (image.isPresent()) {
         builder.imageId(image.get().getId());
         builder.operatingSystem(image.get().getOperatingSystem());
      } else {
         logger.info(">> image with id %s for droplet %s was not found. "
               + "This might be because the image that was used to create the droplet has a new id.",
               input.image().id(), input.id());
      }

      builder.status(toPortableStatus.apply(input.status()));
      builder.backendStatus(input.status().name());

      if (!input.getPublicAddresses().isEmpty()) {
         builder.publicAddresses(FluentIterable
                     .from(input.getPublicAddresses())
                     .transform(new Function<Networks.Address, String>() {
                        @Override
                        public String apply(final Networks.Address input) {
                           return input.ip();
                        }
                     })
         );
      }

      if (!input.getPrivateAddresses().isEmpty()) {
         builder.privateAddresses(FluentIterable
               .from(input.getPrivateAddresses())
               .transform(new Function<Networks.Address, String>() {
                  @Override
                  public String apply(final Networks.Address input) {
                     return input.ip();
                  }
               })
         );
      }

      // DigitalOcean does not provide a way to get the credentials.
      // Try to return them from the credential store
      Credentials credentials = credentialStore.get("node#" + input.id());
      if (credentials instanceof LoginCredentials) {
         builder.credentials(LoginCredentials.class.cast(credentials));
      }

      return builder.build();
   }

   protected Optional<? extends Image> findImage(org.jclouds.digitalocean2.domain.Image image, String region) {
      return Optional.fromNullable(images.get().get(encodeId(ImageInRegion.create(image, region))));
   }

   protected Hardware getHardware(final String slug) {
      return Iterables.find(hardwares.get().values(), new Predicate<Hardware>() {
         @Override
         public boolean apply(Hardware input) {
            return input.getId().equals(slug);
         }
      });
   }

   protected Location getLocation(final Region region) {
      return find(locations.get(), new Predicate<Location>() {
         @Override
         public boolean apply(Location location) {
            return region != null && region.slug().equals(location.getId());
         }
      }, null);
   }
}
