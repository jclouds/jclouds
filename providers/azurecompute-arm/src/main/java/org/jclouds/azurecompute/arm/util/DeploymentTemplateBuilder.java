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
package org.jclouds.azurecompute.arm.util;

import com.google.common.collect.ImmutableMap;
import com.google.inject.assistedinject.Assisted;

import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule;
import org.jclouds.azurecompute.arm.domain.DeploymentProperties;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.DeploymentBody;
import org.jclouds.azurecompute.arm.domain.DeploymentTemplate;
import org.jclouds.azurecompute.arm.domain.DiagnosticsProfile;
import org.jclouds.azurecompute.arm.domain.DnsSettings;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.StorageService.StorageServiceProperties;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.Subnet.SubnetProperties;
import org.jclouds.azurecompute.arm.domain.VHD;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork.VirtualNetworkProperties;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork.AddressSpace;
import org.jclouds.compute.domain.Template;
import org.jclouds.json.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.STORAGE_API_VERSION;

public class DeploymentTemplateBuilder {
   public interface Factory {
      DeploymentTemplateBuilder create(@Assisted("group") String group, @Assisted("name") String name, Template template);
   }

   private final String name;
   private final String group;
   private final Template template;
   private final Json json;

   private TemplateOptions options;
   private List<ResourceDefinition> resources;
   private Map<String, String> variables;
   private static String loginUser;
   private static String loginPassword;
   private String location;
   private AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants;

   private static final String DEPLOYMENT_MODE = "Incremental";
   private static final String DEFAULT_DATA_DISK_SIZE = "1023";

   private static final String DEFAULT_vnAddresSpacePrefix = "10.0.0.0/16";
   private static final String DEFAULT_subnetAddressPrefix = "10.0.0.0/24";

   @Inject
   DeploymentTemplateBuilder(Json json, @Assisted("group") String group, @Assisted("name") String name, @Assisted Template template,
                             final AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants) {
      this.name = name;
      this.group = group;
      this.template = template;
      this.options = template.getOptions().as(TemplateOptions.class);
      this.variables = new HashMap<String, String>();
      this.resources = new ArrayList<ResourceDefinition>();
      this.location = template.getLocation().getId();
      this.json = json;

      this.azureComputeConstants = azureComputeConstants;

      String[] defaultLogin = this.azureComputeConstants.azureDefaultImageLogin().split(":");
      String defaultUser = null;
      String defaultPassword = null;

      if (defaultLogin.length == 2) {
         defaultUser = defaultLogin[0].trim();
         defaultPassword = defaultLogin[1].trim();
      }

      loginUser = options.getLoginUser() == null ? defaultUser : options.getLoginUser();
      loginPassword = options.getLoginPassword() == null ? defaultPassword : options.getLoginPassword();
   }

   public static String getLoginUserUsername() {
      return loginUser;
   }

   public static String getLoginPassword() {
      return loginPassword;
   }

   public Template getTemplate() {
      return template;
   }

   public DeploymentBody getDeploymentTemplate() {

      addStorageResource();
      addVirtualNetworkResource();
      addPublicIpAddress();
      addNetworkInterfaceCard();
      addVirtualMachine();

      DeploymentTemplate template = DeploymentTemplate.builder()
              .schema("https://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#")
              .contentVersion("1.0.0.0")
              .resources(resources)
              .variables(variables)
              .parameters(DeploymentTemplate.Parameters.create())
              .build();

      DeploymentBody body = DeploymentBody.create(template, DEPLOYMENT_MODE, DeploymentTemplate.Parameters.create());

      return body;
   }

   public String getDeploymentTemplateJson(DeploymentProperties properties){
      return json.toJson(properties);
   }

   private void addStorageResource() {
      String storageAccountName = name.replaceAll("[^A-Za-z0-9 ]", "") + "storage";

      variables.put("storageAccountName", storageAccountName);

      ResourceDefinition storageAccount = ResourceDefinition.builder()
              .name("[variables('storageAccountName')]")
              .type("Microsoft.Storage/storageAccounts")
              .location(location)
              .apiVersion(STORAGE_API_VERSION)
              .properties(
                      StorageServiceProperties.builder()
                              .accountType(StorageService.AccountType.Standard_LRS)
                              .build()
              )
              .build();

      resources.add(storageAccount);
   }

