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
package org.jclouds.b2.domain;

import java.util.Map;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class UploadFileResponse {
   public abstract String fileId();
   public abstract String fileName();
   public abstract String accountId();
   public abstract String bucketId();
   public abstract long contentLength();
   public abstract String contentSha1();
   public abstract String contentType();
   public abstract Map<String, String> fileInfo();

   @SerializedNames({"fileId", "fileName", "accountId", "bucketId", "contentLength", "contentSha1", "contentType", "fileInfo"})
   public static UploadFileResponse create(String fileId, String fileName, String accountId, String bucketId, long contentLength, String contentSha1, String contentType, Map<String, String> fileInfo) {
      return new AutoValue_UploadFileResponse(fileId, fileName, accountId, bucketId, contentLength, contentSha1, contentType, ImmutableMap.copyOf(fileInfo));
   }
}
