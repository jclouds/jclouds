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

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.docker.compute.BaseDockerApiLiveTest.tarredDockerfile;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.docker.DockerApi;
import org.jclouds.docker.config.DockerParserModule;
import org.jclouds.docker.domain.Exec;
import org.jclouds.docker.domain.ExecCreateParams;
import org.jclouds.docker.domain.ExecInspect;
import org.jclouds.docker.domain.ExecStartParams;
import org.jclouds.docker.internal.BaseDockerMockTest;
import org.jclouds.docker.parse.InfoParseTest;
import org.jclouds.docker.parse.VersionParseTest;
import org.jclouds.docker.util.DockerInputStream;
import org.jclouds.docker.util.StdStreamData;
import org.jclouds.docker.util.StdStreamData.StdStreamType;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

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
         RecordedRequest request = assertSent(server, "POST", "/build");
         assertDockerBuildHttpHeaders(request);
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
         RecordedRequest request = assertSent(server, "POST", "/build");
         assertDockerBuildHttpHeaders(request);
      } finally {
         server.shutdown();
      }
   }


   public void testExecCreate() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/exec.json")));
      MiscApi api = api(DockerApi.class, server.getUrl("/").toString(), new DockerParserModule()).getMiscApi();
      try {
         final String containerId = "a40d212a0a379de00426a1da2a8fd3fd20d5f74fd7c2dd42f6c93a6b1b0e6974";
         final ExecCreateParams execParams = ExecCreateParams.builder()
               .cmd(ImmutableList.<String> of("/bin/sh", "-c", "echo -n Standard >&1 && echo -n Error >&2"))
               .attachStderr(true).attachStdout(true).build();
         final Exec expectedExec = Exec.create("dbf45d296388032ebb9872edb75847f6655a72b4e9ab0d99ae1c75589c4ca957");
         assertEquals(api.execCreate(containerId, execParams), expectedExec);
         assertSent(server, "POST", "/containers/" + containerId + "/exec");
      } finally {
         server.shutdown();
      }
   }

   public void testExecStart() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/exec.start")));
      MiscApi api = api(DockerApi.class, server.getUrl("/").toString(), new DockerParserModule()).getMiscApi();
      DockerInputStream dis = null;
      try {
         final String execId = "dbf45d296388032ebb9872edb75847f6655a72b4e9ab0d99ae1c75589c4ca957";
         final ExecStartParams startParams = ExecStartParams.builder().detach(false).build();
         dis = new DockerInputStream(api.execStart(execId, startParams));

         final StdStreamData msg1 = dis.readStdStreamData();
         assertFalse(msg1.isTruncated());
         assertEquals(msg1.getPayload(), "Standard".getBytes(StandardCharsets.UTF_8));
         assertEquals(msg1.getType(), StdStreamType.OUT);

         final StdStreamData msg2 = dis.readStdStreamData();
         assertFalse(msg2.isTruncated());
         assertEquals(msg2.getPayload(), "Error".getBytes(StandardCharsets.UTF_8));
         assertEquals(msg2.getType(), StdStreamType.ERR);

         assertNull(dis.readStdStreamData());
         assertSent(server, "POST", "/exec/" + execId + "/start");
      } finally {
         if (dis != null) {
            dis.close();
         }
         server.shutdown();
      }
   }

   public void testExecInspect() throws IOException, InterruptedException {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/execInspect.json")));
      MiscApi api = api(DockerApi.class, server.getUrl("/").toString(), new DockerParserModule()).getMiscApi();
      final String expectedExecId = "fda1cf8064863fc0667c691c69793fdb7d0bd4a1fabb8250536abe5203e4208a";
      ExecInspect execInspect = api.execInspect(expectedExecId);
      assertNotNull(execInspect);
      assertEquals(execInspect.id(), expectedExecId);
      assertEquals(execInspect.running(), false);
      assertEquals(execInspect.exitCode(), 2);
      assertSent(server, "GET", "/exec/" + expectedExecId + "/json");
   }

   /**
    * Asserts that correct values of HTTP headers are used in Docker build REST
    * API calls.
    *
    * @param request
    */
   private void assertDockerBuildHttpHeaders(RecordedRequest request) {
      assertThat(request.getHeader("Connection")).isEqualTo("close");
      assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo("application/tar");
   }

}
