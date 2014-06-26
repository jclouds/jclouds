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
package org.jclouds.elasticstack.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Map;
import java.util.UUID;

import org.jclouds.elasticstack.compute.functions.ServerInfoToNodeMetadata.GetImageIdFromServer;
import org.jclouds.elasticstack.domain.Device;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.DriveMetrics;
import org.jclouds.elasticstack.domain.SCSIDevice;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.VNC;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Unit tests for the {@link GetImageIdFromServer} class.
 */
@Test(groups = "unit", testName = "GetImageIdFromServerTest")
public class GetImageIdFromServerTest {

   private static final String UNKNOWN_DRIVE_UUID = UUID.randomUUID().toString();

   private LoadingCache<String, DriveInfo> knownDrives = CacheBuilder.newBuilder().build(
         new CacheLoader<String, DriveInfo>() {
            @Override
            public DriveInfo load(String key) throws Exception {
               // Use a mock UUID to be able to simulate the unknown drives
               return UNKNOWN_DRIVE_UUID.equals(key) ? null : new DriveInfo.Builder().name("foo")
                     .metrics(new DriveMetrics.Builder().build()).build();
            }
         });

   private GetImageIdFromServer function = new GetImageIdFromServer(knownDrives);

   public void testImageIdExists() {
      Map<String, Device> devices = deviceMapFor(UUID.randomUUID().toString());
      Server server = serverFor(devices, devices.keySet());
      assertEquals(function.apply(server), "foo");
   }

   public void testImageIdExistsAndUsesTheFirstDevice() {
      Map<String, Device> devices = deviceMapFor(UUID.randomUUID().toString(), UNKNOWN_DRIVE_UUID);
      Server server = serverFor(devices, devices.keySet());
      assertEquals(function.apply(server), "foo");
   }

   public void testImageIdIsNullWhenNoBootableDevices() {
      Map<String, Device> devices = deviceMapFor(UUID.randomUUID().toString());
      Server server = serverFor(devices, ImmutableSet.<String> of());
      assertNull(function.apply(server));
   }

   public void testImageIdIsNullWhenNoDeviceWithGivenId() {
      Map<String, Device> devices = deviceMapFor(UUID.randomUUID().toString());
      Server server = serverFor(ImmutableMap.<String, Device> of(), devices.keySet());
      assertNull(function.apply(server));
   }

   private static Map<String, Device> deviceMapFor(String... uuids) {
      ImmutableSet.Builder<Device> devices = ImmutableSet.builder();
      for (int i = 0; i < uuids.length; i++) {
         devices.add(new SCSIDevice.Builder(i).uuid(uuids[i]).build());
      }

      return Maps.uniqueIndex(devices.build(), new Function<Device, String>() {
         @Override
         public String apply(Device input) {
            return input.getId();
         }
      });
   }

   private static Server serverFor(Map<String, Device> devices, Iterable<String> deviceIds) {
      return new Server.Builder().name("test").vnc(new VNC(null, null, false)).devices(devices)
            .bootDeviceIds(deviceIds).build();
   }
}
