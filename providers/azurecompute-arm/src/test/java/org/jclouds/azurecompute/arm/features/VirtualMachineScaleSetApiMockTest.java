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

import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.Extension;
import org.jclouds.azurecompute.arm.domain.ExtensionProfile;
import org.jclouds.azurecompute.arm.domain.ExtensionProfileSettings;
import org.jclouds.azurecompute.arm.domain.ExtensionProperties;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.ManagedDiskParameters;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceConfiguration;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.Secrets;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSet;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetDNSSettings;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetIpConfiguration;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetIpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetNetworkProfile;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetOSProfile;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetProperties;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetPublicIPAddressConfiguration;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetPublicIPAddressProperties;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetSKU;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetUpgradePolicy;
import org.jclouds.azurecompute.arm.domain.VirtualMachineScaleSetVirtualMachineProfile;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.isEmpty;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


@Test(groups = "unit", testName = "VirtualMachineScaleSetAPIMockTest", singleThreaded = true)
public class VirtualMachineScaleSetApiMockTest extends BaseAzureComputeApiMockTest {

   private final String resourcegroup = "myresourcegroup";
   private final String vmssname = "jclouds-vmssname";

   public void testGet() throws InterruptedException {
      server.enqueue(jsonResponse("/virtualmachinescalesetget.json").setResponseCode(200));
      final VirtualMachineScaleSetApi vmssAPI = api.getVirtualMachineScaleSetApi(resourcegroup);
      assertEquals(vmssAPI.get(vmssname).name(), vmssname);
      assertSent(server,
              "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup/" +
                      "providers/Microsoft.Compute"
            + "/VirtualMachineScaleSets/jclouds-vmssname?api-version=2017-03-30");
   }

   public void testGetWhen404() throws InterruptedException {
      server.enqueue(jsonResponse("/virtualmachinescalesetgetwhen404.json").setResponseCode(404));
      final VirtualMachineScaleSetApi vmssAPI = api.getVirtualMachineScaleSetApi(resourcegroup);
      VirtualMachineScaleSet vmss = vmssAPI.get(vmssname + 1);
      assertSent(server,
              "GET",
              "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup/providers/" +
                      "Microsoft.Compute/VirtualMachineScaleSets/" +
                      vmssname + "1?api-version=2017-03-30");
      assertNull(vmss);
   }

