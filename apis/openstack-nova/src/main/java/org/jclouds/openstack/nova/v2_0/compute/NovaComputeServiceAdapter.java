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
package org.jclouds.openstack.nova.v2_0.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static java.lang.String.format;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsCommaDelimitedValue;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.functions.CleanupResources;
import org.jclouds.openstack.nova.v2_0.compute.functions.RemoveFloatingIpFromNodeAndDeallocate;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.compute.strategy.ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.FlavorInRegion;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.ImageInRegion;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.ServerInRegion;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.predicates.ImagePredicates;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;

/**
 * The adapter used by the NovaComputeServiceContextModule to interface the nova-specific domain
 * model to the computeService generic domain model.
 */
public class NovaComputeServiceAdapter implements
         ComputeServiceAdapter<ServerInRegion, FlavorInRegion, ImageInRegion, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final NovaApi novaApi;
   protected final Supplier<Set<String>> regionIds;
   protected final RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate;
   private final Predicate<RegionAndId> serverRunningPredicate;
   private final Predicate<RegionAndId> serverTerminatedPredicate;
   private final CleanupResources cleanupResources;

   @Inject
   public NovaComputeServiceAdapter(NovaApi novaApi, @Region Supplier<Set<String>> regionIds,
                                    RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate,
                                    @Named(TIMEOUT_NODE_RUNNING) Predicate<RegionAndId> serverRunningPredicate,
                                    @Named(TIMEOUT_NODE_TERMINATED) Predicate<RegionAndId> serverTerminatedPredicate,
                                    CleanupResources cleanupResources) {
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.regionIds = checkNotNull(regionIds, "regionIds");
      this.removeFloatingIpFromNodeAndDeallocate = checkNotNull(removeFloatingIpFromNodeAndDeallocate,
               "removeFloatingIpFromNodeAndDeallocate");
      this.serverRunningPredicate = serverRunningPredicate;
      this.serverTerminatedPredicate = serverTerminatedPredicate;
      this.cleanupResources = cleanupResources;
   }

   /**
    * Note that we do not validate extensions here, on basis that
    * {@link ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet} has already
    * done so.
    */
   @Override
   public NodeAndInitialCredentials<ServerInRegion> createNodeWithGroupEncodedIntoName(String group, String name,
            Template template) {
      final String regionId = template.getLocation().getId();
      String imageId = template.getImage().getProviderId();
      String flavorId = template.getHardware().getProviderId();
      NovaTemplateOptions templateOptions = template.getOptions().as(NovaTemplateOptions.class);

      CreateServerOptions options = new CreateServerOptions();
      Map<String, String> metadataAndTagsAsCommaDelimitedValue = metadataAndTagsAsCommaDelimitedValue(template.getOptions());
      options.metadata(metadataAndTagsAsCommaDelimitedValue);
      if (!templateOptions.getGroups().isEmpty()) options.securityGroupNames(templateOptions.getGroups());
      options.userData(templateOptions.getUserData());
      options.diskConfig(templateOptions.getDiskConfig());
      options.configDrive(templateOptions.getConfigDrive());
      options.availabilityZone(templateOptions.getAvailabilityZone());
      
      if (templateOptions.getNovaNetworks() != null) {
         options.novaNetworks(templateOptions.getNovaNetworks());
      }
      if (templateOptions.getNetworks() != null) {
         options.networks(templateOptions.getNetworks());
      }

      if (templateOptions.getKeyPairName() != null) {
         options.keyPairName(templateOptions.getKeyPairName());
      }
      
      logger.debug(">> creating new server region(%s) name(%s) image(%s) flavor(%s) options(%s)", regionId, name, imageId, flavorId, options);
      final ServerCreated lightweightServer = novaApi.getServerApi(regionId).create(name, imageId, flavorId, options);
      if (!serverRunningPredicate.apply(RegionAndId.fromRegionAndId(regionId, lightweightServer.getId()))) {
         final String message = format("Server %s was not created within %sms. The resources created for it will be destroyed", name, "30 * 60");
         logger.warn(message);
         String tagString = metadataAndTagsAsCommaDelimitedValue.get("jclouds_tags");
         Set<String> tags = Sets.newHashSet(Splitter.on(',').split(tagString));
         cleanupResources.removeSecurityGroupCreatedByJcloudsAndInvalidateCache(regionId, tags);
         throw new IllegalStateException(message);
      }
      logger.trace("<< server(%s)", lightweightServer.getId());

      Server server = novaApi.getServerApi(regionId).get(lightweightServer.getId());
      ServerInRegion serverInRegion = new ServerInRegion(server, regionId);

      LoginCredentials.Builder credentialsBuilder = LoginCredentials.builder();
      if (templateOptions.getLoginPrivateKey() != null) {
         credentialsBuilder.privateKey(templateOptions.getLoginPrivateKey());
      } 
      if (lightweightServer.getAdminPass().isPresent()) {
         credentialsBuilder.password(lightweightServer.getAdminPass().get());
      }
      return new NodeAndInitialCredentials<ServerInRegion>(serverInRegion, serverInRegion.slashEncode(), credentialsBuilder
               .build());
   }

  @Override
   public Iterable<FlavorInRegion> listHardwareProfiles() {
      Builder<FlavorInRegion> builder = ImmutableSet.builder();
      for (final String regionId : regionIds.get()) {
         builder.addAll(transform(novaApi.getFlavorApi(regionId).listInDetail().concat(),
                  new Function<Flavor, FlavorInRegion>() {

                     @Override
                     public FlavorInRegion apply(Flavor arg0) {
                        return new FlavorInRegion(arg0, regionId);
                     }

                  }));
      }
      return builder.build();
   }

   @Override
   public Iterable<ImageInRegion> listImages() {
      Builder<ImageInRegion> builder = ImmutableSet.builder();
      Set<String> regions = regionIds.get();
      checkState(!regions.isEmpty(), "no regions found in supplier %s", regionIds);
      for (final String regionId : regions) {
         Set<? extends Image> images = novaApi.getImageApi(regionId).listInDetail().concat().toSet();
         if (images.isEmpty()) {
            logger.debug("no images found in region %s", regionId);
            continue;
         }
         Iterable<? extends Image> active = filter(images, ImagePredicates.statusEquals(Image.Status.ACTIVE));
         if (images.isEmpty()) {
            logger.debug("no images with status active in region %s; non-active: %s", regionId,
                     transform(active, new Function<Image, String>() {

                        @Override
                        public String apply(Image input) {
                           return Objects.toStringHelper("").add("id", input.getId()).add("status", input.getStatus())
                                    .toString();
                        }

                     }));
            continue;
         }
         builder.addAll(transform(active, new Function<Image, ImageInRegion>() {

            @Override
            public ImageInRegion apply(Image arg0) {
               return new ImageInRegion(arg0, regionId);
            }

         }));
      }
      return builder.build();
   }

   @Override
   public Iterable<ServerInRegion> listNodes() {
      Builder<ServerInRegion> builder = ImmutableSet.builder();
      for (final String regionId : regionIds.get()) {
         builder.addAll(novaApi.getServerApi(regionId).listInDetail().concat()
                  .transform(new Function<Server, ServerInRegion>() {

                     @Override
                     public ServerInRegion apply(Server arg0) {
                        return new ServerInRegion(arg0, regionId);
                     }

                  }));
      }
      return builder.build();
   }

   @Override
   public Iterable<ServerInRegion> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<ServerInRegion>() {

            @Override
            public boolean apply(ServerInRegion server) {
               return contains(ids, server.slashEncode());
            }
         });
   }

   @Override
   public Iterable<Location> listLocations() {
      // locations provided by keystone
      return ImmutableSet.of();
   }

   @Override
   public ServerInRegion getNode(String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      Server server = novaApi.getServerApi(regionAndId.getRegion()).get(regionAndId.getId());
      return server == null ? null : new ServerInRegion(server, regionAndId.getRegion());
   }

   @Override
   public ImageInRegion getImage(String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      Image image = novaApi.getImageApi(regionAndId.getRegion()).get(regionAndId.getId());
      return image == null ? null : new ImageInRegion(image, regionAndId.getRegion());
   }

   @Override
   public void destroyNode(String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      novaApi.getServerApi(regionAndId.getRegion()).delete(regionAndId.getId());
      checkState(serverTerminatedPredicate.apply(regionAndId), "server was not destroyed in the configured timeout");
   }

   @Override
   public void rebootNode(String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      novaApi.getServerApi(regionAndId.getRegion()).reboot(regionAndId.getId(), RebootType.HARD);
   }

   @Override
   public void resumeNode(String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      if (novaApi.getServerAdminApi(regionAndId.getRegion()).isPresent()) {
         novaApi.getServerAdminApi(regionAndId.getRegion()).get().resume(regionAndId.getId());
      } else {
         throw new UnsupportedOperationException("resume requires installation of the Admin Actions extension");
      }
   }

   @Override
   public void suspendNode(String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      if (novaApi.getServerAdminApi(regionAndId.getRegion()).isPresent()) {
         novaApi.getServerAdminApi(regionAndId.getRegion()).get().suspend(regionAndId.getId());
      } else {
         throw new UnsupportedOperationException("suspend requires installation of the Admin Actions extension");
      }
   }

}
