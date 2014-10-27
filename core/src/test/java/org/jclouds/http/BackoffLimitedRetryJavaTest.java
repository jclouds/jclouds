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
package org.jclouds.http;

import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.util.Closeables2.closeQuietly;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Properties;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests the retry behavior of the default {@link RetryHandler} implementation
 * {@link BackoffLimitedRetryHandler} to ensure that retries up to the default
 * limit succeed.
 */
@Test(groups = "integration")
public class BackoffLimitedRetryJavaTest extends BaseMockWebServerTest {

   private final int maxRetries = 5;

   @Override
   protected void addOverrideProperties(Properties props) {
      props.setProperty(PROPERTY_MAX_RETRIES, "" + maxRetries);
   }

   @Override
   protected Module createConnectionModule() {
      return new JavaUrlHttpCommandExecutorServiceModule();
   }

   protected IntegrationTestClient client(String url) {
      return api(IntegrationTestClient.class, url);
   }

   @Test
   public void testNoRetriesSuccessful() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse());
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         client.download("");
         assertEquals(server.getRequestCount(), 1);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testSingleRetrySuccessful() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(500), new MockResponse());
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         client.download("");
         assertEquals(server.getRequestCount(), 2);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testMaximumRetriesSuccessful() throws Exception {
      MockWebServer server = mockWebServer();
      for (int i = 0; i < maxRetries - 1; i++) {
         server.enqueue(new MockResponse().setResponseCode(500));
      }
      server.enqueue(new MockResponse());

      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         client.download("");
         assertEquals(server.getRequestCount(), maxRetries);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testMaximumRetriesExceeded() throws Exception {
      MockWebServer server = mockWebServer();
      for (int i = 0; i <= maxRetries; i++) {
         server.enqueue(new MockResponse().setResponseCode(500));
      }

      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {

         client.download("");
         fail("Request should not succeed within " + maxRetries + " requests");
      } catch (HttpResponseException ex) {
         assertEquals(ex.getResponse().getStatusCode(), 500);
         assertEquals(server.getRequestCount(), maxRetries + 1);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testInterleavedSuccessesAndFailures() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse(), new MockResponse());
      for (int i = 0; i <= maxRetries; i++) {
         server.enqueue(new MockResponse().setResponseCode(500));
      }

      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         client.download("");
         client.download("");

         try {
            client.download("");
            fail("Request should not succeed within " + maxRetries + " requests");
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 500);
            assertEquals(server.getRequestCount(), maxRetries + 3);
         }
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

}
