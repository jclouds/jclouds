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
package org.jclouds.azurecompute.arm.compute.functions;

import static com.google.common.collect.Iterables.getOnlyElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.Region;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * Converts an Location into a Location.
 */
@Singleton
public class LocationToLocation implements Function<Location, org.jclouds.domain.Location> {

   private final JustProvider justProvider;

   @Inject
   LocationToLocation(JustProvider justProvider) {
      this.justProvider = justProvider;
   }

   @Override
   public org.jclouds.domain.Location apply(final Location location) {
      final LocationBuilder builder = new LocationBuilder();
      builder.id(location.id());
      builder.description(location.displayName());
      builder.parent(getOnlyElement(justProvider.get()));

      builder.scope(LocationScope.REGION);
      final Region region = Region.byName(location.name());
      if (region != null) {
         builder.iso3166Codes(ImmutableSet.of(region.iso3166Code()));
      }

      return builder.build();
   }

}
