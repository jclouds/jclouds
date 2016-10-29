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
import static org.testng.Assert.assertTrue;

import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.jclouds.aws.ec2.options.CreateSubnetOptions;
import org.jclouds.ec2.domain.Subnet;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "AWSSubnetApiMockTest", singleThreaded = true)
public class AWSSubnetApiMockTest extends BaseAWSEC2ApiMockTest {

   private final String describeSubnetsResponse = "<DescribeSubnetsResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-11-15/\">\n" +
         "  <requestId>7a62c49f-347e-4fc4-9331-6e8eEXAMPLE</requestId>\n" +
         "  <subnetSet>\n" +
         "    <item>\n" +
         "      <subnetId>subnet-9d4a7b6c</subnetId>\n" +
         "      <state>available</state>\n" +
         "      <vpcId>vpc-1a2b3c4d</vpcId>\n" +
         "      <cidrBlock>10.0.1.0/24</cidrBlock> \n" +
         "      <ipv6CidrBlockAssociationSet>\n" +
         "        <item>\n" +
         "          <ipv6CidrBlock>2001:db8:1234:1a00::/64</ipv6CidrBlock>\n" +
         "          <associationId>subnet-cidr-assoc-abababab</associationId>\n" +
         "          <ipv6CidrBlockState>\n" +
         "          <state>ASSOCIATED</state>\n" +
         "          </ipv6CidrBlockState>\n" +
         "        </item>\n" +
         "      </ipv6CidrBlockAssociationSet>\n" +
         "      <availableIpAddressCount>251</availableIpAddressCount>\n" +
         "      <availabilityZone>us-east-1a</availabilityZone>\n" +
         "      <defaultForAz>false</defaultForAz>\n" +
         "      <mapPublicIpOnLaunch>false</mapPublicIpOnLaunch>\n" +
         "      <tagSet/>\n" +
         "      <assignIpv6AddressOnCreation>false</assignIpv6AddressOnCreation>\n" +
         "    </item>\n" +
         "  </subnetSet>\n" +
         "</DescribeSubnetsResponse>";

