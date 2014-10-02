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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import java.io.InputStream;

import org.jclouds.docker.compute.BaseDockerApiLiveTest;
import org.jclouds.docker.options.CreateImageOptions;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "RemoteApiLiveTest", singleThreaded = true)
public class ImageApiLiveTest extends BaseDockerApiLiveTest {

   private static final String DEFAULT_IMAGE = "busybox";
   private static final String DEFAULT_TAG = "ubuntu-14.04";

   @Test
   public void testCreateImage() {
      InputStream createImageStream = api().createImage(CreateImageOptions.Builder.fromImage(DEFAULT_IMAGE).tag(DEFAULT_TAG));
      consumeStream(createImageStream);
   }

   @Test(dependsOnMethods = "testCreateImage")
   public void testInspectImage() {
      assertNotNull(api.getImageApi().inspectImage(String.format("%s:%s", DEFAULT_IMAGE, DEFAULT_TAG)));
   }

   @Test(dependsOnMethods = "testInspectImage")
   public void testListImages() {
      assertNotNull(api().listImages());
   }

   @Test(dependsOnMethods = "testListImages")
   public void testDeleteImage() {
      consumeStream(api().deleteImage(String.format("%s:%s", DEFAULT_IMAGE, DEFAULT_TAG)));
      assertNull(api().inspectImage(String.format("%s:%s", DEFAULT_IMAGE, DEFAULT_TAG)));
   }

   private ImageApi api() {
      return api.getImageApi();
   }

}
