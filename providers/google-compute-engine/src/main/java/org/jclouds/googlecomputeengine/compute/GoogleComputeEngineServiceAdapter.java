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
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.tryFind;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.CENTOS_PROJECT;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.DEBIAN_PROJECT;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.GCE_BOOT_DISK_SUFFIX;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.GCE_DELETE_BOOT_DISK_METADATA_KEY;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.GCE_IMAGE_METADATA_KEY;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig.Type;
import static org.jclouds.googlecomputeengine.predicates.InstancePredicates.isBootDisk;
import static org.jclouds.util.Predicates2.retry;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.AttachedDisk;
import org.jclouds.googlecomputeengine.domain.Instance.PersistentAttachedDisk;
import org.jclouds.googlecomputeengine.domain.InstanceInZone;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate.PersistentDisk;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate.PersistentDisk.Mode;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.domain.MachineTypeInZone;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.SlashEncodedIds;
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.features.InstanceApi;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.inject.Inject;

public class GoogleComputeEngineServiceAdapter implements ComputeServiceAdapter<InstanceInZone, MachineTypeInZone, Image, Zone> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final GoogleComputeEngineApi api;
   private final Supplier<String> userProject;
   private final Supplier<Map<URI, ? extends Location>> zones;
   private final Function<TemplateOptions, ImmutableMap.Builder<String, String>> metatadaFromTemplateOptions;
   private final Predicate<AtomicReference<Operation>> retryOperationDonePredicate;
   private final long operationCompleteCheckInterval;
   private final long operationCompleteCheckTimeout;
   private final FirewallTagNamingConvention.Factory firewallTagNamingConvention;

   @Inject
   public GoogleComputeEngineServiceAdapter(GoogleComputeEngineApi api,
                                            @UserProject Supplier<String> userProject,
                                            Function<TemplateOptions,
                                                    ImmutableMap.Builder<String, String>> metatadaFromTemplateOptions,
                                            @Named("zone") Predicate<AtomicReference<Operation>> operationDonePredicate,
                                            @Named(OPERATION_COMPLETE_INTERVAL) Long operationCompleteCheckInterval,
                                            @Named(OPERATION_COMPLETE_TIMEOUT) Long operationCompleteCheckTimeout,
                                            @Memoized Supplier<Map<URI, ? extends Location>> zones,
                                            FirewallTagNamingConvention.Factory firewallTagNamingConvention) {
      this.api = checkNotNull(api, "google compute api");
      this.userProject = checkNotNull(userProject, "user project name");
      this.metatadaFromTemplateOptions = checkNotNull(metatadaFromTemplateOptions,
              "metadata from template options function");
      this.operationCompleteCheckInterval = checkNotNull(operationCompleteCheckInterval,
              "operation completed check interval");
      this.operationCompleteCheckTimeout = checkNotNull(operationCompleteCheckTimeout,
                                                        "operation completed check timeout");
      this.retryOperationDonePredicate = retry(operationDonePredicate, operationCompleteCheckTimeout,
                                               operationCompleteCheckInterval, TimeUnit.MILLISECONDS);
      this.zones = checkNotNull(zones, "zones");
      this.firewallTagNamingConvention = checkNotNull(firewallTagNamingConvention, "firewallTagNamingConvention");
   }

   @Override
   public NodeAndInitialCredentials<InstanceInZone> createNodeWithGroupEncodedIntoName(
           final String group, final String name, final Template template) {

      checkNotNull(template, "template");

      GoogleComputeEngineTemplateOptions options = GoogleComputeEngineTemplateOptions.class.cast(template.getOptions()).clone();
      checkState(options.getNetwork().isPresent(), "network was not present in template options");
      Hardware hardware = checkNotNull(template.getHardware(), "hardware must be set");

      checkNotNull(hardware.getUri(), "hardware must have a URI");
      checkNotNull(template.getImage().getUri(), "image URI is null");

      // Note that the ordering is significant here - the first disk must be the boot disk.
      List<PersistentDisk> disks = Lists.newArrayList();

      if (!tryFind(options.getDisks(), isBootDisk()).isPresent()) {
         Disk bootDisk = createBootDisk(template, name);

         disks.add(new PersistentDisk(Mode.READ_WRITE,
                                      bootDisk.getSelfLink(),
                                      null,
                                      true,
                                      true));
      }

      disks.addAll(options.getDisks());

      InstanceTemplate instanceTemplate = InstanceTemplate.builder()
              .forMachineType(hardware.getUri());

      if (options.isEnableNat()) {
         instanceTemplate.addNetworkInterface(options.getNetwork().get(), Type.ONE_TO_ONE_NAT);
      } else {
         instanceTemplate.addNetworkInterface(options.getNetwork().get());
      }

      instanceTemplate.disks(disks);

      LoginCredentials credentials = getFromImageAndOverrideIfRequired(template.getImage(), options);

      ImmutableMap.Builder<String, String> metadataBuilder = metatadaFromTemplateOptions.apply(options);

      metadataBuilder.put(GCE_IMAGE_METADATA_KEY, template.getImage().getUri().toString());

      if (!options.shouldKeepBootDisk()) {
         metadataBuilder.put(GCE_DELETE_BOOT_DISK_METADATA_KEY, Boolean.TRUE.toString());
      }

      instanceTemplate.metadata(metadataBuilder.build());
      instanceTemplate.serviceAccounts(options.getServiceAccounts());

      final InstanceApi instanceApi = api.getInstanceApiForProject(userProject.get());
      final String zone = template.getLocation().getId();
      Operation operation = instanceApi.createInZone(name, zone, instanceTemplate);

      if (options.shouldBlockUntilRunning()) {
         waitOperationDone(operation);
      }

      // some times the newly created instances are not immediately returned
      AtomicReference<Instance> instance = Atomics.newReference();

      retry(new Predicate<AtomicReference<Instance>>() {
         @Override
         public boolean apply(AtomicReference<Instance> input) {
            input.set(instanceApi.getInZone(zone, name));
            return input.get() != null;
         }
      }, operationCompleteCheckTimeout, operationCompleteCheckInterval, MILLISECONDS).apply(instance);

      if (!options.getTags().isEmpty()) {
         Operation tagsOperation = instanceApi.setTagsInZone(zone,
                 name, options.getTags(), instance.get().getTags().getFingerprint());

         waitOperationDone(tagsOperation);

         retry(new Predicate<AtomicReference<Instance>>() {
            @Override
            public boolean apply(AtomicReference<Instance> input) {
               input.set(instanceApi.getInZone(zone, name));
               return input.get() != null;
            }
         }, operationCompleteCheckTimeout, operationCompleteCheckInterval, MILLISECONDS).apply(instance);
      }

      // Add tags for security groups
      final FirewallTagNamingConvention naming = firewallTagNamingConvention.get(group);
      Set<String> tags = FluentIterable.from(Ints.asList(options.getInboundPorts()))
              .transform(new Function<Integer, String>(){
                       @Override
                       public String apply(Integer input) {
                          return input != null
                                  ? naming.name(input)
                                  : null;
                       }
                    })
              .toSet();
      instanceApi.setTagsInZone(zone, instance.get().getName(), tags, instance.get().getTags().getFingerprint());

      InstanceInZone instanceInZone = new InstanceInZone(instance.get(), zone);

      return new NodeAndInitialCredentials<InstanceInZone>(instanceInZone, instanceInZone.slashEncode(), credentials);
   }

   private Disk createBootDisk(Template template, String instanceName) {
      URI imageUri = template.getImage().getUri();

      GoogleComputeEngineTemplateOptions options = GoogleComputeEngineTemplateOptions.class.cast(template.getOptions()).clone();

      int diskSize = options.getBootDiskSize().or(10l).intValue();

      String diskName = instanceName + "-" + GCE_BOOT_DISK_SUFFIX;

      Operation diskOperation = api.getDiskApiForProject(userProject.get())
                                   .createFromImageWithSizeInZone(imageUri.toString(),
                                                                  diskName,
                                                                  diskSize,
                                                                  template.getLocation().getId());

      waitOperationDone(diskOperation);

      return api.getDiskApiForProject(userProject.get()).getInZone(template.getLocation().getId(),
                                                                   diskName);
   }

   @Override
   public Iterable<MachineTypeInZone> listHardwareProfiles() {
      ImmutableSet.Builder<MachineTypeInZone> builder = ImmutableSet.builder();

      for (final Location zone : zones.get().values()) {
         builder.addAll(api.getMachineTypeApiForProject(userProject.get())
                 .listInZone(zone.getId())
                 .concat()
                 .filter(new Predicate<MachineType>() {
                    @Override
                    public boolean apply(MachineType input) {
                       return !input.getDeprecated().isPresent();
                    }
                 })
                 .transform(new Function<MachineType, MachineTypeInZone>() {

                    @Override
                    public MachineTypeInZone apply(MachineType arg0) {
                       return new MachineTypeInZone(arg0, arg0.getZone());
                    }
                 }));
      }

      return builder.build();
   }

   @Override
   public Iterable<Image> listImages() {
      return ImmutableSet.<Image>builder()
              .addAll(api.getImageApiForProject(userProject.get()).list().concat())
              .addAll(api.getImageApiForProject(DEBIAN_PROJECT).list().concat())
              .addAll(api.getImageApiForProject(CENTOS_PROJECT).list().concat())
              .build();
   }

   @Override
   public Image getImage(String id) {
      return Objects.firstNonNull(api.getImageApiForProject(userProject.get()).get(id),
                                  Objects.firstNonNull(api.getImageApiForProject(DEBIAN_PROJECT).get(id),
                                          api.getImageApiForProject(CENTOS_PROJECT).get(id)));

   }

   @Override
   public Iterable<Zone> listLocations() {
      return api.getZoneApiForProject(userProject.get()).list().concat();
   }

   @Override
   public InstanceInZone getNode(String name) {
      SlashEncodedIds slashEncodedIds = SlashEncodedIds.fromSlashEncoded(name);

      Instance instance = api.getInstanceApiForProject(userProject.get()).getInZone(slashEncodedIds.getFirstId(),
              slashEncodedIds.getSecondId());

      return instance == null ?  null : new InstanceInZone(instance, slashEncodedIds.getFirstId());
   }

   @Override
   public Iterable<InstanceInZone> listNodes() {
      return FluentIterable.from(zones.get().values()).transformAndConcat(new Function<Location, ImmutableSet<InstanceInZone>>() {
         @Override
         public ImmutableSet<InstanceInZone> apply(final Location input) {
            return api.getInstanceApiForProject(userProject.get()).listInZone(input.getId()).concat()
                    .transform(new Function<Instance, InstanceInZone>() {

                       @Override
                       public InstanceInZone apply(Instance arg0) {
                          return new InstanceInZone(arg0, input.getId());
                       }
                    }).toSet();
         }
      }).toSet();
   }

   @Override
   public Iterable<InstanceInZone> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<InstanceInZone>() {

         @Override
         public boolean apply(InstanceInZone instanceInZone) {
            return contains(ids, instanceInZone.getInstance().getName());
         }
      });
   }

   @Override
   public void destroyNode(final String name) {
      SlashEncodedIds slashEncodedIds = SlashEncodedIds.fromSlashEncoded(name);
      String diskName = null;
      try {
         Instance instance = api.getInstanceApiForProject(userProject.get()).getInZone(slashEncodedIds.getFirstId(),
                                                                              slashEncodedIds.getSecondId());
         if (instance.getMetadata().getItems().get(GCE_DELETE_BOOT_DISK_METADATA_KEY).equals("true")) {
            Optional<AttachedDisk> disk = tryFind(instance.getDisks(), new Predicate<AttachedDisk>() {
               @Override
               public boolean apply(AttachedDisk input) {
                  return PersistentAttachedDisk.class.isInstance(input) &&
                         PersistentAttachedDisk.class.cast(input).isBoot();
               }
            });
            if (disk.isPresent()) {
               diskName = PersistentAttachedDisk.class.cast(disk.get()).getSourceDiskName();
            }
         }
      } catch (Exception e) {
         // TODO: what exception actually gets thrown here if the instance doesn't really exist?
      }
      waitOperationDone(api.getInstanceApiForProject(userProject.get()).deleteInZone(slashEncodedIds.getFirstId(),
              slashEncodedIds.getSecondId()));

      if (diskName != null) {
         waitOperationDone(api.getDiskApiForProject(userProject.get()).deleteInZone(slashEncodedIds.getFirstId(),
                                                                                    diskName));
      }

   }

   @Override
   public void rebootNode(final String name) {
      SlashEncodedIds slashEncodedIds = SlashEncodedIds.fromSlashEncoded(name);

      waitOperationDone(api.getInstanceApiForProject(userProject.get()).resetInZone(slashEncodedIds.getFirstId(),
              slashEncodedIds.getSecondId()));
   }

   @Override
   public void resumeNode(String name) {
      throw new UnsupportedOperationException("resume is not supported by GCE");
   }

   @Override
   public void suspendNode(String name) {
      throw new UnsupportedOperationException("suspend is not supported by GCE");
   }

   private LoginCredentials getFromImageAndOverrideIfRequired(org.jclouds.compute.domain.Image image,
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
      if (!retryOperationDonePredicate.apply(operationRef)) {
         throw new UncheckedTimeoutException("operation did not reach DONE state" + operationRef.get());
      }

      // check if the operation failed
      if (operationRef.get().getHttpError().isPresent()) {
         HttpResponse response = operationRef.get().getHttpError().get();
         throw new IllegalStateException("operation failed. Http Error Code: " + response.getStatusCode() +
                 " HttpError: " + response.getMessage());
      }
   }

}
