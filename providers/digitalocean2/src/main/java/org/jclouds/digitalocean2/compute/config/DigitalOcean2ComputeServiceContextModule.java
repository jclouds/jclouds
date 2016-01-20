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
package org.jclouds.digitalocean2.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.util.Predicates2.retry;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.functions.TemplateOptionsToStatement;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.compute.DigitalOcean2ComputeServiceAdapter;
import org.jclouds.digitalocean2.compute.extensions.DigitalOcean2ImageExtension;
import org.jclouds.digitalocean2.compute.functions.DropletStatusToStatus;
import org.jclouds.digitalocean2.compute.functions.DropletToNodeMetadata;
import org.jclouds.digitalocean2.compute.functions.ImageInRegionToImage;
import org.jclouds.digitalocean2.compute.functions.RegionToLocation;
import org.jclouds.digitalocean2.compute.functions.SizeToHardware;
import org.jclouds.digitalocean2.compute.functions.TemplateOptionsToStatementWithoutPublicKey;
import org.jclouds.digitalocean2.compute.internal.ImageInRegion;
import org.jclouds.digitalocean2.compute.options.DigitalOcean2TemplateOptions;
import org.jclouds.digitalocean2.compute.strategy.CreateKeyPairsThenCreateNodes;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.digitalocean2.domain.Size;
import org.jclouds.domain.Location;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

/**
 * Configures the compute service classes for the DigitalOcean API.
 */
public class DigitalOcean2ComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<Droplet, Size, ImageInRegion, Region> {

   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<ComputeServiceAdapter<Droplet, Size, ImageInRegion, Region>>() {
      }).to(DigitalOcean2ComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<Droplet, NodeMetadata>>() {
      }).to(DropletToNodeMetadata.class);
      bind(new TypeLiteral<Function<ImageInRegion, Image>>() {
      }).to(ImageInRegionToImage.class);
      bind(new TypeLiteral<Function<Region, Location>>() {
      }).to(RegionToLocation.class);
      bind(new TypeLiteral<Function<Size, Hardware>>() {
      }).to(SizeToHardware.class);
      bind(new TypeLiteral<Function<Droplet.Status, Status>>() {
      }).to(DropletStatusToStatus.class);

      install(new LocationsFromComputeServiceAdapterModule<Droplet, Size, ImageInRegion, Region>() {
      });

      bind(CreateNodesInGroupThenAddToSet.class).to(CreateKeyPairsThenCreateNodes.class);
      bind(TemplateOptions.class).to(DigitalOcean2TemplateOptions.class);
      bind(TemplateOptionsToStatement.class).to(TemplateOptionsToStatementWithoutPublicKey.class);

      bind(new TypeLiteral<ImageExtension>() {
      }).to(DigitalOcean2ImageExtension.class);
   }

   @Provides
   @Named(TIMEOUT_NODE_RUNNING)
   protected Predicate<Integer> provideDropletRunningPredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return retry(new DropletInStatusPredicate(api, Droplet.Status.ACTIVE), timeouts.nodeRunning,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected Predicate<Integer> provideDropletSuspendedPredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return retry(new DropletInStatusPredicate(api, Droplet.Status.OFF), timeouts.nodeSuspended,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<Integer> provideDropletTerminatedPredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return retry(new DropletTerminatedPredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_IMAGE_AVAILABLE)
   protected Predicate<Integer> provideImageAvailablePredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return retry(new ActionDonePredicate(api), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Singleton
   protected Predicate<Region> provideRegionAvailablePredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return retry(new RegionAvailablePredicate(), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   protected Predicate<Integer> provideActionCompletedPredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return retry(new ActionDonePredicate(api), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @VisibleForTesting
   static class ActionDonePredicate implements Predicate<Integer> {

      private final DigitalOcean2Api api;

      public ActionDonePredicate(DigitalOcean2Api api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(Integer input) {
         checkNotNull(input, "action id cannot be null");
         Action current = api.actionApi().get(input);
         switch (current.status()) {
            case COMPLETED:
               return true;
            case IN_PROGRESS:
               return false;
            case ERRORED:
            default:
               throw new IllegalStateException("Resource is in invalid status: " + current.status().name());
         }
      }

   }

   @VisibleForTesting
   static class DropletTerminatedPredicate implements Predicate<Integer> {

      private final DigitalOcean2Api api;

      public DropletTerminatedPredicate(DigitalOcean2Api api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(Integer input) {
         checkNotNull(input, "droplet id");
         Droplet droplet = api.dropletApi().get(input);
         return droplet == null;
      }
   }
   
   @VisibleForTesting
   static class DropletInStatusPredicate implements Predicate<Integer> {

      private final DigitalOcean2Api api;
      private final Droplet.Status status;

      public DropletInStatusPredicate(DigitalOcean2Api api, Droplet.Status status) {
         this.api = checkNotNull(api, "api must not be null");
         this.status = checkNotNull(status, "status must not be null");
      }

      @Override
      public boolean apply(Integer input) {
         checkNotNull(input, "droplet id");
         Droplet droplet = api.dropletApi().get(input);
         return droplet != null && status == droplet.status();
      }
   }

   @VisibleForTesting
   static class RegionAvailablePredicate implements Predicate<Region> {
      @Override
      public boolean apply(Region input) {
         return input.available();
      }

   }

}
