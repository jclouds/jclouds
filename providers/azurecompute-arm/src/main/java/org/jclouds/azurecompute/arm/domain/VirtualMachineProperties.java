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

/**
 * A virtual machine properties for the virtual machine.
 */
@AutoValue
public abstract class VirtualMachineProperties {

   public enum ProvisioningState {
      ACCEPTED,
      CREATING,
      READY,
      CANCELED,
      FAILED,
      DELETED,
      SUCCEEDED,
      RUNNING,
      UPDATING,
      UNRECOGNIZED;

      public static ProvisioningState fromValue(final String text) {
         return (ProvisioningState) GetEnumValue.fromValueOrDefault(text, ProvisioningState.UNRECOGNIZED);
      }
   }

   /**
    * The id of the virtual machine.
    */
   @Nullable
   public abstract String vmId();

   /**
    * The license type of the virtual machine.
    */
   @Nullable
   public abstract String licenseType();

   /**
    * The availability set  of the virtual machine
    */
   @Nullable
   public abstract IdReference availabilitySet();

   /**
    * The hardware Profile of the virtual machine .
    */
   @Nullable
   public abstract HardwareProfile hardwareProfile();

   /**
    * The Storage Profile of the virtual machine .
    */
   @Nullable
   public abstract StorageProfile storageProfile();

   /**
    * The OS Profile of the virtual machine .
    */
   @Nullable
   public abstract OSProfile osProfile();

   /**
    * The network profile of the VM
    */
   @Nullable
   public abstract NetworkProfile networkProfile();

   /**
    * The diagnostics profile of the VM
    */
   @Nullable
   public abstract DiagnosticsProfile diagnosticsProfile();

   /**
    * The provisioning state of the VM
    */
   @Nullable
   public abstract ProvisioningState provisioningState();

   @SerializedNames({"vmId", "licenseType", "availabilitySet", "hardwareProfile", "storageProfile", "osProfile",
           "networkProfile", "diagnosticsProfile", "provisioningState"})
   public static VirtualMachineProperties create(final String vmId,
                                                 final String licenseType,
                                                 final IdReference availabilitySet,
                                                 final HardwareProfile hardwareProfile,
                                                 final StorageProfile storageProfile,
                                                 final OSProfile osProfile,
                                                 final NetworkProfile networkProfile,
                                                 final DiagnosticsProfile diagnosticsProfile,
                                                 final ProvisioningState provisioningState) {
      return builder()
              .vmId(vmId)
              .licenseType(licenseType)
              .availabilitySet(availabilitySet)
              .hardwareProfile(hardwareProfile)
              .storageProfile(storageProfile)
              .osProfile(osProfile)
              .networkProfile(networkProfile)
              .diagnosticsProfile(diagnosticsProfile)
              .provisioningState(provisioningState)
              .build();
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_VirtualMachineProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder vmId(String vmId);

      public abstract Builder licenseType(String licenseType);

      public abstract Builder availabilitySet(IdReference availabilitySet);

      public abstract Builder hardwareProfile(HardwareProfile hardwareProfile);

      public abstract Builder storageProfile(StorageProfile storageProfile);

      public abstract Builder osProfile(OSProfile osProfile);

      public abstract Builder networkProfile(NetworkProfile networkProfile);

      public abstract Builder diagnosticsProfile(DiagnosticsProfile diagnosticsProfile);

      public abstract Builder provisioningState(ProvisioningState provisioningState);

      public abstract VirtualMachineProperties build();
   }
}
