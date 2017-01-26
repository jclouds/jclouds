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

import org.jclouds.packet.compute.internal.BasePacketApiMockTest;
import org.jclouds.packet.domain.BillingCycle;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.Device.CreateDevice;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.packet.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "DeviceApiMockTest", singleThreaded = true)
public class DeviceApiMockTest extends BasePacketApiMockTest {

   public void testListDevices() throws InterruptedException {
      server.enqueue(jsonResponse("/devices-first.json"));
      server.enqueue(jsonResponse("/devices-last.json"));

      Iterable<Device> devices = api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").list().concat();

      assertEquals(size(devices), 7); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/projects/93907f48-adfe-43ed-ad89-0e6e83721a54/devices");
      assertSent(server, "GET", "/projects/93907f48-adfe-43ed-ad89-0e6e83721a54/devices?page=2");
   }

   public void testListDevicesReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Device> devices = api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").list().concat();

      assertTrue(isEmpty(devices));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/projects/93907f48-adfe-43ed-ad89-0e6e83721a54/devices");
   }

   public void testListDevicesWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/devices-first.json"));

      Iterable<Device> devices = api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").list(page(1).perPage(5));

      assertEquals(size(devices), 5);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/projects/93907f48-adfe-43ed-ad89-0e6e83721a54/devices?page=1&per_page=5");
   }

   public void testListDevicesWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Device> actions = api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").list(page(1).perPage(5));

      assertTrue(isEmpty(actions));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/projects/93907f48-adfe-43ed-ad89-0e6e83721a54/devices?page=1&per_page=5");
   }

   public void testGetDevice() throws InterruptedException {
      server.enqueue(jsonResponse("/device.json"));

      Device device = api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").get("1");

      assertEquals(device, objectFromResource("/device.json", Device.class));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/devices/1");
   }

   public void testGetDeviceReturns404() throws InterruptedException {
      server.enqueue(response404());

      Device device = api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").get("1");

      assertNull(device);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/devices/1");
   }

   public void testCreateDevice() throws InterruptedException {
      server.enqueue(jsonResponse("/device-create-res.json"));

      Device device = api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").create(
              CreateDevice.builder()
                      .hostname("jclouds-device-livetest")
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

      assertEquals(device, objectFromResource("/device-create-res.json", Device.class));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/projects/93907f48-adfe-43ed-ad89-0e6e83721a54/devices", stringFromResource("/device-create-req.json"));
   }

   public void testDeleteDevice() throws InterruptedException {
      server.enqueue(response204());

      api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").delete("1");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/devices/1");
   }

   public void testDeleteDeviceReturns404() throws InterruptedException {
      server.enqueue(response404());

      api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").delete("1");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/devices/1");
   }

   public void testActionPowerOn() throws InterruptedException {
      server.enqueue(jsonResponse("/power-on.json"));

      api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").powerOn("deviceId");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/devices/deviceId/actions");
   }

   public void testActionPowerOff() throws InterruptedException {
      server.enqueue(jsonResponse("/power-off.json"));

      api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").powerOff("deviceId");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/devices/deviceId/actions");
   }

   public void testActionReboot() throws InterruptedException {
      server.enqueue(jsonResponse("/reboot.json"));

      api.deviceApi("93907f48-adfe-43ed-ad89-0e6e83721a54").reboot("deviceId");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/devices/deviceId/actions");
   }

}
