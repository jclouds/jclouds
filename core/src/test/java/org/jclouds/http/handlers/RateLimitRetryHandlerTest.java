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
package org.jclouds.http.handlers;

import static com.google.common.net.HttpHeaders.RETRY_AFTER;
import static org.jclouds.http.HttpUtils.releasePayload;
import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Uninterruptibles;

@Test(groups = "unit", testName = "RateLimitRetryHandlerTest")
public class RateLimitRetryHandlerTest {

   // Configure a safe timeout of one minute to abort the tests in case they get
   // stuck
   private static final long TEST_SAFE_TIMEOUT = 60000;

   private final RateLimitRetryHandler rateLimitRetryHandler = new RateLimitRetryHandler() {
      @Override
      protected Optional<Long> millisToNextAvailableRequest(HttpCommand command, HttpResponse response) {
         String secondsToNextAvailableRequest = response.getFirstHeaderOrNull(RETRY_AFTER);
         return secondsToNextAvailableRequest != null ? Optional.of(Long.valueOf(secondsToNextAvailableRequest) * 1000)
               : Optional.<Long> absent();
      }
   };

   @Test(timeOut = TEST_SAFE_TIMEOUT)
   public void testDoNotRetryIfNoRateLimit() {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(450).build();

      assertFalse(rateLimitRetryHandler.shouldRetryRequest(command, response));
   }

   @Test(timeOut = TEST_SAFE_TIMEOUT)
   public void testDoNotRetryIfNotReplayable() {
      // InputStream payloads are not replayable
      Payload payload = newInputStreamPayload(new ByteArrayInputStream(new byte[0]));
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost")
            .payload(payload).build());
      HttpResponse response = HttpResponse.builder().statusCode(429).build();

      try {
         assertFalse(rateLimitRetryHandler.shouldRetryRequest(command, response));
      } finally {
         releasePayload(command.getCurrentRequest());
      }
   }

   @Test(timeOut = TEST_SAFE_TIMEOUT)
   public void testDoNotRetryIfNoRateLimitInfo() {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(429).build();

      assertFalse(rateLimitRetryHandler.shouldRetryRequest(command, response));
   }

   @Test(timeOut = TEST_SAFE_TIMEOUT)
   public void testDoNotRetryIfTooMuchWait() {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(429).addHeader(RETRY_AFTER, "400").build();

      assertFalse(rateLimitRetryHandler.shouldRetryRequest(command, response));
   }

   @Test(timeOut = TEST_SAFE_TIMEOUT)
   public void testRequestIsDelayed() {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(429).addHeader(RETRY_AFTER, "5").build();

      long start = System.currentTimeMillis();

      assertTrue(rateLimitRetryHandler.shouldRetryRequest(command, response));
      // Should have blocked the amount of time configured in the header. Use a
      // smaller value to compensate the time it takes to reach the code that
      // computes the amount of time to wait.
      assertTrue(System.currentTimeMillis() - start > 2500);
   }

   @Test(timeOut = TEST_SAFE_TIMEOUT)
   public void testDoNotRetryIfRequestIsAborted() throws Exception {
      final HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost")
            .build());
      final HttpResponse response = HttpResponse.builder().statusCode(429).addHeader(RETRY_AFTER, "10").build();

      final Thread requestThread = Thread.currentThread();
      Thread killer = new Thread() {
         @Override
         public void run() {
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
            requestThread.interrupt();
         }
      };

      // Start the killer thread that will abort the rate limit wait
      killer.start();
      assertFalse(rateLimitRetryHandler.shouldRetryRequest(command, response));
   }

   @Test(timeOut = TEST_SAFE_TIMEOUT)
   public void testIncrementsFailureCount() {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(429).build();

      rateLimitRetryHandler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 1);

      rateLimitRetryHandler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 2);

      rateLimitRetryHandler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 3);
   }

   @Test(timeOut = TEST_SAFE_TIMEOUT)
   public void testDisallowExcessiveRetries() {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      HttpResponse response = HttpResponse.builder().statusCode(429).addHeader(RETRY_AFTER, "0").build();

      for (int i = 0; i < 5; i++) {
         assertTrue(rateLimitRetryHandler.shouldRetryRequest(command, response));
      }
      assertFalse(rateLimitRetryHandler.shouldRetryRequest(command, response));
   }
}
