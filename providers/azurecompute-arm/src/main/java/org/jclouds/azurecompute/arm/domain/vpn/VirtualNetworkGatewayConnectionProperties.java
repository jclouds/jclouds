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
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnection.Status;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnection.Type;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class VirtualNetworkGatewayConnectionProperties implements Provisionable {

   @Nullable public abstract String authorizationKey();
   @Nullable public abstract Status connectionStatus();
   public abstract Type connectionType();
   @Nullable public abstract Integer egressBytesTransferred();
   @Nullable public abstract Integer ingressBytesTransferred();
   public abstract boolean enableBGP();
   public abstract List<IPSecPolicy> ipsecPolicies();
   @Nullable public abstract IdReference peer();
   @Nullable public abstract String provisioningState();
   @Nullable public abstract String resourceGuid();
   @Nullable public abstract Integer routingWeight();
   @Nullable public abstract String sharedKey();
   public abstract List<TunnelConnectionHealth> tunnelConnectionStatus();
   public abstract boolean usePolicyBasedTrafficSelectors();
   public abstract IdReference localNetworkGateway2();
   public abstract IdReference virtualNetworkGateway1();
   @Nullable public abstract IdReference virtualNetworkGateway2();

   VirtualNetworkGatewayConnectionProperties() {

   }
   
   @SerializedNames({ "authorizationKey", "connectionStatus", "connectionType", "egressBytesTransferred",
         "ingressBytesTransferred", "enableBGP", "ipsecPolicies", "peer", "provisioningState", "resourceGuid",
         "routingWeight", "sharedKey", "tunnelConnectionStatus", "usePolicyBasedTrafficSelectors",
         "localNetworkGateway2", "virtualNetworkGateway1", "virtualNetworkGateway2" })
   public static VirtualNetworkGatewayConnectionProperties create(String authorizationKey, Status connectionStatus,
         Type connectionType, Integer egressBytesTransferred, Integer ingressBytesTransferred, boolean enableBGP,
         List<IPSecPolicy> ipsecPolicies, IdReference peer, String provisioningState, String resourceGuid,
         Integer routingWeight, String sharedKey, List<TunnelConnectionHealth> tunnelConnectionStatus,
         boolean usePolicyBasedTrafficSelectors, IdReference localNetworkGateway2, IdReference virtualNetworkGateway1,
         IdReference virtualNetworkGateway2) {
      return builder(connectionType, enableBGP, usePolicyBasedTrafficSelectors, localNetworkGateway2,
            virtualNetworkGateway1).authorizationKey(authorizationKey).connectionStatus(connectionStatus)
            .egressBytesTransferred(egressBytesTransferred).ingressBytesTransferred(ingressBytesTransferred)
            .ipsecPolicies(ipsecPolicies).peer(peer).provisioningState(provisioningState).resourceGuid(resourceGuid)
            .routingWeight(routingWeight).sharedKey(sharedKey).tunnelConnectionStatus(tunnelConnectionStatus)
            .virtualNetworkGateway2(virtualNetworkGateway2).build();
   }
   
   public static Builder builder(Type connectionType, boolean enableBGP, boolean usePolicyBasedTrafficSelectors,
         IdReference localNetworkGateway2, IdReference virtualNetworkGateway1) {
      return new AutoValue_VirtualNetworkGatewayConnectionProperties.Builder().connectionType(connectionType)
            .enableBGP(enableBGP).usePolicyBasedTrafficSelectors(usePolicyBasedTrafficSelectors)
            .localNetworkGateway2(localNetworkGateway2).virtualNetworkGateway1(virtualNetworkGateway1)
            .ipsecPolicies(ImmutableList.<IPSecPolicy> of())
            .tunnelConnectionStatus(ImmutableList.<TunnelConnectionHealth> of());
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder authorizationKey(String authorizationKey);
      public abstract Builder connectionStatus(Status connectionStatus);
      public abstract Builder connectionType(Type connectionType);
      public abstract Builder egressBytesTransferred(Integer egressBytesTransferred);
      public abstract Builder ingressBytesTransferred(Integer ingressBytesTransferred);
      public abstract Builder enableBGP(boolean enableBGP);
      public abstract Builder ipsecPolicies(List<IPSecPolicy> ipsecPolicies);
      public abstract Builder peer(IdReference peer);
      public abstract Builder provisioningState(String provisioningState);
      public abstract Builder resourceGuid(String resourceGuid);
      public abstract Builder routingWeight(Integer routingWeight);
      public abstract Builder sharedKey(String sharedKey);
      public abstract Builder tunnelConnectionStatus(List<TunnelConnectionHealth> tunnelConnectionStatus);
      public abstract Builder usePolicyBasedTrafficSelectors(boolean usePolicyBasedTrafficSelectors);
      public abstract Builder localNetworkGateway2(IdReference localNetworkGateway2);
      public abstract Builder virtualNetworkGateway1(IdReference virtualNetworkGateway1);
      public abstract Builder virtualNetworkGateway2(IdReference virtualNetworkGateway2);

      abstract List<IPSecPolicy> ipsecPolicies();
      abstract List<TunnelConnectionHealth> tunnelConnectionStatus();
      abstract VirtualNetworkGatewayConnectionProperties autoBuild();

      public VirtualNetworkGatewayConnectionProperties build() {
         ipsecPolicies(ipsecPolicies() == null ? ImmutableList.<IPSecPolicy> of() : ImmutableList
               .copyOf(ipsecPolicies()));
         tunnelConnectionStatus(tunnelConnectionStatus() == null ? ImmutableList.<TunnelConnectionHealth> of()
               : ImmutableList.copyOf(tunnelConnectionStatus()));
         return autoBuild();
      }
   }
}
