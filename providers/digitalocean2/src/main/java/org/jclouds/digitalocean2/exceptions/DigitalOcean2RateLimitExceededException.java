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

import static org.jclouds.digitalocean2.handlers.RateLimitRetryHandler.millisUntilNextAvailableRequest;

import org.jclouds.http.HttpResponse;
import org.jclouds.rest.RateLimitExceededException;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Provides detailed information for rate limit exceptions.
 */
@Beta
public class DigitalOcean2RateLimitExceededException extends RateLimitExceededException {
   private static final long serialVersionUID = 1L;
   private static final String RATE_LIMIT_HEADER_PREFIX = "RateLimit-";

   private Integer totalRequestsPerHour;
   private Integer remainingRequests;
   private Long timeToNextAvailableRequest;

   public DigitalOcean2RateLimitExceededException(HttpResponse response) {
      super(response.getStatusLine() + "\n" + rateLimitHeaders(response));
      parseRateLimitInfo(response);
   }

   public DigitalOcean2RateLimitExceededException(HttpResponse response, Throwable cause) {
      super(response.getStatusLine() + "\n" + rateLimitHeaders(response), cause);
      parseRateLimitInfo(response);
   }

   public Integer totalRequestsPerHour() {
      return totalRequestsPerHour;
   }

   public Integer remainingRequests() {
      return remainingRequests;
   }

   public Long timeToNextAvailableRequest() {
      return timeToNextAvailableRequest;
   }

   private void parseRateLimitInfo(HttpResponse response) {
      String limit = response.getFirstHeaderOrNull("RateLimit-Limit");
      String remaining = response.getFirstHeaderOrNull("RateLimit-Remaining");
      String reset = response.getFirstHeaderOrNull("RateLimit-Reset");

      totalRequestsPerHour = limit == null ? null : Integer.valueOf(limit);
      remainingRequests = remaining == null ? null : Integer.valueOf(remaining);
      timeToNextAvailableRequest = reset == null ? null : millisUntilNextAvailableRequest(Long.parseLong(reset));
   }

   private static Multimap<String, String> rateLimitHeaders(HttpResponse response) {
      return Multimaps.filterKeys(response.getHeaders(), new Predicate<String>() {
         @Override
         public boolean apply(String input) {
            return input.startsWith(RATE_LIMIT_HEADER_PREFIX);
         }
      });
   }
}
