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
package org.jclouds.googlecomputeengine.compute.functions;

import static com.google.common.collect.Iterators.singletonIterator;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.jclouds.googlecomputeengine.compute.functions.FirewallToIpPermissionTest.hasProtocol;
import static org.jclouds.googlecomputeengine.compute.functions.FirewallToIpPermissionTest.hasStartAndEndPort;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.ListOptions.Builder;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class NetworkToSecurityGroupTest {

   @Test
   public void testApply() {
      Supplier<String> projectSupplier = new Supplier<String>() {
         @Override
         public String get() {
            return "myproject";
         }
      };

      FirewallToIpPermission fwToPerm = new FirewallToIpPermission();

      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      FirewallApi fwApi = createMock(FirewallApi.class);

      ListOptions options = new Builder().filter("network eq .*/jclouds-test");
      expect(api.getFirewallApi(projectSupplier.get()))
              .andReturn(fwApi);
      expect(fwApi.list(options)).andReturn(
            singletonIterator(ListPage.create(ImmutableList.of(FirewallToIpPermissionTest.fwForTest()), null)));

      replay(api, fwApi);

      Network network = Network.create( //
            "abcd", // id
            URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/jclouds-test"),
            "jclouds-test", // name
            "some description", // description
            "0.0.0.0/0", // rangeIPv4
            "1.2.3.4" // gatewayIPv4
      );

      NetworkToSecurityGroup netToSg = new NetworkToSecurityGroup(fwToPerm, api, projectSupplier);

      SecurityGroup group = netToSg.apply(network);

      assertEquals(group.getId(), "jclouds-test");
      assertEquals(group.getUri(), URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/jclouds-test"));
      assertEquals(group.getIpPermissions().size(), 3);
      assertTrue(Iterables.any(group.getIpPermissions(), Predicates.and(hasProtocol(IpProtocol.TCP),
              hasStartAndEndPort(1, 10))), "No permission found for TCP, ports 1-10");
      assertTrue(Iterables.any(group.getIpPermissions(), Predicates.and(hasProtocol(IpProtocol.TCP),
              hasStartAndEndPort(33, 33))), "No permission found for TCP, port 33");
      assertTrue(Iterables.any(group.getIpPermissions(), hasProtocol(IpProtocol.ICMP)),
              "No permission found for ICMP");
   }
}
