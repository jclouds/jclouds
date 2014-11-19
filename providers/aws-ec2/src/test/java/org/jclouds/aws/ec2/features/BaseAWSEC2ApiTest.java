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
package org.jclouds.aws.ec2.features;

import static com.google.common.collect.Maps.transformValues;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.AWSEC2ProviderMetadata;
import org.jclouds.aws.ec2.config.AWSEC2HttpApiModule;
import org.jclouds.aws.filters.FormSignerV4;
import org.jclouds.aws.filters.FormSignerV4.ServiceAndRegion;
import org.jclouds.compute.domain.Image;
import org.jclouds.date.DateService;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.http.HttpRequest;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.internal.BaseRestAnnotationProcessingTest;
import org.jclouds.util.Suppliers2;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Module;
import com.google.inject.Provides;

@Test(groups = "unit")
public abstract class BaseAWSEC2ApiTest<T> extends BaseRestAnnotationProcessingTest<T> {

   @ConfiguresHttpApi
   protected static class StubAWSEC2HttpApiModule extends AWSEC2HttpApiModule {

      @Override
      protected String provideTimeStamp(DateService dateService) {
         return "20120416T155408Z";
      }

      @Provides
      @Singleton
      LoadingCache<RegionAndName, Image> provide() {
         return CacheBuilder.newBuilder().build(new CacheLoader<RegionAndName, Image>() {

            @Override
            public Image load(RegionAndName key) throws Exception {
               return null;
            }

         });
      }

      @Override
      protected void installLocations() {
         install(new LocationModule());
         bind(RegionIdToURISupplier.class).toInstance(new RegionIdToURISupplier() {

            @Override
            public Map<String, Supplier<URI>> get() {
               return transformValues(ImmutableMap
                     .<String, URI>of(Region.EU_WEST_1, URI.create("https://ec2.eu-west-1.amazonaws.com"),
                           Region.US_EAST_1, URI.create("https://ec2.us-east-1.amazonaws.com"), Region.US_WEST_1,
                           URI.create("https://ec2.us-west-1.amazonaws.com")), Suppliers2.<URI>ofInstanceFunction());
            }

         });
         bind(RegionIdToZoneIdsSupplier.class).toInstance(new RegionIdToZoneIdsSupplier() {

            @Override
            public Map<String, Supplier<Set<String>>> get() {
               return transformValues(ImmutableMap.<String, Set<String>> of("us-east-1", ImmutableSet.of("us-east-1a",
                        "us-east-1b", "us-east-1c", "us-east-1b")), Suppliers2.<Set<String>> ofInstanceFunction());
            }

         });
      }

      @Provides ServiceAndRegion ServiceAndRegion(){
         return new ServiceAndRegion() {
            @Override public String service() {
               return "ec2";
            }

            @Override public String region(String host) {
               return "us-east-1";
            }
         };
      }
   }

   @Override protected void assertNonPayloadHeadersEqual(HttpRequest request, String toMatch) {
      Multimap<String, String> headersToCheck = LinkedHashMultimap.create();
      for (String key : request.getHeaders().keySet()) {
         if (key.equals("X-Amz-Date")) {
            assertEquals(request.getFirstHeaderOrNull(key), "20120416T155408Z");
         } else if (key.equals("Authorization")) {
            assertThat(request.getFirstHeaderOrNull(AUTHORIZATION)).startsWith(
                  "AWS4-HMAC-SHA256 Credential=identity/20120416/"
                        + "us-east-1/ec2/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=");
         } else {
            headersToCheck.putAll(key, request.getHeaders().get(key));
         }
      }
      assertEquals(sortAndConcatHeadersIntoString(headersToCheck), toMatch);
   }

   protected FormSignerV4 filter;

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertTrue(request.getFilters().get(0) instanceof FormSignerV4);
   }

   @Override
   @BeforeTest
   protected void setupFactory() throws IOException {
      super.setupFactory();
      this.filter = injector.getInstance(FormSignerV4.class);
   }

   @Override
   protected Module createModule() {
      return new StubAWSEC2HttpApiModule();
   }

   protected String provider = "aws-ec2";

   @Override
   public AWSEC2ProviderMetadata createProviderMetadata() {
      return new AWSEC2ProviderMetadata();
   }

}
