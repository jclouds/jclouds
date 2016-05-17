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

import com.google.common.base.Predicate;
import com.google.common.net.UrlEscapers;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.Deployment.ProvisioningState;
import org.jclouds.azurecompute.arm.domain.DeploymentBody;
import org.jclouds.azurecompute.arm.domain.DeploymentProperties;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
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
import org.jclouds.util.Predicates2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.List;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test(groups = "live", testName = "DeploymentApiLiveTest", singleThreaded = true)
public class DeploymentApiLiveTest extends BaseAzureComputeApiLiveTest {
   private int maxTestDuration = 190;

   private String resourceName;
   private String deploymentName;
   private String rawtemplate;
   private String rawparameters;
   private String rawbadParameters;
   private String properties;
   private String badProperties;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      resourceName = getResourceGroupName();
      Long now = System.currentTimeMillis();
      deploymentName = "jc" + now;

      rawtemplate = "{\"$schema\":\"https://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#\",\"contentVersion\":\"1.0.0.0\",\"parameters\":{\"newStorageAccountName\":{\"type\":\"string\",\"metadata\":{\"description\":\"Name of the Storage Account\"}},\"storageAccountType\":{\"type\":\"string\",\"defaultValue\":\"Standard_LRS\",\"allowedValues\":[\"Standard_LRS\",\"Standard_GRS\",\"Standard_ZRS\"],\"metadata\":{\"description\":\"Storage Account type\"}},\"location\":{\"type\":\"string\",\"allowedValues\":[\"East US\",\"West US\",\"West Europe\",\"East Asia\",\"Southeast Asia\"],\"metadata\":{\"description\":\"Location of storage account\"}}},\"resources\":[{\"type\":\"Microsoft.Storage/storageAccounts\",\"name\":\"[parameters('newStorageAccountName')]\",\"apiVersion\":\"2015-05-01-preview\",\"location\":\"[parameters('location')]\",\"properties\":{\"accountType\":\"[parameters('storageAccountType')]\"}}]}";
      rawparameters = "{\"newStorageAccountName\":{\"value\":\"" + resourceName + "\"},\"storageAccountType\":{\"value\":\"Standard_LRS\"},\"location\":{\"value\":\"West US\"}}";
      rawbadParameters = "{\"newStorageAccountName\":{\"value\":\"" + resourceName + "\"},\"storageAccountType\":{\"value\":\"Standard_LRS\"},\"location\":{\"value\":\"West\"}}";

