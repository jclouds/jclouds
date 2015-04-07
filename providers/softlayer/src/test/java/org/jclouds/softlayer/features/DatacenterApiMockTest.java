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
package org.jclouds.softlayer.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.internal.BaseSoftLayerMockTest;
import org.jclouds.softlayer.parse.DatacenterParseTest;
import org.jclouds.softlayer.parse.DatacentersParseTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.softlayer.features.DatacenterApi} class.
 */
@Test(groups = "unit", testName = "DatacenterApiMockTest")
public class DatacenterApiMockTest extends BaseSoftLayerMockTest {

   public void testListDatacenters() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/datacenter_list.json")));
      DatacenterApi api = getDatacenterApi(server);
      try {
         assertEquals(api.listDatacenters(), new DatacentersParseTest().expected());
         assertSent(server, "GET", "/SoftLayer_Location_Datacenter/Datacenters?objectMask=locationAddress%3Bregions");
      } finally {
         server.shutdown();
      }
   }


   public void testEmptyListDatacenters() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      DatacenterApi api = getDatacenterApi(server);
      try {
         assertTrue(api.listDatacenters().isEmpty());
         assertSent(server, "GET", "/SoftLayer_Location_Datacenter/Datacenters?objectMask=locationAddress%3Bregions");
      } finally {
         server.shutdown();
      }
   }

   public void testGetDatacenter() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/datacenter_get.json")));
      DatacenterApi api = getDatacenterApi(server);
      try {
         assertEquals(api.getDatacenter(265592), new DatacenterParseTest().expected());
         assertSent(server, "GET", "/SoftLayer_Location_Datacenter/265592?objectMask=locationAddress%3Bregions");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullDatacenter() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      DatacenterApi api = getDatacenterApi(server);
      try {
         assertNull(api.getDatacenter(265592));
         assertSent(server, "GET", "/SoftLayer_Location_Datacenter/265592?objectMask=locationAddress%3Bregions");
      } finally {
         server.shutdown();
      }
   }

   private DatacenterApi getDatacenterApi(MockWebServer server) {
      return api(SoftLayerApi.class, server.getUrl("/").toString()).getDatacenterApi();
   }
}
