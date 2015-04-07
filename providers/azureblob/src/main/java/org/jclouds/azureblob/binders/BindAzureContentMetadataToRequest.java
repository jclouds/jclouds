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
package org.jclouds.azureblob.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.ContentMetadata;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;

@Singleton
public class BindAzureContentMetadataToRequest implements Binder {

   public BindAzureContentMetadataToRequest() {
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof ContentMetadata,
         "this binder is only valid for ContentMetadata");
      checkNotNull(request, "request");
      ContentMetadata contentMetadata = (ContentMetadata) input;

      ImmutableMap.Builder<String, String> headers = ImmutableMap.builder();

      String contentType = contentMetadata.getContentType();
      if (contentType != null) {
         headers.put("x-ms-blob-type", contentType);
      }

      String contentDisposition = contentMetadata.getContentDisposition();
      if (contentDisposition != null) {
         headers.put("x-ms-blob-content-disposition", contentDisposition);
      }

      return (R) request.toBuilder().replaceHeaders(Multimaps.forMap(headers.build())).build();
   }
}
