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
package org.jclouds.azurecompute.arm.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions.Builder.resourceGroup;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.Metric;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

@Test(groups = "live", singleThreaded = true)
public class MetricsApiLiveTest extends BaseComputeServiceContextLiveTest {

   private Predicate<URI> resourceDeleted;
   private AzureComputeApi api;

   private String location;
   private MetricsApi metricsApi;

   private String group;

   private String startTime;
   private SimpleDateFormat dateFormat;

   public MetricsApiLiveTest() {
      provider = "azurecompute-arm";
      group = getClass().getSimpleName().toLowerCase();
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");
      return properties;
   }

   @Override
   protected void initializeContext() {
      super.initializeContext();
      resourceDeleted = context.utils().injector().getInstance(Key.get(new TypeLiteral<Predicate<URI>>() {
      }, Names.named(TIMEOUT_RESOURCE_DELETED)));
      api = view.unwrapApi(AzureComputeApi.class);
   }

   @Override
   @BeforeClass
   public void setupContext() {
      super.setupContext();

      dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      startTime = dateFormat.format(new Date());

      NodeMetadata node = null;
      try {
         node = getOnlyElement(view.getComputeService().createNodesInGroup(group, 1, resourceGroup(group)));
      } catch (RunNodesException e) {
         fail();
      }
      String resourceId = String.format("/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/%s",
            IdReference.extractResourceGroup(node.getProviderId()), IdReference.extractName(node.getProviderId()));

      location = view.getComputeService().templateBuilder().build().getLocation().getId();
      view.unwrapApi(AzureComputeApi.class).getResourceGroupApi().create(group, location, null);
      metricsApi = api.getMetricsApi(resourceId);
   }

   @Override
   @AfterClass(alwaysRun = true)
   protected void tearDownContext() {
      try {
         view.getComputeService().destroyNodesMatching(inGroup(group));
      } finally {
         try {
            URI uri = api.getResourceGroupApi().delete(group);
            assertResourceDeleted(uri);
         } finally {
            super.tearDownContext();
         }
      }
   }

   public void listVirtualMachineMetrics() throws RunNodesException {
      List<Metric> result = metricsApi
            .list("(name.value eq 'Percentage CPU') and startTime eq " + startTime + " and endTime eq " + dateFormat
                  .format(new Date()) + " and timeGrain eq duration'PT1M'");

      // verify we have something
      assertNotNull(result);
      assertEquals(result.size(), 1);
      assertEquals(result.get(0).name().value(), "Percentage CPU");
      assertTrue(result.get(0).data().size() > 1);
   }

   private void assertResourceDeleted(final URI uri) {
      if (uri != null) {
         assertTrue(resourceDeleted.apply(uri),
               String.format("Resource %s was not deleted in the configured timeout", uri));
      }
   }

}

