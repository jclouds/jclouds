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

import javax.inject.Inject;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.domain.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.domain.Network;

import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;

public final class FindNetworkOrCreate extends CacheLoader<NetworkAndAddressRange, Network> {
   private final GoogleComputeEngineApi api;
   private final Function<NetworkAndAddressRange, Network> networkCreator;

   @Inject FindNetworkOrCreate(GoogleComputeEngineApi api, Function<NetworkAndAddressRange, Network> networkCreator) {
      this.api = api;
      this.networkCreator = networkCreator;
   }

   @Override public Network load(NetworkAndAddressRange in) {
      Network network = api.networks().get(in.name());
      if (network != null) {
         return network;
      } else {
         return networkCreator.apply(in);
      }
   }
}
