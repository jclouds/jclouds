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
package org.jclouds.openstack.nova.v2_0.domain.regionscoped;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;

/**
 * Availability Zone which show name, state and host information.
 *
 * Host information includes the host name and the services running on the hosts.
 */
public class AvailabilityZoneDetails {

   public static final class HostService {

      private final boolean available;
      private final boolean active;
      @SerializedName("updated_at")
      private final Date updated;

      @ConstructorProperties({"available", "active", "updated_at"})
      protected HostService(boolean available, boolean active, Date updated) {
         this.available = available;
         this.active = active;
         this.updated = updated;
      }

      public boolean isAvailable() { return available; }

      public boolean isActive() { return active; }

      public Date getUpdated() { return updated; }

      protected Objects.ToStringHelper string() {
         return Objects.toStringHelper(this)
               .add("available", available)
               .add("active", active)
               .add("updated", updated);
      }

      @Override
      public String toString() {
         return string().toString();
      }
   }

   @SerializedName("zoneName")
   private final String name;
   private final ZoneState state;
   private final Map<String, Map<String, HostService>> hosts;

   @ConstructorProperties({"zoneName" , "zoneState", "hosts"})
   protected AvailabilityZoneDetails(String name, ZoneState state, Map<String, Map<String, HostService>> hosts) {
      this.name = name;
      this.state = state;
      this.hosts = hosts == null ? ImmutableMap.<String, Map<String, HostService>>of() : ImmutableMap.copyOf(hosts);
   }

   public String getName() {
      return name;
   }

   public ZoneState getState() {
      return state;
   }

   /**
    * @return returns a map of host name and Host service objects
    */
   public Map<String, Map<String, HostService>> getHosts() {
      return this.hosts;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, state, hosts);
   }

   @Override
   public boolean equals(Object obj) {
      if (this != obj)
         return false;
      if (obj == null || getClass() != obj.getClass()) return false;
      AvailabilityZoneDetails that = AvailabilityZoneDetails.class.cast(obj);
      return Objects.equal(this.name, that.name) && Objects.equal(this.state, that.state) && Objects.equal(this.hosts,
            that.hosts);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("state", state)
            .add("Hosts", hosts);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
