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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.Deployment.ProvisioningState;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.util.Predicates2;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.net.UrlEscapers;

@Test(testName = "DeploymentApiLiveTest", singleThreaded = true)
public class DeploymentApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String deploymentName;
   private String subnetId;

   private String properties;
   private String badProperties;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      deploymentName = "jc" + System.currentTimeMillis();
      String virtualNetworkName = String.format("vn-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));
      String storageAccountName = String.format("st%s%s", System.getProperty("user.name"), RAND);

      String rawtemplate = "{\"$schema\":\"https://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#\",\"contentVersion\":\"1.0.0.0\",\"parameters\":{\"newStorageAccountName\":{\"type\":\"string\",\"metadata\":{\"description\":\"Name of the Storage Account\"}},\"storageAccountType\":{\"type\":\"string\",\"defaultValue\":\"Standard_LRS\",\"allowedValues\":[\"Standard_LRS\",\"Standard_GRS\",\"Standard_ZRS\"],\"metadata\":{\"description\":\"Storage Account type\"}},\"location\":{\"type\":\"string\",\"allowedValues\":[\"East US\",\"West US\",\"West Europe\",\"East Asia\",\"Southeast Asia\"],\"metadata\":{\"description\":\"Location of storage account\"}}},\"resources\":[{\"type\":\"Microsoft.Storage/storageAccounts\",\"name\":\"[parameters('newStorageAccountName')]\",\"apiVersion\":\"2015-05-01-preview\",\"location\":\"[parameters('location')]\",\"properties\":{\"accountType\":\"[parameters('storageAccountType')]\"}}]}";
      String rawparameters = "{\"newStorageAccountName\":{\"value\":\"" + storageAccountName + "\"},\"storageAccountType\":{\"value\":\"Standard_LRS\"},\"location\":{\"value\":\"West US\"}}";
      String rawbadParameters = "{\"newStorageAccountName\":{\"value\":\"" + storageAccountName + "\"},\"storageAccountType\":{\"value\":\"Standard_LRS\"},\"location\":{\"value\":\"West\"}}";

      properties = getPutBody(rawtemplate, "Incremental", rawparameters);
      badProperties = getPutBody(rawtemplate, "Incremental", rawbadParameters);

      //Subnets belong to a virtual network so that needs to be created first
      VirtualNetwork vn = createDefaultVirtualNetwork(resourceGroupName, virtualNetworkName, "10.3.0.0/16", LOCATION);
      assertNotNull(vn);

      //Subnet needs to be up & running before NIC can be created
      String subnetName = String.format("s-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));
      Subnet subnet = createDefaultSubnet(resourceGroupName, subnetName, virtualNetworkName, "10.3.0.0/23");
      assertNotNull(subnet);
      assertNotNull(subnet.id());
      subnetId = subnet.id();
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

   @Test
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
   @Test
   public void testCreate() {
      String rsakey = new String("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAmfk/QSF0pvnrpdz+Ah2KulGruKU+8FFBdlw938MpOysRdmp7uwpH6Z7+5VNGNdxFIAyc/W3UaZXF9hTsU8+78TlwkZpsr2mzU+ycu37XLAQ8Uv7hjsAN0DkKKPrZ9lgUUfZVKV/8E/JIAs03gIbL6zO3y7eYJQ5fNeZb+nji7tQT+YLpGq/FDegvraPKVMQbCSCZhsHyWhdPLyFlu9/30npZ0ahYOPI/KyZxFDtM/pHp88+ZAk9Icq5owaLRWcJQqrBGWqjbZnHtjdDqvHZ+C0wPhdJZPyfkHOrSYTwSQBXfX4JLRRCz3J1jf62MbQWT1o6Y4JEs1ZP1Skxu6zR96Q== mocktest");

      AzureTemplateOptions options = new AzureTemplateOptions();
      options.authorizePublicKey(rsakey);
      options.subnetId(subnetId);

      String deploymentTemplate = "{\n" +
              "  \"id\": \"/subscriptions/04f7ec88-8e28-41ed-8537-5e17766001f5/resourceGroups/jims216group/providers/Microsoft.Resources/deployments/jcdep1458344383064\",\n" +
              "  \"name\": \"jcdep1458344383064\",\n" +
              "  \"properties\": {\n" +
              "    \"parameters\": {\n" +
              "      \"newStorageAccountName\": {\n" +
              "        \"type\": \"String\",\n" +
              "        \"value\": \"jcres1458344383064\"\n" +
              "      },\n" +
              "      \"storageAccountType\": {\n" +
              "        \"type\": \"String\",\n" +
              "        \"value\": \"Standard_LRS\"\n" +
              "      },\n" +
              "      \"location\": {\n" +
              "        \"type\": \"String\",\n" +
              "        \"value\": \"West US\"\n" +
              "      }\n" +
              "    },\n" +
              "    \"mode\": \"Incremental\",\n" +
              "    \"provisioningState\": \"Accepted\",\n" +
              "    \"timestamp\": \"2016-03-18T23:39:47.3048037Z\",\n" +
              "    \"duration\": \"PT2.4433028S\",\n" +
              "    \"correlationId\": \"8dee9711-8632-4948-9fe6-368bb75e6438\",\n" +
              "    \"providers\": [{\n" +
              "      \"namespace\": \"Microsoft.Storage\",\n" +
              "      \"resourceTypes\": [{\n" +
              "        \"resourceType\": \"storageAccounts\",\n" +
              "        \"locations\": [\"westus\"]\n" +
              "      }]\n" +
              "    }],\n" +
              "    \"dependencies\": []\n" +
              "  }\n" +
              "}";
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
            if (state == ProvisioningState.FAILED) Assert.fail();
            return state == ProvisioningState.SUCCEEDED;
         }
      }, 60 * 20 * 1000).apply(deploymentName);
      assertTrue(jobDone, "create operation did not complete in the configured timeout");

      Deployment dp = api().get(deploymentName);
      ProvisioningState state = ProvisioningState.fromValue(dp.properties().provisioningState());
      assertTrue(state == ProvisioningState.SUCCEEDED);
   }


   @Test(dependsOnMethods = "testCreate")
   public void testGetDeployment() {
      Deployment deployment = api().get(deploymentName);
      assertNotNull(deployment);
      ProvisioningState state = ProvisioningState.fromValue(deployment.properties().provisioningState());
      assertTrue(state == ProvisioningState.SUCCEEDED);
   }

   @Test(dependsOnMethods = "testCreate")
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

   @Test(dependsOnMethods = {"testGetDeployment", "testListDeployments"})
   public void testDelete() throws Exception {
      List<Deployment> deployments = api().list();
      for (Deployment d : deployments) {
         if (d.name().contains("jc")) {
            URI uri = api().delete(d.name());
            assertResourceDeleted(uri);
         }
      }
   }

   private DeploymentApi api() {
      return api.getDeploymentApi(resourceGroupName);
   }
}
