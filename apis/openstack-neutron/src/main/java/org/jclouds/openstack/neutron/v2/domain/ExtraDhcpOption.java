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
 * This is used to provide additional DHCP-related options to Subnet. This is
 * based on a neutron extension.
 * For example PXE boot options to DHCP clients can be specified (e.g. tftp-server,
 * server-ip-address, bootfile-name)
 *
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-network/2.0/content/Subnets.html">api
 *      doc</a>
 */
public class ExtraDhcpOption {

   protected final String id;
   @Named("opt_name")
   protected final String optionName;
   @Named("opt_value")
   protected final String optionValue;

   protected ExtraDhcpOption(String id, String optionName, String optionValue) {
      this.id = id;
      this.optionName = optionName;
      this.optionValue = optionValue;
   }

   /**
    * @return the id of the ExtraDhcpOption
    */
   public String getId() {
      return id;
   }

   /**
    * @return the optionName of the ExtraDhcpOption
    */
   public String getOptionName() {
      return optionName;
   }

   /**
    * @return the optionValue of the ExtraDhcpOption
    */
   public String getOptionValue() {
      return optionValue;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, optionName, optionValue);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ExtraDhcpOption that = ExtraDhcpOption.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.optionName, that.optionName)
            && Objects.equal(this.optionValue, that.optionValue);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this).add("id", id).add("optionName", optionName).add("optionValue", optionValue);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * @return the Builder for ExtraDhcpOption
    */
   public static Builder builder() {
      return new Builder();
   }

   /**
    * Gets a Builder configured as this object.
    */
   public Builder toBuilder() {
      return new Builder().fromExtraDhcpOption(this);
   }

   public static class Builder {
      protected String id;
      protected String optionName;
      protected String optionValue;

      /**
       * Provide the id to the ExtraDhcpOption's Builder.
       *
       * @return the Builder.
       * @see ExtraDhcpOption#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * Provide the optionName to the ExtraDhcpOption's Builder.
       *
       * @return the Builder.
       * @see ExtraDhcpOption#getOptionName()
       */
      public Builder optionName(String optionName) {
         this.optionName = optionName;
         return this;
      }

      /**
       * Provide the optionValue to the ExtraDhcpOption's Builder.
       *
       * @return the Builder.
       * @see ExtraDhcpOption#getOptionValue()
       */
      public Builder optionValue(String optionValue) {
         this.optionValue = optionValue;
         return this;
      }

      /**
       * @return a ExtraDhcpOption constructed with this Builder.
       */
      public ExtraDhcpOption build() {
         return new ExtraDhcpOption(id, optionName, optionValue);
      }

      /**
       * @return a Builder from another ExtraDhcpOption.
       */
      public Builder fromExtraDhcpOption(ExtraDhcpOption in) {
         return this.id(in.getId()).optionName(in.getOptionName()).optionValue(in.getOptionValue());
      }
   }
}
