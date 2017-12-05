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
package org.jclouds.openstack.keystone.v3.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.util.concurrent.MoreExecutors.newDirectExecutorService;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.okhttp.config.OkHttpCommandExecutorServiceModule;
import org.jclouds.json.Json;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.auth.AuthenticationApi;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.keystone.v3.KeystoneApi;
import org.jclouds.openstack.keystone.v3.KeystoneApiMetadata;
import org.jclouds.openstack.keystone.v3.domain.Token;
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

public class BaseV3KeystoneApiMockTest {
   
   private static final String DEFAULT_ENDPOINT = new KeystoneApiMetadata().getDefaultEndpoint().orNull();
   
   protected MockWebServer server;
   protected KeystoneApi api;
   protected AuthenticationApi authenticationApi;
   protected String authToken;
   private Json json;
   
   // So that we can ignore formatting.
   private final JsonParser parser = new JsonParser();
   
   @BeforeMethod
   public void start() throws IOException {
      server = new MockWebServer();
      server.play();
      
      ApiContext<KeystoneApi> ctx = ContextBuilder.newBuilder("openstack-keystone-3")
              .credentials("domain:identity", "credential")
              .endpoint(url(""))
              .modules(modules())
              .overrides(overrides())
              .build();
      json = ctx.utils().injector().getInstance(Json.class);
      authenticationApi = ctx.utils().injector().getInstance(AuthenticationApi.class);
      api = ctx.getApi();
   }

   @AfterMethod(alwaysRun = true)
   public void stop() throws IOException {
      server.shutdown();
      api.close();
   }
   
   protected Properties overrides() {
      Properties overrides = new Properties();
      overrides.setProperty(KeystoneProperties.SCOPE, "projectId:1234567890");
      overrides.setProperty(KeystoneProperties.SERVICE_TYPE, "identityv3");
      return overrides;
   }
   
   protected Set<Module> modules() {
      ImmutableSet.Builder<Module> modules = ImmutableSet.builder();
      modules.add(new ExecutorServiceModule(newDirectExecutorService()));
      modules.add(new OkHttpCommandExecutorServiceModule());
      modules.add(new SLF4JLoggingModule());
      return modules.build();
   }

   protected String url(String path) {
      return server.getUrl(path).toString();
   }
   
   protected void enqueueAuthentication(MockWebServer server) {
      authToken = UUID.randomUUID().toString();
      server.enqueue(jsonResponse("/v3/token.json").addHeader("X-Subject-Token", authToken));
   }

   protected MockResponse jsonResponse(String resource) {
      return new MockResponse().addHeader("Content-Type", "application/json").setBody(stringFromResource(resource));
   }

   protected MockResponse response404() {
      return new MockResponse().setStatus("HTTP/1.1 404 Not Found");
   }
   
   protected MockResponse response201() {
      return new MockResponse().setStatus("HTTP/1.1 201 Created");
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
   
   protected void assertAuthentication(MockWebServer server) throws InterruptedException {
      RecordedRequest request = assertSentNoAuth(server, "POST", "/auth/tokens");
      assertBody(request, stringFromResource("/v3/auth-password-scoped.json"));
   }
   
   private RecordedRequest assertSentNoAuth(MockWebServer server, String method, String path) throws InterruptedException {
      RecordedRequest request = server.takeRequest();
      assertEquals(request.getMethod(), method);
      assertEquals(request.getPath(), path);
      assertEquals(request.getHeader("Accept"), "application/json");
      return request;
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path) throws InterruptedException {
      RecordedRequest request = assertSentNoAuth(server, method, path);
      assertEquals(request.getHeader("X-Auth-Token"), authToken);
      return request;
   }

   protected RecordedRequest assertSent(MockWebServer server, String method, String path, String json)
         throws InterruptedException {
      RecordedRequest request = assertSent(server, method, path);
      assertBody(request, json);
      return request;
   }
   
   private void assertBody(RecordedRequest request, String body) {
      assertEquals(request.getHeader("Content-Type"), "application/json");
      assertEquals(parser.parse(new String(request.getBody(), Charsets.UTF_8)), parser.parse(body));
   }

   protected Token tokenFromResource(String resource) {
      return onlyObjectFromResource(resource, new TypeToken<Map<String, Token>>() {
         private static final long serialVersionUID = 1L;
      });
   }
}
