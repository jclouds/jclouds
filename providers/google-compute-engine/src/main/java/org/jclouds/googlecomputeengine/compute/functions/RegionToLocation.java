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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.GOOGLE_PROVIDER_LOCATION;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.googlecomputeengine.domain.Region;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * Transforms a google compute domain specific region to a generic Region object.
 */
public class RegionToLocation implements Function<Region, Location> {

   @Override
   public Location apply(Region input) {
      return new LocationBuilder()
              .description(input.getDescription().orNull())
              .metadata(ImmutableMap.of("selfLink", (Object) checkNotNull(input.getSelfLink(), "region URI")))
              .id(input.getName())
              .scope(LocationScope.REGION)
              .parent(GOOGLE_PROVIDER_LOCATION)
              .build();
   }
}
