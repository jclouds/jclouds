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
package org.jclouds.b2.handlers;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.b2.B2ResponseException;
import org.jclouds.b2.domain.B2Error;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

public final class ParseB2ErrorFromJsonContent extends ParseJson<B2Error> implements HttpErrorHandler {
   @Inject
   ParseB2ErrorFromJsonContent(Json json) {
      super(json, TypeLiteral.get(B2Error.class));
   }

   private static Exception refineException(B2Error error, Exception exception) {
      if ("bad_bucket_id".equals(error.code())) {
         return new ContainerNotFoundException(exception);
      } else if ("bad_json".equals(error.code())) {
         return new IllegalArgumentException(error.message(), exception);
      } else if ("bad_request".equals(error.code())) {
         return new IllegalArgumentException(error.message(), exception);
      } else if ("file_not_present".equals(error.code())) {
         return new KeyNotFoundException(exception);
      } else if ("not_found".equals(error.code())) {
         return new ResourceNotFoundException(error.message(), exception);
      } else {
         return exception;
      }
   }

   @Override
   public void handleError(HttpCommand command, HttpResponse response) {
      B2Error error = this.apply(response);
      Exception exception = refineException(error, new B2ResponseException(command, response, error));
      command.setException(exception);
   }
}
