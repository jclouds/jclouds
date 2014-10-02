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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jclouds.docker.compute.BaseDockerApiLiveTest;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.ContainerSummary;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.options.CreateImageOptions;
import org.jclouds.docker.options.ListContainerOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "live", testName = "RemoteApiLiveTest", singleThreaded = true)
public class ContainerApiLiveTest extends BaseDockerApiLiveTest {

   private Container container = null;
   protected static final String BUSYBOX_IMAGE_TAG = "busybox:ubuntu-12.04";
   protected Image image = null;

   @BeforeClass
   protected void init() {
      if (api.getImageApi().inspectImage(BUSYBOX_IMAGE_TAG) == null) {
         CreateImageOptions options = CreateImageOptions.Builder.fromImage(BUSYBOX_IMAGE_TAG);
         InputStream createImageStream = api.getImageApi().createImage(options);
         consumeStream(createImageStream);
      }
      image = api.getImageApi().inspectImage(BUSYBOX_IMAGE_TAG);
      assertNotNull(image);
   }

   @AfterClass
   protected void tearDown() {
      if (image != null) {
         api.getImageApi().deleteImage(BUSYBOX_IMAGE_TAG);
      }
   }

   public void testCreateContainer() throws IOException, InterruptedException {
      Config containerConfig = Config.builder().image(image.id())
              .cmd(ImmutableList.of("/bin/sh", "-c", "while true; do echo hello world; sleep 1; done"))
              .build();
      container = api().createContainer("testCreateContainer", containerConfig);
      assertNotNull(container);
      assertNotNull(container.id());
   }

   @Test(dependsOnMethods = "testCreateContainer")
   public void testStartContainer() throws IOException, InterruptedException {
      api().startContainer(container.id());
      assertTrue(api().inspectContainer(container.id()).state().running());
   }

   @Test(dependsOnMethods = "testStartContainer")
   public void testStopContainer() {
      api().stopContainer(container.id());
      assertFalse(api().inspectContainer(container.id()).state().running());
   }

   @Test
   public void testListContainers() {
      List<ContainerSummary> containerSummaries = api().listContainers(ListContainerOptions.Builder.all(true));
      for (ContainerSummary containerSummary : containerSummaries) {
         assertNotNull(containerSummary.id());
         assertNotNull(containerSummary.image());
         assertFalse(containerSummary.names().isEmpty());
      }
   }

   @Test(dependsOnMethods = "testStopContainer", expectedExceptions = NullPointerException.class)
   public void testRemoveContainer() {
      api().removeContainer(container.id());
      assertFalse(api().inspectContainer(container.id()).state().running());
   }

   private ContainerApi api() {
      return api.getContainerApi();
   }
}
