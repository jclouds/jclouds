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
package org.jclouds.azurecompute.arm.functions;
import com.google.common.base.Function;
import org.jclouds.http.HttpResponse;

import javax.inject.Singleton;

import java.net.URI;
/**
 * Parses job status from http response
 */
@Singleton
public class URIParser implements Function<HttpResponse, URI> {
   public URI apply(final HttpResponse from) {
      if (from.getStatusCode() == 202 && from.getHeaders().containsKey("Location")){
         String uri = from.getFirstHeaderOrNull("Location");
         return URI.create(uri);

      } else if (from.getStatusCode() == 200){
         return null;
      }
      throw new IllegalStateException("did not receive expected response code and header in: " + from);
   }
}
