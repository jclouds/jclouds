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
package org.jclouds.softlayer.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Date;

/**
 * Class SecurityGroup
 *
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/softlayer_network_subnet" />
 */

@AutoValue
public abstract class Subnet {

   public abstract String boradcastAddress();
   public abstract int cidr();
   public abstract String gateway();
   public abstract long id();
   public abstract boolean isCustomerOwned();
   public abstract boolean isCustomerRoutable();
   @Nullable
   public abstract Date modifyDate();
   public abstract String netmask();
   public abstract String networkIdentifier();
   public abstract long networkVlanId();
   @Nullable
   public abstract String note();
   public abstract String sortOrder();
   @Nullable
   public abstract String subnetType();
   public abstract String totalIpAddresses();
   public abstract String usableIpAddressCount();
   public abstract int version();
   @Nullable
   public abstract String addressSpace();

   @SerializedNames({"broadcastAddress", "cidr", "gateway", "id", "isCustomerOwned", "isCustomerRoutable", "modifyDate",
                     "netmask", "networkIdentifier", "networkVlanId", "note", "sortOrder", "subnetType", "totalIpAddresses",
                     "usableIpAddressCount", "version", "addressSpace"})
   public static Subnet create(String broadcastAddress, int cidr, String gateway, final long id, boolean isCustomerOwned, boolean isCustomerRoutable,
                               Date modifyDate, String netmask, String networkIdentifier, long networkVlanId, String note, String sortOrder, String subnetType,
                               String totalIpAddresses, String usableIpAddressCount, int version, String addressSpace) {
      return new AutoValue_Subnet(broadcastAddress, cidr, gateway, id, isCustomerOwned, isCustomerRoutable, modifyDate, netmask,
              networkIdentifier, networkVlanId, note, sortOrder, subnetType, totalIpAddresses, usableIpAddressCount, version, addressSpace);
   }

   Subnet() {}

   @AutoValue
   public abstract static class CreateSubnet {

      public abstract String networkIdentifier();
      public abstract String note();
      public abstract int cidr();

      @SerializedNames({"networkIdentifier", "note", "cidr"})
      private static CreateSubnet create(final String networkIdentifier, final String note, final int cidr) {
         return builder()
                 .networkIdentifier(networkIdentifier)
                 .cidr(cidr)
                 .note(note)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_Subnet_CreateSubnet.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder networkIdentifier(String networkIdentifier);
         public abstract Builder note(String note);
         public abstract Builder cidr(int cidr);

         abstract CreateSubnet autoBuild();

         public CreateSubnet build() {
            return autoBuild();
         }
      }
   }

   @AutoValue
   public abstract static class DeleteSubnet {

      public abstract long id();

      @SerializedNames({"id"})
      private static DeleteSubnet create(final long id) {
         return builder()
                 .id(id)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_Subnet_DeleteSubnet.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder id(long id);

         abstract DeleteSubnet autoBuild();

         public DeleteSubnet build() {
            return autoBuild();
         }
      }
   }

   @AutoValue
   public abstract static class CreateDatacenterName {
      public abstract String name();

      @SerializedNames({"name"})
      public static CreateDatacenterName create(String name) {
         return builder()
                 .name(name)
                 .build();
      }

      CreateDatacenterName() {}

      public static Builder builder() {
         return new AutoValue_Subnet_CreateDatacenterName.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder name(String name);

         abstract CreateDatacenterName autoBuild();

         public CreateDatacenterName build() {
            return autoBuild();
         }
      }
   }
}

