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
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class VirtualMachineScaleSetNetworkProfile {

   /**
    * The network interface configurations of the Virtual Machine Scale Set Network Profile
    */
   public abstract List<NetworkInterfaceConfiguration> networkInterfaceConfigurations();


   @SerializedNames({"networkInterfaceConfigurations"})
   public static VirtualMachineScaleSetNetworkProfile create(
      final List<NetworkInterfaceConfiguration> networkInterfaceConfigurations) {
         return builder()
            .networkInterfaceConfigurations(networkInterfaceConfigurations == null ?
               ImmutableList.<NetworkInterfaceConfiguration>of() : ImmutableList.copyOf(networkInterfaceConfigurations) )
            .build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_VirtualMachineScaleSetNetworkProfile.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder networkInterfaceConfigurations(
         List<NetworkInterfaceConfiguration> networkInterfaceConfigurations);

      public abstract VirtualMachineScaleSetNetworkProfile build();

   }
}
