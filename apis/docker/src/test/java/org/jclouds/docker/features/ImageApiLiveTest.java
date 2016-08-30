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
package org.jclouds.docker.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.InputStream;
import java.util.List;

import org.jclouds.docker.compute.BaseDockerApiLiveTest;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.domain.ImageSummary;
import org.jclouds.docker.options.CreateImageOptions;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "ImageApiLiveTest", singleThreaded = true)
public class ImageApiLiveTest extends BaseDockerApiLiveTest {

   private Image image;

   @Test
   public void testCreateImage() {
      InputStream createImageStream = api().createImage(CreateImageOptions.Builder.fromImage(DEFAULT_IMAGE).tag(DEFAULT_TAG));
      consumeStream(createImageStream);
   }

   @Test(dependsOnMethods = "testCreateImage")
   public void testInspectImage() {
      image = api.getImageApi().inspectImage(String.format("%s:%s", DEFAULT_IMAGE, DEFAULT_TAG));
      assertNotNull(image);
   }

   @Test(dependsOnMethods = "testInspectImage")
   public void testTagImage() {
      api.getImageApi().tagImage(image.id(), "jclouds", "testTag", true);
      Image taggedImage = api.getImageApi().inspectImage("jclouds:testTag");
      assertEquals(taggedImage.id(), image.id(), "Newly added image tag should point to the same image ID.");
   }

   @Test(dependsOnMethods = "testTagImage")
   public void testListImages() {
      List<ImageSummary> listImages = api().listImages();
      assertNotNull(listImages);

      Iterables.find(listImages, new Predicate<ImageSummary>() {
         @Override
         public boolean apply(ImageSummary input) {
            return input.repoTags().contains("jclouds:testTag");
         }
      });
   }

   @Test(dependsOnMethods = "testListImages", alwaysRun = true)
   public void testDeleteImage() {
      consumeStream(api().deleteImage(String.format("%s:%s", DEFAULT_IMAGE, DEFAULT_TAG)));
      assertNull(api().inspectImage(String.format("%s:%s", DEFAULT_IMAGE, DEFAULT_TAG)));

      assertNotNull(api().inspectImage(image.id()), "Image should should still exist after removing original tag. There is a newly added tag referencing it.");
      consumeStream(api().deleteImage("jclouds:testTag"));
      assertNull(api().inspectImage("jclouds:testTag"));
   }

   private ImageApi api() {
      return api.getImageApi();
   }

}
