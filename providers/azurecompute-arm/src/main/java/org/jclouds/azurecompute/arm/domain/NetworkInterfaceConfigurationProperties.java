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

import java.util.List;


@AutoValue
public abstract class NetworkInterfaceConfigurationProperties {
    /**
     * The primary of the NetworkInterfaceConfigurationProperties
     */
    @Nullable
    public abstract Boolean primary();

    /**
     * The enableAcceleratedNetworking of the NetworkInterfaceConfigurationProperties
     */
    @Nullable
    public abstract Boolean enableAcceleratedNetworking();

    /**
     * The networkSecurityGroup of the NetworkInterfaceConfigurationProperties
     */
    @Nullable
    public abstract VirtualMachineScaleSetNetworkSecurityGroup networkSecurityGroup();

   /**
    * The dnsSettings of the NetworkInterfaceConfigurationProperties
    */
   @Nullable
   public abstract VirtualMachineScaleSetDNSSettings dnsSettings();

   /**
    * The ipConfigurations of the NetworkInterfaceConfigurationProperties
    */
   public abstract List<VirtualMachineScaleSetIpConfiguration> ipConfigurations();


    @SerializedNames({"primary", "enableAcceleratedNetworking", "networkSecurityGroup", "dnsSettings", "ipConfigurations"})
    public static NetworkInterfaceConfigurationProperties create(final Boolean primary,
                                                               final Boolean enableAcceleratedNetworking,
                                                               final VirtualMachineScaleSetNetworkSecurityGroup networkSecurityGroup,
                                                               final VirtualMachineScaleSetDNSSettings dnsSettings,
                                                               final List<VirtualMachineScaleSetIpConfiguration> ipConfigurations) {

        return new AutoValue_NetworkInterfaceConfigurationProperties(primary, enableAcceleratedNetworking,
                networkSecurityGroup, dnsSettings, ipConfigurations);
    }
}
