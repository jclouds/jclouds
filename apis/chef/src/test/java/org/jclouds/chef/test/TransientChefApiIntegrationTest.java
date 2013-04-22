/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.internal.BaseChefLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TransientChefApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = { "integration" })
public class TransientChefApiIntegrationTest extends BaseChefLiveTest<ChefApi> {
   public static final String PREFIX = System.getProperty("user.name") + "-jcloudstest";
   private DatabagItem databagItem;

   public TransientChefApiIntegrationTest() {
      provider = "transientchef";
   }

   @Override
   protected Properties setupProperties() {
      return new Properties();
   }

   public void testCreateDatabag() {
      api.deleteDatabag(PREFIX);
      api.createDatabag(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateDatabag")
   public void testDatabagExists() {
      assertNotNull(api.databagExists(PREFIX));
   }

   @Test(dependsOnMethods = { "testDatabagExists" })
   public void testCreateDatabagItem() {
      Properties config = new Properties();
      config.setProperty("foo", "bar");
      databagItem = api.createDatabagItem(PREFIX, new DatabagItem("config", json.toJson(config)));
      assertNotNull(databagItem);
      assertEquals(databagItem.getId(), "config");
      assertEquals(config, json.fromJson(databagItem.toString(), Properties.class));
   }

   @Test(dependsOnMethods = "testCreateDatabagItem")
   public void testDatabagItemExists() {
      assertNotNull(api.databagItemExists(PREFIX, PREFIX));
   }

   @Test(dependsOnMethods = "testDatabagItemExists")
   public void testUpdateDatabagItem() {
      for (String databagItemId : api.listDatabagItems(PREFIX)) {
         DatabagItem databagItem = api.getDatabagItem(PREFIX, databagItemId);
         api.updateDatabagItem(PREFIX, databagItem);
      }
   }

}
