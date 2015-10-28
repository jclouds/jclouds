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
package org.jclouds.digitalocean2.features;

import static java.util.logging.Logger.getAnonymousLogger;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Backup;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.DropletCreate;
import org.jclouds.digitalocean2.domain.Image;
import org.jclouds.digitalocean2.domain.Kernel;
import org.jclouds.digitalocean2.domain.Key;
import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.digitalocean2.domain.Size;
import org.jclouds.digitalocean2.domain.Snapshot;
import org.jclouds.digitalocean2.domain.options.CreateDropletOptions;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Test(groups = "live", testName = "DropletApiLiveTest")
public class DropletApiLiveTest extends BaseDigitalOcean2ApiLiveTest {

   private Region region;
   private Size size;
   private Image image;
   private Key key;
   private int dropletId = -1;
   
   @BeforeClass
   public void setupDroplet() {
      region = firstAvailableRegion();
      size = cheapestSizeInRegion(region);
      image = ubuntuImageInRegion(region);
      
      Map<String, String> keyPair = ComputeTestUtils.setupKeyPair();
      key = api.keyApi().create(prefix + "-droplet-livetest", keyPair.get("public"));
   }
   
   @AfterClass(alwaysRun = true)
   public void tearDown() {
      if (key != null) {
         api.keyApi().delete(key.id());
      }
   }
   
   public void testCreate() {
      DropletCreate dropletCreate = api().create(prefix + "-droplet-livetest", region.slug(), size.slug(), image.slug(),
            CreateDropletOptions.builder().backupsEnabled(true).addSshKeyId(key.id()).build());
      dropletId = dropletCreate.droplet().id();
      assertNodeRunning(dropletId);
      Droplet droplet = api().get(dropletId);
      assertNotNull(droplet, "Droplet should not be null");
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testListDroplets() {
      assertTrue(api().list().concat().anyMatch(new Predicate<Droplet>() {
         @Override
         public boolean apply(Droplet input) {
            return input.id() == dropletId;
         }
      }), "The created droplet must be in the list");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testListKernels() {
      Iterable<Kernel> kernels = api().listKernels(dropletId).concat();
      assertEquals(kernels.iterator().next().name(), "DO-recovery-static-fsck");
   }
   
   @Test(dependsOnMethods = "testListKernels")
   public void testPowerOff() {
      api().powerOff(dropletId);
      assertNodeStopped(dropletId);
   }

   @Test(groups = "live", dependsOnMethods = "testPowerOff")
   public void testSnapshots() {
      Action action = api().snapshot(dropletId, prefix + dropletId + "-snapshot");
      assertActionCompleted(action.id());
      
      List<Snapshot> snapshots = api().listSnapshots(dropletId).concat().toList();
      assertEquals(snapshots.size(), 1, "Must contain 1 snapshot");
      
      for (Snapshot snapshot : snapshots) {
         try {
            api.imageApi().delete(snapshot.id());
         } catch (Exception ex) {
            getAnonymousLogger().warning("Could not delete snapshot: " + snapshot.id());
         }
      }
   }

   @Test(groups = "live", dependsOnMethods = "testSnapshots")
   public void testBackups() {
      Iterable<Backup> backups = api().listBackups(dropletId).concat();
      // Backups are automatically taken by DO on a weekly basis, so we can't guarantee
      // there will be any backup available. Just check that the call succeeds
      assertNotNull(backups);
   }

   @Test(groups = "live", dependsOnMethods = "testSnapshots")
   public void testListActions() {
      FluentIterable<Action> actions = api().listActions(dropletId).concat();
      assertTrue(actions.anyMatch(new Predicate<Action>() {
         @Override
         public boolean apply(Action input) {
            return "snapshot".equals(input.type());
         }
      }));
   }

   @Test(groups = "live", dependsOnMethods = "testSnapshots")
   public void testPowerOn() {
      // Apparently droplets are automatically powered on after the snapshot process
      api().powerOff(dropletId);
      assertNodeStopped(dropletId);
      
      api().powerOn(dropletId);
      assertNodeRunning(dropletId);
   }
   
   @Test(groups = "live", dependsOnMethods = "testPowerOn")
   public void testReboot() {
      Action action = api().reboot(dropletId);
      assertActionCompleted(action.id());
      assertNodeRunning(dropletId);
   }
   
   @Test(groups = "live", dependsOnMethods = "testReboot")
   public void testPowerCycle() {
      Action action = api().powerCycle(dropletId);
      assertActionCompleted(action.id());
      assertNodeRunning(dropletId);
   }
   
   @Test(groups = "live", dependsOnMethods = "testPowerCycle")
   public void testShutdown() {
      Action action = api().shutdown(dropletId);
      assertActionCompleted(action.id());
      // The shutdown action can fail if the shutdown command fails in the guest OS
      // We can not guarantee that a graceful shutdown action will en up in the droplet
      // being in OFF state
   }

   @Test(groups = "live", dependsOnMethods = "testShutdown", alwaysRun = true)
   public void testDelete() throws InterruptedException {
      if (dropletId != -1) {
         api().delete(dropletId);
         assertNodeTerminated(dropletId);
         assertNull(api().get(dropletId));
      }
   }
   
   private DropletApi api() {
      return api.dropletApi();
   }
}
