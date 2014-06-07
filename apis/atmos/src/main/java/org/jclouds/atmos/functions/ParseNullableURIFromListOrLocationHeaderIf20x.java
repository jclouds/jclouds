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
package org.jclouds.atmos.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;

/**
 * Parses a single URI from a list, returning null when blob length was zero.
 * Atmos returns "HTTP/1.1 201 null" when putting zero-length blobs.
 */
public class ParseNullableURIFromListOrLocationHeaderIf20x extends ParseURIFromListOrLocationHeaderIf20x {

   @Override
   public URI apply(HttpResponse from) {
      if (from.getStatusCode() == 201 && request.getPayload().getContentMetadata().getContentLength() == 0) {
         releasePayload(from);
         return null;
      }
      return super.apply(from);
   }
}
