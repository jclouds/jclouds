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

import com.google.common.collect.Lists;
import java.util.List;
import org.testng.annotations.Test;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.domain.LoadBalancer.Algorithm;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(groups = "unit", testName = "CreateLoadBalancerRequestBinderTest")
public class CreateLoadBalancerRequestBinderTest {

   @Test
   public void testCreatePayload() {
      CreateLoadBalancerRequestBinder binder = new CreateLoadBalancerRequestBinder();

      List<String> serverIds = Lists.newArrayList();
      serverIds.add("server-ids");

      String actual = binder.createPayload(LoadBalancer.Request.CreatePayload.create("datacenter-id", "load-balancer-name", Algorithm.ROUND_ROBIN, "-ip", "lan-id", serverIds));

      assertNotNull(actual, "Binder returned null payload");
      assertEquals(expectedPayload, actual);
   }

   private final String expectedPayload
           = ("<ws:createLoadBalancer>\n"
           + "            <request>\n"
           + "                <dataCenterId>datacenter-id</dataCenterId>\n"
           + "                <loadBalancerName>load-balancer-name</loadBalancerName>\n"
           + "                <loadBalancerAlgorithm>ROUND_ROBIN</loadBalancerAlgorithm>\n"
           + "                <ip>-ip</ip>\n"
           + "                <lanId>lan-id</lanId>\n"
           + "                <serverIds>server-ids</serverIds>\n"
           + "            </request>\n"
           + "        </ws:createLoadBalancer>").replaceAll("\\s+", "");
}
