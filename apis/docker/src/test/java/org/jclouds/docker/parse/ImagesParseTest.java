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

import org.jclouds.docker.domain.ImageSummary;
import org.jclouds.docker.internal.BaseDockerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class ImagesParseTest extends BaseDockerParseTest<List<ImageSummary>> {

   @Override
   public String resource() {
      return "/images.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public List<ImageSummary> expected() {
      return ImmutableList.of(
            ImageSummary.create("d7057cb020844f245031d27b76cb18af05db1cc3a96a29fa7777af75f5ac91a3",
                  1442866547,
                  "cfa753dfea5e68a24366dfba16e6edf573daa447abf65bc11619c1a98a3aff54",
                  0,
                  1095501,
                  ImmutableList.of("docker.io/busybox:1.23.2", "docker.io/busybox:latest")),
            ImageSummary.create("633fcd11259e8d6bccfbb59a4086b95b0d0fb44edfc3912000ef1f70e8a7bfc6",
                  1442598293,
                  "b65c936b5fb601d680ed656b1ccf8ab857c0e5cb521043a005405c194e9a69f3",
                  0,
                  5607885,
                  ImmutableList.of("docker.io/busybox:ubuntu-14.04", "jclouds:testTag")),
            ImageSummary.create("f4fddc471ec22fc1f7d37768132f1753bc171121e30ac2af7fcb0302588197c0",
                  1442260874,
                  "",
                  5244426,
                  5244426,
                  ImmutableList.of("docker.io/alpine:3.2")),
            ImageSummary.create("91e54dfb11794fad694460162bf0cb0a4fa710cfa3f60979c177d920813e267c",
                  1440102075,
                  "d74508fb6632491cea586a1fd7d748dfc5274cd6fdfedee309ecdcbc2bf5cb82",
                  0,
                  188333286,
                  ImmutableList.of("docker.io/ubuntu:14.04", "docker.io/ubuntu:latest"))
      );
   }

}
