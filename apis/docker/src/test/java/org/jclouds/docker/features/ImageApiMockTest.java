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

import org.jclouds.docker.DockerApi;
import org.jclouds.docker.config.DockerParserModule;
import org.jclouds.docker.internal.BaseDockerMockTest;
import org.jclouds.docker.options.CreateImageOptions;
import org.jclouds.docker.parse.ImageParseTest;
import org.jclouds.docker.parse.ImagesParseTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.docker.features.ImageApi} class.
 */
@Test(groups = "unit", testName = "ImageApiMockTest")
public class ImageApiMockTest extends BaseDockerMockTest {

   public void testCreateImage() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(200));
      ImageApi api = api(DockerApi.class, server.getUrl("/").toString()).getImageApi();
      try {
         api.createImage(CreateImageOptions.Builder.fromImage("base"));
         assertSent(server, "POST", "/images/create?fromImage=base");
      } finally {
         server.shutdown();
      }
   }

   public void testGetImage() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/image.json")));
      ImageApi api = api(DockerApi.class, server.getUrl("/").toString(), new DockerParserModule()).getImageApi();
      try {
         String imageId = "cbba6639a342646deed70d7ea6162fa2a0acea9300f911f4e014555fe37d3456";
         assertEquals(api.inspectImage(imageId), new ImageParseTest().expected());
         assertSent(server, "GET", "/images/" + imageId + "/json");
      } finally {
         server.shutdown();
      }
   }

   public void testListImages() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/images.json")));
      ImageApi api = api(DockerApi.class, server.getUrl("/").toString()).getImageApi();
      try {
         assertEquals(api.listImages(), new ImagesParseTest().expected());
         assertSent(server, "GET", "/images/json");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteImage() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(204));
      ImageApi api = api(DockerApi.class, server.getUrl("/").toString()).getImageApi();
      try {
         api.deleteImage("1");
         assertSent(server, "DELETE", "/images/1");

      } finally {
         server.shutdown();
      }
   }

}
