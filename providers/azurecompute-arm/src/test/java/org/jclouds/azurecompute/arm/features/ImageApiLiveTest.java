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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.jclouds.azurecompute.arm.domain.Image;
import org.jclouds.azurecompute.arm.domain.ImageProperties;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", singleThreaded = true)
public class ImageApiLiveTest extends BaseAzureComputeApiLiveTest {

   public static final String JCLOUDS_VM_IMAGE_PREFIX = "jclouds-vm-image-";
   private String imageName;
   private VirtualMachine virtualMachine;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      imageName = JCLOUDS_VM_IMAGE_PREFIX + RAND;
      String vmName = "jclouds-vm-" + RAND;

      virtualMachine = api.getVirtualMachineApi(resourceGroupName).createOrUpdate(vmName, LOCATION, VirtualMachineProperties.builder().build(),
              Collections.<String, String> emptyMap(), null);
   }

   @Test
   public void deleteImageResourceDoesNotExist() {
      assertNull(api().delete(JCLOUDS_VM_IMAGE_PREFIX + UUID.randomUUID()));
   }

   @Test
   public void CreateVirtualMachineImageFromExistingVM() {
      String id = String.format("/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/myVM", getSubscriptionId(), resourceGroupName);
      ImageProperties properties = ImageProperties.builder()
              .sourceVirtualMachine(ImageProperties.SourceVirtualMachine.create(id))
              .build();
      Image image = api().createOrUpdate(imageName, LOCATION, properties);
      assertTrue(waitUntilAvailable(imageName), "creation operation did not complete in the configured timeout");
      assertTrue(id.equals(image.properties().sourceVirtualMachine().id()));
   }

   @Test(dependsOnMethods = "CreateVirtualMachineImageFromExistingVM")
   public void getImage() {
      Image image = api().get(imageName);
      assertNotNull(image);
   }

   @Test(dependsOnMethods = "CreateVirtualMachineImageFromExistingVM")
   public void listImages() {
      List<Image> images = api().list();
      assertTrue(images.size() > 0);
   }

   @Test(dependsOnMethods = {"listImages", "getImage"}, alwaysRun = true)
   public void deleteImage() {
      URI uri = api().delete(imageName);
      assertNotNull(uri);
   }

   private ImageApi api() {
      return api.getVirtualMachineImageApi(resourceGroupName);
   }
   
   private boolean waitUntilAvailable(final String name) {
      return resourceAvailable.apply(new Supplier<Provisionable>() {
         @Override public Provisionable get() {
            Image image = api().get(name);
            return image == null ? null : image.properties();
         }
      });
   }
}

