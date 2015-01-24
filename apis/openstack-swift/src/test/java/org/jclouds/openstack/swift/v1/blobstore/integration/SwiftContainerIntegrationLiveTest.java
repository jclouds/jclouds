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
package org.jclouds.openstack.swift.v1.blobstore.integration;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.testng.Assert.assertTrue;

import java.util.Properties;

import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.testng.annotations.Test;
import org.testng.SkipException;

@Test(groups = "live", testName = "SwiftContainerIntegrationLiveTest")
public class SwiftContainerIntegrationLiveTest extends BaseContainerIntegrationTest {

   public SwiftContainerIntegrationLiveTest() {
      provider = "openstack-swift";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, CREDENTIAL_TYPE);
      return props;
   }

   @Override
   public void testListRootUsesDelimiter() throws InterruptedException {
      try {
         super.testListRootUsesDelimiter();
      } catch (AssertionError e) {
         // swift doesn't have the "common prefixes" in the response that s3
         // does. If we wanted this to pass, we'd need to create
         // pseudo-directories implicitly, which is costly and troublesome. It
         // is better to fail this assertion.
         assertTrue(e.getMessage().matches(".*16.* but .*15.*"), e.getMessage());
         // ^^ squishy regex to deal with various formats of testng messages.
      }
   }

   @Override
   public void testDelimiter() throws Exception {
      throw new SkipException("openstack-swift does not implement pseudo-directories");
   }
}
