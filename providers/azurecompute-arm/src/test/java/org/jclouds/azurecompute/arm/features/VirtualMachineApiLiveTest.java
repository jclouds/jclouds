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

import com.google.common.base.Predicate;
import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.DiagnosticsProfile;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.VHD;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.util.Predicates2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "VirtualMachineApiLiveTest")
public class VirtualMachineApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String subscriptionid = getSubscriptionId();
   private String vmName = null;
   private String nicName = null;

   @BeforeClass
   public void Setup() {
      NetworkInterfaceCard nic = getOrCreateNetworkInterfaceCard(NETWORKINTERFACECARD_NAME);
      assertNotNull(nic);
      nicName = nic.name();
   }

   private String getName() {
      if (vmName == null) {
         vmName = String.format("%3.24s",
                 System.getProperty("user.name") + RAND + this.getClass().getSimpleName()).toLowerCase().substring(0, 15);
      }
      return vmName;
   }

   @Test
   public void testCreate(){

      StorageAccountApi storageApi = api.getStorageAccountApi(getResourceGroupName());
      StorageService storageAccount = storageApi.get(getStorageServiceName());
      String blob = storageAccount.storageServiceProperties().primaryEndpoints().get("blob");

      VirtualMachine vm = api().create(getName(), LOCATION, getProperties(blob, nicName));
      assertTrue(!vm.name().isEmpty());

      //Poll until resource is ready to be used
      boolean jobDone = Predicates2.retry(new Predicate<String>() {
         @Override public boolean apply(String name) {
            return !api().get(name).properties().provisioningState().equals("Creating");
         }
      }, 60 * 20 * 1000).apply(getName());
      assertTrue(jobDone, "create operation did not complete in the configured timeout");

      String status = api().get(vmName).properties().provisioningState();
      // Cannot be creating anymore. Should be succeeded or running but not failed.
      assertTrue(!status.equals("Creating"));
      assertTrue(!status.equals("Failed"));
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      VirtualMachine vm = api().get(getName());
      assertTrue(!vm.name().isEmpty());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGetInstanceView() {
      VirtualMachineInstance vmi = api().getInstanceDetails(getName());
      assertTrue(!vmi.statuses().isEmpty());
   }

   @Test(dependsOnMethods = "testStart")
   public void testStop() {
      api().stop(getName());
      //Poll until resource is ready to be used
      boolean jobDone = Predicates2.retry(new Predicate<String>() {
         @Override public boolean apply(String name) {
            String status = "";
            List<VirtualMachineInstance.VirtualMachineStatus> statuses = api().getInstanceDetails(name).statuses();
            for (int c = 0; c < statuses.size(); c++) {
               if (statuses.get(c).code().substring(0, 10).equals("PowerState")) {
                  status = statuses.get(c).displayStatus();
                  break;
               }
            }
            return status.equals("VM stopped");
         }
      }, 60 * 4 * 1000).apply(getName());
      assertTrue(jobDone, "stop operation did not complete in the configured timeout");

   }

   @Test(dependsOnMethods = "testGet")
   public void testStart() {
      api().start(getName());

      //Poll until resource is ready to be used
      boolean jobDone = Predicates2.retry(new Predicate<String>() {
         @Override public boolean apply(String name) {
            String status = "";
            List<VirtualMachineInstance.VirtualMachineStatus> statuses = api().getInstanceDetails(name).statuses();
            for (int c = 0; c < statuses.size(); c++) {
               if (statuses.get(c).code().substring(0, 10).equals("PowerState")) {
                  status = statuses.get(c).displayStatus();
                  break;
               }
            }
            return status.equals("VM running");
         }
      }, 60 * 4 * 1000).apply(getName());
      assertTrue(jobDone, "start operation did not complete in the configured timeout");

   }

   @Test(dependsOnMethods = "testStop")
   public void testRestart() {
      api().start(getName());

      //Poll until resource is ready to be used
      boolean jobDone = Predicates2.retry(new Predicate<String>() {
         @Override public boolean apply(String name) {
            String status = "";
            List<VirtualMachineInstance.VirtualMachineStatus> statuses = api().getInstanceDetails(name).statuses();
            for (int c = 0; c < statuses.size(); c++) {
               if (statuses.get(c).code().substring(0, 10).equals("PowerState")) {
                  status = statuses.get(c).displayStatus();
                  break;
               }
            }
            return status.equals("VM running");
         }
      }, 60 * 4 * 1000).apply(getName());
      assertTrue(jobDone, "start operation did not complete in the configured timeout");

      api().restart(getName());

      //Poll until resource is ready to be used
      jobDone = Predicates2.retry(new Predicate<String>() {
         @Override public boolean apply(String name) {
            String status = "";
            List<VirtualMachineInstance.VirtualMachineStatus> statuses = api().getInstanceDetails(name).statuses();
            for (int c = 0; c < statuses.size(); c++) {
               if (statuses.get(c).code().substring(0, 10).equals("PowerState")) {
                  status = statuses.get(c).displayStatus();
                  break;
               }
            }
            return status.equals("VM running");
         }
      }, 60 * 4 * 1000).apply(getName());
      assertTrue(jobDone, "restart operation did not complete in the configured timeout");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      List<VirtualMachine> list = api().list();
      VirtualMachine vm = api().get(getName());
      assertTrue(list.contains(vm));
   }

   @Test(dependsOnMethods = {"testRestart", "testList", "testGet"},  alwaysRun = true)
   public void testDelete() throws Exception {
      URI uri = api().delete(getName());

      if (uri != null) {
         assertTrue(uri.toString().contains("api-version"));

         boolean jobDone = Predicates2.retry(new Predicate<URI>() {
            @Override
            public boolean apply(URI uri) {
               return ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri);
            }
         }, 60 * 8 * 1000 /* 2 minutes timeout */).apply(uri);
         assertTrue(jobDone, "delete operation did not complete in the configured timeout");
      }
   }

   private VirtualMachineApi api() {
      return api.getVirtualMachineApi(getResourceGroupName());
   }

   private VirtualMachineProperties getProperties(String blob, String nic) {

      HardwareProfile hwProf = HardwareProfile.create("Standard_D1");
      ImageReference imgRef = ImageReference.create("MicrosoftWindowsServerEssentials",
              "WindowsServerEssentials", "WindowsServerEssentials", "latest");
      VHD vhd = VHD.create(blob + "vhds/" + getName() + ".vhd");
      VHD vhd2 = VHD.create(blob + "vhds/" + getName() + "data.vhd");
      DataDisk dataDisk = DataDisk.create(getName() + "data", "100", 0, vhd2, "Empty");
      OSDisk osDisk = OSDisk.create(null, getName(), vhd, "ReadWrite", "FromImage");
      StorageProfile storageProfile = StorageProfile.create(imgRef, osDisk, null);
      OSProfile.WindowsConfiguration windowsConfig = OSProfile.WindowsConfiguration.create(false, null, null, true,
              null);
      OSProfile osProfile = OSProfile.create(getName(), "azureuser", "RFe3&432dg", null, null, windowsConfig);
      IdReference networkInterface =
              IdReference.create("/subscriptions/" + subscriptionid +
                      "/resourceGroups/" + getResourceGroupName() + "/providers/Microsoft.Network/networkInterfaces/"
                      + nic);
      List<IdReference> networkInterfaces =
              new ArrayList<IdReference>();
      networkInterfaces.add(networkInterface);
      NetworkProfile networkProfile = NetworkProfile.create(networkInterfaces);
      DiagnosticsProfile.BootDiagnostics bootDiagnostics =
              DiagnosticsProfile.BootDiagnostics.create(true, blob);
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(bootDiagnostics);
      VirtualMachineProperties properties = VirtualMachineProperties.create(null,
              null, null, hwProf, storageProfile, osProfile, networkProfile, diagnosticsProfile, "Creating");
      return properties;
   }
}
