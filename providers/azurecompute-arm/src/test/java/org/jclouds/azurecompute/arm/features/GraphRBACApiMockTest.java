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
package org.jclouds.azurecompute.arm.features;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.azurecompute.arm.domain.ServicePrincipal;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "GraphRBACApiMockTest", singleThreaded = true)
public class GraphRBACApiMockTest extends BaseAzureComputeApiMockTest {

   public void testGetCurrentServicePrincipal() throws IOException, InterruptedException {
      server.enqueue(jsonResponse("/serviceprincipals.json"));

      ServicePrincipal sp = api.getGraphRBACApi().getCurrentServicePrincipal();

      assertEquals(sp.appId(), "applicationId");
      assertSent(server, "GET", "/graphrbac/tenant-id/servicePrincipals?$filter=appId%20eq%20%27mock%27&api-version=1.6");
   }

}
