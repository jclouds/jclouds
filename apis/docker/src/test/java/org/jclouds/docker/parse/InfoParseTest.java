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
public class InfoParseTest extends BaseDockerParseTest<Info> {

   @Override
   public String resource() {
      return "/info.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Info expected() {
      return Info.create(
              0, // containers
              1, // debug
              "aufs", // driver
              ImmutableList.<List<String>>of(
                      ImmutableList.of("Root Dir", "/mnt/sda1/var/lib/docker/aufs"),
                      ImmutableList.of("Dirs", "46")
              ), // driverStatus
              "native-0.2", // ExecutionDriver
              1, // IPv4Forwarding
              46, // Images
              "https://index.docker.io/v1/", // IndexServerAddress
              "/usr/local/bin/docker", // InitPath
              "", // InitSha1
              "3.16.7-tinycore64", // KernelVersion
              1, // MemoryLimit
              0, // NEventsListener
              10, // NFd
              11, // NGoroutines
              "Boot2Docker 1.4.1 (TCL 5.4); master : 86f7ec8 - Tue Dec 16 23:11:29 UTC 2014", // OperatingSystem
              1, // SwapLimit
              "/mnt/sda1/var/lib/docker", // DockerRootDir
              null, // Labels
              2105585664, // MemTotal
              8, // NCPU
              "7V5Y:IQ2M:HWIL:AZJV:HKRD:Q7OZ:3EQA:ZHMO:4LAD:OSIY:YBAA:BSX6", // ID
              "boot2docker" // name
      );
   }
}
