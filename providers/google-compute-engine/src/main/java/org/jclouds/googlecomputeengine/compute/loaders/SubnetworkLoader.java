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

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.domain.internal.RegionAndName;
import org.jclouds.googlecomputeengine.domain.Subnetwork;

import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;

@Singleton
public class SubnetworkLoader extends CacheLoader<RegionAndName, Optional<Subnetwork>> {

   private final GoogleComputeEngineApi api;

   @Inject
   SubnetworkLoader(GoogleComputeEngineApi api) {
      this.api = api;
   }

   @Override
   public Optional<Subnetwork> load(RegionAndName key) throws ExecutionException {
      try {
         return Optional.fromNullable(api.subnetworksInRegion(key.regionId()).get(key.name()));
      } catch (Exception ex) {
         throw new ExecutionException(message(key, ex), ex);
      }
   }

   public static String message(RegionAndName key, Exception ex) {
      return String.format("could not find subnet %s in region %s: %s", key.name(), key.regionId(), ex.getMessage());
   }
}
