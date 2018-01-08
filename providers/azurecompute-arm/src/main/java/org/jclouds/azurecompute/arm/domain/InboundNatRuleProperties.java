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
public abstract class InboundNatRuleProperties implements Provisionable {
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

   @Nullable
   public abstract IdReference frontendIPConfiguration();

   @Nullable
   public abstract IdReference backendIPConfiguration();

   public abstract Protocol protocol();

   public abstract int backendPort();

   public abstract int frontendPort();

   @Nullable
   public abstract Boolean enableFloatingIP();

   @Nullable
   public abstract Integer idleTimeoutInMinutes();

   @Nullable
   public abstract String provisioningState();

   @SerializedNames({ "frontendIPConfiguration", "backendIPConfiguration", "protocol", "frontendPort", "backendPort",
         "provisioningState", "enableFloatingIP", "idleTimeoutInMinutes" })
   public static InboundNatRuleProperties create(final IdReference frontendIPConfiguration,
         final IdReference backendIPConfiguration, final Protocol protocol, final int frontendPort,
         final int backendPort, final String provisioningState, Boolean enableFloatingIP, Integer idleTimeoutInMinutes) {
      return builder().frontendIPConfiguration(frontendIPConfiguration).backendIPConfiguration(backendIPConfiguration)
            .protocol(protocol).frontendPort(frontendPort).backendPort(backendPort)
            .provisioningState(provisioningState).enableFloatingIP(enableFloatingIP)
            .idleTimeoutInMinutes(idleTimeoutInMinutes).build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_InboundNatRuleProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder frontendIPConfiguration(IdReference frontendIPConfiguration);

      public abstract Builder backendIPConfiguration(IdReference backendIPConfiguration);

      public abstract Builder protocol(Protocol protocol);

      public abstract Builder frontendPort(int frontendPort);

      public abstract Builder backendPort(int backendPort);

      public abstract Builder provisioningState(String provisioningState);

      public abstract Builder enableFloatingIP(Boolean enableFloatingIP);

      public abstract Builder idleTimeoutInMinutes(Integer idleTimeoutInMinutes);

      public abstract InboundNatRuleProperties build();
   }
}
