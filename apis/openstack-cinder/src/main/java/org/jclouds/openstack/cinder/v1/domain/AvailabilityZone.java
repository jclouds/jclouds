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
package org.jclouds.openstack.cinder.v1.domain;

import com.google.common.base.Objects;
import java.beans.ConstructorProperties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * availability zone for cinder
 */
public class AvailabilityZone {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromAvailabilityZone(this);
   }

   public static class Builder {

      protected boolean available;
      protected String name;

      /**
       * @see AvailabilityZone#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see AvailabilityZone#getZoneState()
       */
      public Builder available(boolean available) {
         this.available = available;
         return this;
      }


      public AvailabilityZone build() {
         return new AvailabilityZone(name, new ZoneState(available));
      }

      public Builder fromAvailabilityZone(AvailabilityZone in) {
         return this
               .name(in.getName())
               .available(in.getZoneState().available());
      }
   }

   private final String name;
   private final ZoneState state;

   @ConstructorProperties({"zoneName", "zoneState"})
   protected AvailabilityZone(String name, ZoneState zoneState) {
      this.name = checkNotNull(name);
      this.state = checkNotNull(zoneState);
   }

   public String getName() {
      return this.name;
   }

   public ZoneState getZoneState() {
      return this.state;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, state);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AvailabilityZone that = AvailabilityZone.class.cast(obj);
      return Objects.equal(this.name, that.name) && Objects.equal(this.state, that.state);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("state", state);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
