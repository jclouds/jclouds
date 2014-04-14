/*
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
 * Describes an IP address
 */
public class IP {

   @Named("ip_address")
   protected final String ipAddress;
   @Named("subnet_id")
   protected final String subnetId;

   protected IP(String ipAddress, String subnetId) {
      this.ipAddress = ipAddress;
      this.subnetId = subnetId;
   }

   /**
    * @return the ipAddress of the IP
    */
   public String getIpAddress() {
      return ipAddress;
   }

   /**
    * @return the subnetId of the IP
    */
   public String getSubnetId() {
      return subnetId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipAddress, subnetId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      IP that = IP.class.cast(obj);
      return Objects.equal(this.ipAddress, that.ipAddress) && Objects.equal(this.subnetId, that.subnetId);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this).add("ipAddress", ipAddress).add("subnetId", subnetId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * @return the Builder for IP
    */
   public static Builder builder() {
      return new Builder();
   }

   /**
    * Gets a Builder configured as this object.
    */
   public Builder toBuilder() {
      return new Builder().fromIP(this);
   }

   public static class Builder {
      protected String ipAddress;
      protected String subnetId;

      /**
       * Provide the ipAddress to the IP's Builder.
       *
       * @return the Builder.
       * @see IP#getIpAddress()
       */
      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      /**
       * Provide the subnetId to the IP's Builder.
       *
       * @return the Builder.
       * @see IP#getSubnetId()
       */
      public Builder subnetId(String subnetId) {
         this.subnetId = subnetId;
         return this;
      }

      /**
       * @return a IP constructed with this Builder.
       */
      public IP build() {
         return new IP(ipAddress, subnetId);
      }

      /**
       * @return a Builder from another IP.
       */
      public Builder fromIP(IP in) {
         return this.ipAddress(in.getIpAddress()).subnetId(in.getSubnetId());
      }
   }
}
