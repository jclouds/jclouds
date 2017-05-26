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
package org.jclouds.aws.filters;

import org.jclouds.http.HttpRequest;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.annotations.ApiVersionOverride;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Optional;
import com.google.common.reflect.Invokable;

/**
 * Utilities for FormSigner implementations.
 */
public final class FormSignerUtils {

   private FormSignerUtils() {}

   /**
    * Get the version from a @ApiVersionOverride() annotation on an API method or its owning class.
    * @param request The API request for the method.
    * @return An optional of the value of the annotation.
    */
   public static Optional<String> getAnnotatedApiVersion(HttpRequest request) {
      if (request instanceof GeneratedHttpRequest) {
         GeneratedHttpRequest generatedRequest = (GeneratedHttpRequest) request;
         return getAnnotatedApiVersion(generatedRequest.getInvocation());
      } else {
         return Optional.absent();
      }
   }

   private static Optional<String> getAnnotatedApiVersion(Invocation invocation) {
      final Invokable<?, ?> invokable = invocation.getInvokable();
      if (invokable.isAnnotationPresent(ApiVersionOverride.class)) {
         return Optional.fromNullable(invokable.getAnnotation(ApiVersionOverride.class).value());
      } else {
         final Class<?> owner = invokable.getOwnerType().getRawType();
         if (owner.isAnnotationPresent(ApiVersionOverride.class)) {
            return Optional.fromNullable(owner.getAnnotation(ApiVersionOverride.class).value());
         }
      }
      return Optional.absent();
   }

}
