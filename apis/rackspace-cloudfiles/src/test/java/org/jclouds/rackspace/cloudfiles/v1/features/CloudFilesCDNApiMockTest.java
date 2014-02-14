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
package org.jclouds.rackspace.cloudfiles.v1.features;

import static com.google.common.base.Charsets.US_ASCII;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.ETAG;
import static javax.ws.rs.core.HttpHeaders.LAST_MODIFIED;
import static org.jclouds.rackspace.cloudfiles.v1.options.UpdateCDNContainerOptions.Builder.enabled;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_ENABLED;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_IOS_URI;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_LOG_RETENTION;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_SSL_URI;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_STREAMING_URI;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_TTL;
import static org.jclouds.rackspace.cloudfiles.v1.reference.CloudFilesHeaders.CDN_URI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;
import org.jclouds.rackspace.cloudfiles.v1.domain.CDNContainer;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "CloudFilesCDNApiMockTest")
public class CloudFilesCDNApiMockTest extends BaseOpenStackMockTest<CloudFilesApi> {

   List<String> emails = ImmutableList.of("foo@bar.com", "bar@foo.com");

   public void testList() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/cdn_container_list.json"))));

      try {
         CloudFilesApi api = api(server.getUrl("/").toString(), "rackspace-cloudfiles");
         CDNApi cdnApi = api.cdnApiInRegion("DFW");

         ImmutableList<CDNContainer> cdnContainers = cdnApi.list().toList();

         assertEquals(cdnContainers, mockContainers);

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/?format=json&enabled_only=true");
      } finally {
         server.shutdown();
      }
   }

   public void testListWithOptions() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/cdn_container_list_at.json"))));

      try {
         CloudFilesApi api = api(server.getUrl("/").toString(), "rackspace-cloudfiles");
         ListContainerOptions options = ListContainerOptions.Builder.marker("cdn-container-3");
         ImmutableList<CDNContainer> containers = api.cdnApiInRegion("DFW").list(options).toList();
         
         for(CDNContainer container : containers) {
            checkCDNContainer(container);
         }
         
         assertEquals(containers, mockContainers.subList(2, mockContainers.size()));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/?format=json&enabled_only=true&marker=cdn-container-3");
      } finally {
         server.shutdown();
      }
   }

   public void testEnableAndDisable() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(enabledResponse().setResponseCode(201)));
      server.enqueue(addCommonHeaders(enabledResponse().setResponseCode(201)));
      server.enqueue(addCommonHeaders(disabledResponse().setResponseCode(201)));
      
      try {
         CloudFilesApi api = api(server.getUrl("/").toString(), "rackspace-cloudfiles");
         CDNApi cdnApi = api.cdnApiInRegion("DFW");
         
         // enable a CDN Container
         URI enabledContainer = cdnApi.enable("container-1");
         assertNotNull(enabledContainer);
         
         // ensure that it is disabled
         assertTrue(cdnApi.disable("container-1"));
         
         // get the container from the CDN and  ensure that it is disabled
         CDNContainer disabledContainer = cdnApi.get("container-1");
         checkCDNContainer(disabledContainer);
         assertFalse(disabledContainer.isEnabled());
         
         assertEquals(server.getRequestCount(), 4);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container-1");
         assertRequest(server.takeRequest(), "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container-1");
         assertRequest(server.takeRequest(), "HEAD", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container-1");
      } finally {
         server.shutdown();
      }
   }

   public void testEnableWithTTL() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(enabledResponse().setResponseCode(201)));
      
      try {
         CloudFilesApi api = api(server.getUrl("/").toString(), "rackspace-cloudfiles");
         CDNApi cdnApi = api.cdnApiInRegion("DFW");
         
         // enable a CDN Container with a TTL
         URI enabledContainer = cdnApi.enable("container-1", 777777);
         assertNotNull(enabledContainer);

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container-1");
      } finally {
         server.shutdown();
      }
   }

   public void testGet() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(enabledResponse().setResponseCode(201)));
      
      try {
         CloudFilesApi api = api(server.getUrl("/").toString(), "rackspace-cloudfiles");
         CDNApi cdnApi = api.cdnApiInRegion("DFW");

         CDNContainer cdnContainer = cdnApi.get("container-1");
         checkCDNContainer(cdnContainer);
         assertEquals(mockCDNContainer, cdnContainer);

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "HEAD", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container-1");
      } finally {
         server.shutdown();
      }
   }
   
   public void testGetFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));
      
      try {
         CloudFilesApi api = api(server.getUrl("/").toString(), "rackspace-cloudfiles");
         CDNApi cdnApi = api.cdnApiInRegion("DFW");

         CDNContainer cdnContainer = cdnApi.get("container-1");

         assertAuthentication(server);
         assertRequest(server.takeRequest(), "HEAD", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container-1");
         assertNull(cdnContainer);
      } finally {
         server.shutdown();
      }
   }

   public void testPurgeObject() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));
      
      try {
         CloudFilesApi api = api(server.getUrl("/").toString(), "rackspace-cloudfiles");
         CDNApi cdnApi = api.cdnApiInRegion("DFW");
         
         // purge the object
         assertTrue(cdnApi.purgeObject("myContainer", "myObject", emails));
         
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdate() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json")).setResponseCode(200))); //POST
      server.enqueue(addCommonHeaders(enabledResponse().setResponseCode(200)));
      server.enqueue(addCommonHeaders(updatedResponse().setResponseCode(204)));
      server.enqueue(addCommonHeaders(updatedResponse().setResponseCode(200)));
      
      try {
         CloudFilesApi api = api(server.getUrl("/").toString(), "rackspace-cloudfiles");
         CDNApi cdnApi = api.cdnApiInRegion("DFW"); 
         
         CDNContainer cdnContainer = cdnApi.get("container-1");
         checkCDNContainer(cdnContainer);
         
         // update the CDN Container
         assertTrue(cdnApi.update("container-1", enabled(false).logRetention(true).ttl(7654321)));
         
         cdnContainer = cdnApi.get("container-1");
         checkCDNContainer(cdnContainer);
         
         CDNContainer updatedContainer = CDNContainer.builder()
               .name("container-1")
               .enabled(false)
               .logRetention(true)
               .ttl(7654321)
               .uri(URI.create("http://id-1.cdn.rackspace.com"))
               .sslUri(URI.create("https://ssl-id-1.ssl.rackspace.com"))
               .streamingUri(URI.create("http://streaming-id-1.stream.rackspace.com"))
               .iosUri(URI.create("http://ios-id-1.iosr.rackspace.com"))
               .build();
         
         assertEquals(updatedContainer, cdnContainer);
         
         assertEquals(server.getRequestCount(), 4);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "HEAD", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container-1");
         assertRequest(server.takeRequest(), "POST", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container-1");
         assertRequest(server.takeRequest(), "HEAD", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container-1");
      } finally {
         server.shutdown();
      }
   }

   private static final void checkCDNContainer(CDNContainer cdnContainer) {
      assertNotNull(cdnContainer.getName());
      assertNotNull(cdnContainer.isEnabled());
      assertNotNull(cdnContainer.isLogRetentionEnabled());
      assertNotNull(cdnContainer.getTtl());
      assertNotNull(cdnContainer.getUri());
      assertNotNull(cdnContainer.getSslUri());
      assertNotNull(cdnContainer.getStreamingUri());
      assertNotNull(cdnContainer.getIosUri());
   }

   private static final CDNContainer mockCDNContainer = CDNContainer.builder()
         .name("container-1")
         .enabled(true)
         .logRetention(false)
         .ttl(777777)
         .uri(URI.create("http://id-1.cdn.rackspace.com"))
         .sslUri(URI.create("https://ssl-id-1.ssl.rackspace.com"))
         .streamingUri(URI.create("http://streaming-id-1.stream.rackspace.com"))
         .iosUri(URI.create("http://ios-id-1.iosr.rackspace.com"))
         .build();

   private static MockResponse enabledResponse() {
      return new MockResponse()
            .addHeader(CDN_ENABLED, "true")
            .addHeader(CDN_LOG_RETENTION, "false")
            .addHeader(CDN_TTL, "777777")
            .addHeader(CDN_URI,"http://id-1.cdn.rackspace.com")
            .addHeader(CDN_SSL_URI, "https://ssl-id-1.ssl.rackspace.com")
            .addHeader(CDN_STREAMING_URI, "http://streaming-id-1.stream.rackspace.com")
            .addHeader(CDN_IOS_URI, "http://ios-id-1.iosr.rackspace.com")
            .addHeader(CONTENT_LENGTH, "0")
            .addHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
   }
   
   private static MockResponse disabledResponse() {
      return new MockResponse()
            .addHeader(CDN_ENABLED, "false")
            .addHeader(CDN_LOG_RETENTION, "false")
            .addHeader(CDN_TTL, "777777")
            .addHeader(CDN_URI,"http://id-1.cdn.rackspace.com")
            .addHeader(CDN_SSL_URI, "https://ssl-id-1.ssl.rackspace.com")
            .addHeader(CDN_STREAMING_URI, "http://streaming-id-1.stream.rackspace.com")
            .addHeader(CDN_IOS_URI, "http://ios-id-1.iosr.rackspace.com")
            .addHeader(CONTENT_LENGTH, "0")
            .addHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
   }

   private static MockResponse updatedResponse() {
      return new MockResponse()
            .addHeader(CDN_ENABLED, "false")
            .addHeader(CDN_LOG_RETENTION, "true")
            .addHeader(CDN_TTL, "7654321")
            .addHeader(CDN_URI,"http://id-1.cdn.rackspace.com")
            .addHeader(CDN_SSL_URI, "https://ssl-id-1.ssl.rackspace.com")
            .addHeader(CDN_STREAMING_URI, "http://streaming-id-1.stream.rackspace.com")
            .addHeader(CDN_IOS_URI, "http://ios-id-1.iosr.rackspace.com")
            .addHeader(CONTENT_LENGTH, "0")
            .addHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
   }

   private static final ImmutableList<CDNContainer> mockContainers = ImmutableList.of(
         CDNContainer.builder()
               .name("cdn-container-1")
               .enabled(true)
               .logRetention(false)
               .ttl(259200)
               .uri(URI.create("http://id-1.cdn.rackspace.com"))
               .sslUri(URI.create("https://ssl-id-1.ssl.rackspace.com"))
               .streamingUri(URI.create("http://streaming-id-1.stream.rackspace.com"))
               .iosUri(URI.create("http://ios-id-1.iosr.rackspace.com"))
               .build(),
         CDNContainer.builder()
               .name("cdn-container-2")
               .enabled(true)
               .logRetention(true)
               .ttl(259200)
               .uri(URI.create("http://id-2.cdn.rackspace.com"))
               .sslUri(URI.create("https://ssl-id-2.ssl.rackspace.com"))
               .streamingUri(URI.create("http://streaming-id-2.stream.rackspace.com"))
               .iosUri(URI.create("http://ios-id-2.iosr.rackspace.com"))
               .build(),
         CDNContainer.builder()
               .name("cdn-container-3")
               .enabled(true)
               .logRetention(false)
               .ttl(259200)
               .uri(URI.create("http://id-3.cdn.rackspace.com"))
               .sslUri(URI.create("https://ssl-id-3.ssl.rackspace.com"))
               .streamingUri(URI.create("http://streaming-id-3.stream.rackspace.com"))
               .iosUri(URI.create("http://ios-id-3.iosr.rackspace.com"))
               .build(),
         CDNContainer.builder()
               .name("cdn-container-4")
               .enabled(true)
               .logRetention(true)
               .ttl(777777)
               .uri(URI.create("http://id-4.cdn.rackspace.com"))
               .sslUri(URI.create("https://ssl-id-4.ssl.rackspace.com"))
               .streamingUri(URI.create("http://streaming-id-4.stream.rackspace.com"))
               .iosUri(URI.create("http://ios-id-4.iosr.rackspace.com"))
               .build());

   private static MockResponse objectResponse() {
      return new MockResponse()
            .addHeader(LAST_MODIFIED, "Fri, 12 Jun 2010 13:40:18 GMT")
            .addHeader(ETAG, "8a964ee2a5e88be344f36c22562a6486")
            // TODO: MWS doesn't allow you to return content length w/o content
            // on HEAD!
            .setBody("ABCD".getBytes(US_ASCII))
            .addHeader(CONTENT_LENGTH, "4").addHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
   }

   private static final byte[] NO_CONTENT = new byte[] {};
   
   private static Payload payload(long bytes, String contentType) {
      Payload payload = Payloads.newByteArrayPayload(NO_CONTENT);
      payload.getContentMetadata().setContentLength(bytes);
      payload.getContentMetadata().setContentType(contentType);
      return payload;
   }
}
