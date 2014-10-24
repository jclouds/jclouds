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

import static com.google.common.base.Preconditions.checkArgument;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Nova (or Neutron) network definition
 * Used to provide support for network, port, and fixed_ip when booting Nova servers.
 * OpenStack will support either a Nova Network or Neutron, but not both at the same time.
 * Specifying a port is only possible with Neutron.
 */
public class Network implements Comparable<Network> {
   private final String networkUuid;
   private final String portUuid;
   private final String fixedIp;

   @ConstructorProperties({
      "networkUuid", "portUuid", "fixedIp"
   })
   protected Network(String networkUuid, String portUuid, String fixedIp) {
      checkArgument(networkUuid != null || portUuid != null, "At least one of networkUuid or portUuid should be specified");
      this.networkUuid = networkUuid;
      this.portUuid = portUuid;
      this.fixedIp = fixedIp;
   }

   /**
    * @return the network uuid - Neutron or Nova
    */
   public String getNetworkUuid() {
      return this.networkUuid;
   }

   /**
    * @return the port uuid - Neutron only
    */
   public String getPortUuid() {
      return this.portUuid;
   }
   
   /**
    * @return the fixed IP address - Neutron or Nova
    */
   public String getFixedIp() {
      return this.fixedIp;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networkUuid, portUuid, fixedIp);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Network that = Network.class.cast(obj);
      return Objects.equal(this.networkUuid, that.networkUuid) && 
            Objects.equal(this.portUuid, that.portUuid) &&
            Objects.equal(this.fixedIp, that.fixedIp);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("networkUuid", networkUuid)
            .add("portUuid", portUuid)
            .add("fixedIp", fixedIp);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * @return A new builder object
    */
   public static Builder builder() { 
      return new Builder();
   }

   /**
    * @return A new Builder object from another Network
    */
   public Builder toBuilder() { 
      return new Builder().fromNetwork(this);
   }

   /**
    * Implements the Builder pattern for this class
    */
   public static class Builder {
      protected String networkUuid;
      protected String portUuid;
      protected String fixedIp;

      /** 
       * @param networkUuid The UUID for the Nova network or Neutron subnet to be attached. 
       * @return The builder object.
       * @see Network#getNetworkUuid()
       */
      public Builder networkUuid(String networkUuid) {
         this.networkUuid = networkUuid;
         return this;
      }

      /** 
       * @param portUuid The port UUID for this Neutron Network.
       * @return The builder object.
       * @see Network#getPortUuid()
       */
      public Builder portUuid(String portUuid) {
         this.portUuid = portUuid;
         return this;
      }
      
      /** 
       * @param fixedIp The fixed IP address for this Network (if any). 
       * Service automatically assigns IP address if this is not provided.
       * Fixed IP is compatible with both Nova Network and Neutron.
       * @return The builder object.
       * @see Network#getFixedIp()
       */
      public Builder fixedIp(String fixedIp) {
         this.fixedIp = fixedIp;
         return this;
      }

      /**
       * @return A new Network object.
       */
      public Network build() {
         return new Network(networkUuid, portUuid, fixedIp);
      }

      /**
       * @param in The target Network
       * @return A Builder from the provided Network
       */
      public Builder fromNetwork(Network in) {
         return this
               .networkUuid(in.getNetworkUuid())
               .portUuid(in.getPortUuid())
               .fixedIp(in.getFixedIp());
      }        
   }

   @Override
   public int compareTo(Network that) {
      return this.toString().compareTo(that.toString());
   }
}
