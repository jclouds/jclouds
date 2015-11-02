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
package org.jclouds.googlecomputeengine.features;

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.NewTargetInstance;
import org.jclouds.googlecomputeengine.domain.TargetInstance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.inject.Module;


public class TargetInstanceApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String INSTANCE_NAME = "test-target-instance-source-1";
   private static final String INSTANCE_NETWORK_NAME = "test-target-instance-test-network";
   private static final String IPV4_RANGE = "10.0.0.0/8";
   private static final String TARGET_INSTANCE_NAME = "test-target-instance-1";
   
   private Instance instance;
   
   @Override
   protected GoogleComputeEngineApi create(Properties props, Iterable<Module> modules) {
      GoogleComputeEngineApi api = super.create(props, modules);
      List<Image> list = api.images().listInProject("centos-cloud", filter("name eq centos.*")).next();
      URI imageUri = FluentIterable.from(list)
                        .filter(new Predicate<Image>() {
                           @Override
                           public boolean apply(Image input) {
                              // filter out all deprecated images
                              return !(input.deprecated() != null && input.deprecated().state() != null);
                           }
                        })
                        .first()
                        .get()
                        .selfLink();

      NewInstance newInstance = NewInstance.create(
            INSTANCE_NAME, // name
            getDefaultMachineTypeUrl(), // machineType
            getNetworkUrl(INSTANCE_NETWORK_NAME), // network
            imageUri);
      
      // need to insert the network first
      assertOperationDoneSuccessfully(api.networks().createInIPv4Range
              (INSTANCE_NETWORK_NAME, IPV4_RANGE));
      assertOperationDoneSuccessfully(api.instancesInZone(DEFAULT_ZONE_NAME).create(newInstance));
      instance = api.instancesInZone(DEFAULT_ZONE_NAME).get(INSTANCE_NAME);
      assertNotNull(instance);

      return api;
   }

   @Test(groups = "live")
   public void testInsertTargetInstance(){
      NewTargetInstance newTargetInstance = new NewTargetInstance.Builder()
         .name(TARGET_INSTANCE_NAME)
         .description("A test Target Instance")
         .instance(instance.selfLink())
         .build();

      assertOperationDoneSuccessfully(api.targetInstancesInZone(DEFAULT_ZONE_NAME).create(newTargetInstance));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetInstance")
   public void testGetTargetInstance(){
      TargetInstance targetInstance = api.targetInstancesInZone(DEFAULT_ZONE_NAME).get(TARGET_INSTANCE_NAME);

      assertNotNull(targetInstance);
      assertEquals(targetInstance.name(), TARGET_INSTANCE_NAME);
      assertEquals(targetInstance.description(), "A test Target Instance");
      assertEquals(targetInstance.zone(), getZoneUrl(DEFAULT_ZONE_NAME));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetInstance", alwaysRun = true)
   public void testListTargetInstance(){
      ListPage<TargetInstance> targetInstances = api.targetInstancesInZone(DEFAULT_ZONE_NAME)
            .list(filter("name eq " + TARGET_INSTANCE_NAME)).next();

      assertEquals(targetInstances.size(), 1);
      assertTargetInstanceEquals(Iterables.getOnlyElement(targetInstances));
   }

   @Test(groups = "live", dependsOnMethods = {"testListTargetInstance", "testGetTargetInstance"}, alwaysRun = true)
   public void testDeleteTargetInstance(){
      assertOperationDoneSuccessfully(api.targetInstancesInZone(DEFAULT_ZONE_NAME).delete(TARGET_INSTANCE_NAME));
      assertOperationDoneSuccessfully(api.instancesInZone(DEFAULT_ZONE_NAME).delete(INSTANCE_NAME));
      assertOperationDoneSuccessfully(api.networks().delete(INSTANCE_NETWORK_NAME));
   }

   private void assertTargetInstanceEquals(TargetInstance targetInstance){
      assertNotNull(targetInstance);
      assertEquals(targetInstance.name(), TARGET_INSTANCE_NAME);
      assertEquals(targetInstance.description(), "A test Target Instance");
      assertEquals(targetInstance.zone(), getZoneUrl(DEFAULT_ZONE_NAME));
   }
}
