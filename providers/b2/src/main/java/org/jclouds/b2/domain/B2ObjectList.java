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
public abstract class B2ObjectList {
   public abstract List<Entry> files();
   @Nullable public abstract String nextFileId();
   @Nullable public abstract String nextFileName();

   @SerializedNames({"files", "nextFileId", "nextFileName"})
   public static B2ObjectList create(List<Entry> files, @Nullable String nextFileId, @Nullable String nextFileName) {
      return new AutoValue_B2ObjectList(ImmutableList.copyOf(files), nextFileId, nextFileName);
   }

   @AutoValue
   public abstract static class Entry {
      public abstract Action action();
      public abstract String accountId();
      public abstract String bucketId();
      @Nullable public abstract String fileId();
      public abstract String fileName();
      public abstract long contentLength();
      @Deprecated
      public long size() {
         return contentLength();
      }
      public abstract Date uploadTimestamp();

      @SerializedNames({"action", "accountId", "bucketId", "fileId", "fileName", "contentLength", "uploadTimestamp"})
      public static Entry create(Action action, String accountId, String bucketId, @Nullable String fileId, String fileName, long contentLength, long uploadTimestamp) {
         return new AutoValue_B2ObjectList_Entry(action, accountId, bucketId, fileId, fileName, contentLength, new Date(uploadTimestamp));
      }
   }
}
