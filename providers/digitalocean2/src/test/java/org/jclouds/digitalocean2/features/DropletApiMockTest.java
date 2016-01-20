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

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.digitalocean2.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Backup;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.DropletCreate;
import org.jclouds.digitalocean2.domain.Kernel;
import org.jclouds.digitalocean2.domain.Snapshot;
import org.jclouds.digitalocean2.domain.options.CreateDropletOptions;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiMockTest;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

@Test(groups = "unit", testName = "DropletApiMockTest", singleThreaded = true)
public class DropletApiMockTest extends BaseDigitalOcean2ApiMockTest {

   public void testListDroplets() throws InterruptedException {
      server.enqueue(jsonResponse("/droplets-first.json"));
      server.enqueue(jsonResponse("/droplets-last.json"));

      Iterable<Droplet> droplets = api.dropletApi().list().concat();

      assertEquals(size(droplets), 2); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/droplets");
      assertSent(server, "GET", "/droplets?page=2&per_page=1");
   }

   public void testListDropletsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Droplet> droplets = api.dropletApi().list().concat();

      assertTrue(isEmpty(droplets));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets");
   }

   public void testListDropletsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/droplets-first.json"));

      Iterable<Droplet> droplets = api.dropletApi().list(page(1).perPage(20));

      assertEquals(size(droplets), 1);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/droplets?page=1&per_page=20");
   }

   public void testListDropletsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Droplet> droplets = api.dropletApi().list(page(1).perPage(20));

      assertTrue(isEmpty(droplets));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets?page=1&per_page=20");
   }
   
   public void testGetDroplet() throws InterruptedException {
      server.enqueue(jsonResponse("/droplet.json"));

      Droplet droplet = api.dropletApi().get(1);

      assertEquals(droplet, dropletFromResource("/droplet.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/1");
   }

   public void testGetDropletReturns404() throws InterruptedException {
      server.enqueue(response404());

      Droplet droplet = api.dropletApi().get(1);

      assertNull(droplet);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/1");
   }
   
   public void testCreateDroplet() throws InterruptedException {
      server.enqueue(jsonResponse("/droplet-create-res.json"));

      DropletCreate droplet = api.dropletApi().create("digitalocean2-s-d5e", "sfo1", "512mb", "6374124", CreateDropletOptions.builder().addSshKeyId(421192).build());

      assertEquals(droplet, objectFromResource("/droplet-create-res.json", DropletCreate.class));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/droplets", stringFromResource("/droplet-create-req.json"));
   }
   
   public void testListKernels() throws InterruptedException {
      server.enqueue(jsonResponse("/kernels-first.json"));
      server.enqueue(jsonResponse("/kernels-last.json"));

      Iterable<Kernel> kernels = api.dropletApi().listKernels(5425561).concat();

      assertEquals(size(kernels), 10); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/droplets/5425561/kernels");
      assertSent(server, "GET", "/droplets/5425561/kernels?page=2");
   }

   public void testListKernelsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Kernel> kernels = api.dropletApi().listKernels(5425561).concat();

      assertTrue(isEmpty(kernels));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/5425561/kernels");
   }
   
   public void testListKernelsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/kernels-first.json"));

      Iterable<Kernel> kernels = api.dropletApi().listKernels(5425561, page(1).perPage(20));

      assertEquals(size(kernels), 5);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/droplets/5425561/kernels?page=1&per_page=20");
   }

   public void testListKernelsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Kernel> kernels = api.dropletApi().listKernels(5425561, page(1).perPage(20));

      assertTrue(isEmpty(kernels));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/5425561/kernels?page=1&per_page=20");
   }
   
   public void testListActions() throws InterruptedException {
      server.enqueue(jsonResponse("/actions-first.json"));
      server.enqueue(jsonResponse("/actions-last.json"));

      Iterable<Action> actions = api.dropletApi().listActions(1).concat();

      assertEquals(size(actions), 8); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/droplets/1/actions");
      assertSent(server, "GET", "/droplets/1/actions?page=2&per_page=5");
   }

   public void testListActionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Action> actions = api.dropletApi().listActions(1).concat();

      assertTrue(isEmpty(actions));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/1/actions");
   }

   public void testListActionsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/actions-first.json"));

      Iterable<Action> actions = api.dropletApi().listActions(1, page(1).perPage(5));

      assertEquals(size(actions), 5);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/droplets/1/actions?page=1&per_page=5");
   }

   public void testListActionsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Action> actions = api.dropletApi().listActions(1, page(1).perPage(5));

      assertTrue(isEmpty(actions));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/1/actions?page=1&per_page=5");
   }
   
   public void testListBackups() throws InterruptedException {
      server.enqueue(jsonResponse("/backups-first.json"));
      server.enqueue(jsonResponse("/backups-last.json"));

      Iterable<Backup> backups = api.dropletApi().listBackups(5425561).concat();

      assertEquals(size(backups), 2); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/droplets/5425561/backups");
      assertSent(server, "GET", "/droplets/5425561/backups?page=2");
   }

   public void testListBackupsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Backup> backups = api.dropletApi().listBackups(5425561).concat();

      assertTrue(isEmpty(backups));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/5425561/backups");
   }
   
   public void testListBackupsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/backups-first.json"));

      Iterable<Backup> backups = api.dropletApi().listBackups(5425561, page(1).perPage(20));

      assertEquals(size(backups), 1);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/droplets/5425561/backups?page=1&per_page=20");
   }

   public void testListBackupsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Backup> backups = api.dropletApi().listBackups(5425561, page(1).perPage(20));

      assertTrue(isEmpty(backups));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/5425561/backups?page=1&per_page=20");
   }
   
   public void testListSnapshots() throws InterruptedException {
      server.enqueue(jsonResponse("/snapshots-first.json"));
      server.enqueue(jsonResponse("/snapshots-last.json"));

      Iterable<Snapshot> snapshots = api.dropletApi().listSnapshots(5425561).concat();

      assertEquals(size(snapshots), 2); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/droplets/5425561/snapshots");
      assertSent(server, "GET", "/droplets/5425561/snapshots?page=2");
   }

   public void testListSnapshotsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Snapshot> snapshots = api.dropletApi().listSnapshots(5425561).concat();

      assertTrue(isEmpty(snapshots));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/5425561/snapshots");
   }
   
   public void testListSnapshotsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/snapshots-first.json"));

      Iterable<Snapshot> snapshots = api.dropletApi().listSnapshots(5425561, page(1).perPage(20));

      assertEquals(size(snapshots), 1);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/droplets/5425561/snapshots?page=1&per_page=20");
   }

   public void testListSnapshotsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Snapshot> snapshots = api.dropletApi().listSnapshots(5425561, page(1).perPage(20));

      assertTrue(isEmpty(snapshots));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/droplets/5425561/snapshots?page=1&per_page=20");
   }
   
   public void testDeleteDroplet() throws InterruptedException {
      server.enqueue(response204());

      api.dropletApi().delete(1);
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/droplets/1");
   }

   public void testDeleteDropletReturns404() throws InterruptedException {
      server.enqueue(response404());

      api.dropletApi().delete(1);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/droplets/1");
   }
   
   public void testPowerCycleDroplet() throws InterruptedException {
      server.enqueue(jsonResponse("/power-cycle.json"));

      Action action = api.dropletApi().powerCycle(1);
      
      assertEquals(action, actionFromResource("/power-cycle.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/droplets/1/actions", "{\"type\":\"power_cycle\"}");
   }
   
   public void testPowerOn() throws InterruptedException {
      server.enqueue(jsonResponse("/power-on.json"));

      Action action = api.dropletApi().powerOn(1);
      
      assertEquals(action, actionFromResource("/power-on.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/droplets/1/actions", "{\"type\":\"power_on\"}");
   }
   
   public void testPowerOff() throws InterruptedException {
      server.enqueue(jsonResponse("/power-off.json"));

      Action action = api.dropletApi().powerOff(1);
      
      assertEquals(action, actionFromResource("/power-off.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/droplets/1/actions", "{\"type\":\"power_off\"}");
   }
   
   public void testReboot() throws InterruptedException {
      server.enqueue(jsonResponse("/reboot.json"));

      Action action = api.dropletApi().reboot(1);
      
      assertEquals(action, actionFromResource("/reboot.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/droplets/1/actions", "{\"type\":\"reboot\"}");
   }
   
   public void testShutdown() throws InterruptedException {
      server.enqueue(jsonResponse("/shutdown.json"));

      Action action = api.dropletApi().shutdown(1);
      
      assertEquals(action, actionFromResource("/shutdown.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/droplets/1/actions", "{\"type\":\"shutdown\"}");
   }
   
   public void testSnapshot() throws InterruptedException {
      server.enqueue(jsonResponse("/snapshot.json"));

      Action action = api.dropletApi().snapshot(1, "foo");
      
      assertEquals(action, actionFromResource("/snapshot.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/droplets/1/actions", "{\"type\":\"snapshot\",\"name\":\"foo\"}");
   }
   
   private Droplet dropletFromResource(String resource) {
      return onlyObjectFromResource(resource, new TypeToken<Map<String, Droplet>>() {
         private static final long serialVersionUID = 1L;
      }); 
   }
   
   private Action actionFromResource(String resource) {
      return onlyObjectFromResource(resource, new TypeToken<Map<String, Action>>() {
         private static final long serialVersionUID = 1L;
      }); 
   }
}
