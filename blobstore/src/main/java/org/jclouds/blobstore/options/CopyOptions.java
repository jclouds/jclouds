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

package org.jclouds.blobstore.options;

import java.util.Date;
import java.util.Map;

import org.jclouds.io.ContentMetadata;
import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;

@AutoValue
@Beta
public abstract class CopyOptions {
   public static final CopyOptions NONE = builder().build();

   public static Builder builder() {
      return new AutoValue_CopyOptions.Builder();
   }

   @Nullable
   public abstract ContentMetadata contentMetadata();
   @Nullable
   public abstract Map<String, String> userMetadata();

   @Nullable
   public abstract Date ifModifiedSince();
   @Nullable
   public abstract Date ifUnmodifiedSince();
   @Nullable
   public abstract String ifMatch();
   @Nullable
   public abstract String ifNoneMatch();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder contentMetadata(ContentMetadata contentMetadata);
      public abstract Builder userMetadata(Map<String, String> userMetadata);

      public abstract Builder ifModifiedSince(Date ifModifiedSince);
      public abstract Builder ifUnmodifiedSince(Date ifUnmodifiedSince);
      public abstract Builder ifMatch(String ifMatch);
      public abstract Builder ifNoneMatch(String ifNoneMatch);

      abstract Map<String, String> userMetadata();
      abstract CopyOptions autoBuild();

      public CopyOptions build() {
         Map<String, String> userMetadata = userMetadata();
         if (userMetadata != null) {
            userMetadata(ImmutableMap.copyOf(userMetadata));
         }
         return autoBuild();
      }
   }
}
