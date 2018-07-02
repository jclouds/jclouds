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

import org.jclouds.azurecompute.arm.domain.AddressSpace;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LocalNetworkGatewayProperties implements Provisionable {
   
   @Nullable public abstract BGPSettings bgpSettings();
   public abstract String gatewayIpAddress();
   @Nullable public abstract AddressSpace localNetworkAddressSpace();
   @Nullable public abstract String provisioningState();
   @Nullable public abstract String resourceGuid();

   @SerializedNames({ "bgpSettings", "gatewayIpAddress", "localNetworkAddressSpace", "provisioningState",
         "resourceGuid" })
   public static LocalNetworkGatewayProperties create(BGPSettings bgpSettings, String gatewayIpAddress,
         AddressSpace localNetworkAddressSpace, String provisioningState, String resourceGuid) {
      return builder(gatewayIpAddress).bgpSettings(bgpSettings).localNetworkAddressSpace(localNetworkAddressSpace)
            .provisioningState(provisioningState).resourceGuid(resourceGuid).build();
   }

   LocalNetworkGatewayProperties() {

   }

   public abstract LocalNetworkGatewayProperties.Builder toBuilder();

   public static LocalNetworkGatewayProperties.Builder builder(String gatewayIpAddress) {
      return new AutoValue_LocalNetworkGatewayProperties.Builder().gatewayIpAddress(gatewayIpAddress);
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder bgpSettings(BGPSettings bgpSettings);
      public abstract Builder gatewayIpAddress(String gatewayIpAddress);
      public abstract Builder localNetworkAddressSpace(AddressSpace localNetworkAddressSpace);
      public abstract Builder provisioningState(String provisioningState);
      public abstract Builder resourceGuid(String resourceGuid);

      public abstract LocalNetworkGatewayProperties build();
   }
}
