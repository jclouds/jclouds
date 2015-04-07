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

import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.internal.BaseSoftLayerMockTest;
import org.jclouds.softlayer.parse.SoftwareDescriptionsParseTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.softlayer.features.SoftwareDescriptionApi} class.
 */
@Test(groups = "unit", testName = "SoftwareDescriptionApiMockTest")
public class SoftwareDescriptionApiMockTest extends BaseSoftLayerMockTest {

   public void testGetAllObjects() throws Exception {

      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/software_description_list.json")));
      SoftwareDescriptionApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getSoftwareDescriptionApi();

      try {
         assertEquals(api.getAllObjects(), new SoftwareDescriptionsParseTest().expected());
         assertSent(server, "GET", "/SoftLayer_Software_Description/getAllObjects?objectMask=id%3Bname%3Bversion%3BoperatingSystem%3BlongDescription%3BreferenceCode");
      } finally {
         server.shutdown();
      }
   }

}
