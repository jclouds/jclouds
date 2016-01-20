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
package org.jclouds.digitalocean2.compute;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsCommaDelimitedValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.compute.internal.ImageInRegion;
import org.jclouds.digitalocean2.compute.options.DigitalOcean2TemplateOptions;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.DropletCreate;
import org.jclouds.digitalocean2.domain.Image;
import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.digitalocean2.domain.Size;
import org.jclouds.digitalocean2.domain.options.CreateDropletOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.primitives.Ints;

/**
 * Implementation of the Compute Service for the DigitalOcean API.
 */
public class DigitalOcean2ComputeServiceAdapter implements ComputeServiceAdapter<Droplet, Size, ImageInRegion, Region> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DigitalOcean2Api api;
   private final Predicate<Integer> nodeRunningPredicate;
   private final Predicate<Integer> nodeStoppedPredicate;
   private final Predicate<Integer> nodeTerminatedPredicate;
   private final Json json;

   @Inject DigitalOcean2ComputeServiceAdapter(DigitalOcean2Api api,
         @Named(TIMEOUT_NODE_RUNNING) Predicate<Integer> nodeRunningPredicate,
         @Named(TIMEOUT_NODE_SUSPENDED) Predicate<Integer> nodeStoppedPredicate,
         @Named(TIMEOUT_NODE_TERMINATED) Predicate<Integer> nodeTerminatedPredicate,
         Json json) {
      this.api = api;
      this.nodeRunningPredicate = nodeRunningPredicate;
      this.nodeStoppedPredicate = nodeStoppedPredicate;
      this.nodeTerminatedPredicate = nodeTerminatedPredicate;
      this.json = json;
   }

   @Override
   public NodeAndInitialCredentials<Droplet> createNodeWithGroupEncodedIntoName(String group, final String name,
         Template template) {
      DigitalOcean2TemplateOptions templateOptions = template.getOptions().as(DigitalOcean2TemplateOptions.class);
      CreateDropletOptions.Builder options = CreateDropletOptions.builder();

      // DigitalOcean specific options
      options.privateNetworking(templateOptions.getPrivateNetworking());
      options.backupsEnabled(templateOptions.getBackupsEnabled());
      if (!templateOptions.getSshKeyIds().isEmpty()) {
         options.addSshKeyIds(templateOptions.getSshKeyIds());
      }

      Map<String, String> metadataAndTags = metadataAndTagsAsCommaDelimitedValue(templateOptions);
      if (!metadataAndTags.isEmpty()) {
         @SuppressWarnings("unchecked")
         List<String> regionFeatures = (List<String>) template.getLocation().getMetadata().get("features");
         if (regionFeatures.contains("metadata")) {
            options.userData(json.toJson(metadataAndTags));
         } else {
            logger.debug(">> region %s does not support metadata, ignoring provided user data", template.getLocation()
                  .getId());
         }
      }

      DropletCreate dropletCreated = api.dropletApi().create(name,
            template.getLocation().getId(),
            template.getHardware().getProviderId(),
            template.getImage().getProviderId(),
            options.build());

      // We have to actively wait until the droplet has been provisioned until
      // we can build the entire Droplet object we want to return
      nodeRunningPredicate.apply(dropletCreated.droplet().id());
      Droplet droplet = api.dropletApi().get(dropletCreated.droplet().id());

      LoginCredentials defaultCredentials = LoginCredentials.builder().user("root")
            .privateKey(templateOptions.getLoginPrivateKey()).build();

      return new NodeAndInitialCredentials<Droplet>(droplet, String.valueOf(droplet.id()), defaultCredentials);
   }

   @Override
   public Iterable<ImageInRegion> listImages() {
      // Images can claim to be available in a region that is currently marked as "unavailable". We shouldn't return
      // the images scoped to those regions.
      final Set<String> availableRegionsIds = newHashSet(transform(listLocations(), new Function<Region, String>() {
         @Override
         public String apply(Region input) {
            return input.slug();
         }
      }));

      // Public images re globally available, but non-public ones can only be available in certain regions.
      // For these kind of images, return one instance of an ImageInRegion for each region where the image is
      // available. This way we can properly scope global and concrete images so they can be properly looked up.
      return concat(filter(api.imageApi().list().concat().transform(new Function<Image, Iterable<ImageInRegion>>() {
         @Override
         public Iterable<ImageInRegion> apply(final Image image) {
            return transform(image.regions(), new Function<String, ImageInRegion>() {
               @Override
               public ImageInRegion apply(String region) {
                  return availableRegionsIds.contains(region) ? ImageInRegion.create(image, region) : null;
               }
            });
         }
      }), notNull()));
   }

   @Override
   public Iterable<Size> listHardwareProfiles() {
      return filter(api.sizeApi().list().concat(), new Predicate<Size>() {
         @Override
         public boolean apply(Size size) {
            return size.available();
         }
      });
   }

   @Override
   public Iterable<Region> listLocations() {
      // DigitalOcean lists regions that are unavailable for droplet creation
      return filter(api.regionApi().list().concat(), new Predicate<Region>() {
         @Override
         public boolean apply(Region region) {
            return region.available();
         }
      });
   }

   @Override
   public Iterable<Droplet> listNodes() {
      return api.dropletApi().list().concat();
   }

   @Override
   public Iterable<Droplet> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<Droplet>() {
         @Override
         public boolean apply(Droplet droplet) {
            return contains(ids, String.valueOf(droplet.id()));
         }
      });
   }

   @Override
   public ImageInRegion getImage(String id) {
      String region = ImageInRegion.extractRegion(id);
      String imageId = ImageInRegion.extractImageId(id);
      // The id of the image can be an id or a slug. Use the corresponding method of the API depending on what is
      // provided. If it can be parsed as a number, use the method to get by ID. Otherwise, get by slug.
      Integer numericId = Ints.tryParse(imageId);
      Image image = numericId == null ? api.imageApi().get(imageId) : api.imageApi().get(numericId);
      return image == null ? null : ImageInRegion.create(image, region);
   }

   @Override
   public Droplet getNode(String id) {
      return api.dropletApi().get(Integer.parseInt(id));
   }

   @Override
   public void destroyNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      int dropletId = Integer.parseInt(id);
      api.dropletApi().delete(dropletId);
      checkState(nodeTerminatedPredicate.apply(dropletId), "node was not destroyed in the configured timeout");
   }

   @Override
   public void rebootNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      int dropletId = Integer.parseInt(id);
      api.dropletApi().reboot(dropletId);
      checkState(nodeRunningPredicate.apply(dropletId), "node did not restart in the configured timeout");
   }

   @Override
   public void resumeNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      int dropletId = Integer.parseInt(id);
      api.dropletApi().powerOn(dropletId);
      checkState(nodeRunningPredicate.apply(dropletId), "node did not started in the configured timeout");
   }

   @Override
   public void suspendNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      int dropletId = Integer.parseInt(id);
      api.dropletApi().powerOff(dropletId);
      checkState(nodeStoppedPredicate.apply(dropletId), "node did not stop in the configured timeout");
   }

}
