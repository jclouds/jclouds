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
package org.jclouds.digitalocean2.compute.options;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.options.TemplateOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "DigitalOcean2TemplateOptionsTest")
public class DigitalOcean2TemplateOptionsTest {

   @Test
   public void testSShKeyIds() {
      TemplateOptions options = new DigitalOcean2TemplateOptions().sshKeyIds(ImmutableSet.of(1, 2, 3));
      assertEquals(options.as(DigitalOcean2TemplateOptions.class).getSshKeyIds(), ImmutableSet.of(1, 2, 3));
   }

   @Test
   public void testPrivateNetworking() {
      TemplateOptions options = new DigitalOcean2TemplateOptions().privateNetworking(true);
      assertEquals(options.as(DigitalOcean2TemplateOptions.class).getPrivateNetworking(), true);
   }

   @Test
   public void testBackupsEnabled() {
      TemplateOptions options = new DigitalOcean2TemplateOptions().backupsEnabled(true);
      assertEquals(options.as(DigitalOcean2TemplateOptions.class).getBackupsEnabled(), true);
   }
   
   @Test
   public void testAutoCreateKeyPair() {
      TemplateOptions options = new DigitalOcean2TemplateOptions().autoCreateKeyPair(false);
      assertEquals(options.as(DigitalOcean2TemplateOptions.class).getAutoCreateKeyPair(), false);
   }
}