   public void testCreateOrUpdate() throws InterruptedException {
      server.enqueue(
              jsonResponse(
                      "/virtualmachinescalesetresponsecreateorupdate.json").setResponseCode(200));
      final VirtualMachineScaleSetApi vmssAPI = api.getVirtualMachineScaleSetApi(resourcegroup);
      VirtualMachineScaleSet vmss = CreateOrUpdateVMSS(vmssAPI);

      assertNotNull(vmss);
      assertSent(server,
              "PUT",
              "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup/providers/" +
                      "Microsoft.Compute"
                      + "/VirtualMachineScaleSets/" + vmssname + "?api-version=2017-03-30",
              "{\n" +
                      "  \"location\": \"eastus\",\n" +
                      "  \"sku\": {\n" +
                      "    \"name\": \"Standard_A1\",\n" +
                      "    \"tier\": \"Standard\",\n" +
                      "    \"capacity\": 10\n" +
                      "  },\n" +
                      "  \"properties\": {\n" +
                      "    \"singlePlacementGroup\": true,\n" +
                      "    \"overProvision\": true,\n" +
                      "    \"upgradePolicy\": {\n" +
                      "      \"mode\": \"Manual\"\n" +
                      "    },\n" +
                      "    \"virtualMachineProfile\": {\n" +
                      "      \"storageProfile\": {\n" +
                      "        \"imageReference\": {\n" +
                      "          \"publisher\": \"Canonical\",\n" +
                      "          \"offer\": \"UbuntuServer\",\n" +
                      "          \"sku\": \"16.04-LTS\",\n" +
                      "          \"version\": \"latest\"\n" +
                      "        },\n" +
                      "        \"osDisk\": {\n" +
                      "          \"osType\": \"Windows\",\n" +
                      "          \"createOption\": \"FromImage\",\n" +
                      "          \"managedDisk\": {\n" +
                      "            \"storageAccountType\": \"Standard_LRS\"\n" +
                      "          }\n" +
                      "        },\n" +
                      "        \"dataDisks\": [{\n" +
                      "          \"diskSizeGB\": \"10\",\n" +
                      "          \"lun\": 1,\n" +
                      "          \"createOption\": \"Unrecognized\",\n" +
                      "          \"caching\": \"None\",\n" +
                      "          \"managedDisk\": {\n" +
                      "            \"storageAccountType\": \"Standard_LRS\"\n" +
                      "          }\n" +
                      "        }\n" +
                      "        ]\n" +
                      "      },\n" +
                      "      \"osProfile\": {\n" +
                      "        \"computerNamePrefix\": \"jclouds-vmssname\",\n" +
                      "        \"adminUsername\": \"admin\",\n" +
                      "        \"adminPassword\": \"password\",\n" +
                      "        \"linuxConfiguration\": {\n" +
                      "          \"disablePasswordAuthentication\": false\n" +
                      "        },\n" +
                      "        \"secrets\": []\n" +
                      "      },\n" +
                      "      \"networkProfile\": {\n" +
                      "        \"networkInterfaceConfigurations\": [{\n" +
                      "          \"name\": \"nicconfig1\",\n" +
                      "          \"properties\": {\n" +
                      "            \"primary\": true,\n" +
                      "            \"enableAcceleratedNetworking\": false,\n" +
                      "            \"dnsSettings\": {\n" +
                      "              \"dnsServers\": [\"8.8.8.8\"]\n" +
                      "            },\n" +
                      "            \"ipConfigurations\": [{\n" +
                      "              \"name\": \"ipconfig1\",\n" +
                      "              \"properties\": {\n" +
                      "                \"publicIPAddressConfiguration\": {\n" +
                      "                  \"name\": \"pub1\",\n" +
                      "                  \"properties\": {\n" +
                      "                    \"idleTimeoutInMinutes\": 15\n" +
                      "                  }\n" +
                      "                },\n" +
                      "                \"subnet\": {\n" +
                      "                  \"name\": \"virtualNetworkName\",\n" +
                      "                  \"id\": \"/subscriptions/xxxxx-xxxx-xxxx-xxxx-xxxxxx/resourceGroups/" +
                      "jcloud-eastus/providers/Microsoft.Network/virtualNetworks/" +
                      "jclouds-eastus-virtualNetworkName/subnets/jclouds-eastus-subnet\",\n" +
                      "                  \"properties\": {}\n" +
                      "                },\n" +
                      "                \"privateIPAddressVersion\": \"IPv4\",\n" +
                      "                \"loadBalancerBackendAddressPools\": [],\n" +
                      "                \"loadBalancerInboundNatPools\": []\n" +
                      "              }\n" +
                      "            }\n" +
                      "            ]\n" +
                      "          }\n" +
                      "        }\n" +
                      "        ]\n" +
                      "      },\n" +
                      "      \"extensionProfile\": {\n" +
                      "        \"extensions\": [{\n" +
                      "          \"name\": \"extensionName\",\n" +
                      "          \"properties\": {\n" +
                      "            \"publisher\": \"Microsoft.compute\",\n" +
                      "            \"type\": \"CustomScriptExtension\",\n" +
                      "            \"typeHandlerVersion\": \"1.1\",\n" +
                      "            \"autoUpgradeMinorVersion\": false,\n" +
                      "            \"settings\": {\n" +
                      "              \"fileUris\": [\"https://mystorage1.blob.core.windows.net/winvmextekfacnt/" +
                      "SampleCmd_1.cmd\"],\n" +
                      "              \"commandToExecute\": \"SampleCmd_1.cmd\"\n" +
                      "            },\n" +
                      "            \"protectedSettings\": {\n" +
                      "              \"StorageAccountKey\": \"jclouds-accountkey\"\n" +
                      "            }\n" +
                      "          }\n" +
                      "        }\n" +
                      "        ]\n" +
                      "      }\n" +
                      "    }\n" +
                      "  }\n" +
                      "}\n"
              );
   }

