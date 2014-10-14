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
package org.jclouds.softlayer.domain.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

public class TemplateObject {
   private final String hostname;
   private final String domain;
   private final int startCpus;
   private final int maxMemory;
   private final boolean hourlyBillingFlag;
   private final boolean localDiskFlag;
   private final boolean dedicatedAccountHostOnlyFlag;
   private final boolean privateNetworkOnlyFlag;
   private final BlockDeviceTemplateGroup blockDeviceTemplateGroup;
   private final String operatingSystemReferenceCode;
   private final Datacenter datacenter;
   private final Set<NetworkComponent> networkComponents;
   private final List<BlockDevice> blockDevices;
   private final String postInstallScriptUri;
   private final PrimaryNetworkComponent primaryNetworkComponent;
   private final PrimaryBackendNetworkComponent primaryBackendNetworkComponent;
   private final Set<Map<String, String>> userData;
   private final Set<Map<String, Integer>> sshKeys;

   private TemplateObject(String hostname, String domain, int startCpus, int maxMemory, boolean hourlyBillingFlag,
                          boolean localDiskFlag, boolean dedicatedAccountHostOnlyFlag,
                          boolean privateNetworkOnlyFlag, String operatingSystemReferenceCode,
                          BlockDeviceTemplateGroup blockDeviceTemplateGroup, Datacenter datacenter,
                          Set<NetworkComponent> networkComponents, List<BlockDevice> blockDevices,
                          String postInstallScriptUri, PrimaryNetworkComponent primaryNetworkComponent,
                          PrimaryBackendNetworkComponent primaryBackendNetworkComponent, Set<Map<String,
           String>> userData, Set<Map<String, Integer>> sshKeys) {
      this.hostname = hostname;
      this.domain = domain;
      this.startCpus = startCpus;
      this.maxMemory = maxMemory;
      this.hourlyBillingFlag = hourlyBillingFlag;
      this.localDiskFlag = localDiskFlag;
      this.dedicatedAccountHostOnlyFlag = dedicatedAccountHostOnlyFlag;
      this.privateNetworkOnlyFlag = privateNetworkOnlyFlag;
      this.operatingSystemReferenceCode = operatingSystemReferenceCode;
      this.blockDeviceTemplateGroup = blockDeviceTemplateGroup;
      this.datacenter = datacenter;
      this.networkComponents = networkComponents;
      this.blockDevices = blockDevices;
      this.postInstallScriptUri = postInstallScriptUri;
      this.primaryNetworkComponent = primaryNetworkComponent;
      this.primaryBackendNetworkComponent = primaryBackendNetworkComponent;
      this.userData = userData;
      this.sshKeys = sshKeys;
   }

   public String getHostname() {
      return hostname;
   }

   public String getDomain() {
      return domain;
   }

   public int getStartCpus() {
      return startCpus;
   }

   public int getMaxMemory() {
      return maxMemory;
   }

   public boolean isHourlyBillingFlag() {
      return hourlyBillingFlag;
   }

   public boolean isLocalDiskFlag() {
      return localDiskFlag;
   }

   public boolean isDedicatedAccountHostOnlyFlag() {
      return dedicatedAccountHostOnlyFlag;
   }

   public boolean isPrivateNetworkOnlyFlag() {
      return privateNetworkOnlyFlag;
   }

   public BlockDeviceTemplateGroup getBlockDeviceTemplateGroup() {
      return blockDeviceTemplateGroup;
   }

   public String getOperatingSystemReferenceCode() {
      return operatingSystemReferenceCode;
   }

   public Datacenter getDatacenter() {
      return datacenter;
   }

   public Set<NetworkComponent> getNetworkComponents() {
      return networkComponents;
   }

   public List<BlockDevice> getBlockDevices() {
      return blockDevices;
   }

   public String getPostInstallScriptUri() {
      return postInstallScriptUri;
   }

   public PrimaryNetworkComponent getPrimaryNetworkComponent() {
      return primaryNetworkComponent;
   }

   public Set<Map<String, String>> getUserData() {
      return userData;
   }

   public Set<Map<String, Integer>> getSshKeys() {
      return sshKeys;
   }

   public PrimaryBackendNetworkComponent getPrimaryBackendNetworkComponent() {
      return primaryBackendNetworkComponent;
   }
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromTemplateObject(this);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateObject that = (TemplateObject) o;

