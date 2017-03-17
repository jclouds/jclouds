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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Properties;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.Image;
import org.jclouds.azurecompute.arm.domain.ImageProperties;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.domain.Location;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

// We extend the BaseComputeServiceContextLiveTest to create nodes using the abstraction, which is much easier
@Test(groups = "live", singleThreaded = true, testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseComputeServiceContextLiveTest {

   private static final String imageName = "imageFromRest";

   private LoadingCache<String, ResourceGroup> resourceGroupMap;
   private Predicate<URI> resourceDeleted;
   private AzureComputeApi api;

   private String resourceGroupName;
   private String location;
   private ImageApi imageApi;
   private Image image;

   private String group;

   public ImageApiLiveTest() {
      provider = "azurecompute-arm";
      group = getClass().getSimpleName().toLowerCase();
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      AzureLiveTestUtils.defaultProperties(properties, getClass().getSimpleName().toLowerCase());
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");
      return properties;
   }

   @Override
   protected void initializeContext() {
      super.initializeContext();
      resourceDeleted = context.utils().injector().getInstance(Key.get(new TypeLiteral<Predicate<URI>>() {
      }, Names.named(TIMEOUT_RESOURCE_DELETED)));
      resourceGroupMap = context.utils().injector()
            .getInstance(Key.get(new TypeLiteral<LoadingCache<String, ResourceGroup>>() {
            }));
      api = view.unwrapApi(AzureComputeApi.class);
   }

   @Override
   @BeforeClass
   public void setupContext() {
      super.setupContext();
      // Use the resource name conventions used in the abstraction
      ResourceGroup resourceGroup = createResourceGroup();
      resourceGroupName = resourceGroup.name();
      location = resourceGroup.location();
      imageApi = api.getVirtualMachineImageApi(resourceGroupName);
   }

   @Override
   @AfterClass(alwaysRun = true)
   protected void tearDownContext() {
      try {
         view.getComputeService().destroyNodesMatching(inGroup(group));
      } finally {
         try {
            URI uri = api.getResourceGroupApi().delete(resourceGroupName);
            assertResourceDeleted(uri);
         } finally {
            super.tearDownContext();
         }
      }
   }

   @Test
   public void testDeleteImageDoesNotExist() {
      assertNull(imageApi.delete("notAnImage"));
   }

   @Test
   public void testCreateImage() throws RunNodesException {
      NodeMetadata node = getOnlyElement(view.getComputeService().createNodesInGroup(group, 1));
      IdReference vmIdRef = IdReference.create(node.getProviderId());
      view.getComputeService().suspendNode(node.getId());

      api.getVirtualMachineApi(resourceGroupName).generalize(node.getName());

      image = imageApi.createOrUpdate(imageName, location, ImageProperties.builder()
            .sourceVirtualMachine(vmIdRef).build());
      assertNotNull(image);
   }

   @Test(dependsOnMethods = "testCreateImage")
   public void testListImages() {
      // Check that the image we've just created exists
      assertTrue(any(imageApi.list(), new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            return image.name().equals(input.name());
         }
      }));
   }

   @Test(dependsOnMethods = "testCreateImage")
   public void testGetImage() {
      assertNotNull(imageApi.get(imageName));
   }

   @Test(dependsOnMethods = { "testCreateImage", "testListImages", "testGetImage" }, alwaysRun = true)
   public void deleteImage() {
      assertResourceDeleted(imageApi.delete(imageName));
   }

   private void assertResourceDeleted(final URI uri) {
      if (uri != null) {
         assertTrue(resourceDeleted.apply(uri),
               String.format("Resource %s was not deleted in the configured timeout", uri));
      }
   }

   private ResourceGroup createResourceGroup() {
      Location location = view.getComputeService().templateBuilder().build().getLocation();
      return resourceGroupMap.getUnchecked(location.getId());
   }

}
