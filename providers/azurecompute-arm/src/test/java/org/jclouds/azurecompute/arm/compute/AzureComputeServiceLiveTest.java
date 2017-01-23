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

import static org.jclouds.compute.options.TemplateOptions.Builder.authorizePublicKey;

import java.util.Properties;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.java.InstallJDK;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.cache.LoadingCache;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Live tests for the {@link org.jclouds.compute.ComputeService} integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeServiceLiveTest")
public class AzureComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   
   private LoadingCache<String, ResourceGroup> resourceGroupMap;

   public AzureComputeServiceLiveTest() {
      provider = "azurecompute-arm";
   }

   @Override
   public void initializeContext() {
      super.initializeContext();
      resourceGroupMap = context.utils().injector()
            .getInstance(Key.get(new TypeLiteral<LoadingCache<String, ResourceGroup>>() {
            }));
   }

   @Override
   @AfterClass(groups = "live", alwaysRun = true)
   protected void tearDownContext() {
      try {
         if (template != null) {
            ResourceGroup rg = resourceGroupMap.getIfPresent(template.getLocation().getId());
            if (rg != null) {
               AzureComputeApi api = view.unwrapApi(AzureComputeApi.class);
               api.getResourceGroupApi().delete(rg.name());
            }
         }
      } finally {
         super.tearDownContext();
      }
   }

   @Override
   protected LoggingModule getLoggingModule() {
      return new SLF4JLoggingModule();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      return AzureComputeProviderMetadata.builder().build();
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      AzureLiveTestUtils.defaultProperties(properties);
      setIfTestSystemPropertyPresent(properties, "oauth.endpoint");
      return properties;
   }

   @Override
   protected TemplateBuilder templateBuilder() {
      return super.templateBuilder().options(
            authorizePublicKey(keyPair.get("public")).overrideLoginPrivateKey(keyPair.get("private")));
   }

   @Override
   protected Template addRunScriptToTemplate(Template template) {
      template.getOptions().runScript(
            Statements.newStatementList(new Statement[] { AdminAccess.standard(), Statements.exec("sleep 50"),
                  InstallJDK.fromOpenJDK() }));
      return template;
   }
}
