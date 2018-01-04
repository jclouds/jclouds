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
package org.jclouds.azurecompute.arm.compute.config;

import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.compute.AzureComputeService;
import org.jclouds.azurecompute.arm.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.arm.compute.domain.ResourceGroupAndNameAndIngressRules;
import org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension;
import org.jclouds.azurecompute.arm.compute.extensions.AzureComputeSecurityGroupExtension;
import org.jclouds.azurecompute.arm.compute.functions.LocationToLocation;
import org.jclouds.azurecompute.arm.compute.functions.NetworkSecurityGroupToSecurityGroup;
import org.jclouds.azurecompute.arm.compute.functions.NetworkSecurityRuleToIpPermission;
import org.jclouds.azurecompute.arm.compute.functions.VMHardwareToHardware;
import org.jclouds.azurecompute.arm.compute.functions.VMImageToImage;
import org.jclouds.azurecompute.arm.compute.functions.VirtualMachineToNodeMetadata;
import org.jclouds.azurecompute.arm.compute.loaders.CreateSecurityGroupIfNeeded;
import org.jclouds.azurecompute.arm.compute.loaders.DefaultResourceGroup;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.compute.strategy.CreateResourcesThenCreateNodes;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.VMHardware;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.NodeAndTemplateOptionsToStatement;
import org.jclouds.compute.functions.NodeAndTemplateOptionsToStatementWithoutPublicKey;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.util.PasswordGenerator;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class AzureComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<VirtualMachine, VMHardware, VMImage, Location> {

   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<ComputeServiceAdapter<VirtualMachine, VMHardware, VMImage, Location>>() {
      }).to(AzureComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<VMImage, org.jclouds.compute.domain.Image>>() {
      }).to(VMImageToImage.class);
      bind(new TypeLiteral<Function<VMHardware, Hardware>>() {
      }).to(VMHardwareToHardware.class);
      bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
      }).to(VirtualMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<Location, org.jclouds.domain.Location>>() {
      }).to(LocationToLocation.class);
      bind(new TypeLiteral<Function<NetworkSecurityGroup, SecurityGroup>>() {
      }).to(NetworkSecurityGroupToSecurityGroup.class);
      bind(new TypeLiteral<Function<NetworkSecurityRule, IpPermission>>() {
      }).to(NetworkSecurityRuleToIpPermission.class);
      bind(ComputeService.class).to(AzureComputeService.class);

      install(new LocationsFromComputeServiceAdapterModule<VirtualMachine, VMHardware, VMImage, Location>() {
      });

      bind(TemplateOptions.class).to(AzureTemplateOptions.class);
      bind(NodeAndTemplateOptionsToStatement.class).to(NodeAndTemplateOptionsToStatementWithoutPublicKey.class);
      bind(CreateNodesInGroupThenAddToSet.class).to(CreateResourcesThenCreateNodes.class);

      bind(new TypeLiteral<CacheLoader<ResourceGroupAndNameAndIngressRules, String>>() {
      }).to(CreateSecurityGroupIfNeeded.class);
      bind(new TypeLiteral<CacheLoader<String, ResourceGroup>>() {
      }).to(DefaultResourceGroup.class);

      bind(new TypeLiteral<ImageExtension>() {
      }).to(AzureComputeImageExtension.class);
      bind(new TypeLiteral<SecurityGroupExtension>() {
      }).to(AzureComputeSecurityGroupExtension.class);
   }
   
   @Provides
   @Singleton
   protected PasswordGenerator.Config providePasswordGenerator() {
      // Guest passwords must be between 6-72 characters long.
      // Must contain an upper case character.
      // Must contain a lower case character.
      // Must contain a numeric digit.
      // Must contain a special character. Control characters are not allowed.
      return new PasswordGenerator()
            .lower().min(2).max(10)
            .upper().min(2).max(10)
            .numbers().min(2).max(10)
            .symbols().min(2).max(10);
   }

   @Provides
   @Singleton
   protected final LoadingCache<ResourceGroupAndNameAndIngressRules, String> securityGroupMap(
         CacheLoader<ResourceGroupAndNameAndIngressRules, String> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   protected final LoadingCache<String, ResourceGroup> defaultResourceGroup(CacheLoader<String, ResourceGroup> in) {
      return CacheBuilder.newBuilder().build(in);
   }
}
