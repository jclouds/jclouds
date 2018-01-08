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
package org.jclouds.azurecompute.arm.compute.functions;

import static com.google.common.collect.Iterables.transform;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractResourceGroup;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.functions.VirtualMachineToStatus.StatusAndBackendStatus;
import org.jclouds.azurecompute.arm.domain.Status;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance.PowerState;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties.ProvisioningState;
import org.jclouds.compute.domain.NodeMetadata;

import com.google.auto.value.AutoValue;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

@Singleton
public class VirtualMachineToStatus implements Function<VirtualMachine, StatusAndBackendStatus> {

   @AutoValue
   public abstract static class StatusAndBackendStatus {
      public abstract NodeMetadata.Status status();

      public abstract String backendStatus();

      public static StatusAndBackendStatus create(NodeMetadata.Status status, String backendStatus) {
         return new AutoValue_VirtualMachineToStatus_StatusAndBackendStatus(status, backendStatus);
      }
   }

   // When using the Deployment API to deploy an ARM template, the deployment
   // goes through stages: Accepted -> Running -> Succeeded.
   // Only when the deployment has SUCCEEDED is the resource deployed using the
   // template actually ready.
   // To get details about the resource(s) deployed via template, one needs to
   // query the various resources after the deployment has SUCCEEDED.
   private static final Function<VirtualMachineProperties.ProvisioningState, NodeMetadata.Status> PROVISIONINGSTATE_TO_NODESTATUS = Functions
         .forMap(
               ImmutableMap.<VirtualMachineProperties.ProvisioningState, NodeMetadata.Status> builder()
                     .put(VirtualMachineProperties.ProvisioningState.ACCEPTED, NodeMetadata.Status.PENDING)
                     .put(VirtualMachineProperties.ProvisioningState.READY, NodeMetadata.Status.PENDING)
                     .put(VirtualMachineProperties.ProvisioningState.CREATING, NodeMetadata.Status.PENDING)
                     .put(VirtualMachineProperties.ProvisioningState.RUNNING, NodeMetadata.Status.PENDING)
                     .put(VirtualMachineProperties.ProvisioningState.UPDATING, NodeMetadata.Status.PENDING)
                     .put(VirtualMachineProperties.ProvisioningState.DELETED, NodeMetadata.Status.TERMINATED)
                     .put(VirtualMachineProperties.ProvisioningState.CANCELED, NodeMetadata.Status.TERMINATED)
                     .put(VirtualMachineProperties.ProvisioningState.FAILED, NodeMetadata.Status.ERROR)
                     .put(VirtualMachineProperties.ProvisioningState.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED)
                     .build(), NodeMetadata.Status.UNRECOGNIZED);

   private static final Function<PowerState, NodeMetadata.Status> POWERSTATE_TO_NODESTATUS = Functions.forMap(
         ImmutableMap.<PowerState, NodeMetadata.Status> builder().put(PowerState.RUNNING, NodeMetadata.Status.RUNNING)
               .put(PowerState.STOPPED, NodeMetadata.Status.SUSPENDED)
               .put(PowerState.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).build(),
         NodeMetadata.Status.UNRECOGNIZED);

   private final AzureComputeApi api;

   @Inject
   VirtualMachineToStatus(AzureComputeApi api) {
      this.api = api;
   }

   @Override
   public StatusAndBackendStatus apply(VirtualMachine virtualMachine) {
      String resourceGroup = extractResourceGroup(virtualMachine.id());
      ProvisioningState provisioningState = virtualMachine.properties().provisioningState();

      NodeMetadata.Status status = PROVISIONINGSTATE_TO_NODESTATUS.apply(provisioningState);
      String backendStatus = provisioningState.name();

      if (ProvisioningState.SUCCEEDED.equals(provisioningState)) {
         // If the provisioning succeeded, we need to query the *real* status of
         // the VM
         VirtualMachineInstance instanceDetails = api.getVirtualMachineApi(resourceGroup).getInstanceDetails(
               virtualMachine.name());
         if (instanceDetails != null && instanceDetails.powerState() != null) {
            status = POWERSTATE_TO_NODESTATUS.apply(instanceDetails.powerState());
            backendStatus = Joiner.on(',').join(transform(instanceDetails.statuses(), new Function<Status, String>() {
               @Override
               public String apply(Status input) {
                  return input.code();
               }
            }));
         } else {
            status = NodeMetadata.Status.PENDING;
         }
      }

      return StatusAndBackendStatus.create(status, backendStatus);
   }
}
