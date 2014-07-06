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
 * The bucket's versioning configuration.
 *
 * @see <a href= "https://developers.google.com/storage/docs/object-versioning" />
 */

public final class Versioning {
   private final Boolean enabled;

   private Versioning(Boolean enabled) {
      this.enabled = enabled;
   }

   public Boolean isEnabled() {
      return enabled;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(enabled);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Versioning other = (Versioning) obj;
      if (enabled == null) {
         if (other.enabled != null)
            return false;
      } else if (!enabled.equals(other.enabled))
         return false;
      return true;
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("enabled", enabled);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private Boolean enabled;

      public Builder enalbled(Boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      public Versioning build() {
         return new Versioning(this.enabled);
      }

      public Builder fromVersioning(Versioning in) {
         return this.enalbled(in.isEnabled());
      }

   }

}
