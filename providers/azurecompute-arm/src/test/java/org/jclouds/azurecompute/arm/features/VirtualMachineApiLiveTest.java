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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.ManagedDiskParameters;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.NetworkProfile.NetworkInterface;
import org.jclouds.azurecompute.arm.domain.NetworkProfile.NetworkInterface.NetworkInterfaceProperties;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.OSProfile.WindowsConfiguration.WinRM.Protocol;
import org.jclouds.azurecompute.arm.domain.OSProfile.WindowsConfiguration.WinRM.ProtocolListener;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.Secrets;
import org.jclouds.azurecompute.arm.domain.StorageAccountType;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VHD;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.domain.VaultCertificate;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance.PowerState;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "VirtualMachineApiLiveTest")
public class VirtualMachineApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String subscriptionid;
   private String vmName;
   private String nicName;
   private String virtualNetworkName;
   private String subnetId;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      subscriptionid = getSubscriptionId();

      createTestResourceGroup();

      virtualNetworkName = String.format("vn-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));

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
      VirtualMachine vm = api().createOrUpdate(vmName, LOCATION, getProperties(nicName, null),
            Collections.<String, String> emptyMap(), null);
      assertTrue(!vm.name().isEmpty());
      waitUntilReady(vmName);
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

   @Test(dependsOnMethods = "testGet")
   public void testStart() {
      api().start(vmName);
      assertTrue(stateReached(vmName, PowerState.RUNNING), "start operation did not complete in the configured timeout");
   }

   @Test(dependsOnMethods = "testStart")
   public void testUpdate() {
      VirtualMachine vm = api().get(vmName);
      VirtualMachineProperties oldProperties = vm.properties();
      StorageProfile oldStorageProfile = oldProperties.storageProfile();
      
      DataDisk newDataDisk = DataDisk.builder()
              .name(vmName + "new-data-disk")
              .diskSizeGB("1")
              .lun(1)
              .createOption(DataDisk.DiskCreateOptionTypes.EMPTY)
              .build();
      List<DataDisk> oldDataDisks = oldStorageProfile.dataDisks();
      assertEquals(oldDataDisks.size(), 1);

      ImmutableList<DataDisk> newDataDisks = ImmutableList.<DataDisk> builder().addAll(oldDataDisks).add(newDataDisk).build();
      StorageProfile newStorageProfile = oldStorageProfile.toBuilder().dataDisks(newDataDisks).build();
      VirtualMachineProperties newProperties = oldProperties.toBuilder().storageProfile(newStorageProfile).build();

      VirtualMachine newVm = vm.toBuilder().properties(newProperties).build();
      vm = api().createOrUpdate(vmName, newVm.location(), newVm.properties(), newVm.tags(), newVm.plan());

      assertEquals(vm.properties().storageProfile().dataDisks().size(), oldDataDisks.size() + 1);
   }

   @Test(dependsOnMethods = "testRestart")
   public void testStop() {
      api().stop(vmName);
      assertTrue(stateReached(vmName, PowerState.STOPPED), "stop operation did not complete in the configured timeout");
   }

   @Test(dependsOnMethods = "testUpdate")
   public void testDeallocate() {
      api().deallocate(vmName);
      assertTrue(stateReached(vmName, PowerState.DEALLOCATED),
            "deallocate operation did not complete in the configured timeout");
   }

   @Test(dependsOnMethods = "testDeallocate")
   public void testRestart() {
      api().start(vmName);
      assertTrue(stateReached(vmName, PowerState.RUNNING), "start operation did not complete in the configured timeout");
      api().restart(vmName);
      assertTrue(stateReached(vmName, PowerState.RUNNING), "restart operation did not complete in the configured timeout");
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

   @Test(dependsOnMethods = "testCreate")
   public void testListAll() {
      List<VirtualMachine> list = api.getVirtualMachineApi(null).listAll();
      final VirtualMachine vm = api().get(vmName);

      boolean vmPresent = Iterables.any(list, new Predicate<VirtualMachine>() {
         public boolean apply(VirtualMachine input) {
            return input.name().equals(vm.name());
         }
      });

      assertTrue(vmPresent);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testListByLocation() {
      List<VirtualMachine> list = api.getVirtualMachineApi(null).listByLocation(LOCATION);
      final VirtualMachine vm = api().get(vmName);

      boolean vmPresent = Iterables.any(list, new Predicate<VirtualMachine>() {
         public boolean apply(VirtualMachine input) {
            return input.name().equals(vm.name());
         }
      });
      assertTrue(vmPresent);

      boolean vmsInOtherLocations = Iterables.any(list, new Predicate<VirtualMachine>() {
         public boolean apply(VirtualMachine input) {
            return !input.location().equals(LOCATION);
         }
      });
      assertFalse(vmsInOtherLocations);

   }

   @Test(dependsOnMethods = "testCreate")
   public void testListAvailableSizes() {
      List<VMSize> vmSizes = api().listAvailableSizes(vmName);

      assertNotNull(vmSizes);
      assertFalse(vmSizes.isEmpty());
   }

   @Test(dependsOnMethods = "testRestart")
   public void testGeneralize() throws IllegalStateException {
      api().stop(vmName);
      assertTrue(stateReached(vmName, PowerState.STOPPED), "restart operation did not complete in the configured timeout");
      api().generalize(vmName);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCapture() throws IllegalStateException {
      // Capture is only allowed for Blob based VMs, so let's create one VM for this test
      NetworkInterfaceCard nic = createNetworkInterfaceCard(resourceGroupName, "capture-nic-" + RAND, LOCATION, "ipConfig-" + RAND);
      StorageService storageService = createStorageService(resourceGroupName, "capture" + RAND, LOCATION);
      String blob = storageService.storageServiceProperties().primaryEndpoints().get("blob");
      
      String captureVmName = "capture-" + RAND;
      api().createOrUpdate(captureVmName, LOCATION, getProperties(nic.name(), blob),
            Collections.<String, String> emptyMap(), null);
      waitUntilReady(captureVmName);
      
      api().stop(captureVmName);
      assertTrue(stateReached(captureVmName, PowerState.STOPPED),
            "restart operation did not complete in the configured timeout");
      api().generalize(captureVmName);
      
      URI uri = api().capture(captureVmName, captureVmName, captureVmName);
      assertNotNull(uri);
      
      if (imageAvailablePredicate.apply(uri)) {
         List<ResourceDefinition> definitions = api.getJobApi().captureStatus(uri);
         if (definitions != null) {
            for (ResourceDefinition definition : definitions) {
               Map<String, String> properties = (Map<String, String>) definition.properties();
               Object storageObject = properties.get("storageProfile");
               Map<String, String> properties2 = (Map<String, String>) storageObject;
               Object osDiskObject = properties2.get("osDisk");
               Map<String, String> osProperties = (Map<String, String>) osDiskObject;
               assertNotNull(osProperties.get("name"));
            }
         }
      }
   }

   @Test(dependsOnMethods = "testGeneralize", alwaysRun = true)
   public void testDelete() throws Exception {
      URI uri = api().delete(vmName);
      assertResourceDeleted(uri);
   }

   private VirtualMachineApi api() {
      return api.getVirtualMachineApi(resourceGroupName);
   }

   private VirtualMachineProperties getProperties(String nic, String blob) {

      HardwareProfile hwProf = HardwareProfile.create("Standard_D1_v2");
      ImageReference imgRef = ImageReference.builder().publisher("MicrosoftWindowsServer")
              .offer("WindowsServer").sku("2008-R2-SP1").version("latest").build();
      
      DataDisk.Builder dataDisk = DataDisk.builder().name("data").diskSizeGB("100").lun(0).createOption(DataDisk.DiskCreateOptionTypes.EMPTY);
      
      OSDisk.Builder osDisk = OSDisk.builder()
              .name("os")
              .osType("Windows")
              .caching(DataDisk.CachingTypes.READ_WRITE.toString())
              .createOption("FromImage");
      
      if (blob == null) {
         osDisk.managedDiskParameters(ManagedDiskParameters.create(null, StorageAccountType.STANDARD_LRS.toString()));
      } else {
         osDisk.vhd(VHD.create(blob + "vhds/" + vmName + ".vhd"));
         dataDisk.vhd(VHD.create(blob + "vhds/" + vmName + "data.vhd"));
      }

      StorageProfile storageProfile = StorageProfile.create(imgRef, osDisk.build(), ImmutableList.of(dataDisk.build()));

      List<Secrets> secrets = null;
      OSProfile.WindowsConfiguration.WinRM winRm = null;
      if (!isNullOrEmpty(vaultResourceGroup) && !isNullOrEmpty(vaultName) && !isNullOrEmpty(vaultCertificateUrl)) {
          List<ProtocolListener> listeners = Lists.newArrayList();

          listeners.add(OSProfile.WindowsConfiguration.WinRM.ProtocolListener.create(Protocol.HTTPS, vaultCertificateUrl));
          listeners.add(OSProfile.WindowsConfiguration.WinRM.ProtocolListener.create(Protocol.HTTP, null));

          winRm = OSProfile.WindowsConfiguration.WinRM.create(listeners);
          VaultCertificate vaultCertificate = VaultCertificate.create(vaultCertificateUrl, vaultName);
          secrets = ImmutableList.of(Secrets.create(Secrets.SourceVault.create(String.format("%s/providers/Microsoft.KeyVault/vaults/%s",
                            api.getResourceGroupApi().get(vaultResourceGroup).id(), vaultName)),
                    ImmutableList.of(vaultCertificate)));
      }
      OSProfile.WindowsConfiguration windowsConfig = OSProfile.WindowsConfiguration.create(true, winRm, null, true);
      OSProfile osProfile = OSProfile.create(vmName, "azureuser", "RFe3&432dg", null, null, windowsConfig, secrets);
      NetworkInterface networkInterface =
            NetworkInterface.create("/subscriptions/" + subscriptionid +
                      "/resourceGroups/" + resourceGroupName + "/providers/Microsoft.Network/networkInterfaces/"
                      + nic, NetworkInterfaceProperties.create(true));
      List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
      networkInterfaces.add(networkInterface);
      NetworkProfile networkProfile = NetworkProfile.create(networkInterfaces);
      VirtualMachineProperties properties = VirtualMachineProperties.create(null,
              null, null, hwProf, storageProfile, osProfile, networkProfile, null, VirtualMachineProperties.ProvisioningState.CREATING);
      return properties;
   }

   private NetworkInterfaceCard createNetworkInterfaceCard(final String resourceGroupName, String networkInterfaceCardName, String locationName, String ipConfigurationName) {
      //Create properties object
      final NetworkInterfaceCardProperties networkInterfaceCardProperties = NetworkInterfaceCardProperties
            .builder().ipConfigurations(Arrays.asList(IpConfiguration.create(ipConfigurationName, null, null,
                  IpConfigurationProperties
                        .create(null, null, "Dynamic", IdReference.create(subnetId), null, null, null, Boolean.TRUE))))
            .build();

      final Map<String, String> tags = ImmutableMap.of("jclouds", "livetest");
      return api.getNetworkInterfaceCardApi(resourceGroupName).createOrUpdate(networkInterfaceCardName, locationName, networkInterfaceCardProperties, tags);
   }
   
   private StorageService createStorageService(final String resourceGroupName, final String storageServiceName,
         final String location) {
      URI uri = api.getStorageAccountApi(resourceGroupName).create(storageServiceName, location,
            ImmutableMap.of("property_name", "property_value"),
            ImmutableMap.of("accountType", StorageService.AccountType.Standard_LRS.toString()));
      if (uri != null) {
         assertTrue(uri.toString().contains("api-version"));

         boolean jobDone = retry(new Predicate<URI>() {
            @Override
            public boolean apply(final URI uri) {
               return ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri);
            }
         }, 60 * 1 * 1000 /* 1 minute timeout */).apply(uri);
         assertTrue(jobDone, "create operation did not complete in the configured timeout");
      }
      return api.getStorageAccountApi(resourceGroupName).get(storageServiceName);
   }

   private boolean waitForState(String name, final PowerState state) {
      return api().getInstanceDetails(name).powerState().equals(state);
   }
   
   private void waitUntilReady(String vmName) {
      boolean ready = retry(new Predicate<String>() {
         @Override
         public boolean apply(String name) {
            return !api().get(name).properties().provisioningState().equals(VirtualMachineProperties.ProvisioningState.CREATING);
         }
      }, 60 * 20 * 1000).apply(vmName);
      assertTrue(ready, "createOrUpdate operation did not complete in the configured timeout");

      VirtualMachineProperties.ProvisioningState status = api().get(vmName).properties().provisioningState();
      // Cannot be creating anymore. Should be succeeded or running but not failed.
      assertThat(status).isNotEqualTo(VirtualMachineProperties.ProvisioningState.CREATING);
      assertThat(status).isNotEqualTo(VirtualMachineProperties.ProvisioningState.FAILED);
   }

   private boolean stateReached(String vmName, final PowerState state) {
      return retry(new Predicate<String>() {
         @Override
         public boolean apply(String name) {
            return waitForState(name, state);
         }
      }, 60 * 4 * 1000).apply(vmName);
   }
}
