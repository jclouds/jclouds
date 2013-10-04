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

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;

/**
 * @author David Alves
 */
public class ParseMachineTypeTest extends BaseGoogleComputeEngineParseTest<MachineType> {


   @Override
   public String resource() {
      return "/machinetype.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public MachineType expected() {
      SimpleDateFormatDateService dateService = new SimpleDateFormatDateService();
      return MachineType.builder()
              .id("12907738072351752276")
              .creationTimestamp(dateService.iso8601DateParse("2012-06-07T20:48:14.670"))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta16/projects/myproject/zones/us-central1-a/machineTypes/n1" +
                      "-standard-1"))
              .zone("us-central1-a")
              .name("n1-standard-1")
              .description("1 vCPU, 3.75 GB RAM, and a 10 GB ephemeral root disk")
              .guestCpus(1)
              .memoryMb(3840)
              .imageSpaceGb(10)
              .addScratchDisk(1770)
              .addScratchDisk(1770)
              .maximumPersistentDisks(16)
              .maximumPersistentDisksSizeGb(128)
              .build();
   }
}
