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
      return Info.create(0,
              1,
              "aufs",
              ImmutableList.<List<String>>of(
                      ImmutableList.of("Root Dir", "/mnt/sda1/var/lib/docker/aufs"),
                      ImmutableList.of("Dirs", "15")
              ),
              "native-0.2",
              1,
              15,
              "https://index.docker.io/v1/",
              "/usr/local/bin/docker",
              "",
              "3.16.4-tinycore64",
              1,
              0,
              10,
              11,
              "Boot2Docker 1.3.0 (TCL 5.4); master : a083df4 - Thu Oct 16 17:05:03 UTC 2014",
              1);
   }
}
