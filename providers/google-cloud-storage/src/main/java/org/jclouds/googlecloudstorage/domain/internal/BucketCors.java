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

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * The bucket's Cross-Origin Resource Sharing (CORS) configuration.
 *
 * @see <a href= "https://developers.google.com/storage/docs/cross-origin" />
 */

public class BucketCors {
   private final Set<String> origin;
   private final Set<String> method;
   private final Set<String> responseHeader;
   private final Integer maxAgeSeconds;
   
   private BucketCors(Set<String> origin, Set<String> method, Set<String> responseHeader,
            Integer maxAgeSeconds) {
      this.origin = origin.isEmpty() ? null : origin;
      this.method = method.isEmpty() ? null : method;
      this.responseHeader = responseHeader.isEmpty() ? null : responseHeader;
      this.maxAgeSeconds = maxAgeSeconds;
   }

   public Set<String> getOrigin() {
      return origin;
   }

   public Set<String> getMethod() {
      return method;
   }

   public Set<String> getResponseHeader() {
      return responseHeader;
   }

   public Integer getMaxAgeSeconds() {
      return maxAgeSeconds;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(origin, method, responseHeader, maxAgeSeconds);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      BucketCors that = BucketCors.class.cast(obj);
      return equal(this.origin, that.origin) && equal(this.method, that.method)
               && equal(this.responseHeader, that.responseHeader) && equal(this.maxAgeSeconds, that.maxAgeSeconds);
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("origin", origin).add("method", method).add("responseHeader", responseHeader)
               .add("maxAgeSeconds", maxAgeSeconds);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private ImmutableSet.Builder<String> origin = ImmutableSet.builder();
      private ImmutableSet.Builder<String> method = ImmutableSet.builder();
      private ImmutableSet.Builder<String> reponseHeader = ImmutableSet.builder();
      private Integer maxAgeSeconds;

      public Builder addOrigin(String origin) {
         this.origin.add(origin);
         return this;
      }

      public Builder origin(Set<String> origin) {
         this.origin.addAll(origin);
         return this;
      }

      public Builder addMethod(String method) {
         this.method.add(method);
         return this;
      }

      public Builder method(Set<String> method) {
         this.method.addAll(method);
         return this;
      }

      public Builder addResponseHeader(String responseHeader) {
         this.reponseHeader.add(responseHeader);
         return this;
      }

      public Builder responseHeaders(Set<String> responseHeaders) {
         this.reponseHeader.addAll(responseHeaders);
         return this;
      }

      public Builder maxAgeSeconds(Integer maxAgeSeconds) {
         this.maxAgeSeconds = maxAgeSeconds;
         return this;
      }

      public BucketCors build() {
         return new BucketCors(this.origin.build(), this.method.build(), this.reponseHeader.build(),
                  this.maxAgeSeconds);
      }

      public Builder fromCors(BucketCors in) {
         return this.maxAgeSeconds(in.getMaxAgeSeconds()).origin(in.getOrigin()).method(in.getMethod())
                  .responseHeaders(in.getResponseHeader());
      }

   }

}
