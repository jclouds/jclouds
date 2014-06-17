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
package org.jclouds.googlecloudstorage.domain;

import static com.google.common.base.Objects.ToStringHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Base class for Google Cloud Storage resources.
 */

public class Resource {

   public enum Kind {
      BUCKET_ACCESS_CONTROL, BUCKET_ACCESS_CONTROLS, BUCKET, BUCKETS, OBJECT_ACCESS_CONTROL, OBJECT_ACCESS_CONTROLS, OBJECT;

      public String value() {
         return Joiner.on("#").join("storage", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name()));
      }

      @Override
      public String toString() {
         return value();
      }

      public static Kind fromValue(String kind) {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat
                 .UPPER_UNDERSCORE,
                 Iterables.getLast(Splitter.on("#").split(checkNotNull(kind,
                         "kind")))));
      }
   }

   protected final Kind kind;
   protected final String id;
   protected final URI selfLink;
   protected final String etag;

   @ConstructorProperties({ "kind", "id", "selfLink", "etag" })
   protected Resource(Kind kind, String id, URI selfLink, String etag) {
      this.kind = checkNotNull(kind, "kind");
      this.id = id;
      this.selfLink = selfLink;
      this.etag = etag;
   }

   public Kind getKind() {
      return kind;
   }

   public String getId() {
      return id;
   }

   public URI getSelfLink() {
      return selfLink;
   }

   public String getEtag() {
      return etag;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, selfLink, etag);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Resource that = Resource.class.cast(obj);
      return equal(this.kind, that.kind) && equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("kind", kind).add("id", id).add("selfLink", selfLink)
               .add("etag", etag);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromResource(this);
   }

   public abstract static class Builder<T extends Builder<T>> {

      protected abstract T self();

      protected Kind kind;
      protected String id;
      protected URI selfLink;
      protected String etag;

      protected T kind(Kind kind) {
         this.kind = kind;
         return self();
      }

      public T id(String id) {
         this.id = id;
         return self();
      }

      public T selfLink(URI selfLink) {
         this.selfLink = selfLink;
         return self();
      }

      public T etag(String etag) {
         this.etag = etag;
         return self();
      }

      public Resource build() {
         return new Resource(kind, id, selfLink, etag);
      }

      public T fromResource(Resource in) {
         return this.kind(in.getKind()).id(in.getId()).selfLink(in.getSelfLink()).etag(in.getEtag());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
