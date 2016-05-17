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

import com.google.common.net.UrlEscapers;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.DeploymentBody;
import org.jclouds.azurecompute.arm.domain.DeploymentProperties;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.azurecompute.arm.util.DeploymentTemplateBuilder;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.TemplateImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "TemplateToDeploymentTemplateLiveTest", singleThreaded = true)
public class TemplateToDeploymentTemplateLiveTest extends BaseAzureComputeApiLiveTest {

   private int maxTestDuration = 400;
   private int pollingInterval = 3; // how frequently to poll for create status
   private String resourceGroup;
   private String deploymentName;
   private String vnetName;
   private String subnetId;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      resourceGroup = getResourceGroupName();

      //Subnets belong to a virtual network so that needs to be created first
      VirtualNetwork vn = getOrCreateVirtualNetwork(VIRTUAL_NETWORK_NAME);
      assertNotNull(vn);
      vnetName = vn.name();

      //Subnet needs to be up & running before NIC can be created
      Subnet subnet = getOrCreateSubnet(DEFAULT_SUBNET_NAME, VIRTUAL_NETWORK_NAME);
      assertNotNull(subnet);
      assertNotNull(subnet.id());
      subnetId = subnet.id();
   }

   @Test(groups = "live")
   public void testValidateDeploymentTemplateLinuxNodeWithOptions() {
      Long now = System.currentTimeMillis();
      deploymentName = "jc" + now;

      AzureTemplateOptions options = new AzureTemplateOptions();
      options.virtualNetworkName(vnetName);
      options.subnetId(subnetId);

      options.inboundPorts(22, 8080);

      DeploymentTemplateBuilder templateBuilder = getDeploymentTemplateBuilderWithOptions(options);

      DeploymentBody deploymentTemplateBody = templateBuilder.getDeploymentTemplate();

      DeploymentProperties properties = DeploymentProperties.create(deploymentTemplateBody);

      String deploymentTemplate = templateBuilder.getDeploymentTemplateJson(properties);
      deploymentTemplate = UrlEscapers.urlFormParameterEscaper().escape(deploymentTemplate);

      //Validates that template is syntactically correct
      Deployment deployment = api().validate(deploymentName, deploymentTemplate);
      assertNotNull(deployment);
   }

   @Test(groups = "live")
   public void testValidateDeploymentTemplateLinuxNode() {
      Long now = System.currentTimeMillis();
      deploymentName = "jc" + now;

      DeploymentTemplateBuilder templateBuilder = getDeploymentTemplateBuilderWithEmptyOptions();

      DeploymentBody deploymentTemplateBody = templateBuilder.getDeploymentTemplate();

      DeploymentProperties properties = DeploymentProperties.create(deploymentTemplateBody);

      String deploymentTemplate = templateBuilder.getDeploymentTemplateJson(properties);
      deploymentTemplate = UrlEscapers.urlFormParameterEscaper().escape(deploymentTemplate);

      //Validates that template is syntactically correct
      Deployment deployment = api().validate(deploymentName, deploymentTemplate);
      assertNotNull(deployment);
   }

   @Test(groups = "live")
   public void testValidateDeploymentTemplateWithCustomOptions() {
      Long now = System.currentTimeMillis();
      deploymentName = "jc" + now;

      String rsakey = new String("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAmfk/QSF0pvnrpdz+Ah2KulGruKU+8FFBdlw938MpOysRdmp7uwpH6Z7+5VNGNdxFIAyc/W3UaZXF9hTsU8+78TlwkZpsr2mzU+ycu37XLAQ8Uv7hjsAN0DkKKPrZ9lgUUfZVKV/8E/JIAs03gIbL6zO3y7eYJQ5fNeZb+nji7tQT+YLpGq/FDegvraPKVMQbCSCZhsHyWhdPLyFlu9/30npZ0ahYOPI/KyZxFDtM/pHp88+ZAk9Icq5owaLRWcJQqrBGWqjbZnHtjdDqvHZ+C0wPhdJZPyfkHOrSYTwSQBXfX4JLRRCz3J1jf62MbQWT1o6Y4JEs1ZP1Skxu6zR96Q== mocktest");
      TemplateOptions options = new AzureTemplateOptions()
              .DNSLabelPrefix("mydnslabel")
              .virtualNetworkAddressPrefix("10.0.0.0/20")
              .subnetAddressPrefix("10.0.0.0/25")
              .authorizePublicKey(rsakey);

      ((AzureTemplateOptions)options).virtualNetworkName(vnetName);
      ((AzureTemplateOptions)options).subnetId(subnetId);

      DeploymentTemplateBuilder templateBuilder = getDeploymentTemplateBuilderWithOptions(options);

      DeploymentBody deploymentTemplateBody = templateBuilder.getDeploymentTemplate();

      DeploymentProperties properties = DeploymentProperties.create(deploymentTemplateBody);

      String deploymentTemplate = templateBuilder.getDeploymentTemplateJson(properties);
      deploymentTemplate = UrlEscapers.urlFormParameterEscaper().escape(deploymentTemplate);

      Deployment deployment = api().validate(deploymentName, deploymentTemplate);
      assertNotNull(deployment);
   }

   @Test(groups = "live")
   public void testValidateDeploymentTemplateLinuxNodeWithSSH() {
      Long now = System.currentTimeMillis();
      deploymentName = "jc" + now;

      String rsakey = new String("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAmfk/QSF0pvnrpdz+Ah2KulGruKU+8FFBdlw938MpOysRdmp7uwpH6Z7+5VNGNdxFIAyc/W3UaZXF9hTsU8+78TlwkZpsr2mzU+ycu37XLAQ8Uv7hjsAN0DkKKPrZ9lgUUfZVKV/8E/JIAs03gIbL6zO3y7eYJQ5fNeZb+nji7tQT+YLpGq/FDegvraPKVMQbCSCZhsHyWhdPLyFlu9/30npZ0ahYOPI/KyZxFDtM/pHp88+ZAk9Icq5owaLRWcJQqrBGWqjbZnHtjdDqvHZ+C0wPhdJZPyfkHOrSYTwSQBXfX4JLRRCz3J1jf62MbQWT1o6Y4JEs1ZP1Skxu6zR96Q== mocktest");

      AzureTemplateOptions options = new AzureTemplateOptions();
      options.virtualNetworkName(vnetName);
      options.subnetId(subnetId);

      options.authorizePublicKey(rsakey);
      DeploymentTemplateBuilder templateBuilder = getDeploymentTemplateBuilderWithOptions(options);

      DeploymentBody deploymentTemplateBody = templateBuilder.getDeploymentTemplate();

      DeploymentProperties properties = DeploymentProperties.create(deploymentTemplateBody);

      String deploymentTemplate = templateBuilder.getDeploymentTemplateJson(properties);
      deploymentTemplate = UrlEscapers.urlFormParameterEscaper().escape(deploymentTemplate);

      Deployment deployment = api().validate(deploymentName, deploymentTemplate);
      assertNotNull(deployment);
   }

   @Test(groups = "live")
   public void testCreateDeploymentTemplateLinuxNode() {
      Long now = System.currentTimeMillis();
      deploymentName = "jc" + now;

      String rsakey = new String("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAmfk/QSF0pvnrpdz+Ah2KulGruKU+8FFBdlw938MpOysRdmp7uwpH6Z7+5VNGNdxFIAyc/W3UaZXF9hTsU8+78TlwkZpsr2mzU+ycu37XLAQ8Uv7hjsAN0DkKKPrZ9lgUUfZVKV/8E/JIAs03gIbL6zO3y7eYJQ5fNeZb+nji7tQT+YLpGq/FDegvraPKVMQbCSCZhsHyWhdPLyFlu9/30npZ0ahYOPI/KyZxFDtM/pHp88+ZAk9Icq5owaLRWcJQqrBGWqjbZnHtjdDqvHZ+C0wPhdJZPyfkHOrSYTwSQBXfX4JLRRCz3J1jf62MbQWT1o6Y4JEs1ZP1Skxu6zR96Q== mocktest");

      AzureTemplateOptions options = new AzureTemplateOptions();
      options.virtualNetworkName(vnetName);
      options.subnetId(subnetId);

      options.authorizePublicKey(rsakey);
      options.inboundPorts(22, 8080);
      DeploymentTemplateBuilder templateBuilder = getDeploymentTemplateBuilderWithOptions(options);

      DeploymentBody deploymentTemplateBody = templateBuilder.getDeploymentTemplate();
      DeploymentProperties properties = DeploymentProperties.create(deploymentTemplateBody);

      String deploymentTemplate = templateBuilder.getDeploymentTemplateJson(properties);
      deploymentTemplate = UrlEscapers.urlFormParameterEscaper().escape(deploymentTemplate);

      //creates an actual VM using deployment template
      Deployment deployment = api().create(deploymentName, deploymentTemplate);

      Deployment.ProvisioningState state = Deployment.ProvisioningState.fromValue(deployment.properties().provisioningState());
      int testTime = 0;
      while (testTime < maxTestDuration) {
         if ((state == Deployment.ProvisioningState.SUCCEEDED) ||
                 (state == Deployment.ProvisioningState.CANCELED) ||
                 (state == Deployment.ProvisioningState.DELETED) ||
                 (state == Deployment.ProvisioningState.FAILED)) {
            break;
         }

         // sleep a little bit before polling, timeout after a fixed time
         try {
            Thread.sleep(pollingInterval * 1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         testTime += pollingInterval;

         deployment = api().get(deploymentName);
         assertNotNull(deployment);
         state = Deployment.ProvisioningState.fromValue(deployment.properties().provisioningState());
      }
      assertTrue(state == Deployment.ProvisioningState.SUCCEEDED);
      assertNotNull(deployment);
   }

   private Template getTemplate(TemplateOptions options) {
      Location provider = (new LocationBuilder()).scope(LocationScope.PROVIDER).id("azurecompute-arm").description("azurecompute-arm").build();
      Location region = (new LocationBuilder()).scope(LocationScope.REGION).id(LOCATION).description(LOCATIONDESCRIPTION).parent(provider).build();

      OperatingSystem os = OperatingSystem.builder()
              .family(OsFamily.UBUNTU)
              .description("14.04.3-LTS")
              .is64Bit(true)
              .build();

      Image image = (new ImageBuilder())
              .id("UbuntuServer14.04.3-LTS")
              .providerId("Canonical")
              .name("UbuntuServer")
              .description("14.04.3-LTS")
              .version("14.04.3-LTS")
              .operatingSystem(os)
              .status(Image.Status.AVAILABLE)
              .location(region)
              .build();

      Hardware hardware = (new HardwareBuilder()).id("Standard_A0").build();
      return new TemplateImpl(image, hardware, region, options);
   }

   private DeploymentTemplateBuilder getDeploymentTemplateBuilderWithEmptyOptions() {
      AzureTemplateOptions options = new AzureTemplateOptions();
      options.virtualNetworkName(vnetName);
      options.subnetId(subnetId);

      Template template = getTemplate(options);
      DeploymentTemplateBuilder templateBuilder = api.deploymentTemplateFactory().create(resourceGroup, deploymentName, template);
      return templateBuilder;
   }

   private DeploymentTemplateBuilder getDeploymentTemplateBuilderWithOptions(TemplateOptions options) {
      Template template = getTemplate(options);
      DeploymentTemplateBuilder templateBuilder = api.deploymentTemplateFactory().create(resourceGroup, deploymentName, template);
      return templateBuilder;
   }

   private DeploymentApi api() {
      return api.getDeploymentApi(resourceGroup);
   }
}
