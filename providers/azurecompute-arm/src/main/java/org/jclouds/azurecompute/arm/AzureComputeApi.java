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
package org.jclouds.azurecompute.arm;

import java.io.Closeable;
import javax.ws.rs.PathParam;

import org.jclouds.azurecompute.arm.domain.ServicePrincipal;
import org.jclouds.azurecompute.arm.features.AvailabilitySetApi;
import org.jclouds.azurecompute.arm.features.DeploymentApi;
import org.jclouds.azurecompute.arm.features.DiskApi;
import org.jclouds.azurecompute.arm.features.GraphRBACApi;
import org.jclouds.azurecompute.arm.features.ImageApi;
import org.jclouds.azurecompute.arm.features.JobApi;
import org.jclouds.azurecompute.arm.features.LoadBalancerApi;
import org.jclouds.azurecompute.arm.features.LocalNetworkGatewayApi;
import org.jclouds.azurecompute.arm.features.LocationApi;
import org.jclouds.azurecompute.arm.features.MetricDefinitionsApi;
import org.jclouds.azurecompute.arm.features.MetricsApi;
import org.jclouds.azurecompute.arm.features.NetworkInterfaceCardApi;
import org.jclouds.azurecompute.arm.features.NetworkSecurityGroupApi;
import org.jclouds.azurecompute.arm.features.NetworkSecurityRuleApi;
import org.jclouds.azurecompute.arm.features.OSImageApi;
import org.jclouds.azurecompute.arm.features.PublicIPAddressApi;
import org.jclouds.azurecompute.arm.features.ResourceGroupApi;
import org.jclouds.azurecompute.arm.features.ResourceProviderApi;
import org.jclouds.azurecompute.arm.features.StorageAccountApi;
import org.jclouds.azurecompute.arm.features.SubnetApi;
import org.jclouds.azurecompute.arm.features.VMSizeApi;
import org.jclouds.azurecompute.arm.features.VaultApi;
import org.jclouds.azurecompute.arm.features.VirtualMachineApi;
import org.jclouds.azurecompute.arm.features.VirtualMachineScaleSetApi;
import org.jclouds.azurecompute.arm.features.VirtualNetworkApi;
import org.jclouds.azurecompute.arm.features.VirtualNetworkGatewayApi;
import org.jclouds.azurecompute.arm.features.VirtualNetworkGatewayConnectionApi;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Delegate;

import com.google.common.base.Supplier;
import com.google.inject.Provides;

/**
 * The Azure Resource Manager API is a REST API for managing your services and deployments.
 * <p>
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790568.aspx" >doc</a>
 */
public interface AzureComputeApi extends Closeable {
   
   /**
    * The Azure Resource Manager API includes operations for managing resource groups in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790546.aspx">docs</a>
    */
   @Delegate
   ResourceGroupApi getResourceGroupApi();

   /**
    * Provides access to the Job tracking API.
    */
   @Delegate
   JobApi getJobApi();

   /**
    * This Azure Resource Manager API provides all of the locations that are available for resource providers
    *
    * @see <a href="https://msdn.microsoft.com/en-US/library/azure/dn790540.aspx">docs</a>
    */
   @Delegate
   LocationApi getLocationApi();

   /**
    * The Azure Resource Manager API includes operations for managing the storage accounts in your subscription.
    *
    * @see <https://msdn.microsoft.com/en-us/library/mt163683.aspx">docs</a>
    */
   @Delegate
   StorageAccountApi getStorageAccountApi(@PathParam("resourceGroup") String resourceGroup);

   /**
    * The Subnet API includes operations for managing the subnets in your virtual network.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163621.aspx">docs</a>
    */
   @Delegate
   SubnetApi getSubnetApi(@PathParam("resourcegroup") String resourcegroup,
                          @PathParam("virtualnetwork") String virtualnetwork);

   /**
    * The Virtual Network API includes operations for managing the virtual networks in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163661.aspx">docs</a>
    */
   @Delegate
   VirtualNetworkApi getVirtualNetworkApi(@PathParam("resourcegroup") String resourcegroup);


   /**
    * The Network Interface Card API includes operations for managing the NICs in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/mt163668.aspx">docs</a>
    */
   @Delegate
   NetworkInterfaceCardApi getNetworkInterfaceCardApi(@Nullable @PathParam("resourcegroup") String resourcegroup);

   /**
    * The Public IP Address API includes operations for managing public ID Addresses for NICs in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163638.aspx">docs</a>
    */
   @Delegate
   PublicIPAddressApi getPublicIPAddressApi(@Nullable @PathParam("resourcegroup") String resourcegroup);


   /**
    * The Virtual Machine API includes operations for managing the virtual machines in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163630.aspx">docs</a>
    */
   @Delegate
   VirtualMachineApi getVirtualMachineApi(@Nullable @PathParam("resourceGroup") String resourceGroup);

   /**
    * The Virtual Machine Scale Set API includes operations for managing the virtual machines in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163630.aspx">docs</a>
    */
   @Delegate
   VirtualMachineScaleSetApi getVirtualMachineScaleSetApi(@PathParam("resourceGroup") String resourceGroup);

