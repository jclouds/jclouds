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
package org.jclouds.openstack.keystone.v3.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.openstack.keystone.v3.domain.Endpoint;
import org.jclouds.openstack.keystone.v3.internal.BaseV3KeystoneApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "CatalogApiMockTest", singleThreaded = true)
public class CatalogApiMockTest extends BaseV3KeystoneApiMockTest {

   public void testListEndpoints() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/endpoints.json"));

      List<Endpoint> endpoints = api.getCatalogApi().endpoints();
      assertFalse(endpoints.isEmpty());
      
      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/endpoints");
   }

   public void testListEndpointsReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      List<Endpoint> endpoints = api.getCatalogApi().endpoints();
      assertTrue(endpoints.isEmpty());
      
      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/endpoints");
   }
   
}
