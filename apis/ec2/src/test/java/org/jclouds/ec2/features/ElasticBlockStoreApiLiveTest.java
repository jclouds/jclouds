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
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.fromSnapshotId;
import static org.jclouds.ec2.options.CreateVolumeOptions.Builder.withSize;
import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.snapshotIds;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.SortedSet;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.domain.Volume.Status;
import org.jclouds.ec2.predicates.SnapshotCompleted;
import org.jclouds.ec2.predicates.VolumeAvailable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code ElasticBlockStoreApi}
 */
@Test(groups = "live", singleThreaded = true)
public class ElasticBlockStoreApiLiveTest extends BaseComputeServiceContextLiveTest {
   public ElasticBlockStoreApiLiveTest() {
      provider = "ec2";
   }

   protected EC2Api ec2Api;
   protected ElasticBlockStoreApi client;

   protected String defaultRegion;
   protected String defaultZone;

   protected String volumeId;
   protected Snapshot snapshot;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      ec2Api = view.unwrapApi(EC2Api.class);
      client = ec2Api.getElasticBlockStoreApi().get();
      AvailabilityZoneInfo info = Iterables.get(ec2Api.getAvailabilityZoneAndRegionApi().get()
            .describeAvailabilityZonesInRegion(defaultRegion), 0);
      defaultRegion = checkNotNull(Strings.emptyToNull(info.getRegion()), "region of " + info);
      defaultZone = checkNotNull(Strings.emptyToNull(info.getZone()), "zone of " + info);
   }

   @Test
   void testDescribeVolumes() {
      String region = defaultRegion;
      SortedSet<Volume> allResults = Sets.newTreeSet(client.describeVolumesInRegion(region));
      assertNotNull(allResults);
      assertFalse(allResults.isEmpty());
      Volume volume = allResults.last();
      SortedSet<Volume> result = Sets.newTreeSet(client.describeVolumesInRegion(region, volume.getId()));
      assertNotNull(result);
      Volume compare = result.last();
      assertEquals(compare, volume);
   }

   @Test
   void testDescribeVolumesWithFilter() {
      String region = defaultRegion;
      SortedSet<Volume> allResults = Sets.newTreeSet(client.describeVolumesInRegion(region));
      assertNotNull(allResults);
      assertFalse(allResults.isEmpty());
      Volume volume = allResults.last();
      SortedSet<Volume> result = Sets.newTreeSet(client.describeVolumesInRegionWithFilter(region,
              ImmutableMultimap.<String, String>builder()
                      .put("volume-id", volume.getId()).build()));
      assertNotNull(result);
      Volume compare = result.last();
      assertEquals(compare, volume);
   }

   @Test(expectedExceptions = AWSResponseException.class)
   void testDescribeVolumesWithInvalidFilter() {
      String region = defaultRegion;
      SortedSet<Volume> allResults = Sets.newTreeSet(client.describeVolumesInRegion(region));
      assertNotNull(allResults);
      assertFalse(allResults.isEmpty());
      Volume volume = allResults.last();
      client.describeVolumesInRegionWithFilter(region,
              ImmutableMultimap.<String, String>builder()
                      .put("invalid-filter", volume.getId()).build());
   }

   @Test
   void testCreateVolumeInAvailabilityZone() {
      Volume expected = client.createVolumeInAvailabilityZone(defaultZone,
              withSize(1));
      assertNotNull(expected);
      assertEquals(expected.getAvailabilityZone(), defaultZone);
      this.volumeId = expected.getId();

      Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(defaultRegion, expected.getId()));
      assertNotNull(result);
      assertEquals(result.size(), 1);
      Volume volume = result.iterator().next();
      assertEquals(volume.getId(), expected.getId());
      assertEquals(volume.getVolumeType(), expected.getVolumeType());
   }

   @Test(dependsOnMethods = "testCreateVolumeInAvailabilityZone")
   void testCreateSnapshotInRegion() {
      Snapshot snapshot = client.createSnapshotInRegion(defaultRegion, volumeId);
      Predicate<Snapshot> snapshotted = retry(new SnapshotCompleted(client), 600, 10, SECONDS);
      assert snapshotted.apply(snapshot);

      Snapshot result = Iterables.getOnlyElement(client.describeSnapshotsInRegion(defaultRegion,
            snapshotIds(snapshot.getId())));

      assertEquals(result.getProgress(), 100);
      this.snapshot = result;
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   void testCreateVolumeFromSnapshotInAvailabilityZone() {
      Volume volume = client.createVolumeFromSnapshotInAvailabilityZone(defaultZone, snapshot.getId());
      assertNotNull(volume);

      Predicate<Volume> availabile = retry(new VolumeAvailable(client), 600, 10, SECONDS);
      assert availabile.apply(volume);

      Volume result = Iterables.getOnlyElement(client.describeVolumesInRegion(defaultRegion, volume.getId()));
      assertEquals(volume.getId(), result.getId());
      assertEquals(volume.getSnapshotId(), snapshot.getId());
      assertEquals(volume.getAvailabilityZone(), defaultZone);
      assertEquals(result.getStatus(), Volume.Status.AVAILABLE);

      client.deleteVolumeInRegion(defaultRegion, result.getId());
   }

   @Test(dependsOnMethods = "testCreateVolumeFromSnapshotInAvailabilityZone")
   void testCreateVolumeFromSnapshotInAvailabilityZoneWithOptions() {
      Volume volume = client.createVolumeInAvailabilityZone(defaultZone,
              fromSnapshotId(snapshot.getId()));
      assertNotNull(volume);

      Predicate<Volume> availabile = retry(new VolumeAvailable(client), 600, 10, SECONDS);
      assert availabile.apply(volume);

      Volume result = Iterables.getOnlyElement(client.describeVolumesInRegion(defaultRegion, volume.getId()));
      assertEquals(volume.getId(), result.getId());
      assertEquals(volume.getSnapshotId(), snapshot.getId());
      assertEquals(volume.getAvailabilityZone(), defaultZone);
      assertEquals(result.getStatus(), Volume.Status.AVAILABLE);

      client.deleteVolumeInRegion(defaultRegion, result.getId());
   }

   @Test(dependsOnMethods = "testCreateVolumeFromSnapshotInAvailabilityZoneWithOptions")
   void testCreateVolumeFromSnapshotInAvailabilityZoneWithSize() {
      Volume volume = client.createVolumeFromSnapshotInAvailabilityZone(defaultZone, 2, snapshot.getId());
      assertNotNull(volume);

      Predicate<Volume> availabile = retry(new VolumeAvailable(client), 600, 10, SECONDS);
      assert availabile.apply(volume);

      Volume result = Iterables.getOnlyElement(client.describeVolumesInRegion(defaultRegion, volume.getId()));
      assertEquals(volume.getId(), result.getId());
      assertEquals(volume.getSnapshotId(), snapshot.getId());
      assertEquals(volume.getAvailabilityZone(), defaultZone);
      assertEquals(volume.getSize(), 2);
      assertEquals(result.getStatus(), Volume.Status.AVAILABLE);

      client.deleteVolumeInRegion(defaultRegion, result.getId());
   }

   @Test
   void testAttachVolumeInRegion() {
      // TODO: need an instance
   }

   @Test
   void testDetachVolumeInRegion() {
      // TODO: need an instance
   }

   @Test
   void testDescribeSnapshots() {
      String region = defaultRegion;
      SortedSet<Snapshot> allResults = Sets.newTreeSet(client.describeSnapshotsInRegion(region));
      assertNotNull(allResults);
      if (!allResults.isEmpty()) {
         Snapshot snapshot = allResults.last();
         Snapshot result = Iterables.getOnlyElement(client.describeSnapshotsInRegion(region,
                 snapshotIds(snapshot.getId())));
         assertNotNull(result);
         assertEquals(result, snapshot);
      }
   }

   @Test
   void testDescribeSnapshotsWithFilter() {
      String region = defaultRegion;
      SortedSet<Snapshot> allResults = Sets.newTreeSet(client.describeSnapshotsInRegion(region));
      assertNotNull(allResults);
      if (!allResults.isEmpty()) {
         Snapshot snapshot = allResults.last();
         Snapshot result = Iterables.getOnlyElement(client.describeSnapshotsInRegionWithFilter(region,
                 ImmutableMultimap.<String, String>builder()
                         .put("snapshot-id", snapshot.getId()).build()));
         assertNotNull(result);
         assertEquals(result, snapshot);
      }
   }

   @Test(expectedExceptions = AWSResponseException.class)
   void testDescribeSnapshotsWithFilterInvalid() {
      String region = defaultRegion;
      SortedSet<Snapshot> allResults = Sets.newTreeSet(client.describeSnapshotsInRegion(region));
      assertNotNull(allResults);
      if (!allResults.isEmpty()) {
         Snapshot snapshot = allResults.last();
         client.describeSnapshotsInRegionWithFilter(region,
                 ImmutableMultimap.<String, String>builder()
                         .put("invalid-filter", snapshot.getId()).build());
      }
   }

   @Test(enabled = false)
   public void testAddCreateVolumePermissionsToSnapshot() {
      // TODO client.addCreateVolumePermissionsToSnapshotInRegion(defaultRegion,
      // userIds,
      // userGroups,
      // snapshotId);
   }

   @Test(enabled = false)
   public void testRemoveCreateVolumePermissionsFromSnapshot() {
      // TODO
      // client.removeCreateVolumePermissionsFromSnapshotInRegion(defaultRegion,
      // userIds,
      // userGroups,
      // snapshotId);
   }

   @Test(enabled = false)
   public void testResetCreateVolumePermissionsOnSnapshot() {
      // TODO
      // client.resetCreateVolumePermissionsOnSnapshotInRegion(defaultRegion,
      // snapshotId);
   }

   @Test(dependsOnMethods = "testCreateVolumeFromSnapshotInAvailabilityZoneWithSize")
   public void testGetCreateVolumePermissionForSnapshot() {
      client.getCreateVolumePermissionForSnapshotInRegion(defaultRegion, snapshot.getId());
   }

   @Test(dependsOnMethods = "testGetCreateVolumePermissionForSnapshot")
   void testDeleteVolumeInRegion() {
      client.deleteVolumeInRegion(defaultRegion, volumeId);
      Set<Volume> volumes = client.describeVolumesInRegionWithFilter(defaultRegion, ImmutableMultimap
            .<String, String> builder().put("volume-id", volumeId).build());
      // The volume may not exist or remain in "deleting" state for a while
      Volume volume = getOnlyElement(volumes, null);
      assertTrue(volume == null || Status.DELETING == volume.getStatus());
   }

   @Test(dependsOnMethods = "testDeleteVolumeInRegion")
   void testDeleteSnapshotInRegion() {
      client.deleteSnapshotInRegion(defaultRegion, snapshot.getId());
      assert client.describeSnapshotsInRegion(defaultRegion, snapshotIds(snapshot.getId())).size() == 0;
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      try {
         client.deleteSnapshotInRegion(defaultRegion, snapshot.getId());
         client.deleteVolumeInRegion(defaultRegion, volumeId);
      } catch (Exception e) {
         // we don't really care about any exception here, so just delete away.
      }

      super.tearDownContext();
   }

}
