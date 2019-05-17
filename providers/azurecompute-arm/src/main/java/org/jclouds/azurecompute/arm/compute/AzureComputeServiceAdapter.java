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
package org.jclouds.azurecompute.arm.compute;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.builder;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.jclouds.azurecompute.arm.compute.domain.LocationAndName.fromSlashEncoded;
import static org.jclouds.azurecompute.arm.compute.domain.ResourceGroupAndName.fromResourceGroupAndName;
import static org.jclouds.azurecompute.arm.compute.functions.VMImageToImage.getMarketplacePlanFromImageMetadata;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.IMAGE_PUBLISHERS;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractName;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractResourceGroup;
import static org.jclouds.azurecompute.arm.util.VMImages.isCustom;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsCommaDelimitedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.PublicIpAvailablePredicateFactory;
import org.jclouds.azurecompute.arm.compute.domain.ResourceGroupAndName;
import org.jclouds.azurecompute.arm.compute.functions.CustomImageToVMImage;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.compute.options.IpOptions;
import org.jclouds.azurecompute.arm.compute.strategy.CleanupResources;
import org.jclouds.azurecompute.arm.domain.AvailabilitySet;
import org.jclouds.azurecompute.arm.domain.CreationData;
import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.ManagedDiskParameters;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.NetworkProfile.NetworkInterface;
import org.jclouds.azurecompute.arm.domain.NetworkProfile.NetworkInterface.NetworkInterfaceProperties;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.Offer;
import org.jclouds.azurecompute.arm.domain.Plan;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.ResourceProviderMetaData;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.StorageAccountType;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.VMHardware;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.domain.Version;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.features.NetworkInterfaceCardApi;
import org.jclouds.azurecompute.arm.features.OSImageApi;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Defines the connection between the {@link AzureComputeApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}.
 */
@Singleton
public class AzureComputeServiceAdapter implements ComputeServiceAdapter<VirtualMachine, VMHardware, VMImage, Location> {

   public static final String GROUP_KEY = "jclouds_group";
   public static final String AUTOGENERATED_IP_KEY = "jclouds-autogenerated";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CleanupResources cleanupResources;
   private final AzureComputeApi api;
   private final List<String> imagePublishers;
   private final Supplier<Set<String>> regionIds;
   private final PublicIpAvailablePredicateFactory publicIpAvailable;
   private final CustomImageToVMImage customImagetoVmImage;
   private final GroupNamingConvention namingConvention;
   private Predicate<Supplier<Provisionable>> resourceAvailable;

   @Inject
   AzureComputeServiceAdapter(final AzureComputeApi api, @Named(IMAGE_PUBLISHERS) String imagePublishers,
         CleanupResources cleanupResources, @Region Supplier<Set<String>> regionIds,
         PublicIpAvailablePredicateFactory publicIpAvailable, CustomImageToVMImage customImagetoVmImage,
         GroupNamingConvention.Factory namingConvention, Predicate<Supplier<Provisionable>> resourceAvailable) {
      this.api = api;
      this.imagePublishers = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(imagePublishers);
      this.cleanupResources = cleanupResources;
      this.regionIds = regionIds;
      this.publicIpAvailable = publicIpAvailable;
      this.customImagetoVmImage = customImagetoVmImage;
      this.namingConvention = namingConvention.create();
      this.resourceAvailable = resourceAvailable;
   }

