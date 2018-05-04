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

import static org.jclouds.Constants.PROPERTY_MAX_RATE_LIMIT_WAIT;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * Retry handler that takes into account the provider rate limit and delays the
 * requests until they are known to succeed.
 */
@Beta
public abstract class RateLimitRetryHandler implements HttpRetryHandler {

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(PROPERTY_MAX_RETRIES)
   private int retryCountLimit = 5;

   @Inject(optional = true)
   @Named(PROPERTY_MAX_RATE_LIMIT_WAIT)
   private int maxRateLimitWait = 2 * 60 * 1000;

   /**
    * Returns the response status that will be considered a rate limit error.
    * <p>
    * Providers can override this to customize which responses are retried.
    */
   protected int rateLimitErrorStatus() {
      return 429;
   }

   /**
    * Compute the number of milliseconds that must pass until a request can be
    * performed.
    * 
    * @param command The command being executed.
    * @param response The rate-limit error response.
    * @return The number of milliseconds to wait for an available request, if taht information is available.
    */
   protected abstract Optional<Long> millisToNextAvailableRequest(final HttpCommand command, final HttpResponse response);

   @Override
   public boolean shouldRetryRequest(final HttpCommand command, final HttpResponse response) {
      command.incrementFailureCount();

      // Do not retry client errors that are not rate limit errors
      if (response.getStatusCode() != rateLimitErrorStatus()) {
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

   protected boolean delayRequestUntilAllowed(final HttpCommand command, final HttpResponse response) {
      Optional<Long> millisToNextAvailableRequest = millisToNextAvailableRequest(command, response);
      if (!millisToNextAvailableRequest.isPresent()) {
         logger.error("Cannot retry after rate limit error, no retry information provided in the response");
         return false;
      }

      long waitPeriod = millisToNextAvailableRequest.get();
      if (waitPeriod > 0L) {
         if (waitPeriod > maxRateLimitWait) {
            logger.error("Max wait for rate limited requests is %sms but need to wait %sms, aborting",
                  maxRateLimitWait, waitPeriod);
            return false;
         }

         try {
            logger.debug("Waiting %sms before retrying, as defined by the rate limit", waitPeriod);
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

   public int getRetryCountLimit() {
      return retryCountLimit;
   }

   public int getMaxRateLimitWait() {
      return maxRateLimitWait;
   }

}
