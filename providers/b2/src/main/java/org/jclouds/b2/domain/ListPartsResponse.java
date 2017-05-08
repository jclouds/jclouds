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
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class ListPartsResponse {
   @Nullable public abstract Integer nextPartNumber();
   public abstract List<Entry> parts();

   @SerializedNames({"nextPartNumber", "parts"})
   public static ListPartsResponse create(@Nullable Integer nextPartNumber, List<Entry> parts) {
      return new AutoValue_ListPartsResponse(nextPartNumber, ImmutableList.copyOf(parts));
   }

   @AutoValue
   public abstract static class Entry {
      public abstract long contentLength();
      public abstract String contentSha1();
      public abstract String fileId();
      public abstract int partNumber();
      public abstract Date uploadTimestamp();

      @SerializedNames({"contentLength", "contentSha1", "fileId", "partNumber", "uploadTimestamp"})
      public static Entry create(long contentLength, String contentSha1, String fileId, int partNumber, long uploadTimestamp) {
         return new AutoValue_ListPartsResponse_Entry(contentLength, contentSha1, fileId, partNumber, new Date(uploadTimestamp));
      }
   }
}
