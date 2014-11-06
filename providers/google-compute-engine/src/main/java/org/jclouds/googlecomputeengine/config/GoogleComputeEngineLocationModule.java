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
package org.jclouds.googlecomputeengine.config;

import static com.google.common.base.Suppliers.compose;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.googlecomputeengine.internal.ListPages.concat;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Region;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.location.reference.LocationConstants;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.location.suppliers.RegionIdsSupplier;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.location.suppliers.all.ZoneToRegionToProviderOrJustProvider;
import org.jclouds.location.suppliers.derived.RegionIdsFromRegionIdToURIKeySet;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.location.suppliers.implicit.FirstZone;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Provides;

/**
 * This configures dynamic locations from {@link org.jclouds.googlecomputeengine.features.RegionApi#list}. As the only
 * assignable location for nodes are zones, this module does not directly expose regions. Rather, they can be found by
 * looking at {@link Location#getParent()} on a zone.

 * <p/> This does not yet support constraining region or zone lists via settings {@linkplain
 * LocationConstants#PROPERTY_REGIONS} or {@linkplain LocationConstants#PROPERTY_ZONES}.
 */
public final class GoogleComputeEngineLocationModule extends LocationModule {

   @Override protected void configure() {
      super.configure();
      // Unlike EC2, you cannot default GCE instances to a region. Hence, we constrain to zones.
      bind(LocationsSupplier.class).to(OnlyZonesLocationSupplier.class);
      bind(ImplicitLocationSupplier.class).to(FirstZone.class);

      // Region and zones are derived from the same network request to RegionApi.list
      // Using these suppliers will make that consistent and also cache timeout consistently
      bind(RegionIdToZoneIdsSupplier.class).to(RegionIdToZoneIdsFromRegionList.class);
      bind(RegionIdToURISupplier.class).to(RegionIdToURISupplierFromRegionList.class);
      bind(ZoneIdToURISupplier.class).to(ZoneIdToURIFromRegionList.class);
      bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class);
      bind(RegionIdsSupplier.class).to(RegionIdsFromRegionIdToURIKeySet.class);
   }

   /** Retain the metadata tree, including regions, just don't present anything except zones as assignable. */
   static final class OnlyZonesLocationSupplier implements LocationsSupplier {
      // This correctly links parents for zone -> region -> provider.
      private final ZoneToRegionToProviderOrJustProvider delegate;

      @Inject OnlyZonesLocationSupplier(ZoneToRegionToProviderOrJustProvider delegate) {
         this.delegate = delegate;
      }

      @Override public Set<? extends Location> get() {
         return Sets.filter(delegate.get(), LocationPredicates.isZone());
      }
   }

   /**
    * Since this is caching a direct api call, we memoize, but short-circuit on any auth exception. This prevents
    * excessive errors when things occur in parallel, or as peers on a function graph.
    */
   @Provides @Singleton @Memoized Supplier<List<Region>> regions(@UserProject Supplier<String> project,
         final GoogleComputeEngineApi api, AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier
            .create(authException, compose(new Function<String, List<Region>>() {
               public List<Region> apply(String project) {
                  return ImmutableList.copyOf(concat(api.getRegionApi(project).list()));
               }
            }, project), seconds, TimeUnit.SECONDS);
   }

   @Provides @Singleton @Memoized Supplier<Map<URI, String>> selfLinkToNames(
         AtomicReference<AuthorizationException> authException, @Memoized Supplier<List<Region>> regions,
         @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier
            .create(authException, compose(new Function<List<Region>, Map<URI, String>>() {
               public Map<URI, String> apply(List<Region> regions) {
                  ImmutableMap.Builder<URI, String> selfLinkToName = ImmutableMap.builder();
                  for (Region region : regions) {
                     selfLinkToName.put(region.selfLink(), region.name());
                     for (URI zoneSelfLink : region.zones()) {
                        selfLinkToName.put(zoneSelfLink, toName(zoneSelfLink));
                     }
                  }
                  return selfLinkToName.build();
               }
            }, regions), seconds, TimeUnit.SECONDS);
   }

   static final class RegionIdToZoneIdsFromRegionList implements RegionIdToZoneIdsSupplier {
      private final Supplier<List<Region>> regions;

      @Inject RegionIdToZoneIdsFromRegionList(@Memoized Supplier<List<Region>> regions) {
         this.regions = regions;
      }

      @Override public Map<String, Supplier<Set<String>>> get() {
         ImmutableMap.Builder<String, Supplier<Set<String>>> result = ImmutableMap.builder();
         for (org.jclouds.googlecomputeengine.domain.Region region : regions.get()) {
            ImmutableSet.Builder<String> zoneIds = ImmutableSet.builder();
            for (URI uri : region.zones()) {
               zoneIds.add(toName(uri));
            }
            result.put(region.name(), Suppliers.<Set<String>>ofInstance(zoneIds.build()));
         }
         return result.build();
      }
   }

   static final class RegionIdToURISupplierFromRegionList implements RegionIdToURISupplier {
      private final Supplier<List<Region>> regions;

      @Inject RegionIdToURISupplierFromRegionList(@Memoized Supplier<List<Region>> regions) {
         this.regions = regions;
      }

      @Override public Map<String, Supplier<URI>> get() {
         ImmutableMap.Builder<String, Supplier<URI>> result = ImmutableMap.builder();
         for (org.jclouds.googlecomputeengine.domain.Region region : regions.get()) {
            result.put(region.name(), Suppliers.ofInstance(region.selfLink()));
         }
         return result.build();
      }
   }

   static final class ZoneIdToURIFromRegionList implements ZoneIdToURISupplier {
      private final Supplier<List<Region>> regions;

      @Inject ZoneIdToURIFromRegionList(@Memoized Supplier<List<Region>> regions) {
         this.regions = regions;
      }

      @Override public Map<String, Supplier<URI>> get() {
         ImmutableMap.Builder<String, Supplier<URI>> result = ImmutableMap.builder();
         for (Region region : regions.get()) {
            for (URI input : region.zones()) {
               result.put(toName(input), Suppliers.ofInstance(input));
            }
         }
         return result.build();
      }
   }

   private static String toName(URI link) {
      String path = link.getPath();
      return path.substring(path.lastIndexOf('/') + 1);
   }
}