   private void addVirtualNetworkResource() {
      String virtualNetworkName = group + "virtualnetwork";

      String subnetName = group + "subnet";
      variables.put("virtualNetworkName", virtualNetworkName);
      variables.put("virtualNetworkReference", "[resourceId('Microsoft.Network/virtualNetworks',variables('virtualNetworkName'))]");
      variables.put("subnetName", subnetName);
      variables.put("subnetReference", "[concat(variables('virtualNetworkReference'),'/subnets/',variables('subnetName'))]");

      VirtualNetworkProperties properties = VirtualNetworkProperties.builder()
              .addressSpace(
                      AddressSpace.create(Arrays.asList(DEFAULT_vnAddresSpacePrefix))
              )
              .subnets(
                      Arrays.asList(
                              Subnet.create("[variables('subnetName')]", null, null,
                                      SubnetProperties.builder()
                                              .addressPrefix(DEFAULT_subnetAddressPrefix).build()
                              ))
              )
              .build();


      ResourceDefinition virtualNetwork = ResourceDefinition.builder()
              .name("[variables('virtualNetworkName')]")
              .type("Microsoft.Network/virtualNetworks")
              .location(location)
              .apiVersion(STORAGE_API_VERSION)
              .properties(properties)
              .build();

      resources.add(virtualNetwork);
   }

   private void addPublicIpAddress() {
      String publicIPAddressName = name + "publicip";
      String dnsLabelPrefix = name; //TODO: read from Azure template properties

      PublicIPAddressProperties.Builder properties = PublicIPAddressProperties.builder();

      if (!dnsLabelPrefix.isEmpty()) {
         properties.dnsSettings(DnsSettings.builder().domainNameLabel(dnsLabelPrefix).build());
         variables.put("dnsLabelPrefix", dnsLabelPrefix);
      }

      properties.publicIPAllocationMethod("Dynamic");
      variables.put("publicIPAddressName", publicIPAddressName);
      variables.put("publicIPAddressReference", "[resourceId('Microsoft.Network/publicIPAddresses',variables('publicIPAddressName'))]");

      ResourceDefinition publicIpAddress = ResourceDefinition.builder()
              .name("[variables('publicIPAddressName')]")
              .type("Microsoft.Network/publicIPAddresses")
              .location(location)
              .apiVersion(STORAGE_API_VERSION)
              .properties(properties.build())
              .build();

      resources.add(publicIpAddress);
   }

   private void addNetworkInterfaceCard() {

      List<IpConfiguration> ipConfigurations = new ArrayList<IpConfiguration>();

      String ipConfigurationName = name + "ipconfig";
      variables.put("ipConfigurationName", ipConfigurationName);

      IpConfiguration ipConfig = IpConfiguration.create(ipConfigurationName, null, null, null,
              IpConfigurationProperties.builder()
                      .privateIPAllocationMethod("Dynamic")
                      .publicIPAddress(IdReference.create("[variables('publicIPAddressReference')]"))
                      .subnet(IdReference.create("[variables('subnetReference')]"))
                      .build());

      ipConfigurations.add(ipConfig);

      NetworkInterfaceCardProperties networkInterfaceCardProperties = NetworkInterfaceCardProperties.builder()
              .ipConfigurations(ipConfigurations)
              .build();

      String networkInterfaceCardName = name + "nic";
      variables.put("networkInterfaceCardName", networkInterfaceCardName);
      variables.put("networkInterfaceCardReference", "[resourceId('Microsoft.Network/networkInterfaces',variables('networkInterfaceCardName'))]");

      ResourceDefinition networkInterfaceCard = ResourceDefinition.builder()
              .name("[variables('networkInterfaceCardName')]")
              .type("Microsoft.Network/networkInterfaces")
              .location(location)
              .apiVersion(STORAGE_API_VERSION)
              .dependsOn(Arrays.asList("[concat('Microsoft.Network/publicIPAddresses/', variables('publicIPAddressName'))]",
                      "[concat('Microsoft.Network/virtualNetworks/', variables('virtualNetworkName'))]"))
              .properties(networkInterfaceCardProperties)
              .build();

      resources.add(networkInterfaceCard);
   }

