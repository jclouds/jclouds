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

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.ContextBuilder;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.io.Payloads;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

@Test(groups = "unit", testName = "BackoffLimitedRetryHandlerTest")
public class BackoffLimitedRetryHandlerTest {

   BackoffLimitedRetryHandler handler = new BackoffLimitedRetryHandler();

   @Test
   void testExponentialBackoffDelayDefaultMaxInterval500() throws InterruptedException {
      long period = 500;
      long acceptableDelay = period - 1;

      long startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 1, 5, "TEST FAILURE: 1");
      long elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert elapsedTime >= period - 1 : elapsedTime;
      assertTrue(elapsedTime < period + acceptableDelay);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 2, 5, "TEST FAILURE: 2");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert elapsedTime >= period * 4 - 1 : elapsedTime;
      assertTrue(elapsedTime < period * 9);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 3, 5, "TEST FAILURE: 3");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert elapsedTime >= period * 9 - 1 : elapsedTime;
      assertTrue(elapsedTime < period * 10);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 4, 5, "TEST FAILURE: 4");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert elapsedTime >= period * 10 - 1 : elapsedTime;
      assertTrue(elapsedTime < period * 11);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 5, 5, "TEST FAILURE: 5");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert elapsedTime >= period * 10 - 1 : elapsedTime;
      assertTrue(elapsedTime < period * 11);

   }

   @Test
   void testExponentialBackoffDelaySmallInterval5() throws InterruptedException {
      long period = 5;
      long acceptableDelay = period - 1;

      long startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 1, 5, "TEST FAILURE: 1");
      long elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert elapsedTime >= period - 1 : elapsedTime;
      assertTrue(elapsedTime < period + acceptableDelay);
   }

   @Test
   void testExponentialBackoffDelaySmallInterval1() throws InterruptedException {
      long period = 1;
      long acceptableDelay = 5;

      long startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 1, 5, "TEST FAILURE: 1");
      long elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert elapsedTime >= period - 1 : elapsedTime;
      assertTrue(elapsedTime < period + acceptableDelay);
   }

   @Test
   void testExponentialBackoffDelaySmallInterval0() throws InterruptedException {
      long period = 0;
      long acceptableDelay = 5;

      long startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 1, 5, "TEST FAILURE: 1");
      long elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert elapsedTime >= period - 1 : elapsedTime;
      assertTrue(elapsedTime < period + acceptableDelay);
   }

   @Test
   void testInputStreamIsNotClosed() throws SecurityException, NoSuchMethodException, IOException {
      HttpCommand command = createCommand();
      HttpResponse response = HttpResponse.builder().statusCode(500).build();

      InputStream inputStream = new InputStream() {
         int count = 2;

         @Override
         public void close() {
            fail("The retry handler should not close the response stream");
         }

         @Override
         public int read() throws IOException {
            return count < 0 ? -1 : --count;
         }

         @Override
         public int available() throws IOException {
            return count < 0 ? 0 : count;
         }
      };

      response.setPayload(Payloads.newInputStreamPayload(inputStream));
      response.getPayload().getContentMetadata().setContentLength(1l);
      assertEquals(response.getPayload().openStream().available(), 2);
      assertEquals(response.getPayload().openStream().read(), 1);

      handler.shouldRetryRequest(command, response);

      assertEquals(response.getPayload().openStream().available(), 1);
      assertEquals(response.getPayload().openStream().read(), 0);
   }

   private final Function<Invocation, HttpRequest> processor = ContextBuilder
         .newBuilder(AnonymousProviderMetadata.forApiOnEndpoint(IntegrationTestClient.class, "http://localhost"))
         .buildInjector().getInstance(RestAnnotationProcessor.class);


   private HttpCommand createCommand() throws SecurityException, NoSuchMethodException {
      Invokable<IntegrationTestClient, String> method = method(IntegrationTestClient.class, "download", String.class);

      return new HttpCommand(processor.apply(Invocation.create(method, ImmutableList.<Object> of("1"))));
   }

   @Test
   void testIncrementsFailureCount() throws InterruptedException, IOException, SecurityException, NoSuchMethodException {
      HttpCommand command = createCommand();
      HttpResponse response = HttpResponse.builder().statusCode(400).build();

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 1);

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 2);

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 3);
   }

   @Test
   void testDisallowsExcessiveRetries() throws InterruptedException, IOException, SecurityException,
            NoSuchMethodException {
      HttpCommand command = createCommand();
      HttpResponse response = HttpResponse.builder().statusCode(400).build();

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 1

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 2

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 3

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 4

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 5

      assertEquals(handler.shouldRetryRequest(command, response), false); // Failure 6
   }

}
