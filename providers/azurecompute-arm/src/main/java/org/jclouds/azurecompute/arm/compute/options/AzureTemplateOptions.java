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
package org.jclouds.azurecompute.arm.compute.options;

import org.jclouds.compute.options.TemplateOptions;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.equal;

/**
 * Azure ARM custom options
 */
public class AzureTemplateOptions extends TemplateOptions implements Cloneable {

   private String virtualNetworkName;
   private String subnetId;
   private String blob;

   /**
    * Sets the virtual network name
    */
   public  AzureTemplateOptions virtualNetworkName(String virtualNetworkName) {
      this.virtualNetworkName = virtualNetworkName;
      return this;
   }

   /**
    * Sets the subnet name
    */
   public  AzureTemplateOptions subnetId(String subnetId) {
      this.subnetId = subnetId;
      return this;
   }

   /**
    * Sets the blob name
    */
   public  AzureTemplateOptions blob(String blob) {
      this.blob = blob;
      return this;
   }

   public String getVirtualNetworkName() { return virtualNetworkName; }
   public String getSubnetId() { return subnetId; }
   public String getBlob() { return blob; }


   @Override
   public AzureTemplateOptions clone() {
      AzureTemplateOptions options = new AzureTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof AzureTemplateOptions) {
         AzureTemplateOptions eTo = AzureTemplateOptions.class.cast(to);
         eTo.virtualNetworkName(virtualNetworkName);
         eTo.subnetId(subnetId);
         eTo.blob(blob);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), virtualNetworkName, subnetId, blob);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      AzureTemplateOptions other = (AzureTemplateOptions) obj;
      return super.equals(other)
            && equal(this.virtualNetworkName, other.virtualNetworkName)
            && equal(this.subnetId, other.subnetId)
            && equal(this.blob, other.blob);
   }

   @Override
   public Objects.ToStringHelper string() {
      Objects.ToStringHelper toString = super.string().omitNullValues();
      toString.add("virtualNetworkName", virtualNetworkName);
      toString.add("subnetId", subnetId);
      toString.add("blob", blob);
      return toString;
   }

   public static class Builder {

      /**
       * @see AzureTemplateOptions#virtualNetworkName
       */
      public static AzureTemplateOptions virtualNetworkName(String virtualNetworkName) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.virtualNetworkName(virtualNetworkName);
      }

      /**
       * @see AzureTemplateOptions#subnetId
       */
      public static AzureTemplateOptions subnetId(String subnetId) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.subnetId(subnetId);
      }

      /**
       * @see AzureTemplateOptions#blob
       */
      public static AzureTemplateOptions blob(String blob) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.blob(blob);
      }
   }
}
