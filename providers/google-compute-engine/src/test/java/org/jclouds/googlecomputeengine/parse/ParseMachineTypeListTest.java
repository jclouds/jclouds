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

import static org.jclouds.googlecomputeengine.domain.Resource.Kind.MACHINE_TYPE_LIST;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;

/**
 * @author David Alves
 */
public class ParseMachineTypeListTest extends BaseGoogleComputeEngineParseTest<ListPage<MachineType>> {


   @Override
   public String resource() {
      return "/machinetype_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<MachineType> expected() {
      SimpleDateFormatDateService dateService = new SimpleDateFormatDateService();
      return ListPage.<MachineType>builder()
              .kind(MACHINE_TYPE_LIST)
              .id("projects/myproject/machineTypes")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta16/projects/myproject/zones/us-central1-a/machineTypes"))
              .addItem(MachineType.builder()
                      .id("4618642685664990776")
                      .creationTimestamp(dateService.iso8601DateParse("2013-04-25T13:32:49.088-07:00"))
                      .selfLink(URI.create("https://www.googleapis" +
                              ".com/compute/v1beta16/projects/myproject/zones/us-central1-a/machineTypes/f1-micro"))
                      .zone("us-central1-a")
                      .name("f1-micro")
                      .description("1 vCPU (shared physical core) and 0.6 GB RAM")
                      .guestCpus(1)
                      .memoryMb(614)
                      .imageSpaceGb(0)
                      .maximumPersistentDisks(4)
                      .maximumPersistentDisksSizeGb(3072)
                      .build())
              .addItem(MachineType.builder()
                      .id("12907738072351752276")
                      .creationTimestamp(dateService.iso8601DateParse("2012-06-07T20:48:14.670"))
                      .selfLink(URI.create("https://www.googleapis" +
                              ".com/compute/v1beta16/projects/myproject/zones/us-central1-a/machineTypes/n1-standard-1"))
                      .zone("us-central1-a")
                      .name("n1-standard-1")
                      .description("1 vCPU, 3.75 GB RAM, and a 10 GB ephemeral root disk")
                      .guestCpus(1)
                      .memoryMb(3840)
                      .imageSpaceGb(10)
                      .maximumPersistentDisks(16)
                      .maximumPersistentDisksSizeGb(128)
                      .build())
              .addItem(MachineType.builder()
                      .id("12908560709887590691")
                      .creationTimestamp(dateService.iso8601DateParse("2012-06-07T20:51:19.936"))
                      .selfLink(URI.create("https://www.googleapis" +
                              ".com/compute/v1beta16/projects/myproject/zones/us-central1-a/machineTypes/n1-standard-8-d"))
                      .zone("us-central1-a")
                      .name("n1-standard-8-d")
                      .description("8 vCPUs, 30 GB RAM, a 10 GB ephemeral root disk, " +
                              "and 2 extra 1770 GB ephemeral disks")
                      .guestCpus(8)
                      .memoryMb(30720)
                      .imageSpaceGb(10)
                      .addScratchDisk(1770)
                      .addScratchDisk(1770)
                      .maximumPersistentDisks(16)
                      .maximumPersistentDisksSizeGb(1024)
                      .build())
              .build();
   }
}
