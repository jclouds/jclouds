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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.domain.VPC;
import org.jclouds.aws.ec2.options.CreateVpcOptions;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;

/**
 * Tests behavior of {@code VPCApi}
 */
@Test(groups = "live", singleThreaded = true)
public class VPCApiLiveTest extends BaseComputeServiceContextLiveTest {
   public VPCApiLiveTest() {
      provider = "aws-ec2";
   }

   private VPCApi client;
   private VPC vpc;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrapApi(AWSEC2Api.class).getVPCApi().get();
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   public void tearDownContext() {
      try {
         if (vpc != null) {
            client.deleteVpc(null, vpc.id());
         }
      } finally {
         super.tearDownContext();
      }
   }

   @Test
   public void testCreate() {
      vpc = client.createVpc(null, "10.0.0.0/16", CreateVpcOptions.NONE);
      assertNotNull(vpc);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      FluentIterable<VPC> vpcs = client.describeVpcsInRegion(null, vpc.id());
      assertTrue(vpcs.toList().size() == 1);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      FluentIterable<VPC> vpcs = client.describeVpcsInRegion(null);
      assertFalse(vpcs.toList().isEmpty());
   }

   @Test(dependsOnMethods = {"testList", "testGet"}, alwaysRun = true)
   public void testDelete() {
      if (vpc != null) {
         String vpcId = vpc.id();
         vpc = null;
         assertTrue(client.deleteVpc(null, vpcId));
      }
   }

}
