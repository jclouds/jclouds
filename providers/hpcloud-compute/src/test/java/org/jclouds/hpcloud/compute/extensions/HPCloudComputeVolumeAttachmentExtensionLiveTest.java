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
package org.jclouds.hpcloud.compute.extensions;

import java.util.Properties;

import org.jclouds.hpcloud.compute.HPCloudComputeProviderMetadata;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAttachmentApiLiveTest;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "HPCloudComputeVolumeAttachmentExtensionLiveTest", singleThreaded = true)
public class HPCloudComputeVolumeAttachmentExtensionLiveTest extends VolumeAttachmentApiLiveTest {

   public HPCloudComputeVolumeAttachmentExtensionLiveTest() {
      HPCloudComputeProviderMetadata metadata = new HPCloudComputeProviderMetadata();
      provider = metadata.getId();
      System.setProperty("test." + provider + ".endpoint", metadata.getEndpoint());
      System.setProperty(KeystoneProperties.CREDENTIAL_TYPE, CredentialTypes.API_ACCESS_KEY_CREDENTIALS);
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      volumeProviderVersion = setIfTestSystemPropertyPresent(props, provider + ".volume-provider-version", "1.0");
      singleRegion = setIfTestSystemPropertyPresent(props, provider + ".region", "region-a.geo-1");
      return props;
   }

}
