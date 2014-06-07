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
package org.jclouds.elasticstack;

import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.http.BaseMockWebServerTest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Mock tests for the {@link ElasticStackApi} class.
 */
@Test(groups = "unit", testName = "ElasticStackMockTest")
public class ElasticStackMockTest extends BaseMockWebServerTest {

   public void testListStandardDrives() throws IOException, InterruptedException {
      MockWebServer server = mockWebServer(new MockResponse()
            .setBody(payloadFromResource("/standard_drives_uuids.txt")));
      ElasticStackApi api = api(ElasticStackApi.class, server.getUrl("/").toString());

      try {
         Set<String> standardDrives = api.listStandardDrives();
         assertEquals(standardDrives.size(), 36);

         RecordedRequest request = server.takeRequest();
         assertAuthentication(request);
         assertEquals(request.getRequestLine(),
               String.format("GET /drives/list/standard HTTP/1.1", server.getUrl("/").toString()));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testListStandardDriveInfo() throws IOException, InterruptedException {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/standard_drives.txt")));
      ElasticStackApi api = api(ElasticStackApi.class, server.getUrl("/").toString());

      try {
         Set<StandardDrive> standardDrives = api.listStandardDriveInfo();
         assertEquals(standardDrives.size(), 36);

         RecordedRequest request = server.takeRequest();
         assertAuthentication(request);
         assertEquals(request.getRequestLine(),
               String.format("GET /drives/info/standard HTTP/1.1", server.getUrl("/").toString()));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   private static void assertAuthentication(final RecordedRequest request) throws InterruptedException {
      assertEquals(request.getHeader(HttpHeaders.AUTHORIZATION), "Basic dXVpZDphcGlrZXk=");
   }

   private byte[] payloadFromResource(String resource) {
      try {
         return toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   protected void addOverrideProperties(Properties props) {
      props.setProperty(PROPERTY_IDENTITY, "uuid");
      props.setProperty(PROPERTY_CREDENTIAL, "apikey");
   }

   @Override
   protected Module createConnectionModule() {
      return new JavaUrlHttpCommandExecutorServiceModule();
   }

}