   @Override
   public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(final String group, final String name, final Template template) {
      String locationName = template.getLocation().getId();
      Image image = template.getImage();
      String hardwareId = fromSlashEncoded(template.getHardware().getId()).name();
      AzureTemplateOptions templateOptions = template.getOptions().as(AzureTemplateOptions.class);
      String resourceGroupName = templateOptions.getResourceGroup();
      
      IdReference availabilitySet = getAvailabilitySetIdReference(templateOptions.getAvailabilitySet());
      NetworkProfile networkProfile = createNetworkProfile(createNetworkInterfaceCards(name, locationName,
            templateOptions));
      StorageProfile storageProfile = createStorageProfile(image, templateOptions.getDataDisks());
      HardwareProfile hardwareProfile = HardwareProfile.builder().vmSize(hardwareId).build();
      OSProfile osProfile = createOsProfile(name, template);
      
      VirtualMachineProperties virtualMachineProperties = VirtualMachineProperties.builder()
              .availabilitySet(availabilitySet)
              .hardwareProfile(hardwareProfile)
              .storageProfile(storageProfile)
              .osProfile(osProfile)
              .networkProfile(networkProfile)
              .build();

      // Store group apart from the name to be able to identify nodes with
      // custom names in the configured group
      templateOptions.getUserMetadata().put(GROUP_KEY, group);
      Map<String, String> metadataAndTags = metadataAndTagsAsCommaDelimitedValue(templateOptions);
      Plan plan = getMarketplacePlanFromImageMetadata(image);

      VirtualMachine virtualMachine = api.getVirtualMachineApi(resourceGroupName).createOrUpdate(name, locationName,
            virtualMachineProperties, metadataAndTags, plan);

      // Safe to pass null credentials here, as jclouds will default populate
      // the node with the default credentials from the image, or the ones in
      // the options, if provided.
      ResourceGroupAndName resourceGroupAndName = fromResourceGroupAndName(resourceGroupName, name);
      return new NodeAndInitialCredentials<VirtualMachine>(virtualMachine, resourceGroupAndName.slashEncode(), null);
   }

   @Override
   public Iterable<VMHardware> listHardwareProfiles() {
      final List<VMHardware> hwProfiles = Lists.newArrayList();
      for (Location location : listLocations()) {
         Iterable<VMSize> vmSizes = api.getVMSizeApi(location.name()).list();
         for (VMSize vmSize : vmSizes) {
            VMHardware hwProfile = VMHardware
                    .create(vmSize.name(), vmSize.numberOfCores(), vmSize.osDiskSizeInMB(),
                            vmSize.resourceDiskSizeInMB(), vmSize.memoryInMB(), vmSize.maxDataDiskCount(), location.name());
            hwProfiles.add(hwProfile);
         }
      }
      return hwProfiles;
   }

   private List<VMImage> getImagesFromPublisher(String publisherName, String location) {
      List<VMImage> osImagesRef = Lists.newArrayList();
      OSImageApi osImageApi = api.getOSImageApi(location);
      Iterable<Offer> offerList = osImageApi.listOffers(publisherName);

      for (Offer offer : offerList) {
         Iterable<SKU> skuList = osImageApi.listSKUs(publisherName, offer.name());

         for (SKU sku : skuList) {
            Iterable<Version> versionList = osImageApi.listVersions(publisherName, offer.name(), sku.name());
            for (Version version : versionList) {
               Version versionDetails = osImageApi.getVersion(publisherName, offer.name(), sku.name(), version.name());
               VMImage vmImage = VMImage.azureImage().publisher(publisherName).offer(offer.name()).sku(sku.name())
                       .version(versionDetails.name()).location(location).versionProperties(versionDetails.properties())
                       .build();
               osImagesRef.add(vmImage);
            }
         }
      }
      return osImagesRef;
   }

   private List<VMImage> listImagesByLocation(String location) {
      final List<VMImage> osImages = Lists.newArrayList();
      for (String publisher : imagePublishers) {
         osImages.addAll(getImagesFromPublisher(publisher, location));
      }
      return osImages;
   }
   
   private List<VMImage> listCustomImagesByResourceGroup(String resourceGroup) {
      List<org.jclouds.azurecompute.arm.domain.Image> customImgs = api.getVirtualMachineImageApi(resourceGroup).list();
      return ImmutableList.copyOf(transform(
            filter(customImgs, new Predicate<org.jclouds.azurecompute.arm.domain.Image>() {
               @Override
               public boolean apply(org.jclouds.azurecompute.arm.domain.Image input) {
                  return regionIds.get().isEmpty() || regionIds.get().contains(input.location());
               }
            }), customImagetoVmImage));
   }

   @Override
   public Iterable<VMImage> listImages() {
      final ImmutableList.Builder<VMImage> osImages = ImmutableList.builder();
      
      final List<String> availableLocationNames = newArrayList(transform(listLocations(),
            new Function<Location, String>() {
               @Override
               public String apply(Location location) {
                  return location.name();
               }
            }));

      for (String locationName : availableLocationNames) {
         osImages.addAll(listImagesByLocation(locationName));
      }

      // We need to look for custom images in all resource groups
      for (ResourceGroup resourceGroup : api.getResourceGroupApi().list()) {
         osImages.addAll(listCustomImagesByResourceGroup(resourceGroup.name()));
      }

      return osImages.build();
   }

