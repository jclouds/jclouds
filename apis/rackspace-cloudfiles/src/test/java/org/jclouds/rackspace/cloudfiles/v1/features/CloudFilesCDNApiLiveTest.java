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
package org.jclouds.rackspace.cloudfiles.v1.features;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.rackspace.cloudfiles.v1.domain.CDNContainer;
import org.jclouds.rackspace.cloudfiles.v1.internal.BaseCloudFilesApiLiveTest;
import org.jclouds.rackspace.cloudfiles.v1.options.ListCDNContainerOptions;
import org.jclouds.rackspace.cloudfiles.v1.options.UpdateCDNContainerOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;


/**
 * Tests the live behavior of the {@code CloudFilesCDNApi}.
 */
@Test(groups = "live", testName = "CloudFilesCDNApiLiveTest")
public class CloudFilesCDNApiLiveTest extends BaseCloudFilesApiLiveTest {

   private String name = getClass().getSimpleName();

   public CloudFilesCDNApiLiveTest() {
      super();
   }

   public void testEnable() throws Exception {
      for (String regionId : regions) {
         assertNotNull(api.getCDNApi(regionId).enable(name));

         CDNContainer container = api.getCDNApi(regionId).get(name);
         assertCDNContainerNotNull(container);
         assertTrue(container.isEnabled());
      }
   }

   public void testEnableWithTTL() throws Exception {
      for (String regionId : regions) {
         assertNotNull(api.getCDNApi(regionId).enable(name, 777777));

         CDNContainer container = api.getCDNApi(regionId).get(name);
         assertCDNContainerNotNull(container);
         assertTrue(container.isEnabled());
         assertTrue(container.getTtl() == 777777);
      }
   }

   public void testDisable() throws Exception {
      for (String regionId : regions) {
         assertTrue(api.getCDNApi(regionId).disable(name));

         CDNContainer container = api.getCDNApi(regionId).get(name);
         assertFalse(container.isEnabled());
      }
   }

   public void testList() throws Exception {
      for (String regionId : regions) {
         List<CDNContainer> cdnResponse = api.getCDNApi(regionId).list().toList();
         assertNotNull(cdnResponse);

         for (CDNContainer cdnContainer : cdnResponse) {
            assertCDNContainerNotNull(cdnContainer);
            assertTrue(cdnContainer.isEnabled());
         }
      }
   }

   public void testListWithOptions() throws Exception {
      String lexicographicallyBeforeName = name.substring(0, name.length() - 1);
      for (String regionId : regions) {
         ListCDNContainerOptions options = new ListCDNContainerOptions().marker(lexicographicallyBeforeName);

         CDNContainer cdnContainer = api.getCDNApi(regionId).list(options).get(0);
         assertCDNContainerNotNull(cdnContainer);
         assertTrue(cdnContainer.isEnabled());
      }
   }

   public void testGet() throws Exception {
      for (String regionId : regions) {
         CDNContainer container = api.getCDNApi(regionId).get(name);
         assertCDNContainerNotNull(container);
         assertTrue(container.isEnabled());
      }
   }

   public void testPurgeObject() throws Exception {
      for (String regionId : regions) {
         String objectName = "testPurge";
         Payload payload = Payloads.newByteSourcePayload(ByteSource.wrap(new byte[] {1, 2, 3}));
         ObjectApi objectApi = api.getObjectApi(regionId, name);

         // create a new object
         objectApi.put(objectName, payload);

         CDNApi cdnApi = api.getCDNApi(regionId);
         assertTrue(cdnApi.purgeObject(name, "testPurge", ImmutableList.<String>of()));

         // delete the object
         objectApi.delete(objectName);
         assertNull(objectApi.get(objectName, GetOptions.NONE));
      }
   }

   public void testUpdate() throws Exception {
      for (String regionId : regions) {
         // enable with a ttl
         assertNotNull(api.getCDNApi(regionId).enable(name, 777777));

         // now get the container
         CDNContainer original = api.getCDNApi(regionId).get(name);
         assertTrue(original.isEnabled());
         assertCDNContainerNotNull(original);

         // update options
         UpdateCDNContainerOptions opts = new UpdateCDNContainerOptions()
                                                .ttl(1234567)
                                                .logRetention(true)
                                                .enabled(false);
         // update the container
         assertTrue(api.getCDNApi(regionId).update(name, opts));

         // now get the updated container
         CDNContainer updated = api.getCDNApi(regionId).get(name);
         assertFalse(updated.isEnabled());
         assertCDNContainerNotNull(updated);

         assertNotEquals(original.getTtl(), updated.getTtl());
         assertTrue(updated.isLogRetentionEnabled());
      }
   }

   private static void assertCDNContainerNotNull(CDNContainer container) {
      assertNotNull(container);
      assertNotNull(container.getName());
      assertNotNull(container.getTtl());
      assertNotNull(container.getUri());
      assertNotNull(container.getIosUri());
      assertNotNull(container.getSslUri());
      assertNotNull(container.getStreamingUri());
      assertNotNull(container.isLogRetentionEnabled());
   }

   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      for (String regionId : regions) {
         api.getContainerApi(regionId).create(name);
      }
   }

   @Override
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : regions) {
         api.getCDNApi(regionId).disable(name);
         api.getContainerApi(regionId).deleteIfEmpty(name);
      }
      super.tearDown();
   }
}
