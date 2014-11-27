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
package org.jclouds.http;

import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.Files.asByteSource;
import static org.jclouds.http.options.GetOptions.Builder.tail;
import static org.jclouds.io.Payloads.newByteSourcePayload;
import static org.jclouds.util.Closeables2.closeQuietly;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.jclouds.io.ByteStreams2;
import org.jclouds.io.Payload;
import org.jclouds.util.Strings2;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Tests for functionality all {@link HttpCommandExecutorService} http executor
 * services must express. These tests will operate against an in-memory http
 * engine, so as to ensure end-to-end functionality works.
 */
@Test(groups = "integration")
public abstract class BaseHttpCommandExecutorServiceIntegrationTest extends BaseMockWebServerTest {

   private static final String XML = "<foo><bar>whoppers</bar></foo>";
   private static final String XML2 = "<foo><bar>chubbs</bar></foo>";
   private static final ByteSource oneHundredOneConstitutions = TestUtils.randomByteSource().slice(0, 101 * 45118);

   @BeforeClass(groups = "integration")
   public void setup() throws IOException {
   }

   protected IntegrationTestClient client(String url) {
      return api(IntegrationTestClient.class, url);
   }

   @Test
   public void testRequestFilter() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("test"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.downloadFilter("", "filterme");

         RecordedRequest request = server.takeRequest();
         assertEquals(request.getHeader("filterme"), "filterme");
         assertEquals(request.getHeader("test"), "test");
         assertEquals(result, "test");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testGetStringWithHeader() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("test"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.download("", "test");

         RecordedRequest request = server.takeRequest();
         assertEquals(request.getHeader("test"), "test");
         assertEquals(result, "test");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testGetString() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(XML));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         assertEquals(client.download(""), XML);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testGetStringIsRetriedOnFailure() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(500), new MockResponse().setBody(XML));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.download("");
         assertEquals(server.getRequestCount(), 2);
         assertEquals(result, XML);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testGetStringViaRequest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(XML));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         HttpResponse getStringResponse = client.invoke(HttpRequest.builder().method("GET")
               .endpoint(server.getUrl("/objects").toString()).build());
         assertEquals(Strings2.toStringAndClose(getStringResponse.getPayload().openStream()).trim(), XML);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @DataProvider(name = "gets")
   public Object[][] createData() {
      return new Object[][] { { "object" }, { "/path" }, { "sp ace" }, { "unic₪de" }, { "qu?stion" } };
   }

   @Test(dataProvider = "gets")
   public void testGetStringSynch(String uri) throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(XML));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.synch(uri);
         RecordedRequest request = server.takeRequest();
         assertTrue(URLDecoder.decode(request.getPath(), "UTF-8").endsWith(uri));
         assertEquals(result, XML);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testGetException() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.downloadException("", tail(1));
         assertEquals(result, "foo");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testGetSynchException() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.synchException("", "");
         assertEquals(result, "foo");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testGetStringRedirect() throws Exception {
      MockWebServer redirectTarget = mockWebServer(new MockResponse().setBody(XML2));
      redirectTarget.useHttps(sslContext.getSocketFactory(), false);
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(302).setHeader("Location",
            redirectTarget.getUrl("/").toString()));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.download("redirect");
         assertEquals(result, XML2);
         assertEquals(server.getRequestCount(), 1);
         assertEquals(redirectTarget.getRequestCount(), 1);
      } finally {
         closeQuietly(client);
         redirectTarget.shutdown();
         server.shutdown();
      }
   }

   @Test
   public void testGetBigFile() throws Exception {
      String constitutionsMd5 = base64().encode(oneHundredOneConstitutions.hash(md5()).asBytes());
      MockResponse response = new MockResponse().addHeader("Content-MD5", constitutionsMd5)
            .addHeader("Content-type", "text/plain")
            .setBody(oneHundredOneConstitutions.openStream(), oneHundredOneConstitutions.size());

      MockWebServer server = mockWebServer(response, response);
      InputStream input = server.getUrl("/101constitutions").openStream();

      try {
         assertValidMd5(input, constitutionsMd5);
      } catch (RuntimeException e) {
      } finally {
         closeQuietly(input);
      }
   }

   private void assertValidMd5(final InputStream input, String md5) throws IOException {
      assertEquals(base64().encode(ByteStreams2.hashAndClose(input, md5()).asBytes()), md5);
   }

   private static class MD5CheckDispatcher extends Dispatcher {

      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
         try {
            MockResponse response = new MockResponse();
            String expectedMd5 = request.getHeader("Content-MD5");
            ByteSource body = ByteSource.wrap(request.getBody());
            String realMd5FromRequest = base64().encode(body.hash(md5()).asBytes());
            boolean matched = expectedMd5.equals(realMd5FromRequest);
            if (matched) {
               response.addHeader("x-Content-MD5", realMd5FromRequest);
            } else {
               response.setResponseCode(500);
            }
            return response;
         } catch (IOException ex) {
            throw Throwables.propagate(ex);
         }
      }

   }

   @Test
   public void testUploadBigFile() throws Exception {
      MockWebServer server = mockWebServer(new MD5CheckDispatcher());
      IntegrationTestClient client = client(server.getUrl("/").toString());

      File f = null;
      Payload payload = null;

      try {
         f = File.createTempFile("jclouds", "tmp");
         long length = (new Random().nextInt(32) + 1) * 1024 * 1024;
         TestUtils.randomByteSource().slice(0, length).copyTo(Files.asByteSink(f));

         ByteSource byteSource = asByteSource(f);
         payload = newByteSourcePayload(byteSource);
         byte[] digest = byteSource.hash(md5()).asBytes();
         String strDigest = base64().encode(digest);

         payload.getContentMetadata().setContentMD5(digest);
         payload.getContentMetadata().setContentLength(f.length());
         Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getHeader("Content-MD5"), strDigest);
         assertEquals(headers.get("x-Content-MD5"), ImmutableList.of(strDigest));
      } finally {
         if (payload != null) {
            payload.release();
         }
         if (f != null && f.exists()) {
            f.delete();
         }
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPost() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("fooPOST"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.post("", "foo");
         // Verify that the body is properly populated
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
         assertEquals(result, "fooPOST");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testZeroLengthPost() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse());
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         client.postNothing("");
         assertEquals(server.getRequestCount(), 1);
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPostIsRetriedOnFailure() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(500),
            new MockResponse().setBody("fooPOST"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.post("", "foo");
         assertEquals(server.getRequestCount(), 2);
         assertEquals(result, "fooPOST");
         // Verify that the body was properly sent in the two requests
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
         request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPostRedirect() throws Exception {
      MockWebServer redirectTarget = mockWebServer(new MockResponse().setBody("fooPOSTREDIRECT"));
      redirectTarget.useHttps(sslContext.getSocketFactory(), false);
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(302).setHeader("Location",
            redirectTarget.getUrl("/").toString()));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.post("redirect", "foo");
         assertEquals(result, "fooPOSTREDIRECT");
         assertEquals(server.getRequestCount(), 1);
         assertEquals(redirectTarget.getRequestCount(), 1);
         // Verify that the body was populated after the redirect
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
         request = redirectTarget.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
      } finally {
         closeQuietly(client);
         redirectTarget.shutdown();
         server.shutdown();
      }
   }

   @Test
   public void testPostAsInputStream() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("fooPOST"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.postAsInputStream("", "foo");
         // Verify that the body is properly populated
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
         assertEquals(result, "fooPOST");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPostAsInputStreamDoesNotRetryOnFailure() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(500), new MockResponse());
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         client.postAsInputStream("", "foo");
         fail("Request should have thrown an exception after a server error");
      } catch (Exception expected) {
         assertEquals(server.getRequestCount(), 1);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPostBinder() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("fooPOSTJSON"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.postJson("", "foo");
         // Verify that the body is properly populated
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "{\"key\":\"foo\"}");
         assertEquals(result, "fooPOSTJSON");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPostContentDisposition() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().addHeader("x-Content-Disposition",
            "attachment; filename=photo.jpg"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      Payload payload = null;
      try {
         ByteSource body = ByteSource.wrap("foo".getBytes());
         payload = newByteSourcePayload(body);
         payload.getContentMetadata().setContentDisposition("attachment; filename=photo.jpg");
         payload.getContentMetadata().setContentLength(body.size());
         Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getHeader("Content-Disposition"), "attachment; filename=photo.jpg");
         assertEquals(headers.get("x-Content-Disposition"), ImmutableList.of("attachment; filename=photo.jpg"));
      } finally {
         if (payload != null) {
            payload.release();
         }
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPostContentEncoding() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().addHeader("x-Content-Encoding", "gzip"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      Payload payload = null;
      try {
         ByteSource body = ByteSource.wrap("foo".getBytes());
         payload = newByteSourcePayload(body);
         payload.getContentMetadata().setContentEncoding("gzip");
         payload.getContentMetadata().setContentLength(body.size());
         Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getHeader("Content-Encoding"), "gzip");
         assertEquals(headers.get("x-Content-Encoding"), ImmutableList.of("gzip"));
      } finally {
         if (payload != null) {
            payload.release();
         }
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPostContentLanguage() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().addHeader("x-Content-Language", "mi, en"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      Payload payload = null;
      try {
         ByteSource body = ByteSource.wrap("foo".getBytes());
         payload = newByteSourcePayload(body);
         payload.getContentMetadata().setContentLanguage("mi, en");
         payload.getContentMetadata().setContentLength(body.size());
         Multimap<String, String> headers = client.postPayloadAndReturnHeaders("", payload);
         RecordedRequest request = server.takeRequest();
         assertEquals(request.getHeader("Content-Language"), "mi, en");
         assertEquals(headers.get("x-Content-Language"), ImmutableList.of("mi, en"));
      } finally {
         if (payload != null) {
            payload.release();
         }
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPut() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("fooPUT"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.upload("", "foo");
         // Verify that the body is properly populated
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
         assertEquals(result, "fooPUT");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPutRedirect() throws Exception {
      MockWebServer redirectTarget = mockWebServer(new MockResponse().setBody("fooPUTREDIRECT"));
      redirectTarget.useHttps(sslContext.getSocketFactory(), false);
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(302).setHeader("Location",
            redirectTarget.getUrl("/").toString()));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.upload("redirect", "foo");
         assertEquals(result, "fooPUTREDIRECT");
         assertEquals(server.getRequestCount(), 1);
         assertEquals(redirectTarget.getRequestCount(), 1);
         // Verify that the body was populated after the redirect
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
         request = redirectTarget.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
      } finally {
         closeQuietly(client);
         redirectTarget.shutdown();
         server.shutdown();
      }
   }

   @Test
   public void testZeroLengthPut() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse());
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         client.putNothing("");
         assertEquals(server.getRequestCount(), 1);
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testPutIsRetriedOnFailure() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(500),
            new MockResponse().setBody("fooPUT"));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.upload("", "foo");
         assertEquals(server.getRequestCount(), 2);
         assertEquals(result, "fooPUT");
         // Verify that the body was properly sent in the two requests
         RecordedRequest request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
         request = server.takeRequest();
         assertEquals(new String(request.getBody(), Charsets.UTF_8), "foo");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testHead() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse());
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         assertTrue(client.exists(""));
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testHeadIsRetriedOnServerError() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(500), new MockResponse());
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         assertTrue(client.exists(""));
         assertEquals(server.getRequestCount(), 2);
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testHeadFailure() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         assertFalse(client.exists(""));
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testGetAndParseSax() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(XML));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         String result = client.downloadAndParse("");
         assertEquals(result, "whoppers");
      } finally {
         closeQuietly(client);
         server.shutdown();
      }
   }

   @Test
   public void testInterruptThrottledGet() throws Exception {
      long timeoutMillis = 10 * 1000;
      MockWebServer server = mockWebServer(new MockResponse().setBody(XML).throttleBody(XML.length() / 2, timeoutMillis, TimeUnit.MILLISECONDS));
      IntegrationTestClient client = client(server.getUrl("/").toString());
      try {
         HttpResponse response = client.invoke(HttpRequest.builder()
            .method("GET")
            .endpoint(server.getUrl("/").toURI())
            .build());
         InputStream is = response.getPayload().openStream();
         long now = System.currentTimeMillis();
         is.close();
         long diff = System.currentTimeMillis() - now;
         assertTrue(diff < timeoutMillis / 2, "expected " + diff + " to be less than " + (timeoutMillis / 2));
      } finally {
         closeQuietly(client);
         try {
            server.shutdown();
         } catch (IOException ex) {
            // MockWebServer 2.1.0 introduces an active wait for its executor termination.
            // That active wait is a hardcoded value and throws an IOE if the executor has not
            // terminated in that timeout. It is safe to ignore this exception (related to how
            // throttling works internally in MWS), as the functionality has been properly verified.
         }
      }
   }
}
