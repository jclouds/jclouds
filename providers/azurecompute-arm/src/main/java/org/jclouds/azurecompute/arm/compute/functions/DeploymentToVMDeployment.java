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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.VMDeployment;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;

import com.google.common.base.Function;

/**
 * Converts an Deployment into a VMDeployment.
 */
@Singleton
public class DeploymentToVMDeployment implements Function<Deployment, VMDeployment> {

   private final AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants;

   private final AzureComputeApi api;

   @Inject
   DeploymentToVMDeployment(AzureComputeApi api, final AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants) {
      this.api = api;
      this.azureComputeConstants = azureComputeConstants;
   }

   @Override
   public VMDeployment apply(final Deployment deployment) {
      String id = deployment.id();
      List<PublicIPAddress> ipAddressList = getIPAddresses(deployment);
      List<NetworkInterfaceCard> networkInterfaceCards = getNetworkInterfaceCards(deployment);
      VirtualMachine vm = api.getVirtualMachineApi(azureComputeConstants.azureResourceGroup()).get(id);
      VirtualMachineInstance vmInstanceDetails = api.getVirtualMachineApi(azureComputeConstants.azureResourceGroup()).getInstanceDetails(id);
      Map<String, String> userMetaData = null;
      Iterable<String> tags = null;
      if (vm != null && vm.tags() != null) {
         userMetaData = vm.tags();
         String tagString = userMetaData.get("tags");
         tags = Arrays.asList(tagString.split(","));
      }
      return VMDeployment.create(deployment, ipAddressList, vmInstanceDetails, vm, networkInterfaceCards, userMetaData, tags);
   }

   private List<PublicIPAddress> getIPAddresses(Deployment deployment) {
      List<PublicIPAddress> list = new ArrayList<PublicIPAddress>();
      String resourceGroup = getResourceGroupFromId(deployment.id());

      if (deployment.properties() != null && deployment.properties().dependencies() != null) {
         List<Deployment.Dependency> dependencies = deployment.properties().dependencies();
         for (int d = 0; d < dependencies.size(); d++) {
            if (dependencies.get(d).resourceType().equals("Microsoft.Network/networkInterfaces")) {
               List<Deployment.Dependency> dependsOn = dependencies.get(d).dependsOn();
               for (int e = 0; e < dependsOn.size(); e++) {
                  if (dependsOn.get(e).resourceType().equals("Microsoft.Network/publicIPAddresses")) {
                     String resourceName = dependsOn.get(e).resourceName();
                     PublicIPAddress ip = api.getPublicIPAddressApi(resourceGroup).get(resourceName);
                     list.add(ip);
                     break;
                  }
               }
            }
         }
      }
      return list;
   }

   private String getResourceGroupFromId(String id) {
      String searchStr = "/resourceGroups/";
      int indexStart = id.lastIndexOf(searchStr) + searchStr.length();
      searchStr = "/providers/";
      int indexEnd = id.lastIndexOf(searchStr);

      String resourceGroup = id.substring(indexStart, indexEnd);
      return resourceGroup;
   }

   private List<NetworkInterfaceCard> getNetworkInterfaceCards(Deployment deployment) {
      List<NetworkInterfaceCard> result = new ArrayList<NetworkInterfaceCard>();

      String resourceGroup = getResourceGroupFromId(deployment.id());

      if (deployment.properties() != null && deployment.properties().dependencies() != null) {
         for (Deployment.Dependency dependency : deployment.properties().dependencies()) {
            if (dependency.resourceType().equals("Microsoft.Network/networkInterfaces")) {
               String resourceName = dependency.resourceName();
               NetworkInterfaceCard nic = api.getNetworkInterfaceCardApi(resourceGroup).get(resourceName);
               result.add(nic);
            }
         }
      }
      return result;
   }

}
