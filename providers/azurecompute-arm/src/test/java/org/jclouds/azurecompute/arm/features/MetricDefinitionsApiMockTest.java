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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.azurecompute.arm.domain.MetricDefinition;
import org.jclouds.azurecompute.arm.domain.MetricName;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "MetricDefinitionsApiMockTest", singleThreaded = true)
public class MetricDefinitionsApiMockTest extends BaseAzureComputeApiMockTest {

   private final String resourceId = "resourceGroups/myresourceGroup/providers/Microsoft.Compute/virtualMachines/myvm";
   private final String filter = "(name.value eq 'Percentage CPU')";

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/metricdefinitions.json"));
      final MetricDefinitionsApi metricDefinitionsApi = api.getMetricsDefinitionsApi(resourceId);
      assertEquals(metricDefinitionsApi.list(filter), ImmutableList.of(MetricDefinition.create(
            "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup/providers/Microsoft"
                  + ".Compute/virtualMachines/myvm", MetricName.create("Percentage CPU", "Percentage CPU"),
            Boolean.FALSE, "Percent", MetricDefinition.AggregationType.Average,
            ImmutableList.<MetricDefinition.MetricAvailability> of(
                  MetricDefinition.MetricAvailability.create("PT1M", "P30D"),
                  MetricDefinition.MetricAvailability.create("PT1H", "P30D")),
            "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup/providers"
                  + "/Microsoft.Compute/virtualMachines/myvm/providers/microsoft"
                  + ".insights/metricdefinitions/Percentage " + "CPU")));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourceGroup/providers/Microsoft"
            + ".Compute/virtualMachines/myvm/providers/microsoft.insights/metricdefinitions?$filter=%28name"
            + ".value%20eq%20%27Percentage%20CPU%27%29&api-version=2017-05-01-preview");
   }

   public void testEmptyList() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final MetricDefinitionsApi metricDefinitionsApi = api.getMetricsDefinitionsApi(resourceId);

      assertTrue(metricDefinitionsApi.list(filter).isEmpty());

      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourceGroup/providers/Microsoft"
            + ".Compute/virtualMachines/myvm/providers/microsoft.insights/metricdefinitions?$filter=%28name"
            + ".value%20eq%20%27Percentage%20CPU%27%29&api-version=2017-05-01-preview");
   }
}
