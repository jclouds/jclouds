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
package org.jclouds.azurecompute.arm.features;

import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.azurecompute.arm.domain.DeploymentBody;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.StorageService.StorageServiceProperties;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork.VirtualNetworkProperties;
import org.jclouds.azurecompute.arm.util.DeploymentTemplateBuilder;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.TemplateImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


@Test(groups = "unit", testName = "DeploymentTemplateBuilderTest", singleThreaded = true)
public class DeploymentTemplateBuilderTest extends BaseAzureComputeApiMockTest {

   final String group = "jcgroup";

   @Test
   public void testResourceGroup() {
      DeploymentTemplateBuilder builder = getMockDeploymentTemplateBuilderWithEmptyOptions();
      DeploymentBody deploymentBody = builder.getDeploymentTemplate();
      List<ResourceDefinition> resources = deploymentBody.template().resources();
      Map<String, String> variables = deploymentBody.template().variables();

      ResourceDefinition resource = getResourceByType(resources, "Microsoft.Storage/storageAccounts");

      StorageServiceProperties properties = (StorageServiceProperties) resource.properties();
      assertEquals(properties.accountType(), StorageService.AccountType.Standard_LRS);
      assertTrue(variables.containsKey(parseVariableName(resource.name())));
   }

   @Test
   void testVirtualNetwork() {
      DeploymentTemplateBuilder builder = getMockDeploymentTemplateBuilderWithEmptyOptions();
      DeploymentBody deploymentBody = builder.getDeploymentTemplate();
      List<ResourceDefinition> resources = deploymentBody.template().resources();
      Map<String, String> variables = deploymentBody.template().variables();

      ResourceDefinition resource = getResourceByType(resources, "Microsoft.Network/virtualNetworks");

      VirtualNetworkProperties properties = (VirtualNetworkProperties) resource.properties();
      assertTrue(properties.addressSpace().addressPrefixes().size() > 0);
      assertTrue(properties.subnets().size() > 0);

      assertTrue(variables.containsKey(parseVariableName(resource.name())));
   }

   @Test
   void testPublicIpAddress() {
      DeploymentTemplateBuilder builder = getMockDeploymentTemplateBuilderWithEmptyOptions();
      DeploymentBody deploymentBody = builder.getDeploymentTemplate();
      List<ResourceDefinition> resources = deploymentBody.template().resources();
      Map<String, String> variables = deploymentBody.template().variables();

      ResourceDefinition resource = getResourceByType(resources, "Microsoft.Network/publicIPAddresses");

      PublicIPAddressProperties properties = (PublicIPAddressProperties) resource.properties();
      assertEquals(properties.publicIPAllocationMethod(), "Dynamic");
      assertTrue(variables.containsKey(parseVariableName(resource.name())));
   }

   @Test
   void testNetworkInterfaceCard() {
      DeploymentTemplateBuilder builder = getMockDeploymentTemplateBuilderWithEmptyOptions();
      DeploymentBody deploymentBody = builder.getDeploymentTemplate();
      List<ResourceDefinition> resources = deploymentBody.template().resources();
      Map<String, String> variables = deploymentBody.template().variables();

      ResourceDefinition resource = getResourceByType(resources, "Microsoft.Network/networkInterfaces");

      NetworkInterfaceCardProperties properties = (NetworkInterfaceCardProperties) resource.properties();
      List<IpConfiguration> ipConfigs = properties.ipConfigurations();
      assertTrue(ipConfigs.size() > 0);
      IpConfigurationProperties ipProperties = ipConfigs.get(0).properties();
      assertEquals(ipProperties.privateIPAllocationMethod(), "Dynamic");
      assertNotNull(ipProperties.publicIPAddress());
      assertNotNull(ipProperties.subnet());

      assertTrue(variables.containsKey(parseVariableName(resource.name())));
   }


   @Test
   void testVirtualMachine() {
      DeploymentTemplateBuilder builder = getMockDeploymentTemplateBuilderWithEmptyOptions();
      Template template = builder.getTemplate();

      DeploymentBody deploymentBody = builder.getDeploymentTemplate();
      List<ResourceDefinition> resources = deploymentBody.template().resources();
      Map<String, String> variables = deploymentBody.template().variables();

      ResourceDefinition resource = getResourceByType(resources, "Microsoft.Compute/virtualMachines");
      assertNotNull(resource);

      VirtualMachineProperties properties = (VirtualMachineProperties) resource.properties();
      assertEquals(properties.hardwareProfile().vmSize(), template.getHardware().getId());

      ImageReference image = properties.storageProfile().imageReference();
      assertEquals(image.publisher(), template.getImage().getProviderId());
      assertEquals(image.offer(), template.getImage().getName());
      assertEquals(image.sku(), template.getImage().getVersion());
      assertEquals(image.version(), "latest");

      assertTrue(variables.containsKey(parseVariableName(resource.name())));
   }