   private void addVirtualMachine() {

      //Build OS Profile
      final String computerName = name + "pc";

      variables.put("loginUser", loginUser);
      OSProfile.Builder profileBuilder = OSProfile.builder()
              .adminUsername(loginUser)
              .computerName(computerName);

      boolean usePublicKey = options.getPublicKey() != null;

      if (usePublicKey) {
         OSProfile.LinuxConfiguration configuration = OSProfile.LinuxConfiguration.create("true",
                 OSProfile.LinuxConfiguration.SSH.create(Arrays.asList(
                         OSProfile.LinuxConfiguration.SSH.SSHPublicKey.create(
                                 "[concat('/home/',variables('loginUser'),'/.ssh/authorized_keys')]",
                                 options.getPublicKey())
                 ))
         );
         profileBuilder.linuxConfiguration(configuration);
      } else {
         profileBuilder.adminPassword(loginPassword);
      }

      OSProfile osProfile = profileBuilder.build();

      //Build Image Reference
      final String imagePublisher = template.getImage().getProviderId();
      final String imageOffer = template.getImage().getName();
      final String imageSku = template.getImage().getVersion();

      ImageReference imageReference = ImageReference.builder()
              .publisher(imagePublisher)
              .offer(imageOffer)
              .sku(imageSku)
              .version("latest")
              .build();

      //Build OsDisk
      final String storageAccountContainerName = "vhds";
      variables.put("storageAccountContainerName", storageAccountContainerName);

      final String osDiskName = name + "osdisk";
      variables.put("osDiskName", osDiskName);

      OSDisk osDisk = OSDisk.builder()
              .name("[variables('osDiskName')]")
              .vhd(
                      VHD.create("[concat('http://',variables('storageAccountName'),'.blob.core.windows.net/',variables('storageAccountContainerName'),'/',variables('osDiskName'),'.vhd')]")
              )
              .caching("ReadWrite")
              .createOption("FromImage")
              .build();


      //Create Data Disk(s) and add to list
      final String dataDiskName = name + "datadisk";
      variables.put("dataDiskName", dataDiskName);

      List<DataDisk> dataDisks = new ArrayList<DataDisk>();
      DataDisk dataDisk = DataDisk.builder()
              .name("[variables('dataDiskName')]")
              .diskSizeGB(DEFAULT_DATA_DISK_SIZE)
              .lun(0)
              .vhd(
                      VHD.create("[concat('http://',variables('storageAccountName'),'.blob.core.windows.net/',variables('storageAccountContainerName'),'/',variables('dataDiskName'),'.vhd')]")
              )
              .createOption("Empty")
              .build();

      dataDisks.add(dataDisk);

      //Create Storage Profile
      StorageProfile storageProfile = StorageProfile.builder()
              .imageReference(imageReference)
              .osDisk(osDisk)
              .dataDisks(dataDisks)
              .build();

      //Create Network Profile for this VM (links to network interface cards)
      NetworkProfile networkProfile = NetworkProfile.create(
              Arrays.asList(
                      IdReference.create("[variables('networkInterfaceCardReference')]")
              ));

      //Boot Diagnostics
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(
              DiagnosticsProfile.BootDiagnostics.builder()
                      .enabled(true)
                      .storageUri("[concat('http://',variables('storageAccountName'),'.blob.core.windows.net')]")
                      .build());

      //Build VirtualMachine properties based on above properties.
      final String vmSize = template.getHardware().getId();
      HardwareProfile hw = HardwareProfile.create(vmSize);

      VirtualMachineProperties properties = VirtualMachineProperties.builder()
              .hardwareProfile(hw)
              .osProfile(osProfile)
              .storageProfile(storageProfile)
              .networkProfile(networkProfile)
              .diagnosticsProfile(diagnosticsProfile)
              .build();

      variables.put("virtualMachineName", name);
      ResourceDefinition virtualMachine = ResourceDefinition.builder()
              .name("[variables('virtualMachineName')]")
              .type("Microsoft.Compute/virtualMachines")
              .location(location)
              .apiVersion(STORAGE_API_VERSION)
              .dependsOn(Arrays.asList("[concat('Microsoft.Storage/storageAccounts/', variables('storageAccountName'))]",
                      "[concat('Microsoft.Network/networkInterfaces/', variables('networkInterfaceCardName'))]"))
              .tags(ImmutableMap.of("displayName", "VirtualMachine"))
              .properties(properties)
              .build();

      resources.add(virtualMachine);
   }

}
