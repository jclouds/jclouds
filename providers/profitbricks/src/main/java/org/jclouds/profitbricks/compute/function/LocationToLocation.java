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
package org.jclouds.profitbricks.compute.function;

import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.profitbricks.domain.Location;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class LocationToLocation implements Function<Location, org.jclouds.domain.Location> {

   private final JustProvider justProvider;

   @Inject
   LocationToLocation(JustProvider justProvider) {
      this.justProvider = justProvider;
   }

   @Override
   public org.jclouds.domain.Location apply(Location in) {
      return new LocationBuilder()
              .id(in.getId())
              .description(in.getDescription())
              .scope(LocationScope.REGION)
              .parent(Iterables.getOnlyElement(justProvider.get()))
              .build();
   }

}
