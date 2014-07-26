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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.nova.v2_0.domain.Console;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

/**
 * Tests behavior of {@code ConsolesApi}
 */
@Test(groups = "live", testName = "ConsolesApiLiveTest")
public class ConsolesApiLiveTest extends BaseNovaApiLiveTest {

   public void testGetNOVNCConsole() {
      testGetConsole(Console.Type.NOVNC);
   }

   public void testGetXVPVNCConsole() {
      testGetConsole(Console.Type.XVPVNC);
   }

   private void testGetConsole(Console.Type consoleType) {
      for (String regionId : api.getConfiguredRegions()) {
         Optional<? extends ConsolesApi> apiOption = api.getConsolesApi(regionId);
         if (!apiOption.isPresent()) {
            System.err.println("Consoles extension not present in server.");
            continue;
         }

         ConsolesApi api = apiOption.get();
         ServerApi serverApi = this.api.getServerApi(regionId);
         Server server = createServerInRegion(regionId);
         Console console = api.getConsole(server.getId(), consoleType);
         assertNotNull(console.getType());
         assertTrue(consoleType.equals(console.getType()));
         assertNotNull(console.getUrl());
         assertTrue(console.getUrl().toString().startsWith("http"));
         serverApi.delete(server.getId());
      }
   }

}