   @Override
   public VMImage getImage(final String id) {
      VMImage image = VMImage.decodeFieldsFromUniqueId(id);

      if (image.custom()) {
         org.jclouds.azurecompute.arm.domain.Image vmImage = api.getVirtualMachineImageApi(image.resourceGroup()).get(
               image.name());
         return vmImage == null ? null : customImagetoVmImage.apply(vmImage);
      }

      String location = image.location();
      String publisher = image.publisher();
      String offer = image.offer();
      String sku = image.sku();

      OSImageApi osImageApi = api.getOSImageApi(location);
      List<Version> versions = osImageApi.listVersions(publisher, offer, sku);
      if (!versions.isEmpty()) {
         Version version = osImageApi.getVersion(publisher, offer, sku, versions.get(0).name());
         return VMImage.azureImage().publisher(publisher).offer(offer).sku(sku).version(version.name())
                 .location(location).versionProperties(version.properties()).build();
      }
      
      return null;
   }

   @Override
   public Iterable<Location> listLocations() {
      final Iterable<String> vmLocations = FluentIterable.from(api.getResourceProviderApi().get("Microsoft.Compute"))
              .filter(new Predicate<ResourceProviderMetaData>() {
                 @Override
                 public boolean apply(ResourceProviderMetaData input) {
                    return input.resourceType().equals("virtualMachines");
                 }
              }).transformAndConcat(new Function<ResourceProviderMetaData, Iterable<String>>() {
                 @Override
                 public Iterable<String> apply(ResourceProviderMetaData resourceProviderMetaData) {
                    return resourceProviderMetaData.locations();
                 }
              });

      List<Location> locations = FluentIterable.from(api.getLocationApi().list()).filter(new Predicate<Location>() {
         @Override
         public boolean apply(Location location) {
            return Iterables.contains(vmLocations, location.displayName());
         }
      }).filter(new Predicate<Location>() {
         @Override
         public boolean apply(Location location) {
            return regionIds.get().isEmpty() ? true : regionIds.get().contains(location.name());
         }
      }).toList();

      return locations;
   }

   @Override
   public VirtualMachine getNode(final String id) {
      ResourceGroupAndName resourceGroupAndName = ResourceGroupAndName.fromSlashEncoded(id);
      return api.getVirtualMachineApi(resourceGroupAndName.resourceGroup()).get(resourceGroupAndName.name());
   }

   @Override
   public void destroyNode(final String id) {
      checkState(cleanupResources.cleanupNode(id), "server(%s) and its resources still there after deleting!?", id);
   }

   @Override
   public void rebootNode(final String id) {
      ResourceGroupAndName resourceGroupAndName = ResourceGroupAndName.fromSlashEncoded(id);
      api.getVirtualMachineApi(resourceGroupAndName.resourceGroup()).restart(resourceGroupAndName.name());
   }

   @Override
   public void resumeNode(final String id) {
      ResourceGroupAndName resourceGroupAndName = ResourceGroupAndName.fromSlashEncoded(id);
      api.getVirtualMachineApi(resourceGroupAndName.resourceGroup()).start(resourceGroupAndName.name());
   }

   @Override
   public void suspendNode(final String id) {
      ResourceGroupAndName resourceGroupAndName = ResourceGroupAndName.fromSlashEncoded(id);
      api.getVirtualMachineApi(resourceGroupAndName.resourceGroup()).stop(resourceGroupAndName.name());
   }

   @Override
   public Iterable<VirtualMachine> listNodes() {
      ImmutableList.Builder<VirtualMachine> nodes = builder();
      if (regionIds.get().isEmpty()) {
         nodes.addAll(api.getVirtualMachineApi(null).listAll());
      } else {
         for (final String location : regionIds.get()) {
            nodes.addAll(api.getVirtualMachineApi(null).listByLocation(location));
         }
      }
      return nodes.build();
   }

