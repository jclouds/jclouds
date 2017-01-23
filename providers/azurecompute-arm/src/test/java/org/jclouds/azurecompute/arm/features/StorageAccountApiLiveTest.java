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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.StorageServiceKeys;
import org.jclouds.azurecompute.arm.domain.StorageServiceUpdateParams;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.util.Predicates2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

@Test(groups = "live", testName = "StorageAccountApiLiveTest")
public class StorageAccountApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String NAME = String.format("%3.24s",
           RAND + StorageAccountApiLiveTest.class.getSimpleName().toLowerCase());

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      List<StorageService> storages = api().list();
      assertTrue(storages.size() > 0);
      for (StorageService storage : storages) {
         check(storage);
      }
   }

   @Test()
   public void testIsAvailable() {
      assertTrue(api().isAvailable(NAME).nameAvailable().equals("true"));
   }

   @Test(dependsOnMethods = "testIsAvailable")
   public void testCreate() {
      URI uri = api().create(NAME, LOCATION, ImmutableMap.of("property_name",
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
      final StorageService service = api().get(NAME);
      assertNotNull(service);
      assertEquals(service.location(), LOCATION);
      assertNotNull(service.storageServiceProperties().creationTime());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      final StorageService service = api().get(NAME);
      assertNotNull(service);
      assertEquals(service.name(), NAME);
      assertEquals(service.storageServiceProperties().primaryLocation(), LOCATION);
      assertEquals(service.storageServiceProperties().accountType(), StorageService.AccountType.Standard_LRS);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGetKeys() {
      final StorageServiceKeys keys = api().getKeys(NAME);
      assertNotNull(keys);
      assertNotNull(keys.key1());
      assertNotNull(keys.key2());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testRegenerateKeys() {
      StorageServiceKeys keys = api().regenerateKeys(NAME, "key1");
      assertFalse(keys.key1().isEmpty());
      assertFalse(keys.key2().isEmpty());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdateTags() {
      StorageServiceUpdateParams.StorageServiceUpdateProperties props =
              StorageServiceUpdateParams.StorageServiceUpdateProperties.create(null);
      final StorageServiceUpdateParams params = api().update(NAME,
              ImmutableMap.of("another_property_name", "another_property_value"), props);
      assertTrue(params.tags().containsKey("another_property_name"));
      assertNull(params.storageServiceProperties().accountType());
   }

   @Test(dependsOnMethods = {"testCreate", "testGet"})
   public void testUpdateAccountType() {
      StorageServiceUpdateParams.StorageServiceUpdateProperties props =
              StorageServiceUpdateParams.StorageServiceUpdateProperties.create(StorageService.AccountType.Standard_GRS);
      final StorageServiceUpdateParams params = api().update(NAME,
              null, props);
      assertNull(params.tags());
      assertEquals(params.storageServiceProperties().accountType(), StorageService.AccountType.Standard_GRS);
   }

   private void check(final StorageService storage) {
      assertNotNull(storage.id());
      assertNotNull(storage.name());
      assertNotNull(storage.storageServiceProperties());
      assertNotNull(storage.storageServiceProperties().accountType());
      assertFalse(storage.storageServiceProperties().primaryEndpoints().isEmpty());
      assertNotNull(storage.storageServiceProperties().creationTime());
   }

   private StorageAccountApi api() {
      return api.getStorageAccountApi(resourceGroupName);
   }
}
