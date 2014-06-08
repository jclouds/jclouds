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
package org.jclouds.googlecomputeengine.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.collect.Memoized;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Region;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Inject;

/**
 * Tests that a Global Operation is done, returning the completed Operation when it is.
 */
public class RegionOperationDonePredicate implements Predicate<AtomicReference<Operation>> {

   private final GoogleComputeEngineApi api;
   private final Supplier<String> project;
   private final Supplier<Map<URI, Region>> regions;

   @Inject
   RegionOperationDonePredicate(GoogleComputeEngineApi api, @UserProject Supplier<String> project,
                                @Memoized Supplier<Map<URI, Region>> regions) {
      this.api = api;
      this.project = project;
      this.regions = regions;
   }

   @Override
   public boolean apply(AtomicReference<Operation> input) {
      checkNotNull(input, "input");

      Operation current = api.getRegionOperationApiForProject(project.get())
              .getInRegion(regions.get().get(input.get().getRegion().get()).getName(),
              input.get().getName());
      switch (current.getStatus()) {
         case DONE:
            input.set(current);
            return true;
         case PENDING:
         case RUNNING:
         default:
            return false;
      }
   }
}
