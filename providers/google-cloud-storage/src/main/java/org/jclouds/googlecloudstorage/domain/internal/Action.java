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

import static com.google.common.base.Objects.toStringHelper;

import com.google.common.base.Objects;

/**
 * This is an Internal Object used in BucketLifeCycles/Rules.
 */

public class Action {
   private final String type;

   public Action(String type) {
      this.type = type;
   }

   public String getType() {
      return type;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(type);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Action other = (Action) obj;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("type", type);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String type;

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Action build() {
         return new Action(this.type);
      }

      public Builder fromAction(Action in) {
         return this.type(in.getType());

      }

   }

}
