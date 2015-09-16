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
package org.jclouds.googlecomputeengine.parse;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import java.net.URI;

import com.google.common.collect.ImmutableList;
import org.jclouds.googlecomputeengine.domain.AttachDisk;
import org.jclouds.googlecomputeengine.domain.AttachDisk.DiskInterface;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.AttachedDisk;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling.OnHostMaintenance;
import org.jclouds.googlecomputeengine.domain.Instance.ServiceAccount;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.Tags;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseInstanceTest")
public class ParseInstanceTest extends BaseGoogleComputeEngineParseTest<Instance> {

   @Override
   public String resource() {
      return "/instance_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public Instance expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public Instance expected(String baseUrl) {
      return Instance.create( //
            "13051190678907570425", // id
            parse("2012-11-25T23:48:20.758"), // creationTimestamp
            URI.create(baseUrl + "/party/zones/us-central1-a/instances/test-0"), // selfLink
            "test-0", // name
            "desc", // description
            Tags.create("abcd", ImmutableList.of("aTag", "Group-port-42")), // tags
            URI.create(baseUrl + "/party/zones/us-central1-a/machineTypes/n1-standard-1"), // machineType
            Instance.Status.RUNNING, // status
            null, // statusMessage
            URI.create(baseUrl + "/party/zones/us-central1-a"), // zone
            true, // canIpForward
            ImmutableList.of(NetworkInterface.create( //
                  "nic0", // name
                  URI.create(baseUrl + "/party/global/networks/default"), // network
                  "10.240.121.115", // networkIP
                  null // accessConfigs
            )), // networkInterfaces
            ImmutableList.of(AttachedDisk.create( //
                  0, // index
                  AttachedDisk.Type.PERSISTENT, // type
                  AttachedDisk.Mode.READ_WRITE, // mode
                  URI.create(baseUrl + "/party/zones/us-central1-a/disks/test"), // source
                  "test", // deviceName
                  false, // autoDelete
                  true, // boot
                  AttachDisk.InitializeParams.create(
                        "test", // diskName
                        Long.parseLong("100", 10), // diskSizeGb
                        URI.create(baseUrl + "/debian-cloud/global/images/debian-7-wheezy-v20140718"), // sourceImage
                        URI.create(baseUrl + "/party/zones/us-central1-a/diskTypes/pd-standard") // diskType
                        ), // initializeParams
                  ImmutableList.of(baseUrl + "/suse-cloud/global/licenses/sles-12"), // licenses
                  DiskInterface.NVME // interface
            )), // disks
            Metadata.create("efgh")
                    .put("aKey", "aValue")
                    .put("jclouds-delete-boot-disk", "true"), // metadata
            ImmutableList.of(ServiceAccount.create("default", ImmutableList.of("myscope"))), // serviceAccounts
            Instance.Scheduling.create(OnHostMaintenance.MIGRATE, false, false) // scheduling
      );
   }
}
