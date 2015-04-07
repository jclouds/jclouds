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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.io.ContentMetadata;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

@Beta
public final class CopyOptions {
   public static final CopyOptions NONE = builder().build();

   private final Optional<ContentMetadata> contentMetadata;
   private final Optional<Map<String, String>> userMetadata;

   private CopyOptions(Builder builder) {
      this.contentMetadata = Optional.fromNullable(builder.contentMetadata);
      this.userMetadata = Optional.fromNullable(builder.userMetadata);
   }

   public Optional<ContentMetadata> getContentMetadata() {
      return contentMetadata;
   }

   public Optional<Map<String, String>> getUserMetadata() {
      return userMetadata;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      ContentMetadata contentMetadata;
      Map<String, String> userMetadata;

      Builder() {
      }

      public Builder contentMetadata(ContentMetadata contentMetadata) {
         this.contentMetadata = checkNotNull(contentMetadata, "contentMetadata");
         return this;
      }

      public Builder userMetadata(Map<String, String> userMetadata) {
         this.userMetadata = ImmutableMap.copyOf(userMetadata);
         return this;
      }

      public CopyOptions build() {
          return new CopyOptions(this);
      }
   }
}
