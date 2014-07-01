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
package org.jclouds.ec2.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.addNewBlockDevice;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Image.ImageType;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.options.RegisterImageBackedByEbsOptions;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.ec2.predicates.SnapshotCompleted;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code AMIApi}
 */
@Test(groups = "live", singleThreaded = true)
public class AMIApiLiveTest extends BaseComputeServiceContextLiveTest {
   private TemplateBuilderSpec ebsTemplate;

   public AMIApiLiveTest() {
      provider = "ec2";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      String ebsSpec = checkNotNull(setIfTestSystemPropertyPresent(overrides, provider + ".ebs-template"), provider
            + ".ebs-template");
      ebsTemplate = TemplateBuilderSpec.parse(ebsSpec);
      return overrides;
   }

   protected EC2Api ec2Api;
   protected AMIApi client;

   protected Predicate<RunningInstance> runningTester;

   protected Set<String> imagesToDeregister = newHashSet();
   protected Set<String> snapshotsToDelete = newHashSet();
   protected String regionId;
   protected String ebsBackedImageId;
   protected String ebsBackedImageName = "jcloudstest1";
   protected String imageId;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      ec2Api = view.unwrapApi(EC2Api.class);
      runningTester = retry(new InstanceStateRunning(ec2Api), 600, 5, SECONDS);

