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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertNull;

import java.net.URI;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseDiskListTest;
import org.jclouds.googlecomputeengine.parse.ParseDiskTest;
import org.jclouds.googlecomputeengine.parse.ParseZoneOperationTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "DiskApiMockTest", singleThreaded = true)
public class DiskApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public static final String IMAGE_URL = "/projects/party/zones/us-central1-a/images/foo";
   public static final String SSD_URL =  "/projects/party/zones/us-central1-a/diskTypes/pd-ssd";

   public void get() throws Exception {
      server.enqueue(jsonResponse("/disk_get.json"));

      assertEquals(diskApi().get("testimage1"), new ParseDiskTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/disks/testimage1");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(diskApi().get("testimage1"));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/disks/testimage1");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      DiskCreationOptions options = new DiskCreationOptions.Builder().sizeGb(1).build();
      assertEquals(diskApi().create("testimage1", options),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/disks",
            stringFromResource("/disk_insert.json"));
   }

   public void insertFromImage() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      DiskCreationOptions diskCreationOptions = new DiskCreationOptions.Builder().sizeGb(1).description("testing 123").build();

      assertEquals(diskApi().create("testimage1", url(IMAGE_URL), diskCreationOptions),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/disks?sourceImage="
            + url("/projects/party/zones/us-central1-a/images/foo").replace(":", "%3A"), //TODO (broudy) clean this up.
            "{\"name\":\"testimage1\",\"sizeGb\":1,\"description\":\"testing 123\"}");
   }

   public void insertFromSSD() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      DiskCreationOptions diskCreationOptions = new DiskCreationOptions.Builder()
         .type(URI.create(url(SSD_URL))).sizeGb(1).build();

      assertEquals(diskApi().create("testimage1", diskCreationOptions),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/disks",
            stringFromResource("/disk_insert_ssd.json"));
   }

   public void creatSnapshot() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(diskApi().createSnapshot("testimage1", "test-snap"),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/disks/testimage1/createSnapshot",
            stringFromResource("/disk_create_snapshot.json"));
   }

   public void creatSnapshot_description() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(diskApi().createSnapshot("testimage1", "test-snap", "This is a test"),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/disks/testimage1/createSnapshot",
            "{\"name\":\"test-snap\",\"description\":\"This is a test\"}");
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(diskApi().delete("testimage1"),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "DELETE", "/projects/party/zones/us-central1-a/disks/testimage1");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(diskApi().delete("testimage1"));
      assertSent(server, "DELETE", "/projects/party/zones/us-central1-a/disks/testimage1");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/disk_list.json"));

      assertEquals(diskApi().list().next(), new ParseDiskListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/disks");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(diskApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/disks");
   }

   public DiskApi diskApi(){
      return api().disksInZone("us-central1-a");
   }
}
