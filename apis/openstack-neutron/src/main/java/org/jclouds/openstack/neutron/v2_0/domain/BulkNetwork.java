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

/**
 * A Neutron network used for creating networks in bulk
 * The only difference between this and the actual network are the missing fields id, tenantId, state & shared
 */
public class BulkNetwork {

   private final String name;
   private final Boolean adminStateUp;
   private final Boolean external;
   private final NetworkType networkType;
   private final String physicalNetworkName;
   private final Integer segmentationId;

   @ConstructorProperties({
      "name", "admin_state_up", "router:external", "provider:network_type", "provider:physical_network", "provider:segmentation_id"
   })
   protected BulkNetwork(String name, Boolean adminStateUp, Boolean external, String networkType, String physicalNetworkName, Integer segmentationId) {
      this.name = name;
      this.adminStateUp = adminStateUp;
      this.external = external;
      this.networkType = NetworkType.fromValue(networkType);
      this.physicalNetworkName = physicalNetworkName;
      this.segmentationId = segmentationId;
   }

   /**
    * @return the name of the network
    */
   public String getName() {
      return name;
   }

   /**
    * @return the administrative state of network. If false, the network does not forward packets.
    */
   public Boolean getAdminStateUp() {
      return adminStateUp;
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
      return Objects.hashCode(name, adminStateUp, external, networkType, physicalNetworkName, segmentationId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      BulkNetwork that = BulkNetwork.class.cast(obj);
      return Objects.equal(this.name, that.name)
         && Objects.equal(this.adminStateUp, that.adminStateUp)
         && Objects.equal(this.external, that.external)
         && Objects.equal(this.networkType, that.networkType)
         && Objects.equal(this.physicalNetworkName, that.physicalNetworkName)
         && Objects.equal(this.segmentationId, that.segmentationId);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("name", name).add("adminStateUp", adminStateUp).add("external", external)
         .add("networkType", networkType).add("physicalNetworkName", physicalNetworkName)
         .add("segmentationId", segmentationId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromBulkNetwork(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected String name;
      protected Boolean adminStateUp;
      protected Boolean external;
      protected NetworkType networkType;
      protected String physcialNetworkName;
      protected Integer segmentationId;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkNetwork#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkNetwork#getAdminStateUp()
       */
      public Builder adminStateUp(Boolean adminStateUp) {
         this.adminStateUp = adminStateUp;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkNetwork#getExternal()
       */
      public Builder external(Boolean external) {
         this.external = external;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkNetwork#getNetworkType()
       */
      public Builder networkType(NetworkType networkType) {
         this.networkType = networkType;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkNetwork#getPhysicalNetworkName()
       */
      public Builder physicalNetworkName(String physicalNetworkName) {
         this.physcialNetworkName = physicalNetworkName;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.BulkNetwork#getSegmentationId()
       */
      public Builder segmentationId(Integer segmentationId) {
         this.segmentationId = segmentationId;
         return self();
      }

      public BulkNetwork build() {
         return new BulkNetwork(name, adminStateUp, external, networkType == null ? null : networkType.getValue(), physcialNetworkName, segmentationId);
      }

      public Builder fromBulkNetwork(BulkNetwork in) {
         return this.name(in.getName())
               .adminStateUp(in.getAdminStateUp())
               .external(in.getExternal())
               .networkType(in.getNetworkType())
               .physicalNetworkName(in.getPhysicalNetworkName())
               .segmentationId(in.getSegmentationId());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
