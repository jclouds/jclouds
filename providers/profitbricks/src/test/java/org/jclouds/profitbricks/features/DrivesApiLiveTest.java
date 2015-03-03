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
package org.jclouds.profitbricks.features;

import autovalue.shaded.com.google.common.common.collect.Iterables;
import java.util.List;
import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.Drive;
import org.jclouds.profitbricks.domain.Image;
import org.jclouds.profitbricks.domain.Server;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "DrivesApiLiveTest", singleThreaded = true)
public class DrivesApiLiveTest extends BaseProfitBricksLiveTest {

   public String serverId;
   public String imageId;

   @Override
   protected void initialize() {
      super.initialize();

      List<Server> servers = api.serverApi().getAllServers();
      assertFalse(servers.isEmpty(), "At least one server is required to run drives test.");

      Server server = Iterables.getFirst(servers, null);
      assertNotNull(server);

      this.serverId = server.id();

      List<Image> images = api.imageApi().getAllImages();
      assertFalse(images.isEmpty(), "At least one image is required to run drives test.");

      Image image = Iterables.getFirst(images, null);
      assertNotNull(image);

      this.imageId = image.id();
   }

   @Test
   public void addRomDriveToServerTest() {
      String requestId = api.drivesApi().addRomDriveToServer(Drive.Request.AddRomDriveToServerPayload.builder()
              .serverId(serverId)
              .storageId("05cadf29-6c12-11e4-beeb-52540066fee9")
              .deviceNumber("0")
              .build());
      assertNotNull(requestId);
   }

   @Test (dependsOnMethods = "addRomDriveToServerTest")
   public void removeRomDriveFromServerTest() {
      String requestId = api.drivesApi().removeRomDriveFromServer(imageId, serverId);

      assertNotNull(requestId);
   }
}
