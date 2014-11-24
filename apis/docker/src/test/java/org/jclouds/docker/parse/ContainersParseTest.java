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

import org.jclouds.docker.domain.ContainerSummary;
import org.jclouds.docker.domain.Port;
import org.jclouds.docker.internal.BaseDockerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class ContainersParseTest extends BaseDockerParseTest<List<ContainerSummary>> {

   @Override
   public String resource() {
      return "/containers.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public List<ContainerSummary> expected() {
      return ImmutableList.of(
              ContainerSummary.create("6d35806c1bd2b25cd92bba2d2c2c5169dc2156f53ab45c2b62d76e2d2fee14a9",
                      ImmutableList.of("/hopeful_mclean"),
                      "1395472605",
                      "jclouds/ubuntu:latest",
                      "/usr/sbin/sshd -D",
                      ImmutableList.of(Port.create("0.0.0.0", 22, 49231, "tcp")),
                      "Up 55 seconds")
      );
   }

}
