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
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.internal.BaseImageExtensionLiveTest;
import org.jclouds.domain.Location;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Live tests for the {@link org.jclouds.compute.extensions.ImageExtension}
 * integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeImageExtensionLiveTest")
public class AzureComputeImageExtensionLiveTest extends BaseImageExtensionLiveTest {

   private LoadingCache<String, ResourceGroup> resourceGroupMap;
   private Predicate<URI> resourceDeleted;
   private ResourceGroup testResourceGroup;
   
   public AzureComputeImageExtensionLiveTest() {
      provider = "azurecompute-arm";
   }
   
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      resourceGroupMap = context.utils().injector()
            .getInstance(Key.get(new TypeLiteral<LoadingCache<String, ResourceGroup>>() {
            }));
      resourceDeleted = context.utils().injector().getInstance(Key.get(new TypeLiteral<Predicate<URI>>() {
      }, Names.named(TIMEOUT_RESOURCE_DELETED)));
      createResourceGroup();
   }
   
   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      try {
         URI uri = view.unwrapApi(AzureComputeApi.class).getResourceGroupApi().delete(testResourceGroup.name());
         if (uri != null) {
            assertTrue(resourceDeleted.apply(uri),
                  String.format("Resource %s was not terminated in the configured timeout", uri));
         }
      } finally {
         super.tearDownContext();
      }
   }

   @Override
   protected void prepareNodeBeforeCreatingImage(NodeMetadata node) {
      // Don't wrap in the init-script, since the comand will clear the user
      // config, and jclouds won't be able to execute more than one command
      // (won't be able to poll for the execution status of the command when
      // running with the init-script)
      ExecResponse result = view.getComputeService().runScriptOnNode(node.getId(), "waagent -deprovision+user -force",
            wrapInInitScript(false));
      assertEquals(result.getExitStatus(), 0);
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      AzureLiveTestUtils.defaultProperties(properties, getClass().getSimpleName().toLowerCase());
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
            authorizePublicKey(keyPair.get("public")).overrideLoginPrivateKey(keyPair.get("private")));
   }

   private void createResourceGroup() {
      Location location = getNodeTemplate().build().getLocation();
      testResourceGroup = resourceGroupMap.getUnchecked(location.getId());
   }
}
