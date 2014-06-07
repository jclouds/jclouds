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
package org.jclouds.atmos.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.atmos.domain.AtmosError;
import org.jclouds.atmos.util.AtmosUtils;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AtmosServerErrorRetryHandler}
 */
@Test(groups = "unit")
public class AtmosServerErrorRetryHandlerTest {
   private static final String HTTP_MESSAGE_FORMAT =
         "<?xml version='1.0' encoding='UTF-8'?>\n" +
         "<Error>\n" +
         "<Code>%d</Code>\n" +
         "<Message>%s</Message>\n" +
         "</Error>\n";

   @Test
   public void testGet500WithoutError() {
      AtmosUtils utils = createMock(AtmosUtils.class);
      BackoffLimitedRetryHandler backoffLimitedRetryHandler = createMock(BackoffLimitedRetryHandler.class);
      HttpCommand command = createMock(HttpCommand.class);

      expect(command.getFailureCount()).andReturn(0).once();
      expect(command.incrementFailureCount()).andReturn(1).once();

      replay(utils, backoffLimitedRetryHandler, command);

      AtmosServerErrorRetryHandler retry = new AtmosServerErrorRetryHandler(backoffLimitedRetryHandler, utils);

      assertFalse(retry.shouldRetryRequest(command, HttpResponse.builder().statusCode(500).build()));

      verify(utils, backoffLimitedRetryHandler, command);
   }

   @Test
   public void testGet500WithError1040() {
      AtmosUtils utils = createMock(AtmosUtils.class);
      BackoffLimitedRetryHandler backoffLimitedRetryHandler = createMock(BackoffLimitedRetryHandler.class);
      HttpCommand command = createMock(HttpCommand.class);
      String content = String.format(HTTP_MESSAGE_FORMAT, 1040, "The server is busy. Please try again");
      HttpResponse response = HttpResponse.builder().statusCode(500).payload(content).build();

      expect(command.getFailureCount()).andReturn(0).once();
      expect(utils.parseAtmosErrorFromContent(command, response, content)).andReturn(new AtmosError(1040, "The server is busy. Please try again")).once();
      expect(backoffLimitedRetryHandler.shouldRetryRequest(command, response)).andReturn(true).once();

      replay(utils, backoffLimitedRetryHandler, command);

      AtmosServerErrorRetryHandler retry = new AtmosServerErrorRetryHandler(backoffLimitedRetryHandler, utils);

      assertTrue(retry.shouldRetryRequest(command, response));

      verify(utils, backoffLimitedRetryHandler, command);
   }
}
