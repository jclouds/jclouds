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
package org.jclouds.chef.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ChefClientErrorRetryHandler}
 */
@Test(groups = { "unit" })
public class ChefApiErrorRetryHandlerTest {
   @Test
   public void test401DoesNotRetry() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = createMock(HttpResponse.class);
      BackoffLimitedRetryHandler retry = createMock(BackoffLimitedRetryHandler.class);

      expect(command.getFailureCount()).andReturn(0);
      expect(response.getStatusCode()).andReturn(401).atLeastOnce();

      replay(response);
      replay(retry);
      replay(command);

      ChefApiErrorRetryHandler handler = new ChefApiErrorRetryHandler(retry);

      assert !handler.shouldRetryRequest(command, response);

      verify(retry);
      verify(command);
      verify(response);

   }

   @Test
   public void test400DoesNotRetry() {

      HttpCommand command = createMock(HttpCommand.class);
      HttpResponse response = createMock(HttpResponse.class);
      BackoffLimitedRetryHandler retry = createMock(BackoffLimitedRetryHandler.class);

      expect(command.getFailureCount()).andReturn(0);
      expect(response.getStatusCode()).andReturn(401).atLeastOnce();

      replay(response);
      replay(retry);
      replay(command);

      ChefApiErrorRetryHandler handler = new ChefApiErrorRetryHandler(retry);

      assert !handler.shouldRetryRequest(command, response);

      verify(retry);
      verify(command);
      verify(response);

   }

   @Test
   public void testRetryOn400PutSandbox() {

      HttpCommand command = createMock(HttpCommand.class);
      BackoffLimitedRetryHandler retry = createMock(BackoffLimitedRetryHandler.class);

      HttpRequest request = HttpRequest.builder().method("PUT")
            .endpoint("https://api.opscode.com/organizations/jclouds/sandboxes/bfd68d4052f44053b2e593a33b5e1cd5")
            .build();
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(400)
            .message("400 Bad Request")
            .payload(
                  "{\"error\":[\"Cannot update sandbox bfd68d4052f44053b2e593a33b5e1cd5: checksum 9b7c23369f4b576451216c39f214af6c was not uploaded\"]}")
            .build();

      expect(command.getFailureCount()).andReturn(0);
      expect(command.getCurrentRequest()).andReturn(request).atLeastOnce();
      expect(retry.shouldRetryRequest(command, response)).andReturn(true);

      replay(retry);
      replay(command);

      ChefApiErrorRetryHandler handler = new ChefApiErrorRetryHandler(retry);

      assert handler.shouldRetryRequest(command, response);

      verify(retry);
      verify(command);

   }
}
