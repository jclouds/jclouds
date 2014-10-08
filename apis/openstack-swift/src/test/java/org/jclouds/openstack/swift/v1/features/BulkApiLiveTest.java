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
package org.jclouds.openstack.swift.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.TarGzExporter;
import org.jclouds.io.ByteStreams2;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.BulkDeleteResponse;
import org.jclouds.openstack.swift.v1.domain.ExtractArchiveResponse;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

@Test(groups = "live", testName = "BulkApiLiveTest")
public class BulkApiLiveTest extends BaseSwiftApiLiveTest<SwiftApi> {

   private static final int OBJECT_COUNT = 10;
   private String containerName = getClass().getSimpleName();
   List<String> paths = Lists.newArrayList();
   byte[] tarGz;

   public void testNotPresentWhenDeleting() throws Exception {
      for (String regionId : regions) {
         BulkDeleteResponse deleteResponse = api.getBulkApi(regionId).bulkDelete(
               ImmutableList.of(UUID.randomUUID().toString()));
         assertEquals(deleteResponse.getDeleted(), 0);
         assertEquals(deleteResponse.getNotFound(), 1);
         assertTrue(deleteResponse.getErrors().isEmpty());
      }
   }

   public void testExtractArchive() throws Exception {
      for (String regionId : regions) {
         Payload payload = new ByteSourcePayload(ByteSource.wrap(tarGz));

         ExtractArchiveResponse extractResponse = api.getBulkApi(regionId)
                                                     .extractArchive(containerName, payload, "tar.gz");
         assertEquals(extractResponse.getCreated(), OBJECT_COUNT);
         assertTrue(extractResponse.getErrors().isEmpty());
         assertEquals(api.getContainerApi(regionId).get(containerName).getObjectCount(), OBJECT_COUNT);

         // repeat the command
         extractResponse = api.getBulkApi(regionId).extractArchive(containerName, payload, "tar.gz");
         assertEquals(extractResponse.getCreated(), OBJECT_COUNT);
         assertTrue(extractResponse.getErrors().isEmpty());
      }
   }

   @Test(dependsOnMethods = "testExtractArchive")
   public void testBulkDelete() throws Exception {
      for (String regionId : regions) {
         BulkDeleteResponse deleteResponse = api.getBulkApi(regionId).bulkDelete(paths);
         assertEquals(deleteResponse.getDeleted(), OBJECT_COUNT);
         assertEquals(deleteResponse.getNotFound(), 0);
         assertTrue(deleteResponse.getErrors().isEmpty());
         assertEquals(api.getContainerApi(regionId).get(containerName).getObjectCount(), 0);
      }
   }

   @Override
   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      for (String regionId : regions) {
         boolean created = api.getContainerApi(regionId).create(containerName);
         if (!created) {
            deleteAllObjectsInContainer(regionId, containerName);
         }
      }
      GenericArchive files = ShrinkWrap.create(GenericArchive.class, "files.tar.gz");
      StringAsset content = new StringAsset("foo");
      for (int i = 0; i < OBJECT_COUNT; i++) {
         paths.add(containerName + "/file" + i);
         files.add(content, "/file" + i);
      }

      try {
         tarGz = ByteStreams2.toByteArrayAndClose(files.as(TarGzExporter.class).exportAsInputStream());
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : regions) {
         deleteAllObjectsInContainer(regionId, containerName);
         api.getContainerApi(regionId).deleteIfEmpty(containerName);
      }
      super.tearDown();
   }
}
