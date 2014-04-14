/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.neutron.v2.domain;

import com.google.common.base.Objects;

import javax.inject.Named;

/**
 * A Neutron Router Interface
 *
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-network/2.0/content/router_add_interface.html">api
 *      doc</a>
 */
public class RouterInterface {

   @Named("subnet_id")
   protected final String subnetId;
   @Named("port_id")
   protected final String portId;

   protected RouterInterface(String subnetId, String portId) {
      this.subnetId = subnetId;
      this.portId = portId;
   }

   /**
    * @return the subnetId of the RouterInterface
    */
   public String getSubnetId() {
      return subnetId;
   }

   /**
    * @return the portId of the RouterInterface
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
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      RouterInterface that = RouterInterface.class.cast(obj);
      return Objects.equal(this.subnetId, that.subnetId) && Objects.equal(this.portId, that.portId);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this).add("subnetId", subnetId).add("portId", portId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * @return the Builder for RouterInterface
    */
   public static Builder builder() {
      return new Builder();
   }

   /**
    * Gets a Builder configured as this object.
    */
   public Builder toBuilder() {
      return new Builder().fromRouterInterface(this);
   }

   public static class Builder {
      protected String subnetId;
      protected String portId;

      /**
       * Provide the subnetId to the RouterInterface's Builder.
       *
       * @return the Builder.
       * @see RouterInterface#getSubnetId()
       */
      public Builder subnetId(String subnetId) {
         this.subnetId = subnetId;
         return this;
      }

      /**
       * Provide the portId to the RouterInterface's Builder.
       *
       * @return the Builder.
       * @see RouterInterface#getPortId()
       */
      public Builder portId(String portId) {
         this.portId = portId;
         return this;
      }

      /**
       * @return a RouterInterface constructed with this Builder.
       */
      public RouterInterface build() {
         return new RouterInterface(subnetId, portId);
      }

      /**
       * @return a Builder from another RouterInterface.
       */
      public Builder fromRouterInterface(RouterInterface in) {
         return this.subnetId(in.getSubnetId()).portId(in.getPortId());
      }
   }
}
