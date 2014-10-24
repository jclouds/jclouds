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

import java.util.Set;

import org.jclouds.googlecloudstorage.domain.Resource.Kind;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

public class ListObjectAccessControls {

   protected final Kind kind;
   protected final Set<ObjectAccessControls> items;

   protected ListObjectAccessControls(Kind kind, Set<ObjectAccessControls> items) {

      this.kind = checkNotNull(kind, "kind");
      this.items = checkNotNull(items, "items");
   }

   public Kind getKind() {
      return kind;
   }

   public Set<ObjectAccessControls> getItems() {
      return items;
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
      ListObjectAccessControls that = ListObjectAccessControls.class.cast(obj);
      return equal(this.kind, that.kind) && equal(this.items, that.items);

   }

   protected ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("kind", kind).add("items", items);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromListObjectAccessControls(this);
   }

   public static final class Builder {

      private Kind kind;
      private ImmutableSet.Builder<ObjectAccessControls> items = ImmutableSet.builder();

      public Builder kind(Kind kind) {
         this.kind = checkNotNull(kind , "kind");
         return this;
      }

      public Builder addItems(ObjectAccessControls bucketAccessControls) {
         this.items.add(bucketAccessControls);
         return this;
      }

      public Builder items(Set<ObjectAccessControls> items) {
         this.items.addAll(items);
         return this;
      }

      public ListObjectAccessControls build() {
         return new ListObjectAccessControls(this.kind, items.build());
      }

      public Builder fromListObjectAccessControls(ListObjectAccessControls in) {
         return this.kind(in.getKind()).items(in.getItems());
      }
   }
}
