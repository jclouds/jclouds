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
package org.jclouds.azurecompute.arm.compute.extensions;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.compute.options.TemplateOptions.Builder.inboundPorts;
import static org.jclouds.compute.options.TemplateOptions.Builder.securityGroups;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.net.domain.IpProtocol.TCP;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.extensions.internal.BaseSecurityGroupExtensionLiveTest;
import org.jclouds.domain.Location;
import org.jclouds.net.util.IpPermissions;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.cache.LoadingCache;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Live test for AzureCompute
 * {@link org.jclouds.compute.extensions.SecurityGroupExtension} implementation.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeSecurityGroupExtensionLiveTest")
public class AzureComputeSecurityGroupExtensionLiveTest extends BaseSecurityGroupExtensionLiveTest {

   private LoadingCache<String, ResourceGroup> resourceGroupMap;
   private ResourceGroup testResourceGroup;

   public AzureComputeSecurityGroupExtensionLiveTest() {
      provider = "azurecompute-arm";
   }

   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      resourceGroupMap = context.utils().injector()
            .getInstance(Key.get(new TypeLiteral<LoadingCache<String, ResourceGroup>>() {
            }));
      createResourceGroup();
   }

   @Test(groups = { "integration", "live" }, dependsOnMethods = "testCreateSecurityGroup")
   public void testCreateNodeWithSecurityGroup() throws RunNodesException, InterruptedException, ExecutionException {
      ComputeService computeService = view.getComputeService();
      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      NodeMetadata node = getOnlyElement(computeService.createNodesInGroup(nodeGroup, 1, securityGroups(groupId)));

      try {
         Set<SecurityGroup> groups = securityGroupExtension.get().listSecurityGroupsForNode(node.getId());
         assertEquals(groups.size(), 1, "node has " + groups.size() + " groups");
         assertEquals(getOnlyElement(groups).getId(), groupId);
      } finally {
         computeService.destroyNodesMatching(inGroup(node.getGroup()));
      }
   }

   @Test(groups = { "integration", "live" }, dependsOnMethods = "testCreateSecurityGroup")
   public void testCreateNodeWithInboundPorts() throws RunNodesException, InterruptedException, ExecutionException {
      ComputeService computeService = view.getComputeService();
      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      NodeMetadata node = getOnlyElement(computeService
            .createNodesInGroup(nodeGroup, 1, inboundPorts(22, 23, 24, 8000)));

      try {
         Set<SecurityGroup> groups = securityGroupExtension.get().listSecurityGroupsForNode(node.getId());
         assertEquals(groups.size(), 1, "node has " + groups.size() + " groups");

         SecurityGroup group = getOnlyElement(groups);
         assertEquals(group.getIpPermissions().size(), 2);
         assertEquals(get(group.getIpPermissions(), 0), IpPermissions.permit(TCP).fromPort(22).to(24));
         assertEquals(get(group.getIpPermissions(), 1), IpPermissions.permit(TCP).port(8000));
      } finally {
         computeService.destroyNodesMatching(inGroup(node.getGroup()));
      }
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      try {
         view.unwrapApi(AzureComputeApi.class).getResourceGroupApi().delete(testResourceGroup.name());
      } finally {
         super.tearDownContext();
      }
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      AzureLiveTestUtils.defaultProperties(properties);
      setIfTestSystemPropertyPresent(properties, "oauth.endpoint");
      return properties;
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      return AzureComputeProviderMetadata.builder().build();
   }

   private void createResourceGroup() {
      Location location = getNodeTemplate().getLocation();
      testResourceGroup = resourceGroupMap.getUnchecked(location.getId());
   }
}
