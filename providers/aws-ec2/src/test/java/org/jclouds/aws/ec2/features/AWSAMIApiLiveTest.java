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

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.filters;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.addNewBlockDevice;
import static org.jclouds.ec2.options.RunInstancesOptions.Builder.withBlockDeviceMappings;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Properties;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.compute.RunNodesException;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.features.AMIApiLiveTest;
import org.jclouds.ec2.options.RegisterImageBackedByEbsOptions;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.predicates.SnapshotCompleted;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AMIApi}
 */
@Test(groups = "live", singleThreaded = true)
public class AWSAMIApiLiveTest extends AMIApiLiveTest {

   public AWSAMIApiLiveTest() {
      provider = "aws-ec2";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.put(Constants.PROPERTY_API_VERSION, "2014-05-01");
      return overrides;
   }

   public void testDescribeImagesCC() {
      Set<? extends Image> ccResults = client.describeImagesInRegion(Region.US_EAST_1,
            filters(ImmutableMultimap.<String, String> builder()//
                  .put("virtualization-type", "hvm")//
                  .put("architecture", "x86_64")//
                  .putAll("owner-id", ImmutableSet.<String> of("137112412989", "099720109477"))//
                  .put("hypervisor", "xen")//
                  .put("state", "available")//
                  .put("image-type", "machine")//
                  .put("root-device-type", "ebs")//
                  .build()).ownedBy("137112412989", "099720109477"));
      assertNotNull(ccResults);
      assert ccResults.size() >= 34 : ccResults;
   }

   protected RegisterImageBackedByEbsOptions newBlockDeviceOption() {
      return addNewBlockDevice("/dev/sda2", "myvirtual", 5, false, "gp2", null, false).withDescription("adrian");
   }

   protected Image.EbsBlockDevice newBlockDeviceInfo() {
      return new Image.EbsBlockDevice(null, 5, false, "gp2", null, false);
   }


   @Override
   protected Snapshot createSnapshot() throws RunNodesException {

      String instanceId = null;
      try {
         BlockDeviceMapping mapping = new BlockDeviceMapping.MapNewVolumeToDevice("/dev/sdb", 1, true, "gp2", null, false);
         RunInstancesOptions options = withBlockDeviceMappings(ImmutableSet
                 .<BlockDeviceMapping> of(mapping));

         RunningInstance instance = getOnlyElement(concat(ec2Api.getInstanceApi().get().runInstancesInRegion(
                 regionId, null, imageId, 1, 1, options)));
         instanceId = instance.getId();

         assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");

         instance = getOnlyElement(concat(ec2Api.getInstanceApi().get().describeInstancesInRegion(regionId,
                 instanceId)));
         BlockDevice gp2Device = instance.getEbsBlockDevices().get("/dev/sdb");
         assertNotNull(gp2Device, "device /dev/sdb not present on " + instance);
         Volume gp2Volume = Iterables.getOnlyElement(ec2Api.getElasticBlockStoreApi().get().describeVolumesInRegion(regionId, gp2Device.getVolumeId()));
         assertNotNull(gp2Volume, "/dev/sdb volume is null");
         assertEquals(gp2Volume.getVolumeType(), "gp2");
         BlockDevice device = instance.getEbsBlockDevices().get("/dev/sda1");
         assertNotNull(device, "device: /dev/sda1 not present on: " + instance);
         Snapshot snapshot = ec2Api.getElasticBlockStoreApi().get().createSnapshotInRegion(regionId,
                 device.getVolumeId());
         snapshotsToDelete.add(snapshot.getId());
         Predicate<Snapshot> snapshotted = retry(new SnapshotCompleted(ec2Api.getElasticBlockStoreApi().get()), 600, 10, SECONDS);
         assert snapshotted.apply(snapshot);
         return snapshot;
      } finally {
         if (instanceId != null)
            ec2Api.getInstanceApi().get().terminateInstancesInRegion(regionId, instanceId);
      }
   }

}
