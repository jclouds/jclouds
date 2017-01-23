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
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension.CONTAINER_NAME;
import static org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension.CUSTOM_IMAGE_OFFER;
import static org.jclouds.azurecompute.arm.compute.functions.VMImageToImage.decodeFieldsFromUniqueId;
import static org.jclouds.azurecompute.arm.compute.functions.VMImageToImage.encodeFieldsToUniqueIdCustom;
import static org.jclouds.azurecompute.arm.compute.functions.VMImageToImage.getMarketplacePlanFromImageMetadata;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.IMAGE_PUBLISHERS;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsCommaDelimitedValue;
import static org.jclouds.util.Closeables2.closeQuietly;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.PublicIpAvailablePredicateFactory;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.compute.strategy.CleanupResources;
import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.Offer;
import org.jclouds.azurecompute.arm.domain.Plan;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.domain.RegionAndId;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.ResourceProviderMetaData;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.StorageService.Status;
import org.jclouds.azurecompute.arm.domain.StorageServiceKeys;
import org.jclouds.azurecompute.arm.domain.VHD;
import org.jclouds.azurecompute.arm.domain.VMHardware;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.domain.Version;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.features.OSImageApi;
import org.jclouds.azurecompute.arm.features.PublicIPAddressApi;
import org.jclouds.azurecompute.arm.util.BlobHelper;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
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
   
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final CleanupResources cleanupResources;
   private final AzureComputeApi api;
   private final List<String> imagePublishers;
   private final Supplier<Set<String>> regionIds;
   private final PublicIpAvailablePredicateFactory publicIpAvailable;
   private final LoadingCache<String, ResourceGroup> resourceGroupMap;

   @Inject
   AzureComputeServiceAdapter(final AzureComputeApi api, @Named(IMAGE_PUBLISHERS) String imagePublishers,
         CleanupResources cleanupResources, @Region Supplier<Set<String>> regionIds,
         PublicIpAvailablePredicateFactory publicIpAvailable, LoadingCache<String, ResourceGroup> resourceGroupMap) {
      this.api = api;
      this.imagePublishers = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(imagePublishers);
      this.cleanupResources = cleanupResources;
      this.regionIds = regionIds;
      this.publicIpAvailable = publicIpAvailable;
      this.resourceGroupMap = resourceGroupMap;
   }

   @Override
   public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(final String group,
         final String name, final Template template) {

      AzureTemplateOptions templateOptions = template.getOptions().as(AzureTemplateOptions.class);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(template.getLocation().getId());

      // TODO ARM specific options
      // TODO network ids => create one nic in each network
      
      IdReference availabilitySet = null;
      if (templateOptions.getAvailabilitySet() != null) {
         availabilitySet = IdReference.create(templateOptions.getAvailabilitySet().id());
      }

      String locationName = template.getLocation().getId();
      String subnetId = templateOptions.getSubnetId();
      NetworkInterfaceCard nic = createNetworkInterfaceCard(subnetId, name, locationName, resourceGroup.name(),
            template.getOptions());
      StorageProfile storageProfile = createStorageProfile(name, template.getImage(), templateOptions.getBlob());
      HardwareProfile hardwareProfile = HardwareProfile.builder().vmSize(template.getHardware().getId()).build();
      OSProfile osProfile = createOsProfile(name, template);
      NetworkProfile networkProfile = NetworkProfile.builder()
            .networkInterfaces(ImmutableList.of(IdReference.create(nic.id()))).build();
      VirtualMachineProperties virtualMachineProperties = VirtualMachineProperties.builder()
            .licenseType(null) // TODO
            .availabilitySet(availabilitySet)
            .hardwareProfile(hardwareProfile).storageProfile(storageProfile).osProfile(osProfile)
            .networkProfile(networkProfile).build();
      
      // Store group apart from the name to be able to identify nodes with
      // custom names in the configured group
      template.getOptions().getUserMetadata().put(GROUP_KEY, group);
      Map<String, String> metadataAndTags = metadataAndTagsAsCommaDelimitedValue(template.getOptions());
      Plan plan = getMarketplacePlanFromImageMetadata(template.getImage());

      VirtualMachine virtualMachine = api.getVirtualMachineApi(resourceGroup.name()).create(name, template.getLocation().getId(),
            virtualMachineProperties, metadataAndTags, plan);

      // Safe to pass null credentials here, as jclouds will default populate
      // the node with the default credentials from the image, or the ones in
      // the options, if provided.
      RegionAndId regionAndId = RegionAndId.fromRegionAndId(template.getLocation().getId(), name);
      return new NodeAndInitialCredentials<VirtualMachine>(virtualMachine, regionAndId.slashEncode(), null);
   }

   @Override
   public Iterable<VMHardware> listHardwareProfiles() {
      final List<VMHardware> hwProfiles = Lists.newArrayList();
      for (Location location : listLocations()) {
         Iterable<VMSize> vmSizes = api.getVMSizeApi(location.name()).list();
         for (VMSize vmSize : vmSizes) {
            VMHardware hwProfile = VMHardware
                  .create(vmSize.name(), vmSize.numberOfCores(), vmSize.osDiskSizeInMB(),
                        vmSize.resourceDiskSizeInMB(), vmSize.memoryInMB(), vmSize.maxDataDiskCount(), location.name(),
                        false);
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

   @Override
   public Iterable<VMImage> listImages() {
      final List<VMImage> osImages = Lists.newArrayList();

      for (Location location : listLocations()) {
         osImages.addAll(listImagesByLocation(location.name()));
      }

      // list custom images
      for (ResourceGroup resourceGroup : api.getResourceGroupApi().list()) {
         String azureGroup = resourceGroup.name();
         List<StorageService> storages = api.getStorageAccountApi(azureGroup).list();

         for (StorageService storage : storages) {
            try {
               String name = storage.name();
               StorageService storageService = api.getStorageAccountApi(azureGroup).get(name);
               if (storageService != null
                     && Status.Succeeded == storageService.storageServiceProperties().provisioningState()) {
                  String key = api.getStorageAccountApi(azureGroup).getKeys(name).key1();
                  BlobHelper blobHelper = new BlobHelper(storage.name(), key);
                  try {
                     List<VMImage> images = blobHelper.getImages(CONTAINER_NAME, azureGroup, CUSTOM_IMAGE_OFFER,
                           storage.location());
                     osImages.addAll(images);
                  } finally {
                     closeQuietly(blobHelper);
                  }
               }
            } catch (Exception ex) {
               logger.warn("<< could not get custom images from storage account %s: %s", storage, ex.getMessage());
            }
         }
      }

      return osImages;
   }

   @Override
   public VMImage getImage(final String id) {
      VMImage image = decodeFieldsFromUniqueId(id);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(image.location());

      if (image.custom()) {
         VMImage customImage = null;
         StorageServiceKeys keys = api.getStorageAccountApi(resourceGroup.name()).getKeys(image.storage());
         if (keys == null) {
            // If the storage account for the image does not exist, it means the
            // image was deleted
            return null;
         }

         BlobHelper blobHelper = new BlobHelper(image.storage(), keys.key1());
         try {
            if (blobHelper.customImageExists()) {
               List<VMImage> customImagesInStorage = blobHelper.getImages(CONTAINER_NAME, resourceGroup.name(),
                     CUSTOM_IMAGE_OFFER, image.location());
               customImage = find(customImagesInStorage, new Predicate<VMImage>() {
                  @Override
                  public boolean apply(VMImage input) {
                     return id.equals(encodeFieldsToUniqueIdCustom(input));
                  }
               }, null);
            }
         } finally {
            closeQuietly(blobHelper);
         }
         return customImage;
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
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());
      return api.getVirtualMachineApi(resourceGroup.name()).get(regionAndId.id());
   }

   @Override
   public void destroyNode(final String id) {
      checkState(cleanupResources.cleanupNode(id), "server(%s) and its resources still there after deleting!?", id);
   }

   @Override
   public void rebootNode(final String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());
      api.getVirtualMachineApi(resourceGroup.name()).restart(regionAndId.id());
   }

   @Override
   public void resumeNode(final String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());
      api.getVirtualMachineApi(resourceGroup.name()).start(regionAndId.id());
   }

   @Override
   public void suspendNode(final String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());
      api.getVirtualMachineApi(resourceGroup.name()).stop(regionAndId.id());
   }

   @Override
   public Iterable<VirtualMachine> listNodes() {
      ImmutableList.Builder<VirtualMachine> nodes = ImmutableList.builder();
      for (ResourceGroup resourceGroup : api.getResourceGroupApi().list()) {
         nodes.addAll(api.getVirtualMachineApi(resourceGroup.name()).list());
      }
      return nodes.build();
   }

   @Override
   public Iterable<VirtualMachine> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<VirtualMachine>() {
         @Override
         public boolean apply(VirtualMachine virtualMachine) {
            return contains(ids, virtualMachine.id());
         }
      });
   }

   private OSProfile createOsProfile(String computerName, Template template) {
      String defaultLoginUser = template.getImage().getDefaultCredentials().getUser();
      String defaultLoginPassword = template.getImage().getDefaultCredentials().getOptionalPassword().get();
      String adminUsername = Objects.firstNonNull(template.getOptions().getLoginUser(), defaultLoginUser);
      String adminPassword = Objects.firstNonNull(template.getOptions().getLoginPassword(), defaultLoginPassword);
      OSProfile.Builder builder = OSProfile.builder().adminUsername(adminUsername).adminPassword(adminPassword)
            .computerName(computerName);

      if (template.getOptions().getPublicKey() != null
            && OsFamily.WINDOWS != template.getImage().getOperatingSystem().getFamily()) {
         OSProfile.LinuxConfiguration linuxConfiguration = OSProfile.LinuxConfiguration.create("true",
               OSProfile.LinuxConfiguration.SSH.create(ImmutableList.of(OSProfile.LinuxConfiguration.SSH.SSHPublicKey
                     .create(String.format("/home/%s/.ssh/authorized_keys", adminUsername), template.getOptions()
                           .getPublicKey()))));
         builder.linuxConfiguration(linuxConfiguration);
      }

      return builder.build();
   }

   private NetworkInterfaceCard createNetworkInterfaceCard(String subnetId, String name, String locationName,
         String azureGroup, TemplateOptions options) {
      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(azureGroup);

      PublicIPAddressProperties properties = PublicIPAddressProperties.builder().publicIPAllocationMethod("Static")
            .idleTimeoutInMinutes(4).build();

      String publicIpAddressName = "public-address-" + name;
      PublicIPAddress ip = ipApi.createOrUpdate(publicIpAddressName, locationName, ImmutableMap.of("jclouds", name),
            properties);

      checkState(publicIpAvailable.create(azureGroup).apply(publicIpAddressName),
            "Public IP was not provisioned in the configured timeout");

      final NetworkInterfaceCardProperties.Builder networkInterfaceCardProperties = NetworkInterfaceCardProperties
            .builder()
            .ipConfigurations(
                  ImmutableList.of(IpConfiguration
                        .builder()
                        .name("ipConfig-" + name)
                        .properties(
                              IpConfigurationProperties.builder().privateIPAllocationMethod("Dynamic")
                                    .publicIPAddress(IdReference.create(ip.id())).subnet(IdReference.create(subnetId))
                                    .build()).build()));

      String securityGroup = getOnlyElement(options.getGroups(), null);
      if (securityGroup != null) {
         networkInterfaceCardProperties.networkSecurityGroup(IdReference.create(securityGroup));
      }

      String networkInterfaceCardName = "jc-nic-" + name;
      return api.getNetworkInterfaceCardApi(azureGroup).createOrUpdate(networkInterfaceCardName, locationName,
            networkInterfaceCardProperties.build(), ImmutableMap.of("jclouds", name));
   }

   private StorageProfile createStorageProfile(String name, Image image, String blob) {
      VMImage imageRef = decodeFieldsFromUniqueId(image.getId());
      ImageReference imageReference = null;
      VHD sourceImage = null;
      String osType = null;

      if (!imageRef.custom()) {
         imageReference = ImageReference.builder().publisher(image.getProviderId()).offer(image.getName())
               .sku(image.getVersion()).version("latest").build();
      } else {
         sourceImage = VHD.create(image.getProviderId());

         // TODO: read the ostype from the image blob
         OsFamily osFamily = image.getOperatingSystem().getFamily();
         osType = osFamily == OsFamily.WINDOWS ? "Windows" : "Linux";
      }

      VHD vhd = VHD.create(blob + "vhds/" + name + ".vhd");
      OSDisk osDisk = OSDisk.create(osType, name, vhd, "ReadWrite", "FromImage", sourceImage);

      return StorageProfile.create(imageReference, osDisk, ImmutableList.<DataDisk> of());
   }
}
