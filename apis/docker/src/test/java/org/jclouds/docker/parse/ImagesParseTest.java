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
              ImageSummary.create("cbba6639a342646deed70d7ea6162fa2a0acea9300f911f4e014555fe37d3456",
                      1416827360,
                      "05794515afd5724df1cdf0e674ae932455fce7dea3c70a94d77119ad1fa954ba",
                      0,
                      808709069,
                      ImmutableList.of("test:latest")),
              ImageSummary.create("e1e548b03259ae30ba12232b6c16ef5205cf71b0363848e78b0394e1ecba4f57",
                      1416826851,
                      "6f36bec79c7f184ceebf7000cfb7244c4bc9b397b6659ac7f420a53d114250d9",
                      0,
                      5609404,
                      ImmutableList.of("<none>:<none>")),
              ImageSummary.create("8201388d2b288539aab6aabf5d3b15ec269eba95c6baa9d6771f16540abf3a3f",
                      1414247273,
                      "4671e2c549c5b60063e349f520c801dc73b53d2226a5a8e5501845ebe94761ca",
                      0,
                      755313702,
                      ImmutableList.of("dockerfile/java:openjdk-7-jdk")),
              ImageSummary.create("5506de2b643be1e6febbf3b8a240760c6843244c41e12aa2f60ccbb7153d17f5",
                      1414108439,
                      "22093c35d77bb609b9257ffb2640845ec05018e3d96cb939f68d0e19127f1723",
                      0,
                      199257566,
                      ImmutableList.of("ubuntu:14.04"))
      );
   }

}
