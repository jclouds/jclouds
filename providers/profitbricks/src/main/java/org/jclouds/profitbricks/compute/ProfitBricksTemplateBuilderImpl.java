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
package org.jclouds.profitbricks.compute;

import static com.google.common.collect.Iterables.find;
import static java.lang.String.format;
import static org.jclouds.domain.LocationScope.ZONE;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

import org.jclouds.compute.domain.Image;

public class ProfitBricksTemplateBuilderImpl extends TemplateBuilderImpl {

   private final Function<org.jclouds.profitbricks.domain.Location, Location> fnLocation;

   @Inject
   ProfitBricksTemplateBuilderImpl(@Memoized Supplier<Set<? extends Location>> locations,
         @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> hardwares,
         Supplier<Location> defaultLocation, @Named("DEFAULT") Provider<TemplateOptions> optionsProvider,
         @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider,
         Function<org.jclouds.profitbricks.domain.Location, Location> fnLocation) {
      super(locations, images, hardwares, defaultLocation, optionsProvider, defaultTemplateProvider);
      this.fnLocation = fnLocation;
   }

   @Override
   public TemplateBuilder locationId(final String locationId) {
      org.jclouds.profitbricks.domain.Location nativeLocation
              = org.jclouds.profitbricks.domain.Location.fromId(locationId);

      Set<? extends Location> dataCenters = this.locations.get();
      if (nativeLocation != org.jclouds.profitbricks.domain.Location.UNRECOGNIZED)
         try {
            // look for a child location instead if provided id is a Region
            final Location parentLocation = fnLocation.apply(nativeLocation);
            this.location = find(dataCenters, new Predicate<Location>() {

               @Override
               public boolean apply(Location input) {
                  return parentLocation.equals(input.getParent());
               }

               @Override
               public String toString() {
                  return "first datacenter in locationId(" + locationId + ")";
               }

            });
         } catch (NoSuchElementException ex) {
            throw new NoSuchElementException(
                    format("no child location found for location id %s in: %s", locationId, locations));
         }
      else
         super.locationId(locationId);
      return this;
   }

   @Override
   public Template build() {
      Template template = super.build();

      Location loc = template.getLocation();
      if (loc != null && loc.getScope() != ZONE)
         return fromTemplate(template).locationId(loc.getId()).build();

      return template;
   }

}
