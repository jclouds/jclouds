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
 * Information on external gateway for the router
 */
public class ExternalGatewayInfo {

   private final String networkId;

   @ConstructorProperties({"network_id"})
   protected ExternalGatewayInfo(String networkId) {
      this.networkId = networkId;
   }

   /**
    * @return the id of the network which is used as external gateway for the router
    */
   public String getNetworkId() {
      return networkId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networkId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ExternalGatewayInfo that = ExternalGatewayInfo.class.cast(obj);
      return Objects.equal(this.networkId, that.networkId);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this).add("networkId", networkId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromExternalGatewayInfo(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected String networkId;

      /**
       * @see ExternalGatewayInfo#getNetworkId()
       */
      public Builder networkId(String networkId) {
         this.networkId = networkId;
         return self();
      }

      public ExternalGatewayInfo build() {
         return new ExternalGatewayInfo(networkId);
      }

      public Builder fromExternalGatewayInfo(ExternalGatewayInfo in) {
         return this.networkId(in.getNetworkId());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
