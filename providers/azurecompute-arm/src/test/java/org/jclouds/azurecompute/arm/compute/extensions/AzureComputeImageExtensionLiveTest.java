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

import static org.jclouds.compute.options.TemplateOptions.Builder.authorizePublicKey;

import java.util.Map;
import java.util.Properties;

import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.internal.BaseImageExtensionLiveTest;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Live tests for the {@link org.jclouds.compute.extensions.ImageExtension}
 * integration.
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
      AzureLiveTestUtils.defaultProperties(properties);
      setIfTestSystemPropertyPresent(properties, "oauth.endpoint");
      return properties;
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      return AzureComputeProviderMetadata.builder().build();
   }

   @Override
   public TemplateBuilder getNodeTemplate() {
      Map<String, String> keyPair = ComputeTestUtils.setupKeyPair();
      return super.getNodeTemplate().options(
            authorizePublicKey(keyPair.get("public"))
            .overrideLoginPrivateKey(keyPair.get("private")));
   }


}
