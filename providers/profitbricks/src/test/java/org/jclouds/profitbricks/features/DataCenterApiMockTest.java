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
import static org.testng.Assert.fail;

import java.util.List;

import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.profitbricks.features.DataCenterApi} class
 */
@Test(groups = "unit", testName = "DataCenterApiMockTest")
public class DataCenterApiMockTest extends BaseProfitBricksMockTest {

   @Test
   public void testGetAllDataCenters() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/datacenter/datacenters.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();

      try {
         List<DataCenter> dataCenters = api.getAllDataCenters();
         assertRequestHasCommonProperties(server.takeRequest(), "<ws:getAllDataCenters/>");
         assertNotNull(dataCenters);
         assertEquals(dataCenters.size(), 2);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetAllDataCentersReturning404() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();

      try {
         List<DataCenter> dataCenters = api.getAllDataCenters();
         assertRequestHasCommonProperties(server.takeRequest());
         assertTrue(dataCenters.isEmpty());
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetDataCenter() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/datacenter/datacenter.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();

      String id = "12345678-abcd-efgh-ijkl-987654321000";
      String content = "<ws:getDataCenter><dataCenterId>" + id + "</dataCenterId></ws:getDataCenter>";
      try {
         DataCenter dataCenter = api.getDataCenter(id);
         assertRequestHasCommonProperties(server.takeRequest(), content );
         assertNotNull(dataCenter);
         assertEquals(dataCenter.id(), id);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetNonExistingDataCenter() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();

      String id = "random-non-existing-id";
      try {
         DataCenter dataCenter = api.getDataCenter(id);
         assertRequestHasCommonProperties(server.takeRequest());
         assertNull(dataCenter);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetDataCenterState() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/datacenter/datacenter-state.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();

      String id = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
      String content = "<ws:getDataCenterState><dataCenterId>" + id + "</dataCenterId></ws:getDataCenterState>";
      try {
         ProvisioningState state = api.getDataCenterState(id);
         assertRequestHasCommonProperties(server.takeRequest(), content );
         assertNotNull(state);
         assertEquals(state, ProvisioningState.AVAILABLE);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testCreateDataCenter() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/datacenter/datacenter-created.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();

      String content = "<ws:createDataCenter><request>"
              + "<dataCenterName>JClouds-DC</dataCenterName>"
              + "<location>de/fra</location>"
              + "</request></ws:createDataCenter>";
      try {
         DataCenter dataCenter = api.createDataCenter(
                 DataCenter.Request.CreatePayload.create("JClouds-DC", Location.DE_FRA)
         );
         assertRequestHasCommonProperties(server.takeRequest(), content );
         assertNotNull(dataCenter);
         assertEquals(dataCenter.id(), "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
         assertEquals(dataCenter.version(), 1);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testCreateDataCenterWithIllegalArguments() throws Exception {
      String[] names = {"JCl@ouds", "JC|ouds", "^clouds", ""};
      for (String name : names) {
         try {
            DataCenter.Request.CreatePayload.create(name, Location.US_LAS);
            fail("Should have failed for name: ".concat(name));
         } catch (IllegalArgumentException ex) {
            // expected exception
         }
      }
   }

   @Test
   public void testUpdateDataCenter() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/datacenter/datacenter-updated.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();
      
      String id = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
      String newName = "Apache";
      
      String content = "<ws:updateDataCenter><request>"
              + "<dataCenterId>" + id + "</dataCenterId>"
              + "<dataCenterName>" + newName + "</dataCenterName>"
              + "</request></ws:updateDataCenter>";
      try {
         DataCenter dataCenter = api.updateDataCenter(
                 DataCenter.Request.UpdatePayload.create(id, newName)
         );
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(dataCenter);
         assertEquals(dataCenter.id(), id);
         assertEquals(dataCenter.version(), 2);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testClearDataCenter() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/datacenter/datacenter-cleared.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();

      String id = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
      
      String content = "<ws:clearDataCenter><dataCenterId>" + id + "</dataCenterId></ws:clearDataCenter>";
      try {
         DataCenter dataCenter = api.clearDataCenter(id);

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(dataCenter);
         assertEquals(dataCenter.id(), id);
         assertEquals(dataCenter.version(), 3);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteDataCenter() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/datacenter/datacenter-deleted.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();

      String id = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
      
      String content = "<ws:deleteDataCenter><dataCenterId>" + id + "</dataCenterId></ws:deleteDataCenter>";
      try {
         boolean result = api.deleteDataCenter(id);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertTrue(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteNonExistingDataCenter() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode( 404 ));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      DataCenterApi api = pbApi.dataCenterApi();

      try {
         boolean result = api.deleteDataCenter("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
         assertRequestHasCommonProperties(server.takeRequest());
         assertFalse(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }
}
