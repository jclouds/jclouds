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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jclouds.azurecompute.arm.domain.Metric;
import org.jclouds.azurecompute.arm.domain.MetricData;
import org.jclouds.azurecompute.arm.domain.MetricName;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "MetricsApiMockTest", singleThreaded = true)
public class MetricsApiMockTest extends BaseAzureComputeApiMockTest {

   private final String resourceId = "resourceGroups/myresourceGroup/providers/Microsoft.Compute/virtualMachines/myvm";
   private final String filter = "(name.value eq 'Percentage CPU') and startTime eq 2017-06-01T11:14:00Z and "
         + "endTime eq 2017-06-01T11:23:00Z and timeGrain eq duration'PT1M'";

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/metrics.json"));
      final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
      final MetricsApi metricsApi = api.getMetricsApi(resourceId);
      assertEquals(metricsApi.list(filter), ImmutableList.of(Metric.create(ImmutableList.of(MetricData
                .create(dateFormat.parse("2017-06-01T07:14:00", new ParsePosition(0)), null,
                    Double.valueOf(0.295), null, null, null)),
            "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourcegroup/providers"
                  + "/Microsoft.Compute/virtualMachines/myvm/providers/Microsoft.Insights/metrics/Percentage CPU",
            MetricName.create("Percentage CPU", "Percentage CPU"), "Microsoft.Insights/metrics", "Percent")));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourceGroup/providers/Microsoft"
            + ".Compute/virtualMachines/myvm/providers/microsoft.insights/metrics?$filter=%28name"
            + ".value%20eq%20%27Percentage%20CPU%27%29%20and%20startTime%20eq%202017-06-01T11%3A14%3A00Z%20and"
            + "%20endTime%20eq%202017-06-01T11%3A23%3A00Z%20and%20timeGrain%20eq%20duration%27PT1M%27&api-version"
            + "=2016-09-01");
   }

   public void testEmptyList() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final MetricsApi metricsAPI = api.getMetricsApi(resourceId);

      assertTrue(metricsAPI.list(filter).isEmpty());

      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/myresourceGroup/providers/Microsoft"
            + ".Compute/virtualMachines/myvm/providers/microsoft.insights/metrics?$filter=%28name"
            + ".value%20eq%20%27Percentage%20CPU%27%29%20and%20startTime%20eq%202017-06-01T11%3A14%3A00Z%20and"
            + "%20endTime%20eq%202017-06-01T11%3A23%3A00Z%20and%20timeGrain%20eq%20duration%27PT1M%27&api-version"
            + "=2016-09-01");
   }
}
