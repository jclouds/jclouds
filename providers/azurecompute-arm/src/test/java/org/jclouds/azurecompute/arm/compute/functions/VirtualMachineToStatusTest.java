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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.Status;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance.PowerState;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties.ProvisioningState;
import org.jclouds.azurecompute.arm.features.VirtualMachineApi;
import org.jclouds.compute.domain.NodeMetadata;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "VirtualMachineToStatusTest")
public class VirtualMachineToStatusTest {

   @Test
   public void testError() {
      assertStatus(ProvisioningState.FAILED, null, NodeMetadata.Status.ERROR);
   }

   @Test
   public void testUnrecognized() {
      assertStatus(ProvisioningState.UNRECOGNIZED, null, NodeMetadata.Status.UNRECOGNIZED);
      assertStatus(ProvisioningState.SUCCEEDED, PowerState.UNKNOWN, NodeMetadata.Status.UNRECOGNIZED);
   }

   @Test
   public void testTerminated() {
      assertStatus(ProvisioningState.DELETED, null, NodeMetadata.Status.TERMINATED);
      assertStatus(ProvisioningState.CANCELED, null, NodeMetadata.Status.TERMINATED);
   }

   @Test
   public void testPending() {
      assertStatus(ProvisioningState.ACCEPTED, null, NodeMetadata.Status.PENDING);
      assertStatus(ProvisioningState.READY, null, NodeMetadata.Status.PENDING);
      assertStatus(ProvisioningState.CREATING, null, NodeMetadata.Status.PENDING);
      assertStatus(ProvisioningState.RUNNING, null, NodeMetadata.Status.PENDING);
      assertStatus(ProvisioningState.UPDATING, null, NodeMetadata.Status.PENDING);

      assertStatus(ProvisioningState.SUCCEEDED, PowerState.STARTING, NodeMetadata.Status.PENDING);
      assertStatus(ProvisioningState.SUCCEEDED, PowerState.STOPPING, NodeMetadata.Status.PENDING);
      assertStatus(ProvisioningState.SUCCEEDED, PowerState.DEALLOCATING, NodeMetadata.Status.PENDING);
   }

   @Test
   public void testSuspended() {
      assertStatus(ProvisioningState.SUCCEEDED, PowerState.STOPPED, NodeMetadata.Status.SUSPENDED);
      assertStatus(ProvisioningState.SUCCEEDED, PowerState.DEALLOCATED, NodeMetadata.Status.SUSPENDED);
   }

   @Test
   public void testRunning() {
      assertStatus(ProvisioningState.SUCCEEDED, PowerState.RUNNING, NodeMetadata.Status.RUNNING);
   }

   @Test
   public void testPendingWhenInstanceNotFound() {
      AzureComputeApi api = createMock(AzureComputeApi.class);
      VirtualMachineApi vmApi = createMock(VirtualMachineApi.class);
      VirtualMachine vm = createMock(VirtualMachine.class);
      VirtualMachineProperties props = createMock(VirtualMachineProperties.class);

      expect(vm.id()).andReturn("/resourceGroups/test/virtualMachines/vm");
      expect(vm.properties()).andReturn(props);
      expect(vm.name()).andReturn("vm");
      expect(props.provisioningState()).andReturn(ProvisioningState.SUCCEEDED);
      expect(api.getVirtualMachineApi("test")).andReturn(vmApi);
      expect(vmApi.getInstanceDetails("vm")).andReturn(null);
      replay(props, vm, vmApi, api);

      assertEquals(new VirtualMachineToStatus(api).apply(vm).status(), NodeMetadata.Status.PENDING);

      verify(props, vm, vmApi, api);
   }

   @Test
   public void testPendingWhenInstanceHasNoPowerState() {
      AzureComputeApi api = createMock(AzureComputeApi.class);
      VirtualMachineApi vmApi = createMock(VirtualMachineApi.class);
      VirtualMachine vm = createMock(VirtualMachine.class);
      VirtualMachineProperties props = createMock(VirtualMachineProperties.class);
      VirtualMachineInstance instance = createMock(VirtualMachineInstance.class);

      expect(vm.id()).andReturn("/resourceGroups/test/virtualMachines/vm");
      expect(vm.properties()).andReturn(props);
      expect(vm.name()).andReturn("vm");
      expect(props.provisioningState()).andReturn(ProvisioningState.SUCCEEDED);
      expect(api.getVirtualMachineApi("test")).andReturn(vmApi);
      expect(vmApi.getInstanceDetails("vm")).andReturn(instance);
      expect(instance.powerState()).andReturn(null);
      replay(props, vm, vmApi, api, instance);

      assertEquals(new VirtualMachineToStatus(api).apply(vm).status(), NodeMetadata.Status.PENDING);

      verify(props, vm, vmApi, api, instance);
   }

   private void assertStatus(ProvisioningState provisioningState, PowerState powerState, NodeMetadata.Status expected) {
      VirtualMachine vm = createMock(VirtualMachine.class);
      VirtualMachineProperties props = createMock(VirtualMachineProperties.class);

      expect(vm.id()).andReturn("/resourceGroups/test/virtualMachines/vm");
      expect(vm.properties()).andReturn(props);
      expect(props.provisioningState()).andReturn(provisioningState);

      AzureComputeApi api = null;
      VirtualMachineApi vmApi = null;
      VirtualMachineInstance instance = null;

      if (powerState != null) {
         api = createMock(AzureComputeApi.class);
         vmApi = createMock(VirtualMachineApi.class);
         instance = createMock(VirtualMachineInstance.class);

         expect(vm.name()).andReturn("vm");

         expect(api.getVirtualMachineApi("test")).andReturn(vmApi);
         expect(vmApi.getInstanceDetails("vm")).andReturn(instance);
         expect(instance.powerState()).andReturn(powerState).times(2);
         expect(instance.statuses()).andReturn(Collections.<Status> emptyList());
         replay(instance, vmApi, api);
      }
      replay(props, vm);

      assertEquals(new VirtualMachineToStatus(api).apply(vm).status(), expected);

      verify(props, vm);
      if (powerState != null) {
         verify(instance, vmApi, api);
      }
   }
}
