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
package org.jclouds.profitbricks.http.parser.loadbalancer;

import com.google.common.collect.Lists;
import java.util.List;
import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.domain.LoadBalancer.Algorithm;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "LoadBalancerResponseHandlerTest")
public class LoadBalancerResponseHandlerTest extends BaseResponseHandlerTest<LoadBalancer> {

   @Override
   protected ParseSax<LoadBalancer> createParser() {
      return factory.create(injector.getInstance(LoadBalancerResponseHandler.class));
   }

   protected DateCodecFactory createDateParser() {
      return injector.getInstance(DateCodecFactory.class);
   }

   @Test
   public void testParseResponseFromGetLoadbalancer() {
      ParseSax<LoadBalancer> parser = createParser();

      LoadBalancer actual = parser.parse(payloadFromResource("/loadbalancer/loadbalancer.xml"));
      assertNotNull(actual, "Parsed content returned null");

      DateCodec dateParser = createDateParser().iso8601();

      List<Storage> emptyStorages = Lists.newArrayList();

      List<Server> balancedServers = Lists.newArrayList();
      balancedServers.add(Server.builder()
              .activate(true)
              .balancedNicId("balanced-nic-id")
              .id("server-id")
              .name("server-name")
              .storages(emptyStorages)
              .build());
      List<Firewall> firewalls = Lists.newArrayList();
      firewalls.add(Firewall.builder()
              .id("firewall-id")
              .nicId("nic-id")
              .active(false)
              .state(ProvisioningState.AVAILABLE)
              .build());

      LoadBalancer expected = LoadBalancer.builder()
              .id("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
              .loadBalancerAlgorithm(Algorithm.ROUND_ROBIN)
              .name("load-balancer-name")
              .dataCenterId("datacenter-id")
              .dataCenterVersion("datacenter-version")
              .internetAccess(true)
              .ip("192.168.0.1")
              .lanId("lan-id")
              .state(ProvisioningState.AVAILABLE)
              .creationTime(dateParser.toDate("2014-12-12T03:08:35.629Z"))
              .lastModificationTime(dateParser.toDate("2014-12-12T03:08:35.629Z"))
              .firewalls(firewalls)
              .balancedServers(balancedServers)
              .build();

      assertEquals(actual, expected);

   }
}
