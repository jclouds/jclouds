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
package org.jclouds.aws.ec2.compute.extensions;

import static org.jclouds.domain.LocationScope.REGION;
import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

@Test(groups = "unit", testName = "AWSEC2SecurityGroupExtensionMockTest", singleThreaded = true)
public class AWSEC2SecurityGroupExtensionApiMockTest extends BaseAWSEC2ApiMockTest {

   SecurityGroup group = new SecurityGroupBuilder() //
         .id(DEFAULT_REGION + "/sg-3c6ef654") //
         .providerId("sg-3c6ef654") //
         .name("jclouds#some-group") //
         .ownerId("993194456877")
         .location(new LocationBuilder().scope(REGION).id(DEFAULT_REGION).description("").build()).build();

   IpPermission permByCidrBlock = IpPermission.builder() //
         .ipProtocol(IpProtocol.TCP) //
         .fromPort(22) //
         .toPort(40) //
         .cidrBlock("0.0.0.0/0").build();

   public void addIpPermissionCidrFromIpPermission() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/authorize_securitygroup_ingress_response.xml");
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_cidr.xml");
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");

      SecurityGroup newGroup = extension().addIpPermission(permByCidrBlock, group);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());
      assertEquals(newPerm, permByCidrBlock);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
            "Action=AuthorizeSecurityGroupIngress&GroupId=sg-3c6ef654&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=22&IpPermissions.0.ToPort=40&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupId.1=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
   }

   public void addIpPermissionCidrFromParams() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/authorize_securitygroup_ingress_response.xml");
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_cidr.xml");
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");

      SecurityGroup newGroup = extension()
            .addIpPermission(permByCidrBlock.getIpProtocol(), permByCidrBlock.getFromPort(),
                  permByCidrBlock.getToPort(), permByCidrBlock.getTenantIdGroupNamePairs(),
                  permByCidrBlock.getCidrBlocks(), permByCidrBlock.getGroupIds(), group);

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());
      assertEquals(newPerm, permByCidrBlock);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
            "Action=AuthorizeSecurityGroupIngress&GroupId=sg-3c6ef654&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=22&IpPermissions.0.ToPort=40&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupId.1=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
   }

   IpPermission permByGroup = IpPermission.builder() //
         .ipProtocol(IpProtocol.TCP) //
         .fromPort(22) //
         .toPort(40) //
         .tenantIdGroupNamePair(group.getOwnerId(), group.getProviderId()).build();

   public void addIpPermissionGroupFromIpPermission() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/authorize_securitygroup_ingress_response.xml");
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_group.xml");
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");

      SecurityGroup newGroup = extension().addIpPermission(permByGroup, group);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());
      assertEquals(newPerm, permByGroup);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
            "Action=AuthorizeSecurityGroupIngress&GroupId=sg-3c6ef654&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=22&IpPermissions.0.ToPort=40&IpPermissions.0.Groups.0.UserId=993194456877&IpPermissions.0.Groups.0.GroupId=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupId.1=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
   }

   public void addIpPermissionGroupFromParams() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/authorize_securitygroup_ingress_response.xml");
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_group.xml");
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");

      SecurityGroup newGroup = extension()
            .addIpPermission(permByGroup.getIpProtocol(), permByGroup.getFromPort(), permByGroup.getToPort(),
                  permByGroup.getTenantIdGroupNamePairs(), permByGroup.getCidrBlocks(), permByGroup.getGroupIds(),
                  group);

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());
      assertEquals(newPerm, permByGroup);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
            "Action=AuthorizeSecurityGroupIngress&GroupId=sg-3c6ef654&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=22&IpPermissions.0.ToPort=40&IpPermissions.0.Groups.0.UserId=993194456877&IpPermissions.0.Groups.0.GroupId=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupId.1=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
   }

   public void createSecurityGroup() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/created_securitygroup.xml");
      // TODO: ridiculously chatty
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_single.xml");
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_single.xml");
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_single.xml");
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_single.xml");
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");

      SecurityGroup newGroup = extension()
            .createSecurityGroup(group.getName().replace("jclouds#", ""), group.getLocation());
      assertEquals(newGroup.getId(), group.getId());
      assertEquals(newGroup.getProviderId(), group.getProviderId());
      assertEquals(newGroup.getName(), group.getName());
      assertEquals(newGroup.getLocation().getId(), group.getLocation().getId()); // One from response has a parent

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION,
            "Action=CreateSecurityGroup&GroupName=jclouds%23some-group&GroupDescription=jclouds%23some-group");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23some-group");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23some-group");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23some-group");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupId.1=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
   }

   private SecurityGroupExtension extension() {
      return computeService().getSecurityGroupExtension().get();
   }
}
