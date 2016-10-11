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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.aws.ec2.domain.VPC;
import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.jclouds.aws.ec2.options.CreateVpcOptions;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "VPCApiMockTest", singleThreaded = true)
public class VPCApiMockTest extends BaseAWSEC2ApiMockTest {

   public void createVpc() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/create_vpc.xml");
      VPC result = vpcApi().createVpc(DEFAULT_REGION, "10.0.0.0/16", CreateVpcOptions.NONE);

      assertNotNull(result);
      assertEquals(result.id(), "vpc-1a2b3c4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateVpc&CidrBlock=10.0.0.0/16");
   }

   public void describeVpcsInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_vpcs.xml");
      FluentIterable<VPC> result = vpcApi().describeVpcsInRegion(DEFAULT_REGION);

      assertFalse(result.isEmpty());
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeVpcs");
   }

   public void describeVpcsInRegionReturns404() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      assertEquals(vpcApi().describeVpcsInRegion(DEFAULT_REGION), FluentIterable.from(ImmutableSet.of()));

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeVpcs");
   }

   public void deleteVpc() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/delete_vpc.xml");

      assertTrue(vpcApi().deleteVpc(DEFAULT_REGION, "vpc-id"));

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteVpc&VpcId=vpc-id");
   }

   public void deleteVpcReturns404() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      assertFalse(vpcApi().deleteVpc(DEFAULT_REGION, "vpc-id"));

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteVpc&VpcId=vpc-id");
   }

   private VPCApi vpcApi() {
      return api().getVPCApi().get();
   }
}
