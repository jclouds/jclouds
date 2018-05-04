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

import static org.jclouds.azurecompute.arm.handlers.AzureRateLimitRetryHandler.isRateLimitError;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.domain.Error;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.annotations.Beta;

/**
 * This handles failed responses that return a <code>RetryableError</code>.
 * <p>
 * In order to determine if the error is retryable, the response body must be
 * read, so this handler will have to buffer the response payload in memory so
 * the response body can be read again in subsequent steps of the response
 * processing flow.
 */
@Singleton
@Beta
public class AzureRetryableErrorHandler extends BackoffLimitedRetryHandler {

   private static final String RETRYABLE_ERROR_CODE = "RetryableError";

   @Resource
   protected Logger logger = Logger.NULL;
   private final ParseJson<Error> parseError;

   @Inject
   AzureRetryableErrorHandler(ParseJson<Error> parseError) {
      this.parseError = parseError;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      // Only consider retryable errors and discard rate limit ones
      if (response.getStatusCode() != 429 || isRateLimitError(response)) {
         return false;
      }

      try {
         // Note that this will consume the response body. At this point,
         // subsequent retry handlers or error handlers will not be able to read
         // again the payload, but that should only be attempted when the
         // command is not retryable and an exception should be thrown.
         Error error = parseError.apply(response);
         logger.debug("processing error: %s", error);

         boolean isRetryable = RETRYABLE_ERROR_CODE.equals(error.details().code());
         return isRetryable ? super.shouldRetryRequest(command, response) : false;
      } catch (Exception ex) {
         // If we can't parse the error, just assume it is not a retryable error
         logger.warn("could not parse error. Request won't be retried: %s", ex.getMessage());
         return false;
      }
   }

}
