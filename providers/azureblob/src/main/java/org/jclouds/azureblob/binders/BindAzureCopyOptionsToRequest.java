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

import java.util.Date;
import java.util.Map;

import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.azureblob.options.CopyBlobOptions;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Optional;

/** Binds options to a copyBlob request. */
public class BindAzureCopyOptionsToRequest implements Binder {
   private static final DateService dateService = new SimpleDateFormatDateService();

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      HttpRequest.Builder builder = request.toBuilder();
      CopyBlobOptions options = (CopyBlobOptions) input;

      Optional<Map<String, String>> userMetadata = options.getUserMetadata();
      if (userMetadata.isPresent()) {
         for (Map.Entry<String, String> entry : userMetadata.get().entrySet()) {
            builder.addHeader(AzureStorageHeaders.USER_METADATA_PREFIX + entry.getKey(), entry.getValue());
         }
      }

      Optional<Date> ifModifiedSince = options.getIfModifiedSince();
      if (ifModifiedSince.isPresent()) {
         builder.addHeader(AzureStorageHeaders.COPY_SOURCE_IF_MODIFIED_SINCE, dateService.rfc822DateFormat(ifModifiedSince.get()));
      }

      Optional<Date> ifUnmodifiedSince = options.getIfUnmodifiedSince();
      if (ifUnmodifiedSince.isPresent()) {
         builder.addHeader(AzureStorageHeaders.COPY_SOURCE_IF_UNMODIFIED_SINCE, dateService.rfc822DateFormat(ifUnmodifiedSince.get()));
      }

      Optional<String> ifMatch = options.getIfMatch();
      if (ifMatch.isPresent()) {
         builder.addHeader(AzureStorageHeaders.COPY_SOURCE_IF_MATCH, ifMatch.get());
      }

      Optional<String> ifNoneMatch = options.getIfNoneMatch();
      if (ifNoneMatch.isPresent()) {
         builder.addHeader(AzureStorageHeaders.COPY_SOURCE_IF_NONE_MATCH, ifNoneMatch.get());
      }

      return (R) builder.build();
   }
}
