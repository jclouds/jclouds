/*
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

package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;
import java.util.Properties;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Module;

/**
 * @author David Alves
 */
public class InstanceApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String INSTANCE_NETWORK_NAME = "instance-api-live-test-network";
   private static final String INSTANCE_NAME = "instance-api-live-test-instance";
   private static final String DISK_NAME = "instance-live-test-disk";
   private static final String IPV4_RANGE = "10.0.0.0/8";
   private static final int TIME_WAIT = 600;

   private InstanceTemplate instance;
   
   @Override
   protected GoogleComputeEngineApi create(Properties props, Iterable<Module> modules) {
      GoogleComputeEngineApi api = super.create(props, modules);
      instance = InstanceTemplate.builder()
            .forMachineType(getDefaultMachineTypekUrl(userProject.get()))
            .addNetworkInterface(getNetworkUrl(userProject.get(), INSTANCE_NETWORK_NAME),
                    Instance.NetworkInterface.AccessConfig.Type.ONE_TO_ONE_NAT)
            .addMetadata("mykey", "myvalue")
            .addTag("atag")
            .description("a description")
            .addDisk(InstanceTemplate.PersistentDisk.Mode.READ_WRITE, getDiskUrl(userProject.get(), DISK_NAME))
            .zone(getDefaultZoneUrl(userProject.get()));
      return api;
   }

   private InstanceApi api() {
      return api.getInstanceApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertInstance() {

      // need to create the network first
      assertOperationDoneSucessfully(api.getNetworkApiForProject(userProject.get()).createInIPv4Range
              (INSTANCE_NETWORK_NAME, IPV4_RANGE), TIME_WAIT);

      assertOperationDoneSucessfully(api.getDiskApiForProject(userProject.get()).createInZone
              ("instance-live-test-disk", 1, getDefaultZoneUrl(userProject.get())), TIME_WAIT);

      assertOperationDoneSucessfully(api().createInZone(INSTANCE_NAME, instance, DEFAULT_ZONE_NAME), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testGetInstance() {

      Instance instance = api().get(INSTANCE_NAME);
      assertNotNull(instance);
      assertInstanceEquals(instance, this.instance);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testListInstance() {

      PagedIterable<Instance> instances = api().list(new ListOptions.Builder()
              .filter("name eq " + INSTANCE_NAME));

      List<Instance> instancesAsList = Lists.newArrayList(instances.concat());

      assertEquals(instancesAsList.size(), 1);

      assertInstanceEquals(Iterables.getOnlyElement(instancesAsList), instance);

   }

   @Test(groups = "live", dependsOnMethods = "testListInstance")
   public void testDeleteInstance() {

      assertOperationDoneSucessfully(api().delete(INSTANCE_NAME), TIME_WAIT);
      assertOperationDoneSucessfully(api.getDiskApiForProject(userProject.get()).delete(DISK_NAME),
              TIME_WAIT);
      assertOperationDoneSucessfully(api.getNetworkApiForProject(userProject.get()).delete
              (INSTANCE_NETWORK_NAME), TIME_WAIT);
   }

   private void assertInstanceEquals(Instance result, InstanceTemplate expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getTags(), expected.getTags());
      assertEquals(result.getMetadata(), expected.getMetadata());
   }
}
