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
package org.jclouds.softlayer.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.tryFind;
import static java.lang.Math.round;
import static java.lang.String.format;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.compute.util.ComputeServiceUtils.getSpace;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY;
import static org.jclouds.util.Predicates2.retry;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.Volume.Type;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.domain.ContainerVirtualGuestConfiguration;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.NetworkVlan;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.Password;
import org.jclouds.softlayer.domain.SecuritySshKey;
import org.jclouds.softlayer.domain.SoftwareDescription;
import org.jclouds.softlayer.domain.SoftwareLicense;
import org.jclouds.softlayer.domain.VirtualDiskImage;
import org.jclouds.softlayer.domain.VirtualDiskImageSoftware;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestAttribute;
import org.jclouds.softlayer.domain.VirtualGuestBlockDevice;
import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplate;
import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup;
import org.jclouds.softlayer.domain.VirtualGuestNetworkComponent;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * defines the connection between the {@link SoftLayerApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 *
 */
@Singleton
public class SoftLayerComputeServiceAdapter implements
      ComputeServiceAdapter<VirtualGuest, Hardware, OperatingSystem, Datacenter> {

   private static final String USER_META_NOTES = "notes";
   private static final int USER_META_NOTES_MAX_LENGTH = 1000;
   private static final String BOOTABLE_DEVICE = "0";
   public static final String DEFAULT_DISK_TYPE = "LOCAL";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final SoftLayerApi api;
   private final Supplier<ContainerVirtualGuestConfiguration> createObjectOptionsSupplier;
   private final Predicate<VirtualGuest> loginDetailsTester;
   private final long guestLoginDelay;
   private final long activeTransactionsDelay;

   @Inject
   public SoftLayerComputeServiceAdapter(SoftLayerApi api,
         VirtualGuestHasLoginDetailsPresent virtualGuestHasLoginDetailsPresent,
         @Memoized Supplier<ContainerVirtualGuestConfiguration> createObjectOptionsSupplier,
         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY) long guestLoginDelay,
         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_DELAY) long activeTransactionsDelay) {
      this.api = checkNotNull(api, "api");
      this.guestLoginDelay = checkNotNull(guestLoginDelay, "guestLoginDelay");
      this.activeTransactionsDelay = checkNotNull(activeTransactionsDelay, "activeTransactionsDelay");
      this.createObjectOptionsSupplier = checkNotNull(createObjectOptionsSupplier, "createObjectOptionsSupplier");
      checkArgument(guestLoginDelay > 500, "guestOrderDelay must be in milliseconds and greater than 500");
      this.loginDetailsTester = retry(virtualGuestHasLoginDetailsPresent, guestLoginDelay);
   }

   @Override
   public NodeAndInitialCredentials<VirtualGuest> createNodeWithGroupEncodedIntoName(String group, String name,
         Template template) {
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");
      checkArgument(template.getOptions().getClass().isAssignableFrom(SoftLayerTemplateOptions.class),
              "options class %s should have been assignable from SoftLayerTemplateOptions",
              template.getOptions().getClass());

      SoftLayerTemplateOptions templateOptions = template.getOptions().as(SoftLayerTemplateOptions.class);
      String domainName = templateOptions.getDomainName();
      String diskType = templateOptions.getDiskType() == null ? DEFAULT_DISK_TYPE : templateOptions.getDiskType();
      boolean hourlyBillingFlag = templateOptions.isHourlyBillingFlag()  == null ? true : templateOptions.isHourlyBillingFlag();
      Integer portSpeed = templateOptions.getPortSpeed();
      Set<VirtualGuestNetworkComponent> networkComponents = portSpeed != null ?
              ImmutableSet.of(VirtualGuestNetworkComponent.builder().speed(portSpeed).build()) :
              ImmutableSet.<VirtualGuestNetworkComponent>of();
      final Datacenter datacenter = Datacenter.builder().name(template.getLocation().getId()).build();
      final String imageId = template.getImage().getId();
      int cores = (int) template.getHardware().getProcessors().get(0).getCores();
      String notes = getNotes(templateOptions);

      VirtualGuest.Builder<?> virtualGuestBuilder = VirtualGuest.builder()
              .domain(domainName)
              .hostname(name)
              .hourlyBillingFlag(hourlyBillingFlag)
              .startCpus(cores)
              .maxMemory(template.getHardware().getRam())
              .datacenter(datacenter)
              .localDiskFlag(isLocalDisk(diskType))
              .networkComponents(networkComponents);

      // set operating system or blockDeviceTemplateGroup
      Optional<OperatingSystem> optionalOperatingSystem = tryExtractOperatingSystemFrom(imageId);
      if (optionalOperatingSystem != null) {
         virtualGuestBuilder.operatingSystem(optionalOperatingSystem.get());
      // the imageId specified is an id of a public/private/flex image
      } else {
         VirtualGuestBlockDeviceTemplateGroup blockDeviceTemplateGroup = VirtualGuestBlockDeviceTemplateGroup
                 .builder().globalIdentifier(imageId).build();
         virtualGuestBuilder.blockDeviceTemplateGroup(blockDeviceTemplateGroup).build();
      }
      // set multi-disks
      if (!templateOptions.getBlockDevices().isEmpty()) {
         List<VirtualGuestBlockDevice> blockDevices = getBlockDevices(templateOptions.getBlockDevices(), diskType);
         virtualGuestBuilder.blockDevices(blockDevices);
      }
      // set dedicatedAccountHostOnlyFlag
      if (templateOptions.isDedicatedAccountHostOnlyFlag() != null) {
         virtualGuestBuilder.dedicatedAccountHostOnly(templateOptions.isDedicatedAccountHostOnlyFlag());
      }
      // set privateNetworkOnlyFlag
      if (templateOptions.isPrivateNetworkOnlyFlag() != null) {
         virtualGuestBuilder.privateNetworkOnlyFlag(templateOptions.isPrivateNetworkOnlyFlag());
      }
      // set primaryNetworkComponent.networkVlan.id
      if (templateOptions.getPrimaryNetworkComponentNetworkVlanId() != null) {
         int primaryNetworkComponentNetworkVlanId = templateOptions.getPrimaryNetworkComponentNetworkVlanId();
         virtualGuestBuilder.primaryNetworkComponent(
                 VirtualGuestNetworkComponent.builder()
                         .networkVlan(NetworkVlan.builder().id(primaryNetworkComponentNetworkVlanId).build())
                         .build());
      }
      // set primaryBackendNetworkComponent.networkVlan.id
      if (templateOptions.getPrimaryBackendNetworkComponentNetworkVlanId() != null) {
         int primaryBackendNetworkComponentNetworkVlanId = templateOptions.getPrimaryBackendNetworkComponentNetworkVlanId();
         virtualGuestBuilder.primaryBackendNetworkComponent(
                 VirtualGuestNetworkComponent.builder()
                         .networkVlan(NetworkVlan.builder().id(primaryBackendNetworkComponentNetworkVlanId).build())
                         .build());
      }
      // set postInstallScriptUri
      if (templateOptions.getPostInstallScriptUri() != null) {
         // Specifies the uri location of the script to be downloaded and run after installation is complete.
         virtualGuestBuilder.postInstallScriptUri(templateOptions.getPostInstallScriptUri());
      }
      // set userData
      if (templateOptions.getUserData() != null) {
         virtualGuestBuilder.virtualGuestAttribute(VirtualGuestAttribute.builder().value(templateOptions.getUserData()).build());
      }
      // set sshKeys
      if (!templateOptions.getSshKeys().isEmpty()) {
         Set<SecuritySshKey> sshKeys = Sets.newHashSet();
         for (int sshKeyId : templateOptions.getSshKeys()) {
            sshKeys.add(SecuritySshKey.builder().id(sshKeyId).build());
         }
         virtualGuestBuilder.sshKeys(sshKeys);
      }

      VirtualGuest virtualGuest = virtualGuestBuilder.build();
      logger.debug(">> creating new VirtualGuest(%s)", virtualGuest);
      VirtualGuest result = api.getVirtualGuestApi().createVirtualGuest(virtualGuest);
      logger.trace("<< VirtualGuest(%s)", result.getId());

      // tags
      if (!templateOptions.getTags().isEmpty()) {
         api.getVirtualGuestApi().setTags(result.getId(), templateOptions.getTags());
      }

      // notes
      if (!Strings.isNullOrEmpty(notes)) {
         api.getVirtualGuestApi().setNotes(result.getId(), notes);
      }

      logger.debug(">> awaiting login details for virtualGuest(%s)", result.getId());
      boolean orderInSystem = loginDetailsTester.apply(result);
      logger.trace("<< VirtualGuest(%s) complete(%s)", result.getId(), orderInSystem);

      if (!orderInSystem) {
         logger.warn("VirtualGuest(%s) doesn't have login details within %sms so it will be destroyed.", result,
              Long.toString(guestLoginDelay));
         api.getVirtualGuestApi().deleteVirtualGuest(result.getId());
         throw new IllegalStateException(format("VirtualGuest(%s) is being destroyed as it doesn't have login details" +
                 " after %sms. Please, try by increasing `jclouds.softlayer.virtualguest.login_details_delay` and " +
                 " try again", result, Long.toString(guestLoginDelay)));
      }
      result = api.getVirtualGuestApi().getVirtualGuest(result.getId());
      Password pwd = get(result.getOperatingSystem().getPasswords(), 0);
      return new NodeAndInitialCredentials<VirtualGuest>(result, result.getId() + "",
              LoginCredentials.builder().user(pwd.getUsername()).password(pwd.getPassword()).build());
   }

   private String getNotes(SoftLayerTemplateOptions templateOptions) {
      String notes = null;
      Map<String, String> meta = templateOptions.getUserMetadata();
      if (meta != null) {
         notes = meta.get(USER_META_NOTES);
         if (!Strings.isNullOrEmpty(notes)) {
            checkArgument(notes.length() <= USER_META_NOTES_MAX_LENGTH, "'notes' property in user metadata should be long at most " + USER_META_NOTES_MAX_LENGTH + " characters.");
         }
      }
      return notes;
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      ContainerVirtualGuestConfiguration virtualGuestConfiguration = createObjectOptionsSupplier.get();
      Builder<Hardware> hardware = ImmutableSortedSet.orderedBy(new Comparator<Hardware>() {
         @Override
         public int compare(Hardware h1, Hardware h2) {
            List<? extends Volume> volumes1 = h1.getVolumes();
            List<? extends Volume> volumes2 = h2.getVolumes();
            ComparisonChain comparisonChain = ComparisonChain.start().compare(getCores(h1), getCores(h2))
                    .compare(h1.getRam(), h2.getRam())
                    .compare(getSpace(h1), getSpace(h2))
                    .compare(getBootableDeviceType(h1), getBootableDeviceType(h2));
            if (!volumes1.isEmpty() && !volumes2.isEmpty() && volumes1.size() == volumes2.size()) {
               for (int i = 0; i < volumes1.size(); i++) {
                  comparisonChain = comparisonChain.compare(volumes1.get(i).getType(), volumes2.get(i).getType());
               }
            }
            return comparisonChain.result();
         }
      });
      for (VirtualGuestBlockDevice blockDevice : virtualGuestConfiguration.getVirtualGuestBlockDevices()) {
         float capacity = blockDevice.getVirtualDiskImage().getCapacity();
         Type type = blockDevice.getVirtualGuest().isLocalDiskFlag() ? Type.LOCAL : Type.SAN;
         if (blockDevice.getDevice().equals(BOOTABLE_DEVICE)) {
            for (Integer cpus : virtualGuestConfiguration.getCpusOfProcessors()) {
               for (Integer memory : virtualGuestConfiguration.getMemories()) {
                  String id = format("cpu=%s,memory=%s,disk=%s,type=%s", cpus, memory, round(capacity), type);
                  hardware.add(new HardwareBuilder()
                          .ids(id)
                          .ram(memory)
                          .processors(ImmutableList.of(new Processor(cpus, 2)))
                          .hypervisor("XenServer")
                          .volumes(ImmutableList.<Volume>of(
                                  new VolumeImpl(blockDevice.getId() + "",
                                          type,
                                          capacity,
                                          blockDevice.getDevice(),
                                          blockDevice.getBootableFlag() == 1,
                                          true)))
                          .build());
               }
            }
         }
      }
      return hardware.build();
   }

   @Override
   public Set<OperatingSystem> listImages() {
      Set<OperatingSystem> result = Sets.newHashSet();
      // add allObjects filtered by the available OS
      Set<SoftwareDescription> allObjects = api.getSoftwareDescriptionApi().getAllObjects();
      for (OperatingSystem os : createObjectOptionsSupplier.get().getVirtualGuestOperatingSystems()) {
         result.addAll(FluentIterable.from(allObjects)
                 .filter(new IsOperatingSystem())
                 .filter(new HasSameOsReferenceCode(os.getOperatingSystemReferenceCode()))
                 .transform(new SoftwareDescriptionToOperatingSystem(os.getId()))
                 .toSet());
      }
      return result;
   }

   @Override
   public OperatingSystem getImage(final String id) {
      // look for imageId among stock images
      Optional<OperatingSystem> operatingSystemOptional = tryFind(listImages(),
              new Predicate<OperatingSystem>() {
                 @Override
                 public boolean apply(OperatingSystem input) {
                    return input.getId().equals(id);
                 }
              }
      );
      if (operatingSystemOptional.isPresent()) return operatingSystemOptional.get();

      // if imageId is not a stock image, it searches among private and public images
      Stopwatch stopwatch = Stopwatch.createStarted();
      VirtualGuestBlockDeviceTemplateGroup image = api.getVirtualGuestBlockDeviceTemplateGroupApi().getObject(id);
      logger.trace("<< Image(%s) found in (%s)", id, stopwatch.elapsed(TimeUnit.SECONDS));
      return tryExtractOperatingSystemFrom(image).orNull();
   }

   @Override
   public Iterable<VirtualGuest> listNodes() {
      return api.getAccountApi().listVirtualGuests();
   }

   @Override
   public Iterable<VirtualGuest> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<VirtualGuest>() {

         @Override
         public boolean apply(VirtualGuest server) {
            return contains(ids, server.getId());
         }
      });
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      Set<Datacenter> result = Sets.newHashSet();
      Set<Datacenter> unfiltered = api.getDatacenterApi().listDatacenters();
      Set<Datacenter> datacenterAvailable = createObjectOptionsSupplier.get().getVirtualGuestDatacenters();
      for (Datacenter datacenter : datacenterAvailable) {
         final String datacenterName = datacenter.getName();
         result.addAll(Sets.newHashSet(filter(unfiltered,
                 new Predicate<Datacenter>() {
                    @Override
                    public boolean apply(Datacenter input) {
                       return input.getName().equals(datacenterName);
                    }
                 })));
      }
      return result;
   }

   @Override
   public VirtualGuest getNode(String id) {
      long serverId = Long.parseLong(id);
      return api.getVirtualGuestApi().getVirtualGuest(serverId);
   }

   @Override
   public void destroyNode(String id) {
      VirtualGuest guest = getNode(id);
      if (guest == null) return;
      logger.debug(">> awaiting virtualGuest(%s) without active transactions", guest.getId());
      checkState(retry(new Predicate<VirtualGuest>() {
         public boolean apply(VirtualGuest guest) {
               return getNode(guest.getId() + "").getActiveTransactionCount() == 0;
         }
      }, activeTransactionsDelay).apply(guest), "%s still has active transactions!", guest);
      logger.debug(">> canceling virtualGuest with globalIdentifier(%s)", id);
      checkState(api.getVirtualGuestApi().deleteVirtualGuest(guest.getId()), "server(%s) still there after deleting!?", id);
   }

   @Override
   public void rebootNode(String id) {
      api.getVirtualGuestApi().rebootHardVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void resumeNode(String id) {
      api.getVirtualGuestApi().resumeVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void suspendNode(String id) {
      api.getVirtualGuestApi().pauseVirtualGuest(Long.parseLong(id));
   }

   /**
    * This method will deliberately skip device position 1 as it is reserved to SWAP
    * @param blockDeviceCapacities list of blockDevices to be attached
    * @param diskType disks can be LOCAL or SAN
    * @return
    */
   private static List<VirtualGuestBlockDevice> getBlockDevices(List<Integer> blockDeviceCapacities, String diskType) {
      ImmutableList.Builder<VirtualGuestBlockDevice> blockDevicesBuilder = ImmutableList.builder();
      int devicePosition = 0;
      for (int i = 0; i < blockDeviceCapacities.size(); i++) {
         if (i > 0) { devicePosition = i + 1; }
         blockDevicesBuilder.add(VirtualGuestBlockDevice.builder()
                 .device(devicePosition + "")
                 .diskImage(VirtualDiskImage.builder()
                         .capacity(blockDeviceCapacities.get(i))
                         .typeId(Type.valueOf(diskType).ordinal())
                         .build())
                 .build());
      }
      return blockDevicesBuilder.build();
   }

   private static boolean isLocalDisk(String diskType) {
      return diskType.equalsIgnoreCase(Type.LOCAL.name());
   }

   private static int getBootableDeviceType(Hardware hardware) {
      List<? extends Volume> volumes = hardware.getVolumes();
      Optional<? extends Volume> optionalBootableVolume = tryFind(volumes, new Predicate<Volume>() {
         @Override
         public boolean apply(Volume volume) {
            return volume.getDevice().equals(BOOTABLE_DEVICE);
         }
      });
      if (!optionalBootableVolume.isPresent()) {
         return Type.LOCAL.ordinal();
      }
      return optionalBootableVolume.get().getType().ordinal();
   }

   private Optional<OperatingSystem> tryExtractOperatingSystemFrom(final String imageId) {
      Set<OperatingSystem> operatingSystemsAvailable = createObjectOptionsSupplier.get().getVirtualGuestOperatingSystems();
      return FluentIterable.from(operatingSystemsAvailable)
              .filter(new Predicate<OperatingSystem>() {
                 @Override
                 public boolean apply(OperatingSystem input) {
                    if (input == null) return false;
                    return input.getId().contains(imageId);
                 }
              })
              .first();
   }

   private Optional<OperatingSystem> tryExtractOperatingSystemFrom(VirtualGuestBlockDeviceTemplateGroup image) {
      if (image.getGlobalIdentifier() == null) return Optional.absent();
      return FluentIterable.from(image.getChildren())
              .transformAndConcat(new BlockDeviceTemplateGroupToBlockDeviceTemplateIterable())
              .filter(new IsBootableDevice())
              .transformAndConcat(new BlockDeviceTemplateToDiskImageSoftware())
              .transform(new DiskImageSoftwareToSoftwareDescription())
              .filter(new IsOperatingSystem())
              .transform(new SoftwareDescriptionToOperatingSystem(image.getGlobalIdentifier()))
              .first();
   }

   public static class VirtualGuestHasLoginDetailsPresent implements Predicate<VirtualGuest> {
      private final SoftLayerApi client;

      @Inject
      public VirtualGuestHasLoginDetailsPresent(SoftLayerApi client) {
         this.client = checkNotNull(client, "client was null");
      }

      @Override
      public boolean apply(VirtualGuest guest) {
         checkNotNull(guest, "virtual guest was null");

         VirtualGuest virtualGuest = client.getVirtualGuestApi().getVirtualGuest(guest.getId());
         boolean hasBackendIp = virtualGuest.getPrimaryBackendIpAddress() != null;
         boolean hasPrimaryIp = virtualGuest.getPrimaryIpAddress() != null;
         boolean hasPasswords = virtualGuest.getOperatingSystem() != null
                 && !virtualGuest.getOperatingSystem().getPasswords().isEmpty();

         return virtualGuest.isPrivateNetworkOnly() ? hasBackendIp && hasPasswords : hasBackendIp && hasPrimaryIp && hasPasswords;
      }
   }

   private static class SoftwareDescriptionToOperatingSystem implements Function<SoftwareDescription, OperatingSystem> {
      private final String osId;

      public SoftwareDescriptionToOperatingSystem(String osId) {
         this.osId = osId;
      }

      @Override
      public OperatingSystem apply(SoftwareDescription input) {
         return OperatingSystem.builder().id(osId)
                                         .softwareLicense(SoftwareLicense.builder().softwareDescription(input).build())
                                         .operatingSystemReferenceCode(input.getReferenceCode())
                                         .build();
      }
   }

   private static class IsBootableDevice implements Predicate<VirtualGuestBlockDeviceTemplate> {
      @Override
      public boolean apply(VirtualGuestBlockDeviceTemplate blockDeviceTemplate) {
         return blockDeviceTemplate.getDevice().equals(BOOTABLE_DEVICE);
      }
   }

   private static class BlockDeviceTemplateGroupToBlockDeviceTemplateIterable implements Function<VirtualGuestBlockDeviceTemplateGroup,
                         Iterable<VirtualGuestBlockDeviceTemplate>> {
      @Override
      public Iterable<VirtualGuestBlockDeviceTemplate> apply(VirtualGuestBlockDeviceTemplateGroup input) {
         return input.getBlockDevices();
      }
   }

   private static class BlockDeviceTemplateToDiskImageSoftware implements Function<VirtualGuestBlockDeviceTemplate, Iterable<VirtualDiskImageSoftware>> {
      @Override
      public Iterable<VirtualDiskImageSoftware> apply(VirtualGuestBlockDeviceTemplate bootableDevice) {
         return bootableDevice.getDiskImage().getSoftwareReferences();
      }
   }

   private static class DiskImageSoftwareToSoftwareDescription implements Function<VirtualDiskImageSoftware, SoftwareDescription> {
      @Override
      public SoftwareDescription apply(VirtualDiskImageSoftware software) {
         return software.getSoftwareDescription();
      }
   }

   private static class HasSameOsReferenceCode implements Predicate<SoftwareDescription> {
      private final String osReferenceCode;

      public HasSameOsReferenceCode(String osReferenceCode) {
         this.osReferenceCode = osReferenceCode;
      }

      @Override
      public boolean apply(SoftwareDescription input) {
         return input.getReferenceCode().equals(osReferenceCode);
      }
   }

   private class IsOperatingSystem implements Predicate<SoftwareDescription> {
      @Override
      public boolean apply(SoftwareDescription softwareDescription) {
         // operatingSystem is set to '1' if this Software Description describes an Operating System.
         return softwareDescription.getOperatingSystem() == 1;
      }
   }
}
