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

package org.jclouds.openstack.neutron.v2_0.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Set;

/**
 * A Neutron port used for creating ports in bulk
 * The only difference between this and the actual port are the missing fields id, tenantId & state
 */
public class BulkPort {

   private final String name;
   private final String networkId;
   private final Boolean adminStateUp;
   private final String deviceId;
   private final String deviceOwner;
   private final Set<IP> fixedIps;
   private final String macAddress;

   @ConstructorProperties({
      "name", "network_id", "admin_state_up", "device_id", "device_owner", "fixed_ips", "mac_address"
   })
   protected BulkPort(String name, String networkId, Boolean adminStateUp, String deviceId, String deviceOwner, Set<IP> fixedIps, String macAddress) {
      this.name = name;
      this.networkId = networkId;
      this.adminStateUp = adminStateUp;
      this.deviceId = deviceId;
      this.deviceOwner = deviceOwner;
      this.fixedIps = fixedIps != null ? ImmutableSet.copyOf(fixedIps) : ImmutableSet.<IP>of();
      this.macAddress = macAddress;
   }

   /**
    * @return the name of the port
    */
   public String getName() {
      return name;
   }

   /**
    * @return the id of the network where this port is associated with
    */
   public String getNetworkId() {
      return networkId;
   }

   /**
    * @return the administrative state of port. If false, port does not forward packets
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the id of the device (e.g. server) using this port.
    */
   public String getDeviceId() {
      return deviceId;
   }

   /**
    * @return the entity (e.g.: dhcp agent) using this port.
    */
   public String getDeviceOwner() {
      return deviceOwner;
   }

   /**
    * @return the set of fixed ips this port has been assigned
    */
   public Set<IP> getFixedIps() {
      return fixedIps;
   }

   /**
    * @return the mac address of this port
    */
   public String getMacAddress() {
      return macAddress;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, networkId, adminStateUp, deviceId, deviceOwner, fixedIps, macAddress);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      BulkPort that = BulkPort.class.cast(obj);
      return Objects.equal(this.name, that.name)
         && Objects.equal(this.networkId, that.networkId)
         && Objects.equal(this.adminStateUp, that.adminStateUp)
         && Objects.equal(this.deviceId, that.deviceId)
         && Objects.equal(this.deviceOwner, that.deviceOwner)
         && Objects.equal(this.fixedIps, that.fixedIps)
         && Objects.equal(this.macAddress, that.macAddress);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("name", name).add("networkId", networkId).add("adminStateUp", adminStateUp)
         .add("deviceId", deviceId).add("deviceOwner", deviceOwner).add("fixedIps", fixedIps).add("macAddress", macAddress);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromBulkPort(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected String name;
      protected String networkId;
      protected String deviceId;
      protected String deviceOwner;
      protected String macAddress;
      protected Set<IP> fixedIps;
      protected Boolean adminStateUp;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkPort#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkPort#getNetworkId()
       */
      public Builder networkId(String networkId) {
         this.networkId = networkId;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkPort#getDeviceId()
       */
      public Builder deviceId(String deviceId) {
         this.deviceId = deviceId;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkPort#getDeviceOwner()
       */
      public Builder deviceOwner(String deviceOwner) {
         this.deviceOwner = deviceOwner;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkPort#getMacAddress()
       */
      public Builder macAddress(String macAddress) {
         this.macAddress = macAddress;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkPort#getFixedIps()
       */
      public Builder fixedIps(Collection<IP> fixedIps) {
         this.fixedIps = ImmutableSet.copyOf(fixedIps);
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkPort#getAdminStateUp()
       */
      public Builder adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      public BulkPort build() {
         return new BulkPort(name, networkId, adminStateUp, deviceId, deviceOwner, fixedIps, macAddress);
      }

      public Builder fromBulkPort(BulkPort in) {
         return this.name(in.getName())
               .networkId(in.getNetworkId())
               .adminStateUp(in.getAdminStateUp())
               .deviceId(in.getDeviceId())
               .deviceOwner(in.getDeviceOwner())
               .fixedIps(in.getFixedIps())
               .macAddress(in.getMacAddress());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
