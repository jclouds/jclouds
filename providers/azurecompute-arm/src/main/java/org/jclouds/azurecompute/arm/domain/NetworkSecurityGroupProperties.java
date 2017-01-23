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

import com.google.auto.value.AutoValue;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import com.google.common.collect.ImmutableList;

import java.util.List;

@AutoValue
public abstract class NetworkSecurityGroupProperties implements Provisionable {

   @Nullable
   public abstract List<NetworkSecurityRule> securityRules();

   @Nullable
   public abstract List<NetworkSecurityRule> defaultSecurityRules();

   @Nullable
   public abstract List<NetworkInterfaceCard> networkInterfaces();

   @Nullable
   public abstract List<Subnet> subnets();

   @Nullable
   public abstract String resourceGuid();

   @Nullable
   public abstract String provisioningState();

   @SerializedNames({"securityRules", "defaultSecurityRules", "networkInterfaces", "subnets", "resourceGuid", "provisioningState"})
   public static NetworkSecurityGroupProperties create(final List<NetworkSecurityRule> securityRules,
                                          final List<NetworkSecurityRule> defaultSecurityRules,
                                          final List<NetworkInterfaceCard> networkInterfaces,
                                          final List<Subnet> subnets,
                                          final String resourceGuid,
                                          final String provisioningState) {
      return builder()
              .securityRules((securityRules == null) ? null : ImmutableList.copyOf(securityRules))
              .defaultSecurityRules((defaultSecurityRules == null) ? null : ImmutableList.copyOf(defaultSecurityRules))
              .networkInterfaces((networkInterfaces == null) ? null : ImmutableList.copyOf(networkInterfaces))
              .subnets((subnets == null) ? null : ImmutableList.copyOf(subnets))
              .resourceGuid(resourceGuid)
              .provisioningState(provisioningState)
              .build();
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_NetworkSecurityGroupProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder securityRules(List<NetworkSecurityRule> securityRules);

      public abstract Builder defaultSecurityRules(List<NetworkSecurityRule> securityRules);

      public abstract Builder networkInterfaces(List<NetworkInterfaceCard> networkInterfaces);

      public abstract Builder subnets(List<Subnet> subnets);

      public abstract Builder resourceGuid(String resourceGuid);

      public abstract Builder provisioningState(String provisioningState);

      public abstract NetworkSecurityGroupProperties build();
   }
}

