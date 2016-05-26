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
package org.jclouds.b2.functions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

import org.jclouds.http.HttpResponse;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.b2.domain.B2Object;
import org.jclouds.b2.reference.B2Headers;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;

public final class ParseB2ObjectFromResponse implements Function<HttpResponse, B2Object> {
   @Override
   public B2Object apply(HttpResponse from) {
      Payload payload = from.getPayload();
      MutableContentMetadata contentMeta = payload.getContentMetadata();

      String fileId = from.getFirstHeaderOrNull(B2Headers.FILE_ID);
      String fileName;
      try {
         fileName = URLDecoder.decode(from.getFirstHeaderOrNull(B2Headers.FILE_NAME), "UTF-8");
      } catch (UnsupportedEncodingException uee) {
         throw Throwables.propagate(uee);
      }
      String contentSha1 = from.getFirstHeaderOrNull(B2Headers.CONTENT_SHA1);
      ImmutableMap.Builder<String, String> fileInfo = ImmutableMap.builder();
      for (Map.Entry<String, String> entry : from.getHeaders().entries()) {
         if (entry.getKey().regionMatches(true, 0, B2Headers.FILE_INFO_PREFIX, 0, B2Headers.FILE_INFO_PREFIX.length())) {
            String value;
            try {
               value = URLDecoder.decode(entry.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException uee) {
               throw Throwables.propagate(uee);
            }
            fileInfo.put(entry.getKey().substring(B2Headers.FILE_INFO_PREFIX.length()), value);
         }
      }
      Date uploadTimestamp = new Date(Long.parseLong(from.getFirstHeaderOrNull(B2Headers.UPLOAD_TIMESTAMP)));
      String contentRange = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_RANGE);

      return B2Object.create(fileId, fileName, null, null, contentMeta.getContentLength(), contentSha1, contentMeta.getContentType(), fileInfo.build(), null, uploadTimestamp.getTime(), contentRange, payload);
   }
}
