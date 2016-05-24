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
package org.jclouds.docker.parse;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.docker.domain.Info;
import org.jclouds.docker.internal.BaseDockerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class Info2ParseTest extends BaseDockerParseTest<Info> {

   @Override
   public String resource() {
      return "/info2.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Info expected() {
      return Info.create(
              0, // containers
              false, // debug
              "aufs", // driver
              ImmutableList.<List<String>>of(
                      ImmutableList.of("Root Dir", "/var/lib/docker/aufs"),
                      ImmutableList.of("Backing Filesystem", "extfs"),
                      ImmutableList.of("Dirs", "117"),
                      ImmutableList.of( "Dirperm1 Supported", "true")
              ), // driverStatus
              "", // ExecutionDriver
              true, // IPv4Forwarding
              39, // Images
              "https://index.docker.io/v1/", // IndexServerAddress
              null, // InitPath
              null, // InitSha1
              "4.4.0-22-generic", // KernelVersion
              true, // MemoryLimit
              0, // NEventsListener
              33, // NFd
              83, // NGoroutines
              "Ubuntu 16.04 LTS", // OperatingSystem
              false, // SwapLimit
              "/var/lib/docker", // DockerRootDir
              null, // Labels
              8248356864L, // MemTotal
              4, // NCPU
              "KFWR:PMVY:BEWE:TD52:5WEU:NXF4:I6S3:WDIE:GCRD:L3YA:VWC4:ZRYZ", // ID
              "test" // name
      );
   }
}
