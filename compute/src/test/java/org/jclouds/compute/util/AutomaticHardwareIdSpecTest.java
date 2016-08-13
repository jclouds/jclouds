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
package org.jclouds.compute.util;

import org.testng.annotations.Test;

import com.google.common.base.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "AutomaticHardwareIdSpecTest")
public class AutomaticHardwareIdSpecTest {
   @Test
   public void isAutomaticIdTest() {
      assertThat(AutomaticHardwareIdSpec.isAutomaticId("automatic:cores=2;ram=256")).isTrue();
   }

   @Test
   public void isNotAutomaticId() {
      assertThat(AutomaticHardwareIdSpec.isAutomaticId("Hi, I'm a non automatic id.")).isFalse();
   }

   @Test
   public void parseAutomaticIdTest() {
      AutomaticHardwareIdSpec parser = AutomaticHardwareIdSpec.parseId("automatic:cores=2;ram=256");
      assertThat(parser.getRam()).isEqualTo(256);
      assertThat(parser.getCores()).isEqualTo(2);
      AutomaticHardwareIdSpec parser2 = AutomaticHardwareIdSpec.parseId("automatic:cores=2;ram=4096;disk=100");
      assertThat(parser2.getRam()).isEqualTo(4096);
      assertThat(parser2.getCores()).isEqualTo(2);
      assertThat(parser2.getDisk().get()).isEqualTo(100);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void parseAutomaticIdMissingValuesTest() {
      AutomaticHardwareIdSpec.parseId("automatic:cores=2");
   }

   @Test(expectedExceptions = IllegalArgumentException.class,
         expectedExceptionsMessageRegExp = "Invalid disk value: automatic:cores=2;ram=4096;disk=-100")
   public void parseAutomaticIdInvalidDiskTest() {
      AutomaticHardwareIdSpec.parseId("automatic:cores=2;ram=4096;disk=-100");
   }

   @Test
   public void generateAutomaticIdTest() {
      AutomaticHardwareIdSpec spec = AutomaticHardwareIdSpec.parseId("automatic:cores=2;ram=1024");
      assertThat(spec.toString()).isEqualTo("automatic:cores=2.0;ram=1024");
      AutomaticHardwareIdSpec spec2 = AutomaticHardwareIdSpec.parseId("automatic:cores=2;ram=4096;disk=100");
      assertThat(spec2.toString()).isEqualTo("automatic:cores=2.0;ram=4096;disk=100");
   }

   @Test
   public void automaticHardwareIdSpecBuilderTest() {
      AutomaticHardwareIdSpec spec = AutomaticHardwareIdSpec.automaticHardwareIdSpecBuilder(2.0, 2048, Optional.<Float>absent());
      assertThat(spec.getCores()).isEqualTo(2.0);
      assertThat(spec.getRam()).isEqualTo(2048);
      assertThat(spec.toString()).isEqualTo("automatic:cores=2.0;ram=2048");
      AutomaticHardwareIdSpec spec2 = AutomaticHardwareIdSpec.automaticHardwareIdSpecBuilder(4.0, 4096, Optional.of(10.0f));
      assertThat(spec2.getCores()).isEqualTo(4.0);
      assertThat(spec2.getRam()).isEqualTo(4096);
      assertThat(spec2.getDisk().get()).isEqualTo(10);
      assertThat(spec2.toString()).isEqualTo("automatic:cores=4.0;ram=4096;disk=10");
   }

   @Test(expectedExceptions = IllegalArgumentException.class,
         expectedExceptionsMessageRegExp = "Omitted or wrong minCores and minRam. If you want to" +
               " use exact values, please set the minCores and minRam values: cores=2.0, ram=0")
   public void automaticHardwareIdSpecBuilderWrongSpecsTest() {
      AutomaticHardwareIdSpec.automaticHardwareIdSpecBuilder(2.0, 0, Optional.<Float>absent());
   }

   @Test(expectedExceptions = IllegalArgumentException.class,
           expectedExceptionsMessageRegExp = "Invalid disk value: -10")
   public void automaticHardwareIdSpecBuilderWrongDiskTest() {
      AutomaticHardwareIdSpec.automaticHardwareIdSpecBuilder(2.0, 2048, Optional.of(-10.0f));
   }

}
