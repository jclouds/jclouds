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
package org.jclouds.aws.ec2.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.domain.VPC;
import org.jclouds.aws.ec2.options.CreateVpcOptions;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.domain.Subnet;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VPCApi}
 */
@Test(groups = "live", singleThreaded = true)
public class AWSSubnetApiLiveTest extends BaseComputeServiceContextLiveTest {

   private String region;

   private AWSEC2Api api;
   private AWSSubnetApi subnetClient;
   private VPCApi vpcClient;

   private Subnet subnet;
   private VPC vpc;

   public AWSSubnetApiLiveTest() {
      provider = "aws-ec2";
      region = "us-west-2";
   }

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      api = view.unwrapApi(AWSEC2Api.class);
      subnetClient = api.getAWSSubnetApi().get();
      vpcClient = view.unwrapApi(AWSEC2Api.class).getVPCApi().get();
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   public void tearDownContext() {
      try {
         try {
            if (subnet != null) {
               subnetClient.deleteSubnetInRegion(region, subnet.getSubnetId());
            }
         } finally {
            if (vpc != null) {
               vpcClient.deleteVpc(region, vpc.id());
            }
         }
      } finally {
         super.tearDownContext();
      }
   }

   @Test
   public void testCreateSubnetInRegion() {
      vpc = vpcClient.createVpc(region, "10.0.0.0/16", CreateVpcOptions.NONE);
      subnet = subnetClient.createSubnetInRegion(region, vpc.id(), "10.0.0.0/20");
      assertNotNull(subnet);
      assertEquals(subnet.getCidrBlock(), "10.0.0.0/20");
   }

   @Test(dependsOnMethods = "testCreateSubnetInRegion")
   public void testGet() {
      FluentIterable<Subnet> subnets = subnetClient.describeSubnetsInRegion(region, subnet.getSubnetId());
      Subnet subnetFound = Iterables.getOnlyElement(subnets);
      assertEquals(subnetFound.getSubnetId(), subnet.getSubnetId());
   }

   @Test(dependsOnMethods = "testCreateSubnetInRegion")
   public void testFilter() {
      FluentIterable<Subnet> subnets = subnetClient.describeSubnetsInRegionWithFilter(region, 
            ImmutableMultimap.of("subnet-id", subnet.getSubnetId()));
      Subnet subnetFound = Iterables.getOnlyElement(subnets);
      assertEquals(subnetFound.getSubnetId(), subnet.getSubnetId());
   }

   @Test(dependsOnMethods = "testCreateSubnetInRegion")
   public void testList() {
      FluentIterable<Subnet> subnets = subnetClient.describeSubnetsInRegionWithFilter(region, 
            ImmutableMultimap.<String, String>of());
      Optional<Subnet> subnetFound = Iterables.tryFind(subnets, new Predicate<Subnet>() {
         @Override
         public boolean apply(Subnet input) {
            return input != null && input.getSubnetId().equals(subnet.getSubnetId());
         }
      });
      assertTrue(subnetFound.isPresent(), "subnets=" + ImmutableList.copyOf(subnets));
   }

   @Test(dependsOnMethods = {"testGet", "testFilter", "testList"}, alwaysRun = true)
   public void testDelete() {
      if (subnet != null) {
         String subnetId = subnet.getSubnetId();
         subnet = null;
         subnetClient.deleteSubnetInRegion(region, subnetId);
      }
   }
}
