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
package org.jclouds.http.okhttp;

import static com.google.common.io.Closeables.close;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.testng.Assert.assertEquals;

import java.io.Closeable;
import java.util.Properties;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.http.BaseHttpCommandExecutorServiceIntegrationTest;
import org.jclouds.http.okhttp.config.OkHttpCommandExecutorServiceModule;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.binders.BindToStringPayload;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Tests the functionality of the {@link OkHttpCommandExecutorService}
 * 
 * @author Ignasi Barrera
 */
@Test
public class OkHttpCommandExecutorServiceTest extends BaseHttpCommandExecutorServiceIntegrationTest {

   @Override
   protected Module createConnectionModule() {
      return new OkHttpCommandExecutorServiceModule();
   }

   @Override
   protected void addOverrideProperties(final Properties props) {
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 50 + "");
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 0 + "");
      // IO workers not used in this executor
      props.setProperty(PROPERTY_IO_WORKER_THREADS, 0 + "");
      props.setProperty(PROPERTY_USER_THREADS, 5 + "");
   }

   private interface PatchApi extends Closeable {
      @PATCH
      @Path("/objects/{id}")
      String patch(@PathParam("id") String id, @BinderParam(BindToStringPayload.class) String body);

      @PATCH
      @Path("/objects/{id}")
      String patchNothing(@PathParam("id") String id);
   }

   @Test
   public void testPatch() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("fooPATCH"));
      PatchApi api = api(PatchApi.class, server.getUrl("/").toString());
      try {
         String result = api.patch("", "foo");
         // Verify that the body is properly populated
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getMethod(), "PATCH");
         assertEquals(new String(request.getBody(), "UTF-8"), "foo");
         assertEquals(result, "fooPATCH");
      } finally {
         close(api, true);
         server.shutdown();
      }
   }

   @Test
   public void testPatchIsRetriedOnFailure() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(500),
            new MockResponse().setBody("fooPATCH"));
      PatchApi api = api(PatchApi.class, server.getUrl("/").toString());
      try {
         String result = api.patch("", "foo");
         assertEquals(server.getRequestCount(), 2);
         assertEquals(result, "fooPATCH");
         // Verify that the body was properly sent in the two requests
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getMethod(), "PATCH");
         assertEquals(new String(request.getBody(), "UTF-8"), "foo");
         request = server.takeRequest();
         assertEquals(request.getMethod(), "PATCH");
         assertEquals(new String(request.getBody(), "UTF-8"), "foo");
      } finally {
         close(api, true);
         server.shutdown();
      }
   }

   @Test
   public void testPatchRedirect() throws Exception {
      MockWebServer redirectTarget = mockWebServer(new MockResponse().setBody("fooPATCHREDIRECT"));
      redirectTarget.useHttps(sslContext.getSocketFactory(), false);
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(302).setHeader("Location",
            redirectTarget.getUrl("/").toString()));
      PatchApi api = api(PatchApi.class, server.getUrl("/").toString());
      try {
         String result = api.patch("", "foo");
         assertEquals(result, "fooPATCHREDIRECT");
         assertEquals(server.getRequestCount(), 1);
         assertEquals(redirectTarget.getRequestCount(), 1);
         // Verify that the body was populated after the redirect
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getMethod(), "PATCH");
         assertEquals(new String(request.getBody(), "UTF-8"), "foo");
         request = redirectTarget.takeRequest();
         assertEquals(request.getMethod(), "PATCH");
         assertEquals(new String(request.getBody(), "UTF-8"), "foo");
      } finally {
         close(api, true);
         redirectTarget.shutdown();
         server.shutdown();
      }
   }

   @Test
   public void testZeroLengthPatch() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse());
      PatchApi api = api(PatchApi.class, server.getUrl("/").toString());
      try {
         api.patchNothing("");
         assertEquals(server.getRequestCount(), 1);
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getMethod(), "PATCH");
         assertEquals(new String(request.getBody(), "UTF-8"), "");
      } finally {
         close(api, true);
         server.shutdown();
      }
   }
}
