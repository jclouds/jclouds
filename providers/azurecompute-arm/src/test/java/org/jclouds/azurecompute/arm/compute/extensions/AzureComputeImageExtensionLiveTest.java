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
package org.jclouds.azurecompute.arm.compute.extensions;

import com.google.inject.Module;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.config.AzureComputeProperties;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.compute.extensions.internal.BaseImageExtensionLiveTest;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.RESOURCE_GROUP_NAME;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_PORT_OPEN;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

/**
 * Live tests for the {@link org.jclouds.compute.extensions.ImageExtension} integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeImageExtensionLiveTest")
public class AzureComputeImageExtensionLiveTest extends BaseImageExtensionLiveTest {

   public AzureComputeImageExtensionLiveTest() {
      provider = "azurecompute-arm";
   }

   public static String NAME_PREFIX = "%s";

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      long scriptTimeout = TimeUnit.MILLISECONDS.convert(60, TimeUnit.MINUTES);
      properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_RUNNING, scriptTimeout + "");
      properties.setProperty(TIMEOUT_PORT_OPEN, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_TERMINATED, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_SUSPENDED, scriptTimeout + "");
      properties.put(RESOURCE_GROUP_NAME, "a5");

      properties.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      properties.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      properties.setProperty(AzureComputeProperties.OPERATION_TIMEOUT, "46000000");
      properties.setProperty(AzureComputeProperties.OPERATION_POLL_INITIAL_PERIOD, "5");
      properties.setProperty(AzureComputeProperties.OPERATION_POLL_MAX_PERIOD, "15");
      properties.setProperty(AzureComputeProperties.TCP_RULE_FORMAT, "tcp_%s-%s");
      properties.setProperty(AzureComputeProperties.TCP_RULE_REGEXP, "tcp_\\d{1,5}-\\d{1,5}");

      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");

      return properties;

   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      AzureComputeProviderMetadata pm = AzureComputeProviderMetadata.builder().build();
      return pm;
   }

}
