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
package org.jclouds.azurecompute.arm.handlers;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.net.HttpHeaders;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "unit", testName = "AzureRetryableErrorHandlerTest")
public class AzureRetryableErrorHandlerTest {

   private HttpRetryHandler handler;

   @BeforeClass
   public void setup() {
      // Initialize an injector with just the Json features to get all
      // serialization stuff
      Injector injector = Guice.createInjector(new GsonModule());
      handler = injector.getInstance(AzureRetryableErrorHandler.class);
   }

   @Test
   public void testDoesNotRetryWhenNot429() {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(400).build();

      assertFalse(handler.shouldRetryRequest(command, response));
   }

   @Test
   public void testDoesNotRetryWhenRateLimitError() {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(429).addHeader(HttpHeaders.RETRY_AFTER, "15").build();

      assertFalse(handler.shouldRetryRequest(command, response));
   }

   @Test
   public void testDoesNotRetryWhenCannotParseError() {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(429).payload("foo").build();

      assertFalse(handler.shouldRetryRequest(command, response));
   }

   @Test
   public void testDoesNotRetryWhenErrorNotRetryable() {
      String nonRetryable = "{\"error\":{\"code\":\"ReferencedResourceNotProvisioned\",\"message\":\"Not provisioned\"}}";
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(429).payload(nonRetryable).build();

      assertFalse(handler.shouldRetryRequest(command, response));
   }

   @Test
   public void testRetriesWhenRetryableError() {
      String retryable = "{\"error\":{\"code\":\"RetryableError\",\"message\":\"Resource busy\"}}";
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(429).payload(retryable).build();

      assertTrue(handler.shouldRetryRequest(command, response));
   }
}
