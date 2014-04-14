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
 * Information on the external gateway for the router
 */
public class ExternalGatewayInfo {

   @Named("network_id")
   protected final String networkId;
   @Named("enable_snat")
   protected final Boolean enableSnat;

   protected ExternalGatewayInfo(String networkId, Boolean enableSnat) {
      this.networkId = networkId;
      this.enableSnat = enableSnat;
   }

   /**
    * @return the networkId of the ExternalGatewayInfo
    */
   public String getNetworkId() {
      return networkId;
   }

   /**
    * @return the enableSnat status of the ExternalGatewayInfo
    */
   public Boolean getEnableSnat() {
      return enableSnat;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networkId, enableSnat);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      ExternalGatewayInfo that = (ExternalGatewayInfo) o;

      return Objects.equal(this.networkId, that.networkId) &&
            Objects.equal(this.enableSnat, that.enableSnat);
   }

   /**
    * @return the Builder for ExternalGatewayInfo
    */
   public static Builder builder() {
      return new Builder();
   }

   /**
    * Gets a Builder configured as this object.
    */
   public Builder toBuilder() {
      return new Builder().fromExternalGatewayInfo(this);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
            .add("networkId", networkId)
            .add("enableSnat", enableSnat)
            .toString();
   }

   public static class Builder {
      protected String networkId;
      protected Boolean enableSnat;

      /**
       * Provide the networkId to the ExternalGatewayInfo's Builder.
       *
       * @return the Builder.
       * @see ExternalGatewayInfo#getNetworkId()
       */
      public Builder networkId(String networkId) {
         this.networkId = networkId;
         return this;
      }

      public Builder enableSnat(boolean enableSnat) {
         this.enableSnat = enableSnat;
         return this;
      }

      /**
       * @return a ExternalGatewayInfo constructed with this Builder.
       */
      public ExternalGatewayInfo build() {
         return new ExternalGatewayInfo(networkId, enableSnat);
      }

      /**
       * @return a Builder from another ExternalGatewayInfo.
       */
      public Builder fromExternalGatewayInfo(ExternalGatewayInfo in) {
         return this.networkId(in.getNetworkId()).enableSnat(in.getEnableSnat());
      }
   }
}
