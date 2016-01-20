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
package org.jclouds.digitalocean2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Transforms an {@link Region} to the jclouds portable model.
 */
@Singleton
public class RegionToLocation implements Function<Region, Location> {

   private final JustProvider justProvider;

   @Inject
   RegionToLocation(JustProvider justProvider) {
      this.justProvider = checkNotNull(justProvider, "justProvider cannot be null");
   }

   @Override
   public Location apply(Region input) {
      LocationBuilder builder = new LocationBuilder();
      builder.id(input.slug());
      builder.description(input.name());
      builder.scope(LocationScope.REGION);
      builder.parent(getOnlyElement(justProvider.get()));
      builder.iso3166Codes(ImmutableSet.<String> of());
      builder.metadata(ImmutableMap.<String, Object> of("available", input.available(), "features", input.features()));
      return builder.build();
   }
}
