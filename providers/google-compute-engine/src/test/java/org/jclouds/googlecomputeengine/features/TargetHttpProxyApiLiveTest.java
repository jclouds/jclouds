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
import org.jclouds.googlecomputeengine.domain.TargetHttpProxy;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class TargetHttpProxyApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String TARGET_HTTP_PROXY_NAME = "target-http-proxy-api-live-test-target-http-proxy";
   public static final String TARGET_HTTP_PROXY_URL_MAP_NAME = "target-http-proxy-api-live-test-url-map";
   public static final String URL_MAP_DEFAULT_SERVICE_NAME = "target-http-proxy-api-live-test-backend-service";
   public static final String HEALTH_CHECK_NAME = "target-http-proxy-api-live-test-health-check";

   private TargetHttpProxyApi api() {
      return api.targetHttpProxies();
   }

   @Test(groups = "live")
   public void testInsertTargetHttpProxy() {

      // Create resources that are required for target http proxies

      assertOperationDoneSuccessfully(api.httpHeathChecks().insert(HEALTH_CHECK_NAME));

      List<URI> healthChecks = ImmutableList.of(getHealthCheckUrl(HEALTH_CHECK_NAME));
      BackendServiceOptions b = new BackendServiceOptions.Builder(URL_MAP_DEFAULT_SERVICE_NAME, healthChecks)
                                                           .build();

      assertOperationDoneSuccessfully(api.backendServices().create(b));

      UrlMapOptions map = new UrlMapOptions.Builder().name(TARGET_HTTP_PROXY_URL_MAP_NAME).description("simple url map")
                                             .defaultService(getBackendServiceUrl(URL_MAP_DEFAULT_SERVICE_NAME)).build();
      assertOperationDoneSuccessfully(api.urlMaps().create(map));

      UrlMapOptions map2 = new UrlMapOptions.Builder().name(TARGET_HTTP_PROXY_URL_MAP_NAME + "-2")
            .description("a second simple url map")
            .defaultService(getBackendServiceUrl(URL_MAP_DEFAULT_SERVICE_NAME))
            .build();
      assertOperationDoneSuccessfully(api.urlMaps().create(map2));


      assertOperationDoneSuccessfully(api().create(TARGET_HTTP_PROXY_NAME, getUrlMapUrl(TARGET_HTTP_PROXY_URL_MAP_NAME)));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetHttpProxy")
   public void testGetTargetHttpProxy() {
      TargetHttpProxy targetHttpProxy = api().get(TARGET_HTTP_PROXY_NAME);
      assertNotNull(targetHttpProxy);
      assertTargetHttpProxyEquals(targetHttpProxy, getUrlMapUrl(TARGET_HTTP_PROXY_URL_MAP_NAME));
   }

   @Test(groups = "live", dependsOnMethods = "testGetTargetHttpProxy")
   public void testSetUrlMapTargetHttpProxy() {
      assertOperationDoneSuccessfully(api().setUrlMap(TARGET_HTTP_PROXY_NAME,
                                                     getUrlMapUrl(TARGET_HTTP_PROXY_URL_MAP_NAME + "-2")));
   }

   @Test(groups = "live", dependsOnMethods = "testSetUrlMapTargetHttpProxy")
   public void testListTargetHttpProxy() {
      ListPage<TargetHttpProxy> targetHttpProxies = api().list(filter("name eq " + TARGET_HTTP_PROXY_NAME)).next();

      assertEquals(targetHttpProxies.size(), 1);

      assertTargetHttpProxyEquals(Iterables.getOnlyElement(targetHttpProxies),
                                  getUrlMapUrl(TARGET_HTTP_PROXY_URL_MAP_NAME + "-2"));
   }

   @Test(groups = "live", dependsOnMethods = "testListTargetHttpProxy", alwaysRun = true)
   public void testDeleteTargetHttpProxy() {
      assertOperationDoneSuccessfully(api().delete(TARGET_HTTP_PROXY_NAME));

      //remove extra resources created
      assertOperationDoneSuccessfully(api.urlMaps().delete(TARGET_HTTP_PROXY_URL_MAP_NAME));
      assertOperationDoneSuccessfully(api.urlMaps().delete(TARGET_HTTP_PROXY_URL_MAP_NAME + "-2"));
      assertOperationDoneSuccessfully(api.backendServices().delete(URL_MAP_DEFAULT_SERVICE_NAME));
      assertOperationDoneSuccessfully(api.httpHeathChecks().delete(HEALTH_CHECK_NAME));

   }

   private void assertTargetHttpProxyEquals(TargetHttpProxy result, URI urlMap) {
      assertEquals(result.name(), TARGET_HTTP_PROXY_NAME);
      assertEquals(result.urlMap(), urlMap);
   }

}
