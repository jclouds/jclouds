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
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void parseAutomaticIdMissingValuesTest() {
         AutomaticHardwareIdSpec.parseId("automatic:cores=2");
   }

   @Test
   public void generateAutomaticIdTest() {
      AutomaticHardwareIdSpec spec = AutomaticHardwareIdSpec.parseId("automatic:cores=2;ram=1024");
      assertThat(spec.toString()).isEqualTo("automatic:cores=2.0;ram=1024");
   }
}