      client = ec2Api.getAMIApi().get();
      if (ebsTemplate != null) {
         Template template = view.getComputeService().templateBuilder().from(ebsTemplate).build();
         regionId = template.getLocation().getId();
         imageId = template.getImage().getProviderId();
         for (Image image : client.describeImagesInRegionWithFilter(regionId,
                 ImmutableMultimap.<String, String>builder()
                         .put("name", ebsBackedImageName).build())) {
            if (ebsBackedImageName.equals(image.getName()))
               client.deregisterImageInRegion(regionId, image.getId());
         }
      }
   }

   public void testDescribeImageNotExists() {
      assertEquals(client.describeImagesInRegion(null, imageIds("ami-cdf819a3")).size(), 0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testDescribeImageBadId() {
      client.describeImagesInRegion(null, imageIds("asdaasdsa"));
   }

   public void testDescribeImages() {
      // Just run in the first region - no need to take the time on all of them.
      String region = getFirst(ec2Api.getConfiguredRegions(), null);
      assertNotNull(region, "region should not be null");
      Set<? extends Image> allResults = client.describeImagesInRegion(region);
      assertNotNull(allResults);
      assertTrue(allResults.size() >= 2);
      Iterator<? extends Image> iterator = allResults.iterator();
      String id1 = iterator.next().getId();
      String id2 = iterator.next().getId();
      Set<? extends Image> twoResults = client.describeImagesInRegion(region, imageIds(id1, id2));
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 2);
      iterator = twoResults.iterator();
      assertEquals(iterator.next().getId(), id1);
      assertEquals(iterator.next().getId(), id2);
   }

   @Test
   public void testDescribeImagesWithFilter() {
      // Just run in the first region - no need to take the time on all of them.
      String region = getFirst(ec2Api.getConfiguredRegions(), null);
      assertNotNull(region, "region should not be null");
      Set<? extends Image> allResults = client.describeImagesInRegion(region);
      assertNotNull(allResults);
      assertTrue(allResults.size() >= 2);
      String id1 = allResults.iterator().next().getId();
      Set<? extends Image> filterResult = client.describeImagesInRegionWithFilter(region,
              ImmutableMultimap.<String, String>builder()
                      .put("image-id", id1)
                      .build());
      assertNotNull(filterResult);
      assertEquals(filterResult.size(), 1);
      assertEquals(filterResult.iterator().next().getId(), id1);
   }

   @Test(expectedExceptions = AWSResponseException.class)
   public void testDescribeImagesWithInvalidFilter() {
      // Just run in the first region - no need to take the time on all of them.
      String region = getFirst(ec2Api.getConfiguredRegions(), null);
      assertNotNull(region, "region should not be null");

      Set<? extends Image> allResults = client.describeImagesInRegion(region);
      assertNotNull(allResults);
      assertTrue(allResults.size() >= 2);
      String id1 = allResults.iterator().next().getId();
      Set<? extends Image> filterResult = client.describeImagesInRegionWithFilter(region,
              ImmutableMultimap.<String, String>builder()
                      .put("invalid-filter-id", id1)
                      .build());
      assertNotNull(filterResult);
      assertEquals(filterResult.size(), 1);
      assertEquals(filterResult.iterator().next().getId(), id1);
   }

   @Test
   public void testCreateAndListEBSBackedImage() throws Exception {
      Snapshot snapshot = createSnapshot();

      // List of images before...
      int sizeBefore = client.describeImagesInRegionWithFilter(regionId,
              ImmutableMultimap.<String, String>builder()
                      .put("name", ebsBackedImageName).build()).size();

      // Register a new image...
      ebsBackedImageId = client.registerUnixImageBackedByEbsInRegion(regionId, ebsBackedImageName, snapshot.getId(),
              newBlockDeviceOption());
      imagesToDeregister.add(ebsBackedImageId);
      final Image ebsBackedImage = getOnlyElement(client.describeImagesInRegion(regionId, imageIds(ebsBackedImageId)));
      assertEquals(ebsBackedImage.getName(), ebsBackedImageName);
      assertEquals(ebsBackedImage.getImageType(), ImageType.MACHINE);
      assertEquals(ebsBackedImage.getRootDeviceType(), RootDeviceType.EBS);
      assertEquals(ebsBackedImage.getRootDeviceName(), "/dev/sda1");
      assertEquals(ebsBackedImage.getDescription(), "adrian");
      assertEquals(
            ebsBackedImage.getEbsBlockDevices().entrySet(),
            ImmutableMap.of("/dev/sda1", new Image.EbsBlockDevice(snapshot.getId(), snapshot.getVolumeSize(), true, null, null, false),
                  "/dev/sda2", newBlockDeviceInfo()).entrySet());

      int describeCount = 0;
      int after = 0;

      // This loop is in here to deal with a lag between image creation and it showing up in filtered describeImage queries.
      while (describeCount < 10 && after == 0) {
         describeCount++;
         Thread.sleep(30000);
         // List of images after - should be one larger than before
         after = client.describeImagesInRegionWithFilter(regionId,
                 ImmutableMultimap.<String, String>builder()
                         .put("name", ebsBackedImageName).build()).size();
      }
      assertEquals(after, sizeBefore + 1);
   }

   protected RegisterImageBackedByEbsOptions newBlockDeviceOption() {
      return addNewBlockDevice("/dev/sda2", "myvirtual", 5, false, null, null, false).withDescription("adrian");
   }

   protected Image.EbsBlockDevice newBlockDeviceInfo() {
      return new Image.EbsBlockDevice(null, 5, false, null, null, false);
   }

   // Fires up an instance, finds its root volume ID, takes a snapshot, then
   // terminates the instance.
   protected Snapshot createSnapshot() throws RunNodesException {

      String instanceId = null;
      try {
         RunningInstance instance = getOnlyElement(concat(ec2Api.getInstanceApi().get().runInstancesInRegion(
               regionId, null, imageId, 1, 1)));
         instanceId = instance.getId();
         
         assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");

         instance = getOnlyElement(concat(ec2Api.getInstanceApi().get().describeInstancesInRegion(regionId,
               instanceId)));
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

   @Test(dependsOnMethods = "testCreateAndListEBSBackedImage")
   public void testGetLaunchPermissionForImage() {
      client.getLaunchPermissionForImageInRegion(regionId, ebsBackedImageId);
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDownContext() {
      for (String imageId : imagesToDeregister)
         client.deregisterImageInRegion(regionId, imageId);
      for (String snapshotId : snapshotsToDelete)
         ec2Api.getElasticBlockStoreApi().get().deleteSnapshotInRegion(regionId, snapshotId);
      super.tearDownContext();
   }

}
