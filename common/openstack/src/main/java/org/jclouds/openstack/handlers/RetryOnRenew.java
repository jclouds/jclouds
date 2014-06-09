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
package org.jclouds.openstack.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.domain.AuthenticationResponse;
import org.jclouds.openstack.reference.AuthHeaders;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This will parse and set an appropriate exception on the command object.
 */
@Singleton
public class RetryOnRenew implements HttpRetryHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   @Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_RETRIES)
   static int NUM_RETRIES = 5;

   private final LoadingCache<Credentials, AuthenticationResponse> authenticationResponseCache;

   /*
    * The reason retries need to be tracked is that it is possible that a token
    * can be expired at any time. The reason we track by request is that only
    * some requests might return a 401 (such as temporary URLs). However
    * consistent failures of the magnitude this code tracks should indicate a
    * problem.
    */
   private static final Cache<HttpCommand, Integer> retryCountMap = CacheBuilder
         .newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

   @Inject
   protected RetryOnRenew(LoadingCache<Credentials, AuthenticationResponse> authenticationResponseCache) {
      this.authenticationResponseCache = authenticationResponseCache;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      boolean retry = false; // default
      try {
         switch (response.getStatusCode()) {
            case 401:
               // Do not retry on 401 from authentication request
               Multimap<String, String> headers = command.getCurrentRequest().getHeaders();
               if (headers != null && headers.containsKey(AuthHeaders.AUTH_USER)
                        && headers.containsKey(AuthHeaders.AUTH_KEY) && !headers.containsKey(AuthHeaders.AUTH_TOKEN)) {
                  retry = false;
               } else {
                  closeClientButKeepContentStream(response);
                  // This is not an authentication request returning 401
                  // Check if we already had seen this request
                  Integer count = retryCountMap.getIfPresent(command);

                  if (count == null) {
                     // First time this non-authentication request failed
                     logger.debug("invalidating authentication token - first time for %s", command);
                     retryCountMap.put(command, 1);
                     authenticationResponseCache.invalidateAll();
                     retry = true;
                  } else {
                     // This request has failed before
                     if (count + 1 >= NUM_RETRIES) {
                        logger.debug("too many 401s - giving up after: %s for %s", count, command);
                        retry = false;
                     } else {
                        // Retry just in case
                        logger.debug("invalidating authentication token - retry %s for %s", count, command);
                        retryCountMap.put(command, count + 1);
                        // Wait between retries
                        authenticationResponseCache.invalidateAll();
                        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
                        retry = true;
                     }
                  }
               }
            break;
         }
         return retry;

      } finally {
         releasePayload(response);
      }
   }

}
