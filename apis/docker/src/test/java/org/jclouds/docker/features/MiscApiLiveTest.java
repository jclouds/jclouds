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
package org.jclouds.docker.features;

import org.jclouds.docker.compute.BaseDockerApiLiveTest;
import org.jclouds.docker.options.BuildOptions;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "MiscApiLiveTest", singleThreaded = true)
public class MiscApiLiveTest extends BaseDockerApiLiveTest {

   private static final String API_VERSION = "1.15";
   private static final String VERSION = "1.3.2";
   private static final String GIT_COMMIT = "39fa2fa";
   private static final String GO_VERSION = "go1.3.3";
   private static final String KERNEL_VERSION = "3.16.7-tinycore64";
   private static final String ARCH = "amd64";
   private static final String OS = "linux";

   private static String imageId;

   @Test
   public void testVersion() {
      assertEquals(api().getVersion().apiVersion(), API_VERSION);
      assertEquals(api().getVersion().version(), VERSION);
      assertEquals(api().getVersion().gitCommit(), GIT_COMMIT);
      assertEquals(api().getVersion().goVersion(), GO_VERSION);
      assertEquals(api().getVersion().kernelVersion(), KERNEL_VERSION);
      assertEquals(api().getVersion().arch(), ARCH);
      assertEquals(api().getVersion().os(), OS);
   }

   @Test
   public void testInfo() {
      assertNotNull(api().getInfo());
   }

   @Test
   public void testBuildImageFromDockerfile() throws IOException, InterruptedException, URISyntaxException {
      BuildOptions options = BuildOptions.Builder.tag("testBuildImage").verbose(false).nocache(false);
      InputStream buildImageStream = api().build(tarredDockerfile(), options);
      String buildStream = consumeStream(buildImageStream);
      Iterable<String> splitted = Splitter.on("\n").split(buildStream.replace("\r", "").trim());
      String lastStreamedLine = Iterables.getLast(splitted).trim();
      String rawImageId = Iterables.getLast(Splitter.on("Successfully built ").split(lastStreamedLine));
      imageId = rawImageId.substring(0, 11);
      assertNotNull(imageId);
   }

   private MiscApi api() {
      return api.getMiscApi();
   }


}
