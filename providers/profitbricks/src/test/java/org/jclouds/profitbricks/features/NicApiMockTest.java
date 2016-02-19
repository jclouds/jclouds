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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "NicApiMockTest")

public class NicApiMockTest extends BaseProfitBricksMockTest {

   @Test
   public void testGetNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/nic/nic.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      NicApi api = pbApi.nicApi();

      String id = "12345678-abcd-efgh-ijkl-987654321000";

      String content = "<ws:getNic><nicId>" + id + "</nicId></ws:getNic>";
      try {
         Nic nic = api.getNic(id);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(nic);
         assertEquals(nic.id(), id);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetNonExistingNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      NicApi api = pbApi.nicApi();

      String id = "nonexisting-nic-id";

      try {
         Nic nic = api.getNic(id);
         assertRequestHasCommonProperties(server.takeRequest());
         assertNull(nic);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetAllNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/nic/nics.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      NicApi api = pbApi.nicApi();
      try {
         List<Nic> nics = api.getAllNics();
         assertRequestHasCommonProperties(server.takeRequest(), "<ws:getAllNic/>");
         assertNotNull(nics);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testCreateNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/nic/nic-create.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      NicApi api = pbApi.nicApi();

      String content = "<ws:createNic>"
              + "<request>"
              + "<ip>192.168.0.1</ip>"
              + "<nicName>nic-name</nicName>"
              + "<dhcpActive>true</dhcpActive>"
              + "<serverId>server-id</serverId>"
              + "<lanId>1</lanId>"
              + "</request>"
              + "</ws:createNic>";

      try {
         String nicId = api.createNic(
                 Nic.Request.creatingBuilder()
                 .ip("192.168.0.1")
                 .name("nic-name")
                 .dhcpActive(true)
                 .lanId(1)
                 .serverId("server-id")
                 .build());

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(nicId);

      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testUpdateNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/nic/nic-update.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      NicApi api = pbApi.nicApi();

      String content = "<ws:updateNic>"
              + "<request>"
              + "<nicId>nic-id</nicId>"
              + "<ip>10.0.0.1</ip>"
              + "<nicName>nic-name</nicName>"
              + "<dhcpActive>true</dhcpActive>"
              + "<lanId>1</lanId>"
              + "</request>"
              + "</ws:updateNic>";
      try {
         String requestId = api.updateNic(Nic.Request.updatingBuilder()
                 .id("nic-id")
                 .ip("10.0.0.1")
                 .name("nic-name")
                 .dhcpActive(true)
                 .lanId(1)
                 .build());
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertEquals(requestId, "request-id");
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testSetInternetAccess() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/nic/nic-internetaccess.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      NicApi api = pbApi.nicApi();

      String content = "<ws:setInternetAccess>"
              + "<dataCenterId>datacenter-id</dataCenterId>"
              + "<lanId>1</lanId>"
              + "<internetAccess>true</internetAccess>"
              + "</ws:setInternetAccess>";
      try {
         String requestId = api.setInternetAccess(Nic.Request.setInternetAccessBuilder()
                 .dataCenterId("datacenter-id")
                 .lanId(1)
                 .internetAccess(true)
                 .build());
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertEquals(requestId, "request-id");
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/nic/nic-delete.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      NicApi api = pbApi.nicApi();

      String id = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

      String content = "<ws:deleteNic><nicId>" + id + "</nicId></ws:deleteNic>";

      try {
         boolean result = api.deleteNic(id);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertTrue(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteNonExistingNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      NicApi api = pbApi.nicApi();

      String id = "nonexisting-nic-id";

      try {
         boolean result = api.deleteNic(id);
         assertRequestHasCommonProperties(server.takeRequest());
         assertFalse(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

}
