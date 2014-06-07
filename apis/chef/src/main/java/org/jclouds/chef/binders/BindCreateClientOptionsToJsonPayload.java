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
package org.jclouds.chef.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.chef.options.CreateClientOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Bind the parameters of a {@link CreateClientOptions} to the payload.
 */
public class BindCreateClientOptionsToJsonPayload extends BindToJsonPayload {
   @Inject
   public BindCreateClientOptionsToJsonPayload(Json jsonBinder) {
      super(jsonBinder);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      checkState(gRequest.getInvocation().getArgs() != null, "args should be initialized at this point");

      String name = checkNotNull(postParams.remove("name"), "name").toString();
      CreateClientOptions options = (CreateClientOptions) Iterables.find(gRequest.getInvocation().getArgs(),
            Predicates.instanceOf(CreateClientOptions.class));

      return bindToRequest(request, new CreateClientParams(name, options));
   }

   @SuppressWarnings("unused")
   private static class CreateClientParams {
      private String name;

      private boolean admin;

      public CreateClientParams(String name, CreateClientOptions options) {
         this.name = name;
         this.admin = options.isAdmin();
      }
   }

}
