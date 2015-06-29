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
package org.jclouds.profitbricks.compute.function;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.profitbricks.domain.Storage;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "StorageToVolumeTest")
public class StorageToVolumeTest {

   private StorageToVolume fnVolume;

   @BeforeTest
   public void setup() {
      this.fnVolume = new StorageToVolume();
   }

   @Test
   public void testStorageToVolume() {
      Storage storage = Storage.builder()
              .id("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
              .size(40)
              .name("hdd-1")
              .busType(Storage.BusType.VIRTIO)
              .bootDevice(true)
              .deviceNumber(1)
              .build();

      Volume actual = fnVolume.apply(storage);

      Volume expected = new VolumeBuilder()
              .id(storage.id())
              .size(40f)
              .bootDevice(true)
              .device("1")
              .type(Volume.Type.LOCAL)
              .durable(true)
              .build();

      assertEquals(actual, expected);
   }
}
