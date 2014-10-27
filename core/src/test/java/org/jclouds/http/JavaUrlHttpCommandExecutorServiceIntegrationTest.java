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

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.util.Closeables2.closeQuietly;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Properties;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.rest.HttpClient;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "integration")
public class JavaUrlHttpCommandExecutorServiceIntegrationTest extends BaseHttpCommandExecutorServiceIntegrationTest {

   protected Module createConnectionModule() {
      return new JavaUrlHttpCommandExecutorServiceModule();
   }

   protected void addOverrideProperties(Properties props) {
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 50 + "");
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 0 + "");
      props.setProperty(PROPERTY_USER_THREADS, 5 + "");
   }

   @Test public void longContentLengthSupported() throws Exception {
      long reallyLongContent = TestUtils.isJava6() ? Integer.MAX_VALUE : Long.MAX_VALUE;

      // Setup a mock server that doesn't attempt to read the request payload.
      MockWebServer server = new MockWebServer();
      server.setDispatcher(new Dispatcher() {
         @Override public MockResponse dispatch(RecordedRequest recordedRequest) {
            return new MockResponse();
         }
      });
      server.play();

      HttpClient client =  api(HttpClient.class, server.getUrl("/").toString());

      // Make a fake payload that has no data, but says there's a lot!
      Payload fakePayload = new StringPayload("");
      fakePayload.getContentMetadata().setContentLength(reallyLongContent);

      try {
         try {
            client.post(server.getUrl("/").toURI(), fakePayload);
            fail("Should have errored since we didn't sent that much data!");
         } catch (HttpResponseException expected) {
         }
         assertEquals(server.takeRequest().getHeader(CONTENT_LENGTH), String.valueOf(reallyLongContent));
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }
}
