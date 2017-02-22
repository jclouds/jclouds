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
package org.jclouds.googlecomputeengine.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.jclouds.googlecloud.internal.ListPages.concat;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineProperties.IMAGE_PROJECTS;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.functions.Resources;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.domain.AttachDisk;
import org.jclouds.googlecomputeengine.domain.DiskType;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling.OnHostMaintenance;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Region;
import org.jclouds.googlecomputeengine.domain.Tags;
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.features.InstanceApi;
import org.jclouds.location.suppliers.all.JustProvider;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * This implementation maps the following:
 * <ul>
 *    <li>{@linkplain NodeMetadata#getId()} to {@link Instance#selfLink()}</li>
 *    <li>{@linkplain NodeMetadata#getGroup()} to {@link Instance#metadata()} as {@code jclouds-group}</li>
 *    <li>{@linkplain NodeMetadata#getImageId()} to {@link Instance#metadata()} as {@code jclouds-image}</li>
 *    <li>{@linkplain Hardware#getId()} to {@link MachineType#selfLink()}</li>
 *    <li>{@linkplain org.jclouds.compute.domain.Image#getId()} to {@link Image#selfLink()}</li>
 *    <li>{@linkplain Location#getId()} to {@link org.jclouds.googlecomputeengine.domain.Zone#name()}</li>
 *    <li>{@linkplain Location#getDescription()} to {@link Zone#selfLink()}</li>
 * </ul>
 */
public final class GoogleComputeEngineServiceAdapter
      implements ComputeServiceAdapter<Instance, MachineType, Image, Location> {

   private final JustProvider justProvider;
   private final GoogleComputeEngineApi api;
   private final Resources resources;
   private final Predicate<AtomicReference<Operation>> operationDone;
   private final Predicate<AtomicReference<Instance>> instanceVisible;
   private final Function<Map<String, ?>, String> windowsPasswordGenerator;
   private final FirewallTagNamingConvention.Factory firewallTagNamingConvention;
   private final List<String> imageProjects;
   private final LoadingCache<URI, Optional<Image>> diskURIToImage;

   @Inject GoogleComputeEngineServiceAdapter(JustProvider justProvider, GoogleComputeEngineApi api,
                                            Predicate<AtomicReference<Operation>> operationDone,
                                            Predicate<AtomicReference<Instance>> instanceVisible,
                                            Function<Map<String, ?>, String> windowsPasswordGenerator,
                                            Resources resources,
                                            FirewallTagNamingConvention.Factory firewallTagNamingConvention,
                                            @Named(IMAGE_PROJECTS) String imageProjects,
                                            LoadingCache<URI, Optional<Image>> diskURIToImage) {
      this.justProvider = justProvider;
      this.api = api;
      this.operationDone = operationDone;
      this.instanceVisible = instanceVisible;
      this.windowsPasswordGenerator = windowsPasswordGenerator;
      this.resources = resources;
      this.firewallTagNamingConvention = firewallTagNamingConvention;
      this.imageProjects = Splitter.on(',').omitEmptyStrings().splitToList(imageProjects);
      this.diskURIToImage = diskURIToImage;
   }

   @Override public NodeAndInitialCredentials<Instance> createNodeWithGroupEncodedIntoName(String group, String name,
         Template template) {
      GoogleComputeEngineTemplateOptions options = GoogleComputeEngineTemplateOptions.class.cast(template.getOptions());

      checkNotNull(options.getNetworks(), "template options must specify a network");
      checkNotNull(template.getHardware().getUri(), "hardware must have a URI");
      checkNotNull(template.getImage().getUri(), "image URI is null");

      String zone = template.getLocation().getId();

      List<AttachDisk> disks = Lists.newArrayList();
      disks.add(AttachDisk.newBootDisk(template.getImage().getUri(), getDiskTypeArgument(options, zone)));

      Iterator<String> networks = options.getNetworks().iterator();

      URI network = URI.create(networks.next());
      assert !networks.hasNext() : "Error: Options should specify only one network";

      Scheduling scheduling = getScheduling(options);

      NewInstance newInstance = new NewInstance.Builder( name,
            template.getHardware().getUri(), // machineType
            network,
            disks)
            .description(group)
            .tags(Tags.create(null, ImmutableList.copyOf(options.getTags())))
            .serviceAccounts(options.serviceAccounts())
            .scheduling(scheduling)
            .build();

      // Add metadata from template and for ssh key and image id
      newInstance.metadata().putAll(options.getUserMetadata());

      LoginCredentials credentials = resolveNodeCredentials(template);
      if (options.getPublicKey() != null) {
         newInstance.metadata().put("sshKeys",
               format("%s:%s %s@localhost", credentials.getUser(), options.getPublicKey(), credentials.getUser()));
      }

      InstanceApi instanceApi = api.instancesInZone(zone);
      Operation create = instanceApi.create(newInstance);

      // We need to see the created instance so that we can access the newly created disk.
      AtomicReference<Instance> instance = Atomics.newReference(Instance.create( //
            "0000000000000000000", // id can't be null, but isn't available until provisioning is done.
            null, // creationTimestamp
            create.targetLink(), // selfLink
            newInstance.name(), // name
            newInstance.description(), // description
            newInstance.tags(), // tags
            newInstance.machineType(), // machineType
            Instance.Status.PROVISIONING, // status
            null, // statusMessage
            create.zone(), // zone
            null, // canIpForward
            null, // networkInterfaces
            null, // disks
            newInstance.metadata(), // metadata
            newInstance.serviceAccounts(), // serviceAccounts
            scheduling) // scheduling
      );
      checkState(instanceVisible.apply(instance), "instance %s is not api visible!", instance.get());

      // Add lookup for InstanceToNodeMetadata
      diskURIToImage.getUnchecked(instance.get().disks().get(0).source());

      if ((options.autoCreateWindowsPassword() != null && options.autoCreateWindowsPassword())
                  || OsFamily.WINDOWS == template.getImage().getOperatingSystem().getFamily()) {
           Map<String, ?> params = ImmutableMap.of("instance", instance, "zone", zone, "email", create.user(), "userName", credentials.getUser());
           String password = windowsPasswordGenerator.apply(params);
           credentials = LoginCredentials.builder(credentials)
                          .password(password)
                          .build();
         }
      return new NodeAndInitialCredentials<Instance>(instance.get(), instance.get().selfLink().toString(), credentials);
   }

   @Override public Iterable<MachineType> listHardwareProfiles() {
      return filter(concat(api.aggregatedList().machineTypes()), new Predicate<MachineType>() {
         @Override
         public boolean apply(MachineType input) {
            return input.deprecated() == null;
         }
      });
   }

   @Override public Iterable<Image> listImages() {
      List<Iterable<Image>> images = newArrayList();

      images.add(concat(api.images().list()));

      for (String project : imageProjects) {
         images.add(concat(api.images().listInProject(project)));
      }

      return Iterables.concat(images);
   }

   @Override public Image getImage(String selfLink) {
      return api.images().get(URI.create(checkNotNull(selfLink, "id")));
   }

   /**  Unlike EC2, you cannot default GCE instances to a region. Hence, we constrain to zones. */
   @Override public Iterable<Location> listLocations() {
      Location provider = justProvider.get().iterator().next();
      ImmutableList.Builder<Location> zones = ImmutableList.builder();
      for (Region region : concat(api.regions().list())) {
         Location regionLocation = new LocationBuilder()
               .scope(LocationScope.REGION)
               .id(region.name())
               .description(region.selfLink().toString())
               .parent(provider).build();
         for (URI zoneSelfLink : region.zones()) {
            String zoneName = toName(zoneSelfLink);
            zones.add(new LocationBuilder()
                  .scope(LocationScope.ZONE)
                  .id(zoneName)
                  .description(zoneSelfLink.toString())
                  .parent(regionLocation).build());
         }
      }
      return zones.build();
   }

   @Override public Instance getNode(String selfLink) {
      return resources.instance(URI.create(checkNotNull(selfLink, "id")));
   }

   @Override public Iterable<Instance> listNodes() {
      return concat(api.aggregatedList().instances());
   }

   @Override public Iterable<Instance> listNodesByIds(final Iterable<String> selfLinks) {
      return filter(listNodes(), new Predicate<Instance>() { // TODO: convert to server-side filter
         @Override public boolean apply(Instance instance) {
            return Iterables.contains(selfLinks, instance.selfLink().toString());
         }
      });
   }

   @Override public void destroyNode(String selfLink) {
      waitOperationDone(resources.delete(URI.create(checkNotNull(selfLink, "id"))));
   }

   @Override public void rebootNode(String selfLink) {
      waitOperationDone(resources.resetInstance(URI.create(checkNotNull(selfLink, "id"))));
   }

   @Override public void resumeNode(String selfLink) {
      waitOperationDone(resources.startInstance(URI.create(checkNotNull(selfLink, "id"))));
   }

   @Override public void suspendNode(String selfLink) {
      waitOperationDone(resources.stopInstance(URI.create(checkNotNull(selfLink, "id"))));
   }

   private void waitOperationDone(Operation operation) {
      AtomicReference<Operation> operationRef = Atomics.newReference(operation);

      // wait for the operation to complete
      if (!operationDone.apply(operationRef)) {
         throw new UncheckedTimeoutException("operation did not reach DONE state" + operationRef.get());
      }

      // check if the operation failed
      if (operationRef.get().httpErrorStatusCode() != null) {
         throw new IllegalStateException(
               "operation failed. Http Error Code: " + operationRef.get().httpErrorStatusCode() +
                     " HttpError: " + operationRef.get().httpErrorMessage());
      }
   }

   private LoginCredentials resolveNodeCredentials(Template template) {
      TemplateOptions options = template.getOptions();
      LoginCredentials.Builder credentials = LoginCredentials.builder(template.getImage().getDefaultCredentials());
      if (!Strings.isNullOrEmpty(options.getLoginUser())) {
         credentials.user(options.getLoginUser());
      }
      if (!Strings.isNullOrEmpty(options.getLoginPrivateKey())) {
         credentials.privateKey(options.getLoginPrivateKey());
      }
      if (!Strings.isNullOrEmpty(options.getLoginPassword())) {
         credentials.password(options.getLoginPassword());
      }
      if (options.shouldAuthenticateSudo() != null) {
         credentials.authenticateSudo(options.shouldAuthenticateSudo());
      }
      return credentials.build();
   }

   private static String toName(URI link) {
      String path = link.getPath();
      return path.substring(path.lastIndexOf('/') + 1);
   }

   private URI getDiskTypeArgument(GoogleComputeEngineTemplateOptions options, String zone) {
      if (options.bootDiskType() != null) {
         DiskType diskType = api.diskTypesInZone(zone).get(options.bootDiskType());
         if (diskType != null) {
            return diskType.selfLink();
         }
      }

      return null;
   }

   public Scheduling getScheduling(GoogleComputeEngineTemplateOptions options) {
      OnHostMaintenance onHostMaintenance = OnHostMaintenance.MIGRATE;
      boolean automaticRestart = true;

      // Preemptible instances cannot use a MIGRATE maintenance strategy or automatic restarts
      if (options.preemptible()) {
         onHostMaintenance = OnHostMaintenance.TERMINATE;
         automaticRestart = false;
      }

      return Scheduling.create(onHostMaintenance, automaticRestart, options.preemptible());
   }
}
