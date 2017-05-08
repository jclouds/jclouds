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

import java.util.Date;
import java.util.Map;

import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class B2Object {
   public abstract String fileId();
   public abstract String fileName();
   @Nullable public abstract String contentSha1();
   @Nullable public abstract Map<String, String> fileInfo();
   @Nullable public abstract Payload payload();
   @Nullable public abstract Date uploadTimestamp();
   @Nullable public abstract Action action();
   @Nullable public abstract String accountId();
   @Nullable public abstract String bucketId();
   @Nullable public abstract Long contentLength();
   @Nullable public abstract String contentType();
   @Nullable public abstract String contentRange();

   @SerializedNames({"fileId", "fileName", "accountId", "bucketId", "contentLength", "contentSha1", "contentType", "fileInfo", "action", "uploadTimestamp", "contentRange", "payload"})
   public static B2Object create(String fileId, String fileName, @Nullable String accountId, @Nullable String bucketId, @Nullable Long contentLength, @Nullable String contentSha1, @Nullable String contentType, @Nullable Map<String, String> fileInfo, @Nullable Action action, @Nullable Long uploadTimestamp, @Nullable String contentRange, @Nullable Payload payload) {
      if (fileInfo != null) {
         fileInfo = ImmutableMap.copyOf(fileInfo);
      }
      Date date = uploadTimestamp == null ? null : new Date(uploadTimestamp);
      return new AutoValue_B2Object(fileId, fileName, contentSha1, fileInfo, payload, date, action, accountId, bucketId, contentLength, contentType, contentRange);
   }
}
