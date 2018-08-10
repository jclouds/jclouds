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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "IsChinaEndpointTest")
public class IsChinaEndpointTest {

   @Test
   public void testIsChinaEndpoint() {
      AzureComputeHttpApiModule module = new AzureComputeHttpApiModule();

      assertTrue(module.isChinaEndpoint("https://login.chinacloudapi.cn/tenantId/oauth2/token"));
      assertFalse(module.isChinaEndpoint("http://login.chinacloudapi.cn/tenantId/oauth2/token"));
      assertFalse(module.isChinaEndpoint("https://login.chinacloudapi.cn/otherpaths/not/oauth"));
      assertFalse(module.isChinaEndpoint("https://login.microsoftonline.com/tenantId/oauth2/token"));
      assertFalse(module.isChinaEndpoint("https://login.microsoft.com/otherpaths/not/oauth"));
   }

}
