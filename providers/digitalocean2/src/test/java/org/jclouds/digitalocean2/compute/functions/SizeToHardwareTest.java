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
package org.jclouds.digitalocean2.compute.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume.Type;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.digitalocean2.domain.Size;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Unit tests for the {@link SizeToHardware} class.
 */
@Test(groups = "unit", testName = "SizeToHardwareTest")
public class SizeToHardwareTest {

   @Test
   public void testConvertSize() {
      Size size = Size.create("2gb", true, 1.0f, 10f, 0.05f, 2048, 1, 20, ImmutableList.<String> of());
      Hardware expected = new HardwareBuilder().id("2gb").providerId("2gb").name("2gb")
            .processor(new Processor(1.0, 1.0)).ram(2048)
            .volume(new VolumeBuilder().size(20f).type(Type.LOCAL).build())
            .userMetadata(ImmutableMap.of("costPerHour", "0.05", "costPerMonth", "10")).build();

      SizeToHardware function = new SizeToHardware();
      assertEquals(function.apply(size), expected);
   }
}
