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

public class IP {

   private String ipAddress;
   private String subnetId;

   @ConstructorProperties({
      "ip_address", "subnet_id"
   })
   protected IP(String ipAddress, String subnetId) {
      this.ipAddress = ipAddress;
      this.subnetId = subnetId;
   }

   /**
    * @return the fixed ip address
    */
   public String getIpAddress() {
      return ipAddress;
   }

   /**
    * @return the id of the subnet of this ip
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
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      IP that = IP.class.cast(obj);
      return Objects.equal(this.ipAddress, that.ipAddress) && Objects.equal(this.subnetId, that.subnetId);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("ipAddress", ipAddress).add("subnetId", subnetId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new ConcreteBuilder();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder().fromIP(this);
   }

   public abstract static class Builder {
      protected abstract Builder self();

      protected String ipAddress;
      protected String subnetId;

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.IP#getIpAddress
       */
      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return self();
      }

      /**
       * @see org.jclouds.openstack.neutron.v2_0.domain.IP#getSubnetId
       */
      public Builder subnetId(String subnetId) {
         this.subnetId = subnetId;
         return self();
      }

      public IP build() {
         return new IP(ipAddress, subnetId);
      }

      public Builder fromIP(IP in) {
         return this.ipAddress(in.getIpAddress()).subnetId(in.getSubnetId());
      }
   }

   private static class ConcreteBuilder extends Builder {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

}
