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
package org.jclouds.googlecloudstorage.parser;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.regex.Pattern;

import org.jclouds.googlecloudstorage.domain.ResumableUpload;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.common.base.Splitter;

public class ParseToResumableUpload implements Function<HttpResponse, ResumableUpload> {

   @Override
   public ResumableUpload apply(HttpResponse response) {

      String contentLength = response.getFirstHeaderOrNull("Content-Length");
      String sessionUri = response.getFirstHeaderOrNull("Location");
      String uploadId = null;
      if (sessionUri != null) {
         uploadId = getUploadId(sessionUri);
      }
      String range = response.getFirstHeaderOrNull("Range");
      Long upperLimit = null;
      Long lowerLimit = null;
      if (range != null) {
         upperLimit = getUpperLimitFromRange(range);
         lowerLimit = getLowerLimitFromRange(range);
         if (lowerLimit != null && upperLimit != null) {
            checkArgument(lowerLimit < upperLimit, "lower range must less than upper range, was: %s - %s", lowerLimit,
                  upperLimit);
         }
      }

      return ResumableUpload.create(response.getStatusCode(), uploadId, contentLength, lowerLimit, upperLimit);
   }

   // Return the Id of the Upload
   private String getUploadId(String sessionUri) {
      // TODO: better way to parse query parameters?
      return Splitter.on(Pattern.compile("\\&")).trimResults().omitEmptyStrings().withKeyValueSeparator("=")
            .split(sessionUri).get("upload_id");
   }

   private long getUpperLimitFromRange(String range) {
      String upperLimit = range.split("-")[1];
      return Long.parseLong(upperLimit);
   }

   private long getLowerLimitFromRange(String range) {
      String removeByte = range.split("=")[1];
      String lowerLimit = removeByte.split("-")[0];
      return Long.parseLong(lowerLimit);
   }
}
