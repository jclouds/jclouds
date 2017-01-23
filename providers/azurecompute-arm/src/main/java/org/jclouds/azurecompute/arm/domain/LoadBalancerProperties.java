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
package org.jclouds.azurecompute.arm.domain;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class LoadBalancerProperties implements Provisionable {

   @Nullable
   public abstract List<FrontendIPConfigurations> frontendIPConfigurations();

   @Nullable
   public abstract List<BackendAddressPool> backendAddressPools();

   @Nullable
   public abstract List<LoadBalancingRule> loadBalancingRules();

   @Nullable
   public abstract List<Probe> probes();

   @Nullable
   public abstract List<InboundNatRule> inboundNatRules();

   @Nullable
   public abstract String resourceGuid();

   @Nullable
   public abstract String provisioningState();

   @SerializedNames({ "frontendIPConfigurations", "backendAddressPools", "loadBalancingRules", "probes",
         "inboundNatRules", "resourceGuid", "provisioningState" })
   public static LoadBalancerProperties create(final List<FrontendIPConfigurations> frontendIPConfigurations,
         final List<BackendAddressPool> backendAddressPools, final List<LoadBalancingRule> loadBalancingRules,
         final List<Probe> probes, final List<InboundNatRule> inboundNatRules, final String resourceGuid,
         final String provisioningState) {
      return builder().frontendIPConfigurations(frontendIPConfigurations)
            .backendAddressPools(backendAddressPools == null ? null : ImmutableList.copyOf(backendAddressPools))
            .loadBalancingRules(loadBalancingRules == null ? null : ImmutableList.copyOf(loadBalancingRules))
            .probes(probes == null ? null : ImmutableList.copyOf(probes))
            .inboundNatRules(inboundNatRules == null ? null : ImmutableList.copyOf(inboundNatRules))
            .resourceGuid(resourceGuid).provisioningState(provisioningState).build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_LoadBalancerProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder frontendIPConfigurations(List<FrontendIPConfigurations> frontendIPConfigurations);

      public abstract Builder backendAddressPools(List<BackendAddressPool> backendAddressPools);

      public abstract Builder loadBalancingRules(List<LoadBalancingRule> networkInterfaces);

      public abstract Builder probes(List<Probe> probes);

      public abstract Builder inboundNatRules(List<InboundNatRule> inboundNatRules);

      public abstract Builder resourceGuid(String resourceGuid);

      public abstract Builder provisioningState(String provisioningState);

      abstract List<FrontendIPConfigurations> frontendIPConfigurations();

      abstract List<BackendAddressPool> backendAddressPools();

      abstract List<LoadBalancingRule> loadBalancingRules();

      abstract List<Probe> probes();

      abstract List<InboundNatRule> inboundNatRules();

      abstract LoadBalancerProperties autoBuild();

      public LoadBalancerProperties build() {
         frontendIPConfigurations(frontendIPConfigurations() != null ? ImmutableList.copyOf(frontendIPConfigurations())
               : null);
         backendAddressPools(backendAddressPools() != null ? ImmutableList.copyOf(backendAddressPools()) : null);
         loadBalancingRules(loadBalancingRules() != null ? ImmutableList.copyOf(loadBalancingRules()) : null);
         probes(probes() != null ? ImmutableList.copyOf(probes()) : null);
         inboundNatRules(inboundNatRules() != null ? ImmutableList.copyOf(inboundNatRules()) : null);
         return autoBuild();
      }
   }
}
