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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Iterator;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * The collection returned from any <code>listFirstPage()</code> method.
 */
public class ListPage<T> extends IterableWithMarker<T> {

   private final Kind kind;
   private final String nextPageToken;
   private final Iterable<T> items;

   @ConstructorProperties({ "kind", "nextPageToken", "items" })
   protected ListPage(Kind kind, String nextPageToken, Iterable<T> items) {

      this.kind = checkNotNull(kind, "kind");
      this.nextPageToken = nextPageToken;
      this.items = items != null ? ImmutableSet.copyOf(items) : ImmutableSet.<T> of();
   }

   public Kind getKind() {
      return kind;
   }

   @Override
   public Optional<Object> nextMarker() {
      return Optional.<Object> fromNullable(nextPageToken);
   }

   @Override
   public Iterator<T> iterator() {
      return checkNotNull(items, "items").iterator();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(kind, items);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ListPage<?> that = ListPage.class.cast(obj);
      return equal(this.kind, that.kind) && equal(this.items, that.items);
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("kind", kind).add("nextPageToken", nextPageToken)
               .add("items", items);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static <T> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromPagedList(this);
   }

   public static final class Builder<T> {

      private Kind kind;
      private String nextPageToken;
      private ImmutableSet.Builder<T> items = ImmutableSet.builder();

      public Builder<T> kind(Kind kind) {
         this.kind = kind;
         return this;
      }

      public Builder<T> addItem(T item) {
         this.items.add(item);
         return this;
      }

      public Builder<T> items(Iterable<T> items) {
         this.items.addAll(items);
         return this;
      }

      public Builder<T> nextPageToken(String nextPageToken) {
         this.nextPageToken = nextPageToken;
         return this;
      }

      public ListPage<T> build() {
         return new ListPage<T>(kind, nextPageToken, items.build());
      }

      public Builder<T> fromPagedList(ListPage<T> in) {
         return this.kind(in.getKind()).nextPageToken((String) in.nextMarker().orNull()).items(in);

      }
   }
}
