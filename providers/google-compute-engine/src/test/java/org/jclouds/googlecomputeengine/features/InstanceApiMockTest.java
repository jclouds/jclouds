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
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.util.Arrays;

import org.jclouds.googlecomputeengine.domain.AttachDisk;
import org.jclouds.googlecomputeengine.domain.AttachDisk.DiskInterface;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig.Type;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling;
import org.jclouds.googlecomputeengine.domain.Instance.ServiceAccount;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling.OnHostMaintenance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceListTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceSerialOutputTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceTest;
import org.jclouds.googlecomputeengine.parse.ParseZoneOperationTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "InstanceApiMockTest", singleThreaded = true)
public class InstanceApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/instance_get.json"));

      assertEquals(instanceApi().get("test-instance"), new ParseInstanceTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/instances/test-instance");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(instanceApi().get("test-1"));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/instances/test-1");
   }

   public void getInstanceSerialPortOutput() throws Exception {
      server.enqueue(jsonResponse("/instance_serial_port.json"));

      assertEquals(instanceApi().getSerialPortOutput("test-1"),
            new ParseInstanceSerialOutputTest().expected(url("/projects")));

      assertSent(server, "GET", "/projects/party/zones/us-central1-a/instances/test-1/serialPort");
   }

   public void insert_noOptions() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      NewInstance newInstance = NewInstance.create(
            "test-1", // name
            URI.create(url("/projects/party/zones/us-central1-a/machineTypes/n1-standard-1")), // machineType
            URI.create(url("/projects/party/global/networks/default")), // network
            URI.create(url("/projects/party/global/images/centos-6-2-v20120326")) // sourceImage
      );

      assertEquals(instanceApi().create(newInstance), new ParseZoneOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances",
            stringFromResource("/instance_insert_simple.json"));
   }

   public void insert_allOptions() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      NewInstance newInstance = NewInstance.create(
            "test-1", // name
            URI.create(url("/projects/party/zones/us-central1-a/machineTypes/n1-standard-1")), // machineType
            URI.create(url("/projects/party/global/networks/default")), // network
            Arrays.asList(AttachDisk.existingBootDisk(URI.create(url("/projects/party/zones/us-central1-a/disks/test")))),
            "desc", // description
            null // tags
      );

      newInstance.metadata().put("aKey", "aValue");
      assertEquals(instanceApi().create(newInstance), new ParseZoneOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances",
            stringFromResource("/instance_insert.json"));
   }

   public void addAccessConfig() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      AccessConfig config = AccessConfig.create("test-access", Type.ONE_TO_ONE_NAT, "1.1.1.1");
      assertEquals(instanceApi().addAccessConfigToNic("test-instance", config, "test-network"),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-instance/"
            + "addAccessConfig?networkInterface=test-network",
            "{" +
            "  \"type\": \"ONE_TO_ONE_NAT\"," +
            "  \"name\": \"test-access\"," +
            "  \"natIP\": \"1.1.1.1\"" +
            "}");
   }

   public void deleteAccessConfig() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().deleteAccessConfigFromNic("test-instance", "test-access", "test-network"),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-instance/"
            + "deleteAccessConfig?accessConfig=test-access&networkInterface=test-network");
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().delete("test-1"),
            new ParseZoneOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/zones/us-central1-a/instances/test-1");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(instanceApi().delete("test-1"));
      assertSent(server, "DELETE", "/projects/party/zones/us-central1-a/instances/test-1");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/instance_list.json"));

      assertEquals(instanceApi().list().next(), new ParseInstanceListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/instances");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(instanceApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/instances");
   }

   public void setMetadata() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().setMetadata("test-1", Metadata.create("efgh").put("foo", "bar")),
            new ParseZoneOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/setMetadata",
            stringFromResource("/instance_set_metadata.json"));
   }

   public void setTags() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().setTags("test-1", ImmutableList.of("foo", "bar"), "efgh"),
            new ParseZoneOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/setTags",
            stringFromResource("/instance_set_tags.json"));
   }

   public void reset() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().reset("test-1"),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/reset");
   }

   public void attachDisk() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().attachDisk("test-1",
            AttachDisk.create(AttachDisk.Type.PERSISTENT, // type
                                     AttachDisk.Mode.READ_WRITE, // mode
                                     URI.create(url("/projects/party/zones/us-central1-a/disks/test")), // source
                                     "test", // deviceName
                                     true, // boot
                                     AttachDisk.InitializeParams.create(
                                           "test", // diskName
                                           Long.parseLong("100", 10), // diskSizeGb
                                           URI.create(url("/projects/party/global/images/test")), // sourceImage
                                           URI.create(url("/projects/party/zones/us-central1-a/diskTypes/pd-standard")) // diskType
                                           ), // initializeParams
                                     true, // autoDelete
                                     ImmutableList.of(url("/projects/suse-cloud/global/licenses/sles-12")), // licenses
                                     DiskInterface.NVME // interface
                                     )),
            new ParseZoneOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/attachDisk",
            stringFromResource("/instance_attach_disk.json"));
   }

   public void detatchDisk() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().detachDisk("test-1", "test-disk-1"),
            new ParseZoneOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/detachDisk?deviceName=test-disk-1");
   }

   public void setDiskAutoDelete() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().setDiskAutoDelete("test-1", "test-disk-1", true),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/setDiskAutoDelete"
          + "?deviceName=test-disk-1&autoDelete=true");
   }

   public void setScheduling() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().setScheduling("test-1", OnHostMaintenance.TERMINATE, true, false),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/setScheduling",
            "{\"onHostMaintenance\": \"TERMINATE\",\"automaticRestart\": true,\"preemptible\": false}");
   }

   public void start_test() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().start("test-1"),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/start");
   }

   public void stop_test() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      assertEquals(instanceApi().stop("test-1"),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/stop");
   }

   public void builderTest() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      NewInstance newInstance = new NewInstance.Builder("test-1", // name
            URI.create(url("/projects/party/zones/us-central1-a/machineTypes/n1-standard-1")), // machineType
            URI.create(url("/projects/party/global/networks/default")), // network
            URI.create(url("/projects/party/global/images/centos-6-2-v20120326"))).build(); // sourceImage)

      assertEquals(instanceApi().create(newInstance), new ParseZoneOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances",
            stringFromResource("/instance_insert_simple.json"));
   }

   public void insert_builder_allOptions() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      NewInstance newInstance = new NewInstance.Builder(
            "test-1", // name
            URI.create(url("/projects/party/zones/us-central1-a/machineTypes/n1-standard-1")), // machineType
            URI.create(url("/projects/party/global/networks/default")), // network
            Arrays.asList(AttachDisk.existingBootDisk(URI.create(url("/projects/party/zones/us-central1-a/disks/test")))))
            .canIpForward(true)
            .description("desc")
            .tags(null)
            .metadata(Metadata.create().put("aKey", "aValue"))
            .serviceAccounts(ImmutableList.of(ServiceAccount.create("default",
                                              ImmutableList.of("https://www.googleapis.com/auth/compute"))))
            .scheduling(Scheduling.create(OnHostMaintenance.MIGRATE, true, false))
            .build();

      assertEquals(instanceApi().create(newInstance), new ParseZoneOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances",
            stringFromResource("/instance_insert_full.json"));
   }

   InstanceApi instanceApi(){
      return api().instancesInZone("us-central1-a");
   }
}
