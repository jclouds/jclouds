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
package org.jclouds.azurecompute.arm.filters;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.API_VERSION_PREFIX;
import static org.jclouds.util.Maps2.transformKeys;
import static org.jclouds.util.Predicates2.startsWith;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.config.InvocationConfig;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.reflect.Invokable;

/**
 * Allow users to customize the api versions for each method call.
 * <p>
 * In Azure ARM, each method may have its own api version. This filter allows to
 * configure the versions of each method, so there is no need to change the code
 * when Azure deprecates old versions.
 */
public class ApiVersionFilter implements HttpRequestFilter {

   private final InvocationConfig config;
   private final Map<String, String> versions;

   @Inject
   ApiVersionFilter(InvocationConfig config, Function<Predicate<String>, Map<String, String>> filterStringsBoundByName) {
      this.config = config;
      this.versions = versions(filterStringsBoundByName);
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      checkArgument(request instanceof GeneratedHttpRequest,
            "This filter can only be applied to GeneratedHttpRequest objects");
      GeneratedHttpRequest generatedRequest = (GeneratedHttpRequest) request;

      // Look if there is a custom api version for the current method
      String commandName = config.getCommandName(generatedRequest.getInvocation());
      String customApiVersion = versions.get(commandName);

      if (customApiVersion == null) {
         // No custom config for the specific method. Let's look for custom
         // config for the class
         Invokable<?, ?> invoked = generatedRequest.getInvocation().getInvokable();
         String className = invoked.getOwnerType().getRawType().getSimpleName();
         customApiVersion = versions.get(className);
      }

      if (customApiVersion != null) {
         return request.toBuilder().replaceQueryParam("api-version", customApiVersion).build();
      }

      return request;
   }

   private static Map<String, String> versions(Function<Predicate<String>, Map<String, String>> filterStringsBoundByName) {
      Map<String, String> stringBoundWithApiVersionPrefix = filterStringsBoundByName
            .apply(startsWith(API_VERSION_PREFIX));
      return transformKeys(stringBoundWithApiVersionPrefix, new Function<String, String>() {
         public String apply(String input) {
            return input.replaceFirst(API_VERSION_PREFIX, "");
         }
      });
   }
}
