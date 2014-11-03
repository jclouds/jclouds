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
package org.jclouds.googlecomputeengine.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.net.URI;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.googlecomputeengine.domain.Image;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "GoogleComputeEngineImageToImageTest")
public class GoogleComputeEngineImageToImageTest {
   public void testArbitratyImageName() {
      GoogleComputeEngineImageToImage imageToImage = new GoogleComputeEngineImageToImage();
      Image image = image("arbitratyname");
      org.jclouds.compute.domain.Image transformed = imageToImage.apply(image);
      assertEquals(transformed.getName(), image.name());
      assertEquals(transformed.getId(), image.name());
      assertEquals(transformed.getProviderId(), image.id());
      assertSame(transformed.getOperatingSystem().getFamily(), OsFamily.LINUX);
   }

   public void testWellFormedImageName() {
      GoogleComputeEngineImageToImage imageToImage = new GoogleComputeEngineImageToImage();
      Image image = image("ubuntu-12-04-v123123");
      org.jclouds.compute.domain.Image transformed = imageToImage.apply(image);
      assertEquals(transformed.getName(), image.name());
      assertEquals(transformed.getId(), image.name());
      assertEquals(transformed.getProviderId(), image.id());
      assertSame(transformed.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(transformed.getOperatingSystem().getVersion(), "12.04");
   }

   private static Image image(String name) {
      return Image.create( //
            "1234", // id
            URI.create("http://test.com/1234"), // selfLink
            name, // name
            "", // description
            "RAW", // sourceType
            Image.RawDisk.create(URI.create("foo"), "TAR", null), // rawDisk
            null // deprecated
      );
   }
}
