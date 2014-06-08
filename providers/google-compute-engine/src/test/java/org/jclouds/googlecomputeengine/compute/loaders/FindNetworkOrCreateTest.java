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
package org.jclouds.googlecomputeengine.compute.loaders;

import static com.google.common.base.Optional.fromNullable;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.internal.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.features.GlobalOperationApi;
import org.jclouds.googlecomputeengine.features.NetworkApi;
import org.jclouds.googlecomputeengine.functions.CreateNetworkIfNeeded;
import org.jclouds.googlecomputeengine.predicates.GlobalOperationDonePredicate;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class FindNetworkOrCreateTest {

   @Test
   public void testLoadExisting() {
      final GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      final NetworkApi nwApi = createMock(NetworkApi.class);

      Network network = Network.builder().IPv4Range("0.0.0.0/0")
              .id("abcd").name("this-network")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/this-network"))
              .build();

      final Supplier<String> userProject = new Supplier<String>() {
         @Override
         public String get() {
            return "myproject";
         }
      };

      expect(api.getNetworkApiForProject(userProject.get())).andReturn(nwApi).atLeastOnce();

      expect(nwApi.get("this-network")).andReturn(network);

      replay(api, nwApi);

      NetworkAndAddressRange input = new NetworkAndAddressRange("this-network", "0.0.0.0/0", null);

      GlobalOperationDonePredicate pred = new GlobalOperationDonePredicate(api, userProject);

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, userProject, pred, 100l, 100l);

      FindNetworkOrCreate loader = new FindNetworkOrCreate(api, creator, userProject);

      LoadingCache<NetworkAndAddressRange, Network> cache = CacheBuilder.newBuilder().build(loader);

      assertEquals(cache.getUnchecked(input), network);

      // Second call is to ensure we only need to make the API calls once.
      assertEquals(cache.getUnchecked(input), network);

      verify(api, nwApi);
   }

   @Test
   public void testLoadNew() {
      final GoogleComputeEngineApi api = createMock(GoogleComputeEngineApi.class);
      final NetworkApi nwApi = createMock(NetworkApi.class);
      final GlobalOperationApi globalApi = createMock(GlobalOperationApi.class);

      Network network = Network.builder().IPv4Range("0.0.0.0/0")
              .id("abcd").name("this-network")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/this-network"))
              .build();

      Operation createOp = createMock(Operation.class);

      final Supplier<String> userProject = new Supplier<String>() {
         @Override
         public String get() {
            return "myproject";
         }
      };

      expect(api.getNetworkApiForProject(userProject.get())).andReturn(nwApi).atLeastOnce();
      expect(api.getGlobalOperationApiForProject(userProject.get())).andReturn(globalApi).atLeastOnce();

      expect(nwApi.createInIPv4Range("this-network", "0.0.0.0/0"))
              .andReturn(createOp);
      expect(globalApi.get("create-op")).andReturn(createOp);
      // pre-creation
      expect(nwApi.get("this-network")).andReturn(null).times(2);
      // post-creation
      expect(nwApi.get("this-network")).andReturn(network);

      expect(createOp.getName()).andReturn("create-op");
      expect(createOp.getStatus()).andReturn(Operation.Status.DONE);
      expect(createOp.getHttpError()).andReturn(fromNullable((HttpResponse)null));
      replay(api, nwApi, createOp, globalApi);

      NetworkAndAddressRange input = new NetworkAndAddressRange("this-network", "0.0.0.0/0", null);

      GlobalOperationDonePredicate pred = new GlobalOperationDonePredicate(api, userProject);

      CreateNetworkIfNeeded creator = new CreateNetworkIfNeeded(api, userProject, pred, 100l, 100l);

      FindNetworkOrCreate loader = new FindNetworkOrCreate(api, creator, userProject);

      LoadingCache<NetworkAndAddressRange, Network> cache = CacheBuilder.newBuilder().build(loader);

      assertEquals(cache.getUnchecked(input), network);

      // Second call is to ensure we only need to make the API calls once.
      assertEquals(cache.getUnchecked(input), network);

      verify(api, nwApi, globalApi, createOp);

   }
}

