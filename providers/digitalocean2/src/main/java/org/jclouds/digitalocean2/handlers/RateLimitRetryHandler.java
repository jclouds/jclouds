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
package org.jclouds.digitalocean2.handlers;

import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.digitalocean2.config.DigitalOcean2Properties.MAX_RATE_LIMIT_WAIT;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;

/**
 * Retry handler that takes into account the DigitalOcean rate limit and delays
 * the requests until they are known to succeed.
 */
@Beta
@Singleton
public class RateLimitRetryHandler implements HttpRetryHandler {

   static final String RATE_LIMIT_RESET_HEADER = "RateLimit-Reset";

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(PROPERTY_MAX_RETRIES)
   private int retryCountLimit = 5;

   @Inject(optional = true)
   @Named(MAX_RATE_LIMIT_WAIT)
   private int maxRateLimitWait = 120000;

   @Override
   public boolean shouldRetryRequest(final HttpCommand command, final HttpResponse response) {
      command.incrementFailureCount();

      // Do not retry client errors that are not rate limit errors
      if (response.getStatusCode() != 429) {
         return false;
      } else if (!command.isReplayable()) {
         logger.error("Cannot retry after rate limit error, command is not replayable: %1$s", command);
         return false;
      } else if (command.getFailureCount() > retryCountLimit) {
         logger.error("Cannot retry after rate limit error, command has exceeded retry limit %1$d: %2$s",
               retryCountLimit, command);
         return false;
      } else {
         return delayRequestUntilAllowed(command, response);
      }
   }

   private boolean delayRequestUntilAllowed(final HttpCommand command, final HttpResponse response) {
      // The header is the Unix epoch time when the next request can be done
      String epochForNextAvailableRequest = response.getFirstHeaderOrNull(RATE_LIMIT_RESET_HEADER);
      if (epochForNextAvailableRequest == null) {
         logger.error("Cannot retry after rate limit error, no retry information provided in the response");
         return false;
      }

      long waitPeriod = millisUntilNextAvailableRequest(Long.parseLong(epochForNextAvailableRequest));

      if (waitPeriod > 0) {
         if (waitPeriod > maxRateLimitWait) {
            logger.error("Max wait for rate limited requests is %s seconds but need to wait %s seconds, aborting",
                  maxRateLimitWait, waitPeriod);
            return false;
         }

         try {
            logger.debug("Waiting %s seconds before retrying, as defined by the rate limit", waitPeriod);
            // Do not use Uninterrumpibles or similar, to let the jclouds
            // tiemout configuration interrupt this thread
            Thread.sleep(waitPeriod);
         } catch (InterruptedException ex) {
            // If the request is being executed and has a timeout configured,
            // the thread may be interrupted when the timeout is reached.
            logger.error("Request execution was interrupted, aborting");
            Thread.currentThread().interrupt();
            return false;
         }
      }

      return true;
   }

   public static long millisUntilNextAvailableRequest(long epochForNextAvailableRequest) {
      return (epochForNextAvailableRequest * 1000) - System.currentTimeMillis();
   }
}
