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

package org.jclouds.azureblob.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public final class CopyBlobOptions {
   public static final CopyBlobOptions NONE = CopyBlobOptions.builder().build();

   private final Optional<Map<String, String>> userMetadata;
   private final Optional<Date> ifModifiedSince;
   private final Optional<Date> ifUnmodifiedSince;
   private final Optional<String> ifMatch;
   private final Optional<String> ifNoneMatch;

   private CopyBlobOptions(Map<String, String> userMetadata, Date ifModifiedSince, Date ifUnmodifiedSince,
         String ifMatch, String ifNoneMatch) {
      this.userMetadata = Optional.fromNullable(userMetadata);
      this.ifModifiedSince = Optional.fromNullable(ifModifiedSince);
      this.ifUnmodifiedSince = Optional.fromNullable(ifUnmodifiedSince);
      this.ifMatch = Optional.fromNullable(ifMatch);
      this.ifNoneMatch = Optional.fromNullable(ifNoneMatch);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Optional<Map<String, String>> getUserMetadata() {
      return userMetadata;
   }

   public Optional<Date> getIfModifiedSince() {
      return Optional.fromNullable(ifModifiedSince.isPresent() ? (Date) ifModifiedSince.get().clone() : null);
   }

   public Optional<Date> getIfUnmodifiedSince() {
      return Optional.fromNullable(ifUnmodifiedSince.isPresent() ? (Date) ifUnmodifiedSince.get().clone() : null);
   }

   public Optional<String> getIfMatch() {
      return ifMatch;
   }

   public Optional<String> getIfNoneMatch() {
      return ifNoneMatch;
   }

   public static class Builder {
      private Map<String, String> userMetadata;
      private Date ifModifiedSince;
      private Date ifUnmodifiedSince;
      private String ifMatch;
      private String ifNoneMatch;

      Builder() {
      }

      public Builder overrideUserMetadata(Map<String, String> userMetadata) {
         this.userMetadata = ImmutableMap.copyOf(checkNotNull(userMetadata, "userMetadata"));
         return this;
      }

      public Builder ifModifiedSince(Date ifModifiedSince) {
         this.ifModifiedSince = (Date) checkNotNull(ifModifiedSince, "ifModifiedSince").clone();
         return this;
      }

      public Builder ifUnmodifiedSince(Date ifUnmodifiedSince) {
         this.ifUnmodifiedSince = (Date) checkNotNull(ifUnmodifiedSince, "ifUnmodifiedSince").clone();
         return this;
      }

      public Builder ifMatch(String ifMatch) {
         this.ifMatch = checkNotNull(ifMatch, "ifMatch");
         return this;
      }

      public Builder ifNoneMatch(String ifNoneMatch) {
         this.ifNoneMatch = checkNotNull(ifNoneMatch, "ifNoneMatch");
         return this;
      }

      public CopyBlobOptions build() {
         return new CopyBlobOptions(userMetadata, ifModifiedSince, ifUnmodifiedSince, ifMatch, ifNoneMatch);
      }
   }
}
