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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.HashSet;
import java.util.List;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathRule;
import org.jclouds.googlecomputeengine.domain.UrlMapValidateResult;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class UrlMapApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String URL_MAP_NAME = "url-map-api-live-test-url-map";
   public static final String URL_MAP_BACKEND_SERVICE_NAME = "url-map-api-live-test-backend-service";
   public static final String HEALTH_CHECK_NAME = "backend-service-api-live-test-health-check";
   public static final int TIME_WAIT = 30;

   private UrlMapApi api() {
      return api.getUrlMapApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertUrlMap() {
      // Create extra resources needed for url maps
      // TODO: (ashmrtnz) create health check once it is merged into project
      HashSet<URI> healthChecks = new HashSet<URI>();
      healthChecks.add(getHealthCheckUrl(userProject.get(), HEALTH_CHECK_NAME));
      BackendServiceOptions b = new BackendServiceOptions().name(URL_MAP_BACKEND_SERVICE_NAME)
                                                           .healthChecks(healthChecks);
      assertGlobalOperationDoneSucessfully(api.getBackendServiceApiForProject(userProject.get())
                                              .create(URL_MAP_BACKEND_SERVICE_NAME, b), TIME_WAIT);
      
      UrlMapOptions map = new UrlMapOptions().name(URL_MAP_NAME).description("simple url map")
                                             .defaultService(getBackendServiceUrl(userProject.get(),
                                                                                  URL_MAP_BACKEND_SERVICE_NAME));
      assertGlobalOperationDoneSucessfully(api().create(URL_MAP_NAME, map), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertUrlMap")
   public void testGetUrlMap() {

      UrlMap urlMap = api().get(URL_MAP_NAME);
      assertNotNull(urlMap);
      assertUrlMapEquals(urlMap);
   }

   @Test(groups = "live", dependsOnMethods = "testGetUrlMap")
   public void testListUrlMap() {

      PagedIterable<UrlMap> urlMaps = api().list(new ListOptions.Builder()
              .filter("name eq " + URL_MAP_NAME));

      List<UrlMap> urlMapsAsList = Lists.newArrayList(urlMaps.concat());

      assertEquals(urlMapsAsList.size(), 1);

      assertUrlMapEquals(Iterables.getOnlyElement(urlMapsAsList));

   }
   
   @Test(groups = "live", dependsOnMethods = "testGetUrlMap")
   public void testUpdateUrlMap() {
      String fingerprint = api().get(URL_MAP_NAME).getFingerprint().get();
      URI service = getBackendServiceUrl(userProject.get(), URL_MAP_BACKEND_SERVICE_NAME);
      ImmutableSet<String> path = ImmutableSet.<String>of("/");
      PathRule rule = PathRule.builder().service(service).paths(path).build();
      ImmutableSet<PathRule> rules = ImmutableSet.<PathRule>of(rule);
      ImmutableSet<PathMatcher> matchers = ImmutableSet.<PathMatcher>of(PathMatcher.builder().defaultService(service)
                                                                                             .name("path")
                                                                                             .pathRules(rules)
                                                                                             .build());
      ImmutableSet<String> hosts = ImmutableSet.<String>of("jclouds-test");
      ImmutableSet<HostRule> hostRules = ImmutableSet.<HostRule>of(HostRule.builder().hosts(hosts)
                                                                                     .pathMatcher("path")
                                                                                     .build());
      UrlMapOptions options = new UrlMapOptions().name(URL_MAP_NAME)
                                                 .pathMatchers(matchers)
                                                 .hostRules(hostRules)
                                                 .defaultService(service)
                                                 .fingerprint(fingerprint);
      assertGlobalOperationDoneSucessfully(api().update(URL_MAP_NAME, options), TIME_WAIT);
      
      assertUrlMapEquals(api().get(URL_MAP_NAME), options);
   }
   
   @Test(groups = "live", dependsOnMethods = "testUpdateUrlMap")
   public void testPatchUrlMap() {
      String fingerprint = api().get(URL_MAP_NAME).getFingerprint().get();
      URI service = getBackendServiceUrl(userProject.get(), URL_MAP_BACKEND_SERVICE_NAME);
      ImmutableSet<UrlMap.UrlMapTest> urlMapTests = ImmutableSet.<UrlMap.UrlMapTest>of(UrlMap.UrlMapTest.builder()
            .host("jclouds-test")
            .path("/test/path")
            .service(service)
            .build());
      UrlMapOptions options = new UrlMapOptions().urlMapTests(urlMapTests)
                                                 .fingerprint(fingerprint);
      assertGlobalOperationDoneSucessfully(api().patch(URL_MAP_NAME, options), TIME_WAIT);
      
      // Update options with settings it should have for later assertions.
      ImmutableSet<String> path = ImmutableSet.<String>of("/");
      PathRule rule = PathRule.builder().service(service).paths(path).build();
      ImmutableSet<PathRule> rules = ImmutableSet.<PathRule>of(rule);
      ImmutableSet<PathMatcher> matchers = ImmutableSet.<PathMatcher>of(PathMatcher.builder().defaultService(service)
                                                                                             .name("path")
                                                                                             .pathRules(rules)
                                                                                             .build());
      ImmutableSet<String> hosts = ImmutableSet.<String>of("jclouds-test");
      ImmutableSet<HostRule> hostRules = ImmutableSet.<HostRule>of(HostRule.builder().hosts(hosts)
                                                                                     .pathMatcher("path")
                                                                                     .build());
      options.name(URL_MAP_NAME)
             .description("simple url map")
             .pathMatchers(matchers)
             .hostRules(hostRules)
             .defaultService(service);
      assertUrlMapEquals(api().get(URL_MAP_NAME), options);
   }
   
   @Test(groups = "live", dependsOnMethods = "testPatchUrlMap")
   public void testValidateUrlMap() {
      UrlMapValidateResult results = api().validate(URL_MAP_NAME, api().get(URL_MAP_NAME));
      UrlMapValidateResult expected = UrlMapValidateResult.builder().testPassed(true).loadSucceeded(true).build();
      assertEquals(results, expected);
   }
   
   @Test(groups = "live", dependsOnMethods = "testPatchUrlMap")
   public void testValidateUrlMapWithOptions() {
      UrlMapOptions options = new UrlMapOptions();
      
      URI service = getBackendServiceUrl(userProject.get(), URL_MAP_BACKEND_SERVICE_NAME);
      ImmutableSet<UrlMap.UrlMapTest> urlMapTests = ImmutableSet.<UrlMap.UrlMapTest>of(UrlMap.UrlMapTest.builder()
            .host("jclouds-test")
            .path("/test/path")
            .service(service)
            .build());
      ImmutableSet<String> path = ImmutableSet.<String>of("/");
      PathRule rule = PathRule.builder().service(service).paths(path).build();
      ImmutableSet<PathRule> rules = ImmutableSet.<PathRule>of(rule);
      ImmutableSet<PathMatcher> matchers = ImmutableSet.<PathMatcher>of(PathMatcher.builder().defaultService(service)
                                                                                             .name("path")
                                                                                             .pathRules(rules)
                                                                                             .build());
      ImmutableSet<String> hosts = ImmutableSet.<String>of("jclouds-test");
      ImmutableSet<HostRule> hostRules = ImmutableSet.<HostRule>of(HostRule.builder().hosts(hosts)
                                                                                     .pathMatcher("path")
                                                                                     .build());
      options.pathMatchers(matchers)
             .name(URL_MAP_NAME)
             .hostRules(hostRules)
             .urlMapTests(urlMapTests)
             .defaultService(service)
             .description("simple url map");
      
      UrlMapValidateResult results = api().validate(URL_MAP_NAME, options);
      UrlMapValidateResult expected = UrlMapValidateResult.builder().testPassed(true).loadSucceeded(true).build();
      assertEquals(results, expected);
   }

   @Test(groups = "live", dependsOnMethods = "testValidateUrlMapWithOptions")
   public void testDeleteUrlMap() {

      assertGlobalOperationDoneSucessfully(api().delete(URL_MAP_NAME), TIME_WAIT);
      
      // remove extra resources created
      assertGlobalOperationDoneSucessfully(api.getBackendServiceApiForProject(userProject.get())
                                              .delete(URL_MAP_BACKEND_SERVICE_NAME), TIME_WAIT);
      // TODO: delete health check once it is merged
   }

   private void assertUrlMapEquals(UrlMap result) {
      assertEquals(result.getName(), URL_MAP_NAME);
      assertEquals(result.getDefaultService(), getBackendServiceUrl(userProject.get(),
                                                                        URL_MAP_BACKEND_SERVICE_NAME)); 
      assertEquals(result.getDescription().get(), "simple url map");
   }

   private void assertUrlMapEquals(UrlMap result, UrlMapOptions expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getDefaultService(), expected.getDefaultService()); 
      assertEquals(result.getPathMatchers(), expected.getPathMatchers());
      assertEquals(result.getHostRules(), expected.getHostRules());
   }
}
