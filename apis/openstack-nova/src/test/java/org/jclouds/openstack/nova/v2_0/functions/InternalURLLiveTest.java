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
package org.jclouds.openstack.nova.v2_0.functions;

import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.v2_0.config.InternalUrlModule;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Simple live test to check the correct loading of the internal endpoint
 * services.
 *
 *
 */
@Test(groups = "live", testName = "InternalURLLiveTest")
public class InternalURLLiveTest extends BaseNovaApiLiveTest {

   @Test(description = "InternalUrl service endpoints loader")
   public void testGetInternalUrlServiceEndpoint() throws Exception {
      String region = api.getConfiguredRegions().iterator().next();
      // List current servers to ensure that can reach nova with internalUrl ip
      try {
         assertNotNull(api.getServerApi(region).list().concat().toList());
      } catch (Exception e) {
         Assert.fail("Could not retrieve servers list using the internalUrl", e);
      }
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.setProperty(PROPERTY_CONNECTION_TIMEOUT, "5000");
      return properties;
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule(), new InternalUrlModule());
   }

}