      return Objects.equal(this.hostname, that.hostname) &&
              Objects.equal(this.domain, that.domain) &&
              Objects.equal(this.startCpus, that.startCpus) &&
              Objects.equal(this.maxMemory, that.maxMemory) &&
              Objects.equal(this.hourlyBillingFlag, that.hourlyBillingFlag) &&
              Objects.equal(this.localDiskFlag, that.localDiskFlag) &&
              Objects.equal(this.dedicatedAccountHostOnlyFlag, that.dedicatedAccountHostOnlyFlag) &&
              Objects.equal(this.privateNetworkOnlyFlag, that.privateNetworkOnlyFlag) &&
              Objects.equal(this.blockDeviceTemplateGroup, that.blockDeviceTemplateGroup) &&
              Objects.equal(this.operatingSystemReferenceCode, that.operatingSystemReferenceCode) &&
              Objects.equal(this.datacenter, that.datacenter) &&
              Objects.equal(this.networkComponents, that.networkComponents) &&
              Objects.equal(this.blockDevices, that.blockDevices) &&
              Objects.equal(this.postInstallScriptUri, that.postInstallScriptUri) &&
              Objects.equal(this.primaryNetworkComponent, that.primaryNetworkComponent) &&
              Objects.equal(this.primaryBackendNetworkComponent, that.primaryBackendNetworkComponent) &&
              Objects.equal(this.userData, that.userData) &&
              Objects.equal(this.sshKeys, that.sshKeys);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(hostname, domain, startCpus, maxMemory, hourlyBillingFlag, localDiskFlag,
              dedicatedAccountHostOnlyFlag, privateNetworkOnlyFlag, blockDeviceTemplateGroup, operatingSystemReferenceCode, datacenter,
              networkComponents, blockDevices, postInstallScriptUri, primaryNetworkComponent,
              primaryBackendNetworkComponent, userData, sshKeys);
   }


   public static class Builder {

      protected String hostname;
      protected String domain;
      protected int startCpus;
      protected int maxMemory;
      protected boolean hourlyBillingFlag;
      protected boolean localDiskFlag;
      protected boolean dedicatedAccountHostOnlyFlag;
      protected boolean privateNetworkOnlyFlag;
      protected String operatingSystemReferenceCode;
      protected BlockDeviceTemplateGroup blockDeviceTemplateGroup;
      protected Datacenter datacenter;
      protected Set<NetworkComponent> networkComponents;
      protected List<BlockDevice> blockDevices;
      protected String postInstallScriptUri;
      protected PrimaryNetworkComponent primaryNetworkComponent;
      protected PrimaryBackendNetworkComponent primaryBackendNetworkComponent;
      protected Set<Map<String, String>> userData;
      protected Set<Map<String, Integer>> sshKeys;

      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder startCpus(int startCpus) {
         this.startCpus = startCpus;
         return this;
      }

      public Builder maxMemory(int maxMemory) {
         this.maxMemory = maxMemory;
         return this;
      }

      public Builder hourlyBillingFlag(boolean hourlyBillingFlag) {
         this.hourlyBillingFlag = hourlyBillingFlag;
         return this;
      }

      public Builder localDiskFlag(boolean localDiskFlag) {
         this.localDiskFlag = localDiskFlag;
         return this;
      }

      public Builder dedicatedAccountHostOnlyFlag(boolean dedicatedAccountHostOnlyFlag) {
         this.dedicatedAccountHostOnlyFlag = dedicatedAccountHostOnlyFlag;
         return this;
      }

      public Builder privateNetworkOnlyFlag(boolean privateNetworkOnlyFlag) {
         this.privateNetworkOnlyFlag = privateNetworkOnlyFlag;
         return this;
      }

      public Builder operatingSystemReferenceCode(String operatingSystemReferenceCode) {
         this.operatingSystemReferenceCode = operatingSystemReferenceCode;
         return this;
      }

      public Builder blockDeviceTemplateGroup(BlockDeviceTemplateGroup blockDeviceTemplateGroup) {
         this.blockDeviceTemplateGroup = blockDeviceTemplateGroup;
         return this;
      }

      public Builder datacenter(Datacenter datacenter) {
         this.datacenter = datacenter;
         return this;
      }

      public Builder networkComponents(Set<NetworkComponent> networkComponents) {
         this.networkComponents = networkComponents;
         return this;
      }

      public Builder blockDevices(List<BlockDevice> blockDevices) {
         this.blockDevices = blockDevices;
         return this;
      }

      public Builder postInstallScriptUri(String postInstallScriptUri) {
         this.postInstallScriptUri = postInstallScriptUri;
         return this;
      }

      public Builder primaryNetworkComponent(PrimaryNetworkComponent primaryNetworkComponent) {
         this.primaryNetworkComponent = primaryNetworkComponent;
         return this;
      }

      public Builder primaryBackendNetworkComponent(PrimaryBackendNetworkComponent primaryBackendNetworkComponent) {
         this.primaryBackendNetworkComponent = primaryBackendNetworkComponent;
         return this;
      }

      public Builder userData(Set<Map<String, String>> userData) {
         this.userData = ImmutableSet.copyOf(userData);
         return this;
      }

      public Builder sshKeys(Set<Map<String, Integer>> sshKeys) {
         this.sshKeys = ImmutableSet.copyOf(sshKeys);
         return this;
      }

      public TemplateObject build() {
         return new TemplateObject(hostname, domain, startCpus, maxMemory, hourlyBillingFlag, localDiskFlag,
                 dedicatedAccountHostOnlyFlag, privateNetworkOnlyFlag, operatingSystemReferenceCode,
                 blockDeviceTemplateGroup, datacenter, networkComponents, blockDevices, postInstallScriptUri,
                 primaryNetworkComponent, primaryBackendNetworkComponent, userData, sshKeys);
      }

      public Builder fromTemplateObject(TemplateObject in) {
         return this
                 .hostname(in.getHostname())
                 .domain(in.getDomain())
                 .startCpus(in.getStartCpus())
                 .maxMemory(in.getMaxMemory())
                 .hourlyBillingFlag(in.isHourlyBillingFlag())
                 .localDiskFlag(in.isLocalDiskFlag())
                 .dedicatedAccountHostOnlyFlag(in.isDedicatedAccountHostOnlyFlag())
                 .privateNetworkOnlyFlag(in.isPrivateNetworkOnlyFlag())
                 .operatingSystemReferenceCode(in.getOperatingSystemReferenceCode())
                 .blockDeviceTemplateGroup(in.getBlockDeviceTemplateGroup())
                 .datacenter(in.getDatacenter())
                 .networkComponents(in.getNetworkComponents())
                 .blockDevices(in.getBlockDevices())
                 .postInstallScriptUri(in.getPostInstallScriptUri())
                 .primaryNetworkComponent(in.getPrimaryNetworkComponent())
                 .primaryBackendNetworkComponent(in.getPrimaryBackendNetworkComponent())
                 .userData(in.getUserData())
                 .sshKeys(in.getSshKeys());
      }

   }

}
