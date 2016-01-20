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
package org.jclouds.digitalocean2.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.DigitalOcean2ProviderMetadata;
import org.jclouds.json.Json;
import org.jclouds.rest.ApiContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonParser;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

public class BaseDigitalOcean2ApiMockTest {
   
   private static final String MOCK_BEARER_TOKEN = "c5401990f0c24135e8d6b5d260603fc71696d4738da9aa04a720229a01a2521d";
   private static final String DEFAULT_ENDPOINT = new DigitalOcean2ProviderMetadata().getEndpoint();
   
   private final Set<Module> modules = ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor()));
   
   protected MockWebServer server;
   protected DigitalOcean2Api api;
   private Json json;
   
   // So that we can ignore formatting.
   private final JsonParser parser = new JsonParser();
   
   @BeforeMethod
   public void start() throws IOException {
      server = new MockWebServer();
      server.play();
      ApiContext<DigitalOcean2Api> ctx = ContextBuilder.newBuilder("digitalocean2")
            .credentials("", MOCK_BEARER_TOKEN)
            .endpoint(url(""))
            .modules(modules)
            .overrides(overrides())
            .build();
      json = ctx.utils().injector().getInstance(Json.class);
      api = ctx.getApi();
   }

   @AfterMethod(alwaysRun = true)
   public void stop() throws IOException {
      server.shutdown();
      api.close();
   }
   
   protected Properties overrides() {
      return new Properties();
   }

   protected String url(String path) {
      return server.getUrl(path).toString();
   }

   protected MockResponse jsonResponse(String resource) {
      return new MockResponse().addHeader("Content-Type", "application/json").setBody(stringFromResource(resource));
   }

   protected MockResponse response404() {
      return new MockResponse().setStatus("HTTP/1.1 404 Not Found");
   }
   
   protected MockResponse response204() {
      return new MockResponse().setStatus("HTTP/1.1 204 No Content");
   }

   protected String stringFromResource(String resourceName) {
      try {
         return Resources.toString(getClass().getResource(resourceName), Charsets.UTF_8)
               .replace(DEFAULT_ENDPOINT, url(""));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
   
   protected <T> T onlyObjectFromResource(String resourceName, TypeToken<Map<String, T>> type) {
      // Assume JSON objects passed here will be in the form: { "entity": { ... } }
      String text = stringFromResource(resourceName);
      Map<String, T> object = json.fromJson(text, type.getType());
      checkArgument(!object.isEmpty(), "The given json does not contain any object: %s", text);
      checkArgument(object.keySet().size() == 1, "The given json does not contain more than one object: %s", text);
      return object.get(getOnlyElement(object.keySet()));
   }
   
   protected <T> T objectFromResource(String resourceName, Class<T> type) {
      String text = stringFromResource(resourceName);
      return json.fromJson(text, type);
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path) throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertEquals(request.getMethod(), method);
      assertEquals(request.getPath(), path);
      assertEquals(request.getHeader("Accept"), "application/json");
      assertEquals(request.getHeader("Authorization"), "Bearer " + MOCK_BEARER_TOKEN);
      return request;
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path, String json)
         throws InterruptedException {
      RecordedRequest request = assertSent(server, method, path);
      assertEquals(request.getHeader("Content-Type"), "application/json");
      assertEquals(parser.parse(new String(request.getBody(), Charsets.UTF_8)), parser.parse(json));
      return request;
   }
}
