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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.AttachedDisk;
import org.jclouds.googlecomputeengine.domain.Instance.PersistentAttachedDisk;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions.DiskMode;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions.DiskType;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Module;

public class InstanceApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String INSTANCE_NETWORK_NAME = "instance-api-live-test-network";
   private static final String INSTANCE_NAME = "instance-api-live-test-instance";
   private static final String BOOT_DISK_NAME = INSTANCE_NAME + "-boot-disk";
   private static final String DISK_NAME = "instance-live-test-disk";
   private static final String IPV4_RANGE = "10.0.0.0/8";
   private static final String METADATA_ITEM_KEY = "instanceLiveTestTestProp";
   private static final String METADATA_ITEM_VALUE = "instanceLiveTestTestValue";
   private static final String ATTACH_DISK_NAME = "instance-api-live-test-attach-disk";
   private static final String ATTACH_DISK_DEVICE_NAME = "attach-disk-1";

   private static final int TIME_WAIT = 600;

   private InstanceTemplate instance;

   @Override
   protected GoogleComputeEngineApi create(Properties props, Iterable<Module> modules) {
      GoogleComputeEngineApi api = super.create(props, modules);
      URI imageUri = api.getImageApiForProject("centos-cloud")
                        .list(new ListOptions.Builder().filter("name eq centos.*"))
                        .concat()
                        .filter(new Predicate<Image>() {
                           @Override
                           public boolean apply(Image input) {
                              // filter out all deprecated images
                              return !(input.getDeprecated().isPresent() && input.getDeprecated().get().getState().isPresent());
                           }
                        })
                        .first()
                        .get()
                        .getSelfLink();
      instance = InstanceTemplate.builder()
              .forMachineType(getDefaultMachineTypeUrl(userProject.get()))
              .addNetworkInterface(getNetworkUrl(userProject.get(), INSTANCE_NETWORK_NAME),
                                   Instance.NetworkInterface.AccessConfig.Type.ONE_TO_ONE_NAT)
              .addMetadata("mykey", "myvalue")
              .description("a description")
              .addDisk(InstanceTemplate.PersistentDisk.Mode.READ_WRITE, getDiskUrl(userProject.get(), BOOT_DISK_NAME),
                       null, true, true)
              .addDisk(InstanceTemplate.PersistentDisk.Mode.READ_WRITE, getDiskUrl(userProject.get(), DISK_NAME))
              .image(imageUri);

      return api;
   }

   private InstanceApi api() {
      return api.getInstanceApiForProject(userProject.get());
   }

   private DiskApi diskApi() {
      return api.getDiskApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertInstance() {

      // need to create the network first
      assertGlobalOperationDoneSucessfully(api.getNetworkApiForProject(userProject.get()).createInIPv4Range
              (INSTANCE_NETWORK_NAME, IPV4_RANGE), TIME_WAIT);


      assertZoneOperationDoneSucessfully(api.getDiskApiForProject(userProject.get())
                                        .createFromImageInZone(instance.getImage().toString(),
                                                               BOOT_DISK_NAME,
                                                               DEFAULT_ZONE_NAME),
                                         TIME_WAIT);


      assertZoneOperationDoneSucessfully(diskApi().createInZone
              ("instance-live-test-disk", 10, DEFAULT_ZONE_NAME), TIME_WAIT);

      assertZoneOperationDoneSucessfully(api().createInZone(INSTANCE_NAME, DEFAULT_ZONE_NAME, instance), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testGetInstance() {

      Instance instance = api().getInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME);
      assertNotNull(instance);
      assertInstanceEquals(instance, this.instance);
   }

   @Test(groups = "live", dependsOnMethods = "testListInstance")
   public void testSetMetadataForInstance() {
      Instance originalInstance = api().getInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME);
      assertZoneOperationDoneSucessfully(api().setMetadataInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME,
              ImmutableMap.of(METADATA_ITEM_KEY, METADATA_ITEM_VALUE),
              originalInstance.getMetadata().getFingerprint()),
              TIME_WAIT);

      Instance modifiedInstance = api().getInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME);

      assertTrue(modifiedInstance.getMetadata().getItems().containsKey(METADATA_ITEM_KEY));
      assertEquals(modifiedInstance.getMetadata().getItems().get(METADATA_ITEM_KEY),
              METADATA_ITEM_VALUE);
      assertNotNull(modifiedInstance.getMetadata().getFingerprint());

   }

   @Test(groups = "live", dependsOnMethods = "testSetMetadataForInstance")
   public void testAttachDiskToInstance() {
      assertZoneOperationDoneSucessfully(diskApi().createInZone(ATTACH_DISK_NAME, 1, DEFAULT_ZONE_NAME), TIME_WAIT);

      Instance originalInstance = api().getInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME);
      assertZoneOperationDoneSucessfully(api().attachDiskInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME,
              new AttachDiskOptions().type(DiskType.PERSISTENT)
                      .source(getDiskUrl(userProject.get(), ATTACH_DISK_NAME))
                      .mode(DiskMode.READ_ONLY)
                      .deviceName(ATTACH_DISK_DEVICE_NAME)),
              TIME_WAIT);

      Instance modifiedInstance = api().getInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME);

      assertTrue(modifiedInstance.getDisks().size() > originalInstance.getDisks().size());
      assertTrue(Iterables.any(modifiedInstance.getDisks(), new Predicate<AttachedDisk>() {

         @Override
         public boolean apply(AttachedDisk disk) {
            return disk instanceof PersistentAttachedDisk &&
                   ((PersistentAttachedDisk) disk).getDeviceName().isPresent() &&
                   ((PersistentAttachedDisk) disk).getDeviceName().get().equals(ATTACH_DISK_DEVICE_NAME);
         }
      }));
   }

   @Test(groups = "live", dependsOnMethods = "testAttachDiskToInstance")
   public void testDetachDiskFromInstance() {
      Instance originalInstance = api().getInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME);
      assertZoneOperationDoneSucessfully(api().detachDiskInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME,
              ATTACH_DISK_DEVICE_NAME), TIME_WAIT);

      Instance modifiedInstance = api().getInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME);

      assertTrue(modifiedInstance.getDisks().size() < originalInstance.getDisks().size());

      assertZoneOperationDoneSucessfully(diskApi().deleteInZone(DEFAULT_ZONE_NAME, ATTACH_DISK_NAME), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testListInstance() {

      PagedIterable<Instance> instances = api().listInZone(DEFAULT_ZONE_NAME, new ListOptions.Builder()
              .filter("name eq " + INSTANCE_NAME));

      List<Instance> instancesAsList = Lists.newArrayList(instances.concat());

      assertEquals(instancesAsList.size(), 1);

      assertInstanceEquals(Iterables.getOnlyElement(instancesAsList), instance);

   }

   @Test(groups = "live", dependsOnMethods = "testDetachDiskFromInstance")
   public void testResetInstance() {
      assertZoneOperationDoneSucessfully(api().resetInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME),
              TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testResetInstance")
   public void testDeleteInstance() {

      assertZoneOperationDoneSucessfully(api().deleteInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME), TIME_WAIT);
      assertZoneOperationDoneSucessfully(api.getDiskApiForProject(userProject.get()).deleteInZone(DEFAULT_ZONE_NAME, DISK_NAME),
              TIME_WAIT);
      assertZoneOperationDoneSucessfully(api.getDiskApiForProject(userProject.get()).deleteInZone(DEFAULT_ZONE_NAME, BOOT_DISK_NAME),
                                         TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api.getNetworkApiForProject(userProject.get()).delete
              (INSTANCE_NETWORK_NAME), TIME_WAIT);
   }

   private void assertInstanceEquals(Instance result, InstanceTemplate expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getMetadata().getItems(), expected.getMetadata());
   }

   @AfterClass(groups = { "integration", "live" })
   protected void tearDownContext() {
      try {
         waitZoneOperationDone(api().deleteInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME), TIME_WAIT);
         waitZoneOperationDone(api.getDiskApiForProject(userProject.get()).deleteInZone(DEFAULT_ZONE_NAME, DISK_NAME),
                               TIME_WAIT);
         waitZoneOperationDone(api.getDiskApiForProject(userProject.get()).deleteInZone(DEFAULT_ZONE_NAME, BOOT_DISK_NAME),
                               TIME_WAIT);
         waitGlobalOperationDone(api.getNetworkApiForProject(userProject.get()).delete
                                                                                (INSTANCE_NETWORK_NAME), TIME_WAIT);
      } catch (Exception e) {
         // we don't really care about any exception here, so just delete away.
       }
   }

}
