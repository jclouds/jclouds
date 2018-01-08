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

import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LoadBalancingRuleProperties {

   public enum Protocol {
      Tcp("Tcp"), Udp("Udp"), UNRECOGNIZED("Unrecognized");

      private final String label;

      private Protocol(final String label) {
         this.label = label;
      }

      public static Protocol fromValue(final String text) {
         return (Protocol) GetEnumValue.fromValueOrDefault(text, Protocol.UNRECOGNIZED);
      }

      @Override
      public String toString() {
         return label;
      }
   }
   
   public enum LoadDistribution {
      Default("Default"), SourceIp("SourceIP"), SourceIPProtocol("SourceIPProtocol"), UNRECOGNIZED("Unrecognized");

      private final String label;

      private LoadDistribution(final String label) {
         this.label = label;
      }

      public static LoadDistribution fromValue(final String text) {
         return (LoadDistribution) GetEnumValue.fromValueOrDefault(text, LoadDistribution.UNRECOGNIZED);
      }

      @Override
      public String toString() {
         return label;
      }
   }

   @Nullable
   public abstract IdReference frontendIPConfiguration();

   @Nullable
   public abstract IdReference backendAddressPool();

   public abstract Protocol protocol();

   public abstract int frontendPort();

   public abstract int backendPort();

   @Nullable
   public abstract IdReference probe();

   @Nullable
   public abstract Boolean enableFloatingIP();

   @Nullable
   public abstract Integer idleTimeoutInMinutes();

   @Nullable
   public abstract LoadDistribution loadDistribution();

   @Nullable
   public abstract String provisioningState();

   @SerializedNames({ "frontendIPConfiguration", "backendAddressPool", "protocol", "frontendPort", "backendPort",
         "probe", "enableFloatingIP", "idleTimeoutInMinutes", "loadDistribution", "provisioningState" })
   public static LoadBalancingRuleProperties create(final IdReference frontendIPConfiguration,
         final IdReference backendAddressPool, final Protocol protocol, final int frontendPort, final int backendPort,
         final IdReference probe, final Boolean enableFloatingIP, final Integer idleTimeoutInMinutes,
         final LoadDistribution loadDistribution, final String provisioningState) {
      return builder().frontendIPConfiguration(frontendIPConfiguration).backendAddressPool(backendAddressPool)
            .protocol(protocol).frontendPort(frontendPort).backendPort(backendPort).probe(probe)
            .enableFloatingIP(enableFloatingIP).idleTimeoutInMinutes(idleTimeoutInMinutes)
            .loadDistribution(loadDistribution).build();
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_LoadBalancingRuleProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder frontendIPConfiguration(IdReference frontendIPConfiguration);

      public abstract Builder backendAddressPool(IdReference backendAddressPool);

      public abstract Builder protocol(Protocol protocol);

      public abstract Builder frontendPort(int frontendPort);

      public abstract Builder backendPort(int backendPort);

      public abstract Builder probe(IdReference probe);

      public abstract Builder enableFloatingIP(Boolean enableFloatingIP);

      public abstract Builder idleTimeoutInMinutes(Integer idleTimeoutInMinutes);

      public abstract Builder loadDistribution(LoadDistribution loadDistribution);

      public abstract Builder provisioningState(String provisioningState);

      public abstract LoadBalancingRuleProperties build();
   }
}
