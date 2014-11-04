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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

public class ImageApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String DISK_NAME = "image-api-live-test-disk";
   public static final int TIME_WAIT = 300;
   public static final int sizeGb = 10;
   public static final String IMAGE_NAME = "image-api-live-test-image";

   private Image image;
   private URI diskURI;

   private ImageApi api() {
      return api.getImageApi("centos-cloud");
   }

   private ImageApi imageApi(){
      return api.getImageApi(userProject.get());
   }

   private DiskApi diskApi() {
      return api.getDiskApi(userProject.get());
   }

   @Test(groups = "live")
   public void testListImage() {

      Iterator<ListPage<Image>> images = api().list(new ListOptions.Builder().maxResults(1));

      List<Image> imageAsList = images.next();

      assertEquals(imageAsList.size(), 1);

      this.image = imageAsList.get(0);
   }


   @Test(groups = "live", dependsOnMethods = "testListImage")
   public void testGetImage() {
      Image image = api().get(this.image.name());
      assertNotNull(image);
      assertImageEquals(image, this.image);
   }

   private void assertImageEquals(Image result, Image expected) {
      assertEquals(result.name(), expected.name());
   }

   @Test(groups = "live")
   public void testInsertDisk() {
      assertZoneOperationDoneSuccessfully(diskApi().createInZone(DISK_NAME, sizeGb, DEFAULT_ZONE_NAME), TIME_WAIT);
      Disk disk = diskApi().getInZone(DEFAULT_ZONE_NAME, DISK_NAME);
      diskURI = disk.selfLink();
   }

   @Test(groups = "live", dependsOnMethods = "testInsertDisk")
   public void testCreateImageFromPD(){
      assertGlobalOperationDoneSucessfully(imageApi().createImageFromPD(IMAGE_NAME, diskURI.toString()), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateImageFromPD")
   public void testGetCreatedImage(){
      Image image = imageApi().get(IMAGE_NAME);
      assertImageEquals(image);
   }

   @Test(groups = "live", dependsOnMethods = "testGetCreatedImage")
   public void testCleanup(){
      assertGlobalOperationDoneSucessfully(imageApi().delete(IMAGE_NAME), TIME_WAIT);
      assertZoneOperationDoneSuccessfully(diskApi().deleteInZone(DEFAULT_ZONE_NAME, DISK_NAME), TIME_WAIT);
   }

   private void assertImageEquals(Image result) {
      assertEquals(result.name(), IMAGE_NAME);
      assertEquals(result.sourceType(), "RAW");
      assertEquals(result.selfLink(), getImageUrl(userProject.get(), IMAGE_NAME) );
   }
}

