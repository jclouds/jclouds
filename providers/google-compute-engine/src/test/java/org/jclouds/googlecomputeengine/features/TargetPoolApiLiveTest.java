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
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.domain.templates.InstanceTemplate;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions.SessionAffinityValue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

public class TargetPoolApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String BACKUP_TARGETPOOL_NAME = "targetpool-api-live-test-backup";
   private static final String TARGETPOOL_NAME = "targetpool-api-live-test-primary";
   private static final String THIRD_TARGETPOOL_NAME = "targetpool-apo-live-test-third";
   private static final int TIME_WAIT = 30;
   private static final String DESCRIPTION = "A New TargetPool!";
   private static final String DESCRIPTION_BACKUP = "A backup target pool!";

   private static final String INSTANCE_NETWORK_NAME = "target-pool-api-live-test-network";
   private static final String INSTANCE_NAME = "target-pool-api-live-test-instance";
   private static final String BOOT_DISK_NAME = INSTANCE_NAME + "-boot-disk";
   private static final String IPV4_RANGE = "10.0.0.0/8";
   private static final String HEALTHCHECK_NAME = "target-pool-test-health-check";

   private static final int DEFAULT_DISK_SIZE_GB = 10;
   private static final int TIME_WAIT_LONG = 600;

   private List<URI> instances;
   private List<URI> httpHealthChecks;

   private TargetPoolApi api() {
      return api.getTargetPoolApi(userProject.get(), DEFAULT_REGION_NAME);
   }

   @Test(groups = "live")
   public void testCreateInstanceAndHealthCheck(){
      InstanceApi instanceApi = api.getInstanceApi(userProject.get(), DEFAULT_ZONE_NAME);
      HttpHealthCheckApi httpHealthCheckApi = api.getHttpHealthCheckApi(userProject.get());

      ListPage<Image> list = api.getImageApi("centos-cloud").list(new ListOptions.Builder().filter("name eq centos.*"))
            .next();
      // Get an imageUri
      URI imageUri = FluentIterable.from(list)
            .filter(new Predicate<Image>() {
               @Override
               public boolean apply(Image input) {
                  // filter out all deprecated images
                  return !(input.deprecated() != null && input.deprecated().state() != null);
               }
            })
            .first().get().selfLink();

      // Make and instanceTemplate
      InstanceTemplate instanceTemplate = new InstanceTemplate()
            .machineType(getDefaultMachineTypeUrl(userProject.get()))
            .addNetworkInterface(getNetworkUrl(userProject.get(), INSTANCE_NETWORK_NAME),
                                 Instance.NetworkInterface.AccessConfig.Type.ONE_TO_ONE_NAT)
            .addMetadata("mykey", "myvalue")
            .description("a description")
            .addDisk(Instance.AttachedDisk.Mode.READ_WRITE, getDiskUrl(userProject.get(), BOOT_DISK_NAME),
                     null, true, true)
            .image(imageUri);


      // Insert a network.
      assertGlobalOperationDoneSucessfully(api.getNetworkApi(userProject.get()).createInIPv4Range
              (INSTANCE_NETWORK_NAME, IPV4_RANGE), TIME_WAIT_LONG);

      // Create a disk.
      DiskCreationOptions diskCreationOptions = new DiskCreationOptions().sourceImage(instanceTemplate.image());
      assertZoneOperationDoneSuccessfully(api.getDiskApi(userProject.get())
                  .createInZone(BOOT_DISK_NAME, DEFAULT_DISK_SIZE_GB, DEFAULT_ZONE_NAME, diskCreationOptions),
            TIME_WAIT_LONG);

      // Create an instance.
      assertZoneOperationDoneSuccessfully(instanceApi.create(INSTANCE_NAME, instanceTemplate),
            TIME_WAIT_LONG);
      Instance instance = instanceApi.get(INSTANCE_NAME);
      instances = new ArrayList<URI>();
      instances.add(instance.selfLink());

      // Create a healthCheck
      HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions()
         .checkIntervalSec(30)
         .timeoutSec(20)
         .description("A test HealthCheck for adding to targetPools");
      assertGlobalOperationDoneSucessfully(httpHealthCheckApi.insert(HEALTHCHECK_NAME, options), TIME_WAIT);
      HttpHealthCheck healthCheck = httpHealthCheckApi.get(HEALTHCHECK_NAME);
      httpHealthChecks = new ArrayList<URI>();
      httpHealthChecks.add(healthCheck.selfLink());
   }

   @Test(groups = "live")
   public void testInsertTargetPool() {
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions()
      .description(DESCRIPTION_BACKUP)
      .sessionAffinity(SessionAffinityValue.CLIENT_IP);
      assertRegionOperationDoneSucessfully(api().create(BACKUP_TARGETPOOL_NAME, targetPoolCreationOptions), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool")
   public void testInsertTargetPool2(){
      TargetPool targetPool = api().get(BACKUP_TARGETPOOL_NAME);
      assertNotNull(targetPool);
      // Make a Target Pool with a backup and failoverRatio specified.
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions()
         .description(DESCRIPTION)
         .sessionAffinity(SessionAffinityValue.CLIENT_IP)
         .backupPool(targetPool.selfLink())
         .failoverRatio((float) 0.5);
      assertRegionOperationDoneSucessfully(api().create(TARGETPOOL_NAME, targetPoolCreationOptions), TIME_WAIT);
      TargetPool targetPool2 = api().get(TARGETPOOL_NAME);
      assertNotNull(targetPool2);
      assertEquals(targetPool2.name(), TARGETPOOL_NAME);
      assertEquals(targetPool2.description(), DESCRIPTION);
      assertEquals(targetPool2.failoverRatio(), (float) 0.5);
      assertEquals(targetPool2.backupPool(), targetPool.selfLink());
      assertEquals(targetPool2.sessionAffinity(), SessionAffinityValue.CLIENT_IP);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool")
   public void testGetTargetPool() {
      TargetPool targetPool = api().get(BACKUP_TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.name(), BACKUP_TARGETPOOL_NAME);
      assertEquals(targetPool.description(), DESCRIPTION_BACKUP);
      assertEquals(targetPool.sessionAffinity(), SessionAffinityValue.CLIENT_IP);
   }

   @Test(groups = "live", dependsOnMethods = {"testInsertTargetPool", "testCreateInstanceAndHealthCheck"})
   public void testAddInstanceTargetPool() {
      assertRegionOperationDoneSucessfully(api().addInstance(BACKUP_TARGETPOOL_NAME, instances), TIME_WAIT);
      TargetPool targetPool = api().get(BACKUP_TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.name(), BACKUP_TARGETPOOL_NAME);
      assertEquals(targetPool.instances(), instances);
   }

   @Test(groups = "live", dependsOnMethods = "testAddInstanceTargetPool")
   public void testRemoveInstanceTargetPool() {
      assertRegionOperationDoneSucessfully(api().removeInstance(BACKUP_TARGETPOOL_NAME, instances), TIME_WAIT);

      TargetPool targetPool = api().get(BACKUP_TARGETPOOL_NAME);

      assertNotNull(targetPool);
      assertEquals(targetPool.name(), BACKUP_TARGETPOOL_NAME);
      assertNotEquals(targetPool.instances(), instances);
   }

   @Test(groups = "live", dependsOnMethods = {"testInsertTargetPool2", "testCreateInstanceAndHealthCheck"})
   public void testAddHealthCheckTargetPool() {
      assertRegionOperationDoneSucessfully(api().addHealthCheck(TARGETPOOL_NAME, httpHealthChecks), TIME_WAIT);
      TargetPool targetPool = api().get(TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.name(), TARGETPOOL_NAME);
      assertEquals(targetPool.healthChecks(), httpHealthChecks);
   }

   @Test(groups = "live", dependsOnMethods = "testAddHealthCheckTargetPool")
   public void testRemoveHealthCheckTargetPool() {
      assertRegionOperationDoneSucessfully(api().removeHealthCheck(TARGETPOOL_NAME, httpHealthChecks), TIME_WAIT);

      TargetPool targetPool = api().get(TARGETPOOL_NAME);

      assertNotNull(targetPool);
      assertEquals(targetPool.name(), TARGETPOOL_NAME);
      assertNotEquals(targetPool.healthChecks(), httpHealthChecks);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool")
   public void testListTargetPool() {
      ListPage<TargetPool> targetPool = api().list(new ListOptions.Builder()
              .filter("name eq " + BACKUP_TARGETPOOL_NAME));
      assertEquals(Iterables.size(targetPool), 1);
   }

   @Test(groups = "live", dependsOnMethods = {"testInsertTargetPool2"})
   public void testListBackupTargetPool() {
      TargetPoolCreationOptions options = new TargetPoolCreationOptions().description("A targetPool for testing setBackup.");
      assertRegionOperationDoneSucessfully(api().create(THIRD_TARGETPOOL_NAME, options), TIME_WAIT);
      TargetPool targetPool = api().get(THIRD_TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.name(), THIRD_TARGETPOOL_NAME);
      assertEquals(targetPool.backupPool(), null);

      URI selfLink = api().get(TARGETPOOL_NAME).selfLink();

      Float failoverRatio = Float.valueOf((float) 0.5);
      assertRegionOperationDoneSucessfully(api().setBackup(THIRD_TARGETPOOL_NAME, failoverRatio, selfLink), TIME_WAIT);

      TargetPool targetPoolUpdated = api().get(THIRD_TARGETPOOL_NAME);
      assertNotNull(targetPoolUpdated);
      assertEquals(targetPoolUpdated.name(), THIRD_TARGETPOOL_NAME);
      assertEquals(targetPoolUpdated.backupPool(), selfLink);
   }

   @Test(groups = "live", dependsOnMethods = {"testListTargetPool",
                                              "testGetTargetPool",
                                              "testRemoveInstanceTargetPool",
                                              "testRemoveHealthCheckTargetPool",
                                              "testListBackupTargetPool"}, alwaysRun = true)
   public void testDeleteTargetPool() {
      // Note: This ordering matters due one being the backup of the other ect.
      assertRegionOperationDoneSucessfully(api().delete(THIRD_TARGETPOOL_NAME), TIME_WAIT);
      assertRegionOperationDoneSucessfully(api().delete(TARGETPOOL_NAME), TIME_WAIT);
      assertRegionOperationDoneSucessfully(api().delete(BACKUP_TARGETPOOL_NAME), TIME_WAIT);
   }

   @AfterClass(groups = { "integration", "live" })
   public void testCleanup(){
      InstanceApi instanceApi = api.getInstanceApi(userProject.get(), DEFAULT_ZONE_NAME);
      HttpHealthCheckApi httpHealthCheckApi = api.getHttpHealthCheckApi(userProject.get());

      try {
         waitZoneOperationDone(instanceApi.delete(INSTANCE_NAME), TIME_WAIT_LONG);

         waitZoneOperationDone(api.getDiskApi(userProject.get()).deleteInZone(DEFAULT_ZONE_NAME, BOOT_DISK_NAME),
                               TIME_WAIT);
         waitGlobalOperationDone(api.getNetworkApi(userProject.get()).delete(INSTANCE_NETWORK_NAME), TIME_WAIT_LONG);

         waitGlobalOperationDone(httpHealthCheckApi.delete(HEALTHCHECK_NAME), TIME_WAIT);
      } catch (Exception e) {
         // we don't really care about any exception here, so just delete away.
       }
   }

}
