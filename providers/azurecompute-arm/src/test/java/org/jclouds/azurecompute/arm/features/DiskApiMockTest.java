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

import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecompute.arm.domain.CreationData;
import org.jclouds.azurecompute.arm.domain.Disk;
import org.jclouds.azurecompute.arm.domain.DiskProperties;
import org.jclouds.azurecompute.arm.domain.DiskSku;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static org.jclouds.azurecompute.arm.domain.StorageAccountType.PREMIUM_LRS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


@Test(groups = "unit", testName = "DiskApiMockTest", singleThreaded = true)
public class DiskApiMockTest extends BaseAzureComputeApiMockTest {

   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String diskName = "myDisk";
   private final String apiVersion = "api-version=2017-03-30";

   public void createDisk() throws InterruptedException {

      server.enqueue(jsonResponse("/creatediskresponse.json").setResponseCode(200));

      final DiskApi diskApi = api.getDiskApi(resourcegroup);

      DiskProperties properties = DiskProperties.builder().diskSizeGB(2).creationData(CreationData.create(CreationData.CreateOptions.EMPTY)).build();

      Disk dataDisk = diskApi.createOrUpdate(diskName, "westus", properties);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/disks/%s?%s", subscriptionid, resourcegroup, diskName, apiVersion);
      String json = "{\"location\":\"westus\",\"properties\":{\"diskSizeGB\":2,\"creationData\":{\"createOption\":\"Empty\"}}}";
      assertSent(server, "PUT", path, json);

      assertEquals(dataDisk.properties().provisioningState(), "Updating");
      assertTrue(dataDisk.properties().diskSizeGB() == 2);
   }

   public void createDiskWithTags() throws InterruptedException {

      server.enqueue(jsonResponse("/creatediskwithtagsresponse.json").setResponseCode(200));

      final DiskApi diskApi = api.getDiskApi(resourcegroup);

      DiskProperties properties = DiskProperties.builder().diskSizeGB(2).creationData(CreationData.create(CreationData.CreateOptions.EMPTY)).build();

      Disk dataDisk = diskApi.createOrUpdate(diskName, "westus", ImmutableMap.of("exampleTag", "jclouds-test-tag"), properties);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/disks/%s?%s", subscriptionid, resourcegroup, diskName, apiVersion);
      String json = "{\"location\":\"westus\",\"tags\":{\"exampleTag\":\"jclouds-test-tag\"},\"properties\":{\"diskSizeGB\":2,\"creationData\":{\"createOption\":\"Empty\"}}}";
      assertSent(server, "PUT", path, json);

      assertEquals(dataDisk.properties().provisioningState(), "Updating");
      assertTrue(dataDisk.properties().diskSizeGB() == 2);
      assertTrue(dataDisk.tags().containsValue("jclouds-test-tag"));
   }

   public void createDiskWithTagsAndSku() throws InterruptedException {

      server.enqueue(jsonResponse("/creatediskwithtagsandskuresponse.json").setResponseCode(200));

      final DiskApi diskApi = api.getDiskApi(resourcegroup);

      DiskProperties properties = DiskProperties.builder().diskSizeGB(2).creationData(CreationData.create(CreationData.CreateOptions.EMPTY)).build();

      DiskSku sku = DiskSku.builder().name(PREMIUM_LRS).build();

      Disk dataDisk = diskApi.createOrUpdate(diskName, "westus", ImmutableMap.of("exampleTag", "jclouds-test-tag"), properties, sku);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/disks/%s?%s", subscriptionid, resourcegroup, diskName, apiVersion);
      String json = "{\"location\":\"westus\",\"tags\":{\"exampleTag\":\"jclouds-test-tag\"},\"properties\":{\"diskSizeGB\":2,\"creationData\":{\"createOption\":\"Empty\"}},\"sku\":{\"name\":\"Premium_LRS\"}}";
      assertSent(server, "PUT", path, json);

      assertEquals(dataDisk.properties().provisioningState(), "Updating");
      assertTrue(dataDisk.properties().diskSizeGB() == 2);
      assertTrue(dataDisk.tags().containsValue("jclouds-test-tag"));
   }

   public void getDisk() throws InterruptedException {

      server.enqueue(jsonResponse("/getdisk.json").setResponseCode(200));

      final DiskApi diskApi = api.getDiskApi(resourcegroup);

      Disk dataDisk = diskApi.get(diskName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/disks/%s?%s", subscriptionid, resourcegroup, diskName, apiVersion);
      assertSent(server, "GET", path);

      assertEquals(dataDisk.name(), diskName);
      assertTrue(dataDisk.properties().diskSizeGB() == 2);
   }

   public void getDiskReturns404() throws InterruptedException {
      server.enqueue(response404());

      final DiskApi diskApi = api.getDiskApi(resourcegroup);

      Disk dataDisk = diskApi.get(diskName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/disks/%s?%s", subscriptionid, resourcegroup, diskName, apiVersion);
      assertSent(server, "GET", path);

      assertNull(dataDisk);
   }

   public void listDisks() throws InterruptedException {

      server.enqueue(jsonResponse("/listdisks.json").setResponseCode(200));

      final DiskApi diskApi = api.getDiskApi(resourcegroup);

      List<Disk> dataDisks = diskApi.list();

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/disks?%s", subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(dataDisks.size() > 0);
   }

   public void listDisksReturns404() throws InterruptedException {
      server.enqueue(response404());

      final DiskApi diskApi = api.getDiskApi(resourcegroup);

      List<Disk> dataDisks = diskApi.list();

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/disks?%s", subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(isEmpty(dataDisks));
   }

   public void deleteDisk() throws InterruptedException {

      server.enqueue(response202WithHeader());

      final DiskApi diskApi = api.getDiskApi(resourcegroup);

      URI uri = diskApi.delete(diskName);
      Assert.assertNotNull(uri);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/disks/%s?%s", subscriptionid, resourcegroup, diskName, apiVersion);
      assertSent(server, "DELETE", path);
   }

   public void deleteDiskResourceDoesNotExist() throws InterruptedException {

      server.enqueue(response204());

      final DiskApi diskApi = api.getDiskApi(resourcegroup);

      URI uri = diskApi.delete(diskName);
      assertNull(uri);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/disks/%s?%s", subscriptionid, resourcegroup, diskName, apiVersion);
      assertSent(server, "DELETE", path);
   }
}
