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
package  org.jclouds.azurecompute.arm.functions;

import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Predicate;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

import java.net.URI;

@Singleton
public class CleanupResources implements Function<String, Boolean> {

   private final AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants;
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final AzureComputeApi api;
   private Predicate<URI> nodeTerminated;
   private Predicate<URI> resourceDeleted;

   @Inject
   public CleanupResources(AzureComputeApi azureComputeApi,
                           AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants,
                           @Named(TIMEOUT_NODE_TERMINATED) Predicate<URI> nodeTerminated,
                           @Named(TIMEOUT_RESOURCE_DELETED) Predicate<URI> resourceDeleted) {
      this.azureComputeConstants = azureComputeConstants;
      this.api = azureComputeApi;
      this.nodeTerminated = nodeTerminated;
      this.resourceDeleted = resourceDeleted;
   }

   @Override
   public Boolean apply(String id) {

      logger.debug("Destroying %s ...", id);
      String storageAccountName = id.replaceAll("[^A-Za-z0-9 ]", "") + "stor";
      String group = azureComputeConstants.azureResourceGroup();

      VirtualMachine vm = api.getVirtualMachineApi(group).get(id);
      if (vm != null) {
         URI uri = api.getVirtualMachineApi(group).delete(id);
         if (uri != null) {
            boolean jobDone = nodeTerminated.apply(uri);
            boolean storageAcctDeleteStatus = false;
            boolean deploymentDeleteStatus = false;

            if (jobDone) {
               Deployment deployment = api.getDeploymentApi(group).get(id);
               if (deployment != null) {
                  uri = api.getDeploymentApi(group).delete(id);
                  jobDone = resourceDeleted.apply(uri);
                  if (jobDone) {
                     deploymentDeleteStatus = true;
                  }
               } else {
                  deploymentDeleteStatus = true;
               }
               NetworkInterfaceCard nic = api.getNetworkInterfaceCardApi(group).get(id + "nic");
               if (nic != null) {
                  uri = api.getNetworkInterfaceCardApi(group).delete(id + "nic");
                  if (uri != null) {
                     jobDone = resourceDeleted.apply(uri);
                     if (jobDone) {
                        boolean ipDeleteStatus = false;
                        PublicIPAddress ip = api.getPublicIPAddressApi(group).get(id + "publicip");
                        if (ip != null) {
                           ipDeleteStatus = api.getPublicIPAddressApi(group).delete(id + "publicip");
                        } else {
                           ipDeleteStatus = true;
                        }

                        // Get NSG
                        boolean nsgDeleteStatus = false;
                        NetworkSecurityGroup nsg = api.getNetworkSecurityGroupApi(group).get(id + "nsg");
                        if (nsg != null) {
                           uri = api.getNetworkSecurityGroupApi(group).delete(id + "nsg");
                           jobDone = resourceDeleted.apply(uri);
                           if (jobDone) {
                              nsgDeleteStatus = true;

                           }
                        }
                        else {
                           nsgDeleteStatus = true;
                        }

                        return deploymentDeleteStatus && storageAcctDeleteStatus && ipDeleteStatus && nsgDeleteStatus;
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
