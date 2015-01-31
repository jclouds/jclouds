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

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Deprecated.State;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.DeprecateOptions;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.testng.annotations.Test;

public class ImageApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String DISK_NAME = "image-api-live-test-disk";
   public static final int sizeGb = 10;
   public static final String IMAGE_NAME = "image-api-live-test-image";

   private Image image;
   private URI diskURI;

   private ImageApi api(){
      return api.images();
   }

   private DiskApi diskApi() {
      return api.disksInZone(DEFAULT_ZONE_NAME);
   }

   @Test(groups = "live")
   public void testListImage() {
      Iterator<ListPage<Image>> images = api().listInProject("centos-cloud", maxResults(1));

      List<Image> imageAsList = images.next();

      assertEquals(imageAsList.size(), 1);

      this.image = imageAsList.get(0);
   }

   @Test(groups = "live", dependsOnMethods = "testListImage")
   public void testGetImage() {
      assertEquals(image, api().get(image.selfLink()));
   }

   @Test(groups = "live")
   public void testInsertDisk() {
      assertOperationDoneSuccessfully(diskApi().create(DISK_NAME,
            new DiskCreationOptions.Builder().sizeGb(sizeGb).build()));
      Disk disk = diskApi().get(DISK_NAME);
      diskURI = disk.selfLink();
   }

   @Test(groups = "live", dependsOnMethods = "testInsertDisk")
   public void testCreateImageFromPD(){
      assertOperationDoneSuccessfully(api().createFromDisk(IMAGE_NAME, diskURI.toString()));
   }

   @Test(groups = "live", dependsOnMethods = "testCreateImageFromPD")
   public void testGetCreatedImage(){
      Image image = api().get(IMAGE_NAME);
      assertImageEquals(image);
   }

   @Test(groups = "live", dependsOnMethods = "testGetCreatedImage")
   public void testDeprecateImage(){
      Image image = api().get(IMAGE_NAME);
      assertNull(image.deprecated());
      String deprecated = "2015-07-16T22:16:13.468Z";
      String obsolete = "2016-10-16T22:16:13.468Z";
      String deleted = "2017-01-16T22:16:13.468Z";

      URI replacement = URI.create("https://www.googleapis.com/compute/v1/projects/centos-cloud/global/images/centos-6-2-v20120326test");

      DeprecateOptions deprecateOptions = new DeprecateOptions.Builder().state(State.DEPRECATED)
            .replacement(replacement)
            .deprecated(new SimpleDateFormatDateService().iso8601DateParse(deprecated))
            .obsolete(new SimpleDateFormatDateService().iso8601DateParse(obsolete))
            .deleted(new SimpleDateFormatDateService().iso8601DateParse(deleted))
            .build();

      assertOperationDoneSuccessfully(api().deprecate(IMAGE_NAME, deprecateOptions));

      image = api().get(IMAGE_NAME);
      assertEquals(image.deprecated().state(), State.DEPRECATED);
      assertEquals(image.deprecated().replacement(), replacement);
      assertEquals(image.deprecated().deprecated(), deprecated);
      assertEquals(image.deprecated().obsolete(), obsolete);
      assertEquals(image.deprecated().deleted(), deleted);
   }

   @Test(groups = "live", dependsOnMethods = "testDeprecateImage", alwaysRun = true)
   public void testCleanup(){
      assertOperationDoneSuccessfully(api().delete(IMAGE_NAME));
      assertOperationDoneSuccessfully(diskApi().delete(DISK_NAME));
   }

   private void assertImageEquals(Image result) {
      assertEquals(result.name(), IMAGE_NAME);
      assertEquals(result.sourceType(), "RAW");
      assertEquals(result.selfLink(), getImageUrl(IMAGE_NAME) );
   }
}

