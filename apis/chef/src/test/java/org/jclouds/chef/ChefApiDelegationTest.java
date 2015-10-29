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
package org.jclouds.chef;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.jclouds.ContextBuilder;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ChefApiDelegationTest")
public class ChefApiDelegationTest {

   public void testOrganizationApiNotAvailableInOldVersions() throws IOException {
      ChefApi chef = ContextBuilder.newBuilder(new ChefApiMetadata())
            .credentials("foo", "bar")
            .apiVersion("11.0.4")
            .buildApi(ChefApi.class);
      
      try {
         assertFalse(chef.organizationApi().isPresent());
      } finally {
         chef.close();
      }
   }
   
   public void testOrganizationApiPresentInRecentVersions() throws IOException {
      ChefApi chef = ContextBuilder.newBuilder(new ChefApiMetadata())
            .credentials("foo", "bar")
            .apiVersion("12.0.4")
            .buildApi(ChefApi.class);
      
      try {
         assertTrue(chef.organizationApi().isPresent());
      } finally {
         chef.close();
      }
   }
}
