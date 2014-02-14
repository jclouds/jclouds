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

import com.google.common.base.Objects;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;

public class TagType {
   private final String keyName;
   private final String description;

   @ConstructorProperties({"keyName", "description"} )
   public TagType(@Nullable String keyName, @Nullable String description) {
      this.keyName = keyName;
      this.description = description;
   }

   public String getKeyName() {
      return keyName;
   }

   public String getDescription() {
      return description;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TagType that = (TagType) o;

      return Objects.equal(this.keyName, that.keyName) &&
              Objects.equal(this.description, that.description);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(keyName, description);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("keyName", keyName)
              .add("description", description)
              .toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromTagType(this);
   }

   public static class Builder {
      private String keyName;
      private String description;

      /**
       * @see org.jclouds.softlayer.domain.TagType#getKeyName()
       */
      public Builder keyName(String keyName) {
         this.keyName = keyName;
         return this;
      }

      /**
       * @see TagType#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public TagType build() {
         return new TagType(keyName, description);
      }

      public Builder fromTagType(TagType in) {
         return this
                 .keyName(in.getKeyName())
                 .description(in.getDescription());
      }
   }
}
