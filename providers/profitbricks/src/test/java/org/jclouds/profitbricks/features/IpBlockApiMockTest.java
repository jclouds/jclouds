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

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.util.List;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.IpBlock;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "IpBlockApiMockTest")
public class IpBlockApiMockTest extends BaseProfitBricksMockTest {

   @Test
   public void testGetOneIpBlock() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/ipblock/ipblock.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      IpBlockApi api = pbApi.ipBlockApi();

      String id = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";

      String content = "<ws:getPublicIpBlock><blockId>" + id + "</blockId></ws:getPublicIpBlock>";

      try {
         IpBlock ipBlock = api.getIpBlock(id);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(ipBlock);
         assertEquals(ipBlock.id(), id);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetNonExisingIpBlock() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      IpBlockApi api = pbApi.ipBlockApi();

      String id = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";

      try {
         IpBlock ipBlock = api.getIpBlock(id);
         assertRequestHasCommonProperties(server.takeRequest());
         assertNull(ipBlock);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetAllIpBlock() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/ipblock/ipblocks.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      IpBlockApi api = pbApi.ipBlockApi();

      try {
         List<IpBlock> ipBlocks = api.getAllIpBlock();
         assertRequestHasCommonProperties(server.takeRequest());
         assertNotNull(ipBlocks);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetAllIpBlockReturning404() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      IpBlockApi api = pbApi.ipBlockApi();

      try {
         List<IpBlock> ipBlocks = api.getAllIpBlock();
         assertRequestHasCommonProperties(server.takeRequest());
         assertTrue(ipBlocks.isEmpty());
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testReservePublicIpBlock() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/ipblock/ipblock-reserve.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      IpBlockApi api = pbApi.ipBlockApi();

      String blockSize = "2";
      Location location = Location.US_LAS;

      String content = "<ws:reservePublicIpBlock><request><blockSize>" + blockSize + "</blockSize><location>" + location.value() + "</location></request></ws:reservePublicIpBlock>";
      try {
         IpBlock ipBlock = api.reservePublicIpBlock(blockSize, location.value());
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(ipBlock);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testAddPublicIpToNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/ipblock/ipblock-addtonic.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      IpBlockApi api = pbApi.ipBlockApi();

      String ip = "2";
      String nicid = "nicid";

      String content = "<ws:addPublicIpToNic><ip>" + ip + "</ip><nicId>" + nicid + "</nicId></ws:addPublicIpToNic>";
      try {
         String requestId = api.addPublicIpToNic(ip, nicid);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(requestId);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testRemovePublicIpFromNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/ipblock/ipblock-removefromnic.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      IpBlockApi api = pbApi.ipBlockApi();

      String ip = "2";
      String nicid = "nicid";

      String content = "<ws:removePublicIpFromNic><ip>" + ip + "</ip><nicId>" + nicid + "</nicId></ws:removePublicIpFromNic>";
      try {
         String requestId = api.removePublicIpFromNic(ip, nicid);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(requestId);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testReleasePublicIpBlock() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/ipblock/ipblock-release.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      IpBlockApi api = pbApi.ipBlockApi();

      String blockid = "2";

      String content = "<ws:releasePublicIpBlock><blockId>" + blockid + "</blockId></ws:releasePublicIpBlock>";
      try {
         String requestId = api.releasePublicIpBlock(blockid);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(requestId);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }
}