   public void createSubnetInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody("<CreateSubnetResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-09-15/\">\n" +
              "  <requestId>7a62c49f-347e-4fc4-9331-6e8eEXAMPLE</requestId>\n" +
              "  <subnet>\n" +
              "    <subnetId>subnet-9d4a7b6c</subnetId>\n" +
              "    <state>pending</state>\n" +
              "    <vpcId>vpc-1a2b3c4d</vpcId>\n" +
              "    <cidrBlock>10.0.1.0/24</cidrBlock> \n" +
              "    <availableIpAddressCount>251</availableIpAddressCount>\n" +
              "    <availabilityZone>us-east-1a</availabilityZone>\n" +
              "    <tagSet/>\n" +
              "  </subnet>\n" +
              "</CreateSubnetResponse>"));

      Subnet result = subnetApi().createSubnetInRegion(DEFAULT_REGION, "vpc-1a2b3c4d", "10.0.1.0/24");
      assertEquals(result.getVpcId(), "vpc-1a2b3c4d");
      assertEquals(result.getCidrBlock(), "10.0.1.0/24");
      assertEquals(result.getAvailabilityZone(), "us-east-1a");
      assertEquals(result.getSubnetId(), "subnet-9d4a7b6c");
      assertEquals(result.getSubnetState().value(), "pending");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateSubnet&VpcId=vpc-1a2b3c4d&CidrBlock=10.0.1.0/24");
   }

   public void createSubnetInRegion_options() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION,
            new MockResponse().setBody("<CreateSubnetResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-09-15/\">\n" +
                    "  <requestId>7a62c49f-347e-4fc4-9331-6e8eEXAMPLE</requestId>\n" +
                    "  <subnet>\n" +
                    "    <subnetId>subnet-9d4a7b6c</subnetId>\n" +
                    "    <state>pending</state>\n" +
                    "    <vpcId>vpc-1a2b3c4d</vpcId>\n" +
                    "    <cidrBlock>10.0.1.0/24</cidrBlock> \n" +
                    "    <availableIpAddressCount>251</availableIpAddressCount>\n" +
                    "    <availabilityZone>us-east-1a</availabilityZone>\n" +
                    "    <tagSet/>\n" +
                    "  </subnet>\n" +
                    "</CreateSubnetResponse>"));

      Subnet result = subnetApi().createSubnetInRegion(DEFAULT_REGION, "vpc-1a2b3c4d", "10.0.1.0/24",
            new CreateSubnetOptions().dryRun().availabilityZone("us-east-1a"));
      assertEquals(result.getVpcId(), "vpc-1a2b3c4d");
      assertEquals(result.getCidrBlock(), "10.0.1.0/24");
      assertEquals(result.getAvailabilityZone(), "us-east-1a");
      assertEquals(result.getSubnetId(), "subnet-9d4a7b6c");
      assertEquals(result.getSubnetState().value(), "pending");
      
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateSubnet&VpcId=vpc-1a2b3c4d&CidrBlock=10.0.1.0/24&DryRun=true&AvailabilityZone=us-east-1a");
   }

   public void deleteSubnetInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION,
            new MockResponse().setBody("<DeleteSubnetResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-09-15/\">\n" +
                    "  <requestId>7a62c49f-347e-4fc4-9331-6e8eEXAMPLE</requestId>\n" +
                    "  <return>true</return>\n" +
                    "</DeleteSubnetResponse>"));

      subnetApi().deleteSubnetInRegion(DEFAULT_REGION, "subnet-9d4a7b6c");
      
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteSubnet&SubnetId=subnet-9d4a7b6c");
   }

   public void describeSubnetInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION,
            new MockResponse().setBody(describeSubnetsResponse));

      FluentIterable<Subnet> results = subnetApi().describeSubnetsInRegion(DEFAULT_REGION);
      Subnet result = Iterables.getOnlyElement(results);
      assertEquals(result.getSubnetId(), "subnet-9d4a7b6c");
      assertEquals(result.getSubnetState().value(), "available");
      assertEquals(result.getVpcId(), "vpc-1a2b3c4d");
      assertEquals(result.getCidrBlock(), "10.0.1.0/24");
      assertEquals(result.getAvailabilityZone(), "us-east-1a");
      assertEquals(result.getAvailableIpAddressCount(), 251);
      
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSubnets");
   }

   public void describeSubnetInRegionWithFilter() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION,
            new MockResponse().setBody(describeSubnetsResponse));

      FluentIterable<Subnet> results = subnetApi().describeSubnetsInRegionWithFilter(DEFAULT_REGION,
            ImmutableMultimap.of("vpc-id", "vpc-1a2b3c4d", "availabilityZone", "us-east-1a"));
      Subnet result = Iterables.getOnlyElement(results);
      assertEquals(result.getSubnetId(), "subnet-9d4a7b6c");
      assertEquals(result.getSubnetState().value(), "available");
      assertEquals(result.getVpcId(), "vpc-1a2b3c4d");
      assertEquals(result.getCidrBlock(), "10.0.1.0/24");
      assertEquals(result.getAvailabilityZone(), "us-east-1a");
      assertEquals(result.getAvailableIpAddressCount(), 251);
      
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSubnets&Filter.1.Name=vpc-id&Filter.1.Value.1=vpc-1a2b3c4d&Filter.2.Name=availabilityZone&Filter.2.Value.1=us-east-1a");
   }

   public void describeSubnetInRegionWhen404() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION,
            new MockResponse().setResponseCode(404));

      FluentIterable<Subnet> results = subnetApi().describeSubnetsInRegion(DEFAULT_REGION);
      assertTrue(Iterables.isEmpty(results));
      
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSubnets");
   }

   public void describeSubnetInRegionWithFilterWhen404() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION,
            new MockResponse().setResponseCode(404));

      FluentIterable<Subnet> results = subnetApi().describeSubnetsInRegionWithFilter(DEFAULT_REGION,
            ImmutableMultimap.of("vpc-id", "vpc-1a2b3c4d"));
      assertTrue(Iterables.isEmpty(results));
      
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSubnets&Filter.1.Name=vpc-id&Filter.1.Value.1=vpc-1a2b3c4d");
   }

   public void describeSubnetInNonDefaultRegionWhen404() throws Exception {
      String region = "us-west-2";
      
      enqueueRegions(DEFAULT_REGION, region);
      enqueue(region,
            new MockResponse().setResponseCode(404));

      FluentIterable<Subnet> results = subnetApiForRegion(region).list();
      assertTrue(Iterables.isEmpty(results));
      
      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(region, "Action=DescribeSubnets");
   }

   private AWSSubnetApi subnetApi() {
      return api().getAWSSubnetApi().get();
   }
   
   private AWSSubnetApi subnetApiForRegion(String region) {
      return api().getAWSSubnetApiForRegion(region).get();
   }
}