   @Override
   public Iterable<VirtualMachine> listNodesByIds(final Iterable<String> ids) {
      return transform(ids, new Function<String, VirtualMachine>() {
         @Override
         public VirtualMachine apply(String input) {
            return getNode(input);
         }
      });
   }

   private OSProfile createOsProfile(String computerName, Template template) {
      String defaultLoginUser = template.getImage().getDefaultCredentials().getUser();
      String adminUsername = MoreObjects.firstNonNull(template.getOptions().getLoginUser(), defaultLoginUser);
      // Password already generated in CreateResourcesThenCreateNodes (if not set by user)
      String adminPassword = template.getOptions().getLoginPassword();
      OSProfile.Builder builder = OSProfile.builder().adminUsername(adminUsername).adminPassword(adminPassword)
              .computerName(computerName);

      if (!Strings.isNullOrEmpty(template.getOptions().getPublicKey())
              && OsFamily.WINDOWS != template.getImage().getOperatingSystem().getFamily()) {
         OSProfile.LinuxConfiguration linuxConfiguration = OSProfile.LinuxConfiguration.create("true",
                 OSProfile.LinuxConfiguration.SSH.create(of(
                         OSProfile.LinuxConfiguration.SSH.SSHPublicKey.create(
                                 String.format("/home/%s/.ssh/authorized_keys", adminUsername),
                                 template.getOptions().getPublicKey()))));
         builder.linuxConfiguration(linuxConfiguration);
      }

      AzureTemplateOptions azureTemplateOptions = template.getOptions().as(AzureTemplateOptions.class);

      if (azureTemplateOptions.getWindowsConfiguration() != null) {
          builder.windowsConfiguration(azureTemplateOptions.getWindowsConfiguration());
      }

      if (azureTemplateOptions.getSecrets() != null) {
          builder.secrets(azureTemplateOptions.getSecrets());
      }

      if (!Strings.isNullOrEmpty(azureTemplateOptions.getCustomData())) {
         builder.customData(azureTemplateOptions.getCustomData());
      }

      return builder.build();
   }

   private List<NetworkInterfaceCard> createNetworkInterfaceCards(final String nodeName, final String location,
         AzureTemplateOptions options) {
      // Prefer a sorted list of NICs with the ones with public IPs first, to
      // make sure the primary NIC is the public one
      final String securityGroup = getOnlyElement(options.getGroups(), null);
      return Lists.transform(publicIpsFirst(options.getIpOptions()), new Function<IpOptions, NetworkInterfaceCard>() {
         @Override
         public NetworkInterfaceCard apply(IpOptions input) {
            return createNetworkInterfaceCard(input, nodeName, location, securityGroup);
         }
      });
   }
   
   private NetworkInterfaceCard createNetworkInterfaceCard(IpOptions ipConfig, String nodeName, String location,
         String securityGroup) {
      String resourceGroup = extractResourceGroup(ipConfig.subnet());
      String subnetName = extractName(ipConfig.subnet());

      IpConfigurationProperties.Builder ipProperties = IpConfigurationProperties.builder()
            .subnet(IdReference.create(ipConfig.subnet()))
            .privateIPAllocationMethod(ipConfig.address().isPresent() ? "Static" : "Dynamic")
            .privateIPAddress(ipConfig.address().orNull());

      configurePublicIP(ipConfig, ipProperties, resourceGroup, location, nodeName);

      String ipName = namingConvention.uniqueNameForGroup(subnetName);
      final String nicName = namingConvention.uniqueNameForGroup(subnetName);

      IpConfiguration config = IpConfiguration.builder().name(ipName).properties(ipProperties.build()).build();

      NetworkInterfaceCardProperties.Builder nicProperties = NetworkInterfaceCardProperties.builder().ipConfigurations(
            ImmutableList.of(config));

      if (securityGroup != null) {
         nicProperties.networkSecurityGroup(IdReference.create(securityGroup));
      }

      logger.debug(">> creating nic %s(%s) with security groups (%s)", nicName, config,
            securityGroup != null ? securityGroup : "");

      final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourceGroup);
      NetworkInterfaceCard nic = nicApi.createOrUpdate(nicName, location, nicProperties.build(),
            ImmutableMap.of("jclouds", nodeName));

