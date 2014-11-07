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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.domain.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.compute.predicates.AtomicOperationDone;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.NetworkApi;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

@Test
public class CreateNetworkIfNeededTest {

   private static final String BASE_URL = "https://www.googleapis.com/compute/v1/projects";

   public void testApply() {
      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      NetworkApi nwApi = createMock(NetworkApi.class);
      ResourceFunctions resources = createMock(ResourceFunctions.class);

      Network network = Network.create( //
            "abcd", // id
            URI.create(BASE_URL + "/myproject/global/networks/this-network"), // selfLink
            "this-network", // name
            null, // description
            "0.0.0.0/0", // rangeIPv4
            null // gatewayIPv4
      );

      Operation createOp = new ParseGlobalOperationTest().expected();

      Supplier<String> userProject = Suppliers.ofInstance("myproject");

      expect(api.getNetworkApi(userProject.get())).andReturn(nwApi).atLeastOnce();

      expect(nwApi.createInIPv4Range("this-network", "0.0.0.0/0")) .andReturn(createOp);
      expect(resources.operation(createOp.selfLink())).andReturn(createOp);
      expect(nwApi.get("this-network")).andReturn(null);
      expect(nwApi.get("this-network")).andReturn(network);

      replay(api, nwApi, resources);

      NetworkAndAddressRange input = NetworkAndAddressRange.create("this-network", "0.0.0.0/0", null);

      AtomicOperationDone pred = atomicOperationDone(api, resources);

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, userProject, pred);

      assertEquals(creator.apply(input), network);

      verify(api, nwApi, resources);
   }

   public void testApplyWithGateway() {
      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      NetworkApi nwApi = createMock(NetworkApi.class);
      ResourceFunctions resources = createMock(ResourceFunctions.class);

      Network network = Network.create( //
            "abcd", // id
            URI.create(BASE_URL + "/myproject/global/networks/this-network"), // selfLink
            "this-network", // name
            null, // description
            "0.0.0.0/0", // rangeIPv4
            "1.2.3.4" // gatewayIPv4
      );

      Operation createOp = new ParseGlobalOperationTest().expected();

      Supplier<String> userProject = Suppliers.ofInstance("myproject");

      expect(api.getNetworkApi(userProject.get())).andReturn(nwApi).atLeastOnce();

      expect(nwApi.createInIPv4RangeWithGateway("this-network", "0.0.0.0/0", "1.2.3.4")).andReturn(createOp);
      expect(resources.operation(createOp.selfLink())).andReturn(createOp);
      expect(nwApi.get("this-network")).andReturn(null);
      expect(nwApi.get("this-network")).andReturn(network);

      replay(api, nwApi, resources);

      NetworkAndAddressRange input = NetworkAndAddressRange.create("this-network", "0.0.0.0/0", "1.2.3.4");

      AtomicOperationDone pred = atomicOperationDone(api, resources);

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, userProject, pred);

      assertEquals(creator.apply(input), network);

      verify(api, nwApi, resources);
   }

   private AtomicOperationDone atomicOperationDone(final GoogleComputeEngineApi api,
         final ResourceFunctions resources) {
      return Guice.createInjector(new AbstractModule() { // Rather than opening ctor public
         @Override protected void configure() {
            bind(GoogleComputeEngineApi.class).toInstance(api);
            bind(ResourceFunctions.class).toInstance(resources);
         }

         @Provides @UserProject Supplier<String> project() {
            return Suppliers.ofInstance("myproject");
         }
      }).getInstance(AtomicOperationDone.class);
   }
}
