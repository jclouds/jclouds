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
package org.jclouds.openstack.v2_0.internal;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.util.Strings2;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.QueueDispatcher;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

public class BaseOpenStackMockTest<A extends Closeable> {

   /**
    * Needed for backwards-compatibility. This variable might not be up-to-date
    * with current services available.
    * */
   @Deprecated
   public static final String accessRackspace = "{\"access\":{\"token\":{\"id\":\"b84f4a37-5126-4603-9521-ccd0665fbde1\",\"expires\":\"2013-04-13T16:49:57.000-05:00\",\"tenant\":{\"id\":\"123123\",\"name\":\"123123\"}},\"serviceCatalog\":[{\"endpoints\":[{\"tenantId\":\"123123\",\"publicURL\":\"URL/v1.0/123123\"}],\"name\":\"cloudMonitoring\",\"type\":\"rax:monitor\"},{\"endpoints\":[{\"region\":\"DFW\",\"tenantId\":\"MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\",\"publicURL\":\"URL/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\"},{\"region\":\"ORD\",\"tenantId\":\"MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\",\"publicURL\":\"URL/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\"}],\"name\":\"cloudFilesCDN\",\"type\":\"rax:object-cdn\"},{\"endpoints\":[{\"region\":\"ORD\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1.0/123123\"},{\"region\":\"DFW\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1.0/123123\"}],\"name\":\"cloudLoadBalancers\",\"type\":\"rax:load-balancer\"},{\"endpoints\":[{\"region\":\"DFW\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1.0/123123\"},{\"region\":\"ORD\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1.0/123123\"}],\"name\":\"cloudDatabases\",\"type\":\"rax:database\"},{\"endpoints\":[{\"region\":\"DFW\",\"tenantId\":\"MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\",\"publicURL\":\"URL/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\",\"internalURL\":\"URL/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\"},{\"region\":\"ORD\",\"tenantId\":\"MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\",\"publicURL\":\"URL/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\",\"internalURL\":\"URL/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9\"}],\"name\":\"cloudFiles\",\"type\":\"object-store\"},{\"endpoints\":[{\"tenantId\":\"123123\",\"publicURL\":\"URL/v1.0/123123\",\"versionInfo\":\"URL/v1.0\",\"versionList\":\"URL/\",\"versionId\":\"1.0\"}],\"name\":\"cloudServers\",\"type\":\"compute\"},{\"endpoints\":[{\"region\":\"DFW\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v2/123123\",\"versionInfo\":\"URL/v2\",\"versionList\":\"URL/\",\"versionId\":\"2\"},{\"region\":\"ORD\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v2/123123\",\"versionInfo\":\"URL/v2\",\"versionList\":\"URL/\",\"versionId\":\"2\"}],\"name\":\"cloudServersOpenStack\",\"type\":\"compute\"},{\"endpoints\":[{\"tenantId\":\"123123\",\"publicURL\":\"URL/v1.0/123123\"}],\"name\":\"cloudDNS\",\"type\":\"rax:dns\"},{\"endpoints\":[{\"tenantId\":\"123123\",\"publicURL\":\"URL/v1.0/123123\"}],\"name\":\"cloudBackup\",\"type\":\"rax:backup\"},{\"endpoints\":[{\"region\":\"DFW\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1/123123\"},{\"region\":\"ORD\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1/123123\"}],\"name\":\"cloudBlockStorage\",\"type\":\"volume\"},{\"endpoints\":[{\"region\":\"DFW\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1/123123\",\"internalURL\":\"URL/v1/123123\"},{\"region\":\"ORD\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1/123123\",\"internalURL\":\"URL/v1/123123\"}],\"name\":\"marconi\",\"type\":\"queuing\"},{\"endpoints\":[{\"region\":\"DFW\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1/123123\",\"internalURL\":\"URL/v1/123123\"},{\"region\":\"ORD\",\"tenantId\":\"123123\",\"publicURL\":\"URL/v1/123123\",\"internalURL\":\"URL/v1/123123\"}],\"name\":\"autoscale\",\"type\":\"rax:autoscale\"}],\"user\":{\"id\":\"1234\",\"roles\":[{\"id\":\"3\",\"description\":\"User Admin Role.\",\"name\":\"identity:user-admin\"}],\"name\":\"jclouds-joe\",\"RAX-AUTH:defaultRegion\":\"DFW\"}}}";

   private final Set<Module> modules = ImmutableSet.<Module> of(
         new ExecutorServiceModule(sameThreadExecutor()));

