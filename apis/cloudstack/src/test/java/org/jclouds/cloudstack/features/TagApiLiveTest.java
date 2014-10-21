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
package org.jclouds.cloudstack.features;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import javax.annotation.Resource;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.Tag;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.internal.BaseCloudStackApiLiveTest;
import org.jclouds.cloudstack.options.CreateTagsOptions;
import org.jclouds.cloudstack.options.DeleteTagsOptions;
import org.jclouds.cloudstack.options.ListTagsOptions;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.logging.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TagApi}
 */
@Test(groups = "live", singleThreaded = true, testName = "TagApiLiveTest")
public class TagApiLiveTest extends BaseCloudStackApiLiveTest {

   @Resource Logger logger = Logger.NULL;

   protected String prefix = System.getProperty("user.name") + "-" + getClass().getSimpleName();
   protected String volumeToDelete;
   private String zoneId;

   @BeforeMethod(groups = "live")
   public void setZoneId() {
      Set<Zone> zones = client.getZoneApi().listZones();
      assertNotNull(zones);
      assertFalse(zones.isEmpty());
      zoneId = Iterables.get(zones, 0).getId();
   }

   public void testCreateTags() {
      createVolumeToTag();

      AsyncCreateResponse job = client.getTagApi().createTags(CreateTagsOptions.Builder.resourceType("Volume")
            .resourceIds(volumeToDelete)
            .tags(ImmutableMap.of(prefix + "-first-tag", "first-tag-value",
                  prefix + "-second-tag", "second-tag-value")));
      assertTrue(jobComplete.apply(job.getJobId()));
   }

   @Test(dependsOnMethods = "testCreateTags")
   public void testListTags() {
      Set<Tag> tags = client.getTagApi().listTags();
      assertNotNull(tags);
      assertFalse(tags.isEmpty());

      for (Tag tag : tags) {
         checkTag(tag);
      }
   }

   @Test(dependsOnMethods = "testListTags")
   public void testListSingleTag() {
      Set<Tag> tags = client.getTagApi().listTags(ListTagsOptions.Builder.key(prefix + "-second-tag"));
      assertNotNull(tags);
      assertFalse(tags.isEmpty());
      assertEquals(1, tags.size());
      for (Tag tag : tags) {
         assertEquals(volumeToDelete, tag.getResourceId());
         checkTag(tag);
      }
   }

   @Test(dependsOnMethods = "testListSingleTag")
   public void testListResourceByTag() {
      Set<Volume> volumes = client.getVolumeApi().listVolumes(ListVolumesOptions.Builder.tags(ImmutableMap.of(prefix + "-second-tag", "second-tag-value")));
      assertNotNull(volumes);
      assertFalse(volumes.isEmpty());
      assertEquals(1, volumes.size());
   }

   @Test(dependsOnMethods = "testListResourceByTag")
   public void testDeleteTags() {
      AsyncCreateResponse job = client.getTagApi().deleteTags(DeleteTagsOptions.Builder.resourceType("Volume")
            .resourceIds(volumeToDelete)
            .tags(ImmutableMap.of(prefix + "-first-tag", "first-tag-value",
                  prefix + "-second-tag", "second-tag-value")));
      assertTrue(jobComplete.apply(job.getJobId()));
   }

   static void checkTag(final Tag tag) {
      assertNotNull(tag.getAccount());
      assertNotNull(tag.getResourceId());
   }

   protected DiskOffering getPreferredDiskOffering() {
      for (DiskOffering candidate : client.getOfferingApi().listDiskOfferings()) {
         if (!candidate.isCustomized()) {
            return candidate;
         }
      }
      throw new AssertionError("No suitable DiskOffering found.");
   }

   protected void createVolumeToTag() {
      AsyncCreateResponse job = client.getVolumeApi().createVolumeFromDiskOfferingInZone(prefix + "-jclouds-volume",
            getPreferredDiskOffering().getId(), zoneId);
      assertTrue(jobComplete.apply(job.getJobId()));
      logger.info("created volume " + job.getId());
      volumeToDelete = job.getId();
   }

   @Override
   @AfterClass(groups = "live")
   protected void tearDownContext() {
      try {
         client.getVolumeApi().deleteVolume(volumeToDelete);
      } catch (Exception e) {
         // Don't really care if there's an exception here
      }
      super.tearDownContext();
   }

}
