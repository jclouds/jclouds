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
package org.jclouds.profitbricks.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;
import static java.lang.String.format;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;

import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.AvailabilityZone;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Image;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.features.DataCenterApi;
import org.jclouds.profitbricks.features.ServerApi;
import org.jclouds.profitbricks.compute.concurrent.ProvisioningJob;
import org.jclouds.profitbricks.compute.concurrent.ProvisioningManager;
import org.jclouds.profitbricks.compute.function.ProvisionableToImage;
import org.jclouds.profitbricks.domain.Snapshot;
import org.jclouds.profitbricks.domain.Provisionable;
import org.jclouds.profitbricks.util.Passwords;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

@Singleton
public class ProfitBricksComputeServiceAdapter implements ComputeServiceAdapter<Server, Hardware, Provisionable, DataCenter> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ProfitBricksApi api;
   private final Predicate<String> waitDcUntilAvailable;
   private final ListeningExecutorService executorService;
   private final ProvisioningJob.Factory jobFactory;
   private final ProvisioningManager provisioningManager;

   private static final Integer DEFAULT_LAN_ID = 1;

   @Inject
   ProfitBricksComputeServiceAdapter(ProfitBricksApi api,
           @Named(POLL_PREDICATE_DATACENTER) Predicate<String> waitDcUntilAvailable,
           @Named(PROPERTY_USER_THREADS) ListeningExecutorService executorService,
           ProvisioningJob.Factory jobFactory,
           ProvisioningManager provisioningManager) {
      this.api = api;
      this.waitDcUntilAvailable = waitDcUntilAvailable;
      this.executorService = executorService;
      this.jobFactory = jobFactory;
      this.provisioningManager = provisioningManager;
   }

   @Override
   public NodeAndInitialCredentials<Server> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      Location location = template.getLocation();
      checkArgument(location.getScope() == LocationScope.ZONE, "Template must use a ZONE-scoped location");
      final String dataCenterId = location.getId();

      Hardware hardware = template.getHardware();

      TemplateOptions options = template.getOptions();
      final String loginUser = isNullOrEmpty(options.getLoginUser()) ? "root" : options.getLoginUser();
      final String password = options.hasLoginPassword() ? options.getLoginPassword() : Passwords.generate();

      final org.jclouds.compute.domain.Image image = template.getImage();

      // provision all storages based on hardware
      List<? extends Volume> volumes = hardware.getVolumes();
      List<String> storageIds = Lists.newArrayListWithExpectedSize(volumes.size());

      int i = 1;
      for (final Volume volume : volumes)
         try {
            logger.trace("<< provisioning storage '%s'", volume);
            final Storage.Request.CreatePayload.Builder storageBuilder = Storage.Request.creatingBuilder();
            if (i == 1) {
               storageBuilder.mountImageId(image.getId());
               // we don't need to pass password to the API if we're using a snapshot
               Provisionable.Type provisionableType = Provisionable.Type.fromValue(
                       image.getUserMetadata().get(ProvisionableToImage.KEY_PROVISIONABLE_TYPE));
               if (provisionableType == Provisionable.Type.IMAGE)
                  storageBuilder.imagePassword(password);
            }
            storageBuilder.dataCenterId(dataCenterId)
                    .name(format("%s-disk-%d", name, i++))
                    .size(volume.getSize());

            String storageId = (String) provisioningManager.provision(jobFactory.create(dataCenterId, new Supplier<Object>() {

               @Override
               public Object get() {
                  return api.storageApi().createStorage(storageBuilder.build());
               }
            }));

            storageIds.add(storageId);
            logger.trace(">> provisioning complete for storage. returned id='%s'", storageId);
         } catch (Exception ex) {
            if (i - 1 == 1) // if first storage (one with image) provisioning fails; stop method
               throw Throwables.propagate(ex);
            logger.warn(ex, ">> failed to provision storage. skipping..");
         }

      int lanId = DEFAULT_LAN_ID;
      if (options.getNetworks() != null)
         try {
            String networkId = Iterables.get(options.getNetworks(), 0);
            lanId = Integer.valueOf(networkId);
         } catch (Exception ex) {
            logger.warn("no valid network id found from options. using default id='%d'", DEFAULT_LAN_ID);
         }

      Double cores = ComputeServiceUtils.getCores(hardware);

      // provision server and connect boot storage (first provisioned)
      String serverId = null;
      try {
         String storageBootDeviceId = Iterables.get(storageIds, 0); // must have atleast 1
         final Server.Request.CreatePayload serverRequest = Server.Request.creatingBuilder()
                 .dataCenterId(dataCenterId)
                 .name(name)
                 .bootFromStorageId(storageBootDeviceId)
                 .cores(cores.intValue())
                 .ram(hardware.getRam())
                 .availabilityZone(AvailabilityZone.AUTO)
                 .hasInternetAccess(true)
                 .lanId(lanId)
                 .build();
         logger.trace("<< provisioning server '%s'", serverRequest);

         serverId = (String) provisioningManager.provision(jobFactory.create(dataCenterId, new Supplier<Object>() {

            @Override
            public Object get() {
               return api.serverApi().createServer(serverRequest);
            }
         }));
         logger.trace(">> provisioning complete for server. returned id='%s'", serverId);

      } catch (Exception ex) {
         logger.error(ex, ">> failed to provision server. rollbacking..");
         destroyStorages(storageIds, dataCenterId);
         throw Throwables.propagate(ex);
      }

      // connect the rest of storages to server; delete if fails
      final int storageCount = storageIds.size();
      for (int j = 1; j < storageCount; j++) { // skip first; already connected
         String storageId = storageIds.get(j);
         try {
            logger.trace("<< connecting storage '%s' to server '%s'", storageId, serverId);
            final Storage.Request.ConnectPayload request = Storage.Request.connectingBuilder()
                    .storageId(storageId)
                    .serverId(serverId)
                    .build();

            provisioningManager.provision(jobFactory.create(group, new Supplier<Object>() {

               @Override
               public Object get() {
                  return api.storageApi().connectStorageToServer(request);
               }
            }));

            logger.trace(">> storage connected.");
         } catch (Exception ex) {
            // delete unconnected storage
            logger.warn(ex, ">> failed to connect storage '%s'. deleting..", storageId);
            destroyStorage(storageId, dataCenterId);
         }
      }

      // Last paranoid check
      waitDcUntilAvailable.apply(dataCenterId);

      LoginCredentials serverCredentials = LoginCredentials.builder()
              .user(loginUser)
              .password(password)
              .build();

      Server server = getNode(serverId);

      return new NodeAndInitialCredentials<Server>(server, serverId, serverCredentials);
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      // Max [cores=48] [disk size per storage=2048GB] [ram=200704 MB]
      List<Hardware> hardwares = Lists.newArrayList();
      for (int core = 1; core <= 48; core++)
         for (int ram : new int[]{1024, 2 * 1024, 4 * 1024, 8 * 1024,
            10 * 1024, 16 * 1024, 24 * 1024, 28 * 1024, 32 * 1024})
            for (float size : new float[]{10, 20, 30, 50, 80, 100, 150, 200, 250, 500}) {
               String id = String.format("cpu=%d,ram=%s,disk=%f", core, ram, size);
               hardwares.add(new HardwareBuilder()
                       .ids(id)
                       .ram(ram)
                       .hypervisor("kvm")
                       .name(id)
                       .processor(new Processor(core, 1d))
                       .volume(new VolumeImpl(size, true, true))
                       .build());
            }
      return hardwares;
   }

   @Override
   public Iterable<Provisionable> listImages() {
      // fetch images..
      ListenableFuture<List<Image>> images = executorService.submit(new Callable<List<Image>>() {

         @Override
         public List<Image> call() throws Exception {
            logger.trace("<< fetching images..");
            // Filter HDD types only, since JClouds doesn't have a concept of "CD-ROM" anyway
            Iterable<Image> filteredImages = Iterables.filter(api.imageApi().getAllImages(), new Predicate<Image>() {

               @Override
               public boolean apply(Image image) {
                  return image.type() == Image.Type.HDD;
               }
            });
            logger.trace(">> images fetched.");

            return ImmutableList.copyOf(filteredImages);
         }

      });
      // and snapshots at the same time
      ListenableFuture<List<Snapshot>> snapshots = executorService.submit(new Callable<List<Snapshot>>() {

         @Override
         public List<Snapshot> call() throws Exception {
            logger.trace("<< fetching snapshots");
            List<Snapshot> remoteSnapshots = api.snapshotApi().getAllSnapshots();
            logger.trace(">> snapshots feched.");

            return remoteSnapshots;
         }

      });

      return Iterables.concat(getUnchecked(images), getUnchecked(snapshots));
   }

   @Override
   public Provisionable getImage(String id) {
      // try search images
      logger.trace("<< searching for image with id=%s", id);
      Image image = api.imageApi().getImage(id);
      if (image != null) {
         logger.trace(">> found image [%s].", image.name());
         return image;
      }
      // try search snapshots
      logger.trace("<< not found from images. searching for snapshot with id=%s", id);
      Snapshot snapshot = api.snapshotApi().getSnapshot(id);
      if (snapshot != null) {
         logger.trace(">> found snapshot [%s]", snapshot.name());
         return snapshot;
      }
      throw new ResourceNotFoundException("No image/snapshot with id '" + id + "' was found");
   }

   @Override
   public Iterable<DataCenter> listLocations() {
      logger.trace("<< fetching datacenters..");
      final DataCenterApi dcApi = api.dataCenterApi();

      // Fetch all datacenters
      ListenableFuture<List<DataCenter>> futures = allAsList(transform(dcApi.getAllDataCenters(),
              new Function<DataCenter, ListenableFuture<DataCenter>>() {

                 @Override
                 public ListenableFuture<DataCenter> apply(final DataCenter input) {
                    // Fetch more details in parallel
                    return executorService.submit(new Callable<DataCenter>() {
                       @Override
                       public DataCenter call() throws Exception {
                          logger.trace("<< fetching datacenter with id [%s]", input.id());
                          return dcApi.getDataCenter(input.id());
                       }

                    });
                 }
              }));

      return getUnchecked(futures);
   }

   @Override
   public Server getNode(String id) {
      logger.trace("<< searching for server with id=%s", id);

      Server server = api.serverApi().getServer(id);
      if (server != null)
         logger.trace(">> found server [%s]", server.name());
      return server;
   }

   @Override
   public void destroyNode(String nodeId) {
      ServerApi serverApi = api.serverApi();
      Server server = serverApi.getServer(nodeId);
      if (server != null) {
         String dataCenterId = server.dataCenter().id();
         for (Storage storage : server.storages())
            destroyStorage(storage.id(), dataCenterId);

         try {
            destroyServer(nodeId, dataCenterId);
         } catch (Exception ex) {
            logger.warn(ex, ">> failed to delete server with id=%s", nodeId);
         }
      }
   }

   @Override
   public void rebootNode(final String id) {
      // Fail pre-emptively if not found
      final Server node = getRequiredNode(id);
      final DataCenter dataCenter = node.dataCenter();
      provisioningManager.provision(jobFactory.create(dataCenter.id(), new Supplier<Object>() {

         @Override
         public Object get() {
            api.serverApi().resetServer(id);

            return node;
         }
      }));
   }

   @Override
   public void resumeNode(final String id) {
      final Server node = getRequiredNode(id);
      if (node.status() == Server.Status.RUNNING)
         return;

      final DataCenter dataCenter = node.dataCenter();
      provisioningManager.provision(jobFactory.create(dataCenter.id(), new Supplier<Object>() {

         @Override
         public Object get() {
            api.serverApi().startServer(id);

            return node;
         }
      }));
   }

   @Override
   public void suspendNode(final String id) {
      final Server node = getRequiredNode(id);
      // Intentionally didn't include SHUTDOWN (only achieved via UI; soft-shutdown). 
      // A SHUTOFF server is no longer billed, so we execute method for all other status
      if (node.status() == Server.Status.SHUTOFF)
         return;

      final DataCenter dataCenter = node.dataCenter();
      provisioningManager.provision(jobFactory.create(dataCenter.id(), new Supplier<Object>() {

         @Override
         public Object get() {
            api.serverApi().stopServer(id);

            return node;
         }
      }));
   }

   @Override
   public Iterable<Server> listNodes() {
      logger.trace(">> fetching all servers..");
      List<Server> servers = api.serverApi().getAllServers();
      logger.trace(">> servers fetched.");
      return servers;
   }

   @Override
   public Iterable<Server> listNodesByIds(final Iterable<String> ids) {
      // Only fetch the requested nodes. Do it in parallel.
      ListenableFuture<List<Server>> futures = allAsList(transform(ids,
              new Function<String, ListenableFuture<Server>>() {

                 @Override
                 public ListenableFuture<Server> apply(final String input) {
                    return executorService.submit(new Callable<Server>() {

                       @Override
                       public Server call() throws Exception {
                          return getNode(input);
                       }
                    });
                 }
              }));

      return getUnchecked(futures);
   }

   private void destroyServer(final String serverId, final String dataCenterId) {
      try {
         logger.trace("<< deleting server with id=%s", serverId);
         provisioningManager.provision(jobFactory.create(dataCenterId, new Supplier<Object>() {

            @Override
            public Object get() {
               api.serverApi().deleteServer(serverId);

               return serverId;
            }
         }));
         logger.trace(">> server '%s' deleted.", serverId);
      } catch (Exception ex) {
         logger.warn(ex, ">> failed to delete server with id=%s", serverId);
      }
   }

   private void destroyStorages(List<String> storageIds, String dataCenterId) {
      for (String storageId : storageIds)
         destroyStorage(storageId, dataCenterId);
   }

   private void destroyStorage(final String storageId, final String dataCenterId) {
      try {
         logger.trace("<< deleting storage with id=%s", storageId);
         provisioningManager.provision(jobFactory.create(dataCenterId, new Supplier<Object>() {

            @Override
            public Object get() {
               api.storageApi().deleteStorage(storageId);

               return storageId;
            }
         }));
         logger.trace(">> storage '%s' deleted.", storageId);
      } catch (Exception ex) {
         logger.warn(ex, ">> failed to delete storage with id=%s", storageId);
      }
   }

   private Server getRequiredNode(String nodeId) {
      Server node = getNode(nodeId);
      if (node == null)
         throw new ResourceNotFoundException("Node with id'" + nodeId + "' was not found.");
      return node;
   }
}
