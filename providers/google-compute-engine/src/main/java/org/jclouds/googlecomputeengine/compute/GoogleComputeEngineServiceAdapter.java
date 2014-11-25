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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.functions.Resources;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.domain.AttachDisk;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling.OnHostMaintenance;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Region;
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.features.InstanceApi;
import org.jclouds.location.suppliers.all.JustProvider;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
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
   private final Map<URI, URI> diskToSourceImage;
   private final Predicate<AtomicReference<Operation>> operationDone;
   private final Predicate<AtomicReference<Instance>> instanceVisible;
   private final FirewallTagNamingConvention.Factory firewallTagNamingConvention;
   private final List<String> imageProjects;

   @Inject GoogleComputeEngineServiceAdapter(JustProvider justProvider, GoogleComputeEngineApi api,
                                            Predicate<AtomicReference<Operation>> operationDone,
                                            Predicate<AtomicReference<Instance>> instanceVisible,
                                            Map<URI, URI> diskToSourceImage,
                                            Resources resources,
                                            FirewallTagNamingConvention.Factory firewallTagNamingConvention,
                                            @Named(IMAGE_PROJECTS) String imageProjects) {
      this.justProvider = justProvider;
      this.api = api;
      this.operationDone = operationDone;
      this.instanceVisible = instanceVisible;
      this.diskToSourceImage = diskToSourceImage;
      this.resources = resources;
      this.firewallTagNamingConvention = firewallTagNamingConvention;
      this.imageProjects = Splitter.on(',').omitEmptyStrings().splitToList(imageProjects);
   }

   @Override public NodeAndInitialCredentials<Instance> createNodeWithGroupEncodedIntoName(String group, String name,
         Template template) {

      checkNotNull(template, "template");

      GoogleComputeEngineTemplateOptions options = GoogleComputeEngineTemplateOptions.class.cast(template.getOptions());
      checkNotNull(options.network(), "template options must specify a network");
      Hardware hardware = checkNotNull(template.getHardware(), "hardware must be set");

      checkNotNull(hardware.getUri(), "hardware must have a URI");
      checkNotNull(template.getImage().getUri(), "image URI is null");

      List<AttachDisk> disks = Lists.newArrayList();
      disks.add(AttachDisk.newBootDisk(template.getImage().getUri()));
      for (URI existingDisk : options.additionalDisks()) {
         disks.add(AttachDisk.existingDisk(existingDisk));
      }

      NewInstance newInstance = NewInstance.create(
            name, // name
            hardware.getUri(), // machineType
            options.network(), // network
            disks, // disks
            group // description
      );

      // Add tags from template and for security groups
      newInstance.tags().items().addAll(options.getTags());
      FirewallTagNamingConvention naming = firewallTagNamingConvention.get(group);
      for (int port : options.getInboundPorts()) {
         newInstance.tags().items().add(naming.name(port));
      }

      // Add metadata from template and for ssh key and image id
      newInstance.metadata().putAll(options.getUserMetadata());
      if (options.getPublicKey() != null) { // TODO: why are we doing this?
         newInstance.metadata().put("sshKeys", format("%s:%s %s@localhost", checkNotNull(options.getLoginUser(),
               "loginUser cannot be null"), options.getPublicKey(), options.getLoginUser()));
      }

      String zone = template.getLocation().getId();
      InstanceApi instanceApi = api.instancesInZone(zone);
      Operation create = instanceApi.create(newInstance);

      // We need to see the created instance so that we can access the newly created disk.
      AtomicReference<Instance> instance = Atomics.newReference(Instance.create( //
            "0000000000000000000", // id can't be null, but isn't available until provisioning is done.
            create.targetLink(), // selfLink
            newInstance.name(), // name
            newInstance.description(), // description
            newInstance.tags(), // tags
            newInstance.machineType(), // machineType
            Instance.Status.PROVISIONING, // status
            null, // statusMessage
            create.zone(), // zone
            null, // networkInterfaces
            null, // disks
            newInstance.metadata(), // metadata
            null, // serviceAccounts
            Scheduling.create(OnHostMaintenance.MIGRATE, true) // scheduling
      ));
      checkState(instanceVisible.apply(instance), "instance %s is not api visible!", instance.get());

      // Add lookup for InstanceToNodeMetadata
      diskToSourceImage.put(instance.get().disks().get(0).source(), template.getImage().getUri());

      LoginCredentials credentials = getFromImageAndOverrideIfRequired(template.getImage(), options);
      return new NodeAndInitialCredentials<Instance>(instance.get(), instance.get().selfLink().toString(), credentials);
   }

   @Override public Iterable<MachineType> listHardwareProfiles() {
      return filter(concat(api.aggregatedList().machineTypes()), new Predicate<MachineType>() {
         @Override public boolean apply(MachineType input) {
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

   @Override public void resumeNode(String name) {
      throw new UnsupportedOperationException("resume is not supported by GCE");
   }

   @Override  public void suspendNode(String name) {
      throw new UnsupportedOperationException("suspend is not supported by GCE");
   }

   // TODO: this entire method is questionable. needs a test case, or to be removed.
   private static LoginCredentials getFromImageAndOverrideIfRequired(org.jclouds.compute.domain.Image image,
                                                              GoogleComputeEngineTemplateOptions options) {
      LoginCredentials defaultCredentials = image.getDefaultCredentials();
      String[] keys = defaultCredentials.getPrivateKey().split(":");
      String publicKey = keys[0];
      String privateKey = keys[1];

      LoginCredentials.Builder credentialsBuilder = defaultCredentials.toBuilder();
      credentialsBuilder.privateKey(privateKey);

      // LoginCredentials from image stores the public key along with the private key in the privateKey field
      // @see GoogleComputePopulateDefaultLoginCredentialsForImageStrategy
      // so if options doesn't have a public key set we set it from the default
      if (options.getPublicKey() == null) {
         options.authorizePublicKey(publicKey);
      }
      if (options.hasLoginPrivateKeyOption()) {
         credentialsBuilder.privateKey(options.getPrivateKey());
      }
      if (options.getLoginUser() != null) {
         credentialsBuilder.identity(options.getLoginUser());
      }
      if (options.hasLoginPasswordOption()) {
         credentialsBuilder.password(options.getLoginPassword());
      }
      if (options.shouldAuthenticateSudo() != null) {
         credentialsBuilder.authenticateSudo(options.shouldAuthenticateSudo());
      }
      LoginCredentials credentials = credentialsBuilder.build();
      options.overrideLoginCredentials(credentials);
      return credentials;
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

   private static String toName(URI link) {
      String path = link.getPath();
      return path.substring(path.lastIndexOf('/') + 1);
   }
}
