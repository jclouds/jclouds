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

import java.util.List;

import org.jclouds.azurecompute.arm.domain.AvailabilitySet;
import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.compute.options.TemplateOptions;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Azure ARM custom options
 */
public class AzureTemplateOptions extends TemplateOptions implements Cloneable {

   private String virtualNetworkName;
   private String subnetId;
   private AvailabilitySet availabilitySet;
   private String availabilitySetName;
   private List<DataDisk> dataDisks = ImmutableList.of();
   private String resourceGroup;

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
    * Sets the availability set where the nodes will be configured. If it does
    * not exist jclouds will create a new one with the given configuration.
    */
   public AzureTemplateOptions availabilitySet(AvailabilitySet availabilitySet) {
      this.availabilitySet = availabilitySet;
      return this;
   }

   /**
    * Sets the availability set where the nodes will be configured. The
    * availability set must exist.
    */
   public AzureTemplateOptions availabilitySet(String availabilitySetName) {
      this.availabilitySetName = availabilitySetName;
      return this;
   }
   
   /**
    * The resource group where the new resources will be created.
    */
   public AzureTemplateOptions resourceGroup(String resourceGroup) {
      this.resourceGroup = resourceGroup;
      return this;
   }

   public AzureTemplateOptions dataDisks(Iterable<DataDisk> dataDisks) {
      for (DataDisk dataDisk : checkNotNull(dataDisks, "dataDisks"))
         checkNotNull(dataDisk, "all dataDisks must be non-empty");
      this.dataDisks = ImmutableList.copyOf(dataDisks);
      return this;
   }

   public AzureTemplateOptions dataDisks(DataDisk... dataDisks) {
      return dataDisks(ImmutableList.copyOf(checkNotNull(dataDisks, "dataDisks")));
   }
   
   public String getVirtualNetworkName() { return virtualNetworkName; }
   public String getSubnetId() { return subnetId; }
   public AvailabilitySet getAvailabilitySet() { return availabilitySet; }
   public String getAvailabilitySetName() { return availabilitySetName; }
   public List<DataDisk> getDataDisks() { return dataDisks; }
   public String getResourceGroup() { return resourceGroup; }

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
         eTo.availabilitySet(availabilitySet);
         eTo.availabilitySet(availabilitySetName);
         eTo.dataDisks(dataDisks);
         eTo.resourceGroup(resourceGroup);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof AzureTemplateOptions)) return false;
      if (!super.equals(o)) return false;

      AzureTemplateOptions that = (AzureTemplateOptions) o;
      
      return Objects.equal(virtualNetworkName, that.virtualNetworkName) &&
            Objects.equal(subnetId, that.subnetId) &&
            Objects.equal(availabilitySet, that.availabilitySet) &&
            Objects.equal(availabilitySetName, that.availabilitySetName) &&
            Objects.equal(dataDisks, that.dataDisks) &&
            Objects.equal(resourceGroup, that.resourceGroup);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(virtualNetworkName, subnetId, availabilitySet, availabilitySetName, dataDisks,
            resourceGroup);
   }

   @Override
   public Objects.ToStringHelper string() {
      Objects.ToStringHelper toString = super.string();
      if (virtualNetworkName != null)
         toString.add("virtualNetworkName", virtualNetworkName);
      if (subnetId != null)
         toString.add("subnetId", subnetId);
      if (availabilitySet != null)
         toString.add("availabilitySet", availabilitySet);
      if (availabilitySetName != null)
         toString.add("availabilitySetName", availabilitySetName);
      if (!dataDisks.isEmpty())
         toString.add("dataDisks", dataDisks);
      if (resourceGroup != null)
         toString.add("resourceGroup", resourceGroup);
      return toString;
   }

   public static class Builder {

      /**
       * @see AzureTemplateOptions#virtualNetworkName(String)
       */
      public static AzureTemplateOptions virtualNetworkName(String virtualNetworkName) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.virtualNetworkName(virtualNetworkName);
      }

      /**
       * @see AzureTemplateOptions#subnetId(String)
       */
      public static AzureTemplateOptions subnetId(String subnetId) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.subnetId(subnetId);
      }
      
      /**
       * @see AzureTemplateOptions#availabilitySet(AvailabilitySet)
       */
      public static AzureTemplateOptions availabilitySet(AvailabilitySet availabilitySet) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.availabilitySet(availabilitySet);
      }
      
      /**
       * @see AzureTemplateOptions#availabilitySet(String)
       */
      public static AzureTemplateOptions availabilitySet(String availabilitySetName) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.availabilitySet(availabilitySetName);
      }

      /**
       * @see AzureTemplateOptions#dataDisks(DataDisk...)
       */
      public static AzureTemplateOptions dataDisks(DataDisk... dataDisks) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.dataDisks(dataDisks);
      }

      /**
       * @see AzureTemplateOptions#dataDisks(Iterable)
       */
      public static AzureTemplateOptions dataDisks(Iterable<DataDisk> dataDisks) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.dataDisks(dataDisks);
      }
      
      /**
       * @see AzureTemplateOptions#resourceGroup(String)
       */
      public static AzureTemplateOptions resourceGroup(String resourceGroup) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.resourceGroup(resourceGroup);
      }
   }
}
