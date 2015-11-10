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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jclouds.docker.compute.BaseDockerApiLiveTest;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.domain.Network;
import org.jclouds.docker.options.CreateImageOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "NetworkApiLiveTest", singleThreaded = true)
public class NetworkApiLiveTest extends BaseDockerApiLiveTest {

   private static final String NETWORK_NAME = "JCLOUDS_NETWORK";

   private Network network = null;
   protected Image image = null;
   private Container container;

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
              .cmd(ImmutableList.of("sh", "-c", "touch hello; while true; do echo hello world; sleep 1; done"))
              .build();
      container = api.getContainerApi().createContainer("jclouds-test-network", containerConfig);
      api.getContainerApi().startContainer(container.id());
      container = api.getContainerApi().inspectContainer(container.id());
   }

   @AfterClass(alwaysRun = true)
   protected void tearDown() {
      if (container != null) {
         api.getContainerApi().stopContainer(container.id());
         api.getContainerApi().removeContainer(container.id());
      }
      if (network != null) {
         api().removeNetwork(network.id());
      }
   }

   public void testCreateNetwork() throws IOException, InterruptedException {
      network = api().createNetwork(Network.create(NETWORK_NAME, null, null, null, null, ImmutableMap.<String, Network.Details> of(), ImmutableMap.<String, String> of()));
      assertNotNull(network);
      assertNotNull(network.id());
   }

   @Test(dependsOnMethods = "testCreateNetwork")
   public void testGetNetwork() {
      network = api().inspectNetwork(network.id());
      assertNotNull(network);
   }

   @Test(dependsOnMethods = "testGetNetwork")
   public void testAttachContainerToNetwork() {
      api().connectContainerToNetwork(network.id(), container.id());
      container = api.getContainerApi().inspectContainer(container.id());
      assertTrue(Iterables.any(container.networkSettings().networks().keySet(), Predicates.equalTo(network.name())));
   }

   @Test(dependsOnMethods = "testAttachContainerToNetwork")
   public void testDisconnectContainerFromNetwork() {
      api().disconnectContainerFromNetwork(network.id(), container.id());
      container = api.getContainerApi().inspectContainer(container.id());
      assertFalse(Iterables.any(container.networkSettings().networks().keySet(), Predicates.equalTo(network.name())));
   }

   @Test(dependsOnMethods = "testCreateNetwork")
   public void testListNetworks() {
      List<Network> networks = api().listNetworks();
      for (Network network : networks) {
         assertNotNull(network.id());
      }
   }

   @Test(dependsOnMethods = "testDisconnectContainerFromNetwork")
   public void testRemoveNetwork() {
      api().removeNetwork(network.id());
      assertNull(api().inspectNetwork(network.id()));
      network = null;
   }

   private NetworkApi api() {
      return api.getNetworkApi();
   }
}
