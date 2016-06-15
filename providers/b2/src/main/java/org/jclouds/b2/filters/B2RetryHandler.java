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
package org.jclouds.b2.filters;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.b2.B2Api;
import org.jclouds.b2.domain.GetUploadPartResponse;
import org.jclouds.b2.domain.UploadUrlResponse;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.net.HttpHeaders;
import com.google.inject.Singleton;

@Singleton
public final class B2RetryHandler extends BackoffLimitedRetryHandler implements HttpRequestFilter {
   private final B2Api api;

   @Resource
   private Logger logger = Logger.NULL;

   @Inject
   B2RetryHandler(B2Api api) {
      this.api = api;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      HttpRequest.Builder<?> builder = request.toBuilder();

      // B2 requires retrying on a different storage node for uploads
      String path = request.getEndpoint().getPath();
      if (path.startsWith("/b2api/v1/b2_upload_file")) {
         String bucketId = path.split("/")[4];
         UploadUrlResponse uploadUrl = api.getObjectApi().getUploadUrl(bucketId);
         builder.endpoint(uploadUrl.uploadUrl())
               .replaceHeader(HttpHeaders.AUTHORIZATION, uploadUrl.authorizationToken());
      } else if (path.startsWith("/b2api/v1/b2_upload_part")) {
         String fileId = path.split("/")[4];
         GetUploadPartResponse uploadUrl = api.getMultipartApi().getUploadPartUrl(fileId);
         builder.endpoint(uploadUrl.uploadUrl())
               .replaceHeader(HttpHeaders.AUTHORIZATION, uploadUrl.authorizationToken());
      }

      return builder.build();
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      boolean retry = false;
      try {
         byte[] data = closeClientButKeepContentStream(response);
         switch (response.getStatusCode()) {
         case 500:
         case 503:
            retry = super.shouldRetryRequest(command, response);
            break;
         default:
            break;
         }
      } finally {
         releasePayload(response);
      }
      return retry;
   }
}
