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

import java.util.Set;

import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.jclouds.aws.ec2.options.CreateSecurityGroupOptions;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "AWSSecurityGroupApiMockTest", singleThreaded = true)
public class AWSSecurityGroupApiMockTest extends BaseAWSEC2ApiMockTest {

   private final String describeSecurityGroupsResponse = Joiner.on("\n").join(
         "<DescribeSecurityGroupsResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-11-15/\">",
         "  <requestId>59dbff89-35bd-4eac-99ed-be587EXAMPLE</requestId>",
         "  <securityGroupInfo>",
         "    <item>",
         "      <ownerId>123456789012</ownerId>",
         "      <groupId>sg-1a2b3c4d</groupId>",
         "      <groupName>WebServers</groupName>",
         "      <groupDescription>Web Servers</groupDescription>",
         "      <vpcId>vpc-614cc409</vpcId>",
         "      <ipPermissions>",
         "        <item>",
         "          <ipProtocol>-1</ipProtocol>",
         "          <groups>",
         "            <item>",
         "              <userId>123456789012</userId>",
         "              <groupId>sg-af8661c0</groupId>",
         "            </item>",
         "          </groups>",
         "          <ipRanges/>",
         "          <prefixListIds/>",
         "        </item>",
         "        <item>",
         "          <ipProtocol>tcp</ipProtocol>",
         "          <fromPort>22</fromPort>",
         "          <toPort>22</toPort>",
         "          <groups/>",
         "          <ipRanges>",
         "            <item>",
         "              <cidrIp>204.246.162.38/32</cidrIp>",
         "            </item>",
         "          </ipRanges>",
         "          <prefixListIds/>",
         "        </item>",
         "      </ipPermissions>",
         "      <ipPermissionsEgress>",
         "        <item>",
         "          <ipProtocol>-1</ipProtocol>",
         "          <groups/>",
         "          <ipRanges>",
         "            <item>",
         "              <cidrIp>0.0.0.0/0</cidrIp>",
         "            </item>",
         "          </ipRanges>",
         "          <prefixListIds/>",
         "        </item>",
         "      </ipPermissionsEgress>",
         "    </item>",
         "  </securityGroupInfo>",
         "</DescribeSecurityGroupsResponse>");

   private final String createSecurityGroupResponse = Joiner.on("\n").join(
         "<CreateSecurityGroupResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-11-15/\">",
         "  <requestId>59dbff89-35bd-4eac-99ed-be587EXAMPLE</requestId>",
         "  <return>true</return>",
         "  <groupId>sg-0a42d66a</groupId>",
         "</CreateSecurityGroupResponse>");

   private final String authorizeSecurityGroupIngressResponse = Joiner.on("\n").join(
            "<AuthorizeSecurityGroupIngressResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-11-15/\">",
            "  <requestId>59dbff89-35bd-4eac-99ed-be587EXAMPLE</requestId>",
            "  <return>true</return>",
            "</AuthorizeSecurityGroupIngressResponse>");

   private final String revokeSecurityGroupIngressResponse = Joiner.on("\n").join(
         "<RevokeSecurityGroupIngressResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-11-15/\">",
         "  <requestId>59dbff89-35bd-4eac-99ed-be587EXAMPLE</requestId>",
         "  <return>true</return>",
         "</RevokeSecurityGroupIngressResponse>");

   private final String deleteSecurityGroupResponse = Joiner.on("\n").join(
         "<DeleteSecurityGroupResponse xmlns=\"http://ec2.amazonaws.com/doc/2016-11-15/\">",
         "  <requestId>59dbff89-35bd-4eac-99ed-be587EXAMPLE</requestId>",
         "  <return>true</return>",
         "</DeleteSecurityGroupResponse>");

