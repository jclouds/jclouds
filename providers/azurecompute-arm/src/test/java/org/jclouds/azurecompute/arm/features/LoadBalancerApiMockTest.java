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

import static com.google.common.collect.Iterables.isEmpty;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.API_VERSION_PREFIX;
import static org.jclouds.azurecompute.arm.domain.loadbalancer.LoadBalancer.SKU.SKUName.Basic;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.domain.FrontendIPConfigurations;
import org.jclouds.azurecompute.arm.domain.FrontendIPConfigurationsProperties;
import org.jclouds.azurecompute.arm.domain.loadbalancer.LoadBalancer;
import org.jclouds.azurecompute.arm.domain.loadbalancer.LoadBalancerProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "LoadBalancerApiMockTest", singleThreaded = true)
public class LoadBalancerApiMockTest extends BaseAzureComputeApiMockTest {
   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String apiVersion = "api-version=" + AzureComputeProviderMetadata.defaultProperties()
         .getProperty(API_VERSION_PREFIX + LoadBalancerApi.class.getSimpleName());
   private final String lbName = "testLoadBalancer";

   public void createLoadBalancer() throws InterruptedException {
      LoadBalancer nsg = newLoadBalancer();

      server.enqueue(jsonResponse("/loadbalancercreate.json").setResponseCode(200));
      final LoadBalancerApi loadBalancerApi = api.getLoadBalancerApi(resourcegroup);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/loadBalancers/%s?%s",
            subscriptionid, resourcegroup, lbName, apiVersion);
      
      String json = "{\"location\":\"westeurope\",\"properties\":{\"frontendIPConfigurations\":[{\"name\":\"ipConfigs"
            + "\",\"properties\":{}}]} }";
      
      LoadBalancer result = loadBalancerApi.createOrUpdate(lbName, "westeurope", null, null, nsg.properties());
      assertSent(server, "PUT", path, json);

      assertEquals(result.name(), lbName);
      assertEquals(result.location(), "westeurope");
      assertEquals(result.sku().name(), Basic);
   }

   public void getLoadBalancer() throws InterruptedException {
      server.enqueue(jsonResponse("/loadbalancerget.json").setResponseCode(200));

      final LoadBalancerApi nsgApi = api.getLoadBalancerApi(resourcegroup);
      LoadBalancer result = nsgApi.get(lbName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/loadBalancers/%s?%s",
            subscriptionid, resourcegroup, lbName, apiVersion);
      assertSent(server, "GET", path);

      assertEquals(result.name(), lbName);
      assertEquals(result.location(), "westeurope");
      assertEquals(result.properties().loadBalancingRules().size(), 1);
      assertEquals(result.properties().loadBalancingRules().get(0).name(), "lbRule1");
      assertEquals(result.sku().name(), Basic);
   }

   public void getLoadBalancerReturns404() throws InterruptedException {
      server.enqueue(response404());

      final LoadBalancerApi nsgApi = api.getLoadBalancerApi(resourcegroup);
      LoadBalancer result = nsgApi.get(lbName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/loadBalancers/%s?%s",
            subscriptionid, resourcegroup, lbName, apiVersion);
      assertSent(server, "GET", path);

      assertNull(result);
   }

   public void listLoadBalancers() throws InterruptedException {
      server.enqueue(jsonResponse("/loadbalancerlist.json").setResponseCode(200));

      final LoadBalancerApi nsgApi = api.getLoadBalancerApi(resourcegroup);
      List<LoadBalancer> result = nsgApi.list();

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/loadBalancers?%s",
            subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(result);
      assertTrue(result.size() > 0);
   }

   public void listAllLoadBalancers() throws InterruptedException {
      server.enqueue(jsonResponse("/loadbalancerlistall.json").setResponseCode(200));

      final LoadBalancerApi nsgApi = api.getLoadBalancerApi(resourcegroup);
      List<LoadBalancer> result = nsgApi.listAll();

      String path = String.format("/subscriptions/%s/providers/Microsoft.Network/loadBalancers?%s", subscriptionid, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(result);
      assertTrue(result.size() > 0);
   }

   public void listLoadBalancersReturns404() throws InterruptedException {
      server.enqueue(response404());

      final LoadBalancerApi nsgApi = api.getLoadBalancerApi(resourcegroup);
      List<LoadBalancer> result = nsgApi.list();

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/loadBalancers?%s",
            subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(isEmpty(result));
   }

   public void deleteLoadBalancer() throws InterruptedException {
      server.enqueue(response202WithHeader());

      final LoadBalancerApi nsgApi = api.getLoadBalancerApi(resourcegroup);
      URI uri = nsgApi.delete(lbName);

      assertEquals(server.getRequestCount(), 1);
      assertNotNull(uri);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/loadBalancers/%s?%s",
            subscriptionid, resourcegroup, lbName, apiVersion);
      assertSent(server, "DELETE", path);

      assertTrue(uri.toString().contains("api-version"));
      assertTrue(uri.toString().contains("operationresults"));
   }

   public void deleteLoadBalancerDoesNotExist() throws InterruptedException {
      server.enqueue(response404());

      final LoadBalancerApi nsgApi = api.getLoadBalancerApi(resourcegroup);
      URI uri = nsgApi.delete(lbName);
      assertNull(uri);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/loadBalancers/%s?%s",
            subscriptionid, resourcegroup, lbName, apiVersion);
      assertSent(server, "DELETE", path);
   }
   
   private LoadBalancer newLoadBalancer() {
      FrontendIPConfigurationsProperties frontendIPConfigurationsProperties = FrontendIPConfigurationsProperties
            .builder().build();
      FrontendIPConfigurations frontendIPConfigurations = FrontendIPConfigurations.create("ipConfigs", null,
            frontendIPConfigurationsProperties, null);
      return LoadBalancer
            .builder()
            .name(lbName)
            .location("westus")
            .properties(
                  LoadBalancerProperties.builder().frontendIPConfigurations(ImmutableList.of(frontendIPConfigurations))
                        .build()).build();
   }
}