      resourceAvailable.apply(new Supplier<Provisionable>() {
         @Override
         public Provisionable get() {
            NetworkInterfaceCard updated = nicApi.get(nicName);
            return updated == null ? null : updated.properties();
         }
      });

      return nic;
   }
   
   private void configurePublicIP(IpOptions ipConfig, IpConfigurationProperties.Builder ipProperties,
         String resourceGroup, String location, String nodeName) {
      if (ipConfig.publicIpId() != null) {
         logger.debug(">> configuring public ip: %s",  extractName(ipConfig.publicIpId()));
         PublicIPAddress publicIp = api.getPublicIPAddressApi(extractResourceGroup(ipConfig.publicIpId())).get(
               extractName(ipConfig.publicIpId()));
         ipProperties.publicIPAddress(IdReference.create(publicIp.id()));
      } else if (ipConfig.allocateNewPublicIp()) {
         PublicIPAddress publicIp = createPublicIp(resourceGroup, location, nodeName);
         ipProperties.publicIPAddress(IdReference.create(publicIp.id()));
      }
   }
   
   /**
    * Create the network profile and configure the first NIC as primary.
    */
   private NetworkProfile createNetworkProfile(List<NetworkInterfaceCard> nics) {
      List<NetworkInterface> nicAttachments = new ArrayList<NetworkInterface>(nics.size());
      for (int i = 0; i < nics.size(); i++) {
         nicAttachments.add(NetworkInterface.create(nics.get(i).id(), NetworkInterfaceProperties.create(i == 0)));
      }
      return NetworkProfile.create(nicAttachments);
   }
   
   private static List<IpOptions> publicIpsFirst(List<IpOptions> ipOptions) {
      List<IpOptions> sorted = new ArrayList<IpOptions>(ipOptions);
      Collections.sort(sorted, new Comparator<IpOptions>() {
         @Override
         public int compare(IpOptions o1, IpOptions o2) {
            return o1.allocateNewPublicIp() == o2.allocateNewPublicIp() ? 0 : o1.allocateNewPublicIp() ? -1 : 1;
         }
      });
      return sorted;
   }
   
   private PublicIPAddress createPublicIp(String resourceGroup, String location, String nodeName) {
      String name = namingConvention.uniqueNameForGroup(nodeName);
      
      PublicIPAddressProperties properties = PublicIPAddressProperties.builder()
            .publicIPAllocationMethod("Static")
            .idleTimeoutInMinutes(4)
            .build();
      
      logger.debug(">> allocating new public ip address: %s", name);

      PublicIPAddress ip = api.getPublicIPAddressApi(resourceGroup).createOrUpdate(name, location,
            ImmutableMap.of("jclouds", nodeName, AUTOGENERATED_IP_KEY, "true"), null, properties);

      checkState(publicIpAvailable.create(resourceGroup).apply(name),
              "Public IP was not provisioned in the configured timeout");
      
      return ip;
   }

   private StorageProfile createStorageProfile(Image image, List<DataDisk> dataDisks) {
      return StorageProfile.create(createImageReference(image), createOSDisk(image), dataDisks);
   }

   private ImageReference createImageReference(Image image) {
      return isCustom(image.getId()) ? ImageReference.builder().customImageId(image.getProviderId()).build() : ImageReference
            .builder().publisher(image.getProviderId()).offer(image.getName()).sku(image.getVersion())
            .version("latest").build();
   }

   private OSDisk createOSDisk(Image image) {
      OsFamily osFamily = image.getOperatingSystem().getFamily();
      String osType = osFamily == OsFamily.WINDOWS ? "Windows" : "Linux";
      return OSDisk.builder()
              .osType(osType)
              .caching(DataDisk.CachingTypes.READ_WRITE.toString())
              .createOption(CreationData.CreateOptions.FROM_IMAGE.toString())
              .managedDiskParameters(ManagedDiskParameters.create(null, StorageAccountType.STANDARD_LRS.toString()))
              .build();
   }
   
   private IdReference getAvailabilitySetIdReference(AvailabilitySet availabilitySet) {
      return availabilitySet != null ? IdReference.create(availabilitySet.id()) : null;
   }
}
