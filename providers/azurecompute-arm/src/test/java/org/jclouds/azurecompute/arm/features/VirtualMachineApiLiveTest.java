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
package org.jclouds.azurecompute.arm.features;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.DiagnosticsProfile;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VHD;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance.PowerState;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "VirtualMachineApiLiveTest")
public class VirtualMachineApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String subscriptionid;
   private String storageServiceName;
   private String vmName;
   private String nicName;
   private StorageService storageService;
   private String virtualNetworkName;
   private String subnetId;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      subscriptionid = getSubscriptionId();

      createTestResourceGroup();
      virtualNetworkName = String.format("vn-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));

      storageServiceName = String.format("st%s%s", System.getProperty("user.name"), RAND);
      storageService = createStorageService(resourceGroupName, storageServiceName, LOCATION);

      // Subnets belong to a virtual network so that needs to be created first
      assertNotNull(createDefaultVirtualNetwork(resourceGroupName, virtualNetworkName, "10.2.0.0/16", LOCATION));

      //Subnet needs to be up & running before NIC can be created
      String subnetName = String.format("s-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));
      Subnet subnet = createDefaultSubnet(resourceGroupName, subnetName, virtualNetworkName, "10.2.0.0/23");
      assertNotNull(subnet);
      assertNotNull(subnet.id());
      subnetId = subnet.id();

      NetworkInterfaceCard nic = createNetworkInterfaceCard(resourceGroupName, "jc-nic-" + RAND, LOCATION, "ipConfig-" + RAND);
      assertNotNull(nic);
      nicName = nic.name();

      vmName = String.format("%3.24s", System.getProperty("user.name") + RAND + this.getClass().getSimpleName()).toLowerCase().substring(0, 15);
   }

   @Test
   public void testCreate() {
      String blob = storageService.storageServiceProperties().primaryEndpoints().get("blob");

      VirtualMachine vm = api().create(vmName, LOCATION, getProperties(blob, nicName),
            Collections.<String, String> emptyMap(), null);
      assertTrue(!vm.name().isEmpty());

      //Poll until resource is ready to be used
      boolean jobDone = retry(new Predicate<String>() {
         @Override
         public boolean apply(String name) {
            return !api().get(name).properties().provisioningState().equals(VirtualMachineProperties.ProvisioningState.CREATING);
         }
      }, 60 * 20 * 1000).apply(vmName);
      assertTrue(jobDone, "create operation did not complete in the configured timeout");

      VirtualMachineProperties.ProvisioningState status = api().get(vmName).properties().provisioningState();
      // Cannot be creating anymore. Should be succeeded or running but not failed.
      assertThat(status).isNotEqualTo(VirtualMachineProperties.ProvisioningState.CREATING);
      assertThat(status).isNotEqualTo(VirtualMachineProperties.ProvisioningState.FAILED);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      VirtualMachine vm = api().get(vmName);
      assertTrue(!vm.name().isEmpty());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGetInstanceView() {
      VirtualMachineInstance vmi = api().getInstanceDetails(vmName);
      assertTrue(!vmi.statuses().isEmpty());
   }

   @Test(dependsOnMethods = "testStart")
   public void testStop() {
      api().stop(vmName);
      assertTrue(stateReached(PowerState.STOPPED), "stop operation did not complete in the configured timeout");
   }

   @Test(dependsOnMethods = "testGet")
   public void testStart() {
      api().start(vmName);
      assertTrue(stateReached(PowerState.RUNNING), "start operation did not complete in the configured timeout");
   }

   @Test(dependsOnMethods = "testStop")
   public void testRestart() {
      api().start(vmName);
      assertTrue(stateReached(PowerState.RUNNING), "start operation did not complete in the configured timeout");
      api().restart(vmName);
      assertTrue(stateReached(PowerState.RUNNING), "restart operation did not complete in the configured timeout");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      List<VirtualMachine> list = api().list();
      final VirtualMachine vm = api().get(vmName);

      boolean vmPresent = Iterables.any(list, new Predicate<VirtualMachine>() {
         public boolean apply(VirtualMachine input) {
            return input.name().equals(vm.name());
         }
      });

      assertTrue(vmPresent);
   }

   @Test(dependsOnMethods = "testRestart")
   public void testGeneralize() throws IllegalStateException {
      api().stop(vmName);
      assertTrue(stateReached(PowerState.STOPPED), "restart operation did not complete in the configured timeout");
      api().generalize(vmName);
   }

   @SuppressWarnings("unchecked")
   @Test(dependsOnMethods = "testGeneralize")
   public void testCapture() throws IllegalStateException {
      URI uri = api().capture(vmName, vmName, vmName);
      if (uri == null) Assert.fail();
      if (imageAvailablePredicate.apply(uri)) {
         List<ResourceDefinition> definitions = api.getJobApi().captureStatus(uri);
         if (definitions != null) {
            for (ResourceDefinition definition : definitions) {
               Map<String, String> properties = (Map<String, String>) definition.properties();
               Object storageObject = properties.get("storageProfile");
               Map<String, String> properties2 = (Map<String, String>) storageObject;
               Object osDiskObject = properties2.get("osDisk");
               Map<String, String> osProperties = (Map<String, String>) osDiskObject;
               Object dataDisksObject = properties2.get("dataDisks");
               List<Object> dataProperties = (List<Object>) dataDisksObject;
               Map<String, String> datadiskObject = (Map<String, String>) dataProperties.get(0);

               assertNotNull(osProperties.get("name"));
               assertNotNull(datadiskObject.get("name"));
            }
         }
      }
   }

   @Test(dependsOnMethods = "testCapture", alwaysRun = true)
   public void testDelete() throws Exception {
      URI uri = api().delete(vmName);
      assertResourceDeleted(uri);
   }

   private VirtualMachineApi api() {
      return api.getVirtualMachineApi(resourceGroupName);
   }

   private VirtualMachineProperties getProperties(String blob, String nic) {

      HardwareProfile hwProf = HardwareProfile.create("Standard_D1");
      ImageReference imgRef = ImageReference.create("MicrosoftWindowsServerEssentials",
              "WindowsServerEssentials", "WindowsServerEssentials", "latest");
      VHD vhd = VHD.create(blob + "vhds/" + vmName + ".vhd");
      VHD vhd2 = VHD.create(blob + "vhds/" + vmName + "data.vhd");
      DataDisk dataDisk = DataDisk.create(vmName + "data", "100", 0, vhd2, "Empty");
      List<DataDisk> dataDisks = new ArrayList<DataDisk>();
      dataDisks.add(dataDisk);
      OSDisk osDisk = OSDisk.create(null, vmName, vhd, "ReadWrite", "FromImage", null);
      StorageProfile storageProfile = StorageProfile.create(imgRef, osDisk, dataDisks);
      OSProfile.WindowsConfiguration windowsConfig = OSProfile.WindowsConfiguration.create(false, null, null, true,
              null);
      OSProfile osProfile = OSProfile.create(vmName, "azureuser", "RFe3&432dg", null, null, windowsConfig);
      IdReference networkInterface =
              IdReference.create("/subscriptions/" + subscriptionid +
                      "/resourceGroups/" + resourceGroupName + "/providers/Microsoft.Network/networkInterfaces/"
                      + nic);
      List<IdReference> networkInterfaces =
              new ArrayList<IdReference>();
      networkInterfaces.add(networkInterface);
      NetworkProfile networkProfile = NetworkProfile.create(networkInterfaces);
      DiagnosticsProfile.BootDiagnostics bootDiagnostics =
              DiagnosticsProfile.BootDiagnostics.create(true, blob);
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(bootDiagnostics);
      VirtualMachineProperties properties = VirtualMachineProperties.create(null,
              null, null, hwProf, storageProfile, osProfile, networkProfile, diagnosticsProfile, VirtualMachineProperties.ProvisioningState.CREATING);
      return properties;
   }

   protected NetworkInterfaceCard createNetworkInterfaceCard(final String resourceGroupName, String networkInterfaceCardName, String locationName, String ipConfigurationName) {
      //Create properties object
      final NetworkInterfaceCardProperties networkInterfaceCardProperties = NetworkInterfaceCardProperties
            .builder()
            .ipConfigurations(
                  Arrays.asList(IpConfiguration.create(ipConfigurationName, null, null, null, IpConfigurationProperties
                        .create(null, null, "Dynamic", IdReference.create(subnetId), null, null, null)))).build();

      final Map<String, String> tags = ImmutableMap.of("jclouds", "livetest");
      return api.getNetworkInterfaceCardApi(resourceGroupName).createOrUpdate(networkInterfaceCardName, locationName, networkInterfaceCardProperties, tags);
   }

   private boolean waitForState(String name, final PowerState state) {
      return api().getInstanceDetails(name).powerState().equals(state);
   }

   private boolean stateReached(final PowerState state) {
      return retry(new Predicate<String>() {
         @Override
         public boolean apply(String name) {
            return waitForState(name, state);
         }
      }, 60 * 4 * 1000).apply(vmName);
   }
}
