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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;
import org.jclouds.azurecompute.arm.compute.functions.DeploymentToVMDeployment;
import org.jclouds.azurecompute.arm.compute.functions.VMImageToImage;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.DeploymentBody;
import org.jclouds.azurecompute.arm.domain.DeploymentProperties;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.Offer;
import org.jclouds.azurecompute.arm.domain.ResourceProviderMetaData;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.VMDeployment;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.domain.Version;
import org.jclouds.azurecompute.arm.domain.VMHardware;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.Value;
import org.jclouds.azurecompute.arm.features.DeploymentApi;
import org.jclouds.azurecompute.arm.features.OSImageApi;
import org.jclouds.azurecompute.arm.functions.CleanupResources;
import org.jclouds.azurecompute.arm.util.BlobHelper;
import org.jclouds.azurecompute.arm.util.DeploymentTemplateBuilder;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.json.Json;
import org.jclouds.location.reference.LocationConstants;
import org.jclouds.logging.Logger;
import org.jclouds.providers.ProviderMetadata;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.net.UrlEscapers;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

/**
 * Defines the connection between the {@link AzureComputeApi} implementation and the jclouds
 * {@link org.jclouds.compute.ComputeService}.
 */
@Singleton
public class AzureComputeServiceAdapter implements ComputeServiceAdapter<VMDeployment, VMHardware, VMImage, Location> {

   private String azureGroup;
   protected final CleanupResources cleanupResources;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;
   private final Json json;
   private final AzureComputeApi api;
   private final AzureComputeConstants azureComputeConstants;
   private final ProviderMetadata providerMetadata;
   private final DeploymentToVMDeployment deploymentToVMDeployment;

   @Inject
   AzureComputeServiceAdapter(final AzureComputeApi api, final AzureComputeConstants azureComputeConstants,
                              CleanupResources cleanupResources, Json json, ProviderMetadata providerMetadata, DeploymentToVMDeployment deploymentToVMDeployment) {
      this.json = json;
      this.api = api;
      this.azureComputeConstants = azureComputeConstants;
      this.azureGroup = azureComputeConstants.azureResourceGroup();

      logger.debug("AzureComputeServiceAdapter set azuregroup to: " + azureGroup);

      this.cleanupResources = cleanupResources;
      this.providerMetadata = providerMetadata;
      this.deploymentToVMDeployment = deploymentToVMDeployment;
   }

   @Override
   public NodeAndInitialCredentials<VMDeployment> createNodeWithGroupEncodedIntoName(
           final String group, final String name, final Template template) {

      DeploymentTemplateBuilder deploymentTemplateBuilder = api.deploymentTemplateFactory().create(group, name, template);

      final String loginUser = DeploymentTemplateBuilder.getLoginUserUsername();
      final String loginPassword = DeploymentTemplateBuilder.getLoginPassword();

      DeploymentBody deploymentTemplateBody =  deploymentTemplateBuilder.getDeploymentTemplate();

      DeploymentProperties properties = DeploymentProperties.create(deploymentTemplateBody);

      final String deploymentTemplate = UrlEscapers.urlFormParameterEscaper().escape(deploymentTemplateBuilder.getDeploymentTemplateJson(properties));

      logger.debug("Deployment created with name: %s group: %s", name, group);


      final Set<VMDeployment> deployments = Sets.newHashSet();

      final DeploymentApi deploymentApi = api.getDeploymentApi(azureGroup);

      if (!retry(new Predicate<String>() {
         @Override
         public boolean apply(final String name) {
            Deployment deployment = deploymentApi.create(name, deploymentTemplate);

            if (deployment != null) {
               VMDeployment vmDeployment = VMDeployment.create(deployment);
               deployments.add(vmDeployment);
            } else {
               logger.debug("Failed to create deployment!");
            }
            return !deployments.isEmpty();
         }
      }, azureComputeConstants.operationTimeout(), 1, SECONDS).apply(name)) {
         final String illegalStateExceptionMessage = format("Deployment %s was not created within %sms so it will be destroyed.",
                 name, azureComputeConstants.operationTimeout());
         logger.warn(illegalStateExceptionMessage);
         destroyNode(name);
         throw new IllegalStateException(illegalStateExceptionMessage);
      }
      final VMDeployment deployment = deployments.iterator().next();
      NodeAndInitialCredentials<VMDeployment> credential;
      if (template.getOptions().getPublicKey() != null){
         String privateKey = template.getOptions().getPrivateKey();
         credential = new NodeAndInitialCredentials<VMDeployment>(deployment, name,
                 LoginCredentials.builder().user(loginUser).privateKey(privateKey).authenticateSudo(true).build());
      } else {
         credential = new NodeAndInitialCredentials<VMDeployment>(deployment, name,
                 LoginCredentials.builder().user(loginUser).password(loginPassword).authenticateSudo(true).build());
      }
      return credential;
   }

