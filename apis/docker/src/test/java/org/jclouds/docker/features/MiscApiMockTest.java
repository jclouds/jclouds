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

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.docker.DockerApi;
import org.jclouds.docker.internal.BaseDockerMockTest;
import org.jclouds.docker.parse.InfoParseTest;
import org.jclouds.docker.parse.VersionParseTest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import static org.jclouds.docker.compute.BaseDockerApiLiveTest.tarredDockerfile;
import static org.testng.Assert.assertEquals;
import java.io.File;
import java.io.FileInputStream;

/**
 * Mock tests for the {@link org.jclouds.docker.features.MiscApi} class.
 */
@Test(groups = "unit", testName = "MiscApiMockTest")
public class MiscApiMockTest extends BaseDockerMockTest {

   public void testGetVersion() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/version.json")));
      MiscApi api = api(DockerApi.class, server.getUrl("/").toString()).getMiscApi();
      try {
         assertEquals(api.getVersion(), new VersionParseTest().expected());
         assertSent(server, "GET", "/version");
      } finally {
         server.shutdown();
      }
   }

   public void testGetInfo() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/info.json")));
      MiscApi api = api(DockerApi.class, server.getUrl("/").toString()).getMiscApi();
      try {
         assertEquals(api.getInfo(), new InfoParseTest().expected());
         assertSent(server, "GET", "/info");
      } finally {
         server.shutdown();
      }
   }

   public void testBuildContainer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(200));
      MiscApi api = api(DockerApi.class, server.getUrl("/").toString()).getMiscApi();
      try {
         api.build(tarredDockerfile());
         assertSent(server, "POST", "/build");
      } finally {
         server.shutdown();
      }
   }

   public void testBuildContainerUsingPayload() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(200));
      MiscApi api = api(DockerApi.class, server.getUrl("/").toString()).getMiscApi();
      File file = File.createTempFile("docker", "tmp");
      FileInputStream data = new FileInputStream(file);
      Payload payload = Payloads.newInputStreamPayload(data);
      payload.getContentMetadata().setContentLength(file.length());
      try {
         api.build(payload);
         assertSent(server, "POST", "/build");
      } finally {
         server.shutdown();
      }
   }

}
