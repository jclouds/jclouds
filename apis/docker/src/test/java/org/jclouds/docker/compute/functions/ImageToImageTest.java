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
package org.jclouds.docker.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.Image;
import org.jclouds.docker.domain.Config;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Unit tests for the {@link org.jclouds.docker.compute.functions.ImageToImage} class.
 */
@Test(groups = "unit", testName = "ImageToImageTest")
public class ImageToImageTest {

   private ImageToImage function;

   private org.jclouds.docker.domain.Image image;

   @BeforeMethod
   public void setup() {
      image = org.jclouds.docker.domain.Image.create(
              "id", // id
              "author",
              "comment",
              Config.builder()
                      .image("imageId")
                      .build(),
              Config.builder()
                      .image("imageId")
                      .build(),
              "parent", // parent
              new Date(), // created
              "containerId", // container
              "1.3.1", // dockerVersion
              "x86_64", // architecture
              "os", // os
              0l, // size
              0l, // virtualSize
              ImmutableList.of("repoTag1:version") // repoTags
      );
      function = new ImageToImage();
   }

   public void testImageToImage() {
      org.jclouds.docker.domain.Image mockImage = mockImage();

      Image image = function.apply(mockImage);

      verify(mockImage);

      assertEquals(mockImage.id(), image.getId().toString());
   }

   private org.jclouds.docker.domain.Image mockImage() {
      org.jclouds.docker.domain.Image mockImage = EasyMock.createMock(org.jclouds.docker.domain.Image.class);

      expect(mockImage.id()).andReturn(image.id()).anyTimes();
      expect(mockImage.repoTags()).andReturn(image.repoTags()).anyTimes();
      expect(mockImage.architecture()).andReturn(image.architecture()).anyTimes();
      replay(mockImage);

      return mockImage;
   }
}
