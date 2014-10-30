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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions.SessionAffinityValue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static com.google.common.base.Optional.fromNullable;

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

   private Set<URI> instances;
   private Set<URI> httpHealthChecks;

   private TargetPoolApi api() {
      return api.getTargetPoolApi(userProject.get(), DEFAULT_REGION_NAME);
   }

   @Test(groups = "live")
   public void testCreateInstanceAndHealthCheck(){
      InstanceApi instanceApi = api.getInstanceApi(userProject.get());
      HttpHealthCheckApi httpHealthCheckApi = api.getHttpHealthCheckApi(userProject.get());

      // Get an imageUri
      URI imageUri = api.getImageApi("centos-cloud")
            .list(new ListOptions.Builder().filter("name eq centos.*"))
            .concat()
            .filter(new Predicate<Image>() {
               @Override
               public boolean apply(Image input) {
                  // filter out all deprecated images
                  return !(input.getDeprecated().isPresent() && input.getDeprecated().get().getState().isPresent());
               }
            })
            .first().get().getSelfLink();

      // Make and instanceTemplate
      InstanceTemplate instanceTemplate = InstanceTemplate.builder()
            .forMachineType(getDefaultMachineTypeUrl(userProject.get()))
            .addNetworkInterface(getNetworkUrl(userProject.get(), INSTANCE_NETWORK_NAME),
                                 Instance.NetworkInterface.AccessConfig.Type.ONE_TO_ONE_NAT)
            .addMetadata("mykey", "myvalue")
            .description("a description")
            .addDisk(InstanceTemplate.PersistentDisk.Mode.READ_WRITE, getDiskUrl(userProject.get(), BOOT_DISK_NAME),
                     null, true, true)
            .image(imageUri);


      // Insert a network.
      assertGlobalOperationDoneSucessfully(api.getNetworkApi(userProject.get()).createInIPv4Range
              (INSTANCE_NETWORK_NAME, IPV4_RANGE), TIME_WAIT_LONG);

      // Create a disk.
      DiskCreationOptions diskCreationOptions = new DiskCreationOptions().sourceImage(instanceTemplate.getImage());
      assertZoneOperationDoneSucessfully(api.getDiskApi(userProject.get())
                                        .createInZone(BOOT_DISK_NAME, DEFAULT_DISK_SIZE_GB, DEFAULT_ZONE_NAME, diskCreationOptions),
                                        TIME_WAIT_LONG);

      // Create an instance.
      assertZoneOperationDoneSucessfully(instanceApi.createInZone(INSTANCE_NAME,
                                                                  DEFAULT_ZONE_NAME,
                                                                  instanceTemplate),
                                                                  TIME_WAIT_LONG);
      Instance instance = instanceApi.getInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME);
      instances = new HashSet<URI>();
      instances.add(instance.getSelfLink());

      // Create a healthCheck
      HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions()
         .checkIntervalSec(30)
         .timeoutSec(20)
         .description("A test HealthCheck for adding to targetPools");
      assertGlobalOperationDoneSucessfully(httpHealthCheckApi.insert(HEALTHCHECK_NAME, options), TIME_WAIT);
      HttpHealthCheck healthCheck = httpHealthCheckApi.get(HEALTHCHECK_NAME);
      httpHealthChecks = new HashSet<URI>();
      httpHealthChecks.add(healthCheck.getSelfLink());
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
         .backupPool(targetPool.getSelfLink())
         .failoverRatio((float) 0.5);
      assertRegionOperationDoneSucessfully(api().create(TARGETPOOL_NAME, targetPoolCreationOptions), TIME_WAIT);
      TargetPool targetPool2 = api().get(TARGETPOOL_NAME);
      assertNotNull(targetPool2);
      assertEquals(targetPool2.getName(), TARGETPOOL_NAME);
      assertEquals(targetPool2.getDescription(), fromNullable(DESCRIPTION));
      assertEquals(targetPool2.getFailoverRatio(), (float) 0.5);
      assertEquals(targetPool2.getBackupPool(), fromNullable(targetPool.getSelfLink()));
      assertEquals(targetPool2.getSessionAffinity(), fromNullable(SessionAffinityValue.CLIENT_IP));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool")
   public void testGetTargetPool() {
      TargetPool targetPool = api().get(BACKUP_TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.getName(), BACKUP_TARGETPOOL_NAME);
      assertEquals(targetPool.getDescription(), fromNullable(DESCRIPTION_BACKUP));
      assertEquals(targetPool.getSessionAffinity(), fromNullable(SessionAffinityValue.CLIENT_IP));
   }

   @Test(groups = "live", dependsOnMethods = {"testInsertTargetPool", "testCreateInstanceAndHealthCheck"})
   public void testAddInstanceTargetPool() {
      assertRegionOperationDoneSucessfully(api().addInstance(BACKUP_TARGETPOOL_NAME, instances), TIME_WAIT);
      TargetPool targetPool = api().get(BACKUP_TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.getName(), BACKUP_TARGETPOOL_NAME);
      assertEquals(targetPool.getInstances(), instances);
   }

   @Test(groups = "live", dependsOnMethods = "testAddInstanceTargetPool")
   public void testRemoveInstanceTargetPool() {
      assertRegionOperationDoneSucessfully(api().removeInstance(BACKUP_TARGETPOOL_NAME, instances), TIME_WAIT);

      TargetPool targetPool = api().get(BACKUP_TARGETPOOL_NAME);

      assertNotNull(targetPool);
      assertEquals(targetPool.getName(), BACKUP_TARGETPOOL_NAME);
      assertNotEquals(targetPool.getInstances(), fromNullable(instances));
   }

   @Test(groups = "live", dependsOnMethods = {"testInsertTargetPool2", "testCreateInstanceAndHealthCheck"})
   public void testAddHealthCheckTargetPool() {
      assertRegionOperationDoneSucessfully(api().addHealthCheck(TARGETPOOL_NAME, httpHealthChecks), TIME_WAIT);
      TargetPool targetPool = api().get(TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.getName(), TARGETPOOL_NAME);
      assertEquals(targetPool.getHealthChecks(), httpHealthChecks);
   }

   @Test(groups = "live", dependsOnMethods = "testAddHealthCheckTargetPool")
   public void testRemoveHealthCheckTargetPool() {
      assertRegionOperationDoneSucessfully(api().removeHealthCheck(TARGETPOOL_NAME, httpHealthChecks), TIME_WAIT);

      TargetPool targetPool = api().get(TARGETPOOL_NAME);

      assertNotNull(targetPool);
      assertEquals(targetPool.getName(), TARGETPOOL_NAME);
      assertNotEquals(targetPool.getHealthChecks(), fromNullable(httpHealthChecks));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool")
   public void testListTargetPool() {

      IterableWithMarker<TargetPool> targetPool = api().list(new ListOptions.Builder()
              .filter("name eq " + BACKUP_TARGETPOOL_NAME));
      assertEquals(targetPool.toList().size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = {"testInsertTargetPool2"})
   public void testSetBackupTargetPool() {
      TargetPoolCreationOptions options = new TargetPoolCreationOptions().description("A targetPool for testing setBackup.");
      assertRegionOperationDoneSucessfully(api().create(THIRD_TARGETPOOL_NAME, options), TIME_WAIT);
      TargetPool targetPool = api().get(THIRD_TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.getName(), THIRD_TARGETPOOL_NAME);
      assertEquals(targetPool.getBackupPool(), fromNullable(null));

      URI selfLink = api().get(TARGETPOOL_NAME).getSelfLink();

      Float failoverRatio = Float.valueOf((float) 0.5);
      assertRegionOperationDoneSucessfully(api().setBackup(THIRD_TARGETPOOL_NAME, failoverRatio, selfLink), TIME_WAIT);

      TargetPool targetPoolUpdated = api().get(THIRD_TARGETPOOL_NAME);
      assertNotNull(targetPoolUpdated);
      assertEquals(targetPoolUpdated.getName(), THIRD_TARGETPOOL_NAME);
      assertEquals(targetPoolUpdated.getBackupPool(), fromNullable(selfLink));
   }

   @Test(groups = "live", dependsOnMethods = {"testListTargetPool",
                                              "testGetTargetPool",
                                              "testRemoveInstanceTargetPool",
                                              "testRemoveHealthCheckTargetPool",
                                              "testSetBackupTargetPool"}, alwaysRun = true)
   public void testDeleteTargetPool() {
      // Note: This ordering matters due one being the backup of the other ect.
      assertRegionOperationDoneSucessfully(api().delete(THIRD_TARGETPOOL_NAME), TIME_WAIT);
      assertRegionOperationDoneSucessfully(api().delete(TARGETPOOL_NAME), TIME_WAIT);
      assertRegionOperationDoneSucessfully(api().delete(BACKUP_TARGETPOOL_NAME), TIME_WAIT);
   }

   @AfterClass(groups = { "integration", "live" })
   public void testCleanup(){
      InstanceApi instanceApi = api.getInstanceApi(userProject.get());
      HttpHealthCheckApi httpHealthCheckApi = api.getHttpHealthCheckApi(userProject.get());

      try {
         waitZoneOperationDone(instanceApi.deleteInZone(DEFAULT_ZONE_NAME, INSTANCE_NAME), TIME_WAIT_LONG);

         waitZoneOperationDone(api.getDiskApi(userProject.get()).deleteInZone(DEFAULT_ZONE_NAME, BOOT_DISK_NAME),
                               TIME_WAIT);
         waitGlobalOperationDone(api.getNetworkApi(userProject.get()).delete(INSTANCE_NETWORK_NAME), TIME_WAIT_LONG);

         waitGlobalOperationDone(httpHealthCheckApi.delete(HEALTHCHECK_NAME), TIME_WAIT);
      } catch (Exception e) {
         // we don't really care about any exception here, so just delete away.
       }
   }

}
