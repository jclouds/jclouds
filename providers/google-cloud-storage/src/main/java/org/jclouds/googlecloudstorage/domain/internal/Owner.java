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
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * This is an internal object used in both Bucket and Object representation
 */

public class Owner {
   private final String entity;
   private final String entityId;

   private Owner(String entity, @Nullable String entityId) {
      this.entity = checkNotNull(entity, "entity");
      this.entityId = entityId;
   }

   public String getEntity() {
      return entity;
   }

   public String getEntityId() {
      return entityId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(entity, entityId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Owner that = Owner.class.cast(obj);
      return equal(this.entity, that.entity);
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("entity", entity).add("entityId", entityId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String entity;
      private String entityId;

      public Builder entity(String entity) {
         this.entity = entity;
         return this;
      }

      public Builder entityId(String entityId) {
         this.entityId = entityId;
         return this;
      }

      public Owner build() {
         return new Owner(this.entity, this.entityId);
      }

      public Builder fromOwner(Owner in) {
         return this.entity(in.getEntity()).entityId(in.getEntityId());
      }

   }
}
