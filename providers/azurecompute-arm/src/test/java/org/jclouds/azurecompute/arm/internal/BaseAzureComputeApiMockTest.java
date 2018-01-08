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
package org.jclouds.azurecompute.arm.internal;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.assertj.core.util.Sets.newHashSet;
import static org.jclouds.oauth.v2.config.CredentialType.BEARER_TOKEN_CREDENTIALS;
import static org.jclouds.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.AzureManagementApiMetadata;
import org.jclouds.azurecompute.arm.config.AzureComputeHttpApiModule;
import org.jclouds.azurecompute.arm.config.GraphRBAC;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.date.DateService;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.ApiContext;
import org.jclouds.rest.ConfiguresHttpApi;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Charsets;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.gson.JsonParser;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

public class BaseAzureComputeApiMockTest {

   private static final String MOCK_BEARER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Ik1uQ19WWmNBVGZNNXBPWWlKSE1iYTlnb0VLWSIsImtpZCI6Ik1uQ19WWmNBVGZNNXBPWWlKSE1iYTlnb0VLWSJ9";
   private static final String DEFAULT_ENDPOINT = new AzureComputeProviderMetadata().getEndpoint();

   protected MockWebServer server;
   protected AzureComputeApi api;
   protected ApiContext<AzureComputeApi> context;
   protected DateService dateService;

   // So that we can ignore formatting.
   private final JsonParser parser = new JsonParser();

   @BeforeMethod
   public void start() throws IOException, URISyntaxException {
      server = new MockWebServer();
      server.play();
      
      context = ContextBuilder.newBuilder(testProviderMetadata())
              .credentials("mock", MOCK_BEARER_TOKEN)
              .endpoint(server.getUrl("/").toString() + "subscriptions/SUBSCRIPTIONID")
              .modules(setupModules())
              .overrides(setupProperties())
              .build();
      api = context.getApi();
      dateService = context.utils().injector().getInstance(DateService.class);
   }
   
   protected ProviderMetadata testProviderMetadata() {
      // Omit the default HTTP API modules to allow overriding
      Set<Class<? extends Module>> defaultModules = newHashSet(filter(
            new AzureManagementApiMetadata().getDefaultModules(),
            not(Predicates.<Class<? extends Module>> equalTo(AzureComputeHttpApiModule.class))));
      return AzureComputeProviderMetadata.builder()
            .apiMetadata(AzureManagementApiMetadata.builder().defaultModules(defaultModules).build()).build();
   }

   protected Properties setupProperties() {
      Properties properties = new Properties();
      properties.put(CREDENTIAL_TYPE, BEARER_TOKEN_CREDENTIALS.toString());
      properties.put("oauth.endpoint", "https://login.microsoftonline.com/tenant-id/oauth2/token");
      return properties;
   }

   protected Set<Module> setupModules() {
      ImmutableSet.Builder<Module> modules = ImmutableSet.builder();
      modules.add(new ExecutorServiceModule(sameThreadExecutor()));
      // Override the default HTTP module to accomodate custom bindings for the
      // hardcoded endpoints such as the Graph RBAC API one.
      modules.add(new TestAzureComputeHttpApiModule(server));
      return modules.build();
   }
   
   @AfterMethod(alwaysRun = true)
   public void stop() throws IOException {
      server.shutdown();
      api.close();
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

   protected MockResponse response200() {
      return new MockResponse().setStatus("HTTP/1.1 200 OK");
   }

   protected MockResponse response202() {
      return new MockResponse().setStatus("HTTP/1.1 202 Accepted");
   }

   protected MockResponse response204() {
      return new MockResponse().setStatus("HTTP/1.1 204 No Content");
   }

   protected MockResponse response202WithHeader() {
      return new MockResponse()
            .setStatus("HTTP/1.1 202 Accepted")
            .addHeader(
                  "Location",
                  "https://management.azure.com/subscriptions/SUBSCRIPTIONID/operationresults/eyJqb2JJZCI6IlJFU09VUkNFR1JPVVBERUxFVElPTkpPQi1SVEVTVC1DRU5UUkFMVVMiLCJqb2JMb2NhdGlvbiI6ImNlbnRyYWx1cyJ9?api-version=2014-04-01");
   }

   protected MockResponse response200WithHeader() {
      return new MockResponse()
              .setStatus("HTTP/1.1 200 O")
              .addHeader("Location", "https://management.azure.com/subscriptions/SUBSCRIPTIONID/operationresults/eyJqb2JJZCI6IlJFU09VUkNFR1JPVVBERUxFVElPTkpPQi1SVEVTVC1DRU5UUkFMVVMiLCJqb2JMb2NhdGlvbiI6ImNlbnRyYWx1cyJ9?api-version=2014-04-01");
   }

   protected String stringFromResource(String resourceName) {
      try {
         String rsrc = Resources.toString(getClass().getResource(resourceName), Charsets.UTF_8);
         if (rsrc.contains(DEFAULT_ENDPOINT)) {
            rsrc = rsrc.replace(DEFAULT_ENDPOINT, url(""));
         }
         return rsrc;
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
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
   
   @ConfiguresHttpApi
   private static class TestAzureComputeHttpApiModule extends AzureComputeHttpApiModule {
      private final MockWebServer server;
      
      public TestAzureComputeHttpApiModule(MockWebServer server) {
         this.server = server;
      }

      @Override
      protected void bindServiceEndpoints() {
         // Override the hardcoded service URIs to allow mocking service endpoints
         bind(new TypeLiteral<Supplier<URI>>() {
         }).annotatedWith(GraphRBAC.class).toInstance(
               Suppliers.ofInstance(URI.create(server.getUrl("/graphrbac").toString() + "/tenant-id")));
      }
   }
}
