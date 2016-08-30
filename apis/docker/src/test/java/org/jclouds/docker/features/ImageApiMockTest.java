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
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.docker.DockerApi;
import org.jclouds.docker.config.DockerParserModule;
import org.jclouds.docker.domain.ImageHistory;
import org.jclouds.docker.internal.BaseDockerMockTest;
import org.jclouds.docker.options.CreateImageOptions;
import org.jclouds.docker.parse.HistoryParseTest;
import org.jclouds.docker.parse.ImageParseTest;
import org.jclouds.docker.parse.ImagesParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
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

   public void testTagImage() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(201));
      ImageApi api = api(DockerApi.class, server.getUrl("/").toString()).getImageApi();
      try {
         api.tagImage("633fcd11259e8d6bccfbb59a4086b95b0d0fb44edfc3912000ef1f70e8a7bfc6", "jclouds", "testTag", true);
         assertSent(server, "POST",
               "/images/633fcd11259e8d6bccfbb59a4086b95b0d0fb44edfc3912000ef1f70e8a7bfc6/tag?repo=jclouds&tag=testTag&force=true");
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

   public void testGetHistory() throws Exception {
      MockWebServer server = mockWebServer(
            new MockResponse().setBody(payloadFromResource("/history.json")),
            new MockResponse().setBody(payloadFromResource("/history-apiver22.json")),
            new MockResponse().setResponseCode(404));
      ImageApi api = api(DockerApi.class, server.getUrl("/").toString()).getImageApi();
      try {
         assertEquals(api.getHistory("ubuntu"), new HistoryParseTest().expected());
         assertSent(server, "GET", "/images/ubuntu/history");

         // Docker Engine 1.10 (REST API ver 22) doesn't return parent layer IDs
         assertEquals(api.getHistory("fcf9d588ee9ab46c5a888e67f892fac66e6396eb195a743e50c0c5f9a4710e66"), 
               ImmutableList.of(
               ImageHistory.create("sha256:fcf9d588ee9ab46c5a888e67f892fac66e6396eb195a743e50c0c5f9a4710e66",
                     1456304238,
                     "",
                     ImmutableList.of("registry.acme.com:8888/jboss-eap-test/eap-test:1.0-3"),
                     188605160,
                     ""),
               ImageHistory.create("<missing>",
                     1455838658,
                     "",
                     null,
                     195019519,
                     ""),
               ImageHistory.create("<missing>",
                     1455812978,
                     "",
                     null,
                     203250948,
                     "Imported from -")
               ));
         assertSent(server, "GET", "/images/fcf9d588ee9ab46c5a888e67f892fac66e6396eb195a743e50c0c5f9a4710e66/history");

         // check also if  empty list is returned if the image is not found
         List<ImageHistory> historyList = api.getHistory("missing-image");
         assertNotNull(historyList);
         assertTrue(historyList.isEmpty());
      } finally {
         server.shutdown();
      }
   }
}
