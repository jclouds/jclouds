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

import org.jclouds.docker.domain.ImageHistory;
import org.jclouds.docker.internal.BaseDockerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class HistoryParseTest extends BaseDockerParseTest<List<ImageHistory>> {

   @Override
   public String resource() {
      return "/history.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public List<ImageHistory> expected() {
      return ImmutableList.of(
            ImageHistory.create("3db9c44f45209632d6050b35958829c3a2aa256d81b9a7be45b362ff85c54710",
                  1398108230,
                  "/bin/sh -c #(nop) ADD file:eb15dbd63394e063b805a3c32ca7bf0266ef64676d5a6fab4801f2e81e2a5148 in /",
                  ImmutableList.of("ubuntu:lucid", "ubuntu:10.04"),
                  182964289,
                  ""),
            ImageHistory.create("6cfa4d1f33fb861d4d114f43b25abd0ac737509268065cdfd69d544a59c85ab8",
                  1398108222,
                  "/bin/sh -c #(nop) MAINTAINER Tianon Gravi <admwiggin@gmail.com> - mkimage-debootstrap.sh -i iproute,iputils-ping,ubuntu-minimal -t lucid.tar.xz lucid http://archive.ubuntu.com/ubuntu/",
                  null,
                  0,
                  ""),
            ImageHistory.create("511136ea3c5a64f264b78b5433614aec563103b4d4702f3ba7d4d2698e22c158",
                  1371157430,
                  "",
                  ImmutableList.of("scratch12:latest", "scratch:latest"),
                  0,
                  "Imported from -")
            );
   }

}
