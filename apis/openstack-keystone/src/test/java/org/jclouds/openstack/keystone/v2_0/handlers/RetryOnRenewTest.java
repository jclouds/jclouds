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
package org.jclouds.openstack.keystone.v2_0.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.testng.annotations.Test;

import com.google.common.cache.LoadingCache;

/**
 * Tests behavior of {@code RetryOnRenew} handler
 */
@Test(groups = "unit", testName = "RetryOnRenewTest")
public class RetryOnRenewTest {
   @Test
   public void test401ShouldRetry() {
      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = createMock(HttpRequest.class);
      HttpResponse response = createMock(HttpResponse.class);
      @SuppressWarnings("unchecked")
      LoadingCache<Credentials, Access> cache = createMock(LoadingCache.class);
      BackoffLimitedRetryHandler backoffHandler = createMock(BackoffLimitedRetryHandler.class);

      expect(command.getCurrentRequest()).andReturn(request);

      cache.invalidateAll();
      expectLastCall();

      expect(response.getPayload()).andReturn(Payloads.newStringPayload("")).anyTimes();
      expect(response.getStatusCode()).andReturn(401).atLeastOnce();

      replay(command);
      replay(response);
      replay(cache);
      replay(backoffHandler);

      RetryOnRenew retry = new RetryOnRenew(cache, backoffHandler);

      assertTrue(retry.shouldRetryRequest(command, response));

      verify(command);
      verify(response);
      verify(cache);
   }

   @Test
   public void test401ShouldRetry4Times() {
      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = createMock(HttpRequest.class);
      HttpResponse response = createMock(HttpResponse.class);

      @SuppressWarnings("unchecked")
      LoadingCache<Credentials, Access> cache = createMock(LoadingCache.class);
      BackoffLimitedRetryHandler backoffHandler = createMock(BackoffLimitedRetryHandler.class);

      expect(command.getCurrentRequest()).andReturn(request).anyTimes();
      expect(request.getHeaders()).andStubReturn(null);

      cache.invalidateAll();
      expectLastCall().anyTimes();

      expect(response.getPayload()).andReturn(Payloads.newStringPayload("")).anyTimes();
      expect(response.getStatusCode()).andReturn(401).anyTimes();

      replay(command, request, response, cache);

      RetryOnRenew retry = new RetryOnRenew(cache, backoffHandler);

      for (int i = 0; i < RetryOnRenew.NUM_RETRIES - 1; ++i) {
         assertTrue(retry.shouldRetryRequest(command, response), "Expected retry to succeed");
      }

      assertFalse(retry.shouldRetryRequest(command, response), "Expected retry to fail on attempt " + RetryOnRenew.NUM_RETRIES);

      verify(command, response, cache);
   }

   @Test
   public void test408ShouldRetry() {
      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = createMock(HttpResponse.class);
      @SuppressWarnings("unchecked")
      LoadingCache<Credentials, Access> cache = createMock(LoadingCache.class);
      BackoffLimitedRetryHandler backoffHandler = createMock(BackoffLimitedRetryHandler.class);

      expect(response.getPayload()).andReturn(Payloads.newStringPayload(
                  "The server has waited too long for the request to be sent by the client.")).times(3);
      expect(backoffHandler.shouldRetryRequest(command, response)).andReturn(true).once();
      expect(response.getStatusCode()).andReturn(408).once();

      replay(command);
      replay(response);
      replay(cache);
      replay(backoffHandler);

      RetryOnRenew retry = new RetryOnRenew(cache, backoffHandler);

      assertTrue(retry.shouldRetryRequest(command, response));

      verify(command);
      verify(response);
      verify(cache);
      verify(backoffHandler);
   }
}
