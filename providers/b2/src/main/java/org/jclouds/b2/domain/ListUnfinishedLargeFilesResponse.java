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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class ListUnfinishedLargeFilesResponse {
   @Nullable public abstract String nextFileId();
   public abstract List<Entry> files();

   @SerializedNames({"nextFileId", "files"})
   public static ListUnfinishedLargeFilesResponse create(@Nullable String nextFileId, List<Entry> files) {
      return new AutoValue_ListUnfinishedLargeFilesResponse(nextFileId, ImmutableList.copyOf(files));
   }

   @AutoValue
   public abstract static class Entry {
      public abstract String accountId();
      public abstract String bucketId();
      public abstract String contentType();
      public abstract String fileId();
      public abstract Map<String, String> fileInfo();
      public abstract String fileName();
      public abstract Date uploadTimestamp();

      @SerializedNames({"accountId", "bucketId", "contentType", "fileId", "fileInfo", "fileName", "uploadTimestamp"})
      public static Entry create(String accountId, String bucketId, String contentType, String fileId, Map<String, String> fileInfo, String fileName, long uploadTimestamp) {
         return new AutoValue_ListUnfinishedLargeFilesResponse_Entry(accountId, bucketId, contentType, fileId, ImmutableMap.copyOf(fileInfo), fileName, new Date(uploadTimestamp));
      }
   }
}