   /**
    * Pattern for replacing the URL token with the correct local address.
    */
   private static final Pattern urlTokenPattern = Pattern.compile(":\\s*\"\\s*URL");

   @SuppressWarnings("serial")
   public A api(String uri, String provider, Properties overrides) {
      if (!overrides.containsKey(PROPERTY_MAX_RETRIES)) {
         overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      }

      return ContextBuilder.newBuilder(provider)
            .credentials("jclouds:joe", "letmein")
            .endpoint(uri)
            .overrides(overrides)
            .modules(modules)
            .buildApi(new TypeToken<A>(getClass()) {});
   }

   public A api(String uri, String provider) {
      return api(uri, provider, new Properties());
   }

   public static MockWebServer mockOpenStackServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      URL url = server.getUrl("");
      server.setDispatcher(getURLReplacingQueueDispatcher(url));
      return server;
   }

   /**
    * there's no built-in way to defer evaluation of a response header, hence
    * this method, which allows us to send back links to the mock server.
    */
   public static QueueDispatcher getURLReplacingQueueDispatcher(final URL url) {
      final QueueDispatcher dispatcher = new QueueDispatcher() {
         protected final BlockingQueue<MockResponse> responseQueue = new LinkedBlockingQueue<MockResponse>();

         @Override
         public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            MockResponse response = responseQueue.take();
            if (response.getBody() != null) {
               /*
                * "URL" must be used in the service catalog sample (such as
                * access.json or accessRackspace.json) for the declared service
                * endpoints.
                */
               String newBody = urlTokenPattern.matcher(new String(response.getBody().readByteArray())).replaceAll(": \"" + url.toString());

               response = response.setBody(newBody);
            }
            return response;
         }

         @Override
         public void enqueueResponse(MockResponse response) {
            responseQueue.add(response);
         }
      };

      return dispatcher;
   }

   /**
    * Ensure json headers are included
    * */
   public MockResponse addCommonHeaders(MockResponse mockResponse) {
      mockResponse.addHeader(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.withoutParameters());
      return mockResponse;
   }

   /**
    * Get a string from a resource
    *
    * @param resourceName
    *           The name of the resource.
    * @return The content of the resource
    */
   public String stringFromResource(String resourceName) {
      try {
         return Strings2.toStringAndClose(getClass().getResourceAsStream(resourceName));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   /**
    * Ensures server received authentication request.
    */
   public void assertAuthentication(MockWebServer server) {
      assertTrue(server.getRequestCount() >= 1);
      try {
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
      } catch (InterruptedException e) {
         Throwables.propagate(e);
      }
   }

   /**
    * Ensures server received an extensions request. Most OpenStack APIs that expose an {@link ExtensionApi}
    * should use this method.
    */
   public void assertExtensions(MockWebServer server) {
      assertExtensions(server, "");
   }

   /**
    * Ensures server received a <i>versioned</i> extensions request. This is necessary for testing APIs whose version
    * is not included in the Endpoint's {@link Endpoint#getPublicURL() publicURL}.
    */
   public void assertExtensions(MockWebServer server, String path) {
      assertTrue(server.getRequestCount() >= 1);
      try {
         assertEquals(server.takeRequest().getRequestLine(), "GET " + path + "/extensions HTTP/1.1");
      } catch (InterruptedException e) {
         Throwables.propagate(e);
      }
   }

   /**
    * Ensures the request has a json header.
    *
    * @param request
    * @see RecordedRequest
    */
   private void assertContentTypeIsJSON(RecordedRequest request) {
      assertTrue(request.getHeaders().contains("Content-Type: application/json"));
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
   public void assertRequest(RecordedRequest request, String method, String path) {
      assertEquals(request.getMethod(), method);
      assertEquals(request.getPath(), path);
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
   public void assertRequest(RecordedRequest request, String method, String path, String resourceLocation) {
      assertRequest(request, method, path);
      assertContentTypeIsJSON(request);
      JsonParser parser = new JsonParser();
      JsonElement requestJson = null;  // to be compared
      JsonElement resourceJson;        // to be compared
      try {
         requestJson = parser.parse(new String(request.getBody(), Charsets.UTF_8));
      } catch (Exception e) {
         Throwables.propagate(e);
      }
      resourceJson = parser.parse(stringFromResource(resourceLocation));
      assertEquals(requestJson, resourceJson); // Compare as JSON
   }
}
