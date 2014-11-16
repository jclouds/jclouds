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
package org.jclouds.s3.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Provides;

@Test
public class AssignCorrectHostnameForBucketTest {
   static final RegionToEndpointOrProviderIfNull REGION_TO_ENDPOINT = Guice.createInjector(new Module() {
      @Override public void configure(Binder binder) {
         binder.bindConstant().annotatedWith(Provider.class).to("s3");
      }

      @Provides @Provider Supplier<URI> defaultUri() {
         return Suppliers.ofInstance(URI.create("https://s3.amazonaws.com"));
      }

      @Provides @Region Supplier<Map<String, Supplier<URI>>> regionToEndpoints() {
         Map<String, Supplier<URI>> regionToEndpoint = ImmutableMap.of( //
               "us-standard", defaultUri(), //
               "us-west-1", Suppliers.ofInstance(URI.create("https://s3-us-west-1.amazonaws.com")));
         return Suppliers.ofInstance(regionToEndpoint);
      }
   }).getInstance(RegionToEndpointOrProviderIfNull.class);

   public void testWhenNoBucketRegionMappingInCache() {
      AssignCorrectHostnameForBucket fn = new AssignCorrectHostnameForBucket(REGION_TO_ENDPOINT,
            Functions.forMap(ImmutableMap.of("bucket", Optional.<String>absent())));

      assertEquals(fn.apply("bucket"), URI.create("https://s3.amazonaws.com"));
   }

   public void testWhenBucketRegionMappingInCache() {
      AssignCorrectHostnameForBucket fn = new AssignCorrectHostnameForBucket(REGION_TO_ENDPOINT,
            Functions.forMap(ImmutableMap.of("bucket", Optional.of("us-west-1"))));

      assertEquals(fn.apply("bucket"), URI.create("https://s3-us-west-1.amazonaws.com"));
   }
}
