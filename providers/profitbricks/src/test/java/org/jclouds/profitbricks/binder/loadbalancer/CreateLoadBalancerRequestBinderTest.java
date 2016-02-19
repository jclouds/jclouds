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
package org.jclouds.profitbricks.binder.loadbalancer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;

import org.testng.annotations.Test;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.domain.LoadBalancer.Algorithm;

@Test(groups = "unit", testName = "CreateLoadBalancerRequestBinderTest")
public class CreateLoadBalancerRequestBinderTest {

   @Test
   public void testCreatePayload() {
      CreateLoadBalancerRequestBinder binder = new CreateLoadBalancerRequestBinder();

      List<String> serverIds = Lists.newArrayList();
      serverIds.add("server-ids");

      String actual = binder.createPayload(
              LoadBalancer.Request.creatingBuilder()
              .dataCenterId("datacenter-id")
              .name("load-balancer-name")
              .algorithm(Algorithm.ROUND_ROBIN)
              .ip("10.0.0.1")
              .lanId(2)
              .serverIds(ImmutableList.<String>of(
                              "server-id-1", "server-id-2"))
              .build());

      assertNotNull(actual, "Binder returned null payload");
      assertEquals(expectedPayload, actual);
   }

   private final String expectedPayload
           = ("<ws:createLoadBalancer>\n"
           + "            <request>\n"
           + "                <dataCenterId>datacenter-id</dataCenterId>\n"
           + "                <loadBalancerName>load-balancer-name</loadBalancerName>\n"
           + "                <loadBalancerAlgorithm>ROUND_ROBIN</loadBalancerAlgorithm>\n"
           + "                <ip>10.0.0.1</ip>\n"
           + "                <lanId>2</lanId>\n"
           + "                <serverIds>server-id-1</serverIds>\n"
           + "                <serverIds>server-id-2</serverIds>\n"
           + "            </request>\n"
           + "        </ws:createLoadBalancer>").replaceAll("\\s+", "");
}
