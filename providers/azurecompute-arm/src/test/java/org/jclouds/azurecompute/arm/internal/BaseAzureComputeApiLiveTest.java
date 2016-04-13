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
package org.jclouds.azurecompute.arm.internal;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;

import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.features.StorageAccountApi;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
import org.jclouds.util.Predicates2;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseAzureComputeApiLiveTest extends AbstractAzureComputeApiLiveTest {
   public static final String LOCATION = "westeurope";
   private String resourceGroupName = null;

   protected StorageService storageService;


   private String storageServiceName = null;

   protected String getStorageServiceName() {
      if (storageServiceName == null) {
         storageServiceName = String.format("%3.24s",
                 System.getProperty("user.name") + RAND + this.getClass().getSimpleName()).toLowerCase();
      }
      return storageServiceName;
   }

   protected String getEndpoint() {
      String endpoint = null;
      if (System.getProperty("test.azurecompute-arm.endpoint") != null) {
         endpoint = System.getProperty("test.azurecompute-arm.endpoint");
      }
      assertNotNull(endpoint);
      return endpoint;
   }

   protected String getResourceGroupName() {
      if (resourceGroupName == null) {
         resourceGroupName = String.format("%3.24s",
                 System.getProperty("user.name") + RAND + "groupjclouds");
         createResourceGroup(resourceGroupName);
      }
      return resourceGroupName;
   }

   private void createResourceGroup(String name) {
      ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder().build();

      final ResourceGroup resourceGroup = api.getResourceGroupApi().create(
              name, LOCATION, tags);
   }

   private void deleteResourceGroup(String name) {
      api.getResourceGroupApi().delete(name);
   }


   @BeforeClass
   @Override
   public void setup() {
      super.setup();

      storageService = getOrCreateStorageService(getStorageServiceName());
   }

   @AfterClass(alwaysRun = true)
   @Override
   protected void tearDown() {
      super.tearDown();
      Boolean status = api.getStorageAccountApi(getResourceGroupName()).delete(getStorageServiceName());
      assertTrue(status.booleanValue());
      deleteResourceGroup(getResourceGroupName());
   }

   protected StorageService getOrCreateStorageService(String storageServiceName) {
      StorageAccountApi storageApi = api.getStorageAccountApi(getResourceGroupName());
      StorageService ss = storageApi.get(storageServiceName);
      if (ss != null) {
         return ss;
      }
      URI uri = storageApi.create(storageServiceName, LOCATION, ImmutableMap.of("property_name",
              "property_value"), ImmutableMap.of("accountType", StorageService.AccountType.Standard_LRS.toString()));
      if (uri != null){
         assertTrue(uri.toString().contains("api-version"));

         boolean jobDone = Predicates2.retry(new Predicate<URI>() {
            @Override public boolean apply(URI uri) {
               return ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri);
            }
         }, 60 * 1 * 1000 /* 1 minute timeout */).apply(uri);
         assertTrue(jobDone, "create operation did not complete in the configured timeout");
      }
      ss = storageApi.get(storageServiceName);
      Assert.assertEquals(ss.location(), LOCATION);

      Logger.getAnonymousLogger().log(Level.INFO, "created storageService: {0}", ss);
      return ss;
   }
}
