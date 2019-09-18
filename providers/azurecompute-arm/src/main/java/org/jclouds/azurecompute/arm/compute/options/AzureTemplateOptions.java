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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jclouds.azurecompute.arm.domain.AvailabilitySet;
import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile.WindowsConfiguration;
import org.jclouds.azurecompute.arm.domain.Secrets;
import org.jclouds.azurecompute.arm.domain.StorageAccountType;
import org.jclouds.compute.options.TemplateOptions;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Azure ARM custom options
 */
public class AzureTemplateOptions extends TemplateOptions implements Cloneable {

   private AvailabilitySet availabilitySet;
   private String availabilitySetName;
   private List<DataDisk> dataDisks = ImmutableList.of();
   private String resourceGroup;
   private List<IpOptions> ipOptions = ImmutableList.of();
   private WindowsConfiguration windowsConfiguration;
   private List<Secrets> secrets = ImmutableList.of();
   private String customData;
   private StorageAccountType osDiskStorageType = StorageAccountType.STANDARD_LRS;

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

   /**
    * Configure the NICs that will be attached to the created nodes.
    * <p>
    * Note that the number of NICs that can be attached depends on the size of
    * the virtual machine, and that the guest operating system needs to be
    * prepared to set up all the configured interfaces.
    * <p>
    * Depending on the image being used, a cloud-init or bootstrap script might
    * be needed to make the interface setup.
    */
   public AzureTemplateOptions ipOptions(Iterable<IpOptions> ipOptions) {
      for (IpOptions ipOption : checkNotNull(ipOptions, "ipOptions"))
         checkNotNull(ipOption, "all ipOptions must be non-empty");
      this.ipOptions = ImmutableList.copyOf(ipOptions);
      return this;
   }

   /**
    * @see {@link AzureTemplateOptions#ipOptions(Iterable)

    */
   public AzureTemplateOptions ipOptions(IpOptions... ipOptions) {
      return ipOptions(ImmutableList.copyOf(checkNotNull(ipOptions, "ipOptions")));
   }

   /**
    * Windows configuration parameters
    *
    * @see <a
    *      href="https://docs.microsoft.com/en-us/rest/api/compute/virtualmachines/virtualmachines-create-or-update#bk_windowsconfig5">docs</a>
    */
   public AzureTemplateOptions windowsConfiguration(WindowsConfiguration windowsConfiguration) {
      this.windowsConfiguration = windowsConfiguration;
      return this;
   }

   /**
    * Import certificates in the Windows Certificate Store
    *
    * @see <a
    *      href="https://docs.microsoft.com/en-us/rest/api/compute/virtualmachines/virtualmachines-create-or-update#bk_srcvault">docs</a>
    */
   public AzureTemplateOptions secrets(Iterable<? extends Secrets> secrets) {
      for (Secrets secret : checkNotNull(secrets, "secrets"))
         checkNotNull(secret, "secrets can not be empty");
      this.secrets = ImmutableList.copyOf(secrets);
      return this;
   }

   /**
    * Custom data (for cloud-init) for the Azure ARM API
    */
   public AzureTemplateOptions customData(String customData) {
      this.customData = customData;
      return this;
   }

   public AzureTemplateOptions osDiskStorageType(StorageAccountType osDiskStorageType) {
      this.osDiskStorageType = osDiskStorageType;
      return this;
   }

   public AvailabilitySet getAvailabilitySet() {
      return availabilitySet;
   }

   public String getAvailabilitySetName() {
      return availabilitySetName;
   }

   public List<DataDisk> getDataDisks() {
      return dataDisks;
   }

   public String getResourceGroup() {
      return resourceGroup;
   }

   public List<IpOptions> getIpOptions() {
      return ipOptions;
   }

   public WindowsConfiguration getWindowsConfiguration() {
      return windowsConfiguration;
   }

   public List<Secrets> getSecrets() {
      return secrets;
   }

   public String getCustomData() {
      return customData;
   }

   public StorageAccountType getOsDiskStorageType() {
      return osDiskStorageType;
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
         eTo.availabilitySet(availabilitySet);
         eTo.availabilitySet(availabilitySetName);
         eTo.dataDisks(dataDisks);
         eTo.resourceGroup(resourceGroup);
         eTo.ipOptions(ipOptions);
         eTo.windowsConfiguration(windowsConfiguration);
         eTo.secrets(secrets);
         eTo.customData(customData);
         eTo.osDiskStorageType(osDiskStorageType);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof AzureTemplateOptions))
         return false;
      if (!super.equals(o))
         return false;

      AzureTemplateOptions that = (AzureTemplateOptions) o;

      return Objects.equal(availabilitySetName, that.availabilitySetName)
            && Objects.equal(resourceGroup, that.resourceGroup) && Objects.equal(availabilitySet, that.availabilitySet)
            && Objects.equal(dataDisks, that.dataDisks) && Objects.equal(ipOptions, that.ipOptions)
            && Objects.equal(windowsConfiguration, that.windowsConfiguration) && Objects.equal(secrets, that.secrets)
            && Objects.equal(this.customData, that.customData);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), availabilitySet, availabilitySetName, dataDisks, resourceGroup,
            ipOptions, customData);
   }

   @Override
   public MoreObjects.ToStringHelper string() {
      MoreObjects.ToStringHelper toString = super.string();
      if (availabilitySet != null)
         toString.add("availabilitySet", availabilitySet);
      if (availabilitySetName != null)
         toString.add("availabilitySetName", availabilitySetName);
      if (!dataDisks.isEmpty())
         toString.add("dataDisks", dataDisks);
      if (resourceGroup != null)
         toString.add("resourceGroup", resourceGroup);
      if (!ipOptions.isEmpty())
         toString.add("ipOptions", ipOptions);
      if (windowsConfiguration != null)
         toString.add("windowsConfiguration", windowsConfiguration);
      if (!secrets.isEmpty())
         toString.add("secrets", secrets);
      if (customData != null)
         toString.add("customData", customData);
      return toString;
   }

   public static class Builder {

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

      /**
       * @see AzureTemplateOptions#ipOptions(IpOptions...)
       */
      public static AzureTemplateOptions ipOptions(IpOptions... ipOptions) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.ipOptions(ipOptions);
      }

      /**
       * @see AzureTemplateOptions#ipOptions(Iterable)
       */
      public static AzureTemplateOptions ipOptions(Iterable<IpOptions> ipOptions) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.ipOptions(ipOptions);
      }

      /**
       * @see AzureTemplateOptions#windowsConfiguration(WindowsConfiguration)
       */
      public static AzureTemplateOptions windowsConfiguration(WindowsConfiguration windowsConfiguration) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.windowsConfiguration(windowsConfiguration);
      }

      /**
       * @see AzureTemplateOptions#secrets(Iterable)
       */
      public static AzureTemplateOptions secrets(Iterable<? extends Secrets> secrets) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.secrets(secrets);
      }

      /**
       * @see AzureTemplateOptions#customData(String)
       */
      public static AzureTemplateOptions customData(String customData) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.customData(customData);
      }

      /**
       * @see AzureTemplateOptions#osDiskStorageType(StorageAccountType)
       */
      public static AzureTemplateOptions osDiskStorageType(StorageAccountType osDiskStorageType) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.osDiskStorageType(osDiskStorageType);
      }
   }
}
