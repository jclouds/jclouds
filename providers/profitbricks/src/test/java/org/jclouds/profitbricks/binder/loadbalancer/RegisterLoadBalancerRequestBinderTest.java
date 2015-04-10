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
import org.jclouds.profitbricks.domain.LoadBalancer;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "RegisterLoadBalancerRequestBinderTest")
public class RegisterLoadBalancerRequestBinderTest {

   @Test
   public void testRegisterPayload() {
      RegisterLoadBalancerRequestBinder binder = new RegisterLoadBalancerRequestBinder();
      List<String> serverIds = Lists.newArrayList();
      serverIds.add("1");
      serverIds.add("2");

      LoadBalancer.Request.RegisterPayload payload = LoadBalancer.Request.RegisterPayload.create(serverIds, "load-balancer-id");

      String actual = binder.createPayload(payload);

      assertNotNull(actual, "Binder returned null payload");
      assertEquals(expectedPayload, actual);
   }

   private final String expectedPayload
           = ("        <ws:registerServersOnLoadBalancer>\n"
           + "             <request>"
           + "                <loadBalancerId>load-balancer-id</loadBalancerId>\n"
           + "                <serverIds>1</serverIds>\n"
           + "                <serverIds>2</serverIds>\n"
           + "             </request>"
           + "        </ws:registerServersOnLoadBalancer>").replaceAll("\\s+", "");
}
