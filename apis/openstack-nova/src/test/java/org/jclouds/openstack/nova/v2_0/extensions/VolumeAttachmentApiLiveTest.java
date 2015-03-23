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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.jclouds.ContextBuilder;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.cinder.v1.CinderApiMetadata;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.features.VolumeApi;
import org.jclouds.openstack.cinder.v1.options.CreateVolumeOptions;
import org.jclouds.openstack.cinder.v1.predicates.VolumePredicates;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;

/**
 * Tests behavior of Volume Attachment API
 */
@Test(groups = "live", testName = "VolumeAttachmentApiLiveTest", singleThreaded = true)
public class VolumeAttachmentApiLiveTest extends BaseNovaApiLiveTest {
   private VolumeApi volumeApi;
   private VolumeAttachmentApi volumeAttachmentApi;

   private Volume volume;
   private Server server;

   protected String volumeProvider;
   protected String volumeProviderVersion;
   protected int volumeSizeGB;
   protected String deviceId = "/dev/wtf";

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      volumeProvider = setIfTestSystemPropertyPresent(props, provider + ".volume-provider", "openstack-cinder");
      volumeProviderVersion = setIfTestSystemPropertyPresent(props, provider + ".volume-provider-version",
          new CinderApiMetadata().getVersion());
      volumeSizeGB = Integer.parseInt(setIfTestSystemPropertyPresent(props, provider + ".volume-size-gb", "1"));
      singleRegion = setIfTestSystemPropertyPresent(props, provider + ".region", "RegionOne");
      return props;
   }

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();

      CinderApi cinderApi;

      if ("openstack-cinder".equals(volumeProvider)) {
         cinderApi = ContextBuilder.newBuilder(volumeProvider)
               .endpoint(endpoint)
               .apiVersion(volumeProviderVersion)
               .credentials(identity, credential)
               .buildApi(CinderApi.class);
      }
      else {
         cinderApi = ContextBuilder.newBuilder(volumeProvider)
               .credentials(identity, credential)
               .buildApi(CinderApi.class);
      }

      volumeApi = cinderApi.getVolumeApi(singleRegion);
      volumeAttachmentApi = api.getVolumeAttachmentApi(singleRegion).get();

      CreateVolumeOptions options = CreateVolumeOptions.Builder
            .name("jclouds-test-volume")
            .description("description of test volume");

      volume = volumeApi.create(volumeSizeGB, options);
      VolumePredicates.awaitAvailable(volumeApi).apply(volume);

      server = createServerInRegion(singleRegion);
   }

   @AfterClass(groups = {"integration", "live"})
   @Override
   public void tearDown() {
      volumeApi.delete(volume.getId());
      api.getServerApi(singleRegion).delete(server.getId());

      super.tearDown();
   }

   @Test
   public void testAttachVolume() throws TimeoutException {
      VolumeAttachment volumeAttachment = volumeAttachmentApi
               .attachVolumeToServerAsDevice(volume.getId(), server.getId(), deviceId);

      // Wait for the volume to become Attached (aka In Use) before moving on
      if (!VolumePredicates.awaitInUse(volumeApi).apply(volume)) {
         throw new TimeoutException("Timeout on volume: " + volume);
      }

      assertEquals(volumeAttachment.getVolumeId(), volume.getId());
      assertEquals(volumeAttachment.getServerId(), server.getId());
      // we can't assert the device because, depending on the implementation, the passed in device may be ignored
      // and the implementation just picks a device itself
   }

   @Test(dependsOnMethods = "testAttachVolume")
   public void testListAttachments() {
      FluentIterable<VolumeAttachment> volumeAttachments = volumeAttachmentApi
            .listAttachmentsOnServer(server.getId());

      assertEquals(volumeAttachments.size(), 1);

      VolumeAttachment volumeAttachment = volumeAttachments.get(0);

      assertEquals(volumeAttachment.getVolumeId(), volume.getId());
      assertEquals(volumeAttachment.getServerId(), server.getId());
   }

   @Test(dependsOnMethods = "testListAttachments")
   public void testGetAttachment() {
      VolumeAttachment volumeAttachment = volumeAttachmentApi
            .getAttachmentForVolumeOnServer(volume.getId(), server.getId());

      assertEquals(volumeAttachment.getVolumeId(), volume.getId());
      assertEquals(volumeAttachment.getServerId(), server.getId());
   }


   @Test(dependsOnMethods = "testGetAttachment")
   public void testDetachVolume() throws TimeoutException {
      volumeAttachmentApi.detachVolumeFromServer(volume.getId(), server.getId());

      // Wait for the volume to become Detached (aka Available) before moving on
      if (!VolumePredicates.awaitAvailable(volumeApi).apply(volume)) {
         throw new TimeoutException("Timeout on volume: " + volume);
      }

      VolumeAttachment volumeAttachment = volumeAttachmentApi
            .getAttachmentForVolumeOnServer(volume.getId(), server.getId());

      assertNull(volumeAttachment);
   }
}
