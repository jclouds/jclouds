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

import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.RateLimitRetryHandler;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.net.HttpHeaders;

@Beta
@Singleton
public class AzureRateLimitRetryHandler extends RateLimitRetryHandler {

   @Override
   protected Optional<Long> millisToNextAvailableRequest(HttpCommand command, HttpResponse response) {
      String secondsToNextAvailableRequest = response.getFirstHeaderOrNull(HttpHeaders.RETRY_AFTER);
      return secondsToNextAvailableRequest != null ? Optional.of(Long.valueOf(secondsToNextAvailableRequest) * 1000)
            : Optional.<Long> absent();
   }
}
