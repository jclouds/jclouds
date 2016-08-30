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

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.internal.BaseDockerParseTest;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "unit")
public class ImageParseTest extends BaseDockerParseTest<Image> {

   @Override
   public String resource() {
      return "/image.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Image expected() {
      return Image.create("cbba6639a342646deed70d7ea6162fa2a0acea9300f911f4e014555fe37d3456",
              "author",
              "comment",
              Config.builder().cmd(ImmutableList.of("/bin/sh", "-c", "echo hello world"))
                      .env(ImmutableList.of(
                                      "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                                      "HOME=/root",
                                      "JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64"
                              )
                      )
                      .exposedPorts(ImmutableMap.of("8081/tcp", Maps.newHashMap()))
                      .hostname("f22711318734")
                      .domainname("")
                      .user("user")
                      .image("05794515afd5724df1cdf0e674ae932455fce7dea3c70a94d77119ad1fa954ba")
                      .workingDir("/home/user")
                      .build(),
              Config.builder().cmd(ImmutableList.of("/bin/sh", "-c", "echo hello world"))
                      .env(ImmutableList.of(
                                      "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                                      "HOME=/root",
                                      "JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64")
                      )
                      .exposedPorts(ImmutableMap.of("8081/tcp", Maps.newHashMap()))
                      .hostname("f22711318734")
                      .domainname("")
                      .user("user")
                      .image("05794515afd5724df1cdf0e674ae932455fce7dea3c70a94d77119ad1fa954ba")
                      .workingDir("/home/user")
                      .build(),
              "05794515afd5724df1cdf0e674ae932455fce7dea3c70a94d77119ad1fa954ba",
              new SimpleDateFormatDateService().iso8601DateParse("2014-11-24T11:09:20.310023104Z"),
              "0d14967353dbbd2ee78abe209f026f71654da49692fa2b044296ec3c810027b3",
              "1.3.1",
              "amd64",
              "linux",
              0,
              808709069,
              null);
   }
}
