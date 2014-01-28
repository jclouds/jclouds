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
package org.jclouds.docker.binders;

import com.google.common.io.CharStreams;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@Test(groups = "unit", testName = "BindInputStreamToRequestTest")
public class BindInputStreamToRequestTest {

   @Test
   public void testBindInputStreamToRequest() throws IOException {
      BindInputStreamToRequest binder = new BindInputStreamToRequest();

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://test").build();
      request = binder.bindToRequest(request, File.createTempFile("dockerfile", ""));
      String rawContent = CharStreams.toString(new InputStreamReader((FileInputStream) request.getPayload().getRawContent(), "UTF-8"));
      assertTrue(rawContent.startsWith("Dockerfile"));
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/tar");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testBindInputStreamToRequestWithObjectAsInput() throws IOException {
      BindInputStreamToRequest binder = new BindInputStreamToRequest();

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://test").build();
      request = binder.bindToRequest(request, new Object());
      String rawContent = CharStreams.toString(new InputStreamReader((FileInputStream) request.getPayload().getRawContent(), "UTF-8"));
      assertTrue(rawContent.startsWith("Dockerfile"));
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/tar");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testBindInputStreamToRequestWithNullInput() throws IOException {
      BindInputStreamToRequest binder = new BindInputStreamToRequest();

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://test").build();
      request = binder.bindToRequest(request, null);
      String rawContent = CharStreams.toString(new InputStreamReader((FileInputStream) request.getPayload().getRawContent(), "UTF-8"));
      assertTrue(rawContent.startsWith("Dockerfile"));
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/tar");
   }
}
