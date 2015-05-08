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
package org.jclouds.openstack.swift.v1.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * Will bind to headers, as needed, and will process content-* headers in a jclouds-compatible fashion.
 */
public class BindToHeaders implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkNotNull(request, "request");
      checkArgument(input instanceof Map<?, ?>, "input must be a non-null java.util.Map!");
      // Input map
      Map<String, String> headers = Map.class.cast(input);

      // Content map
      if (request.getPayload() == null) {
         request.setPayload("");
      }
      MutableContentMetadata contentMetadata = request.getPayload().getContentMetadata();

      // Regular headers map
      Builder<String, String> builder = ImmutableMultimap.<String, String> builder();

      for (Entry<String, String> keyVal : headers.entrySet()) {
         String keyInLowercase = keyVal.getKey().toLowerCase();

         if (keyInLowercase.equals("content-type")) {
            contentMetadata.setContentType(keyVal.getValue());
            continue;
         }
         if (keyInLowercase.equals("content-disposition")) {
            contentMetadata.setContentDisposition(keyVal.getValue());
            continue;
         }
         if (keyInLowercase.equals("content-encoding")) {
            contentMetadata.setContentEncoding(keyVal.getValue());
            continue;
         }
         if (keyInLowercase.equals("content-language")) {
            contentMetadata.setContentLanguage(keyVal.getValue());
            continue;
         }
         if (keyInLowercase.equals("content-length")) {
            contentMetadata.setContentLength(Long.parseLong(keyVal.getValue()));
            continue;
         }
         builder.put(keyInLowercase, keyVal.getValue());
      }
      request.getPayload().setContentMetadata(contentMetadata);
      return (R) request.toBuilder().replaceHeaders(builder.build()).build();
   }
}
