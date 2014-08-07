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
import org.jclouds.googlecomputeengine.domain.TargetHttpProxy;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class TargetHttpProxyApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String TARGET_HTTP_PROXY_NAME = "target-http-proxy-api-live-test-target-http-proxy";
   public static final String TARGET_HTTP_PROXY_URL_MAP_NAME = "target-http-proxy-api-live-test-url-map";
   public static final String URL_MAP_DEFAULT_SERVICE_NAME = "target-http-proxy-api-live-test-backend-service";
   public static final String HEALTH_CHECK_NAME = "backend-service-api-live-test-health-check";
   public static final int TIME_WAIT = 30;

   private TargetHttpProxyApi api() {
      return api.getTargetHttpProxyApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertTargetHttpProxy() {
      String project = userProject.get();
      // Create resources that are required for target http proxies
      // TODO: (ashmrtnz) create health check once it is merged into project
      HashSet<URI> healthChecks = new HashSet<URI>();
      healthChecks.add(getHealthCheckUrl(userProject.get(), HEALTH_CHECK_NAME));
      BackendServiceOptions b = new BackendServiceOptions().name(URL_MAP_DEFAULT_SERVICE_NAME)
                                                           .healthChecks(healthChecks);
      assertGlobalOperationDoneSucessfully(api.getBackendServiceApiForProject(userProject.get())
                                              .create(URL_MAP_DEFAULT_SERVICE_NAME, b), TIME_WAIT);
      
      UrlMapOptions map = new UrlMapOptions().name(TARGET_HTTP_PROXY_URL_MAP_NAME).description("simple url map")
                                             .defaultService(getBackendServiceUrl(project,
                                                                                  URL_MAP_DEFAULT_SERVICE_NAME));
      assertGlobalOperationDoneSucessfully(api.getUrlMapApiForProject(project).create(TARGET_HTTP_PROXY_URL_MAP_NAME,
                                                                                      map), TIME_WAIT);
      
      assertGlobalOperationDoneSucessfully(api().create(TARGET_HTTP_PROXY_NAME,
                                                        getUrlMapUrl(project, TARGET_HTTP_PROXY_URL_MAP_NAME)),
                                           TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetHttpProxy")
   public void testGetTargetHttpProxy() {
      TargetHttpProxy targetHttpProxy = api().get(TARGET_HTTP_PROXY_NAME);
      assertNotNull(targetHttpProxy);
      assertTargetHttpProxyEquals(targetHttpProxy, getUrlMapUrl(userProject.get(), TARGET_HTTP_PROXY_URL_MAP_NAME));
   }
   
   @Test(groups = "live", dependsOnMethods = "testGetTargetHttpProxy")
   public void testSetUrlMapTargetHttpProxy() {
      UrlMapOptions map = new UrlMapOptions().name(TARGET_HTTP_PROXY_URL_MAP_NAME).description("simple url map")
                                             .defaultService(getBackendServiceUrl(userProject.get(),
                                                                                  URL_MAP_DEFAULT_SERVICE_NAME));
      assertGlobalOperationDoneSucessfully(api.getUrlMapApiForProject(userProject.get())
                                              .create(TARGET_HTTP_PROXY_URL_MAP_NAME + "-2", map), TIME_WAIT);
      
      assertGlobalOperationDoneSucessfully(api().setUrlMap(TARGET_HTTP_PROXY_NAME,
                                                           getUrlMapUrl(userProject.get(),
                                                                        TARGET_HTTP_PROXY_URL_MAP_NAME + "-2")),
                                           TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testSetUrlMapTargetHttpProxy")
   public void testListTargetHttpProxy() {
      PagedIterable<TargetHttpProxy> disks = api().list(new ListOptions.Builder()
              .filter("name eq " + TARGET_HTTP_PROXY_NAME));

      List<TargetHttpProxy> targetHttpProxiesAsList = Lists.newArrayList(disks.concat());

      assertEquals(targetHttpProxiesAsList.size(), 1);

      assertTargetHttpProxyEquals(Iterables.getOnlyElement(targetHttpProxiesAsList),
                                  getUrlMapUrl(userProject.get(), TARGET_HTTP_PROXY_URL_MAP_NAME + "-2"));

   }

   @Test(groups = "live", dependsOnMethods = "testListTargetHttpProxy")
   public void testDeleteTargetHttpProxy() {
      assertGlobalOperationDoneSucessfully(api().delete(TARGET_HTTP_PROXY_NAME), TIME_WAIT);
      
      //remove extra resources created
      assertGlobalOperationDoneSucessfully(api.getUrlMapApiForProject(userProject.get())
                                           .delete(TARGET_HTTP_PROXY_URL_MAP_NAME), TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api.getUrlMapApiForProject(userProject.get())
                                           .delete(TARGET_HTTP_PROXY_URL_MAP_NAME + "-2"), TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api.getBackendServiceApiForProject(userProject.get())
                                           .delete(URL_MAP_DEFAULT_SERVICE_NAME), TIME_WAIT);
      // TODO: delete health check once it is merged
   }

   private void assertTargetHttpProxyEquals(TargetHttpProxy result, URI urlMap) {
      assertEquals(result.getName(), TARGET_HTTP_PROXY_NAME);
      assertEquals(result.getUrlMap(), urlMap);
   }

}
