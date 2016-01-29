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

import java.net.URI;

import javax.ws.rs.Consumes;

import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.domain.MachineType.ScratchDisk;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseMachineTypeTest")
public class ParseMachineTypeTest extends BaseGoogleComputeEngineParseTest<MachineType> {

   @Override
   public String resource() {
      return "/machinetype.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public MachineType expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public MachineType expected(String baseUrl) {
      return MachineType.create( //
            "12907738072351752276", // id
            parse("2012-06-07T20:48:14.670"), // creationTimestamp
            URI.create(baseUrl + "/party/zones/us-central1-a/machineTypes/n1-standard-1"), // selfLink
            "n1-standard-1", // name
            "1 vCPU, 3.75 GB RAM, and a 10 GB ephemeral root disk", // description
            1, // guestCpus
            3840, // memoryMb
            null, // imageSpaceGb
            ImmutableList.of(ScratchDisk.create(1770), ScratchDisk.create(1770)), // scratchDisks
            16, // maximumPersistentDisks
            128, // maximumPersistentDisksSizeGb
            "us-central1-a", // zone
            null // deprecated
      );
   }
}
