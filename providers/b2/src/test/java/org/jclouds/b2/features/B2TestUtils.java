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
package org.jclouds.b2.features;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.b2.B2Api;
import org.jclouds.util.Strings2;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

final class B2TestUtils {
   static B2Api api(String uri, String provider, Properties overrides) {
       Set<Module> modules = ImmutableSet.<Module> of(
             new ExecutorServiceModule(MoreExecutors.sameThreadExecutor()));

      return ContextBuilder.newBuilder(provider)
            .credentials("ACCOUNT_ID", "APPLICATION_KEY")
            .endpoint(uri)
            .overrides(overrides)
            .modules(modules)
            .buildApi(B2Api.class);
   }

   static B2Api api(String uri, String provider) {
      return api(uri, provider, new Properties());
   }

   static MockWebServer createMockWebServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      URL url = server.getUrl("");
      return server;
   }

   static void assertAuthentication(MockWebServer server) {
      assertThat(server.getRequestCount()).isGreaterThanOrEqualTo(1);
      try {
         assertThat(server.takeRequest().getRequestLine()).isEqualTo("GET /b2api/v1/b2_authorize_account HTTP/1.1");
      } catch (InterruptedException e) {
         throw Throwables.propagate(e);
      }
   }

   /**
    * Ensures the request has a json header for the proper REST methods.
    *
    * @param request
    * @param method
    *           The request method (such as GET).
    * @param path
    *           The path requested for this REST call.
    * @see RecordedRequest
    */
   static void assertRequest(RecordedRequest request, String method, String path) {
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
   }

   /**
    * Ensures the request is json and has the same contents as the resource
    * file provided.
    *
    * @param request
    * @param method
    *           The request method (such as GET).
    * @param resourceLocation
    *           The location of the resource file. Contents will be compared to
    *           the request body as JSON.
    * @see RecordedRequest
    */
   static void assertRequest(RecordedRequest request, String method, String path, String resourceLocation) {
      assertRequest(request, method, path);
      assertContentTypeIsJson(request);
      JsonParser parser = new JsonParser();
      JsonElement requestJson;
      try {
         requestJson = parser.parse(new String(request.getBody(), Charsets.UTF_8));
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
      JsonElement resourceJson = parser.parse(stringFromResource(resourceLocation));
      assertThat(requestJson).isEqualTo(resourceJson);
   }

   /**
    * Ensures the request has a json header.
    *
    * @param request
    * @see RecordedRequest
    */
   private static void assertContentTypeIsJson(RecordedRequest request) {
      assertThat(request.getHeaders()).contains("Content-Type: application/json");
   }

   /**
    * Get a string from a resource
    *
    * @param resourceName
    *           The name of the resource.
    * @return The content of the resource
    */
   static String stringFromResource(String resourceName) {
      try {
         return Strings2.toStringAndClose(BucketApiMockTest.class.getResourceAsStream(resourceName));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
}
