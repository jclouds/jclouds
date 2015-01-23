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

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.util.List;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.Image;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;
import static org.jclouds.profitbricks.internal.BaseProfitBricksMockTest.mockWebServer;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 * Mock tests for the {@link org.jclouds.profitbricks.features.ImageApi} class
 */
@Test(groups = "unit", testName = "ImageApiMockTest")
public class ImageApiMockTest extends BaseProfitBricksMockTest {

   @Test
   public void testGetAllImages() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/image/images.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      ImageApi api = pbApi.imageApi();

      try {
         List<Image> images = api.getAllImages();
         assertRequestHasCommonProperties(server.takeRequest(), "<ws:getAllImages/>");
         assertNotNull(images);
         assertTrue(images.size() == 7);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetAllImagesReturning404() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      ImageApi api = pbApi.imageApi();

      try {
         List<Image> images = api.getAllImages();
         assertRequestHasCommonProperties(server.takeRequest());
         assertTrue(images.isEmpty());
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/image/image.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      ImageApi api = pbApi.imageApi();

      String id = "5ad99c9e-9166-11e4-9d74-52540066fee9";
      
      String content = "<ws:getImage><imageId>" + id + "</imageId></ws:getImage>";
      try {
         Image image = api.getImage(id);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(image);
         assertEquals(image.id(), id);
      } finally {
         pbApi.close();
         server.shutdown();
      }

   }

   @Test
   public void testGetNonExistingImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      ImageApi api = pbApi.imageApi();

      String id = "random-non-existing-id";
      try {
         Image image = api.getImage(id);
         assertRequestHasCommonProperties(server.takeRequest());
         assertNull(image);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }
}
