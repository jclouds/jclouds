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
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.domain.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.compute.predicates.AtomicOperationDone;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.NetworkApi;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

@Test
public class FindNetworkOrCreateTest {
   private static final String BASE_URL = "https://www.googleapis.com/compute/v1/projects";
   private static final Network NETWORK = Network.create( //
         "abcd", // id
         URI.create(BASE_URL + "/party/global/networks/this-network"), // selfLink
         "this-network", // name
         null, // description
         "0.0.0.0/0", // rangeIPv4
         null // gatewayIPv4
   );

   public void testLoadExisting() {
      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      NetworkApi nwApi = createMock(NetworkApi.class);

      expect(api.networks()).andReturn(nwApi).atLeastOnce();

      expect(nwApi.get("this-network")).andReturn(NETWORK);

      replay(api, nwApi);

      NetworkAndAddressRange input = NetworkAndAddressRange.create("this-network", "0.0.0.0/0", null);

      Predicate<AtomicReference<Operation>> operationDone = Predicates.alwaysFalse(); // No op should be created!

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, operationDone);

      FindNetworkOrCreate loader = new FindNetworkOrCreate(api, creator);

      LoadingCache<NetworkAndAddressRange, Network> cache = CacheBuilder.newBuilder().build(loader);

      assertEquals(cache.getUnchecked(input), NETWORK);

      // Second call is to ensure we only need to make the API calls once.
      assertEquals(cache.getUnchecked(input), NETWORK);

      verify(api, nwApi);
   }

   public void testLoadNew() {
      GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      NetworkApi nwApi = createMock(NetworkApi.class);
      Resources resources = createMock(Resources.class);

      Operation createOp = new ParseGlobalOperationTest().expected();

      expect(api.networks()).andReturn(nwApi).atLeastOnce();

      expect(nwApi.createInIPv4Range("this-network", "0.0.0.0/0")).andReturn(createOp);
      expect(resources.operation(createOp.selfLink())).andReturn(createOp);
      // pre-creation
      expect(nwApi.get("this-network")).andReturn(null).times(2);
      // post-creation
      expect(nwApi.get("this-network")).andReturn(NETWORK);

      replay(api, nwApi, resources);

      NetworkAndAddressRange input = NetworkAndAddressRange.create("this-network", "0.0.0.0/0", null);

      AtomicOperationDone pred = atomicOperationDone(resources);

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, pred);

      FindNetworkOrCreate loader = new FindNetworkOrCreate(api, creator);

      LoadingCache<NetworkAndAddressRange, Network> cache = CacheBuilder.newBuilder().build(loader);

      assertEquals(cache.getUnchecked(input), NETWORK);

      // Second call is to ensure we only need to make the API calls once.
      assertEquals(cache.getUnchecked(input), NETWORK);

      verify(api, nwApi, resources);
   }

   private AtomicOperationDone atomicOperationDone(final Resources resources) {
      return Guice.createInjector(new AbstractModule() { // Rather than opening ctor public
         @Override protected void configure() {
            bind(Resources.class).toInstance(resources);
         }
      }).getInstance(AtomicOperationDone.class);
   }
}

