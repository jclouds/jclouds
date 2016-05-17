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
package org.jclouds.azurecompute.arm.compute;

import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import org.jclouds.providers.ProviderMetadata;

import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;

import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.RESOURCE_GROUP_NAME;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;

import com.google.inject.Module;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Live tests for the {@link org.jclouds.compute.ComputeService} integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeServiceLiveTest")
public class AzureComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   public String azureGroup;

   public AzureComputeServiceLiveTest() {
      provider = "azurecompute-arm";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      AzureComputeProviderMetadata pm = AzureComputeProviderMetadata.builder().build();
      return pm;
   }

   @Override protected Properties setupProperties() {
      azureGroup = "jc" + System.getProperty("user.name").substring(0, 3);
      Properties properties = super.setupProperties();
      long scriptTimeout = TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES);
      properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_RUNNING, scriptTimeout + "");
      properties.put(RESOURCE_GROUP_NAME, azureGroup);

      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");

      return properties;

   }
}
