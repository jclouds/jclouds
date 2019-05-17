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

import static com.google.common.collect.Iterables.isEmpty;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.DiagnosticsProfile;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.ManagedDiskParameters;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.NetworkProfile.NetworkInterface;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.OSProfile.LinuxConfiguration;
import org.jclouds.azurecompute.arm.domain.OSProfile.WindowsConfiguration.AdditionalUnattendContent;
import org.jclouds.azurecompute.arm.domain.OSProfile.WindowsConfiguration.WinRM.Protocol;
import org.jclouds.azurecompute.arm.domain.Plan;
import org.jclouds.azurecompute.arm.domain.Secrets;
import org.jclouds.azurecompute.arm.domain.Secrets.SourceVault;
import org.jclouds.azurecompute.arm.domain.Status;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.VHD;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.domain.VaultCertificate;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "VirtualMachineApiMockTest", singleThreaded = true)
public class VirtualMachineApiMockTest extends BaseAzureComputeApiMockTest {

   public void testGet() throws Exception {
      server.enqueue(jsonResponse("/virtualmachine.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertEquals(vmAPI.get("windowsmachine"),
            getVM(Plan.create("thinkboxsoftware", "deadline-slave-7-2", "deadline7-2")));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine?api-version=2018-06-01");
   }

   public void testGetEmpty() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertNull(vmAPI.get("windowsmachine"));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine?api-version=2018-06-01");
   }

   public void testGetInstanceDetails() throws Exception {
      server.enqueue(jsonResponse("/virtualmachineInstance.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      VirtualMachineInstance actual = vmAPI.getInstanceDetails("windowsmachine");
      VirtualMachineInstance expected = getVMInstance();

      assertEquals(actual.statuses().get(0).code(), expected.statuses().get(0).code());
      assertEquals(actual.statuses().get(0).displayStatus(), expected.statuses().get(0).displayStatus());
      assertEquals(actual.statuses().get(0).level(), expected.statuses().get(0).level());
      // assertEquals(actual.statuses().get(0).time().toString(),
      // expected.statuses().get(0).time().toString());
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine/instanceView?api-version=2018-06-01");
   }

   public void testGetInstanceDetailsEmpty() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertNull(vmAPI.getInstanceDetails("windowsmachine"));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine/instanceView?api-version=2018-06-01");
   }

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/virtualmachinesinresourcegroup.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertEquals(vmAPI.list(), getVMList());
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines?api-version=2018-06-01");
   }

