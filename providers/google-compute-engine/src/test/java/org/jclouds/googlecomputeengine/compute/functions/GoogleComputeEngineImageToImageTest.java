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
package org.jclouds.googlecomputeengine.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.net.URI;

import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.Deprecated;
import org.jclouds.googlecomputeengine.domain.Deprecated.State;
import org.jclouds.googlecomputeengine.domain.Image;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "GoogleComputeEngineImageToImageTest")
public class GoogleComputeEngineImageToImageTest {
   public void testArbitraryImageName() {
      GoogleComputeEngineImageToImage imageToImage = new GoogleComputeEngineImageToImage(new ImageNameToOperatingSystem());
      Image image = image("arbitratyname", null);
      org.jclouds.compute.domain.Image transformed = imageToImage.apply(image);
      assertEquals(transformed.getName(), image.name());
      assertEquals(transformed.getId(), image.selfLink().toString());
      assertEquals(transformed.getProviderId(), image.id());
      assertSame(transformed.getOperatingSystem().getFamily(), OsFamily.LINUX);
   }

   public void testWellFormedImageName() {
      GoogleComputeEngineImageToImage imageToImage = new GoogleComputeEngineImageToImage(new ImageNameToOperatingSystem());
      Image image = image("ubuntu-12-04-v123123", null);
      org.jclouds.compute.domain.Image transformed = imageToImage.apply(image);
      assertEquals(transformed.getName(), image.name());
      assertEquals(transformed.getId(), image.selfLink().toString());
      assertEquals(transformed.getProviderId(), image.id());
      assertSame(transformed.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(transformed.getOperatingSystem().getVersion(), "12.04");
   }

   public void testDeleted(){
      GoogleComputeEngineImageToImage imageToImage = new GoogleComputeEngineImageToImage(new ImageNameToOperatingSystem());
      Deprecated deprecated =  Deprecated.create(
         State.DELETED, // state
         URI.create("http://baseurl/projects/centos-cloud/global/images/centos-6-2-v20120326test"), // replacement
         "2014-07-16T22:16:13.468Z", // deprecated
         "2015-07-16T22:16:13.468Z", // obsolete
         "2016-07-16T22:16:13.468Z"); // deleted
      Image image = image("test-deprecated", deprecated);
      org.jclouds.compute.domain.Image transformed = imageToImage.apply(image);
      assertEquals(transformed.getName(), image.name());
      assertEquals(transformed.getId(), image.selfLink().toString());
      assertEquals(transformed.getProviderId(), image.id());
      assertSame(transformed.getOperatingSystem().getFamily(), OsFamily.LINUX);
      assertEquals(transformed.getUserMetadata().get("deprecatedState"), image.deprecated().state().name());
      assertEquals(transformed.getStatus(), Status.DELETED);
   }

   public void testDeprecated(){
      GoogleComputeEngineImageToImage imageToImage = new GoogleComputeEngineImageToImage(new ImageNameToOperatingSystem());
      Deprecated deprecated =  Deprecated.create(
         State.DEPRECATED, // state
         URI.create("http://baseurl/projects/centos-cloud/global/images/centos-6-2-v20120326test"), // replacement
         "2014-07-16T22:16:13.468Z", // deprecated
         "2015-07-16T22:16:13.468Z", // obsolete
         "2016-07-16T22:16:13.468Z"); // deleted
      Image image = image("test-deprecated", deprecated);
      org.jclouds.compute.domain.Image transformed = imageToImage.apply(image);
      assertEquals(transformed.getStatus(), Status.AVAILABLE);
   }

   private static Image image(String name, Deprecated deprecated) {
      return Image.create( //
            "1234", // id
            URI.create("http://test.com/1234"), // selfLink
            new SimpleDateFormatDateService().iso8601DateParse("2012-07-16T22:16:13.468"), // creationTimestamp
            name, // name
            "", // description
            "RAW", // sourceType
            Image.RawDisk.create(URI.create("foo"), "TAR", null), // rawDisk
            deprecated, // deprecated
            Image.Status.READY, // status
            Long.parseLong("881210631", 10), // archivedSizeBytes
            Long.parseLong("8", 10), // diskSizeGb
            "https://www.googleapis.com/compute/v1/projects/party/zones/us-central1-a/disk/disk", // sourceDisk
            "9598530021316715047", // sourceDiskId
            ImmutableList.of("https://www.googleapis.com/compute/v1/projects/suse-cloud/global/licenses/sles-12") // license
      );
   }
}
