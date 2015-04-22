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
package org.jclouds.aws.ec2.compute;

import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.blockUntilRunning;
import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "AWSEC2ComputeServiceMockTest", singleThreaded = true)
public class AWSEC2ComputeServiceApiMockTest extends BaseAWSEC2ApiMockTest {

   protected String getDefaultSmallestInstanceType() {
      // NOT t2.xxx because that requires a VPC
      return "m3.medium";
   }
     
   protected String getDefaultParavirtualInstanceType() {
      // smallest non-deprecated instance type supporting paravirtual
       return "m3.medium";
   }

   protected String getDefaultImageId() {
       return "be3adfd7";
   }
   
   public void launchVPCSpotInstanceSubnetId() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images_cc.xml");
      enqueueXml(DEFAULT_REGION, "/request_spot_instances-ebs.xml");
      enqueueXml(DEFAULT_REGION, "/request_spot_instances-ebs.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images_ebs.xml");
      enqueue(DEFAULT_REGION, new MockResponse()); // create tags

      ComputeService computeService = computeService();

      Template template = computeService.templateBuilder().locationId("us-east-1a").build();

      template.getOptions().as(AWSEC2TemplateOptions.class)
            .spotPrice(1f).subnetId("subnet-xyz").keyPair("Demo").blockUntilRunning(false);

      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test", 1, template));
      assertEquals(node.getId(), "us-east-1/sir-228e6406");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=owner-id&Filter.1.Value.1=137112412989&Filter.1.Value.2=801119661308&Filter.1.Value.3=063491364108&Filter.1.Value.4=099720109477&Filter.1.Value.5=411009282317&Filter.2.Name=state&Filter.2.Value.1=available&Filter.3.Name=image-type&Filter.3.Value.1=machine");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=virtualization-type&Filter.1.Value.1=hvm&Filter.2.Name=architecture&Filter.2.Value.1=x86_64&Filter.3.Name=owner-id&Filter.3.Value.1=137112412989&Filter.3.Value.2=099720109477&Filter.4.Name=hypervisor&Filter.4.Value.1=xen&Filter.5.Name=state&Filter.5.Value.1=available&Filter.6.Name=image-type&Filter.6.Value.1=machine&Filter.7.Name=root-device-type&Filter.7.Value.1=ebs");
      assertPosted(DEFAULT_REGION, "Action=RequestSpotInstances&SpotPrice=1.0&InstanceCount=1&LaunchSpecification.ImageId=ami-" + getDefaultImageId() + "&LaunchSpecification.Placement.AvailabilityZone=us-east-1a&LaunchSpecification.InstanceType=" + getDefaultSmallestInstanceType() + "&LaunchSpecification.SubnetId=subnet-xyz&LaunchSpecification.KeyName=Demo&LaunchSpecification.UserData=I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK");
      assertPosted(DEFAULT_REGION, "Action=DescribeSpotInstanceRequests&SpotInstanceRequestId.1=sir-228e6406");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&ImageId.1=ami-595a0a1c");
      assertPosted(DEFAULT_REGION, "Action=CreateTags&Tag.1.Key=Name&Tag.1.Value=test-228e6406&ResourceId.1=sir-228e6406");
   }

   String iamInstanceProfileArn = "arn:aws:iam::123456789012:instance-profile/application_abc/component_xyz/Webserver";

   public void launchSpotInstanceIAMInstanceProfileArn() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images_cc.xml");
      enqueueXml(DEFAULT_REGION, "/created_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/authorize_securitygroup_ingress_response.xml");
      enqueueXml(DEFAULT_REGION, "/request_spot_instances-ebs.xml");
      enqueueXml(DEFAULT_REGION, "/request_spot_instances-ebs.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images_ebs.xml");
      enqueue(DEFAULT_REGION, new MockResponse()); // create tags

      ComputeService computeService = computeService();

      Template template = computeService.templateBuilder().locationId("us-east-1a").build();

      template.getOptions().as(AWSEC2TemplateOptions.class).spotPrice(1f).iamInstanceProfileArn(iamInstanceProfileArn)
            .noKeyPair().blockUntilRunning(false);

      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test", 1, template));
      assertEquals(node.getId(), "us-east-1/sir-228e6406");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=owner-id&Filter.1.Value.1=137112412989&Filter.1.Value.2=801119661308&Filter.1.Value.3=063491364108&Filter.1.Value.4=099720109477&Filter.1.Value.5=411009282317&Filter.2.Name=state&Filter.2.Value.1=available&Filter.3.Name=image-type&Filter.3.Value.1=machine");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=virtualization-type&Filter.1.Value.1=hvm&Filter.2.Name=architecture&Filter.2.Value.1=x86_64&Filter.3.Name=owner-id&Filter.3.Value.1=137112412989&Filter.3.Value.2=099720109477&Filter.4.Name=hypervisor&Filter.4.Value.1=xen&Filter.5.Name=state&Filter.5.Value.1=available&Filter.6.Name=image-type&Filter.6.Value.1=machine&Filter.7.Name=root-device-type&Filter.7.Value.1=ebs");
      assertPosted(DEFAULT_REGION, "Action=CreateSecurityGroup&GroupName=jclouds%23test&GroupDescription=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=AuthorizeSecurityGroupIngress&GroupId=sg-3c6ef654&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=22&IpPermissions.0.ToPort=22&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0&IpPermissions.1.IpProtocol=tcp&IpPermissions.1.FromPort=0&IpPermissions.1.ToPort=65535&IpPermissions.1.Groups.0.UserId=993194456877&IpPermissions.1.Groups.0.GroupId=sg-3c6ef654&IpPermissions.2.IpProtocol=udp&IpPermissions.2.FromPort=0&IpPermissions.2.ToPort=65535&IpPermissions.2.Groups.0.UserId=993194456877&IpPermissions.2.Groups.0.GroupId=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=RequestSpotInstances&SpotPrice=1.0&InstanceCount=1&LaunchSpecification.ImageId=ami-" + getDefaultImageId() + "&LaunchSpecification.Placement.AvailabilityZone=us-east-1a&LaunchSpecification.SecurityGroup.1=jclouds%23test&LaunchSpecification.InstanceType=" + getDefaultSmallestInstanceType() + "&LaunchSpecification.UserData=I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK&LaunchSpecification.IamInstanceProfile.Arn=arn%3Aaws%3Aiam%3A%3A123456789012%3Ainstance-profile/application_abc/component_xyz/Webserver");
      assertPosted(DEFAULT_REGION, "Action=DescribeSpotInstanceRequests&SpotInstanceRequestId.1=sir-228e6406");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&ImageId.1=ami-595a0a1c");
      assertPosted(DEFAULT_REGION, "Action=CreateTags&Tag.1.Key=Name&Tag.1.Value=test-228e6406&ResourceId.1=sir-228e6406");
   }

   public void launchSpotInstanceIAMInstanceProfileName() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images_cc.xml");
      enqueueXml(DEFAULT_REGION, "/created_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/authorize_securitygroup_ingress_response.xml");
      enqueueXml(DEFAULT_REGION, "/request_spot_instances-ebs.xml");
      enqueueXml(DEFAULT_REGION, "/request_spot_instances-ebs.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images_ebs.xml");
      enqueue(DEFAULT_REGION, new MockResponse()); // create tags

      ComputeService computeService = computeService();

      Template template = computeService.templateBuilder().locationId("us-east-1a").build();

      template.getOptions().as(AWSEC2TemplateOptions.class).spotPrice(1f).iamInstanceProfileName("Webserver")
            .noKeyPair().blockUntilRunning(false);

      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test", 1, template));
      assertEquals(node.getId(), "us-east-1/sir-228e6406");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=owner-id&Filter.1.Value.1=137112412989&Filter.1.Value.2=801119661308&Filter.1.Value.3=063491364108&Filter.1.Value.4=099720109477&Filter.1.Value.5=411009282317&Filter.2.Name=state&Filter.2.Value.1=available&Filter.3.Name=image-type&Filter.3.Value.1=machine");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=virtualization-type&Filter.1.Value.1=hvm&Filter.2.Name=architecture&Filter.2.Value.1=x86_64&Filter.3.Name=owner-id&Filter.3.Value.1=137112412989&Filter.3.Value.2=099720109477&Filter.4.Name=hypervisor&Filter.4.Value.1=xen&Filter.5.Name=state&Filter.5.Value.1=available&Filter.6.Name=image-type&Filter.6.Value.1=machine&Filter.7.Name=root-device-type&Filter.7.Value.1=ebs");
      assertPosted(DEFAULT_REGION, "Action=CreateSecurityGroup&GroupName=jclouds%23test&GroupDescription=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=AuthorizeSecurityGroupIngress&GroupId=sg-3c6ef654&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=22&IpPermissions.0.ToPort=22&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0&IpPermissions.1.IpProtocol=tcp&IpPermissions.1.FromPort=0&IpPermissions.1.ToPort=65535&IpPermissions.1.Groups.0.UserId=993194456877&IpPermissions.1.Groups.0.GroupId=sg-3c6ef654&IpPermissions.2.IpProtocol=udp&IpPermissions.2.FromPort=0&IpPermissions.2.ToPort=65535&IpPermissions.2.Groups.0.UserId=993194456877&IpPermissions.2.Groups.0.GroupId=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=RequestSpotInstances&SpotPrice=1.0&InstanceCount=1&LaunchSpecification.ImageId=ami-" + getDefaultImageId() + "&LaunchSpecification.Placement.AvailabilityZone=us-east-1a&LaunchSpecification.SecurityGroup.1=jclouds%23test&LaunchSpecification.InstanceType=" + getDefaultSmallestInstanceType() + "&LaunchSpecification.UserData=I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK&LaunchSpecification.IamInstanceProfile.Name=Webserver");
      assertPosted(DEFAULT_REGION, "Action=DescribeSpotInstanceRequests&SpotInstanceRequestId.1=sir-228e6406");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&ImageId.1=ami-595a0a1c");
      assertPosted(DEFAULT_REGION, "Action=CreateTags&Tag.1.Key=Name&Tag.1.Value=test-228e6406&ResourceId.1=sir-228e6406");
   }

   public void createNodeWithIAMInstanceProfileArn() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/amzn_images.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images_cc.xml");
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");
      enqueueXml(DEFAULT_REGION, "/created_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/authorize_securitygroup_ingress_response.xml");
      enqueueXml(DEFAULT_REGION, "/new_instance.xml");
      enqueueXml(DEFAULT_REGION, "/describe_instances_running-1.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images.xml");
      enqueue(DEFAULT_REGION, new MockResponse()); // create tags

      ComputeService computeService = computeService();

      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test", 1,
            blockUntilRunning(false).iamInstanceProfileArn(iamInstanceProfileArn).noKeyPair()));
      assertEquals(node.getId(), "us-east-1/i-2baa5550");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=owner-id&Filter.1.Value.1=137112412989&Filter.1.Value.2=801119661308&Filter.1.Value.3=063491364108&Filter.1.Value.4=099720109477&Filter.1.Value.5=411009282317&Filter.2.Name=state&Filter.2.Value.1=available&Filter.3.Name=image-type&Filter.3.Value.1=machine");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=virtualization-type&Filter.1.Value.1=hvm&Filter.2.Name=architecture&Filter.2.Value.1=x86_64&Filter.3.Name=owner-id&Filter.3.Value.1=137112412989&Filter.3.Value.2=099720109477&Filter.4.Name=hypervisor&Filter.4.Value.1=xen&Filter.5.Name=state&Filter.5.Value.1=available&Filter.6.Name=image-type&Filter.6.Value.1=machine&Filter.7.Name=root-device-type&Filter.7.Value.1=ebs");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
      assertPosted(DEFAULT_REGION, "Action=CreateSecurityGroup&GroupName=jclouds%23test&GroupDescription=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=AuthorizeSecurityGroupIngress&GroupId=sg-3c6ef654&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=22&IpPermissions.0.ToPort=22&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0&IpPermissions.1.IpProtocol=tcp&IpPermissions.1.FromPort=0&IpPermissions.1.ToPort=65535&IpPermissions.1.Groups.0.UserId=993194456877&IpPermissions.1.Groups.0.GroupId=sg-3c6ef654&IpPermissions.2.IpProtocol=udp&IpPermissions.2.FromPort=0&IpPermissions.2.ToPort=65535&IpPermissions.2.Groups.0.UserId=993194456877&IpPermissions.2.Groups.0.GroupId=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=RunInstances&ImageId=ami-8ce4b5c9&MinCount=1&MaxCount=1&InstanceType=" + getDefaultParavirtualInstanceType() + "&SecurityGroup.1=jclouds%23test&UserData=I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK&IamInstanceProfile.Arn=arn%3Aaws%3Aiam%3A%3A123456789012%3Ainstance-profile/application_abc/component_xyz/Webserver");
      assertPosted(DEFAULT_REGION, "Action=DescribeInstances&InstanceId.1=i-2baa5550");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&ImageId.1=ami-aecd60c7");
      assertPosted(DEFAULT_REGION, "Action=CreateTags&Tag.1.Key=Name&Tag.1.Value=test-2baa5550&ResourceId.1=i-2baa5550");
   }

   public void createNodeWithIAMInstanceProfileName() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/amzn_images.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images_cc.xml");
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");
      enqueueXml(DEFAULT_REGION, "/created_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/new_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/authorize_securitygroup_ingress_response.xml");
      enqueueXml(DEFAULT_REGION, "/new_instance.xml");
      enqueueXml(DEFAULT_REGION, "/describe_instances_running-1.xml");
      enqueueXml(DEFAULT_REGION, "/describe_images.xml");
      enqueue(DEFAULT_REGION, new MockResponse()); // create tags

      ComputeService computeService = computeService();

      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test", 1,
            blockUntilRunning(false).iamInstanceProfileName("Webserver").noKeyPair()));
      assertEquals(node.getId(), "us-east-1/i-2baa5550");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=owner-id&Filter.1.Value.1=137112412989&Filter.1.Value.2=801119661308&Filter.1.Value.3=063491364108&Filter.1.Value.4=099720109477&Filter.1.Value.5=411009282317&Filter.2.Name=state&Filter.2.Value.1=available&Filter.3.Name=image-type&Filter.3.Value.1=machine");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&Filter.1.Name=virtualization-type&Filter.1.Value.1=hvm&Filter.2.Name=architecture&Filter.2.Value.1=x86_64&Filter.3.Name=owner-id&Filter.3.Value.1=137112412989&Filter.3.Value.2=099720109477&Filter.4.Name=hypervisor&Filter.4.Value.1=xen&Filter.5.Name=state&Filter.5.Value.1=available&Filter.6.Name=image-type&Filter.6.Value.1=machine&Filter.7.Name=root-device-type&Filter.7.Value.1=ebs");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
      assertPosted(DEFAULT_REGION, "Action=CreateSecurityGroup&GroupName=jclouds%23test&GroupDescription=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23test");
      assertPosted(DEFAULT_REGION, "Action=AuthorizeSecurityGroupIngress&GroupId=sg-3c6ef654&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=22&IpPermissions.0.ToPort=22&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0/0&IpPermissions.1.IpProtocol=tcp&IpPermissions.1.FromPort=0&IpPermissions.1.ToPort=65535&IpPermissions.1.Groups.0.UserId=993194456877&IpPermissions.1.Groups.0.GroupId=sg-3c6ef654&IpPermissions.2.IpProtocol=udp&IpPermissions.2.FromPort=0&IpPermissions.2.ToPort=65535&IpPermissions.2.Groups.0.UserId=993194456877&IpPermissions.2.Groups.0.GroupId=sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=RunInstances&ImageId=ami-8ce4b5c9&MinCount=1&MaxCount=1&InstanceType=" + getDefaultParavirtualInstanceType() + "&SecurityGroup.1=jclouds%23test&UserData=I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK&IamInstanceProfile.Name=Webserver");
      assertPosted(DEFAULT_REGION, "Action=DescribeInstances&InstanceId.1=i-2baa5550");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&ImageId.1=ami-aecd60c7");
      assertPosted(DEFAULT_REGION, "Action=CreateTags&Tag.1.Key=Name&Tag.1.Value=test-2baa5550&ResourceId.1=i-2baa5550");
   }

   public void listNodesWhereImageDoesntExist() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_instances_running-1.xml");
      enqueueXml(DEFAULT_REGION, "/availabilityZones.xml");
      enqueue(DEFAULT_REGION, new MockResponse().setBody("<DescribeImagesResponse><imagesSet></imagesSet></DescribeImagesResponse>"));
      enqueue(DEFAULT_REGION, new MockResponse().setBody("<DescribeSpotInstanceRequestsResponse><spotInstanceRequestSet></spotInstanceRequestSet></DescribeSpotInstanceRequestsResponse>"));

      ComputeService computeService = computeService();

      NodeMetadata node = Iterables.getOnlyElement(computeService.listNodesDetailsMatching(NodePredicates.all()));
      assertEquals(node.getId(), "us-east-1/i-2baa5550");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeInstances");
      assertPosted(DEFAULT_REGION, "Action=DescribeAvailabilityZones");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&ImageId.1=ami-aecd60c7");
      assertPosted(DEFAULT_REGION, "Action=DescribeSpotInstanceRequests");
   }
   
   public void deleteIncidentalResourcesSuccessfully() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_single.xml");
      enqueueXml(DEFAULT_REGION, "/delete_securitygroup.xml");
      enqueueXml(DEFAULT_REGION, "/describe_keypairs_jcloudssingle.xml");
      enqueueXml(DEFAULT_REGION, "/describe_instances_empty.xml");
      enqueueXml(DEFAULT_REGION, "/delete_keypair.xml");
      enqueueXml(DEFAULT_REGION, "/describe_placement_groups.xml");
      enqueueXml(DEFAULT_REGION, "/delete_placementgroup.xml");
      enqueueXml(DEFAULT_REGION, "/describe_placement_groups_empty.xml");

      AWSEC2ComputeService computeService = (AWSEC2ComputeService) computeService();

      computeService.cleanUpIncidentalResources(DEFAULT_REGION, "sg-3c6ef654");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DeleteSecurityGroup&GroupName=jclouds%23sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribeKeyPairs&Filter.1.Name=key-name&Filter.1.Value.1=jclouds%23sg-3c6ef654%23%2A");
      assertPosted(DEFAULT_REGION, "Action=DescribeInstances&Filter.1.Name=instance-state-name&Filter.1.Value.1=terminated&Filter.1.Value.2=shutting-down&Filter.2.Name=key-name&Filter.2.Value.1=jclouds%23sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DeleteKeyPair&KeyName=jclouds%23sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribePlacementGroups&GroupName.1=jclouds%23sg-3c6ef654%23us-east-1");
      assertPosted(DEFAULT_REGION, "Action=DeletePlacementGroup&GroupName=jclouds%23sg-3c6ef654%23us-east-1");
      assertPosted(DEFAULT_REGION, "Action=DescribePlacementGroups&GroupName.1=jclouds%23sg-3c6ef654%23us-east-1");
   }

   public void deleteIncidentalResourcesGivingDependencyViolationForSecurityGroup() throws Exception {
      runDeleteIncidentalResourcesGivingErrForSecurityGroup("DependencyViolation");
   }
   
   public void deleteIncidentalResourcesGivingInUseForSecurityGroup() throws Exception {
      runDeleteIncidentalResourcesGivingErrForSecurityGroup("InvalidGroup.InUse");
   }
   
   protected void runDeleteIncidentalResourcesGivingErrForSecurityGroup(String errCode) throws Exception {
      // Does not return delete_securitygroup.xml, but instead gives a 400 error.
      // Because super.builder has set TIMEOUT_CLEANUP_INCIDENTAL_RESOURCES to 0, it will not retry.

      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_securitygroups_extension_single.xml");
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(400).setBody("<Response><Errors><Error><Code>" + errCode + "</Code><Message>resource sg-3c6ef654 has a dependent object</Message></Error></Errors><RequestID>e4f4c78f-4455-43dd-b5cb-9af0bc4bc804</RequestID></Response>"));
      enqueueXml(DEFAULT_REGION, "/describe_placement_groups.xml");
      enqueueXml(DEFAULT_REGION, "/delete_placementgroup.xml");
      enqueueXml(DEFAULT_REGION, "/describe_placement_groups_empty.xml");

      AWSEC2ComputeService computeService = (AWSEC2ComputeService) computeService();

      computeService.cleanUpIncidentalResources(DEFAULT_REGION, "sg-3c6ef654");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeSecurityGroups&GroupName.1=jclouds%23sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DeleteSecurityGroup&GroupName=jclouds%23sg-3c6ef654");
      assertPosted(DEFAULT_REGION, "Action=DescribePlacementGroups&GroupName.1=jclouds%23sg-3c6ef654%23us-east-1");
      assertPosted(DEFAULT_REGION, "Action=DeletePlacementGroup&GroupName=jclouds%23sg-3c6ef654%23us-east-1");
      assertPosted(DEFAULT_REGION, "Action=DescribePlacementGroups&GroupName.1=jclouds%23sg-3c6ef654%23us-east-1");
   }
}