   public void testListEmpty() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertTrue(isEmpty(vmAPI.list()));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines?api-version=2018-06-01");
   }

   public void testListAll() throws Exception {
      server.enqueue(jsonResponse("/virtualmachinesinsubscription.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi(null);
      assertEquals(vmAPI.listAll(), getVMListAll());
      assertSent(server, "GET",
            "/subscriptions/SUBSCRIPTIONID/providers/Microsoft.Compute/virtualMachines?api-version=2018-06-01");
   }

   public void testListByLocation() throws Exception {
      server.enqueue(jsonResponse("/virtualmachinesinlocation.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi(null);
      assertEquals(vmAPI.listByLocation("testlocation"), getVMListByLocation()); // TODO bylocation
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/providers/Microsoft.Compute/locations/testlocation"
            + "/virtualMachines?api-version=2018-06-01");
   }

   public void testListAvailableSizes() throws Exception {
      server.enqueue(jsonResponse("/virtualmachineavailablesizes.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertEquals(vmAPI.listAvailableSizes("windowsmachine"), ImmutableList.of(VMSize.create("Standard_A0", 1,
            1047552, 20480, 768, 1), VMSize.create("Standard_A1", 1,
            1047552, 71680, 1792, 2)));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine/vmSizes?api-version=2018-06-01");
   }

   public void testCreateWithPlan() throws Exception {
      server.enqueue(jsonResponse("/createvirtualmachineresponse.json"));
      Plan plan = Plan.create("thinkboxsoftware", "deadline-slave-7-2", "deadline7-2");
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      VirtualMachine vm = vmAPI
            .createOrUpdate("windowsmachine", "westus", getVMWithManagedDisksProperties(), ImmutableMap.of("foo", "bar"), plan);
      assertEquals(vm, getVM(plan));
      assertSent(
            server,
            "PUT",
            "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
                  + "/virtualMachines/windowsmachine?validating=false&api-version=2018-06-01",
            "{\"location\":\"westus\",\"properties\":"
                  + "{\"vmId\":\"27ee085b-d707-xxxx-yyyy-2370e2eb1cc1\",\"licenseType\":\"Windows_Server\","
                  + "\"availabilitySet\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/availabilitySets/myAVSet\"},"
                  + "\"hardwareProfile\":{\"vmSize\":\"Standard_D1\"},"
                  + "\"storageProfile\":{\"imageReference\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/providers/Microsoft.Compute/locations/westus/publishers/MicrosoftWindowsServerEssentials/artifactype/vmimage/offers/OFFER/skus/OFFER/versions/latest\","
                  + "\"publisher\":\"publisher\",\"offer\":\"OFFER\",\"sku\":\"sku\",\"version\":\"ver\"},"
                  + "\"osDisk\":{\"osType\":\"Windows\",\"name\":\"windowsmachine\","
                  + "\"caching\":\"ReadWrite\",\"createOption\":\"FromImage\","
                  + "\"managedDisk\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/disks/osDisk\",\"storageAccountType\":\"Standard_LRS\"}},"
                  + "\"dataDisks\":[{\"name\":\"mydatadisk1\",\"diskSizeGB\":\"1\",\"lun\":0,\"createOption\":\"Empty\",\"caching\":\"ReadWrite\",\"managedDisk\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/disks/osDisk\",\"storageAccountType\":\"Standard_LRS\"}}]},"
                  + "\"osProfile\":{\"computerName\":\"windowsmachine\",\"adminUsername\":\"azureuser\",\"adminPassword\":\"password\",\"customData\":\"\",\"windowsConfiguration\":{\"provisionVMAgent\":false,"
                  + "\"winRM\":{\"listeners\":[{\"protocol\":\"https\",\"certificateUrl\":\"url-to-certificate\"}]},\"additionalUnattendContent\":[{\"passName\":\"oobesystem\",\"componentName\":\"Microsoft-Windows-Shell-Setup\",\"settingName\":\"FirstLogonCommands\",\"content\":\"<XML unattend content>\"}],"
                  + "\"enableAutomaticUpdates\":true},"
                  + "\"secrets\":[{\"sourceVault\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup1/providers/Microsoft.KeyVault/vaults/myvault1\"},\"vaultCertificates\":[{\"certificateUrl\":\"https://myvault1.vault.azure.net/secrets/SECRETNAME/SECRETVERSION\",\"certificateStore\":\"CERTIFICATESTORENAME\"}]}]},"
                  + "\"networkProfile\":{\"networkInterfaces\":[{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Network/networkInterfaces/windowsmachine167\"}]},"
                  + "\"diagnosticsProfile\":{\"bootDiagnostics\":{\"enabled\":true,\"storageUri\":\"https://groupname2760.blob.core.windows.net/\"}},\"provisioningState\":\"CREATING\"},"
                  + "\"tags\":{\"foo\":\"bar\"},"
                  + "\"plan\":{\"name\":\"deadline-slave-7-2\",\"publisher\":\"thinkboxsoftware\",\"product\":\"deadline7-2\"}}");
   }

   // See https://docs.microsoft.com/en-us/rest/api/compute/virtualmachines/virtualmachines-create-or-update
   // for where part of the example json response comes from. Unfortunately examples in the microsoft docs
   // are not valid json (e.g. missing commas, illegal quotes). Therefore this example merges the original 
   // real-world example (presumably taken from the jclouds wire log), and snippets from the microsoft docs.
   public void testCreate() throws Exception {
      server.enqueue(jsonResponse("/createvirtualmachineresponse.json"));

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      VirtualMachine vm = vmAPI.createOrUpdate("windowsmachine", "westus", getVMWithManagedDisksProperties(), ImmutableMap.of("foo", "bar"), null);
      assertEquals(vm, getVM());
      assertSent(
            server,
            "PUT",
            "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
                  + "/virtualMachines/windowsmachine?validating=false&api-version=2018-06-01",
            "{\"location\":\"westus\",\"properties\":"
                  + "{\"vmId\":\"27ee085b-d707-xxxx-yyyy-2370e2eb1cc1\",\"licenseType\":\"Windows_Server\","
                  + "\"availabilitySet\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/availabilitySets/myAVSet\"},"
                  + "\"hardwareProfile\":{\"vmSize\":\"Standard_D1\"},"
                  + "\"storageProfile\":{\"imageReference\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/providers/Microsoft.Compute/locations/westus/publishers/MicrosoftWindowsServerEssentials/artifactype/vmimage/offers/OFFER/skus/OFFER/versions/latest\","
                  + "\"publisher\":\"publisher\",\"offer\":\"OFFER\",\"sku\":\"sku\",\"version\":\"ver\"},"
                  + "\"osDisk\":{\"osType\":\"Windows\",\"name\":\"windowsmachine\","
                  + "\"caching\":\"ReadWrite\",\"createOption\":\"FromImage\","
                  + "\"managedDisk\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/disks/osDisk\",\"storageAccountType\":\"Standard_LRS\"}},"
                  + "\"dataDisks\":[{\"name\":\"mydatadisk1\",\"diskSizeGB\":\"1\",\"lun\":0,\"createOption\":\"Empty\",\"caching\":\"ReadWrite\",\"managedDisk\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/disks/osDisk\",\"storageAccountType\":\"Standard_LRS\"}}]},"
                  + "\"osProfile\":{\"computerName\":\"windowsmachine\",\"adminUsername\":\"azureuser\",\"adminPassword\":\"password\",\"customData\":\"\",\"windowsConfiguration\":{\"provisionVMAgent\":false,"
                  + "\"winRM\":{\"listeners\":[{\"protocol\":\"https\",\"certificateUrl\":\"url-to-certificate\"}]},\"additionalUnattendContent\":[{\"passName\":\"oobesystem\",\"componentName\":\"Microsoft-Windows-Shell-Setup\",\"settingName\":\"FirstLogonCommands\",\"content\":\"<XML unattend content>\"}],"
                  + "\"enableAutomaticUpdates\":true},"
                  + "\"secrets\":[{\"sourceVault\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup1/providers/Microsoft.KeyVault/vaults/myvault1\"},\"vaultCertificates\":[{\"certificateUrl\":\"https://myvault1.vault.azure.net/secrets/SECRETNAME/SECRETVERSION\",\"certificateStore\":\"CERTIFICATESTORENAME\"}]}]},"
                  + "\"networkProfile\":{\"networkInterfaces\":[{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Network/networkInterfaces/windowsmachine167\"}]},"
                  + "\"diagnosticsProfile\":{\"bootDiagnostics\":{\"enabled\":true,\"storageUri\":\"https://groupname2760.blob.core.windows.net/\"}},\"provisioningState\":\"CREATING\"},"
                  + "\"tags\":{\"foo\":\"bar\"}}");
   }

   public void testDeleteReturns404() throws Exception {
      server.enqueue(response404());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      URI uri = vmAPI.delete("windowsmachine");

      assertEquals(server.getRequestCount(), 1);
      assertNull(uri);

      assertSent(server, "DELETE", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine?api-version=2018-06-01");
   }

   public void testDelete() throws Exception {
      server.enqueue(response202WithHeader());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      URI uri = vmAPI.delete("windowsmachine");

      assertEquals(server.getRequestCount(), 1);
      assertNotNull(uri);

      assertSent(server, "DELETE", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine?api-version=2018-06-01");
   }

   public void testStart() throws Exception {
      server.enqueue(response202WithHeader());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      vmAPI.start("windowsmachine");

      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine/start?api-version=2018-06-01");
   }

   public void testRestart() throws Exception {
      server.enqueue(response202WithHeader());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      vmAPI.restart("windowsmachine");

      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine/restart?api-version=2018-06-01");
   }

   public void testStop() throws Exception {
      server.enqueue(response202WithHeader());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      vmAPI.stop("windowsmachine");

      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine/powerOff?api-version=2018-06-01");
   }

   public void testDeallocate() throws Exception {
      server.enqueue(response202WithHeader());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      vmAPI.deallocate("windowsmachine");

      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/windowsmachine/deallocate?api-version=2018-06-01");
   }

   public void testGeneralize() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      vmAPI.generalize("vm"); // IllegalStateException if failed
      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/vm/generalize?api-version=2018-06-01");
   }

   public void testCapture() throws Exception {
      server.enqueue(response202WithHeader());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      URI uri = vmAPI.capture("vm", "prefix", "container");
      assertNotNull(uri);
      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/vm/capture?api-version=2018-06-01",
            "{\"vhdPrefix\":\"prefix\",\"destinationContainerName\":\"container\",\"overwriteVhds\":\"true\"}");
   }

   public void testCapture404() throws Exception {
      server.enqueue(response404());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      URI uri = vmAPI.capture("vm", "prefix", "container");
      assertNull(uri);
      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute"
            + "/virtualMachines/vm/capture?api-version=2018-06-01",
            "{\"vhdPrefix\":\"prefix\",\"destinationContainerName\":\"container\",\"overwriteVhds\":\"true\"}");
   }

   private VirtualMachineProperties getVMWithBlobDisksProperties() {
      String licenseType = "Windows_Server";
      IdReference availabilitySet = IdReference.create("/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/availabilitySets/myAVSet");
      HardwareProfile hwProf = HardwareProfile.create("Standard_D1");
      ImageReference imgRef = ImageReference.builder().publisher("publisher").offer("OFFER").sku("sku").version("ver")
            .customImageId("/subscriptions/SUBSCRIPTIONID/providers/Microsoft.Compute/locations/westus/publishers/MicrosoftWindowsServerEssentials/artifactype/vmimage/offers/OFFER/skus/OFFER/versions/latest")
            .build();
      VHD vhd = VHD.create("https://groupname2760.blob.core.windows.net/vhds/windowsmachine201624102936.vhd");
      List<DataDisk> dataDisks = ImmutableList.of(
            DataDisk.create("mydatadisk1", "1", 0, VHD.create("http://mystorage1.blob.core.windows.net/vhds/mydatadisk1.vhd"),
                  null, "Empty", null, null, null));
      ManagedDiskParameters managedDiskParameters = ManagedDiskParameters.create("/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/disks/osDisk",
            "Standard_LRS");
      OSDisk osDisk = OSDisk.create("Windows", "windowsmachine", vhd, "ReadWrite", "FromImage", null, managedDiskParameters, null);
      StorageProfile storageProfile = StorageProfile.create(imgRef, osDisk, dataDisks);
      LinuxConfiguration linuxConfig = null;
      OSProfile.WindowsConfiguration.WinRM winrm = OSProfile.WindowsConfiguration.WinRM.create(
            ImmutableList.of(
                  OSProfile.WindowsConfiguration.WinRM.ProtocolListener.create(Protocol.HTTPS, "url-to-certificate")));
      List<AdditionalUnattendContent> additionalUnattendContent = ImmutableList.of(
            AdditionalUnattendContent.create("oobesystem", "Microsoft-Windows-Shell-Setup", "FirstLogonCommands", "<XML unattend content>"));
      OSProfile.WindowsConfiguration windowsConfig = OSProfile.WindowsConfiguration.create(false, winrm, additionalUnattendContent, true);
      List<Secrets> secrets =  ImmutableList.of(
            Secrets.create(SourceVault.create("/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup1/providers/Microsoft.KeyVault/vaults/myvault1"),
                  ImmutableList.of(VaultCertificate.create("https://myvault1.vault.azure.net/secrets/SECRETNAME/SECRETVERSION", "CERTIFICATESTORENAME"))));
      OSProfile osProfile = OSProfile.create("windowsmachine", "azureuser", "password", "", linuxConfig, windowsConfig, secrets);
      NetworkInterface networkInterface = NetworkInterface.create("/subscriptions/SUBSCRIPTIONID"
            + "/resourceGroups/groupname/providers/Microsoft.Network/networkInterfaces/" + "windowsmachine167", null);
      List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
      networkInterfaces.add(networkInterface);
      NetworkProfile networkProfile = NetworkProfile.create(networkInterfaces);
      DiagnosticsProfile.BootDiagnostics bootDiagnostics = DiagnosticsProfile.BootDiagnostics.create(true,
            "https://groupname2760.blob.core.windows.net/");
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(bootDiagnostics);
      VirtualMachineProperties properties = VirtualMachineProperties.create("27ee085b-d707-xxxx-yyyy-2370e2eb1cc1",
            licenseType, availabilitySet, hwProf, storageProfile, osProfile, networkProfile, diagnosticsProfile,
            VirtualMachineProperties.ProvisioningState.CREATING);
      return properties;
   }

   private VirtualMachineProperties getVMWithManagedDisksProperties() {
      String licenseType = "Windows_Server";
      IdReference availabilitySet = IdReference.create("/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/availabilitySets/myAVSet");
      HardwareProfile hwProf = HardwareProfile.create("Standard_D1");
      ImageReference imgRef = ImageReference.builder().publisher("publisher").offer("OFFER").sku("sku").version("ver")
            .customImageId("/subscriptions/SUBSCRIPTIONID/providers/Microsoft.Compute/locations/westus/publishers/MicrosoftWindowsServerEssentials/artifactype/vmimage/offers/OFFER/skus/OFFER/versions/latest")
            .build();
      ManagedDiskParameters managedDiskParameters = ManagedDiskParameters.create("/subscriptions/SUBSCRIPTIONID/resourceGroups/myResourceGroup/providers/Microsoft.Compute/disks/osDisk",
            "Standard_LRS");
      List<DataDisk> dataDisks = ImmutableList.of(
              DataDisk.builder().name("mydatadisk1").diskSizeGB("1").lun(0).managedDiskParameters(managedDiskParameters).createOption(DataDisk.DiskCreateOptionTypes.EMPTY).caching(DataDisk.CachingTypes.READ_WRITE).build());
      OSDisk osDisk = OSDisk.builder().osType("Windows").name("windowsmachine").caching("ReadWrite").createOption("FromImage").managedDiskParameters(managedDiskParameters).build();
      StorageProfile storageProfile = StorageProfile.create(imgRef, osDisk, dataDisks);
      LinuxConfiguration linuxConfig = null;
      OSProfile.WindowsConfiguration.WinRM winrm = OSProfile.WindowsConfiguration.WinRM.create(
            ImmutableList.of(
                  OSProfile.WindowsConfiguration.WinRM.ProtocolListener.create(Protocol.HTTPS, "url-to-certificate")));
      List<AdditionalUnattendContent> additionalUnattendContent = ImmutableList.of(
            AdditionalUnattendContent.create("oobesystem", "Microsoft-Windows-Shell-Setup", "FirstLogonCommands", "<XML unattend content>"));
      OSProfile.WindowsConfiguration windowsConfig = OSProfile.WindowsConfiguration.create(false, winrm, additionalUnattendContent, true);
      List<Secrets> secrets =  ImmutableList.of(
            Secrets.create(SourceVault.create("/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup1/providers/Microsoft.KeyVault/vaults/myvault1"), 
                  ImmutableList.of(VaultCertificate.create("https://myvault1.vault.azure.net/secrets/SECRETNAME/SECRETVERSION", "CERTIFICATESTORENAME"))));
      OSProfile osProfile = OSProfile.create("windowsmachine", "azureuser", "password", "", linuxConfig, windowsConfig, secrets);
      NetworkInterface networkInterface = NetworkInterface.create("/subscriptions/SUBSCRIPTIONID"
            + "/resourceGroups/groupname/providers/Microsoft.Network/networkInterfaces/" + "windowsmachine167", null);
      List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
      networkInterfaces.add(networkInterface);
      NetworkProfile networkProfile = NetworkProfile.create(networkInterfaces);
      DiagnosticsProfile.BootDiagnostics bootDiagnostics = DiagnosticsProfile.BootDiagnostics.create(true,
            "https://groupname2760.blob.core.windows.net/");
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(bootDiagnostics);
      VirtualMachineProperties properties = VirtualMachineProperties.create("27ee085b-d707-xxxx-yyyy-2370e2eb1cc1",
            licenseType, availabilitySet, hwProf, storageProfile, osProfile, networkProfile, diagnosticsProfile,
            VirtualMachineProperties.ProvisioningState.CREATING);
      return properties;
   }

   private VirtualMachine getVM() {
      VirtualMachineProperties properties = getVMWithManagedDisksProperties();
      VirtualMachine machine = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
            + "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine", "windowsmachine",
            "Microsoft.Compute/virtualMachines", "westus", ImmutableMap.of("foo", "bar"), properties,
            Plan.create("thinkboxsoftware", "deadline-slave-7-2", "deadline7-2"));
      return machine;
   }
   
   private VirtualMachine getVM(Plan plan) {
      VirtualMachineProperties properties = getVMWithManagedDisksProperties();
      VirtualMachine machine = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
            + "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine", "windowsmachine",
            "Microsoft.Compute/virtualMachines", "westus", ImmutableMap.of("foo", "bar"), properties, plan);
      return machine;
   }

   private VirtualMachineInstance getVMInstance() {
      List<Status> statuses = new ArrayList<Status>();
      String testDate = "Wed May 04 01:38:52 PDT 2016";
      DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
      Date date = null;
      try {
         date = formatter.parse(testDate);
      } catch (Exception e) {
         e.printStackTrace();
      }
      Status vmStatus = Status.create(
            "ProvisioningState/succeeded", "Info", "Provisioning succeeded", null, date);
      statuses.add(vmStatus);
      Status vmStatus1 = Status.create(
            "PowerState/running", "Info", "VM running", null, null);
      statuses.add(vmStatus1);

      VirtualMachineInstance machineInstance = VirtualMachineInstance
            .create(null, null, ImmutableList.copyOf(statuses));
      return machineInstance;
   }

   private List<VirtualMachine> getVMList() {
      List<VirtualMachine> list = new ArrayList<VirtualMachine>();
      VirtualMachineProperties propertiesWithManagedDisks = getVMWithManagedDisksProperties();
      VirtualMachine machineWithManagedDisks = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
            + "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine", "windowsmachine",
            "Microsoft.Compute/virtualMachines", "westus", null, propertiesWithManagedDisks, null);
      list.add(machineWithManagedDisks);
      VirtualMachineProperties propertiesWithBlobDisks = getVMWithBlobDisksProperties();
      VirtualMachine machineWithBlobDisks = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
                      + "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine", "windowsmachine",
              "Microsoft.Compute/virtualMachines", "westus", null, propertiesWithBlobDisks, null);
      list.add(machineWithBlobDisks);
      return list;
   }

   private List<VirtualMachine> getVMListAll() {
      List<VirtualMachine> list = new ArrayList<VirtualMachine>();
      VirtualMachineProperties propertiesWithManagedDisks = getVMWithManagedDisksProperties();
      VirtualMachine machineWithManagedDisks = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
                  + "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine",
            "windowsmachine",
            "Microsoft.Compute/virtualMachines", "westus", null, propertiesWithManagedDisks, null);
      list.add(machineWithManagedDisks);
      VirtualMachineProperties propertiesWithBlobDisks = getVMWithBlobDisksProperties();
      VirtualMachine machineWithBlobDisks = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
                  + "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine",
            "windowsmachine",
            "Microsoft.Compute/virtualMachines", "westus", null, propertiesWithBlobDisks, null);
      list.add(machineWithBlobDisks);
      VirtualMachine machineInDifferentResourceGroup = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
                  + "resourceGroups/otherresourcegroup/providers/Microsoft.Compute/virtualMachines/windowsmachine",
            "windowsmachine", "Microsoft.Compute/virtualMachines", "westus", null, propertiesWithBlobDisks, null);
      list.add(machineInDifferentResourceGroup);
      VirtualMachine machineInDifferentLocation = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
                  + "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine",
            "windowsmachine",
            "Microsoft.Compute/virtualMachines", "eastus", null, propertiesWithBlobDisks, null);
      list.add(machineInDifferentLocation);
      return list;
   }

   private List<VirtualMachine> getVMListByLocation() {
      List<VirtualMachine> list = new ArrayList<VirtualMachine>();
      VirtualMachineProperties propertiesWithManagedDisks = getVMWithManagedDisksProperties();
      VirtualMachine machineWithManagedDisks = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
                  + "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine",
            "windowsmachine",
            "Microsoft.Compute/virtualMachines", "westus", null, propertiesWithManagedDisks, null);
      list.add(machineWithManagedDisks);
      VirtualMachineProperties propertiesWithBlobDisks = getVMWithBlobDisksProperties();
      VirtualMachine machineWithBlobDisks = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
                  + "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine",
            "windowsmachine",
            "Microsoft.Compute/virtualMachines", "westus", null, propertiesWithBlobDisks, null);
      list.add(machineWithBlobDisks);
      VirtualMachine machineInDifferentResourceGroup = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + ""
                  + "resourceGroups/otherresourcegroup/providers/Microsoft.Compute/virtualMachines/windowsmachine",
            "windowsmachine", "Microsoft.Compute/virtualMachines", "westus", null, propertiesWithBlobDisks, null);
      list.add(machineInDifferentResourceGroup);
      return list;
   }

}
