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
package org.jclouds.packet.features;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.packet.compute.internal.BasePacketApiLiveTest;
import org.jclouds.packet.domain.BillingCycle;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.SshKey;
import org.jclouds.ssh.SshKeys;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import static org.jclouds.packet.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

@Test(groups = "live", singleThreaded = true, testName = "DeviceApiLiveTest")
public class DeviceApiLiveTest extends BasePacketApiLiveTest {

   private SshKey sshKey;
   private String deviceId;

   @BeforeClass
   public void setupDevice() {
      Map<String, String> keyPair = SshKeys.generate();
      sshKey = api.sshKeyApi().create(prefix + "-device-livetest", keyPair.get("public"));
   }

   @AfterClass(alwaysRun = true)
   public void tearDown() {
      if (sshKey != null) {
         api.sshKeyApi().delete(sshKey.id());
      }
   }

   public void testCreate() {
      Device deviceCreated = api().create(
              Device.CreateDevice.builder()
                      .hostname(prefix + "-device-livetest")
                      .plan("baremetal_0")
                      .billingCycle(BillingCycle.HOURLY.value())
                      .facility("ewr1")
                      .features(ImmutableMap.<String, String>of())
                      .operatingSystem("ubuntu_16_04")
                      .locked(false)
                      .userdata("")
                      .tags(ImmutableSet.<String> of())
                      .build()
      );
      deviceId = deviceCreated.id();
      assertNodeRunning(deviceId);
      Device device = api().get(deviceId);
      assertNotNull(device, "Device must not be null");
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testReboot() {
      api().reboot(deviceId);
      assertNodeRunning(deviceId);
   }

   @Test(groups = "live", dependsOnMethods = "testReboot")
   public void testPowerOff() {
      api().powerOff(deviceId);
      assertNodeSuspended(deviceId);
   }

   @Test(groups = "live", dependsOnMethods = "testPowerOff")
   public void testPowerOn() {
      api().powerOn(deviceId);
      assertNodeRunning(deviceId);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list().concat(), new Predicate<Device>() {
         @Override
         public boolean apply(Device input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All devices must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some devices to be returned");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testListOnePage() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(api().list(page(1).perPage(5)).allMatch(new Predicate<Device>() {
         @Override
         public boolean apply(Device input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All devices must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some devices to be returned");
   }

   @Test(dependsOnMethods = "testPowerOn", alwaysRun = true)
   public void testDelete() throws InterruptedException {
      if (deviceId != null) {
         api().delete(deviceId);
         assertNodeTerminated(deviceId);
         assertNull(api().get(deviceId));
      }
   }

   private DeviceApi api() {
      return api.deviceApi(identity);
   }
}
