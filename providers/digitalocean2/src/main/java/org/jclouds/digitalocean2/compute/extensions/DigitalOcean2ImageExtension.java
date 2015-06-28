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
package org.jclouds.digitalocean2.compute.extensions;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.compute.internal.ImageInRegion;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.Droplet.Status;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * The {@link org.jclouds.compute.extensions.ImageExtension} implementation for the DigitalOcean provider.
 */
@Singleton
public class DigitalOcean2ImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DigitalOcean2Api api;
   private final Predicate<Integer> imageAvailablePredicate;
   private final Predicate<Integer> nodeStoppedPredicate;
   private final Function<ImageInRegion, Image> imageTransformer;

   @Inject DigitalOcean2ImageExtension(DigitalOcean2Api api,
         @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<Integer> imageAvailablePredicate,
         @Named(TIMEOUT_NODE_SUSPENDED) Predicate<Integer> nodeStoppedPredicate,
         Function<ImageInRegion, Image> imageTransformer) {
      this.api = api;
      this.imageAvailablePredicate = imageAvailablePredicate;
      this.nodeStoppedPredicate = nodeStoppedPredicate;
      this.imageTransformer = imageTransformer;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      Droplet droplet = api.dropletApi().get(Integer.parseInt(id));

      if (droplet == null) {
         throw new NoSuchElementException("Cannot find droplet with id: " + id);
      }

      return new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate, "DigitalOcean only supports creating images through cloning.");
      final CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;

      // Droplet needs to be stopped
      int dropletId = Integer.parseInt(cloneTemplate.getSourceNodeId());
      Action powerOffEvent = api.dropletApi().powerOff(dropletId);
      checkState(nodeStoppedPredicate.apply(powerOffEvent.id()), "node was not powered off in the configured timeout");
      
      Droplet droplet = api.dropletApi().get(dropletId);
      checkState(droplet.status() == Status.OFF, "node was not powered off in the configured timeout");

      Action snapshotEvent = api.dropletApi().snapshot(Integer.parseInt(cloneTemplate.getSourceNodeId()),
            cloneTemplate.getName());

      logger.info(">> registered new Image, waiting for it to become available");

      // Until the process completes we don't have enough information to build an image to return
      checkState(imageAvailablePredicate.apply(snapshotEvent.id()),
            "snapshot failed to complete in the configured timeout");

      org.jclouds.digitalocean2.domain.Image snapshot = api.imageApi().list().concat().firstMatch(
            new Predicate<org.jclouds.digitalocean2.domain.Image>() {
               @Override
               public boolean apply(org.jclouds.digitalocean2.domain.Image input) {
                  return input.name().equals(cloneTemplate.getName());
               }
            }).get();

      // By default snapshots are only available in the Droplet's region
      return immediateFuture(imageTransformer.apply(ImageInRegion.create(snapshot, droplet.region().slug())));
   }

   @Override
   public boolean deleteImage(String id) {
      try {
         // The id of the image can be an id or a slug. Use the corresponding method of the API depending on what is
         // provided. If it can be parsed as a number, use the method to destroy by ID. Otherwise, destroy by slug.
         Integer imageId = Ints.tryParse(id);
         if (imageId != null) {
            logger.debug(">> image does not have a slug. Using the id to delete the image...");
            api.imageApi().delete(imageId);
         }
         return true;
      } catch (Exception ex) {
         return false;
      }
   }
}
