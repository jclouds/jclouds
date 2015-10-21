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
package org.jclouds.digitalocean2.exceptions;

import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.digitalocean2.handlers.RateLimitRetryHandler.millisUntilNextAvailableRequest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Properties;

import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "RateLimitExceptionMockTest", singleThreaded = true)
public class RateLimitExceptionMockTest extends BaseDigitalOcean2ApiMockTest {

   @Override
   protected Properties overrides() {
      Properties overrides = super.overrides();
      overrides.put(PROPERTY_MAX_RETRIES, "0"); // Do not retry
      return overrides;
   }

   public void testRateLimitExceptionIsThrown() throws InterruptedException {
      long reset = (System.currentTimeMillis() / 1000) + 3600; // Epoch for one
                                                               // hour from now
      long millisToReset = millisUntilNextAvailableRequest(reset);

      server.enqueue(new MockResponse().setResponseCode(429).addHeader("RateLimit-Limit", "5000")
            .addHeader("RateLimit-Remaining", "1235").addHeader("RateLimit-Reset", String.valueOf(reset)));

      try {
         api.keyApi().list();
         fail("Expected a DigitalOcean2RateLimitExceededException to be thrown");
      } catch (DigitalOcean2RateLimitExceededException ex) {
         assertEquals(ex.totalRequestsPerHour().intValue(), 5000);
         assertEquals(ex.remainingRequests().intValue(), 1235);
         // Can't verify with millisecond precision. Use an interval to have a
         // consistent test.
         assertTrue(ex.timeToNextAvailableRequest() < millisToReset
               && ex.timeToNextAvailableRequest() > millisToReset - 1800000);
      }
   }

}
