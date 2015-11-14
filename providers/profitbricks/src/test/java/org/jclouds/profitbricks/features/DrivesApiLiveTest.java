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

import static org.testng.Assert.assertNotNull;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Drive;
import org.jclouds.profitbricks.domain.Image;
import org.jclouds.profitbricks.domain.Server;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "DrivesApiLiveTest")
public class DrivesApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Server server;
   private Image image;

   @BeforeClass
   public void setupTest() {
      dataCenter = findOrCreateDataCenter("drivesApiLiveTest" + System.currentTimeMillis());
      server = findOrCreateServer(dataCenter);
      image = Iterables.tryFind(api.imageApi().getAllImages(), new Predicate<Image>() {

         @Override
         public boolean apply(Image input) {
            return input.location() == dataCenter.location()
                    && input.type() == Image.Type.CDROM;
         }
      }).get();
   }

   @Test
   public void addRomDriveToServerTest() {
      assertDataCenterAvailable(dataCenter);
      String requestId = api.drivesApi().addRomDriveToServer(
              Drive.Request.AddRomDriveToServerPayload.builder()
              .serverId(server.id())
              .imageId(image.id())
              .deviceNumber("0")
              .build());
      assertNotNull(requestId);
   }

   @Test(dependsOnMethods = "addRomDriveToServerTest")
   public void removeRomDriveFromServerTest() {
      assertDataCenterAvailable(dataCenter);
      String requestId = api.drivesApi().removeRomDriveFromServer(image.id(), server.id());
      assertNotNull(requestId);
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      destroyDataCenter(dataCenter);
   }
}