   /**
    * This Azure Resource Manager API lists all available virtual machine sizes for a subscription in a given region
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt269440.aspx">docs</a>
    */
   @Delegate
   VMSizeApi getVMSizeApi(@PathParam("location") String location);

   /**
    * The Azure Resource Manager API gets all the OS images in your subscription.
    *
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/compute/virtualmachineimages">docs</a>
    */
   @Delegate
   OSImageApi getOSImageApi(@PathParam("location") String location);

   /**
    * The Deployment API allows for the management of Azure Resource Manager resources through the use of templates.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790549.aspx">docs</a>
    */
   @Delegate
   DeploymentApi getDeploymentApi(@PathParam("resourcegroup") String resourceGroup);

   /**
    * The NetworkSecurityGroup API includes operations for managing network security groups within your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163615.aspx">docs</a>
    */
   @Delegate
   NetworkSecurityGroupApi getNetworkSecurityGroupApi(@PathParam("resourcegroup") String resourcegroup);
 
   /**
    * The NetworkSecurityRule API includes operations for managing network security rules within a network security group.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163580.aspx">docs</a>
    */
   @Delegate
   NetworkSecurityRuleApi getNetworkSecurityRuleApi(@PathParam("resourcegroup") String resourcegroup,
                                                    @PathParam("networksecuritygroup") String networksecuritygroup);

   /**
    * The LoadBalancer API includes operations for managing load balancers
    * within your subscription.
    *
    * @see <a href=
    *      "https://msdn.microsoft.com/en-us/library/azure/mt163574.aspx">docs
    *      </a>
    */
   @Delegate
   LoadBalancerApi getLoadBalancerApi(@PathParam("resourcegroup") String resourcegroup);

   /**
    * The AvailabilitySet API includes operations for managing availability sets
    * within your subscription.
    *
    * @see <a href=
    *      "https://docs.microsoft.com/en-us/rest/api/compute/availabilitysets">docs
    *      </a>
    */
   @Delegate
   AvailabilitySetApi getAvailabilitySetApi(@PathParam("resourcegroup") String resourcegroup);

   /**
    * The Azure Resource Provider API provides information about a resource provider and its supported resource types.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790534.aspx">docs</a>
    */
   @Delegate
   ResourceProviderApi getResourceProviderApi();

   /**
    * The ManagedDataDisk API includes operations for managing data disks within your subscription.
    *
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/manageddisks/disks/disks-rest-api">docs</a>
    */
   @Delegate
   DiskApi getDiskApi(@PathParam("resourcegroup") String resourcegroup);

   /**
    * The virtual machine image API includes operations for managing data disks within your subscription.
    *
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/manageddisks/images/images-rest-api">docs</a>
    */
   @Delegate
   ImageApi getVirtualMachineImageApi(@PathParam("resourcegroup") String resourcegroup);

   /**
    * The metrics API includes operations to get insights into entities within your
    * subscription.
    *
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/monitor/metrics">docs</a>
    */
   @Delegate
   MetricsApi getMetricsApi(@PathParam("resourceid") String resourceid);

   /**
    * The metric definitions API includes operations to get insights available for entities within your
    * subscription.
    *
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/monitor/metricdefinitions">docs</a>
    */
   @Delegate
   MetricDefinitionsApi getMetricsDefinitionsApi(@PathParam("resourceid") String resourceid);

   /**
    * The Azure Active Directory Graph API provides programmatic access to Azure
    * AD through REST API endpoints.
    *
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/graphrbac/">docs</a>
    */
   @Delegate
   GraphRBACApi getGraphRBACApi();
   
   /**
    * Managing your key vaults as well as the keys, secrets, and certificates within your key vaults can be 
    * accomplished through a REST interface.
    *
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/keyvault/">docs</a>
    */
   @Delegate
   VaultApi getVaultApi(@PathParam("resourcegroup") String resourcegroup);
   
   /**
    * Management features for Local Network Gateways.
    * 
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/network-gateway/localnetworkgateways">docs</a>
    */
   @Delegate
   LocalNetworkGatewayApi getLocalNetworkGatewayApi(@PathParam("resourcegroup") String resourcegroup);
   
   /**
    * Management features for Virtual Network Gateways.
    * 
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/network-gateway/virtualnetworkgateways">docs</a>
    */
   @Delegate
   VirtualNetworkGatewayApi getVirtualNetworkGatewayApi(@PathParam("resourcegroup") String resourcegroup);
   
   /**
    * Management features for Virtual Network Gateway Connections.
    * 
    * @see <a href="https://docs.microsoft.com/en-us/rest/api/network-gateway/virtualnetworkgatewayconnections">docs</a>
    */
   @Delegate
   VirtualNetworkGatewayConnectionApi getVirtualNetworkGatewayConnectionApi(@PathParam("resourcegroup") String resourcegroup);
   
   /**
    * Returns the information about the current service principal.
    */
   @Provides
   Supplier<ServicePrincipal> getServicePrincipal();
}
