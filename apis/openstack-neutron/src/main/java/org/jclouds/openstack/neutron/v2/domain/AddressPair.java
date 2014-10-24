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
package org.jclouds.openstack.neutron.v2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * Contains a mapping between a MAC address and an IP address.
 */
public class AddressPair  {

   @Named("mac_address")
   private String macAddress;
   @Named("ip_address")
   private String ipAddress;

   @ConstructorProperties({"mac_address", "ip_address"})
   protected AddressPair(String macAddress, String ipAddress) {
      checkNotNull(macAddress, "mac address should not be null");
      checkNotNull(ipAddress, "ip should not be null");
      this.macAddress = macAddress;
      this.ipAddress = ipAddress;
   }

   /**
    * Copy constructor
    * @param addressPair
    */
   private AddressPair(AddressPair addressPair) {
      this(addressPair.getMacAddress(), addressPair.getIpAddress());
   }

   /**
    * Default constructor
    */
   private AddressPair() {}

   /**
    * @return the macAddress of the AddressPair
    */
   @Nullable
   public String getMacAddress() {
      return macAddress;
   }

   /**
    * @return the ipAddress of the AddressPair
    */
   @Nullable
   public String getIpAddress() {
      return ipAddress;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(macAddress, ipAddress);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      AddressPair that = AddressPair.class.cast(obj);
      return Objects.equal(this.macAddress, that.macAddress) && Objects.equal(this.ipAddress, that.ipAddress);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this).add("macAddress", macAddress).add("ipAddress", ipAddress);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * Returns a builder, but requires the user to specify any parameters required when creating a resource.
    * In this case, both parameters are required.
    * @return the Builder for AddressPair
    */
   public static Builder builder(String macAddress, String ipAddress) {
      return new Builder(macAddress, ipAddress);
   }

   /**
    * Gets a Builder configured as this object.
    */
   public Builder toBuilder() {
      return new Builder().fromAddressPair(this);
   }

   public static class Builder {
      // Keep track of the builder's state.
      private AddressPair addressPair;

      /**
       * No-parameters constructor used when updating.
       * */
      private Builder() {
         addressPair = new AddressPair();
      }

      /**
       * Required parameters constructor used when creating.
       * @param macAddress
       * @param ipAddress
       */
      private Builder(String macAddress, String ipAddress) {
         addressPair = new AddressPair();
         addressPair.macAddress = macAddress;
         addressPair.ipAddress = ipAddress;
      }

      /**
       * Provide the macAddress to the AddressPair's Builder.
       *
       * @return the Builder.
       * @see AddressPair#getMacAddress()
       */
      public Builder macAddress(String macAddress) {
         addressPair.macAddress = macAddress;
         return this;
      }

      /**
       * Provide the ipAddress to the AddressPair's Builder.
       *
       * @return the Builder.
       * @see AddressPair#getIpAddress()
       */
      public Builder ipAddress(String ipAddress) {
         addressPair.ipAddress = ipAddress;
         return this;
      }

      /**
       * @return a AddressPair constructed with this Builder.
       */
      public AddressPair build() {
         // Use the copy constructor to copy the builder's state (config) object and pass back to the user.
         // Immutability is preserved, and fields are defined only once.
         return new AddressPair(addressPair);
      }

      /**
       * @return a Builder from another AddressPair.
       */
      public Builder fromAddressPair(AddressPair in) {
         return this.macAddress(in.getMacAddress()).ipAddress(in.getIpAddress());
      }
   }
}
