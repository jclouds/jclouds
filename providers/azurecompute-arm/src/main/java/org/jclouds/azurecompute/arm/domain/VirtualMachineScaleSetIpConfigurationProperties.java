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
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class VirtualMachineScaleSetIpConfigurationProperties {

   /**
    * The public IP address configuration of the Virtual Machine Scale Set Ip Configuration Properties
    */
   @Nullable
   public abstract VirtualMachineScaleSetPublicIPAddressConfiguration publicIPAddressConfiguration();

   /**
    * The subnet of the Virtual Machine Scale Set Ip Configuration Properties
    */
   public abstract Subnet subnet();

   /**
    * The private IP address version of the Virtual Machine Scale Set Ip Configuration Properties
    */
   @Nullable
   public abstract String privateIPAddressVersion();

   /**
    * The load balancer backend address pools of the Virtual Machine Scale Set Ip Configuration Properties
    */
   public abstract List<IdReference> loadBalancerBackendAddressPools();

   /**
    * The load balancer inbound nat pools of the Virtual Machine Scale Set Ip Configuration Properties
    */
   public abstract List<IdReference> loadBalancerInboundNatPools();

   /**
    * The application gateway backend address Pools of the Virtual Machine Scale Set Ip Configuration Properties
    */
   @Nullable
   public abstract String applicationGatewayBackendAddressPools();

   @SerializedNames({"publicIPAddressConfiguration", "subnet", "privateIPAddressVersion",
      "loadBalancerBackendAddressPools", "loadBalancerInboundNatPools", "applicationGatewayBackendAddressPools"})
   public static VirtualMachineScaleSetIpConfigurationProperties create(
      final VirtualMachineScaleSetPublicIPAddressConfiguration publicIPAddressConfiguration,
      final Subnet subnet,
      final String privateIPAddressVersion,
      final List<IdReference> loadBalancerBackendAddressPools,
      final List<IdReference> loadBalancerInboundNatPools,
      final String applicationGatewayBackendAddressPools)

   {

      return builder()
         .publicIPAddressConfiguration(publicIPAddressConfiguration)
         .subnet(subnet)
         .privateIPAddressVersion(privateIPAddressVersion)
         .lbBackendAddressPools(loadBalancerBackendAddressPools)
         .lbInboundNatPools(loadBalancerInboundNatPools)
         .applicationGatewayBackendAddressPools(applicationGatewayBackendAddressPools)
         .build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_VirtualMachineScaleSetIpConfigurationProperties.Builder()
              .lbBackendAddressPools(null)
              .lbInboundNatPools(null);
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder publicIPAddressConfiguration(VirtualMachineScaleSetPublicIPAddressConfiguration publicIPAddressConfiguration);

      public abstract Builder subnet(Subnet subnet);

      public abstract Builder loadBalancerBackendAddressPools(List<IdReference> loadBalancerBackendAddressPools);

      public abstract Builder loadBalancerInboundNatPools(List<IdReference> loadBalancerInboundNatPools);

      public abstract Builder privateIPAddressVersion(String privateIPAddressVersion);

      public Builder lbBackendAddressPools(List<IdReference> loadBalancerBackendAddressPools) {
         return loadBalancerBackendAddressPools(loadBalancerBackendAddressPools != null ? ImmutableList
                 .copyOf(loadBalancerBackendAddressPools) : ImmutableList.<IdReference>of());
      }
      public Builder lbInboundNatPools(List<IdReference> loadBalancerInboundNatPools) {
         return loadBalancerInboundNatPools(loadBalancerInboundNatPools != null ? ImmutableList
         .copyOf(loadBalancerInboundNatPools) : ImmutableList.<IdReference>of());
      }

      public abstract Builder applicationGatewayBackendAddressPools(String applicationGatewayBackendAddressPools);
      public abstract VirtualMachineScaleSetIpConfigurationProperties build();

   }
}

