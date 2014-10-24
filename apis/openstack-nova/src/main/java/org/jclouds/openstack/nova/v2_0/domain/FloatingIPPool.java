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
package org.jclouds.openstack.nova.v2_0.domain;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;

/**
 * A pool that holds a collection of floating IP's that can be allocated from it.
 */
public class FloatingIPPool implements Comparable<FloatingIPPool> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromFloatingIPPool(this);
   }

   public static class Builder {

      protected String name;

      /**
       * @see FloatingIPPool#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public FloatingIPPool build() {
         return new FloatingIPPool(name);
      }

      public Builder fromFloatingIPPool(FloatingIPPool in) {
         return this
               .name(in.getName());
      }
   }


   private final String name;


   @ConstructorProperties({
         "name"
   })
   protected FloatingIPPool(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      FloatingIPPool that = FloatingIPPool.class.cast(obj);
      return Objects.equal(this.name, that.name);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(FloatingIPPool o) {
      return this.name.compareTo(o.getName());
   }

}
