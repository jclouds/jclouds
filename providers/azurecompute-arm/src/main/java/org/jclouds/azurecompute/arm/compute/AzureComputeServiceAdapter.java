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

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension.CUSTOM_IMAGE_PREFIX;
import static org.jclouds.util.Predicates2.retry;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collection;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.UrlEscapers;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;
import org.jclouds.azurecompute.arm.compute.functions.VMImageToImage;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.DeploymentBody;
import org.jclouds.azurecompute.arm.domain.DeploymentProperties;
import org.jclouds.azurecompute.arm.domain.ResourceProviderMetaData;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VMHardware;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.Offer;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.VMDeployment;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.features.DeploymentApi;
import org.jclouds.azurecompute.arm.features.OSImageApi;
import org.jclouds.azurecompute.arm.features.VirtualMachineApi;
import org.jclouds.azurecompute.arm.util.DeploymentTemplateBuilder;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.azurecompute.arm.functions.CleanupResources;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.domain.Version;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.base.Splitter;
import static com.google.common.base.Preconditions.checkState;

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

   private final AzureComputeApi api;

   private final AzureComputeConstants azureComputeConstants;

   @Inject
   AzureComputeServiceAdapter(final AzureComputeApi api, final AzureComputeConstants azureComputeConstants,
                              CleanupResources cleanupResources) {

      this.api = api;
      this.azureComputeConstants = azureComputeConstants;

      this.azureGroup = azureComputeConstants.azureResourceGroup();

      logger.debug("AzureComputeServiceAdapter set azuregroup to: " + azureGroup);

      this.cleanupResources = cleanupResources;
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
               VMDeployment vmDeployment = new VMDeployment();
               vmDeployment.deployment = deployment;
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


      NodeAndInitialCredentials<VMDeployment> credential = null;

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

   private void getImagesFromPublisher(String publisherName, List<VMImage> osImagesRef, String location) {


      OSImageApi osImageApi = api.getOSImageApi(location);
      Iterable<Offer> offerList = osImageApi.listOffers(publisherName);

      for (Offer offer : offerList) {
         Iterable<SKU> skuList = osImageApi.listSKUs(publisherName, offer.name());

         for (SKU sku : skuList) {
            Iterable<Version> versionList = osImageApi.listVersions(publisherName, offer.name(), sku.name());
            for (Version version : versionList) {
               VMImage vmImage = VMImage.create(publisherName, offer.name(), sku.name(), version.name(), location, false);
               osImagesRef.add(vmImage);
            }
         }
      }

   }

   private List<VMImage> listImagesByLocation(String location) {
      final List<VMImage> osImages = Lists.newArrayList();
      Iterable<String> publishers = Splitter.on(',').trimResults().omitEmptyStrings().split(this.azureComputeConstants.azureImagePublishers());
      for (String publisher : publishers) {
         getImagesFromPublisher(publisher, osImages, location);
      }
      return osImages;
   }

   @Override
   public Iterable<VMImage> listImages() {

      final List<VMImage> osImages = Lists.newArrayList();
      final List<String> locationIds = Lists.newArrayList();

      for (Location location : listLocations()){
         locationIds.add(location.name());
         osImages.addAll(listImagesByLocation(location.name()));
      }
      checkAndSetImageAvailability(osImages, Sets.newHashSet(locationIds));
      return osImages;
   }

   private void checkAndSetImageAvailability(List<VMImage> images, Collection<String> locations) {
      Multimap<String, String> map = ArrayListMultimap.create();

      for (VMImage image : images) {
         map.put( image.offer() + "/" + image.sku(), image.location());
      }
      ///TODO
      //      for (VMImage image : images) {
      //         image.globallyAvailable() = map.get(image.offer() + "/" + image.sku()).containsAll(locations);
      //      }
   }

   @Override
   public VMImage getImage(final String id) {
      String[] fields = VMImageToImage.decodeFieldsFromUniqueId(id);
      if (fields[2].startsWith(CUSTOM_IMAGE_PREFIX)) {
         String name = fields[2].substring(CUSTOM_IMAGE_PREFIX.length());
         String sku = fields[3];
         String version = "1";
         VMImage ref = VMImage.create(CUSTOM_IMAGE_PREFIX + azureGroup, CUSTOM_IMAGE_PREFIX + name, sku, version, fields[0], false);
         return ref;
      }

      Iterable<VMImage> images = listImages();

      for (VMImage image : images) {
         String imageId = VMImageToImage.encodeFieldsToUniqueId(image);
         if (id.equals(imageId)){
            return image;
         }
      }
      return null;
   }

   @Override
   public Iterable<Location> listLocations() {

      List<Location> locations = api.getLocationApi().list();

      List<ResourceProviderMetaData> resources = api.getResourceProviderApi().get("Microsoft.Compute");

      final List<String> vmLocations = new ArrayList<String>();

      for (ResourceProviderMetaData m : resources){
         if (m.resourceType().equals("virtualMachines")){
            vmLocations.addAll(m.locations());
            break;
         }
      }

      Iterable<Location> result = Iterables.filter(locations, new Predicate<Location>() {
         @Override
         public boolean apply(Location input) {
            return vmLocations.contains(input.displayName());
         }
      });

      return result;
   }

   private String getResourceGroupFromId(String id) {
      String searchStr = "/resourceGroups/";
      int indexStart = id.lastIndexOf(searchStr) + searchStr.length();
      searchStr = "/providers/";
      int indexEnd = id.lastIndexOf(searchStr);

      String resourceGroup = id.substring(indexStart, indexEnd);
      return resourceGroup;
   }

   @Override
   public VMDeployment getNode(final String id) {
      Deployment deployment = api.getDeploymentApi(azureGroup).get(id);
      if (deployment == null)
         return null;
      String resourceGroup = getResourceGroupFromId(deployment.id());
      VMDeployment vmDeployment = new VMDeployment();
      vmDeployment.deployment = deployment;
      List<PublicIPAddress> list = getIPAddresses(deployment);
      vmDeployment.ipAddressList = list;
      VirtualMachine vm = api.getVirtualMachineApi(azureGroup).get(id);
      vmDeployment.virtualMachine = vm;
      vmDeployment.vm = api.getVirtualMachineApi(azureGroup).getInstanceDetails(id);
      if (vm != null && vm.tags() != null) {
         vmDeployment.userMetaData = vm.tags();
         String tagString = vmDeployment.userMetaData.get("tags");
         List<String> tags = Arrays.asList(tagString.split(","));
         vmDeployment.tags = tags;
      }
      return vmDeployment;
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

   private List<PublicIPAddress> getIPAddresses(Deployment deployment) {
      List<PublicIPAddress> list = new ArrayList<PublicIPAddress>();
      String resourceGroup = getResourceGroupFromId(deployment.id());

      if (deployment.properties() != null && deployment.properties().dependencies() != null) {
         List<Deployment.Dependency> dependencies = deployment.properties().dependencies();
         for (int d = 0; d < dependencies.size(); d++) {
            if (dependencies.get(d).resourceType().equals("Microsoft.Network/networkInterfaces")) {
               List<Deployment.Dependency> dependsOn = dependencies.get(d).dependsOn();
               for (int e = 0; e < dependsOn.size(); e++) {
                  if (dependsOn.get(e).resourceType().equals("Microsoft.Network/publicIPAddresses")) {
                     String resourceName = dependsOn.get(e).resourceName();
                     PublicIPAddress ip = api.getPublicIPAddressApi(resourceGroup).get(resourceName);
                     list.add(ip);
                     break;
                  }
               }
            }
         }
      }
      return list;
   }

   @Override
   public Iterable<VMDeployment> listNodes() {
      List<Deployment> deployments = api.getDeploymentApi(azureGroup).list();

      List<VMDeployment> vmDeployments = new ArrayList<VMDeployment>();

      for (Deployment d : deployments){
         VMDeployment vmDeployment = new VMDeployment();
         vmDeployment.deployment = d;
         VirtualMachineApi vmApi = api.getVirtualMachineApi(azureGroup);
         vmDeployment.vm = vmApi.getInstanceDetails(d.name());
         List<PublicIPAddress> list = getIPAddresses(d);
         vmDeployment.ipAddressList = list;

         VirtualMachine vm = vmApi.get(d.name());
         vmDeployment.virtualMachine = vm;

         if (vm != null && vm.tags() != null) {
            vmDeployment.userMetaData = vm.tags();
            String tagString = vmDeployment.userMetaData.get("tags");
            List<String> tags = Arrays.asList(tagString.split(","));
            vmDeployment.tags = tags;
         }
         vmDeployments.add(vmDeployment);
      }
      return vmDeployments;
   }

   @Override
   public Iterable<VMDeployment> listNodesByIds(final Iterable<String> ids) {
      return Iterables.filter(listNodes(), new Predicate<VMDeployment>() {
         @Override
         public boolean apply(final VMDeployment input) {
            return Iterables.contains(ids, input.deployment.name());
         }
      });
   }

}
