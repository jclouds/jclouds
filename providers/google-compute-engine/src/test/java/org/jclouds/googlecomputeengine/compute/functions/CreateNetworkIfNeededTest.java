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
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.GlobalOperationApi;
import org.jclouds.googlecomputeengine.features.NetworkApi;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.jclouds.googlecomputeengine.predicates.GlobalOperationDonePredicate;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

@Test
public class CreateNetworkIfNeededTest {

   private static final String BASE_URL = "https://www.googleapis.com/compute/v1/projects";

   public void testApply() {
      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      NetworkApi nwApi = createMock(NetworkApi.class);
      GlobalOperationApi globalApi = createMock(GlobalOperationApi.class);

      Network network = Network.create( //
            "abcd", // id
            URI.create(BASE_URL + "/myproject/global/networks/this-network"), // selfLink
            "this-network", // name
            null, // description
            "0.0.0.0/0", // rangeIPv4
            null // gatewayIPv4
      );

      Operation createOp = new ParseGlobalOperationTest().expected();

      Supplier<String> userProject = new Supplier<String>() {
         @Override
         public String get() {
            return "myproject";
         }
      };

      expect(api.getNetworkApi(userProject.get())).andReturn(nwApi).atLeastOnce();
      expect(api.getGlobalOperationApi(userProject.get())).andReturn(globalApi).atLeastOnce();

      expect(nwApi.createInIPv4Range("this-network", "0.0.0.0/0")) .andReturn(createOp);
      expect(globalApi.get(createOp.name())).andReturn(createOp);
      expect(nwApi.get("this-network")).andReturn(null);
      expect(nwApi.get("this-network")).andReturn(network);

      replay(api, nwApi, globalApi);

      NetworkAndAddressRange input = NetworkAndAddressRange.create("this-network", "0.0.0.0/0", null);

      GlobalOperationDonePredicate pred = globalOperationDonePredicate(api, userProject);

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, userProject, pred, 100l, 100l);

      assertEquals(creator.apply(input), network);

      verify(api, nwApi, globalApi);
   }

   public void testApplyWithGateway() {
      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      NetworkApi nwApi = createMock(NetworkApi.class);
      GlobalOperationApi globalApi = createMock(GlobalOperationApi.class);

      Network network = Network.create( //
            "abcd", // id
            URI.create(BASE_URL + "/myproject/global/networks/this-network"), // selfLink
            "this-network", // name
            null, // description
            "0.0.0.0/0", // rangeIPv4
            "1.2.3.4" // gatewayIPv4
      );

      Operation createOp = new ParseGlobalOperationTest().expected();

      Supplier<String> userProject = new Supplier<String>() {
         @Override
         public String get() {
            return "myproject";
         }
      };

      expect(api.getNetworkApi(userProject.get())).andReturn(nwApi).atLeastOnce();
      expect(api.getGlobalOperationApi(userProject.get())).andReturn(globalApi).atLeastOnce();

      expect(nwApi.createInIPv4RangeWithGateway("this-network", "0.0.0.0/0", "1.2.3.4")).andReturn(createOp);
      expect(globalApi.get(createOp.name())).andReturn(createOp);
      expect(nwApi.get("this-network")).andReturn(null);
      expect(nwApi.get("this-network")).andReturn(network);

      replay(api, nwApi, globalApi);

      NetworkAndAddressRange input = NetworkAndAddressRange.create("this-network", "0.0.0.0/0", "1.2.3.4");

      GlobalOperationDonePredicate pred = globalOperationDonePredicate(api, userProject);

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, userProject, pred, 100l, 100l);

      assertEquals(creator.apply(input), network);

      verify(api, nwApi, globalApi);
   }

   private GlobalOperationDonePredicate globalOperationDonePredicate(final GoogleComputeEngineApi api,
         final Supplier<String> userProject) {
      return Guice.createInjector(new AbstractModule() { // Rather than opening ctor public
         @Override protected void configure() {
            bind(GoogleComputeEngineApi.class).toInstance(api);
         }

         @Provides @UserProject Supplier<String> project() {
            return userProject;
         }
      }).getInstance(GlobalOperationDonePredicate.class);
   }
}
