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
package org.jclouds.googlecloudstorage.handlers;

import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.http.HttpRetryHandler;

import com.google.inject.Inject;

@Singleton
public final class GoogleCloudStorageClientErrorRetryHandler implements HttpRetryHandler {
   /** The user has sent too many requests in a given amount of time ("rate limiting"). */
   // TODO: remove when upgrading to jax-rs api 2.1
   private static final int TOO_MANY_REQUESTS = 429;

   private final BackoffLimitedRetryHandler backoffHandler;

   @Inject
   protected GoogleCloudStorageClientErrorRetryHandler(BackoffLimitedRetryHandler backoffHandler) {
      this.backoffHandler = backoffHandler;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (response.getStatusCode() == TOO_MANY_REQUESTS) {
         return backoffHandler.shouldRetryRequest(command, response);
      } else {
         return false;
      }
   }
}
