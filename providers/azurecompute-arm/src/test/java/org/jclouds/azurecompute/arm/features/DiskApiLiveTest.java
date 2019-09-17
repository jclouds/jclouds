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

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.jclouds.azurecompute.arm.domain.CreationData;
import org.jclouds.azurecompute.arm.domain.Disk;
import org.jclouds.azurecompute.arm.domain.DiskProperties;
import org.jclouds.azurecompute.arm.domain.DiskSku;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import static org.jclouds.azurecompute.arm.domain.StorageAccountType.PREMIUM_LRS;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "DiskApiLiveTest", singleThreaded = true)
public class DiskApiLiveTest extends BaseAzureComputeApiLiveTest {

   public static final String JCLOUDS_DISK_PREFIX = "jclouds-";
   private String diskName;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      diskName = JCLOUDS_DISK_PREFIX + RAND;
   }

   @Test
   public void deleteDiskResourceDoesNotExist() {
      assertNull(api().delete(JCLOUDS_DISK_PREFIX + UUID.randomUUID()));
   }

   @Test
   public void createDisk() {
      DiskProperties properties = DiskProperties.builder().creationData(CreationData.create(CreationData.CreateOptions.EMPTY)).diskSizeGB(2).build();
      DiskSku sku = DiskSku.builder().name(PREMIUM_LRS).build();
      Disk dataDisk = api().createOrUpdate(diskName, LOCATION, ImmutableMap.of("exampleTag", "jclouds-test-tag"), properties, sku);
      assertTrue(waitUntilAvailable(diskName), "creation operation did not complete in the configured timeout");
      assertTrue(dataDisk.properties().diskSizeGB() == 2);
      assertTrue(dataDisk.tags().containsValue("jclouds-test-tag"));
   }

   @Test(dependsOnMethods = "createDisk")
   public void getDisk() {
      Disk dataDisk = api().get(diskName);
      assertNotNull(dataDisk.name());
      assertTrue(dataDisk.properties().diskSizeGB() == 2);
   }

   @Test(dependsOnMethods = "createDisk")
   public void listDisks() {
      List<Disk> dataDisks = api().list();
      assertTrue(dataDisks.size() > 0);
      final Disk dataDisk = api().get(diskName);

      assertTrue(Iterables.any(dataDisks, new Predicate<Disk>() {
         @Override
         public boolean apply(Disk input) {
            return dataDisk.equals(input);
         }
      }));
   }

   @Test(dependsOnMethods = {"listDisks", "getDisk"}, alwaysRun = true)
   public void deleteDisk() {
      URI uri = api().delete(diskName);
      assertNotNull(uri);
   }

   private DiskApi api() {
      return api.getDiskApi(resourceGroupName);
   }

   private boolean waitUntilAvailable(final String name) {
      return resourceAvailable.apply(new Supplier<Provisionable>() {
         @Override public Provisionable get() {
            Disk disk = api().get(name);
            return disk == null ? null : disk.properties();
         }
      });
   }
   
}