   @Override
   public Iterable<VMHardware> listHardwareProfiles() {

      final List<VMHardware> hwProfiles = Lists.newArrayList();
      final List<String> locationIds = Lists.newArrayList();

      Iterable<Location> locations = listLocations();
      for (Location location : locations){
         locationIds.add(location.name());

         Iterable<VMSize> vmSizes = api.getVMSizeApi(location.name()).list();

         for (VMSize vmSize : vmSizes){
            VMHardware hwProfile = VMHardware.create(
                    vmSize.name(),
                    vmSize.numberOfCores(),
                    vmSize.osDiskSizeInMB(),
                    vmSize.resourceDiskSizeInMB(),
                    vmSize.memoryInMB(),
                    vmSize.maxDataDiskCount(),
                    location.name(),
                    false);
            hwProfiles.add(hwProfile);
         }
      }

      checkAndSetHwAvailability(hwProfiles, Sets.newHashSet(locationIds));

      return hwProfiles;
   }
   private void checkAndSetHwAvailability(List<VMHardware> hwProfiles, Collection<String> locations) {
      Multimap<String, String> hwMap = ArrayListMultimap.create();
      for (VMHardware hw : hwProfiles) {
         hwMap.put(hw.name(), hw.location());
      }

      /// TODO
      //      for (VMHardware hw : hwProfiles) {
      //         hw.globallyAvailable() = hwMap.get(hw.name()).containsAll(locations);
      //      }
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
               VMImage vmImage = VMImage.create(publisherName, offer.name(), sku.name(), version.name(), location);
               osImagesRef.add(vmImage);
            }
         }
      }
      return osImagesRef;
   }

   private List<VMImage> listImagesByLocation(String location) {
      final List<VMImage> osImages = Lists.newArrayList();
      Iterable<String> publishers = Splitter.on(',').trimResults().omitEmptyStrings().split(this.azureComputeConstants.azureImagePublishers());
      for (String publisher : publishers) {
         osImages.addAll(getImagesFromPublisher(publisher, location));
      }
      return osImages;
   }

   @Override
   public Iterable<VMImage> listImages() {

      final List<VMImage> osImages = Lists.newArrayList();

      for (Location location : listLocations()){
         osImages.addAll(listImagesByLocation(location.name()));
      }
      // list custom images
      List<StorageService> storages = api.getStorageAccountApi(azureGroup).list();
      for (StorageService storage : storages) {
         String name = storage.name();
         String key = api.getStorageAccountApi(azureGroup).getKeys(name).key1();
            List<VMImage> images = BlobHelper.getImages("jclouds", azureGroup, storage.name(), key,
                  "custom", storage.location());
            osImages.addAll(images);
      }
      return osImages;
   }

   @Override
   public VMImage getImage(final String id) {
      VMImage image = VMImageToImage.decodeFieldsFromUniqueId(id);
      if (image.custom()) {
         String key = api.getStorageAccountApi(azureGroup).getKeys(image.storage()).key1();
         if (BlobHelper.customImageExists(image.storage(), key))
            return image;
         else
            return null;

      }

      String location = image.location();
      String publisher = image.publisher();
      String offer = image.offer();
      String sku = image.sku();

      OSImageApi osImageApi = api.getOSImageApi(location);
      List<Version> versions = osImageApi.listVersions(publisher, offer, sku);
      if (!versions.isEmpty()) {
         return VMImage.create(publisher, offer, sku, versions.get(0).name(), location);
      }
      return null;
   }

   @Override
   public Iterable<Location> listLocations() {
      final Iterable<String> whiteListedRegionNames = findWhiteListOfRegions();

      final Iterable<String> vmLocations = FluentIterable.from(api.getResourceProviderApi().get("Microsoft.Compute"))
              .filter(new Predicate<ResourceProviderMetaData>() {
                 @Override
                 public boolean apply(ResourceProviderMetaData input) {
                    return input.resourceType().equals("virtualMachines");
                 }
              })
              .transformAndConcat(new Function<ResourceProviderMetaData, Iterable<String>>() {
                 @Override
                 public Iterable<String> apply(ResourceProviderMetaData resourceProviderMetaData) {
                    return resourceProviderMetaData.locations();
                 }
              });

      List<Location> locations = FluentIterable.from(api.getLocationApi().list())
              .filter(new Predicate<Location>() {
                 @Override
                 public boolean apply(Location location) {
                    return Iterables.contains(vmLocations, location.displayName());
                 }
              })
              .filter(new Predicate<Location>() {
                 @Override
                 public boolean apply(Location location) {
                    return whiteListedRegionNames == null ? true : Iterables.contains(whiteListedRegionNames, location.name());
                 }
              })
              .toList();

      return locations;
   }

   @Override
   public VMDeployment getNode(final String id) {
      Deployment deployment = api.getDeploymentApi(azureGroup).get(id);
      if (deployment == null) return null;
      if (new IsDeploymentInRegions(findWhiteListOfRegions()).apply(deployment)) {
         return deploymentToVMDeployment.apply(deployment);
      }
      return null;
   }

   @Override
   public void destroyNode(final String id) {
      checkState(cleanupResources.apply(id), "server(%s) and its resources still there after deleting!?", id);
   }

   @Override
   public void rebootNode(final String id) {
      api.getVirtualMachineApi(azureGroup).restart(id);
   }

   @Override
   public void resumeNode(final String id) {
      api.getVirtualMachineApi(azureGroup).start(id);
   }

   @Override
   public void suspendNode(final String id) {
      api.getVirtualMachineApi(azureGroup).stop(id);
   }

   @Override
   public Iterable<VMDeployment> listNodes() {
      return FluentIterable.from(api.getDeploymentApi(azureGroup).list())
              .filter(new IsDeploymentInRegions(findWhiteListOfRegions()))
              .filter(new Predicate<Deployment>() {
                 @Override
                 public boolean apply(Deployment deployment) {
                    Value storageAccountNameValue = deployment.properties().parameters().get("storageAccountName");
                    String storageAccountName = storageAccountNameValue.value();
                    String key = api.getStorageAccountApi(azureGroup).getKeys(storageAccountName).key1();
                    return !BlobHelper.customImageExists(storageAccountName, key);
                 }
              })
              .transform(deploymentToVMDeployment)
              .toList();
   }

   @Override
   public Iterable<VMDeployment> listNodesByIds(final Iterable<String> ids) {
      return Iterables.filter(listNodes(), new Predicate<VMDeployment>() {
         @Override
         public boolean apply(final VMDeployment input) {
            return Iterables.contains(ids, input.deployment().name());
         }
      });
   }

   private Iterable<String> findWhiteListOfRegions() {
      if (providerMetadata.getDefaultProperties().get(LocationConstants.PROPERTY_REGIONS) == null)  return null;
      return Splitter.on(",").trimResults().split((CharSequence) providerMetadata.getDefaultProperties().get(LocationConstants.PROPERTY_REGIONS));
   }

   private class IsDeploymentInRegions implements Predicate<Deployment> {

      private final Iterable<String> whiteListOfRegions;

      public IsDeploymentInRegions(Iterable<String> whiteListOfRegions) {
         this.whiteListOfRegions = whiteListOfRegions;
      }

      @Override
      public boolean apply(Deployment deployment) {
         Value locationValue = deployment.properties().parameters().get("location");
         return Iterables.contains(whiteListOfRegions, locationValue.value());
      }
   }
}
