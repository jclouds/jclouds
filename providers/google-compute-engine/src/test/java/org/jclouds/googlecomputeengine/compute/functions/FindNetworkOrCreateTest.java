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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

@Test
public class FindNetworkOrCreateTest {
   private static final String BASE_URL = "https://www.googleapis.com/compute/v1/projects";
   private static final Network NETWORK = Network.create( //
         "abcd", // id
         URI.create(BASE_URL + "/myproject/global/networks/this-network"), // selfLink
         "this-network", // name
         null, // description
         "0.0.0.0/0", // rangeIPv4
         null // gatewayIPv4
   );

   public void testLoadExisting() {
      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      NetworkApi nwApi = createMock(NetworkApi.class);

      Supplier<String> userProject = new Supplier<String>() {
         @Override
         public String get() {
            return "myproject";
         }
      };

      expect(api.getNetworkApi(userProject.get())).andReturn(nwApi).atLeastOnce();

      expect(nwApi.get("this-network")).andReturn(NETWORK);

      replay(api, nwApi);

      NetworkAndAddressRange input = NetworkAndAddressRange.create("this-network", "0.0.0.0/0", null);

      GlobalOperationDonePredicate pred = globalOperationDonePredicate(api, userProject);

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, userProject, pred, 100l, 100l);

      FindNetworkOrCreate loader = new FindNetworkOrCreate(api, creator, userProject);

      LoadingCache<NetworkAndAddressRange, Network> cache = CacheBuilder.newBuilder().build(loader);

      assertEquals(cache.getUnchecked(input), NETWORK);

      // Second call is to ensure we only need to make the API calls once.
      assertEquals(cache.getUnchecked(input), NETWORK);

      verify(api, nwApi);
   }

   public void testLoadNew() {
      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      NetworkApi nwApi = createMock(NetworkApi.class);
      GlobalOperationApi globalApi = createMock(GlobalOperationApi.class);

      Operation createOp = new ParseGlobalOperationTest().expected();

      Supplier<String> userProject = new Supplier<String>() {
         @Override
         public String get() {
            return "myproject";
         }
      };

      expect(api.getNetworkApi(userProject.get())).andReturn(nwApi).atLeastOnce();
      expect(api.getGlobalOperationApi(userProject.get())).andReturn(globalApi).atLeastOnce();

      expect(nwApi.createInIPv4Range("this-network", "0.0.0.0/0")).andReturn(createOp);
      expect(globalApi.get(createOp.name())).andReturn(createOp);
      // pre-creation
      expect(nwApi.get("this-network")).andReturn(null).times(2);
      // post-creation
      expect(nwApi.get("this-network")).andReturn(NETWORK);

      replay(api, nwApi, globalApi);

      NetworkAndAddressRange input = NetworkAndAddressRange.create("this-network", "0.0.0.0/0", null);

      GlobalOperationDonePredicate pred = globalOperationDonePredicate(api, userProject);

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, userProject, pred, 100l, 100l);

      FindNetworkOrCreate loader = new FindNetworkOrCreate(api, creator, userProject);

      LoadingCache<NetworkAndAddressRange, Network> cache = CacheBuilder.newBuilder().build(loader);

      assertEquals(cache.getUnchecked(input), NETWORK);

      // Second call is to ensure we only need to make the API calls once.
      assertEquals(cache.getUnchecked(input), NETWORK);

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