      properties = getPutBody(rawtemplate, "Incremental", rawparameters);
      badProperties = getPutBody(rawtemplate, "Incremental", rawbadParameters);
   }

   private String getPutBody(String template, String mode, String parameters) {
      String body = "{ " +
              "\"properties\" : " +
              "  { " +
              "    \"template\" : " + template + ", " +
              "    \"mode\" : \"" + mode + "\", " +
              "    \"parameters\" : " + parameters + " " +
              "  } " +
              "}";
      return body;
   }

   private Template getTemplate(TemplateOptions options) {
      Location provider = (new LocationBuilder()).scope(LocationScope.PROVIDER).id("azurecompute-arm").description("azurecompute-arm").build();
      Location region = (new LocationBuilder()).scope(LocationScope.REGION).id("northeurope").description("North Europe").parent(provider).build();

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

   private DeploymentTemplateBuilder getDeploymentTemplateBuilderWithOptions(TemplateOptions options) {
      Template template = getTemplate(options);
      DeploymentTemplateBuilder templateBuilder = api.deploymentTemplateFactory().create(resourceName, deploymentName, template);
      return templateBuilder;
   }

   @Test(groups = "live")
   public void testValidate(){
      Deployment deploymentInvalid = null;
      try {
         deploymentInvalid = api().validate(deploymentName + "invalid", badProperties);
      } catch (Exception ex) {
         assertTrue(ex.getClass() == java.lang.IllegalArgumentException.class);
      }
      assertNull(deploymentInvalid);

      Deployment deploymentValid = null;
      try {
         deploymentValid = api().validate(deploymentName + "valid", properties);
      } catch (Exception ex) {
         assertTrue(ex.getClass() == java.lang.IllegalArgumentException.class);
      }
      assertNotNull(deploymentValid);
   }
   @Test(groups = "live")
   public void testCreate() {
      String rsakey = new String("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAmfk/QSF0pvnrpdz+Ah2KulGruKU+8FFBdlw938MpOysRdmp7uwpH6Z7+5VNGNdxFIAyc/W3UaZXF9hTsU8+78TlwkZpsr2mzU+ycu37XLAQ8Uv7hjsAN0DkKKPrZ9lgUUfZVKV/8E/JIAs03gIbL6zO3y7eYJQ5fNeZb+nji7tQT+YLpGq/FDegvraPKVMQbCSCZhsHyWhdPLyFlu9/30npZ0ahYOPI/KyZxFDtM/pHp88+ZAk9Icq5owaLRWcJQqrBGWqjbZnHtjdDqvHZ+C0wPhdJZPyfkHOrSYTwSQBXfX4JLRRCz3J1jf62MbQWT1o6Y4JEs1ZP1Skxu6zR96Q== mocktest");

      TemplateOptions options = new AzureTemplateOptions();
      options.authorizePublicKey(rsakey);
      DeploymentTemplateBuilder templateBuilder = getDeploymentTemplateBuilderWithOptions(options);
      DeploymentBody deploymentTemplateBody = templateBuilder.getDeploymentTemplate();

      DeploymentProperties properties = DeploymentProperties.create(deploymentTemplateBody);

      String deploymentTemplate = templateBuilder.getDeploymentTemplateJson(properties);
      deploymentTemplate = UrlEscapers.urlFormParameterEscaper().escape(deploymentTemplate);


      Deployment deploymentValid = api().validate(deploymentName, deploymentTemplate);
      assertNotNull(deploymentValid);

      Deployment deployment = api().create(deploymentName, deploymentTemplate);
      assertNotNull(deployment);

      //Poll until resource is ready to be used
      boolean jobDone = Predicates2.retry(new Predicate<String>() {
         @Override
         public boolean apply(String name) {
            Deployment dp = api().get(deploymentName);
            ProvisioningState state = ProvisioningState.fromValue(dp.properties().provisioningState());
            return state == ProvisioningState.SUCCEEDED;
         }
      }, 60 * maxTestDuration * 1000).apply(deploymentName);
      assertTrue(jobDone, "create operation did not complete in the configured timeout");

      Deployment dp = api().get(deploymentName);
      ProvisioningState state = ProvisioningState.fromValue(dp.properties().provisioningState());
      assertTrue(state == ProvisioningState.SUCCEEDED);
   }


   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testGetDeployment() {
      Deployment deployment = api().get(deploymentName);
      assertNotNull(deployment);
      ProvisioningState state = ProvisioningState.fromValue(deployment.properties().provisioningState());
      assertTrue(state == ProvisioningState.SUCCEEDED);
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testListDeployments() {
      List<Deployment> deployments = api().list();
      assertTrue(deployments.size() > 0);
      boolean deploymentFound = false;
      for (Deployment d : deployments) {

         if (d.name().equals(deploymentName)) {
            deploymentFound = true;
            break;
         }
      }
      assertTrue(deploymentFound);

   }

   @Test(groups = "live", dependsOnMethods = {"testGetDeployment", "testListDeployments"}, alwaysRun = true)
   public void testDelete() throws Exception {
      List<Deployment> deployments = api().list();
      for (Deployment d : deployments) {
         if (d.name().contains("jc")) {
            URI uri = api().delete(d.name());
            assertNotNull(uri);
            assertTrue(uri.toString().contains("api-version"));
            assertTrue(uri.toString().contains("operationresults"));

            boolean jobDone = Predicates2.retry(new Predicate<URI>() {
               @Override
               public boolean apply(URI uri) {
                  return ParseJobStatus.JobStatus.NO_CONTENT == api.getJobApi().jobStatus(uri);
               }
            }, 60 * maxTestDuration * 1000).apply(uri);
            assertTrue(jobDone, "delete operation did not complete in the configured timeout");
         }
      }
   }

   private DeploymentApi api() {
      return api.getDeploymentApi(resourceName);
   }
}
