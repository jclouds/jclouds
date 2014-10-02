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
import org.jclouds.docker.domain.Info;
import org.jclouds.docker.domain.Version;
import org.jclouds.docker.internal.BaseDockerMockTest;
import org.jclouds.docker.options.BuildOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import static org.jclouds.docker.compute.BaseDockerApiLiveTest.tarredDockerfile;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;
import java.io.File;
import java.io.FileInputStream;

/**
 * Mock tests for the {@link org.jclouds.docker.features.MiscApi} class.
 */
@Test(groups = "unit", testName = "MiscApiMockTest")
public class MiscApiMockTest extends BaseDockerMockTest {

   private static final String API_VERSION = "1.15";
   private static final String VERSION = "1.3.0";
   private static final String GIT_COMMIT = "c78088f";
   private static final String GO_VERSION = "go1.3.3";
   private static final String KERNEL_VERSION = "3.16.4-tinycore64";
   private static final String ARCH = "amd64";
   private static final String OS = "linux";

   public void testGetVersion() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/version.json")));
      DockerApi dockerApi = api(server.getUrl("/"));
      MiscApi api = dockerApi.getMiscApi();
      try {
         Version version = api.getVersion();
         assertRequestHasCommonFields(server.takeRequest(), "/version");
         assertNotNull(version);
         assertEquals(version.version(), VERSION);
         assertEquals(version.gitCommit(), GIT_COMMIT);
         assertEquals(version.apiVersion(), API_VERSION);
         assertEquals(version.goVersion(), GO_VERSION);
         assertEquals(version.kernelVersion(), KERNEL_VERSION);
         assertEquals(version.arch(), ARCH);
         assertEquals(version.os(), OS);
      } finally {
         dockerApi.close();
         server.shutdown();
      }
   }

   public void testGetInfo() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/info.json")));
      DockerApi dockerApi = api(server.getUrl("/"));
      MiscApi api = dockerApi.getMiscApi();

      try {
         Info info = api.getInfo();
         assertRequestHasCommonFields(server.takeRequest(), "/info");
         assertNotNull(info);
         assertNotNull(info.containers());
         assertNotNull(info.debug());
         assertNotNull(info.driver());
         assertNotNull(info.executionDriver());
         assertNotNull(info.images());
         assertNotNull(info.indexServerAddress());
         assertNotNull(info.initPath());
         assertNotNull(info.iPv4Forwarding());
         assertNotNull(info.kernelVersion());
         assertNotNull(info.memoryLimit());
         assertNotNull(info.nEventsListener());
         assertNotNull(info.nFd());
         assertNotNull(info.nGoroutines());
         assertNotNull(info.swapLimit());
      } finally {
         dockerApi.close();
         server.shutdown();
      }
   }

   public void testBuildContainer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(200));
      DockerApi dockerApi = api(server.getUrl("/"));
      MiscApi api = dockerApi.getMiscApi();
      File dockerFile = File.createTempFile("docker", "tmp");
      try {
         api.build(tarredDockerfile(), BuildOptions.NONE);
         assertRequestHasCommonFields(server.takeRequest(), "POST", "/build");
      } finally {
         dockerFile.delete();
         dockerApi.close();
         server.shutdown();
      }
   }

   public void testBuildContainerUsingPayload() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(200));
      DockerApi dockerApi = api(server.getUrl("/"));
      MiscApi api = dockerApi.getMiscApi();
      File file = File.createTempFile("docker", "tmp");
      FileInputStream data = new FileInputStream(file);
      Payload payload = Payloads.newInputStreamPayload(data);
      payload.getContentMetadata().setContentLength(file.length());

      try {
         api.build(payload, BuildOptions.NONE);
         assertRequestHasCommonFields(server.takeRequest(), "POST", "/build");
      } finally {
         dockerApi.close();
         server.shutdown();
      }
   }

   public void testBuildNonexistentContainer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));
      DockerApi dockerApi = api(server.getUrl("/"));
      MiscApi api = dockerApi.getMiscApi();
      File dockerFile = File.createTempFile("docker", "tmp");
      try {
         try {
            api.build(tarredDockerfile(), BuildOptions.NONE);
            fail("Build container must fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }
      } finally {
         dockerFile.delete();
         dockerApi.close();
         server.shutdown();
      }
   }

}
