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
 * A Neutron Router Interface
 *
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/router_add_interface.html">api doc</a>
 */
public class RouterInterface {

   private final String subnetId;
   private final String portId;

   @ConstructorProperties({
      "subnet_id", "port_id"
   })
   protected RouterInterface(String subnetId, String portId) {
      this.subnetId = subnetId;
      this.portId = portId;
   }

   /**
    * @return the id of the subnet this router interface is associated with
    */
   public String getSubnetId() {
      return subnetId;
   }

   /**
    * @return the id of the port this router interface is associated with
    */
   public String getPortId() {
      return portId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(subnetId, portId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      RouterInterface that = RouterInterface.class.cast(obj);
      return Objects.equal(this.subnetId, that.subnetId) && Objects.equal(this.portId, that.portId);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("subnetId", subnetId).add("portId", portId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromRouterInterface(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected String subnetId;
      protected String portId;

      /**
       * @see RouterInterface#getSubnetId()
       */
      public Builder subnetId(String subnetId) {
         this.subnetId = subnetId;
         return self();
      }

      /**
       * @see RouterInterface#getPortId()
       */
      public Builder portId(String portId) {
         this.portId = portId;
         return self();
      }

      public RouterInterface build() {
         return new RouterInterface(subnetId, portId);
      }

      public Builder fromRouterInterface(RouterInterface in) {
         return this.subnetId(in.getSubnetId()).portId(in.getPortId());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