   @SuppressWarnings("deprecation")
   public void describeSecurityGroups() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(describeSecurityGroupsResponse));

      Set<SecurityGroup> results = securityGroupApi().describeSecurityGroupsInRegion(DEFAULT_REGION);
      SecurityGroup result = Iterables.getOnlyElement(results);
      assertEquals(result.getId(), "sg-1a2b3c4d");
      assertEquals(result.getRegion(), "us-east-1");
      assertEquals(result.getName(), "WebServers");
      assertEquals(result.getOwnerId(), "123456789012");
      assertEquals(result.getDescription(), "Web Servers");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups");
   }

   public void describeSecurityGroupsGiving404() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      Set<SecurityGroup> results = securityGroupApi().describeSecurityGroupsInRegion(DEFAULT_REGION);
      assertEquals(results, ImmutableSet.of());

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups");
   }

   public void describeSecurityGroupsById() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(describeSecurityGroupsResponse));

      Set<SecurityGroup> results = securityGroupApi().describeSecurityGroupsInRegionById(DEFAULT_REGION, "sg-1a2b3c4d");
      SecurityGroup result = Iterables.getOnlyElement(results);
      assertEquals(result.getId(), "sg-1a2b3c4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupId.1=sg-1a2b3c4d");
   }

   public void describeSecurityGroupsByName() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(describeSecurityGroupsResponse));

      Set<SecurityGroup> results = securityGroupApi().describeSecurityGroupsInRegion(DEFAULT_REGION, "WebServers");
      SecurityGroup result = Iterables.getOnlyElement(results);
      assertEquals(result.getId(), "sg-1a2b3c4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=WebServers");
   }

   public void describeSecurityGroupsFiltered() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(describeSecurityGroupsResponse));

      Set<SecurityGroup> results = securityGroupApi().describeSecurityGroupsInRegionWithFilter(DEFAULT_REGION, 
            ImmutableMultimap.of("group-name", "WebServers", "vpc-id", "vpc-614cc409"));
      SecurityGroup result = Iterables.getOnlyElement(results);
      assertEquals(result.getId(), "sg-1a2b3c4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&Filter.1.Name=group-name&Filter.1.Value.1=WebServers&Filter.2.Name=vpc-id&Filter.2.Value.1=vpc-614cc409");
   }

   public void describeSecurityGroupsDifferentRegion() throws Exception {
      String region = "us-west-2";
      enqueueRegions(DEFAULT_REGION, region);
      enqueue(region, new MockResponse().setBody(describeSecurityGroupsResponse));

      Set<SecurityGroup> results = securityGroupApi().describeSecurityGroupsInRegion(region);
      SecurityGroup result = Iterables.getOnlyElement(results);
      assertEquals(result.getId(), "sg-1a2b3c4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(region, "Action=DescribeSecurityGroups");
   }

   public void createSecurityGroupsInRegionAndReturnId() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(createSecurityGroupResponse));

      String result = securityGroupApi().createSecurityGroupInRegionAndReturnId(DEFAULT_REGION, "WebServers", "Web Servers", CreateSecurityGroupOptions.Builder.vpcId("vpc-614cc409"));
      assertEquals(result, "sg-0a42d66a");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateSecurityGroup&GroupName=WebServers&GroupDescription=Web%20Servers&VpcId=vpc-614cc409");
   }

   public void authorizeSecurityGroupIngress() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(authorizeSecurityGroupIngressResponse));

      IpPermission perm = IpPermission.builder().ipProtocol(IpProtocol.TCP).cidrBlock("0.0.0.0/0")
            .fromPort(8080).toPort(8080).build();
      securityGroupApi().authorizeSecurityGroupIngressInRegion(DEFAULT_REGION, "sg-1a2b3c4d", perm);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=AuthorizeSecurityGroupIngress&GroupId=sg-1a2b3c4d&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=8080&IpPermissions.0.ToPort=8080&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0");
   }

   public void authorizeSecurityGroupIngressList() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(authorizeSecurityGroupIngressResponse));

      IpPermission perm = IpPermission.builder().ipProtocol(IpProtocol.TCP).cidrBlock("0.0.0.0/0")
            .fromPort(8080).toPort(8080).build();
      IpPermission perm2 = IpPermission.builder().ipProtocol(IpProtocol.TCP).cidrBlock("0.0.0.0/0")
            .fromPort(8443).toPort(8443).build();
      securityGroupApi().authorizeSecurityGroupIngressInRegion(DEFAULT_REGION, "sg-1a2b3c4d", ImmutableList.of(perm, perm2));

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=AuthorizeSecurityGroupIngress&GroupId=sg-1a2b3c4d&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=8080&IpPermissions.0.ToPort=8080&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0&IpPermissions.1.IpProtocol=tcp&IpPermissions.1.FromPort=8443&IpPermissions.1.ToPort=8443&IpPermissions.1.IpRanges.0.CidrIp=0.0.0.0/0");
   }

   public void revokeSecurityGroupIngress() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(revokeSecurityGroupIngressResponse));

      IpPermission perm = IpPermission.builder().ipProtocol(IpProtocol.TCP).cidrBlock("0.0.0.0/0")
            .fromPort(8080).toPort(8080).build();
      securityGroupApi().revokeSecurityGroupIngressInRegion(DEFAULT_REGION, "sg-1a2b3c4d", perm);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=RevokeSecurityGroupIngress&GroupId=sg-1a2b3c4d&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=8080&IpPermissions.0.ToPort=8080&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0");
   }

   public void revokeSecurityGroupIngressList() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(revokeSecurityGroupIngressResponse));

      IpPermission perm = IpPermission.builder().ipProtocol(IpProtocol.TCP).cidrBlock("0.0.0.0/0")
            .fromPort(8080).toPort(8080).build();
      IpPermission perm2 = IpPermission.builder().ipProtocol(IpProtocol.TCP).cidrBlock("0.0.0.0/0")
            .fromPort(8443).toPort(8443).build();
      securityGroupApi().revokeSecurityGroupIngressInRegion(DEFAULT_REGION, "sg-1a2b3c4d", ImmutableList.of(perm, perm2));

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=RevokeSecurityGroupIngress&GroupId=sg-1a2b3c4d&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=8080&IpPermissions.0.ToPort=8080&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0&IpPermissions.1.IpProtocol=tcp&IpPermissions.1.FromPort=8443&IpPermissions.1.ToPort=8443&IpPermissions.1.IpRanges.0.CidrIp=0.0.0.0/0");
   }

   public void deleteSecurityGroups() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody(deleteSecurityGroupResponse));

      securityGroupApi().deleteSecurityGroupInRegionById(DEFAULT_REGION, "sg-1a2b3c4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeleteSecurityGroup&GroupId=sg-1a2b3c4d");
   }

   private AWSSecurityGroupApi securityGroupApi() {
      return api().getSecurityGroupApi().get();
   }
}
