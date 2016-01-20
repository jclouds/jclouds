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
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
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
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.UncheckedTimeoutException;

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
   private final ListeningExecutorService userExecutor;

   @Inject DigitalOcean2ImageExtension(DigitalOcean2Api api,
         @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<Integer> imageAvailablePredicate,
         @Named(TIMEOUT_NODE_SUSPENDED) Predicate<Integer> nodeStoppedPredicate,
         Function<ImageInRegion, Image> imageTransformer,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.api = api;
      this.imageAvailablePredicate = imageAvailablePredicate;
      this.nodeStoppedPredicate = nodeStoppedPredicate;
      this.imageTransformer = imageTransformer;
      this.userExecutor = userExecutor;
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
      int dropletId = Integer.parseInt(cloneTemplate.getSourceNodeId());

      // Droplet needs to be stopped
      final Droplet droplet = api.dropletApi().get(dropletId);
      if (droplet.status() != Status.OFF) {
         api.dropletApi().powerOff(dropletId);
         checkState(nodeStoppedPredicate.apply(dropletId), "node was not powered off in the configured timeout");
      }

      final Action snapshotEvent = api.dropletApi().snapshot(Integer.parseInt(cloneTemplate.getSourceNodeId()),
            cloneTemplate.getName());

      logger.info(">> registered new Image, waiting for it to become available");

      return userExecutor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
            if (imageAvailablePredicate.apply(snapshotEvent.id())) {
               org.jclouds.digitalocean2.domain.Image snapshot = api.imageApi().list().concat()
                     .firstMatch(new Predicate<org.jclouds.digitalocean2.domain.Image>() {
                        @Override
                        public boolean apply(org.jclouds.digitalocean2.domain.Image input) {
                           return input.name().equals(cloneTemplate.getName());
                        }
                     }).get();

               return imageTransformer.apply(ImageInRegion.create(snapshot, droplet.region().slug()));
            }

            throw new UncheckedTimeoutException("Image was not created within the time limit: "
                  + cloneTemplate.getName());
         }
      });
   }

   @Override
   public boolean deleteImage(String id) {
      String imageId = ImageInRegion.extractImageId(id);
      Integer numericId = Ints.tryParse(imageId); // User images don't have a slug, so we expect a numeric id here

      if (numericId != null) {
         try {
            logger.debug(">> deleting image %s...", id);
            api.imageApi().delete(numericId);
            return true;
         } catch (Exception ex) {
            logger.error(ex, ">> error deleting image %s", id);
         }
      } else {
         logger.warn(">> image %s is not a user image and cannot be deleted", id);
      }

      return false;
   }
}
