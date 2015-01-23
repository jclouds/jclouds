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
package org.jclouds.profitbricks.features;

import com.google.common.collect.Iterables;
import java.util.List;
import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.Image;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ImageApiLiveTest", singleThreaded = true)
public class ImageApiLiveTest extends BaseProfitBricksLiveTest {

   private String imageId;

   @Test
   public void testGetAllImages() {
      List<Image> images = api.imageApi().getAllImages();

      assertNotNull(images);
      assertFalse(images.isEmpty(), "No images found.");
      imageId = Iterables.getFirst(images, null).id();
   }

   @Test(dependsOnMethods = "testGetAllImages")
   public void testGetImage() {
      Image image = api.imageApi().getImage(imageId);

      assertNotNull(image);
      assertEquals(image.id(), imageId);
   }

   @Test
   public void testGetNonExistingImage() {
      String id = "random-non-existing-id";
      Image image = api.imageApi().getImage(id);

      assertNull(image, "Should've just returned null");
   }
}
