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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;

import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling.OnHostMaintenance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.parse.ParseZoneOperationTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "InstanceApiMockTest", singleThreaded = true)
public class InstanceApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void setDiskAutoDeleteResponseIs2xx() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      InstanceApi instanceApi = api().instancesInZone("us-central1-a");

      Operation o = instanceApi.setDiskAutoDelete("test-1", "test-disk-1", true);
      int port = server.getPort();
      // Endpoint is different for URIs such as zone and selfLink.
      assertEquals(o, new ParseZoneOperationTest().expected("http://localhost:" + port + "/projects"));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/setDiskAutoDelete"
          + "?deviceName=test-disk-1&autoDelete=true");
   }

   public void setScheduling() throws Exception {
      server.enqueue(jsonResponse("/zone_operation.json"));

      InstanceApi instanceApi = api().instancesInZone("us-central1-a");

      assertEquals(instanceApi.setScheduling("test-1", OnHostMaintenance.TERMINATE, true),
            new ParseZoneOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances/test-1/setScheduling",
            "{\"onHostMaintenance\": \"TERMINATE\",\"automaticRestart\": true}");
   }
}
