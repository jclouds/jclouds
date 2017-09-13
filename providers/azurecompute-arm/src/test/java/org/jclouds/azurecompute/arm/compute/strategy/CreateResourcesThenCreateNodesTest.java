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
package org.jclouds.azurecompute.arm.compute.strategy;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.compute.options.IpOptions;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.features.PublicIPAddressApi;
import org.jclouds.azurecompute.arm.features.SubnetApi;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "CreateResourcesThenCreateNodesTest")
public class CreateResourcesThenCreateNodesTest {

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "The options.networks and options.ipOptions are exclusive")
   public void testNormalizeNetworkOptionsWithConflictingConfig() {
      AzureTemplateOptions options = new AzureTemplateOptions();
      options.ipOptions(IpOptions.builder().subnet(netResource("/virtualNetworks/vn/subnets/foo")).build());
      options.networks(netResource("/virtualNetworks/vn/subnets/bar"));
      strategy(null).normalizeNetworkOptions(options);
   }
   
   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "The allocateNewPublicIps and publicIpId are exclusive")
   public void testNormalizeNetworkOptionsExclusivePublicIps() {
      AzureTemplateOptions options = new AzureTemplateOptions();
      options.ipOptions(IpOptions.builder().subnet(netResource("/virtualNetworks/vn/subnets/foo"))
            .allocateNewPublicIp(true).publicIpId(netResource("/publicIPAddresses/pub")).build());
      strategy(null).normalizeNetworkOptions(options);
   }

   public void testPortableNetworkOptions() {
      AzureComputeApi api = createMock(AzureComputeApi.class);
      SubnetApi subnetApi = createMock(SubnetApi.class);

      expect(api.getSubnetApi(anyObject(String.class), anyObject(String.class))).andReturn(subnetApi).times(2);
      expect(subnetApi.get(anyObject(String.class))).andReturn(Subnet.builder().build()).times(2);
      replay(api, subnetApi);

      AzureTemplateOptions options = new AzureTemplateOptions();
      options.networks(netResource("/virtualNetworks/vn/subnets/foo"), netResource("/virtualNetworks/vn/subnets/bar"));
      strategy(api).normalizeNetworkOptions(options);

      assertEquals(options.getIpOptions(), ImmutableList.of(
              IpOptions.builder().subnet(netResource("/virtualNetworks/vn/subnets/foo")).allocateNewPublicIp(true).build(), 
              IpOptions.builder().subnet(netResource("/virtualNetworks/vn/subnets/bar")).allocateNewPublicIp(true).build())
      );

      // Verify that the code has validated that the subnets exist
      verify(api, subnetApi);
   }

   public void testProviderSpecificNetworkOptions() {
      AzureComputeApi api = createMock(AzureComputeApi.class);
      SubnetApi subnetApi = createMock(SubnetApi.class);
      PublicIPAddressApi publicIpApi = createMock(PublicIPAddressApi.class);

      expect(api.getSubnetApi(anyObject(String.class), anyObject(String.class))).andReturn(subnetApi).times(2);
      expect(api.getPublicIPAddressApi(anyObject(String.class))).andReturn(publicIpApi);
      expect(subnetApi.get(anyObject(String.class))).andReturn(Subnet.builder().build()).times(2);
      expect(publicIpApi.get(anyObject(String.class))).andReturn(mockAddress());
      replay(api, subnetApi, publicIpApi);

      IpOptions publicOpts = IpOptions.builder().subnet(netResource("/virtualNetworks/vn/subnets/foo"))
            .publicIpId(netResource("/publicIPAddresses/pub")).address("10.0.0.2").build();
      IpOptions privateOpts = IpOptions.builder().subnet(netResource("/virtualNetworks/vn/subnets/bar")).build();

      AzureTemplateOptions options = new AzureTemplateOptions();
      options.ipOptions(publicOpts, privateOpts);
      strategy(api).normalizeNetworkOptions(options);

      assertEquals(options.getIpOptions(), ImmutableList.of(publicOpts, privateOpts));

      // Verify that the code has validated that the subnets exist
      verify(api, subnetApi, publicIpApi);
   }

   private static CreateResourcesThenCreateNodes strategy(AzureComputeApi api) {
      return new CreateResourcesThenCreateNodes(null, null, null, null, null, api, null, null, null, null);
   }

   private static String netResource(String resource) {
      return "/subscriptions/subs/resourceGroups/rg/providers/Microsoft.Network" + resource;
   }

   private static PublicIPAddress mockAddress() {
      return PublicIPAddress.builder().name("name").id("id").etag("etag").location("location")
            .properties(PublicIPAddressProperties.builder().publicIPAllocationMethod("Dynamic").build()).build();
   }
}
