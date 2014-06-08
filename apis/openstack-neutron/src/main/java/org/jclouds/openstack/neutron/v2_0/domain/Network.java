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
import java.util.Set;

/**
 * A Neutron network
 *
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/Networks.html">api doc</a>
 */
public class Network extends ReferenceWithName {

   private final State state;
   private final Set<String> subnets;
   private final Boolean adminStateUp;
   private final Boolean shared;
   private final Boolean external;
   private final NetworkType networkType;
   private final String physicalNetworkName;
   private final Integer segmentationId;

   @ConstructorProperties({
      "id", "tenant_id", "name", "status", "subnets", "admin_state_up", "shared", "router:external",
      "provider:network_type", "provider:physical_network", "provider:segmentation_id"
   })
   protected Network(String id, String tenantId, String name, State state,
                     Set<String> subnets, Boolean adminStateUp, Boolean shared, Boolean external,
                     String networkType, String physicalNetworkName, Integer segmentationId) {
      super(id, tenantId, name);
      this.state = state;
      this.subnets = subnets != null ? ImmutableSet.copyOf(subnets) : ImmutableSet.<String>of();
      this.adminStateUp = adminStateUp;
      this.shared = shared;
      this.external = external;
      this.networkType = networkType != null ? NetworkType.fromValue(networkType) : null;
      this.physicalNetworkName = physicalNetworkName;
      this.segmentationId = segmentationId;
   }

   /**
    * @return the current state of the network
    */
   public State getState() {
      return state;
   }

   /**
    * @return set of subnet ids that are associated with this network
    */
   public Set<String> getSubnets() {
      return subnets;
   }

   /**
    * @return the administrative state of network. If false, the network does not forward packets.
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return true if the network resource can be accessed by any tenant or not, false if not
    */
   public Boolean getShared() {
      return shared;
   }

   /**
    * @return true if network is external, false if not
    */
   public Boolean getExternal() {
      return external;
   }

   /**
    * @return the type of network
    */
   public NetworkType getNetworkType() {
      return networkType;
   }

   /**
    * @return the physical network name
    */
   public String getPhysicalNetworkName() {
      return physicalNetworkName;
   }

   /**
    * @return the segmentation id of the network
    */
   public Integer getSegmentationId() {
      return segmentationId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), state, subnets, adminStateUp, shared, external,
         networkType, physicalNetworkName, segmentationId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Network that = Network.class.cast(obj);
      return super.equals(obj)
         && Objects.equal(this.state, that.state)
         && Objects.equal(this.subnets, that.subnets)
         && Objects.equal(this.adminStateUp, that.adminStateUp)
         && Objects.equal(this.shared, that.shared)
         && Objects.equal(this.external, that.external)
         && Objects.equal(this.networkType, that.networkType)
         && Objects.equal(this.physicalNetworkName, that.physicalNetworkName)
         && Objects.equal(this.segmentationId, that.segmentationId);
   }

   protected Objects.ToStringHelper string() {
      return super.string()
         .add("state", state).add("subnets", subnets).add("adminStateUp", adminStateUp).add("shared", shared).add("external", external)
         .add("networkType", networkType).add("physicalNetworkName", physicalNetworkName).add("segmentationId", segmentationId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromNetwork(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends ReferenceWithName.Builder<T> {
      protected State state;
      protected Set<String> subnets;
      protected Boolean adminStateUp;
      protected Boolean shared;
      protected Boolean external;
      protected NetworkType networkType;
      protected String physicalNetworkName;
      protected Integer segmentationId;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Network#getState()
       */
      public T state(State state) {
         this.state = state;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Network#getSubnets()
       */
      public T subnets(Set<String> subnets) {
         this.subnets = subnets;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Network#getAdminStateUp()
       */
      public T adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Network#getShared()
       */
      public T shared(Boolean shared) {
         this.shared = shared;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Network#getExternal()
       */
      public T external(Boolean external) {
         this.external = external;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Network#getNetworkType()
       */
      public T networkType(NetworkType networkType) {
         this.networkType = networkType;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Network#getPhysicalNetworkName()
       */
      public T physicalNetworkName(String physicalNetworkName) {
         this.physicalNetworkName = physicalNetworkName;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.Network#getSegmentationId()
       */
      public T segmentationId(Integer segmentationId) {
         this.segmentationId = segmentationId;
         return self();
      }

      public Network build() {
         return new Network(id, tenantId, name, state, subnets, adminStateUp, shared, external, networkType == null ? null : networkType.getValue(), physicalNetworkName, segmentationId);
      }

      public T fromNetwork(Network in) {
         return super.fromReference(in)
               .state(in.getState())
               .subnets(in.getSubnets())
               .adminStateUp(in.getAdminStateUp())
               .shared(in.getShared())
               .external(in.getExternal())
               .networkType(in.getNetworkType())
               .physicalNetworkName(in.getPhysicalNetworkName())
               .segmentationId(in.getSegmentationId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
