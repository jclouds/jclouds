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
package org.jclouds.blobstore.integration.internal;

import static com.google.common.net.HttpHeaders.EXPECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.blobstore.options.GetOptions.Builder.range;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.Uninterruptibles;

/**
 * Tests integrated functionality of all signature commands.
 * <p/>
 * Each test uses a different container name, so it should be perfectly fine to run in parallel.
 */
@Test(groups = {"live"})
public class BaseBlobSignerLiveTest extends BaseBlobStoreIntegrationTest {
   protected int getSignedUrlTimeout() {
      return 60;
   }

   @Test
   public void testSignGetUrl() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         view.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = view.getSigner().signGetBlob(container, name);
         assertEquals(request.getFilters().size(), 0);
         assertEquals(Strings2.toStringAndClose(view.utils().http().invoke(request).getPayload().openStream()), text);
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignGetUrlOptions() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         view.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = view.getSigner().signGetBlob(container, name, range(0, 1));
         assertEquals(request.getFilters().size(), 0);
         assertEquals(Strings2.toStringAndClose(view.utils().http().invoke(request).getPayload().openStream()), "fo");
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignGetUrlWithTime() throws InterruptedException, IOException {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         view.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = view.getSigner().signGetBlob(container, name, getSignedUrlTimeout());
         assertEquals(request.getFilters().size(), 0);
         assertEquals(Strings2.toStringAndClose(view.utils().http().invoke(request).getPayload().openStream()), text);
      } finally {
         returnContainer(container);
      }
   }

   public void testSignGetUrlWithTime(final long timeout) throws InterruptedException, IOException {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         view.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = view.getSigner().signGetBlob(container, name, timeout);
         assertEquals(request.getFilters().size(), 0);

         try {
            Strings2.toStringAndClose(view.utils().http().invoke(request).getPayload().openStream());
            fail("Temporary URL did not expire as expected");
         } catch (AuthorizationException expected) {
         }
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignGetUrlWithTimeExpired() throws InterruptedException, IOException {
       testSignGetUrlWithTime(-getSignedUrlTimeout());
   }

   @Test
   public void testSignPutUrl() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("image/png").build();
      String container = getContainerName();
      try {
         HttpRequest request = view.getSigner().signPutBlob(container, blob);
         assertEquals(request.getFilters().size(), 0);
         Strings2.toStringAndClose(view.utils().http().invoke(request).getPayload().openStream());

         blob = view.getBlobStore().getBlob(container, name);
         assertThat(blob.getMetadata().getContentMetadata().getContentType()).isEqualTo("image/png");
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignPutUrlWithTime() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         HttpRequest request = view.getSigner().signPutBlob(container, blob, getSignedUrlTimeout());
         assertEquals(request.getFilters().size(), 0);

         // Strip Expect: 100-continue to make actual responses visible, since
         // Java 7+ will throw a ProtocolException instead of setting the response code:
         // http://www.docjar.com/html/api/sun/net/www/protocol/http/HttpURLConnection.java.html#1021
         request = request.toBuilder().removeHeader(EXPECT).build();
         Strings2.toStringAndClose(view.utils().http().invoke(request).getPayload().openStream());
      } finally {
         returnContainer(container);
      }
   }

   public void testSignPutUrlWithTime(final long timeout) throws InterruptedException, IOException {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         HttpRequest request = view.getSigner().signPutBlob(container, blob, 0);
         assertEquals(request.getFilters().size(), 0);

         // Strip Expect: 100-continue to make actual responses visible, since
         // Java 7+ will throw a ProtocolException instead of setting the response code:
         // http://www.docjar.com/html/api/sun/net/www/protocol/http/HttpURLConnection.java.html#1021
         request = request.toBuilder().removeHeader(EXPECT).build();

         try {
            Strings2.toStringAndClose(view.utils().http().invoke(request).getPayload().openStream());
            fail("Temporary URL did not expire as expected");
         } catch (AuthorizationException expected) {
         }
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignPutUrlWithTimeExpired() throws Exception {
       testSignPutUrlWithTime(-getSignedUrlTimeout());
   }

   protected void awaitConsistency() {
      if (view.getConsistencyModel() == ConsistencyModel.EVENTUAL) {
         Uninterruptibles.sleepUninterruptibly(AWAIT_CONSISTENCY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
      }
   }
}
