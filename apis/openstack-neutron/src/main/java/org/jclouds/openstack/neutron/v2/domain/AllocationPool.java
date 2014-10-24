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
package org.jclouds.openstack.neutron.v2.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * A Neutron Subnet Allocation Pool
 * Contains a start and an end IP address describing the pool.
 *
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-network/2.0/content/Subnets.html">api
 *      doc</a>
 */
public class AllocationPool {

   protected final String start;
   protected final String end;

   @ConstructorProperties({"start", "end"})
   protected AllocationPool(String start, String end) {
      this.start = start;
      this.end = end;
   }

   /**
    * @return the start of the AllocationPool
    */
   @Nullable
   public String getStart() {
      return start;
   }

   /**
    * @return the end of the AllocationPool
    */
   @Nullable
   public String getEnd() {
      return end;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(start, end);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      AllocationPool that = AllocationPool.class.cast(obj);
      return Objects.equal(this.start, that.start) && Objects.equal(this.end, that.end);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this).add("start", start).add("end", end);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * @return the Builder for AllocationPool
    */
   public static Builder builder() {
      return new Builder();
   }

   /**
    * Gets a Builder configured as this object.
    */
   public Builder toBuilder() {
      return new Builder().fromAllocationPool(this);
   }

   public static class Builder {
      protected String start;
      protected String end;

      /**
       * Provide the start to the AllocationPool's Builder.
       *
       * @return the Builder.
       * @see AllocationPool#getStart()
       */
      public Builder start(String start) {
         this.start = start;
         return this;
      }

      /**
       * Provide the end to the AllocationPool's Builder.
       *
       * @return the Builder.
       * @see AllocationPool#getEnd()
       */
      public Builder end(String end) {
         this.end = end;
         return this;
      }

      /**
       * @return a AllocationPool constructed with this Builder.
       */
      public AllocationPool build() {
         return new AllocationPool(start, end);
      }

      /**
       * @return a Builder from another AllocationPool.
       */
      public Builder fromAllocationPool(AllocationPool in) {
         return this.start(in.getStart()).end(in.getEnd());
      }
   }
}
