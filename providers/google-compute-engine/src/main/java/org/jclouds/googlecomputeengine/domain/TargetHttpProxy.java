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
package org.jclouds.googlecomputeengine.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;

/**
 * A target http proxy resource.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/targetHttpProxies"/>
 * @see <a href="https://developers.google.com/compute/docs/load-balancing/http/target-http-proxy"/>
 */
@Beta
public final class TargetHttpProxy extends Resource {

   private final URI urlMap;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "urlMap",
   })
   private TargetHttpProxy(String id, Date creationTimestamp, URI selfLink, String name,
                           @Nullable String description, URI urlMap) {
      super(Kind.TARGET_HTTP_PROXY, id, creationTimestamp, selfLink, name, description);
      this.urlMap = checkNotNull(urlMap, "urlMap");
   }

   /**
    * @return the url map this proxy points to.
    */
   public URI getUrlMap() {
      return urlMap;
   }
   
   /**
    *  {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, name, urlMap);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      TargetHttpProxy that = TargetHttpProxy.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.urlMap, that.urlMap);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("urlMap", urlMap);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTargetHttpProxy(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private URI urlMap;
      
      /**
       * @see TargetHttpProxy#getUrlMap()
       */
      public Builder urlMap(URI urlMap) {
         this.urlMap = urlMap;
         return this;
      }
      
      @Override
      protected Builder self() {
         return this;
      }

      public TargetHttpProxy build() {
         return new TargetHttpProxy(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, urlMap);
      }

      public Builder fromTargetHttpProxy(TargetHttpProxy in) {
         return super.fromResource(in)
                 .urlMap(in.getUrlMap());
      }

   }
}
