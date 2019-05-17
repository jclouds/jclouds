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

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions.Builder.resourceGroup;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.NodeMetadata;
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Live tests for the {@link org.jclouds.compute.ComputeService} integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeServiceLiveTest")
public class AzureComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   
   private Predicate<URI> resourceDeleted;
   private String resourceGroupName;

   public AzureComputeServiceLiveTest() {
      provider = "azurecompute-arm";
      resourceGroupName = getClass().getSimpleName().toLowerCase();
   }

   @Override
   public void initializeContext() {
      super.initializeContext();
      resourceDeleted = context.utils().injector().getInstance(Key.get(new TypeLiteral<Predicate<URI>>() {
      }, Names.named(TIMEOUT_RESOURCE_DELETED)));
   }

   // Base method performs Iterables.elementsEqual which is case sensitive.
   // Azure API can return values in different cases so we'll perform a custom validation
   @Override
   @Test(dependsOnMethods = "testSuspendResume")
   public void testListNodesByIds() {
      final Set<String> nodeIds = copyOf(transform(nodes, new Function<NodeMetadata, String>() {

         @Override
         public String apply(NodeMetadata from) {
            return from.getId();
         }

      }));

      final Set<? extends ComputeMetadata> listedNodes = client.listNodesByIds(nodeIds);

      assertEquals(listedNodes.size(), nodes.size());

      for (ComputeMetadata listedNode : listedNodes) {
         assert listedNode.getProviderId() != null : listedNode;
         assert listedNode.getLocation() != null : listedNode;
         assertEquals(listedNode.getType(), ComputeType.NODE);
         assert nodeIds.contains(listedNode.getId());
      }
   }

   @Override
   @AfterClass(groups = "live", alwaysRun = true)
   protected void tearDownContext() {
      try {
         URI uri = view.unwrapApi(AzureComputeApi.class).getResourceGroupApi().delete(resourceGroupName);
         if (uri != null) {
            assertTrue(resourceDeleted.apply(uri),
                  String.format("Resource %s was not terminated in the configured timeout", uri));
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
            resourceGroup(resourceGroupName).authorizePublicKey(keyPair.get("public")).overrideLoginPrivateKey(
                  keyPair.get("private")));
   }

   @Override
   protected Template addRunScriptToTemplate(Template template) {
      template.getOptions().runScript(
            Statements.newStatementList(new Statement[] { AdminAccess.standard(), Statements.exec("sleep 50"),
                  InstallJDK.fromOpenJDK() }));
      return template;
   }
}
