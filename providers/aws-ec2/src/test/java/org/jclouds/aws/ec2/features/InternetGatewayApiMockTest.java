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

import static org.jclouds.aws.ec2.options.InternetGatewayOptions.Builder.dryRun;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.ec2.domain.InternetGateway;
import org.jclouds.aws.ec2.domain.InternetGatewayAttachment;
import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.jclouds.aws.ec2.options.InternetGatewayOptions;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "InternetGatewayApiMockTest", singleThreaded = true)
public class InternetGatewayApiMockTest extends BaseAWSEC2ApiMockTest {

   public void createInternetGateway() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/create_internet_gateway.xml");
      InternetGateway result = gatewayApi().createInternetGateway(DEFAULT_REGION, InternetGatewayOptions.NONE);

      assertNotNull(result, "Failed to create InternetGateway object");
      assertEquals(result.id(), "igw-fada7c9c", "Gateway id does not match mock data: " + result.id());

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateInternetGateway");
   }

   public void attachInternetGateway() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/attach_internet_gateway.xml");

      final Boolean attached = gatewayApi().attachInternetGateway(DEFAULT_REGION, "igw-fada7c9c", "vpc-6250b91b");
      assertTrue(attached, "Failed to attach InternetGateway");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=AttachInternetGateway&InternetGatewayId=igw-fada7c9c&VpcId=vpc-6250b91b");
   }

   public void attachInternetGatewayFail() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/attach_internet_gateway_failed.xml");

      final Boolean attached = gatewayApi().attachInternetGateway(DEFAULT_REGION, "igw-fada7c9c", "vpc-6250b91b");
      assertFalse(attached, "Gateway reported as created despite failure response");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=AttachInternetGateway&InternetGatewayId=igw-fada7c9c&VpcId=vpc-6250b91b");
   }

   public void attachInternetGatewayNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      final Boolean attached = gatewayApi().attachInternetGateway(DEFAULT_REGION, "igw-fada7c9c", "vpc-6250b91b");
      assertFalse(attached, "Somehow attached gateway despite NotFound response");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=AttachInternetGateway&InternetGatewayId=igw-fada7c9c&VpcId=vpc-6250b91b");
   }

   public void detachInternetGateway() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/detach_internet_gateway.xml");

      final Boolean detached = gatewayApi().detachInternetGateway(DEFAULT_REGION, "igw-fada7c9c", "vpc-6250b91b");
      assertTrue(detached, "Gateway not successfully detached");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DetachInternetGateway&InternetGatewayId=igw-fada7c9c&VpcId=vpc-6250b91b");

   }

   public void detachInternetGatewayNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      final Boolean detached = gatewayApi().detachInternetGateway(DEFAULT_REGION, "igw-fada7c9c", "vpc-6250b91b");
      assertFalse(detached, "Non-existent gateway somehow successfully detached");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DetachInternetGateway&InternetGatewayId=igw-fada7c9c&VpcId=vpc-6250b91b");

   }

   public void getInternetGateway() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/get_internet_gateway.xml");

      final String igwId = "igw-fada7c9c";
      final FluentIterable<InternetGateway> internetGateways =
         gatewayApi().describeInternetGatewaysInRegion(DEFAULT_REGION, igwId);

      final ImmutableList<InternetGateway> gateways = internetGateways.toList();
      assertEquals(gateways.size(), 1);
      assertEquals(gateways.get(0).id(), igwId);
      assertEquals(gateways.get(0).tags().get("Name"), "get_internet_gateway_test");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeInternetGateways&InternetGatewayId.1=igw-fada7c9c");
   }

   public void getInternetGatewayNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      final String igwId = "igw-fada7c9c";
      final FluentIterable<InternetGateway> internetGateways =
         gatewayApi().describeInternetGatewaysInRegion(DEFAULT_REGION, igwId);

      final ImmutableList<InternetGateway> gateways = internetGateways.toList();
      assertEquals(gateways.size(), 0);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeInternetGateways&InternetGatewayId.1=igw-fada7c9c");

   }

   public void describeInternetGateways() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_internet_gateways.xml");

      final FluentIterable<InternetGateway> internetGateways =
         gatewayApi().describeInternetGatewaysInRegion(DEFAULT_REGION);

      final List<InternetGateway> gateways = Lists.newArrayList(internetGateways.toList());

      assertEquals(gateways.size(), 3);
      final ImmutableMap<String, InternetGateway> asMap =
         ImmutableMap.of(gateways.get(0).id(), gateways.get(0),
            gateways.get(1).id(), gateways.get(1),
            gateways.get(2).id(), gateways.get(2));

      assertEquals(asMap.get("igw-fada7c9c").tags().get("Name"), "describe_internet_gateways_test");
      final InternetGatewayAttachment gw6bca130c = asMap.get("igw-6bca130c").attachmentSet().iterator().next();
      assertEquals(gw6bca130c.vpcId(), "vpc-a13d29c6");
      assertEquals(gw6bca130c.state(), InternetGatewayAttachment.State.AVAILABLE);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeInternetGateways");

   }

   public void deleteInternetGateway() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/delete_internet_gateway.xml");

      final boolean deleted = gatewayApi().deleteInternetGateway(DEFAULT_REGION, "igw-fada7c9c");
      assertTrue(deleted, "Failed to delete gateway");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteInternetGateway&InternetGatewayId=igw-fada7c9c");
   }

   public void deleteInternetGatewayNotFound() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      final boolean deleted = gatewayApi().deleteInternetGateway(DEFAULT_REGION, "igw-fada7c9c");
      assertFalse(deleted, "Somehow deleted a gateway that does not exist");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteInternetGateway&InternetGatewayId=igw-fada7c9c");
   }

   @Test
   public void testWithOptions() throws Exception {

      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/create_internet_gateway_dry_run.xml");

      try {
         gatewayApi().createInternetGateway(DEFAULT_REGION, dryRun());
      } catch (AWSResponseException e) {
         assertEquals(e.getError().getCode(), "DryRunOperation", "Expected DryRunOperation but got " + e.getError());
      }

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateInternetGateway&DryRun=true");

   }
   private InternetGatewayApi gatewayApi() {
      return api().getInternetGatewayApi().get();
   }
}
