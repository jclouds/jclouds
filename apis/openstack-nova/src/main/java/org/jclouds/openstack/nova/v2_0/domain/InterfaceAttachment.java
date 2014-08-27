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

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

public class InterfaceAttachment {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromInterfaceAttachment(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      private String networkId;
      private String portId;
      private PortState portState;
      private String macAddress;
      private ImmutableSet<FixedIP> fixedIps;

      /**
       * @see InterfaceAttachment#getNetworkId()
       */
      public T networkId(String networkId) {
         this.networkId = networkId;
         return self();
      }

      /**
       * @see InterfaceAttachment#getPortId()
       */
      public T portId(String portId) {
         this.portId = portId;
         return self();
      }

      /**
       * @see InterfaceAttachment#getPortState()
       */
      public T portState(PortState portState) {
         this.portState = portState;
         return self();
      }

      /**
       * @see InterfaceAttachment#getMacAddress()
       */
      public T macAddress(String macAddress) {
         this.macAddress = macAddress;
         return self();
      }

      /**
       * @see InterfaceAttachment#getFixedIps()
       */
      public T fixedIps(ImmutableSet<FixedIP> fixedIps) {
         this.fixedIps = fixedIps;
         return self();
      }

      public InterfaceAttachment build() {
         return new InterfaceAttachment(networkId, portId, portState, macAddress, fixedIps);
      }

      public T fromInterfaceAttachment(InterfaceAttachment in) {
         return this.networkId(in.getNetworkId()).portId(in.getPortId()).portState(in.getPortState())
               .macAddress(in.getMacAddress()).fixedIps(in.getFixedIps());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("net_id")
   private String networkId;
   @Named("port_id")
   private String portId;
   @Named("port_state")
   private PortState portState;
   @Named("mac_addr")
   private String macAddress;
   @Named("fixed_ips")
   private ImmutableSet<FixedIP> fixedIps;

   @ConstructorProperties({ "net_id", "port_id", "port_state", "mac_addr", "fixed_ips" })
   protected InterfaceAttachment(String networkId, String portId, PortState portState,
         String macAddress, ImmutableSet<FixedIP> fixedIps) {
      this.networkId = networkId;
      this.portId = checkNotNull(portId, "portId");
      this.portState = portState;
      this.macAddress = macAddress;
      this.fixedIps = fixedIps;
   }

   public String getNetworkId() {
      return this.networkId;
   }

   public String getPortId() {
      return this.portId;
   }

   public PortState getPortState() {
      return this.portState;
   }

   public String getMacAddress() {
      return this.macAddress;
   }

   public ImmutableSet<FixedIP> getFixedIps() {
      return this.fixedIps;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networkId, portId, portState, macAddress, fixedIps);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      InterfaceAttachment that = InterfaceAttachment.class.cast(obj);
      return Objects.equal(this.networkId, that.networkId) && Objects.equal(this.portId, that.portId)
            && Objects.equal(this.portState, that.portState) && Objects.equal(this.macAddress, that.macAddress)
            && Objects.equal(this.fixedIps, that.fixedIps);
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("networkId", networkId).add("portId", portId).add("portState", portState)
            .add("macAddress", macAddress).add("fixedIps", fixedIps);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
