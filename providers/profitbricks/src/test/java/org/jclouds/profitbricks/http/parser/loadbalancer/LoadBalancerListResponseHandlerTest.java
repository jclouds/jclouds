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

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.domain.LoadBalancer.Algorithm;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "LoadBalancerListResponseHandlerTest")
public class LoadBalancerListResponseHandlerTest extends BaseResponseHandlerTest<List<LoadBalancer>> {

   @Override
   protected ParseSax<List<LoadBalancer>> createParser() {
      return factory.create(injector.getInstance(LoadBalancerListResponseHandler.class));
   }

   protected DateCodecFactory createDateParser() {
      return injector.getInstance(DateCodecFactory.class);
   }

   @Test
   public void testParseResponseFromGetAllLoadbalancer() {
      ParseSax<List<LoadBalancer>> parser = createParser();

      List<LoadBalancer> actual = parser.parse(payloadFromResource("/loadbalancer/loadbalancers.xml"));
      assertNotNull(actual, "Parsed content returned null");

      DateCodec dateParser = createDateParser().iso8601();

      List<LoadBalancer> expected = ImmutableList.<LoadBalancer>of(LoadBalancer.builder().id("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee").loadBalancerAlgorithm(Algorithm.ROUND_ROBIN).name("load-1234567890-name")
              .dataCenterId("datacenter-id").dataCenterVersion("datacenter-version").internetAccess(true).ip("192.168.0.1").lanId("lan-id").state(ProvisioningState.AVAILABLE).creationTime(dateParser.toDate("2014-12-04T07:09:23.138Z")).lastModificationTime(dateParser.toDate("2014-12-04T07:09:23.138Z"))
              .firewalls(ImmutableList.<Firewall>of(
                              Firewall.builder().id("firewall-id").nicId("nic-id").active(false).state(ProvisioningState.AVAILABLE).build()
                      ))
              .balancedServers(ImmutableList.<Server>of(
                              Server.builder().activate(true).balancedNicId("balanced-nic-id").id("server-id").name("server-name").build()
                      )).build(),
              LoadBalancer.builder().id("qqqqqqqq-wwww-rrrr-tttt-yyyyyyyyyyyy").loadBalancerAlgorithm(Algorithm.ROUND_ROBIN).name("load-balancer-name")
              .dataCenterId("datacenter-id").dataCenterVersion("datacenter-version").internetAccess(false).ip("192.168.0.1").lanId("lan-id").state(ProvisioningState.AVAILABLE).creationTime(dateParser.toDate("2014-12-04T07:09:23.138Z")).lastModificationTime(dateParser.toDate("2014-12-04T07:09:23.138Z"))
              .firewalls(ImmutableList.<Firewall>of(
                              Firewall.builder().id("firewall-id").nicId("nic-id").active(false).state(ProvisioningState.AVAILABLE).build()
                      ))
              .balancedServers(ImmutableList.<Server>of(
                              Server.builder().activate(false).balancedNicId("balanced-nic-id").id("server-id").name("server-name").build()
                      ))
              .build()
      );
      assertEquals(actual, expected);
   }
}
