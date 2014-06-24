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
package org.jclouds.googlecloudstorage.domain.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import com.google.common.base.Objects;

/**
 * The bucket's logging configuration, which defines the destination bucket and optional name prefix for the current
 * bucket's logs.
 *
 * @see <a href= "https://developers.google.com/storage/docs/accesslogs" />
 */

public final class Logging {
   private final String logBucket;
   private final String logObjectPrefix;

   public Logging(String logBucket, String logObjectPrefix) {

      this.logBucket = logBucket;
      this.logObjectPrefix = logObjectPrefix;
   }

   public String getLogBucket() {
      return logBucket;
   }

   public String getLogObjectPrefix() {
      return logObjectPrefix;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(logBucket, logObjectPrefix);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Logging that = Logging.class.cast(obj);
      return equal(this.logBucket, that.logBucket) && equal(this.logObjectPrefix, that.logObjectPrefix);
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("logBucket", logBucket).add("logObjectPrefix", logObjectPrefix);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String logBucket;
      private String logObjectPrefix;

      public Builder logBucket(String logBucket) {
         this.logBucket = logBucket;
         return this;
      }

      public Builder logObjectPrefix(String logObjectPrefix) {
         this.logObjectPrefix = logObjectPrefix;
         return this;
      }

      public Logging build() {
         return new Logging(this.logBucket, this.logObjectPrefix);
      }

      public Builder fromLogging(Logging in) {
         return this.logBucket(in.getLogBucket()).logObjectPrefix(in.getLogObjectPrefix());
      }

   }

}