   private VirtualMachineScaleSet CreateOrUpdateVMSS(VirtualMachineScaleSetApi vmssAPI) {
      return vmssAPI.createOrUpdate(
              vmssname,
              "eastus",
              VirtualMachineScaleSetSKU.create(
                      "Standard_A1",
                      "Standard",
                      10),
              null,
              VirtualMachineScaleSetProperties.create(
                      true,
                      true,
                      VirtualMachineScaleSetUpgradePolicy.create("Manual"),
                      null,
                      VirtualMachineScaleSetVirtualMachineProfile.create(
                              StorageProfile.create(
                                      ImageReference.create(
                                              null,
                                              "Canonical",
                                              "UbuntuServer",
                                              "16.04-LTS",
                                              "latest"),
                                      OSDisk.create(
                                              "Windows",
                                              null,
                                              null,
                                              null,
                                              "FromImage",
                                              null,
                                              ManagedDiskParameters.create(
                                                      null,
                                                      "Standard_LRS"),
                                              null),
                                      Arrays.asList(DataDisk.create(
                                              null,
                                              "10",
                                              1,
                                              null,
                                              null,
                                              "FromImage",
                                              "None",
                                              ManagedDiskParameters.create(
                                                      null,
                                                      "Standard_LRS"),
                                              null))),
                              VirtualMachineScaleSetOSProfile.create(
                                      "jclouds-vmssname",
                                      "admin",
                                      "password",
                                      VirtualMachineScaleSetOSProfile.LinuxConfiguration.create(
                                              false,
                                              null),
                                      null,
                                      new ArrayList<Secrets>()),
                              getNetworkProfile(),
                              getExtensionProfile()

                      )
              ));
   }


   private VirtualMachineScaleSetNetworkProfile getNetworkProfile() {
      List<NetworkProfile.NetworkInterface> networkInterfacesList =
              new ArrayList<NetworkProfile.NetworkInterface>();

      NetworkInterfaceCard nic =
              createNetworkInterfaceCard(
                      "jc-nic-" + 123,
                      "eastus");
      networkInterfacesList.add(NetworkProfile.NetworkInterface.create(
              nic.id(),
              NetworkProfile.NetworkInterface.NetworkInterfaceProperties.create(true)));

      List<NetworkInterfaceConfiguration> networkInterfaceConfigurations =
              new ArrayList<NetworkInterfaceConfiguration>();
      List<VirtualMachineScaleSetIpConfiguration> virtualMachineScaleSetIpConfigurations =
              new ArrayList<VirtualMachineScaleSetIpConfiguration>();


      VirtualMachineScaleSetPublicIPAddressConfiguration publicIPAddressConfiguration =
              VirtualMachineScaleSetPublicIPAddressConfiguration.create(
                      "pub1",
                      VirtualMachineScaleSetPublicIPAddressProperties.create(15));


      VirtualMachineScaleSetIpConfigurationProperties virtualMachineScaleSetIpConfigurationProperties =
              VirtualMachineScaleSetIpConfigurationProperties.create(
                      publicIPAddressConfiguration,
                      createDefaultSubnet(
                              "virtualNetworkName"
                      ),
                      "IPv4",
                      null,
                      null,
                      null);

      VirtualMachineScaleSetIpConfiguration virtualMachineScaleSetIpConfiguration =
              VirtualMachineScaleSetIpConfiguration.create(
                      "ipconfig1",
                      virtualMachineScaleSetIpConfigurationProperties);

      virtualMachineScaleSetIpConfigurations.add(virtualMachineScaleSetIpConfiguration);

      ArrayList<String> dnsList = new ArrayList<String>();
      dnsList.add("8.8.8.8");
      VirtualMachineScaleSetDNSSettings dnsSettings =  VirtualMachineScaleSetDNSSettings.create(dnsList);

      NetworkInterfaceConfigurationProperties networkInterfaceConfigurationProperties =
              NetworkInterfaceConfigurationProperties.create(
                      true,
                      false,
                      null,
                      dnsSettings,
                      virtualMachineScaleSetIpConfigurations);
      NetworkInterfaceConfiguration networkInterfaceConfiguration =
              NetworkInterfaceConfiguration.create(
                      "nicconfig1",
                      networkInterfaceConfigurationProperties);
      networkInterfaceConfigurations.add(networkInterfaceConfiguration);

      return VirtualMachineScaleSetNetworkProfile.create(networkInterfaceConfigurations);
   }


