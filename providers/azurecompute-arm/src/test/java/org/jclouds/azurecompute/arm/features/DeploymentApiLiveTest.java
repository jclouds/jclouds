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

import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.Deployment.ProvisioningState;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.util.Predicates2;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.net.UrlEscapers;

@Test(groups = "live", testName = "DeploymentApiLiveTest", singleThreaded = true)
public class DeploymentApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String deploymentName;
   private String properties;
   private String badProperties;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      deploymentName = "jc" + System.currentTimeMillis();
      String storageAccountName = String.format("st%s%s", System.getProperty("user.name"), RAND);

      String rawtemplate = "{\"$schema\":\"https://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#\",\"contentVersion\":\"1.0.0.0\",\"parameters\":{\"newStorageAccountName\":{\"type\":\"string\",\"metadata\":{\"description\":\"Name of the Storage Account\"}},\"storageAccountType\":{\"type\":\"string\",\"defaultValue\":\"Standard_LRS\",\"allowedValues\":[\"Standard_LRS\",\"Standard_GRS\",\"Standard_ZRS\"],\"metadata\":{\"description\":\"Storage Account type\"}},\"location\":{\"type\":\"string\",\"allowedValues\":[\"East US\",\"West US\",\"West Europe\",\"East Asia\",\"Southeast Asia\"],\"metadata\":{\"description\":\"Location of storage account\"}}},\"resources\":[{\"type\":\"Microsoft.Storage/storageAccounts\",\"name\":\"[parameters('newStorageAccountName')]\",\"apiVersion\":\"2015-05-01-preview\",\"location\":\"[parameters('location')]\",\"properties\":{\"accountType\":\"[parameters('storageAccountType')]\"}}]}";
      String rawparameters = "{\"newStorageAccountName\":{\"value\":\"" + storageAccountName + "\"},\"storageAccountType\":{\"value\":\"Standard_LRS\"},\"location\":{\"value\":\"West US\"}}";
      String rawbadParameters = "{\"newStorageAccountName\":{\"value\":\"" + storageAccountName + "\"},\"storageAccountType\":{\"value\":\"Standard_LRS\"},\"location\":{\"value\":\"West\"}}";

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
   
   @Test(dependsOnMethods = "testValidate")
   public void testCreate() {
      String deploymentTemplate = UrlEscapers.urlFormParameterEscaper().escape(properties);

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
