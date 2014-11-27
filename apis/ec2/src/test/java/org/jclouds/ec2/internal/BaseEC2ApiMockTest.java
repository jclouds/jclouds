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
package org.jclouds.ec2.internal;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.EC2ApiMetadata;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Tests need to run {@code singleThreaded = true) as otherwise tests will clash on the regionToServers field.
 * Sharing the regionToServers field means less code to write.
 */
public class BaseEC2ApiMockTest {
   protected static final String DEFAULT_REGION = "us-east-1";

   // Example keys from http://docs.aws.amazon.com/general/latest/gr/signature-version-2.html
   private static final String ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
   private static final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

   private Map<String, MockWebServer> regionToServers = Maps.newLinkedHashMap();

   protected EC2Api api() {
      return builder(new Properties()).buildApi(EC2Api.class);
   }

   protected ContextBuilder builder(Properties overrides) {
      overrides.setProperty(Constants.PROPERTY_MAX_RETRIES, "1");
      MockWebServer defaultServer = regionToServers.get(DEFAULT_REGION);
      return ContextBuilder.newBuilder(new EC2ApiMetadata())
            .credentials(ACCESS_KEY, SECRET_KEY)
            .endpoint(defaultServer.getUrl("").toString())
            .overrides(overrides)
            .modules(modules);
   }

   private final Set<Module> modules = ImmutableSet.<Module>of(new ExecutorServiceModule(sameThreadExecutor()));

   @BeforeMethod
   public void start() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      regionToServers.put(DEFAULT_REGION, server);
   }

   @AfterMethod(alwaysRun = true)
   public void stop() throws IOException {
      for (MockWebServer server : regionToServers.values()) {
         server.shutdown();
      }
   }

   protected void enqueue(String region, MockResponse response) {
      regionToServers.get(region).enqueue(response);
   }

   protected void enqueueRegions(String... regions) throws IOException {
      StringBuilder describeRegionsResponse = new StringBuilder();
      describeRegionsResponse.append("<DescribeRegionsResponse>");
      for (String region : regions) {
         describeRegionsResponse.append("<item>");
         describeRegionsResponse.append("<regionName>").append(region).append("</regionName>");
         if (!regionToServers.containsKey(region)) {
            MockWebServer server = new MockWebServer();
            server.play();
            regionToServers.put(region, server);
         }
         MockWebServer server = regionToServers.get(region);
         String regionEndpoint = server.getUrl("").toString();
         describeRegionsResponse.append("<regionEndpoint>").append(regionEndpoint).append("</regionEndpoint>");
         describeRegionsResponse.append("</item>");
      }
      describeRegionsResponse.append("</DescribeRegionsResponse>");
      enqueue(DEFAULT_REGION,
            new MockResponse().addHeader(CONTENT_TYPE, APPLICATION_XML).setBody(describeRegionsResponse.toString()));
   }

   protected void enqueueXml(String region, String resource) {
      enqueue(region,
            new MockResponse().addHeader(CONTENT_TYPE, APPLICATION_XML).setBody(stringFromResource(resource)));
   }

   protected String stringFromResource(String resourceName) {
      try {
         return toStringAndClose(getClass().getResourceAsStream(resourceName));
      } catch (IOException e) {
         throw propagate(e);
      }
   }

   /** Stripping out authorization, ensures the following post params were sent. */
   protected RecordedRequest assertPosted(String region, String postParams) throws InterruptedException {
      RecordedRequest request = regionToServers.get(region).takeRequest();
      assertEquals(request.getMethod(), "POST");
      assertEquals(request.getPath(), "/");
      assertEquals(new String(request.getBody(), Charsets.UTF_8).replaceAll("&Signature.*", ""), postParams);
      return request;
   }
}
