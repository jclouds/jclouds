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
package org.jclouds.openstack.swift.v1.features;

import static com.google.common.base.Charsets.US_ASCII;
import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.net.HttpHeaders.EXPIRES;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_RETRY_DELAY_START;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.http.options.GetOptions.Builder.tail;
import static org.jclouds.io.Payloads.newByteSourcePayload;
import static org.jclouds.openstack.swift.v1.features.ContainerApiMockTest.containerResponse;
import static org.jclouds.openstack.swift.v1.options.ListContainerOptions.Builder.marker;
import static org.jclouds.openstack.swift.v1.options.PutOptions.Builder.metadata;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_ACL_ANYBODY_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_METADATA_PREFIX;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_REMOVE_METADATA_PREFIX;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.jclouds.openstack.swift.v1.CopyObjectException;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.openstack.swift.v1.reference.SwiftHeaders;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Provides mock tests for the {@link ObjectApi}.
 */
@Test(groups = "unit", testName = "ObjectApiMockTest")
public class ObjectApiMockTest extends BaseOpenStackMockTest<SwiftApi> {
   SimpleDateFormatDateService dates = new SimpleDateFormatDateService();

   static final Payload PAYLOAD = newByteSourcePayload(ByteSource.wrap("swifty".getBytes()));

   protected ImmutableList<SwiftObject> parsedObjectsForUrl(String baseUri) {
      baseUri += "v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer";
      return ImmutableList.of(
            SwiftObject.builder()
                  .name("test_obj_1")
                  .uri(URI.create(baseUri + "/test_obj_1"))
                  .etag("4281c348eaf83e70ddce0e07221c3d28")
                  .payload(payload(14, "application/octet-stream", new Date(1406243553)))
                  .lastModified(dates.iso8601DateParse("2009-02-03T05:26:32.612278")).build(),
            SwiftObject.builder()
                  .name("test_obj_2")
                  .uri(URI.create(baseUri + "/test_obj_2"))
                  .etag("b039efe731ad111bc1b0ef221c3849d0")
                  .payload(payload(64l, "application/octet-stream", null))
                  .lastModified(dates.iso8601DateParse("2009-02-03T05:26:32.612278")).build(),
            SwiftObject.builder()
                  .name("test obj 3")
                  .uri(URI.create(baseUri + "/test%20obj%203"))
                  .etag("0b2e80bd0744d9ebb20484149a57c82e")
                  .payload(payload(14, "application/octet-stream", new Date()))
                  .lastModified(dates.iso8601DateParse("2014-05-20T05:26:32.612278")).build());
   }

