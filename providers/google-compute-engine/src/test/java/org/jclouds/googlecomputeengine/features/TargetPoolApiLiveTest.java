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
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.HealthStatus;
import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.domain.ForwardingRule.IPProtocol;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
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
   private static final String DESCRIPTION = "A New TargetPool!";
   private static final String DESCRIPTION_BACKUP = "A backup target pool!";

   private static final String INSTANCE_NETWORK_NAME = "target-pool-api-live-test-network";
   private static final String INSTANCE_NAME = "target-pool-api-live-test-instance";
   private static final String IPV4_RANGE = "10.0.0.0/8";
   private static final String HEALTHCHECK_NAME = "target-pool-test-health-check";
   private static final String FORWARDING_RULE_NAME = "target-pool-api-forwarding-rule";

   private List<URI> instances;
   private List<URI> httpHealthChecks;

   private TargetPoolApi api() {
      return api.targetPoolsInRegion(DEFAULT_REGION_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool2")
   public void testCreateInstanceAndHealthCheck(){
      InstanceApi instanceApi = api.instancesInZone(DEFAULT_ZONE_NAME);
      HttpHealthCheckApi httpHealthCheckApi = api.httpHeathChecks();

      ListPage<Image> list = api.images().listInProject("centos-cloud", filter("name eq centos.*")).next();
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

      // Insert a network.
      assertOperationDoneSuccessfully(api.networks().createInIPv4Range(INSTANCE_NETWORK_NAME,
            IPV4_RANGE));

      // Create an instance.
      assertOperationDoneSuccessfully(
            instanceApi.create(NewInstance.create( //
                  INSTANCE_NAME, // name
                  getDefaultMachineTypeUrl(), // machineType
                  getNetworkUrl(INSTANCE_NETWORK_NAME), // network
                  imageUri // disks
            )));

      Instance instance = instanceApi.get(INSTANCE_NAME);
      instances = new ArrayList<URI>();
      instances.add(instance.selfLink());

      // Create a healthCheck
      HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions.Builder()
         .checkIntervalSec(3)
         .timeoutSec(2)
         .description("A test HealthCheck for adding to targetPools")
         .buildWithDefaults();
      assertOperationDoneSuccessfully(httpHealthCheckApi.insert(HEALTHCHECK_NAME, options));
      HttpHealthCheck healthCheck = httpHealthCheckApi.get(HEALTHCHECK_NAME);
      httpHealthChecks = new ArrayList<URI>();
      httpHealthChecks.add(healthCheck.selfLink());

      // Create a forwarding rule
      TargetPool targetPool = api().get(TARGETPOOL_NAME);
      URI target = targetPool.selfLink();

      ForwardingRuleCreationOptions forwardingRuleOptions = new ForwardingRuleCreationOptions.Builder()
         .ipProtocol(IPProtocol.TCP)
         .portRange("80-80")
         .target(target)
         .build();

      assertOperationDoneSuccessfully(api.forwardingRulesInRegion(DEFAULT_REGION_NAME)
               .create(FORWARDING_RULE_NAME, forwardingRuleOptions));
   }

   @Test(groups = "live")
   public void testInsertTargetPool() {
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions.Builder(BACKUP_TARGETPOOL_NAME)
      .description(DESCRIPTION_BACKUP)
      .sessionAffinity(SessionAffinityValue.CLIENT_IP)
      .build();
      assertOperationDoneSuccessfully(api().create(targetPoolCreationOptions));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool")
   public void testInsertTargetPool2(){
      TargetPool targetPool = api().get(BACKUP_TARGETPOOL_NAME);
      assertNotNull(targetPool);
      // Make a Target Pool with a backup and failoverRatio specified.
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions.Builder(TARGETPOOL_NAME)
         .description(DESCRIPTION)
         .sessionAffinity(SessionAffinityValue.CLIENT_IP)
         .backupPool(targetPool.selfLink())
         .failoverRatio((float) 0.5)
         .build();
      assertOperationDoneSuccessfully(api().create(targetPoolCreationOptions));
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
      assertOperationDoneSuccessfully(api().addInstance(TARGETPOOL_NAME, instances));
      TargetPool targetPool = api().get(TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.name(), TARGETPOOL_NAME);
      assertEquals(targetPool.instances(), instances);
   }

   @Test(groups = "live", dependsOnMethods = {"testInsertTargetPool2", "testCreateInstanceAndHealthCheck"})
   public void testAddHealthCheckTargetPool() {
      assertOperationDoneSuccessfully(api().addHealthCheck(TARGETPOOL_NAME, httpHealthChecks));
      TargetPool targetPool = api().get(TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.name(), TARGETPOOL_NAME);
      assertEquals(targetPool.healthChecks(), httpHealthChecks);
   }

   @Test(groups = "live", dependsOnMethods = {"testAddHealthCheckTargetPool", "testAddInstanceTargetPool"} )
   public void testGetHealthTargetPool() {
      TargetPool targetPool = api().get(TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.instances(), instances);
      assertEquals(targetPool.healthChecks(), httpHealthChecks);

      HealthStatus healthStatus = api().getHealth(TARGETPOOL_NAME, instances.get(0));
      assertNotNull(healthStatus);
      assertEquals(healthStatus.healthStatus().get(0).instance(), instances.get(0));
      assertEquals(healthStatus.healthStatus().get(0).healthState(), "UNHEALTHY");
   }

   @Test(groups = "live", dependsOnMethods = "testGetHealthTargetPool")
   public void testRemoveInstanceTargetPool() {
      assertOperationDoneSuccessfully(api().removeInstance(TARGETPOOL_NAME, instances));

      TargetPool targetPool = api().get(TARGETPOOL_NAME);

      assertNotNull(targetPool);
      assertEquals(targetPool.name(), TARGETPOOL_NAME);
      assertNotEquals(targetPool.instances(), instances);
   }

   @Test(groups = "live", dependsOnMethods = "testGetHealthTargetPool")
   public void testRemoveHealthCheckTargetPool() {
      assertOperationDoneSuccessfully(api().removeHealthCheck(TARGETPOOL_NAME, httpHealthChecks));

      TargetPool targetPool = api().get(TARGETPOOL_NAME);

      assertNotNull(targetPool);
      assertEquals(targetPool.name(), TARGETPOOL_NAME);
      assertNotEquals(targetPool.healthChecks(), httpHealthChecks);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool")
   public void testListTargetPool() {
      ListPage<TargetPool> targetPool = api().list(filter("name eq " + BACKUP_TARGETPOOL_NAME)).next();
      assertEquals(Iterables.size(targetPool), 1);
   }

   @Test(groups = "live", dependsOnMethods = {"testInsertTargetPool2"})
   public void testListBackupTargetPool() {
      TargetPoolCreationOptions options = new TargetPoolCreationOptions.Builder(THIRD_TARGETPOOL_NAME)
      .description("A targetPool for testing setBackup.").build();
      assertOperationDoneSuccessfully(api().create(options));
      TargetPool targetPool = api().get(THIRD_TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.name(), THIRD_TARGETPOOL_NAME);
      assertEquals(targetPool.backupPool(), null);

      URI selfLink = api().get(TARGETPOOL_NAME).selfLink();

      Float failoverRatio = Float.valueOf((float) 0.5);
      assertOperationDoneSuccessfully(api().setBackup(THIRD_TARGETPOOL_NAME, failoverRatio, selfLink));

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
      assertOperationDoneSuccessfully(api().delete(THIRD_TARGETPOOL_NAME));
      assertOperationDoneSuccessfully(api.forwardingRulesInRegion(DEFAULT_REGION_NAME).delete(FORWARDING_RULE_NAME));
      assertOperationDoneSuccessfully(api().delete(TARGETPOOL_NAME));
      assertOperationDoneSuccessfully(api().delete(BACKUP_TARGETPOOL_NAME));
   }

   @AfterClass(groups = { "integration", "live" })
   public void testCleanup(){
      InstanceApi instanceApi = api.instancesInZone(DEFAULT_ZONE_NAME);
      HttpHealthCheckApi httpHealthCheckApi = api.httpHeathChecks();

      try {
         waitOperationDone(instanceApi.delete(INSTANCE_NAME));
         waitOperationDone(api.networks().delete(INSTANCE_NETWORK_NAME));
         waitOperationDone(httpHealthCheckApi.delete(HEALTHCHECK_NAME));
      } catch (Exception e) {
         // we don't really care about any exception here, so just delete away.
       }
   }

}