   @Test
   void testVirtualMachineWithSSH() {

      String rsakey = new String("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAmfk/QSF0pvnrpdz+Ah2KulGruKU+8FFBdlw938MpOysRdmp7uwpH6Z7+5VNGNdxFIAyc/W3UaZXF9hTsU8+78TlwkZpsr2mzU+ycu37XLAQ8Uv7hjsAN0DkKKPrZ9lgUUfZVKV/8E/JIAs03gIbL6zO3y7eYJQ5fNeZb+nji7tQT+YLpGq/FDegvraPKVMQbCSCZhsHyWhdPLyFlu9/30npZ0ahYOPI/KyZxFDtM/pHp88+ZAk9Icq5owaLRWcJQqrBGWqjbZnHtjdDqvHZ+C0wPhdJZPyfkHOrSYTwSQBXfX4JLRRCz3J1jf62MbQWT1o6Y4JEs1ZP1Skxu6zR96Q== mocktest");

      TemplateOptions options = new TemplateOptions();
      options.authorizePublicKey(rsakey);

      DeploymentTemplateBuilder builder = getMockDeploymentTemplateBuilderWithOptions(options);
      Template template = builder.getTemplate();

      DeploymentBody deploymentBody = builder.getDeploymentTemplate();
      List<ResourceDefinition> resources = deploymentBody.template().resources();
      Map<String, String> variables = deploymentBody.template().variables();

      ResourceDefinition resource = getResourceByType(resources, "Microsoft.Compute/virtualMachines");
      assertNotNull(resource);

      VirtualMachineProperties properties = (VirtualMachineProperties) resource.properties();
      assertEquals(properties.hardwareProfile().vmSize(), template.getHardware().getId());

      ImageReference image = properties.storageProfile().imageReference();
      assertEquals(image.publisher(), template.getImage().getProviderId());
      assertEquals(image.offer(), template.getImage().getName());
      assertEquals(image.sku(), template.getImage().getVersion());
      assertEquals(image.version(), "latest");

      // Check that ssh key is in place
      OSProfile.LinuxConfiguration osConfig = properties.osProfile().linuxConfiguration();
      assertEquals(osConfig.disablePasswordAuthentication(), "true");
      assertTrue(osConfig.ssh().publicKeys().size() > 0);
      assertEquals(osConfig.ssh().publicKeys().get(0).keyData(), rsakey);

      assertTrue(variables.containsKey(parseVariableName(resource.name())));
   }

   private Template getMockTemplate(TemplateOptions options) {
      Location provider = (new LocationBuilder()).scope(LocationScope.PROVIDER).id("azurecompute-arm").description("azurecompute-arm").build();
      Location region = (new LocationBuilder()).scope(LocationScope.REGION).id("northeurope").description("North Europe").parent(provider).build();
      OperatingSystem os = OperatingSystem.builder().name("osName").version("osVersion").description("osDescription").arch("X86_32").build();
      //Note that version is set to "latest"
      Image image = (new ImageBuilder()).id("imageId").providerId("imageId").name("imageName").description("imageDescription").version("sku").operatingSystem(os).status(Image.Status.AVAILABLE).location(region).build();
      Hardware hardware = (new HardwareBuilder()).id("Standard_A0").build();
      return new TemplateImpl(image, hardware, region, options);
   }

   private DeploymentTemplateBuilder getMockDeploymentTemplateBuilderWithEmptyOptions() {
      TemplateOptions options = new TemplateOptions();
      Template template = getMockTemplate(options);
      DeploymentTemplateBuilder templateBuilder = api.deploymentTemplateFactory().create(group, "mydeployment", template);
      return templateBuilder;
   }

   private DeploymentTemplateBuilder getMockDeploymentTemplateBuilderWithOptions(TemplateOptions options) {
      Template template = getMockTemplate(options);
      DeploymentTemplateBuilder templateBuilder = api.deploymentTemplateFactory().create(group, "mydeployment", template);
      return templateBuilder;
   }

   private ResourceDefinition getResourceByType(List<ResourceDefinition> resources, String type) {
      for (ResourceDefinition r : resources) {
         if (r.type().equals(type)) {
            return r;
         }
      }
      fail("Resource with type: " + type + " not found");
      return null;
   }

   private String parseVariableName(String variable) {
      String[] parts = variable.split("\'");
      assertTrue(parts.length == 3);
      return parts[1];
   }
}

