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

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher.PathRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.UrlMapTest;
import org.jclouds.googlecomputeengine.domain.UrlMapValidateResult;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class UrlMapApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String URL_MAP_NAME = "url-map-api-live-test-url-map";
   public static final String URL_MAP_BACKEND_SERVICE_NAME = "url-map-api-live-test-backend-service";
   public static final String HEALTH_CHECK_NAME = "url-map-api-live-test-health-check";

   private UrlMapApi api() {
      return api.urlMaps();
   }

   @Test(groups = "live")
   public void testInsertUrlMap() {
      // Create extra resources needed for url maps
      assertOperationDoneSuccessfully(api.httpHeathChecks().insert(HEALTH_CHECK_NAME));

      List<URI> healthChecks = ImmutableList.of(getHealthCheckUrl(HEALTH_CHECK_NAME));
      BackendServiceOptions b = new BackendServiceOptions().name(URL_MAP_BACKEND_SERVICE_NAME)
                                                           .healthChecks(healthChecks);
      assertOperationDoneSuccessfully(api.backendServices().create(b));

      UrlMapOptions map = new UrlMapOptions.Builder().name(URL_MAP_NAME).description("simple url map")
                                             .defaultService(getBackendServiceUrl(URL_MAP_BACKEND_SERVICE_NAME)).build();

      assertOperationDoneSuccessfully(api().create(map));

   }

   @Test(groups = "live", dependsOnMethods = "testInsertUrlMap")
   public void testGetUrlMap() {

      UrlMap urlMap = api().get(URL_MAP_NAME);
      assertNotNull(urlMap);
      assertUrlMapEquals(urlMap);
   }

   @Test(groups = "live", dependsOnMethods = "testGetUrlMap")
   public void testListUrlMap() {

      ListPage<UrlMap> urlMaps = api().list(filter("name eq " + URL_MAP_NAME)).next();

      assertEquals(urlMaps.size(), 1);

      assertUrlMapEquals(Iterables.getOnlyElement(urlMaps));

   }

   @Test(groups = "live", dependsOnMethods = "testGetUrlMap")
   public void testUpdateUrlMap() {
      String fingerprint = api().get(URL_MAP_NAME).fingerprint();

      URI service = getBackendServiceUrl(URL_MAP_BACKEND_SERVICE_NAME);

      ImmutableList<String> paths = ImmutableList.<String>of("/");

      PathRule rule = PathRule.create(paths, service);

      ImmutableList<PathRule> rules = ImmutableList.<PathRule>of(rule);

      ImmutableList<PathMatcher> matchers = ImmutableList.<PathMatcher>of(PathMatcher.create("test-path-matcher", "", service, rules));

      ImmutableList<String> hosts = ImmutableList.<String>of("jclouds-test");
      ImmutableList<HostRule> hostRules = ImmutableList.<HostRule>of(HostRule.create("", hosts, "test-path-matcher"));
      UrlMapOptions options = new UrlMapOptions.Builder().name(URL_MAP_NAME)
                                                 .pathMatchers(matchers)
                                                 .hostRules(hostRules)
                                                 .defaultService(service)
                                                 .fingerprint(fingerprint).build();

      assertOperationDoneSuccessfully(api().update(URL_MAP_NAME, options));

      assertUrlMapEquals(api().get(URL_MAP_NAME), options);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateUrlMap")
   public void testPatchUrlMap() {
      String fingerprint = api().get(URL_MAP_NAME).fingerprint();
      URI service = getBackendServiceUrl(URL_MAP_BACKEND_SERVICE_NAME);

      UrlMapTest urlMapTest = UrlMapTest.create(null, "jclouds-test", "/test/path", service);
      ImmutableList<UrlMap.UrlMapTest> urlMapTests = ImmutableList.<UrlMap.UrlMapTest>of(urlMapTest);

      UrlMapOptions options = new UrlMapOptions.Builder().tests(urlMapTests)
                                                 .fingerprint(fingerprint).buildForPatch();
      assertOperationDoneSuccessfully(api().patch(URL_MAP_NAME, options));

      // Update options with settings it should have for later assertions.
      ImmutableList<String> paths = ImmutableList.<String>of("/");
      PathRule rule = PathRule.create(paths, service);

      ImmutableList<PathRule> rules = ImmutableList.<PathRule>of(rule);
      ImmutableList<PathMatcher> matchers = ImmutableList.<PathMatcher>of(PathMatcher.create("test-path-matcher", "", service, rules));

      ImmutableList<String> hosts = ImmutableList.<String>of("jclouds-test");
      ImmutableList<HostRule> hostRules = ImmutableList.<HostRule>of(HostRule.create("", hosts, "test-path-matcher"));

      options = new UrlMapOptions.Builder().name(URL_MAP_NAME)
             .description("simple url map")
             .pathMatchers(matchers)
             .hostRules(hostRules)
             .defaultService(service)
             .tests(urlMapTests)
             .fingerprint(fingerprint)
             .build();

      assertUrlMapEquals(api().get(URL_MAP_NAME), options);
   }

   @Test(groups = "live", dependsOnMethods = "testPatchUrlMap")
   public void testValidateUrlMap() {
      UrlMapValidateResult results = api().validate(URL_MAP_NAME, api().get(URL_MAP_NAME));
      UrlMapValidateResult expected = UrlMapValidateResult.allPass();
      assertEquals(results, expected);
   }

   @Test(groups = "live", dependsOnMethods = "testPatchUrlMap")
   public void testValidateUrlMapWithOptions() {
      URI service = getBackendServiceUrl(URL_MAP_BACKEND_SERVICE_NAME);
      ImmutableList<String> paths = ImmutableList.<String>of("/");
      PathRule rule = PathRule.create(paths, service);
      ImmutableList<PathRule> rules = ImmutableList.<PathRule>of(rule);

      ImmutableList<PathMatcher> matchers = ImmutableList.<PathMatcher>of(PathMatcher.create("test-path-matcher", "", service, rules));

      ImmutableList<String> hosts = ImmutableList.<String>of("jclouds-test");

      ImmutableList<HostRule> hostRules = ImmutableList.<HostRule>of(HostRule.create("", hosts, "test-path-matcher"));
      UrlMapTest urlMapTest = UrlMapTest.create(null, "jclouds-test", "/test/path", service);
      ImmutableList<UrlMap.UrlMapTest> urlMapTests = ImmutableList.<UrlMap.UrlMapTest>of(urlMapTest);

      UrlMapOptions options = new UrlMapOptions.Builder()
             .pathMatchers(matchers)
             .name(URL_MAP_NAME)
             .hostRules(hostRules)
             .tests(urlMapTests)
             .defaultService(service)
             .description("simple url map")
             .build();

      UrlMapValidateResult results = api().validate(URL_MAP_NAME, options);
      UrlMapValidateResult expected = UrlMapValidateResult.allPass();
      assertEquals(results, expected);
   }

   @Test(groups = "live", dependsOnMethods = "testValidateUrlMapWithOptions", alwaysRun = true)
   public void testDeleteUrlMap() {

      assertOperationDoneSuccessfully(api().delete(URL_MAP_NAME));

      // remove extra resources created
      assertOperationDoneSuccessfully(api.backendServices().delete(URL_MAP_BACKEND_SERVICE_NAME));

      assertOperationDoneSuccessfully(api.httpHeathChecks().delete(HEALTH_CHECK_NAME));

   }

   private void assertUrlMapEquals(UrlMap result) {
      assertEquals(result.name(), URL_MAP_NAME);
      assertEquals(result.defaultService(), getBackendServiceUrl(URL_MAP_BACKEND_SERVICE_NAME));
      assertEquals(result.description(), "simple url map");
   }

   private void assertUrlMapEquals(UrlMap result, UrlMapOptions expected) {
      assertEquals(result.name(), expected.name());
      assertEquals(result.defaultService(), expected.defaultService());
      assertEquals(result.pathMatchers(), expected.pathMatchers());
      assertEquals(result.hostRules(), expected.hostRules());
   }
}
