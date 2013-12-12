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
package org.jclouds.openstack.trove.v1.features;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.jclouds.openstack.trove.v1.domain.Instance;
import org.jclouds.openstack.trove.v1.internal.BaseTroveApiLiveTest;
import org.jclouds.openstack.trove.v1.utils.TroveUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Zack Shoylev
 */
@Test(groups = "live", testName = "DatabaseApiLiveTest")
public class DatabaseApiLiveTest extends BaseTroveApiLiveTest {

   // zone to instance
   private static Map<String,List<Instance>> instancesToDelete = Maps.newHashMap();
   // not deleting databases. they will be deleted when instances are deleted

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      TroveUtils utils = new TroveUtils(api);
      for (String zone : api.getConfiguredZones()) {
         // create instances
         List<Instance> instanceList = Lists.newArrayList();
         Instance first = utils.getWorkingInstance(zone, "first_database_testing_" + zone, "1", 1);
         Instance second = utils.getWorkingInstance(zone, "second_database_testing_" + zone, "1", 1);
         instanceList.add(first);
         instanceList.add(second);
         instancesToDelete.put(zone, instanceList);

         DatabaseApi databaseApiFirst = api.getDatabaseApiForZoneAndInstance(zone, first.getId());
         DatabaseApi databaseApiSecond = api.getDatabaseApiForZoneAndInstance(zone, second.getId());
         databaseApiFirst.create("livetest_db1");
         databaseApiFirst.create("livetest_db2");
         databaseApiSecond.create("livetest_db3");
      }
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   public void tearDown(){
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         for(Instance instance : instancesToDelete.get(zone)){
            if( !instanceApi.delete(instance.getId() ) )
               throw new RuntimeException("Could not delete a database instance after tests!");
         }
      }
      super.tearDown();
   }

   @Test
   public void testListDatabases() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instancesToDelete.get(zone)) {
            DatabaseApi databaseApi = api.getDatabaseApiForZoneAndInstance(zone, instance.getId());
            if(!instance.getName().contains("database_testing"))continue;
            assertTrue(databaseApi.list().size() >=1);
            for(String database : databaseApi.list()){
               assertNotNull(database);      
            }
         }  
      }   
   }

   @Test
   public void testDeleteDatabases() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instancesToDelete.get(zone)) {
            DatabaseApi databaseApi = api.getDatabaseApiForZoneAndInstance(zone, instance.getId());
            if(!instance.getName().contains("database_testing"))continue;
            assertTrue(databaseApi.list().size() >=1);
            for(String database : databaseApi.list()){
               assertNotNull(database);
               assertTrue(database.equals("livetest_db1") || database.equals("livetest_db2") || database.equals("livetest_db3") );
               assertTrue(databaseApi.delete(database));
               assertTrue(databaseApi.create(database));
            }
         }  
      }   
   }
}
