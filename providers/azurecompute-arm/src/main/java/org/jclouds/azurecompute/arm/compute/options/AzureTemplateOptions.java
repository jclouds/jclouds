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
   private String blob;
   private AvailabilitySet availabilitySet;
   private String availabilitySetName;
   private List<DataDisk> dataDisks = ImmutableList.of();

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
   public String getBlob() { return blob; }
   public AvailabilitySet getAvailabilitySet() { return availabilitySet; }
   public String getAvailabilitySetName() { return availabilitySetName; }
   public List<DataDisk> getDataDisks() {
      return dataDisks;
   }

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
         eTo.availabilitySet(availabilitySet);
         eTo.availabilitySet(availabilitySetName);
         eTo.dataDisks(dataDisks);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof AzureTemplateOptions)) return false;
      if (!super.equals(o)) return false;

      AzureTemplateOptions that = (AzureTemplateOptions) o;

      if (virtualNetworkName != null ? !virtualNetworkName.equals(that.virtualNetworkName) : that.virtualNetworkName != null)
         return false;
      if (subnetId != null ? !subnetId.equals(that.subnetId) : that.subnetId != null) return false;
      if (blob != null ? !blob.equals(that.blob) : that.blob != null) return false;
      if (availabilitySet != null ? !availabilitySet.equals(that.availabilitySet) : that.availabilitySet != null)
         return false;
      if (availabilitySetName != null ? !availabilitySetName.equals(that.availabilitySetName) : that.availabilitySetName != null)
         return false;
      return dataDisks != null ? dataDisks.equals(that.dataDisks) : that.dataDisks == null;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (virtualNetworkName != null ? virtualNetworkName.hashCode() : 0);
      result = 31 * result + (subnetId != null ? subnetId.hashCode() : 0);
      result = 31 * result + (blob != null ? blob.hashCode() : 0);
      result = 31 * result + (availabilitySet != null ? availabilitySet.hashCode() : 0);
      result = 31 * result + (availabilitySetName != null ? availabilitySetName.hashCode() : 0);
      result = 31 * result + (dataDisks != null ? dataDisks.hashCode() : 0);
      return result;
   }

   @Override
   public Objects.ToStringHelper string() {
      Objects.ToStringHelper toString = super.string();
      if (virtualNetworkName != null)
         toString.add("virtualNetworkName", virtualNetworkName);
      if (subnetId != null)
         toString.add("subnetId", subnetId);
      if (blob != null)
         toString.add("blob", blob);
      if (availabilitySet != null)
         toString.add("availabilitySet", availabilitySet);
      if (availabilitySetName != null)
         toString.add("availabilitySetName", availabilitySetName);
      if (!dataDisks.isEmpty())
         toString.add("dataDisks", dataDisks);
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
       * @see AzureTemplateOptions#blob(String)
       */
      public static AzureTemplateOptions blob(String blob) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.blob(blob);
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
       * @see AzureTemplateOptions#dataDisks
       */
      public static AzureTemplateOptions dataDisks(DataDisk... dataDisks) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.dataDisks(dataDisks);
      }

      public static AzureTemplateOptions dataDisks(Iterable<DataDisk> dataDisks) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.dataDisks(dataDisks);
      }
   }
}
