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

import static com.google.common.base.Preconditions.checkArgument;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jclouds.openstack.trove.v1.domain.Instance;
import org.jclouds.openstack.trove.v1.domain.User;
import org.jclouds.openstack.trove.v1.internal.BaseTroveApiLiveTest;
import org.jclouds.openstack.trove.v1.utils.TroveUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Zack Shoylev
 */
@Test(groups = "live", testName = "UserApiLiveTest")
public class UserApiLiveTest extends BaseTroveApiLiveTest {

   // zone to instance
   private static Map<String,List<Instance>> instancesToDelete = Maps.newHashMap();
   // not deleting users. they will be deleted when instances are deleted

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      TroveUtils utils= new TroveUtils(api);
      for (String zone : api.getConfiguredZones()) {
         // create instances
         List<Instance> instanceList = Lists.newArrayList();
         Instance first = utils.getWorkingInstance(zone, "first_user_trove_live_testing_" + zone, "1", 1);
         Instance second = utils.getWorkingInstance(zone, "second_user_trove_live_testing_" + zone, "1", 1);
         instanceList.add(first);
         instanceList.add(second);
         instancesToDelete.put(zone, instanceList);
         // create users
         User user1 = User.builder()
               .name("user1")
               .password(UUID.randomUUID().toString())
               .databases(ImmutableSet.of(
                     "u1db1", 
                     "u1db2")).build();
         User user2 = User.builder()
               .name("user2")
               .password(UUID.randomUUID().toString())
               .databases(ImmutableSet.of(
                     "u2db1", 
                     "u2db2")).build();
         User user3 = User.builder()
               .name("user3")
               .password(UUID.randomUUID().toString())
               .host("173.203.44.122")
               .databases(ImmutableSet.of(
                     "u3db1", 
                     "u3db2")).build();
         UserApi userApiFirst = api.getUserApiForZoneAndInstance(zone, first.getId());
         UserApi userApiSecond = api.getUserApiForZoneAndInstance(zone, second.getId());
         userApiFirst.create(ImmutableSet.of(user1, user2));
         userApiSecond.create(ImmutableSet.of(user3));
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

   private void checkUser(User user) {
      assertNotNull(user.getName(), "Name cannot be null for " + user);
      assertNotNull(user.getHost(), "Host cannot be null (should be '%' if default) for " + user);
      checkArgument(!user.getDatabases().isEmpty(), "Number of databases must not be 0");
   }

   @Test
   public void testListUsers() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instancesToDelete.get(zone)) {
            UserApi userApi = api.getUserApiForZoneAndInstance(zone, instance.getId());
            if(!instance.getName().contains("user_trove_live_testing"))continue;
            assertTrue(userApi.list().size() >=1);
            for(User user : userApi.list()){
               checkUser(user);      
            }
         }  
      }   
   }    

   @Test
   public void testGetUser() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instancesToDelete.get(zone)) {
            UserApi userApi = api.getUserApiForZoneAndInstance(zone, instance.getId());
            if(!instance.getName().contains("user_trove_live_testing"))continue;
            assertTrue(userApi.list().size() >=1);
            for(User user : userApi.list()){
               User userFromGet = userApi.get(user.getIdentifier());
               assertEquals(userFromGet.getName(), user.getName());
               assertEquals(userFromGet.getHost(), user.getHost());
               assertEquals(userFromGet.getIdentifier(), user.getIdentifier());
               assertEquals(userFromGet.getDatabases(), user.getDatabases());
               assertEquals(userFromGet, user);
            }
         }  
      } 
   }

   @Test
   public void testGetDatabaseListForUser() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2 );
         for(Instance instance : instancesToDelete.get(zone)) {
            UserApi userApi = api.getUserApiForZoneAndInstance(zone, instance.getId());
            if(!instance.getName().contains("user_trove_live_testing"))continue;
            assertTrue(userApi.list().size() >=1);
            for(User user : userApi.list()){
               assertFalse(userApi.getDatabaseList(user.getIdentifier()).isEmpty());
            }
         }  
      } 
   }

   @Test
   public void testGrantAndRevokeAcccessForUser() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instancesToDelete.get(zone)) {
            UserApi userApi = api.getUserApiForZoneAndInstance(zone, instance.getId());
            if(!instance.getName().contains("user_trove_live_testing"))continue;
            assertTrue(userApi.list().size() >=1);
            for(User user : userApi.list()){
               userApi.grant(user.getIdentifier(), "dbA");
               userApi.grant(user.getIdentifier(), ImmutableList.of(
                     "dbB", 
                     "dbC"));

               Set<String> databases = userApi.getDatabaseList(user.getIdentifier()).toSet();
               assertTrue(databases.contains("dbA"));
               assertTrue(databases.contains("dbB"));
               assertTrue(databases.contains("dbC"));

               userApi.revoke(user.getIdentifier(), "dbA");
               userApi.revoke(user.getIdentifier(), "dbB");
               userApi.revoke(user.getIdentifier(), "dbC");

               databases = userApi.getDatabaseList(user.getIdentifier()).toSet();
               assertFalse(databases.contains("dbA"));
               assertFalse(databases.contains("dbB"));
               assertFalse(databases.contains("dbC"));
            }
         }  
      } 
   }

   @Test
   public void testGetUserWhenNotFound() {
      for (String zone : api.getConfiguredZones()) {
         String instanceId = instancesToDelete.get(zone).iterator().next().getId();
         UserApi userApi = api.getUserApiForZoneAndInstance(zone, instanceId);
         assertNull(userApi.get("9999"));
      }
   }
}
