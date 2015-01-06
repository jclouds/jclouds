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
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.AttachedDisk;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig.Type;
import org.jclouds.googlecomputeengine.domain.Instance.Scheduling;
import org.jclouds.googlecomputeengine.domain.Instance.SerialPortOutput;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.AttachDisk;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

@Test(groups = "live", testName = "InstanceApiLiveTest")
public class InstanceApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String INSTANCE_NETWORK_NAME = "instance-api-live-test-network";
   private static final String INSTANCE_NAME = "instance-api-test-instance-1";
   private static final String DISK_NAME = "instance-live-test-disk";
   private static final String IPV4_RANGE = "10.0.0.0/8";
   private static final String METADATA_ITEM_KEY = "instanceLiveTestTestProp";
   private static final String METADATA_ITEM_VALUE = "instanceLiveTestTestValue";
   private static final List<String> TAGS = ImmutableList.of("instance-live-test-tag1", "instance-live-test-tag2");
   private static final String ATTACH_DISK_NAME = "instance-api-live-test-attach-disk";
   private static final String ATTACH_DISK_DEVICE_NAME = "attach-disk-1";
   private static final int DEFAULT_DISK_SIZE_GB = 10;

   private static final String DEFAULT_BOOT_DISK_NAME = "persistent-disk-0";

   private NewInstance instance;

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

      instance = NewInstance.create(
            INSTANCE_NAME, // name
            getDefaultMachineTypeUrl(), // machineType
            getNetworkUrl(INSTANCE_NETWORK_NAME), // network
            Arrays.asList(AttachDisk.newBootDisk(imageUri),
                  AttachDisk.existingDisk(getDiskUrl(DISK_NAME))), // disks
            "a description" // description
      );
      instance.tags().items().addAll(Arrays.asList("foo", "bar"));
      instance.metadata().put("mykey", "myvalue");
      return api;
   }

   private InstanceApi api() {
      return api.instancesInZone(DEFAULT_ZONE_NAME);
   }

   private DiskApi diskApi() {
      return api.disksInZone(DEFAULT_ZONE_NAME);
   }

   @Test(groups = "live")
   public void testInsertInstance() {
      // need to insert the network first
      assertOperationDoneSuccessfully(api.networks().createInIPv4Range
              (INSTANCE_NETWORK_NAME, IPV4_RANGE));

      assertOperationDoneSuccessfully(diskApi().create(DISK_NAME,
            new DiskCreationOptions().sizeGb(DEFAULT_DISK_SIZE_GB)));
      assertOperationDoneSuccessfully(api().create(instance));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testGetInstance() {
      Instance instance = api().get(INSTANCE_NAME);
      assertNotNull(instance);
      assertInstanceEquals(instance, this.instance);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testAddAccessConfig() {
      Instance instance = api().get(INSTANCE_NAME);
      assertNotNull(instance);
      assertOperationDoneSuccessfully(api().deleteAccessConfigFromNic(INSTANCE_NAME,
            instance.networkInterfaces().get(0).accessConfigs().get(0).name(), "nic0"));

      AccessConfig config = AccessConfig.create("test-config", Type.ONE_TO_ONE_NAT, null);
      assertOperationDoneSuccessfully(api().addAccessConfigToNic(INSTANCE_NAME, config, "nic0"));
      instance = api().get(INSTANCE_NAME);
      assertNotNull(instance);
      assertEquals(instance.networkInterfaces().get(0).accessConfigs().get(0).name(), "test-config");
   }

   @Test(groups = "live", dependsOnMethods = "testAddAccessConfig")
   public void testDeleteAccessConfig() {
      Instance instance = api().get(INSTANCE_NAME);
      assertOperationDoneSuccessfully(api().deleteAccessConfigFromNic(INSTANCE_NAME, "test-config", "nic0"));
      instance = api().get(INSTANCE_NAME);
      assertNotNull(instance);
      assertTrue(instance.networkInterfaces().get(0).accessConfigs().isEmpty());
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testGetSerialPortOutput() {
      SerialPortOutput output = api().getSerialPortOutput(INSTANCE_NAME);
      assertNotNull(output);
      assertNotNull(output.selfLink());
      assertNotNull(output.contents());
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testSetDiskAutoDelete() {

      assertTrue(existsDiskWithNameAndAutoDelete(INSTANCE_NAME, DEFAULT_BOOT_DISK_NAME, true));

      Operation o = api().setDiskAutoDelete(INSTANCE_NAME, DEFAULT_BOOT_DISK_NAME, false);
      assertOperationDoneSuccessfully(o);

      assertTrue(existsDiskWithNameAndAutoDelete(INSTANCE_NAME, DEFAULT_BOOT_DISK_NAME, false));

      o = api().setDiskAutoDelete(INSTANCE_NAME, DEFAULT_BOOT_DISK_NAME, true);

      assertOperationDoneSuccessfully(o);
      assertTrue(existsDiskWithNameAndAutoDelete(INSTANCE_NAME, DEFAULT_BOOT_DISK_NAME, true));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testSetScheduling() {
      Instance instance = api().get(INSTANCE_NAME);
      assertEquals(instance.scheduling().automaticRestart(), true);
      assertEquals(instance.scheduling().onHostMaintenance(), Scheduling.OnHostMaintenance.MIGRATE);

      assertOperationDoneSuccessfully(api().setScheduling(INSTANCE_NAME, Scheduling.OnHostMaintenance.TERMINATE, false));

      Instance instanceAltered = api().get(INSTANCE_NAME);
      assertEquals(instanceAltered.scheduling().automaticRestart(), false);
      assertEquals(instanceAltered.scheduling().onHostMaintenance(), Scheduling.OnHostMaintenance.TERMINATE);
   }

   private boolean existsDiskWithNameAndAutoDelete(String instanceName, final String diskName, final boolean autoDelete){
      Instance revertedInstance = api().get(instanceName);

      return Iterables.any(revertedInstance.disks(), new Predicate<AttachedDisk>() {
         @Override
         public boolean apply(AttachedDisk disk) {
            return disk.type() == AttachedDisk.Type.PERSISTENT &&
            diskName.equals(disk.deviceName()) &&
            disk.autoDelete() == autoDelete;
         }
      });
   }

   @Test(groups = "live", dependsOnMethods = "testListInstance")
   public void testSetMetadataForInstance() {
      Instance originalInstance = api().get(INSTANCE_NAME);
      Metadata update = Metadata.create(originalInstance.metadata().fingerprint())
            .put(METADATA_ITEM_KEY, METADATA_ITEM_VALUE);
      assertOperationDoneSuccessfully(api().setMetadata(INSTANCE_NAME, update));

      Instance modifiedInstance = api().get(INSTANCE_NAME);

      assertTrue(modifiedInstance.metadata().containsKey(METADATA_ITEM_KEY));
      assertEquals(modifiedInstance.metadata().get(METADATA_ITEM_KEY), METADATA_ITEM_VALUE);
      assertNotNull(modifiedInstance.metadata().fingerprint());
   }

   @Test(groups = "live", dependsOnMethods = "testListInstance")
   public void testSetTagsForInstance() {
      Instance originalInstance = api().get(INSTANCE_NAME);
      assertOperationDoneSuccessfully(
            api().setTags(INSTANCE_NAME, TAGS, originalInstance.tags().fingerprint()));

      Instance modifiedInstance = api().get(INSTANCE_NAME);

      assertTrue(modifiedInstance.tags().items().containsAll(TAGS));
      assertNotNull(modifiedInstance.tags().fingerprint());
   }

   @Test(groups = "live", dependsOnMethods = "testSetMetadataForInstance")
   public void testAttachDiskToInstance() {
      assertOperationDoneSuccessfully(diskApi().create(ATTACH_DISK_NAME,
            new DiskCreationOptions().sizeGb(1)));

      Instance originalInstance = api().get(INSTANCE_NAME);
      assertOperationDoneSuccessfully(api().attachDisk(INSTANCE_NAME,
                  AttachDisk.create(AttachDisk.Type.PERSISTENT, // type
                                    AttachDisk.Mode.READ_ONLY, // mode
                                    getDiskUrl(ATTACH_DISK_NAME), // source
                                    ATTACH_DISK_DEVICE_NAME, // deviceName
                                    false, // boot
                                    null, // initializeParams
                                    false, // autoDelete
                                    null, // licenses
                                    null // interface
                                    )));

      Instance modifiedInstance = api().get(INSTANCE_NAME);

      assertTrue(modifiedInstance.disks().size() > originalInstance.disks().size());
      assertTrue(Iterables.any(modifiedInstance.disks(), new Predicate<AttachedDisk>() {

         @Override
         public boolean apply(AttachedDisk disk) {
            return disk.type() == AttachedDisk.Type.PERSISTENT &&
                  ATTACH_DISK_DEVICE_NAME.equals(disk.deviceName());
         }
      }));
   }

   @Test(groups = "live", dependsOnMethods = "testAttachDiskToInstance", alwaysRun = true)
   public void testDetachDiskFromInstance() {
      Instance originalInstance = api().get(INSTANCE_NAME);
      assertOperationDoneSuccessfully(api().detachDisk(INSTANCE_NAME, ATTACH_DISK_DEVICE_NAME));

      Instance modifiedInstance = api().get(INSTANCE_NAME);

      assertTrue(modifiedInstance.disks().size() < originalInstance.disks().size());

      assertOperationDoneSuccessfully(diskApi().delete(ATTACH_DISK_NAME));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertInstance")
   public void testListInstance() {

      Iterator<ListPage<Instance>> instances = api().list(filter("name eq " + INSTANCE_NAME));

      List<Instance> instancesAsList = instances.next();

      assertEquals(instancesAsList.size(), 1);

      assertInstanceEquals(instancesAsList.get(0), instance);
   }

   @Test(groups = "live", dependsOnMethods = "testDetachDiskFromInstance")
   public void testResetInstance() {
      assertOperationDoneSuccessfully(api().reset(INSTANCE_NAME));
   }

   @Test(groups = "live", dependsOnMethods = {"testSetDiskAutoDelete", "testResetInstance", "testSetScheduling",
         "testGetInstance", "testGetSerialPortOutput", "testDeleteAccessConfig"}, alwaysRun = true)
   public void testDeleteInstance() {
      assertOperationDoneSuccessfully(api().delete(INSTANCE_NAME));
      assertOperationDoneSuccessfully(diskApi().delete(DISK_NAME));
      Operation deleteNetwork = api.networks().delete(INSTANCE_NETWORK_NAME);
      assertOperationDoneSuccessfully(deleteNetwork);
   }

   private void assertInstanceEquals(Instance result, NewInstance expected) {
      assertEquals(result.name(), expected.name());
      assertEquals(result.metadata().asMap(), expected.metadata().asMap()); // ignore fingerprint!
      assertEquals(result.tags().items(), expected.tags().items());
   }

   @AfterClass(groups = { "integration", "live" })
   protected void tearDownContext() {
      try {
         waitOperationDone(api().delete(INSTANCE_NAME));
         waitOperationDone(diskApi().delete(DISK_NAME));
         waitOperationDone(api.networks().delete(INSTANCE_NETWORK_NAME));
      } catch (Exception e) {
         // we don't really care about any exception here, so just delete away.
       }
   }
}
