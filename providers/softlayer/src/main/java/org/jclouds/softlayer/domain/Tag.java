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
package org.jclouds.softlayer.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.ConstructorProperties;

import com.google.common.base.Objects;

public class Tag {
   private final int accountId;
   private final int id;
   private final int internal;
   private final String name;

   @ConstructorProperties({"accountId", "id", "internal", "name"} )
   public Tag(int accountId, int id, int internal, String name) {
      this.accountId = accountId;
      this.id = id;
      this.internal = internal;
      this.name = checkNotNull(name, "name");
   }

   public int getAccountId() {
      return accountId;
   }

   public int getId() {
      return id;
   }

   public int getInternal() {
      return internal;
   }

   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Tag that = (Tag) o;

      return Objects.equal(this.accountId, that.accountId) &&
              Objects.equal(this.id, that.id) &&
              Objects.equal(this.internal, that.internal) &&
              Objects.equal(this.name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accountId, id, internal, name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("accountId", accountId)
              .add("id", id)
              .add("internal", internal)
              .add("name", name)
              .toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromTag(this);
   }

   public static class Builder {
      private int accountId;
      private int id;
      private int internal;
      private String name;

      /**
       * @see org.jclouds.softlayer.domain.Tag#getAccountId()
       */
      public Builder accountId(int accountId) {
         this.accountId = accountId;
         return this;
      }

      /**
       * @see Tag#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.Tag#getInternal()
       */
      public Builder internal(int internal) {
         this.internal = internal;
         return this;
      }

      /**
       * @see Tag#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Tag build() {
         return new Tag(accountId, id, internal, name);
      }

      public Builder fromTag(Tag in) {
         return this
                 .accountId(in.getAccountId())
                 .id(in.getId())
                 .internal(in.getInternal())
                 .name(in.getName());
      }
   }
}
