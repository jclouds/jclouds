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
package org.jclouds.packet.domain.internal;

import java.util.Iterator;
import java.util.List;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.packet.domain.Href;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for all collections that return paginated results.
 */
public abstract class PaginatedCollection<T> extends IterableWithMarker<T> {

   @AutoValue
   public abstract static class Meta {
      public abstract long total();
      @Nullable public abstract Href first();
      @Nullable public abstract Href previous();
      @Nullable public abstract Href self();
      @Nullable public abstract Href next();
      @Nullable public abstract Href last();

      @SerializedNames({ "total", "first", "previous", "self", "next", "last" })
      public static Meta create(long total, Href first, Href previous, Href self, Href next, Href last) {
         return new AutoValue_PaginatedCollection_Meta(total, first, previous, self, next, last);
      }

      Meta() { }
   }

   private final List<T> items;
   private final Meta meta;

   protected PaginatedCollection(List<T> items, Meta meta) {
      this.items = ImmutableList.copyOf(checkNotNull(items, "items cannot be null"));
      this.meta = meta;
   }

   public List<T> items() {
      return items;
   }

   public Meta meta() {
      return meta;
   }  

   @Override
   public Iterator<T> iterator() {
      return items.iterator();
   }

   @Override
   public Optional<Object> nextMarker() {
      if (meta == null || meta.next() == null) {
         return Optional.absent();
      }
      return Optional.fromNullable((Object) meta.next());
   }

}
