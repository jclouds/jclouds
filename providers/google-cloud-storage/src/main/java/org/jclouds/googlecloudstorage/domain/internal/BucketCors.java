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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * The bucket's Cross-Origin Resource Sharing (CORS) configuration.
 *
 * @see <a href= "https://developers.google.com/storage/docs/cross-origin" />
 */

public final class BucketCors {
   private final Set<String> origins;
   private final Set<String> methods;
   private final Set<String> responseHeaders;
   private final Integer maxAgeSeconds;

   public BucketCors(@Nullable Set<String> origin, @Nullable Set<String> method, @Nullable Set<String> responseHeader,
            Integer maxAgeSeconds) {

      this.origins = origin == null ? ImmutableSet.<String> of() : origin;
      this.methods = method == null ? ImmutableSet.<String> of() : method;
      this.responseHeaders = responseHeader == null ? ImmutableSet.<String> of() : responseHeader;
      this.maxAgeSeconds = maxAgeSeconds;
   }

   public Set<String> getOrigin() {
      return origins;
   }

   public Set<String> getMethod() {
      return methods;
   }

   public Set<String> getResponseHeader() {
      return responseHeaders;
   }

   public Integer getMaxAgeSeconds() {
      return maxAgeSeconds;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(origins, methods, responseHeaders, maxAgeSeconds);
   }

   /* TODO -Check equals */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      BucketCors that = BucketCors.class.cast(obj);
      return equal(this.origins, that.origins) && equal(this.methods, that.methods)
               && equal(this.responseHeaders, that.responseHeaders) && equal(this.maxAgeSeconds, that.maxAgeSeconds);
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("origin", origins).add("method", methods).add("responseHeader", responseHeaders)
               .add("maxAgeSeconds", maxAgeSeconds);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private ImmutableSet.Builder<String> origins = ImmutableSet.builder();
      private ImmutableSet.Builder<String> methods = ImmutableSet.builder();
      private ImmutableSet.Builder<String> reponseHeaders = ImmutableSet.builder();
      private Integer maxAgeSeconds;

      public Builder addOrigin(String origin) {
         this.origins.add(origin);
         return this;
      }

      public Builder origin(Set<String> origin) {
         this.origins.addAll(origin);
         return this;
      }

      public Builder addMethod(String method) {
         this.methods.add(method);
         return this;
      }

      public Builder method(Set<String> method) {
         this.methods.addAll(method);
         return this;
      }

      public Builder addResponseHeader(String responseHeader) {
         this.reponseHeaders.add(responseHeader);
         return this;
      }

      public Builder responseHeader(Set<String> responseHeader) {
         this.reponseHeaders.addAll(responseHeader);
         return this;
      }

      public Builder maxAgeSeconds(Integer maxAgeSeconds) {
         this.maxAgeSeconds = maxAgeSeconds;
         return this;
      }

      public BucketCors build() {
         return new BucketCors(this.origins.build(), this.methods.build(), this.reponseHeaders.build(),
                  this.maxAgeSeconds);
      }

      public Builder fromCors(BucketCors c) {
         return this.maxAgeSeconds(c.getMaxAgeSeconds()).origin(c.getOrigin()).method(c.getMethod())
                  .responseHeader(c.getResponseHeader());
      }

   }

}
