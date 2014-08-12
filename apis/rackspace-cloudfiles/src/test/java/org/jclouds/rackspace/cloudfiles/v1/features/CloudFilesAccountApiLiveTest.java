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
package org.jclouds.rackspace.cloudfiles.v1.features;

import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.swift.v1.domain.Account;
import org.jclouds.openstack.swift.v1.features.AccountApi;
import org.jclouds.openstack.swift.v1.features.AccountApiLiveTest;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;
import org.testng.annotations.Test;

/**
 * Tests the live behavior of the OpenStack Object Storage {@link AccountApi}
 * via the {@link CloudFilesApi}.
 */
@Test(groups = "live", testName = "CloudFilesAccountApiLiveTest")
public class CloudFilesAccountApiLiveTest extends AccountApiLiveTest {

   public CloudFilesAccountApiLiveTest() {
      provider = "rackspace-cloudfiles";
   }

   public void testUrlKeyExists() throws Exception {
      for (String regionId : regions) {
         Account account = api.getAccountApi(regionId).get();
         assertTrue(account.getTemporaryUrlKey().isPresent());
      }
   }
}
