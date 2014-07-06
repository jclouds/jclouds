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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * This is a internal object in bucket resource
 *
 * @see <a href= "https://developers.google.com/storage/docs/website-configuration" />
 */

public class Website {
   private final String mainPageSuffix;
   private final String notFoundPage;

   private Website(@Nullable String mainPageSuffix, @Nullable String notFoundPage) {

      this.mainPageSuffix = mainPageSuffix;
      this.notFoundPage = notFoundPage;
   }

   public String getMainPageSuffix() {
      return mainPageSuffix;
   }

   public String getNotFoundPage() {
      return notFoundPage;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(mainPageSuffix, notFoundPage);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Website that = Website.class.cast(obj);
      return equal(this.mainPageSuffix, that.mainPageSuffix);
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("mainPageSuffix", mainPageSuffix).add("notFoundPage", notFoundPage);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String mainPageSuffix;
      private String notFoundPage;

      public Builder mainPageSuffix(String mainPageSuffix) {
         this.mainPageSuffix = mainPageSuffix;
         return this;
      }

      public Builder notFoundPage(String notFoundPage) {
         this.notFoundPage = notFoundPage;
         return this;
      }

      public Website build() {
         return new Website(this.mainPageSuffix, this.notFoundPage);
      }

      public Builder fromWebsite(Website in) {
         return this.mainPageSuffix(in.getMainPageSuffix()).notFoundPage(in.getNotFoundPage());
      }

   }

}