   public void testList() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(containerResponse()
            .addHeader(CONTAINER_READ, CONTAINER_ACL_ANYBODY_READ)
            .setBody(stringFromResource("/object_list.json"))));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         ObjectList objects = api.getObjectApi("DFW", "myContainer").list();
         assertEquals(objects, parsedObjectsForUrl(server.getUrl("/").toString()));
         assertEquals(objects.getContainer().getName(), "myContainer");
         assertTrue(objects.getContainer().getAnybodyRead().get());

         // Check MD5 is parsed from the ETag header.
         SwiftObject object1 = objects.get(1);
         assertEquals(base16().lowerCase().decode(object1.getETag()),
               object1.getPayload().getContentMetadata().getContentMD5AsHashCode().asBytes());

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer");
      } finally {
         server.shutdown();
      }
   }

   public void testListWithOptions() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(containerResponse()
            .addHeader(CONTAINER_READ, CONTAINER_ACL_ANYBODY_READ)
            .setBody(stringFromResource("/object_list.json"))));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         ObjectList objects = api.getObjectApi("DFW", "myContainer").list(new ListContainerOptions());
         assertEquals(objects, parsedObjectsForUrl(server.getUrl("/").toString()));
         assertEquals(objects.getContainer().getName(), "myContainer");
         assertTrue(objects.getContainer().getAnybodyRead().get());

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer");
      } finally {
         server.shutdown();
      }
   }

   public void testListOptions() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(containerResponse().setBody(stringFromResource("/object_list.json"))));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         ObjectList objects = api.getObjectApi("DFW", "myContainer").list(marker("test"));
         assertEquals(objects, parsedObjectsForUrl(server.getUrl("/").toString()));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer?marker=test");
      } finally {
         server.shutdown();
      }
   }

   public void testCreate() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse()
            .setResponseCode(201)
            .addHeader("ETag", "d9f5eb4bba4e2f2f046e54611bc8196b"))
            .addHeader("Expires", "1406243553"));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertEquals(
               api.getObjectApi("DFW", "myContainer").put("myObject", PAYLOAD,
                     metadata(metadata)), "d9f5eb4bba4e2f2f046e54611bc8196b");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         RecordedRequest replace = server.takeRequest();
         assertRequest(replace, "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject");

         assertEquals(new String(replace.getBody()), "swifty");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replace.getHeader(OBJECT_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWithSpacesAndSpecialCharacters() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).addHeader("ETag", "d9f5eb4bba4e2f2f046e54611bc8196b")));

      final String containerName = "container # ! special";
      final String objectName = "object # ! special";

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertEquals(
               api.getObjectApi("DFW", containerName).put(objectName, PAYLOAD,
                     metadata(metadata)), "d9f5eb4bba4e2f2f046e54611bc8196b");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         RecordedRequest replace = server.takeRequest();
         assertRequest(replace, "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container%20%23%20%21%20special/object%20%23%20%21%20special");

         assertEquals(new String(replace.getBody()), "swifty");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWith408Retry() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(408))); // 1
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(408))); // 2
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(408))); // 3

      // Finally success
      server.enqueue(addCommonHeaders(new MockResponse()
            .setResponseCode(201)
            .addHeader("ETag", "d9f5eb4bba4e2f2f046e54611bc8196b")));

      try {
         Properties overrides = new Properties();
         overrides.setProperty(PROPERTY_MAX_RETRIES, 5 + "");

         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift", overrides);
         assertEquals(
               api.getObjectApi("DFW", "myContainer").put("myObject", PAYLOAD,
                     metadata(metadata)), "d9f5eb4bba4e2f2f046e54611bc8196b");

         assertEquals(server.getRequestCount(), 5);
         assertAuthentication(server);
         RecordedRequest replace = server.takeRequest();
         // This should take a while.
         assertRequest(replace, "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject");

         assertEquals(new String(replace.getBody()), "swifty");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replace.getHeader(OBJECT_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   /** upper-cases first char, and lower-cases rest!! **/
   public void testGetWithoutKnowingServerMessesWithMetadataKeyCaseFormat() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()
            // note silly casing
            .addHeader(OBJECT_METADATA_PREFIX + "Apiname", "swift")
            .addHeader(OBJECT_METADATA_PREFIX + "Apiversion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         SwiftObject object = api.getObjectApi("DFW", "myContainer").getWithoutBody("myObject");
         assertEquals(object.getName(), "myObject");
         assertEquals(object.getETag(), "8a964ee2a5e88be344f36c22562a6486");

         // Check MD5 is parsed from the ETag header.
         assertEquals(base16().lowerCase().decode(object.getETag()),
               object.getPayload().getContentMetadata().getContentMD5AsHashCode().asBytes());

         assertEquals(object.getLastModified(), dates.rfc822DateParse("Fri, 12 Jun 2010 13:40:18 GMT"));
         for (Entry<String, String> entry : object.getMetadata().entrySet()) {
            assertEquals(object.getMetadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }
         assertEquals(object.getPayload().getContentMetadata().getContentType(), "text/plain; charset=UTF-8");
         assertEquals(toStringAndClose(object.getPayload().openStream()), "");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "HEAD",
               "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject");
      } finally {
         server.shutdown();
      }
   }

   public void testGet() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()
            // note silly casing
            .addHeader(OBJECT_METADATA_PREFIX + "Apiname", "swift")
            .addHeader(OBJECT_METADATA_PREFIX + "Apiversion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         SwiftObject object = api.getObjectApi("DFW", "myContainer").get("myObject", tail(1));
         assertEquals(object.getName(), "myObject");
         assertEquals(object.getETag(), "8a964ee2a5e88be344f36c22562a6486");
         assertEquals(object.getLastModified(), dates.rfc822DateParse("Fri, 12 Jun 2010 13:40:18 GMT"));
         for (Entry<String, String> entry : object.getMetadata().entrySet()) {
            assertEquals(object.getMetadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }

         Payload payload = object.getPayload();
         assertEquals(payload.getContentMetadata().getContentLength(), Long.valueOf(4));
         assertEquals(payload.getContentMetadata().getContentType(), "text/plain; charset=UTF-8");
         assertEquals(payload.getContentMetadata().getExpires(), dates.rfc822DateParse("Wed, 23 Jul 2014 14:00:00 GMT"));

         assertEquals(toStringAndClose(payload.openStream()), "ABCD");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest get = server.takeRequest();
         assertEquals(get.getRequestLine(),
               "GET /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = HttpResponseException.class, timeOut = 20000)
   public void testCreateWithTimeout() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      // Typically we would enqueue a response for the put. However, in this case, test the timeout by not providing one.

      try {
         Properties overrides = new Properties();

         overrides.setProperty(PROPERTY_SO_TIMEOUT, 5000 + ""); // This time-outs the connection
         overrides.setProperty(PROPERTY_MAX_RETRIES, 0 + ""); // 0 retries == 1 try. Semantics.
         overrides.setProperty(PROPERTY_RETRY_DELAY_START, 0 + ""); // exponential backoff already working for this call. This is the delay BETWEEN attempts.

         final SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift", overrides);

         api.getObjectApi("DFW", "myContainer").put("myObject", new ByteSourcePayload(ByteSource.wrap("swifty".getBytes())), metadata(metadata));

         fail("testReplaceTimeout test should have failed with an HttpResponseException.");
      } finally {
         try {
            server.shutdown();
         } catch (IOException e) {
            // MockWebServer 2.1.0 introduces an active wait for its executor termination.
            // That active wait is a hardcoded value and throws an IOE if the executor has not
            // terminated in that timeout. It is safe to ignore this exception as the functionality
            // has been properly verified.
         }
      }
   }

   public void testUpdateMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()
            .addHeader(OBJECT_METADATA_PREFIX + "ApiName", "swift")
            .addHeader(OBJECT_METADATA_PREFIX + "ApiVersion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApi("DFW", "myContainer").updateMetadata("myObject", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replaceRequest.getHeader(OBJECT_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateMetadataContentType() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()
            .addHeader(OBJECT_METADATA_PREFIX + "ApiName", "swift")
            .addHeader(OBJECT_METADATA_PREFIX + "ApiVersion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApi("DFW", "myContainer").updateMetadata("myObject", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getHeaders("Content-Type").get(0), "", "updateMetadata should send an empty content-type header, but sent "
               + replaceRequest.getHeaders("Content-Type").get(0).toString());

         assertEquals(replaceRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replaceRequest.getHeader(OBJECT_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(objectResponse()));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApi("DFW", "myContainer").deleteMetadata("myObject", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
         for (String key : metadata.keySet()) {
            assertEquals(deleteRequest.getHeader(OBJECT_REMOVE_METADATA_PREFIX + key.toLowerCase()), "ignored");
         }
      } finally {
         server.shutdown();
      }
   }

   public void testDelete() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         api.getObjectApi("DFW", "myContainer").delete("myObject");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void testAlreadyDeleted() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         api.getObjectApi("DFW", "myContainer").delete("myObject");

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void testCopyObject() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)
            .addHeader(SwiftHeaders.OBJECT_COPY_FROM, "/bar/foo.txt")));
      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApi("DFW", "foo")
               .copy("bar.txt", "bar", "foo.txt"));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");

         RecordedRequest copyRequest = server.takeRequest();
         assertEquals(copyRequest.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/foo/bar.txt HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = CopyObjectException.class)
   public void testCopyObjectFail() throws InterruptedException, IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)
            .addHeader(SwiftHeaders.OBJECT_COPY_FROM, "/bogus/foo.txt")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         // the following line will throw the CopyObjectException
         api.getObjectApi("DFW", "foo").copy("bar.txt", "bogus", "foo.txt");
      } finally {
         server.shutdown();
      }
   }

   public void testCopyObjectWithMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)
            .addHeader(SwiftHeaders.OBJECT_COPY_FROM, "/bar/foo.txt")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApi("DFW", "foo")
               .copy("bar.txt", "bar", "foo.txt", ImmutableMap.of("someUserHeader", "someUserMetadataValue"),
                     ImmutableMap.of("Content-Disposition", "attachment; filename=\"fname.ext\"")));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");

         RecordedRequest copyRequest = server.takeRequest();
         assertEquals(copyRequest.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/foo/bar.txt HTTP/1.1");

         List<String> requestHeaders = copyRequest.getHeaders();
         assertTrue(requestHeaders.contains("X-Object-Meta-someuserheader: someUserMetadataValue"));
         assertTrue(requestHeaders.contains("content-disposition: attachment; filename=\"fname.ext\""));
         assertTrue(requestHeaders.contains(SwiftHeaders.OBJECT_COPY_FROM + ": /bar/foo.txt"));
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = CopyObjectException.class)
   public void testCopyObjectWithMetadataFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)
            .addHeader(SwiftHeaders.OBJECT_COPY_FROM, "/bar/foo.txt")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getObjectApi("DFW", "foo")
               .copy("bar.txt", "bar", "foo.txt", ImmutableMap.of("someUserHeader", "someUserMetadataValue"),
                     ImmutableMap.of("Content-Disposition", "attachment; filename=\"fname.ext\"")));
      } finally {
         server.shutdown();
      }
   }

   private static final Map<String, String> metadata = ImmutableMap.of("ApiName", "swift", "ApiVersion", "v1.1");

   static MockResponse objectResponse() {
      return new MockResponse()
            .addHeader("Last-Modified", "Fri, 12 Jun 2010 13:40:18 GMT")
            .addHeader("ETag", "8a964ee2a5e88be344f36c22562a6486")
            // TODO: MWS doesn't allow you to return content length w/o content
            // on HEAD!
            .setBody("ABCD".getBytes(US_ASCII))
            .addHeader("Content-Length", "4")
            .addHeader("Content-Type", "text/plain; charset=UTF-8")
            .addHeader(EXPIRES, "Wed, 23 Jul 2014 14:00:00 GMT");
   }

   static Payload payload(long bytes, String contentType, Date expires) {
      Payload payload = newByteSourcePayload(ByteSource.empty());
      payload.getContentMetadata().setContentLength(bytes);
      payload.getContentMetadata().setContentType(contentType);
      payload.getContentMetadata().setExpires(expires);
      return payload;
   }
}
