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

import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.executableBy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.jclouds.aws.ec2.internal.BaseAWSEC2ApiMockTest;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Permission;
import org.jclouds.ec2.options.CreateImageOptions;
import org.jclouds.ec2.options.RegisterImageBackedByEbsOptions;
import org.jclouds.ec2.options.RegisterImageOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "AWSAMIApiMockTest", singleThreaded = true)
public class AWSAMIApiMockTest extends BaseAWSEC2ApiMockTest {

   public void describeImagesInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/amzn_images.xml");

      Set<? extends Image> result = amiApi().describeImagesInRegion(DEFAULT_REGION);

      assertFalse(result.isEmpty());

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages");
   }

   public void describeImagesInRegion_options() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/amzn_images.xml");

      Set<? extends Image> result = amiApi()
            .describeImagesInRegion(DEFAULT_REGION, executableBy("me").ownedBy("fred", "nancy").imageIds("1", "2"));

      assertFalse(result.isEmpty());

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages&ExecutableBy=me&Owner.1=fred&Owner.2=nancy&ImageId.1=1&ImageId.2=2");
   }

   public void describeImagesInRegion_404() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setResponseCode(404));

      Set<? extends Image> result = amiApi().describeImagesInRegion(DEFAULT_REGION);

      assertTrue(result.isEmpty());

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeImages");
   }

   public void createImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody("<CreateImageResponse><ImageId>ami-246f8d4d</ImageId></CreateImageResponse>"));

      String result = amiApi().createImageInRegion(DEFAULT_REGION, "name", "instanceId");
      assertEquals(result, "ami-246f8d4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateImage&Name=name&InstanceId=instanceId");
   }

   public void createImageInRegion_options() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION,
            new MockResponse().setBody("<CreateImageResponse><ImageId>ami-246f8d4d</ImageId></CreateImageResponse>"));

      String result = amiApi().createImageInRegion(DEFAULT_REGION, "name", "instanceId",
            new CreateImageOptions().withDescription("description").noReboot());
      assertEquals(result, "ami-246f8d4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=CreateImage&Name=name&InstanceId=instanceId&Description=description&NoReboot=true");
   }

   public void registerImageFromManifestInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody("<RegisterImageResponse><ImageId>ami-246f8d4d</ImageId></RegisterImageResponse>"));

      String result = amiApi().registerImageFromManifestInRegion(DEFAULT_REGION, "name", "pathToManifest");
      assertEquals(result, "ami-246f8d4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=RegisterImage&Name=name&ImageLocation=pathToManifest");
   }

   public void registerImageFromManifestInRegion_options() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody("<RegisterImageResponse><ImageId>ami-246f8d4d</ImageId></RegisterImageResponse>"));

      String result = amiApi().registerImageFromManifestInRegion(DEFAULT_REGION, "name", "pathToManifest",
            new RegisterImageOptions().withDescription("description"));
      assertEquals(result, "ami-246f8d4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=RegisterImage&Name=name&ImageLocation=pathToManifest&Description=description");
   }

   public void registerUnixImageBackedByEbsInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse()
            .setBody("<RegisterImageResponse><ImageId>ami-246f8d4d</ImageId></RegisterImageResponse>"));

      String result = amiApi().registerUnixImageBackedByEbsInRegion(DEFAULT_REGION, "imageName", "snapshotId");
      assertEquals(result, "ami-246f8d4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=RegisterImage&RootDeviceName=/dev/sda1&BlockDeviceMapping.0.DeviceName=/dev/sda1&Name=imageName&BlockDeviceMapping.0.Ebs.SnapshotId=snapshotId");
   }

   public void registerUnixImageBackedByEbsInRegion_options() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse().setBody("<RegisterImageResponse><ImageId>ami-246f8d4d</ImageId></RegisterImageResponse>"));

      String result = amiApi().registerUnixImageBackedByEbsInRegion(DEFAULT_REGION, "imageName", "snapshotId",
            new RegisterImageBackedByEbsOptions().withDescription("description")
                  .addBlockDeviceFromSnapshot("/dev/device", null, "snapshot", false, "gp2", null, false)
                  .addNewBlockDevice("/dev/newdevice", "newblock", 100));
      assertEquals(result, "ami-246f8d4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=RegisterImage&RootDeviceName=/dev/sda1&BlockDeviceMapping.0.DeviceName=/dev/sda1&Name=imageName&BlockDeviceMapping.0.Ebs.SnapshotId=snapshotId&Description=description&BlockDeviceMapping.1.Ebs.DeleteOnTermination=false&BlockDeviceMapping.1.Ebs.VolumeType=gp2&BlockDeviceMapping.1.DeviceName=/dev/device&BlockDeviceMapping.1.Ebs.SnapshotId=snapshot&BlockDeviceMapping.2.Ebs.DeleteOnTermination=false&BlockDeviceMapping.2.DeviceName=/dev/newdevice&BlockDeviceMapping.2.VirtualName=newblock&BlockDeviceMapping.2.Ebs.VolumeSize=100");
   }

   public void deregisterImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse());

      amiApi().deregisterImageInRegion(DEFAULT_REGION, "ami-246f8d4d");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DeregisterImage&ImageId=ami-246f8d4d");
   }

   public void getBlockDeviceMappingsForImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_image_attribute_blockDeviceMapping.xml");

      Map<String, Image.EbsBlockDevice> result = amiApi()
            .getBlockDeviceMappingsForImageInRegion(DEFAULT_REGION, "ami-246f8d4d");

      assertFalse(result.isEmpty());

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeImageAttribute&Attribute=blockDeviceMapping&ImageId=ami-246f8d4d");
   }

   public void getLaunchPermissionForImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_image_attribute_launchPermission.xml");

      Permission result = amiApi().getLaunchPermissionForImageInRegion(DEFAULT_REGION, "ami-246f8d4d");

      assertNotNull(result);

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeImageAttribute&Attribute=launchPermission&ImageId=ami-246f8d4d");
   }

   public void addLaunchPermissionsToImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse());

      amiApi().addLaunchPermissionsToImageInRegion(DEFAULT_REGION, ImmutableList.of("bob", "sue"),
            ImmutableList.of("all"), "imageId");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=ModifyImageAttribute&OperationType=add&Attribute=launchPermission&ImageId=imageId&UserId.1=bob&UserId.2=sue&UserGroup.1=all");
   }

   public void removeLaunchPermissionsFromImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse());

      amiApi().removeLaunchPermissionsFromImageInRegion(DEFAULT_REGION, ImmutableList.of("bob", "sue"),
            ImmutableList.of("all"), "imageId");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=ModifyImageAttribute&OperationType=remove&Attribute=launchPermission&ImageId=imageId&UserId.1=bob&UserId.2=sue&UserGroup.1=all");
   }

   public void resetLaunchPermissionsOnImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse());

      amiApi().resetLaunchPermissionsOnImageInRegion(DEFAULT_REGION, "imageId");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=ResetImageAttribute&Attribute=launchPermission&ImageId=imageId");
   }

   public void getProductCodesForImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueueXml(DEFAULT_REGION, "/describe_image_attribute_productCodes.xml");

      Set<String> result = amiApi().getProductCodesForImageInRegion(DEFAULT_REGION, "ami-246f8d4d");

      assertFalse(result.isEmpty());

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=DescribeImageAttribute&Attribute=productCodes&ImageId=ami-246f8d4d");
   }

   public void addProductCodesToImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse());

      amiApi().addProductCodesToImageInRegion(DEFAULT_REGION, ImmutableList.of("code1", "code2"), "imageId");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=ModifyImageAttribute&OperationType=add&Attribute=productCodes&ImageId=imageId&ProductCode.1=code1&ProductCode.2=code2");
   }

   public void removeProductCodesFromImageInRegion() throws Exception {
      enqueueRegions(DEFAULT_REGION);
      enqueue(DEFAULT_REGION, new MockResponse());

      amiApi().removeProductCodesFromImageInRegion(DEFAULT_REGION, ImmutableList.of("code1", "code2"), "imageId");

      assertPosted(DEFAULT_REGION, "Action=DescribeRegions");
      assertPosted(DEFAULT_REGION, "Action=ModifyImageAttribute&OperationType=remove&Attribute=productCodes&ImageId=imageId&ProductCode.1=code1&ProductCode.2=code2");
   }

   private AWSAMIApi amiApi() {
      return api().getAMIApi().get();
   }
}
