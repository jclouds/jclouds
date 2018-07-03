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
package org.jclouds.azurecompute.arm.domain.vpn;

import java.util.List;

import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpAllocationMethod;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class VirtualNetworkGatewayProperties implements Provisionable {
   
   @Nullable public abstract Boolean activeActive();
   @Nullable public abstract BGPSettings bgpSettings();
   public abstract boolean enableBGP();
   @Nullable public abstract IdReference gatewayDefaultSite();
   public abstract VirtualNetworkGatewayType gatewayType();
   public abstract List<IpConfiguration> ipConfigurations();
   @Nullable public abstract String provisioningState();
   @Nullable public abstract String resourceGuid();
   public abstract SKU sku();
   @Nullable public abstract VPNClientConfiguration vpnClientConfiguration();
   @Nullable public abstract VPNType vpnType();

   VirtualNetworkGatewayProperties() {

   }
   
   @SerializedNames({ "activeActive", "bgpSettings", "enableBgp", "gatewayDefaultSite", "gatewayType",
         "ipConfigurations", "provisioningState", "resourceGuid", "sku", "vpnClientConfiguration", "vpnType" })
   public static VirtualNetworkGatewayProperties create(Boolean activeActive, BGPSettings bgpSettings,
         boolean enableBGP, IdReference gatewayDefaultSite, VirtualNetworkGatewayType gatewayType,
         List<IpConfiguration> ipConfigurations, String provisioningState, String resourceGuid, SKU sku,
         VPNClientConfiguration vpnClientConfiguration, VPNType vpnType) {
      return builder(enableBGP, gatewayType, sku).activeActive(activeActive).bgpSettings(bgpSettings)
            .gatewayDefaultSite(gatewayDefaultSite).ipConfigurations(ipConfigurations)
            .provisioningState(provisioningState).resourceGuid(resourceGuid)
            .vpnClientConfiguration(vpnClientConfiguration).vpnType(vpnType).build();
   }
   
   public static Builder builder(boolean enableBGP, VirtualNetworkGatewayType virtualNetworkGatewayType, SKU sku) {
      return new AutoValue_VirtualNetworkGatewayProperties.Builder().enableBGP(enableBGP)
            .gatewayType(virtualNetworkGatewayType).sku(sku).ipConfigurations(ImmutableList.<IpConfiguration> of());
   }
   
   public abstract Builder toBuilder();
   
   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder activeActive( Boolean activeActive);
      public abstract Builder bgpSettings(BGPSettings bgpSettings);
      public abstract Builder enableBGP(boolean enableBG);
      public abstract Builder gatewayDefaultSite(IdReference gatewayDefaultSite);
      public abstract Builder gatewayType(VirtualNetworkGatewayType gatewayType);
      public abstract Builder ipConfigurations(List<IpConfiguration> ipConfigurations);
      public abstract Builder provisioningState(String provisioningState);
      public abstract Builder resourceGuid(String resourceGuid);
      public abstract Builder sku(SKU sku);
      public abstract Builder vpnClientConfiguration(VPNClientConfiguration vpnClientConfiguration);
      public abstract Builder vpnType(VPNType vpnType);
      
      abstract List<IpConfiguration> ipConfigurations();
      abstract VirtualNetworkGatewayProperties autoBuild();

      public VirtualNetworkGatewayProperties build() {
         ipConfigurations(ipConfigurations() == null ? ImmutableList.<IpConfiguration> of() : ImmutableList
               .copyOf(ipConfigurations()));
         return autoBuild();
      }
   }
   
   @AutoValue
   public abstract static class IpConfiguration {
      @Nullable public abstract String id();
      public abstract String name();
      @Nullable public abstract String etag();
      public abstract IpConfigurationProperties properties();
      
      IpConfiguration() {

      }

      @SerializedNames({ "id", "name", "etag", "properties" })
      public static IpConfiguration create(String id, String name, String etag, IpConfigurationProperties properties) {
         return new AutoValue_VirtualNetworkGatewayProperties_IpConfiguration(id, name, etag, properties);
      }
      
      @AutoValue
      public abstract static class IpConfigurationProperties implements Provisionable {
         public abstract IpAllocationMethod privateIPAllocationMethod();
         @Nullable public abstract String provisioningState();
         @Nullable public abstract IdReference publicIPAddress();
         @Nullable public abstract IdReference subnet();
         
         @SerializedNames({ "privateIPAllocationMethod", "provisioningState", "publicIPAddress", "subnet" })
         public static IpConfigurationProperties create(IpAllocationMethod privateIPAllocationMethod,
               String provisioningState, IdReference publicIPAddress, IdReference subnet) {
            return builder(privateIPAllocationMethod).provisioningState(provisioningState)
                  .publicIPAddress(publicIPAddress).subnet(subnet).build();
         }

         IpConfigurationProperties() {

         }
         
         public static Builder builder(IpAllocationMethod privateIPAllocationMethod) {
            return new AutoValue_VirtualNetworkGatewayProperties_IpConfiguration_IpConfigurationProperties.Builder()
                  .privateIPAllocationMethod(privateIPAllocationMethod);
         }
         
         public abstract Builder toBuilder();
         
         @AutoValue.Builder
         public abstract static class Builder {
            public abstract Builder privateIPAllocationMethod(IpAllocationMethod privateIPAllocationMethod);
            public abstract Builder provisioningState(String provisioningState);
            public abstract Builder publicIPAddress(IdReference publicIPAddress);
            public abstract Builder subnet(IdReference subnet);

            public abstract IpConfigurationProperties build();
         }
      }
   }
}
