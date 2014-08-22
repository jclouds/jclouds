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
package org.jclouds.openstack.neutron.v2.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiMockTest;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests annotation parsing of {@code ExtensionApi}
 */
@Test(groups = "unit", testName = "ExtensionApiMockTest")
public class ExtensionApiMockTest extends BaseNeutronApiMockTest {

   public void testListExtensions() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse()
         .setResponseCode(200).setBody(stringFromResource("/extension_list.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         ExtensionApi api = neutronApi.getExtensionApi("RegionOne");

         Set<Extension> extensions = api.list();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertExtensions(server, "/v2.0");

         /*
          * Check response
          */
         assertNotNull(extensions);
         assertEquals(extensions.size(), 17);
      } finally {
         server.shutdown();
      }
   }

   public void testListExtensionsIsEmpty() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         ExtensionApi api = neutronApi.getExtensionApi("RegionOne");

         Set<Extension> extensions = api.list();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/extensions");

         /*
          * Check response
          */
         assertTrue(extensions.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetExtensionByAlias() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse()
         .setResponseCode(200).setBody(stringFromResource("/extension_details.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);

         Extension routerExtension = neutronApi.getExtensionApi("RegionOne").get("router");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/extensions/router");

         /*
          * Check response
          */
         assertNotNull(routerExtension);
         assertEquals(routerExtension.getName(), "Neutron L3 Router");
      } finally {
         server.shutdown();
      }
   }

   public void testGetExtensionByAliasFail() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);

         Extension routerExtension = neutronApi.getExtensionApi("RegionOne").get("router");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/extensions/router");

         /*
          * Check response
          */
         assertNull(routerExtension);
      } finally {
         server.shutdown();
      }

   }

}
