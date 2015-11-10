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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.assertj.core.api.Fail;
import org.jclouds.docker.compute.BaseDockerApiLiveTest;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.Exec;
import org.jclouds.docker.domain.ExecCreateParams;
import org.jclouds.docker.domain.ExecInspect;
import org.jclouds.docker.domain.ExecStartParams;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.options.BuildOptions;
import org.jclouds.docker.options.CreateImageOptions;
import org.jclouds.docker.options.RemoveContainerOptions;
import org.jclouds.docker.util.DockerInputStream;
import org.jclouds.docker.util.StdStreamData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "MiscApiLiveTest", singleThreaded = true)
public class MiscApiLiveTest extends BaseDockerApiLiveTest {

   private static String imageId;

   private Container container = null;
   private Image image = null;
   private Exec exec = null;

   @BeforeClass
   protected void init() {
      if (api.getImageApi().inspectImage(ALPINE_IMAGE_TAG) == null) {
         CreateImageOptions options = CreateImageOptions.Builder.fromImage(ALPINE_IMAGE_TAG);
         InputStream createImageStream = api.getImageApi().createImage(options);
         consumeStream(createImageStream);
      }
      image = api.getImageApi().inspectImage(ALPINE_IMAGE_TAG);
      assertNotNull(image);
      Config containerConfig = Config.builder().image(image.id())
            .cmd(ImmutableList.of("/bin/sh", "-c", "touch hello; while true; do echo hello world; sleep 1; done"))
            .build();
      container = api.getContainerApi().createContainer("miscApiTest", containerConfig);
      assertNotNull(container);
      api.getContainerApi().startContainer(container.id());
      assertTrue(api.getContainerApi().inspectContainer(container.id()).state().running());
   }

   @AfterClass
   protected void tearDown() {
      if (container != null) {
         if (api.getContainerApi().inspectContainer(container.id()) != null) {
            api.getContainerApi().removeContainer(container.id(), RemoveContainerOptions.Builder.force(true));
         }
      }
      if (image != null) {
         api.getImageApi().deleteImage(ALPINE_IMAGE_TAG);
      }
   }

   @Test
   public void testVersion() {
      assertNotNull(api().getVersion().apiVersion());
      assertNotNull(api().getVersion().version());
      assertNotNull(api().getVersion().gitCommit());
      assertNotNull(api().getVersion().goVersion());
      assertNotNull(api().getVersion().kernelVersion());
      assertNotNull(api().getVersion().arch());
      assertNotNull(api().getVersion().os());
   }

   @Test
   public void testInfo() {
      assertNotNull(api().getInfo());
   }

   @Test
   public void testBuildImageFromDockerfile() throws IOException, InterruptedException, URISyntaxException {
      BuildOptions options = BuildOptions.Builder.tag("jclouds-test-test-build-image").verbose(false).nocache(false);
      InputStream buildImageStream = api().build(tarredDockerfile(), options);
      String buildStream = consumeStream(buildImageStream);
      Iterable<String> splitted = Splitter.on("\n").split(buildStream.replace("\r", "").trim());
      String lastStreamedLine = Iterables.getLast(splitted).trim();
      String rawImageId = Iterables.getLast(Splitter.on("Successfully built ").split(lastStreamedLine));
      imageId = rawImageId.substring(0, 11);
      assertNotNull(imageId);
   }

   @Test
   public void testExecCreate() {
      exec = api().execCreate(container.id(),
            ExecCreateParams.builder()
                  .cmd(ImmutableList.<String> of("/bin/sh", "-c",
                        "echo -n Standard >&1 && echo -n Error >&2 && exit 2"))
                  .attachStderr(true).attachStdout(true).build());
      assertNotNull(exec);
      assertNotNull(exec.id());
   }

   @Test(dependsOnMethods = "testExecCreate")
   public void testExecStart() throws IOException {
      final ExecStartParams startParams = ExecStartParams.builder().detach(false).build();
      DockerInputStream inputStream = null;
      try {
         inputStream = new DockerInputStream(api().execStart(exec.id(), startParams));
         assertNotNull(inputStream);
         ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
         ByteArrayOutputStream baosErr = new ByteArrayOutputStream();
         StdStreamData data = null;
         while (null != (data = inputStream.readStdStreamData())) {
            assertFalse(data.isTruncated());
            switch (data.getType()) {
               case OUT:
                  baosOut.write(data.getPayload());
                  break;
               case ERR:
                  baosErr.write(data.getPayload());
                  break;
               default:
                  Fail.fail("Unexpected Stream type");
                  break;
            }
         }
         assertEquals(baosOut.toString(), "Standard");
         assertEquals(baosErr.toString(), "Error");
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   @Test(dependsOnMethods = "testExecStart")
   public void testExecInspect() throws IOException {
      ExecInspect execInspect = api().execInspect(exec.id());
      assertNotNull(execInspect);
      assertEquals(execInspect.id(), exec.id());
      assertEquals(execInspect.running(), false);
      assertEquals(execInspect.exitCode(), 2);
   }

   
   private MiscApi api() {
      return api.getMiscApi();
   }

}
