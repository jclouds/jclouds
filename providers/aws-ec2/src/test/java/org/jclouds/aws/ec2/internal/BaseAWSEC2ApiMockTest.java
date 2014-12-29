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
package org.jclouds.aws.ec2.internal;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.AWSEC2ProviderMetadata;
import org.jclouds.aws.ec2.config.AWSEC2HttpApiModule;
import org.jclouds.aws.filters.FormSignerV4.ServiceAndRegion;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.date.DateService;
import org.jclouds.rest.ConfiguresHttpApi;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Tests need to run {@code singleThreaded = true) as otherwise tests will clash on the regionToServers field.
 * Sharing the regionToServers field means less code to write.
 */
public class BaseAWSEC2ApiMockTest {
   protected static final String DEFAULT_REGION = "us-east-1";

   // Example keys from http://docs.aws.amazon.com/general/latest/gr/signature-version-2.html
   private static final String ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
   private static final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

   private Map<String, MockWebServer> regionToServers = Maps.newLinkedHashMap();

   protected AWSEC2Api api() {
      return builder(new Properties()).buildApi(AWSEC2Api.class);
   }

   protected ComputeService computeService() {
      return builder(new Properties()).buildView(ComputeServiceContext.class).getComputeService();
   }

   protected ContextBuilder builder(Properties overrides) {
      MockWebServer defaultServer = regionToServers.get(DEFAULT_REGION);
      overrides.setProperty(Constants.PROPERTY_MAX_RETRIES, "1");
      overrides.setProperty(ComputeServiceProperties.TIMEOUT_CLEANUP_INCIDENTAL_RESOURCES, "0");
      return ContextBuilder.newBuilder(new AWSEC2ProviderMetadata())
            .credentials(ACCESS_KEY, SECRET_KEY)
            .endpoint(defaultServer.getUrl("").toString())
            .overrides(overrides)
            .modules(modules);
   }

   private final Set<Module> modules = ImmutableSet
         .<Module>of(new MockAWSEC2HttpApiModule(), new ExecutorServiceModule(sameThreadExecutor()));

   @ConfiguresHttpApi
   class MockAWSEC2HttpApiModule extends AWSEC2HttpApiModule {

      @Override
      protected String provideTimeStamp(DateService dateService) {
         return "20120416T155408Z";
      }

      @Provides ServiceAndRegion serviceAndRegion(){
         return new ServiceAndRegion() {
            @Override public String service() {
               return "ec2";
            }

            @Override public String region(String host) {
               for (Map.Entry<String, MockWebServer> regionToServer : regionToServers.entrySet()) {
                  MockWebServer server = regionToServer.getValue();
                  if (host.equals(server.getHostName() + ":" + regionToServer.getValue().getPort())) {
                     return regionToServer.getKey();
                  }
               }
               throw new IllegalStateException(host + " not found");
            }
         };
      }
   }

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

   protected RecordedRequest assertPosted(String region, String postParams) throws InterruptedException {
      RecordedRequest request = regionToServers.get(region).takeRequest();
      assertEquals(request.getMethod(), "POST");
      assertEquals(request.getPath(), "/");
      assertEquals(request.getHeader("X-Amz-Date"), "20120416T155408Z");
      assertThat(
            request.getHeader(AUTHORIZATION)).startsWith("AWS4-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20120416/" +
            region + "/ec2/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=");
      String body = new String(request.getBody(), Charsets.UTF_8);
      assertThat(body).contains("&Version=2012-06-01");
      assertEquals(body.replace("&Version=2012-06-01", ""), postParams);
      return request;
   }
}
