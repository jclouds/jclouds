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
package org.jclouds.docker.internal;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.http.utils.Queries.encodeQueryLine;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.docker.DockerApi;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Base class for all Docker mock tests.
 */
public class BaseDockerMockTest {
   private final Set<Module> modules = ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(),
         sameThreadExecutor()));

   protected String provider;

   public BaseDockerMockTest() {
      provider = "docker";
   }

   public DockerApi api(URL url) {
      return ContextBuilder.newBuilder(provider)
            .credentials("clientid", "apikey")
            .endpoint(url.toString())
            .modules(modules) 
            .overrides(setupProperties()) 
            .buildApi(DockerApi.class);
   }

   protected Properties setupProperties() {
      return new Properties();
   }

   public static MockWebServer mockWebServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      return server;
   }

   public byte[] payloadFromResource(String resource) {
      try {
         return toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   protected static void assertRequestHasCommonFields(final RecordedRequest request, final String path)
           throws InterruptedException {
      assertRequestHasParameters(request, "GET", path, ImmutableMultimap.<String, String> of());
   }

   protected static void assertRequestHasCommonFields(final RecordedRequest request,
                                                      final String verb, final String path)
         throws InterruptedException {
      assertRequestHasParameters(request, verb, path, ImmutableMultimap.<String, String> of());
   }

   protected static void assertRequestHasParameters(final RecordedRequest request, final String path,
                                                    Multimap<String, String> parameters) throws InterruptedException {
      assertRequestHasParameters(request, "GET", path, parameters);
   }

   protected static void assertRequestHasParameters(final RecordedRequest request, String verb, final String path,
                                                    Multimap<String, String> parameters) throws InterruptedException {
      String queryParameters = "";
      if (!parameters.isEmpty()) {
         Multimap<String, String> allparams = ImmutableMultimap.<String, String>builder()
                 .putAll(parameters)
                 .build();

         assertRequestHasAcceptHeader(request);
         queryParameters = "?" + encodeQueryLine(allparams);
      }
      assertEquals(request.getRequestLine(), verb + " " + path + queryParameters + " HTTP/1.1");
   }

   protected static void assertRequestHasAcceptHeader(final RecordedRequest request) throws InterruptedException {
      assertEquals(request.getHeader(HttpHeaders.ACCEPT), MediaType.APPLICATION_JSON);
   }

}
