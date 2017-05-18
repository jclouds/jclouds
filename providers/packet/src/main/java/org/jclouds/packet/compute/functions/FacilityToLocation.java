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
package org.jclouds.packet.compute.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.packet.domain.Facility;

import com.google.common.base.Function;

import static com.google.common.collect.Iterables.getOnlyElement;

/**
 * Transforms an {@link Facility} to the jclouds portable model.
 */
@Singleton
public class FacilityToLocation implements Function<Facility, Location> {

    private final JustProvider justProvider;

    // allow us to lazy discover the provider of a resource
    @Inject
    FacilityToLocation(JustProvider justProvider) {
        this.justProvider = justProvider;
    }

    @Override
    public Location apply(final Facility facility) {
        final LocationBuilder builder = new LocationBuilder();
        builder.id(facility.code());
        builder.description(facility.name());
        builder.parent(getOnlyElement(justProvider.get()));
        builder.scope(LocationScope.REGION);
        return builder.build();
    }
}
