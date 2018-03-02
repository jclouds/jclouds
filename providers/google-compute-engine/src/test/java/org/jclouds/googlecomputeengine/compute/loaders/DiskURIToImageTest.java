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
package org.jclouds.googlecomputeengine.compute.loaders;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.jclouds.googlecomputeengine.compute.functions.Resources;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Disk.Status;
import org.jclouds.googlecomputeengine.domain.Image;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "DiskURIToImageTest")
public class DiskURIToImageTest {
   
   private static final URI DISK_URI = URI.create("http://localhost/disk/1");
   private static final URI IMAGE_URI = URI.create("http://localhost/image/1");
   
   public void testLoadNotFound() throws ExecutionException {
      Resources resources = mock(Resources.class);
      expect(resources.disk(DISK_URI)).andReturn(null);
      replay(resources);
      
      DiskURIToImage diskURIToImage = new DiskURIToImage(resources);
      assertFalse(diskURIToImage.load(DISK_URI).isPresent());
      
      verify(resources);
   }
   
   public void testImageSourceMissing() throws ExecutionException {
      Resources resources = mock(Resources.class);
      expect(resources.disk(DISK_URI)).andReturn(disk(null));
      replay(resources);
      
      DiskURIToImage diskURIToImage = new DiskURIToImage(resources);
      assertFalse(diskURIToImage.load(DISK_URI).isPresent());
      
      verify(resources);
   }
   
   public void testImageNotFound() throws ExecutionException {
      Resources resources = mock(Resources.class);
      expect(resources.disk(DISK_URI)).andReturn(disk(IMAGE_URI));
      expect(resources.image(IMAGE_URI)).andReturn(null);
      replay(resources);
      
      DiskURIToImage diskURIToImage = new DiskURIToImage(resources);
      assertFalse(diskURIToImage.load(DISK_URI).isPresent());
      
      verify(resources);
   }
   
   public void testImageFound() throws ExecutionException {
      Resources resources = mock(Resources.class);
      expect(resources.disk(DISK_URI)).andReturn(disk(IMAGE_URI));
      expect(resources.image(IMAGE_URI)).andReturn(image());
      replay(resources);
      
      DiskURIToImage diskURIToImage = new DiskURIToImage(resources);
      assertTrue(diskURIToImage.load(DISK_URI).isPresent());
      
      verify(resources);
   }
   
   private static Disk disk(URI sourceImage) {
      return Disk.create( //
            "13050421646334304115", // id
            new Date(), // creationTimestamp
            URI.create("http://localhost/zone"), // zone
            Status.READY, // status
            "testimage1", // name
            null, // description
            1, // sizeGb
            null, // sourceSnapshot
            null, // sourceSnapshotId
            DISK_URI, // selfLink
            sourceImage, // sourceImage
            null, // sourceImageId
            URI.create("http://localhost/type"), // type
            null // license
      );
   }
   
   private static Image image() {
      return Image.create( //
            "1234", // id
            IMAGE_URI, // selfLink
            new Date(), // creationTimestamp
            "", // name
            "", // description
            "RAW", // sourceType
            Image.RawDisk.create(URI.create("foo"), "TAR", null), // rawDisk
            null, // deprecated
            Image.Status.READY, // status
            Long.parseLong("881210631", 10), // archivedSizeBytes
            Long.parseLong("8", 10), // diskSizeGb
            DISK_URI.toString(), // sourceDisk
            "9598530021316715047", // sourceDiskId
            ImmutableList.of("https://www.googleapis.com/compute/v1/projects/suse-cloud/global/licenses/sles-12") // license
      );
   }
}
