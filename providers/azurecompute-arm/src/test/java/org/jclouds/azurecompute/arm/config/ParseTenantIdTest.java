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
package org.jclouds.azurecompute.arm.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseTenantIdTest")
public class ParseTenantIdTest {

   @Test
   public void testParseTenantId() {
      AzureComputeHttpApiModule module = new AzureComputeHttpApiModule();

      assertEquals(module.provideTenant("https://login.microsoftonline.com/tenantId/oauth2/token"), "tenantId");
      assertEquals(module.provideTenant("https://login.microsoft.com/tenant2/oauth2/token"), "tenant2");
      
      assertInvalid(module, "https://login.microsoftonline.com/a/b/c/oauth2/token");
      assertInvalid(module, "https://login.microsoft.com/a/b/c/oauth2/token");
      assertInvalid(module, "https://login.microsoftonline.com//oauth2/token");
      assertInvalid(module, "https://login.microsoft.com//oauth2/token");
      assertInvalid(module, "https://login.microsoftabc.com/tenant/oauth2/token");
   }

   private static void assertInvalid(AzureComputeHttpApiModule module, String endpoint) {
      try {
         module.provideTenant(endpoint);
         fail("Expected an IllegalArgumentException for endpoint: " + endpoint);
      } catch (IllegalArgumentException ex) {
         assertEquals(ex.getMessage(), "Could not parse tenantId from: " + endpoint);
      }
   }

}
