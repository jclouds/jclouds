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
package org.jclouds.rackspace.cloudfiles.v1.blobstore.integration;

import static org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes.API_KEY_CREDENTIALS;

import java.util.Properties;

import org.jclouds.openstack.swift.v1.blobstore.integration.SwiftServiceIntegrationLiveTest;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "CloudFilesServiceIntegrationLiveTest")
public class CloudFilesServiceIntegrationLiveTest extends SwiftServiceIntegrationLiveTest {

   public CloudFilesServiceIntegrationLiveTest() {
      provider = "rackspace-cloudfiles";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, API_KEY_CREDENTIALS);
      return props;
   }
}
