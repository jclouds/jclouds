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

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Neutron port
 *
 * @see <a href="http://docs.openstack.org/api/openstack-network/1.0/content/Ports.html">api doc</a>
 */
public class Port extends ReferenceWithName {

   private final State state;
   private final Boolean adminStateUp;
   private final String networkId;
   private final String deviceId;
   private final String deviceOwner;
   private final String macAddress;
   private final Set<IP> fixedIps;

   @ConstructorProperties({
      "id", "tenant_id", "name", "status", "network_id", "admin_state_up", "device_id", "device_owner", "fixed_ips", "mac_address"
   })
   protected Port(String id, String tenantId, String name, State state, String networkId, Boolean adminStateUp,
                  String deviceId, String deviceOwner, Set<IP> fixedIps, String macAddress) {
      super(id, tenantId, name);
      this.adminStateUp = adminStateUp;
      this.state = state;
      this.networkId = checkNotNull(networkId, "networkId");
      this.deviceId = deviceId;
      this.deviceOwner = deviceOwner;
      this.fixedIps = fixedIps;
      this.macAddress = macAddress;
   }

   /**
    * @return the current state of the port
    */
   public State getState() {
      return this.state;
   }

   /**
    * @return the administrative state of port. If false, port does not forward packets.
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the id of the network where this port is associated with
    */
   public String getNetworkId() {
      return networkId;
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
      return Objects.hashCode(super.hashCode(), state, adminStateUp, networkId, deviceId, deviceOwner, fixedIps, macAddress);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Port that = Port.class.cast(obj);
      return super.equals(obj)
         && Objects.equal(this.state, that.state)
         && Objects.equal(this.adminStateUp, that.adminStateUp)
         && Objects.equal(this.networkId, that.networkId)
         && Objects.equal(this.deviceId, that.deviceId)
         && Objects.equal(this.deviceOwner, that.deviceOwner)
         && Objects.equal(this.fixedIps, that.fixedIps)
         && Objects.equal(this.macAddress, that.macAddress);
   }

   protected Objects.ToStringHelper string() {
      return super.string()
         .add("state", state).add("adminStateUp", adminStateUp).add("networkId", networkId).add("deviceId", deviceId)
         .add("deviceOwner", deviceOwner).add("fixedIps", fixedIps).add("macAddress", macAddress);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromPort(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends ReferenceWithName.Builder<T> {
      protected String networkId;
      protected String deviceId;
      protected String deviceOwner;
      protected String macAddress;
      protected Set<IP> fixedIps;
      protected State state;
      protected Boolean adminStateUp;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Port#getState()
       */
      public T state(State state) {
         this.state = state;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Port#getNetworkId()
       */
      public T networkId(String networkId) {
         this.networkId = networkId;
         return self();
      }


      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Port#getAdminStateUp()
       */
      public T adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Port#getDeviceId()
       */
      public T deviceId(String deviceId) {
         this.deviceId = deviceId;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Port#getDeviceOwner()
       */
      public T deviceOwner(String deviceOwner) {
         this.deviceOwner = deviceOwner;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Port#getDeviceId()
       */
      public T fixedIps(Set<IP> fixedIps) {
         this.fixedIps = fixedIps;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Port#getMacAddress()
       */
      public T macAddress(String macAddress) {
         this.macAddress = macAddress;
         return self();
      }

      public Port build() {
         return new Port(id, tenantId, name, state, networkId, adminStateUp, deviceId, deviceOwner, fixedIps, macAddress);
      }

      public T fromPort(Port in) {
         return super.fromReference(in)
               .state(in.getState())
               .networkId(in.getNetworkId())
               .adminStateUp(in.getAdminStateUp())
               .deviceId(in.getDeviceId())
               .deviceOwner(in.getDeviceOwner())
               .fixedIps(in.getFixedIps())
               .macAddress(in.getMacAddress());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
