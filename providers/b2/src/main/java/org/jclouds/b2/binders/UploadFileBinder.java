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
package org.jclouds.b2.binders;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.b2.domain.UploadUrlResponse;
import org.jclouds.b2.reference.B2Headers;
import org.jclouds.rest.MapBinder;

import com.google.common.net.HttpHeaders;
import com.google.common.net.PercentEscaper;

public final class UploadFileBinder implements MapBinder {
   private static final PercentEscaper escaper = new PercentEscaper("._-/~!$'()*;=:@", false);

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      UploadUrlResponse uploadUrl = (UploadUrlResponse) postParams.get("uploadUrl");
      String fileName = (String) postParams.get("fileName");
      String contentSha1 = (String) postParams.get("contentSha1");
      if (contentSha1 == null) {
         contentSha1 = "do_not_verify";
      }
      Map<String, String> fileInfo = (Map<String, String>) postParams.get("fileInfo");
      HttpRequest.Builder builder = request.toBuilder()
            .endpoint(uploadUrl.uploadUrl())
            .replaceHeader(HttpHeaders.AUTHORIZATION, uploadUrl.authorizationToken())
            .replaceHeader(B2Headers.CONTENT_SHA1, contentSha1)
            .replaceHeader(B2Headers.FILE_NAME, escaper.escape(fileName));
      for (Map.Entry<String, String> entry : fileInfo.entrySet()) {
         builder.replaceHeader(B2Headers.FILE_INFO_PREFIX + entry.getKey(), escaper.escape(entry.getValue()));
      }
      return (R) builder.build();
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException();
   }
}