   private NetworkInterfaceCard createNetworkInterfaceCard(
           String networkInterfaceCardName, String locationName) {
      //Create properties object
      NetworkInterfaceCardProperties networkInterfaceCardProperties =
              NetworkInterfaceCardProperties.create(
                      null,
                      null,
                      false,
                      null,
                      IdReference.create(
                              "/subscriptions/xxxxx-xxxx-xxxx-xxxx-xxxxxx/resourceGroups/" +
                                      "jcloud-eastus/providers/" +
                                      "Microsoft.Network/virtualNetworks/" +
                                      "jclouds-eastus-virtualNetworkName/subnets/" +
                                      "jclouds-eastus-subnet")
              );

      Map<String, String> tags = ImmutableMap.of("jclouds", "livetest");
      return NetworkInterfaceCard.create(
              networkInterfaceCardName,
              "",
              null,
              locationName,
              networkInterfaceCardProperties, tags);
   }

   protected Subnet createDefaultSubnet(final String subnetName) {
      Subnet.SubnetProperties properties = Subnet.SubnetProperties.create(
              null,
              null,
              null);
      return Subnet.create(
              subnetName,
              "/subscriptions/xxxxx-xxxx-xxxx-xxxx-xxxxxx/resourceGroups/jcloud-eastus/providers/" +
                      "Microsoft.Network/virtualNetworks/jclouds-eastus-virtualNetworkName/subnets/" +
                      "jclouds-eastus-subnet",
              null,
              properties);
   }

   private ExtensionProfile getExtensionProfile() {
      List<Extension> extensions = new ArrayList<Extension>();

      List<String> uris = new ArrayList<String>();
      uris.add("https://mystorage1.blob.core.windows.net/winvmextekfacnt/SampleCmd_1.cmd");

      Map<String, String> protectedSettings = new HashMap<String, String>();
      protectedSettings.put("StorageAccountKey", "jclouds-accountkey");

      Extension extension = Extension.create(
              "extensionName",
              ExtensionProperties.create(
                      "Microsoft.compute",
                      "CustomScriptExtension",
                      "1.1",
                      false,
                      ExtensionProfileSettings.create(
                              uris,
                              "SampleCmd_1.cmd"),
                      protectedSettings));
      extensions.add(extension);

      return ExtensionProfile.create(extensions);
   }

   public void testList() throws InterruptedException {
      server.enqueue(jsonResponse("/virtualmachinescalesetlist.json").setResponseCode(200));
      final VirtualMachineScaleSetApi vmssAPI = api.getVirtualMachineScaleSetApi(resourcegroup);
      assertEquals(vmssAPI.list().size(), 1);
      assertSent(server,
              "GET",
              "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup/providers/" +
                      "Microsoft.Compute"
              + "/VirtualMachineScaleSets?api-version=2017-03-30");
   }

   public void testListWhen404() throws InterruptedException {
      server.enqueue(
              jsonResponse("/virtualmachinescalesetlistwhen404.json").setResponseCode(404));
      final VirtualMachineScaleSetApi vmssAPI = api.getVirtualMachineScaleSetApi(
              resourcegroup + "1");
      List<VirtualMachineScaleSet> vmssList = vmssAPI.list();
      assertSent(server,
              "GET",
              "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup1/providers/" +
                      "Microsoft.Compute"
              + "/VirtualMachineScaleSets?api-version=2017-03-30");
      assertTrue(isEmpty(vmssList));
   }


   public void testDeleteWhen404() throws InterruptedException {
      server.enqueue(jsonResponse("/virtualmachinescalesetlist.json").setResponseCode(404));
      final VirtualMachineScaleSetApi vmssAPI = api.getVirtualMachineScaleSetApi(resourcegroup);
      assertNull(vmssAPI.delete(vmssname));
      assertSent(server,
              "DELETE",
              "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup/providers/" +
                      "Microsoft.Compute"
              + "/VirtualMachineScaleSets/" + vmssname + "?api-version=2017-03-30");

   }

   public void testDelete() throws InterruptedException {
      server.enqueue(response202WithHeader());
      final VirtualMachineScaleSetApi vmssAPI = api.getVirtualMachineScaleSetApi(resourcegroup);
      assertNotNull(vmssAPI.delete(vmssname));
      assertSent(server,
              "DELETE",
              "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup/providers/" +
                      "Microsoft.Compute"
              + "/VirtualMachineScaleSets/jclouds-vmssname?api-version=2017-03-30");
   }
}
