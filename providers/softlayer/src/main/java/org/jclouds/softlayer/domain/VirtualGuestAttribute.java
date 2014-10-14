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

public class VirtualGuestAttribute {
   private final String value;

   @ConstructorProperties({"value"} )
   public VirtualGuestAttribute(String value) {
      this.value = checkNotNull(value, "value");
   }

   public String getValue() {
      return value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualGuestAttribute that = (VirtualGuestAttribute) o;

      return Objects.equal(this.value, that.value);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(value);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("value", value)
              .toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVirtualGuestAttribute(this);
   }

   public static class Builder {
      private String value;

      /**
       * @see VirtualGuestAttribute#getValue()
       */
      public Builder value(String value) {
         this.value = value;
         return this;
      }

      public VirtualGuestAttribute build() {
         return new VirtualGuestAttribute(value);
      }

      public Builder fromVirtualGuestAttribute(VirtualGuestAttribute in) {
         return this
                 .value(in.getValue());
      }
   }
}
