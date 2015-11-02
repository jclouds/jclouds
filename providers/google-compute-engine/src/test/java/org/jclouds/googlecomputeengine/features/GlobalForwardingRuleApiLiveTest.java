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
import static org.testng.Assert.assertTrue;

import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.ForwardingRule.IPProtocol;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import java.net.URI;
import java.util.List;



public class GlobalForwardingRuleApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String GLOBAL_FORWARDING_RULE_NAME = "global-forwarding-rule-api-live-test-forwarding-rule";
   private static final String GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME = "global-"
            + "forwarding-rule-api-live-test-target-http-proxy";
   private static final String GLOBAL_FORWARDING_RULE_URL_MAP_NAME = "global-"
            + "forwarding-rule-api-live-test-url-map";
   private static final String GLOBAL_FORWARDING_RULE_BACKEND_SERVICE_NAME = "global-"
            + "forwarding-rule-api-live-test-backend-service";
   private static final String GLOBAL_FORWARDING_RULE_HEALTH_CHECK_NAME = "global-"
            + "forwarding-rule-api-live-test-health-check";
   private static final String PORT_RANGE = "80";

   private ForwardingRuleApi api() {
      return api.globalForwardingRules();
   }

   @Test(groups = "live")
   public void testInsertGlobalForwardingRule() {
      assertOperationDoneSuccessfully(api.httpHeathChecks().insert(GLOBAL_FORWARDING_RULE_HEALTH_CHECK_NAME));

      List<URI> healthChecks = ImmutableList.of(getHealthCheckUrl(GLOBAL_FORWARDING_RULE_HEALTH_CHECK_NAME));
      BackendServiceOptions b = new BackendServiceOptions.Builder(GLOBAL_FORWARDING_RULE_BACKEND_SERVICE_NAME, healthChecks).build();
      assertOperationDoneSuccessfully(api.backendServices()
                                              .create(b));

      UrlMapOptions map = new UrlMapOptions.Builder().name(GLOBAL_FORWARDING_RULE_URL_MAP_NAME)
            .description("simple url map")
            .defaultService(getBackendServiceUrl(GLOBAL_FORWARDING_RULE_BACKEND_SERVICE_NAME)).build();
      assertOperationDoneSuccessfully(api.urlMaps().create(map));
      assertOperationDoneSuccessfully(api.targetHttpProxies().create(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME,
            getUrlMapUrl(GLOBAL_FORWARDING_RULE_URL_MAP_NAME)));
      assertOperationDoneSuccessfully(api().create(
            GLOBAL_FORWARDING_RULE_NAME,
            new ForwardingRuleCreationOptions.Builder()
                  .target(getTargetHttpProxyUrl(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME))
                  .description("jclodus-test").portRange(PORT_RANGE).build()));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertGlobalForwardingRule")
   public void testGetGlobalForwardingRule() {
      ForwardingRule forwardingRule = api().get(GLOBAL_FORWARDING_RULE_NAME);
      assertNotNull(forwardingRule);
      ForwardingRuleCreationOptions expected = new ForwardingRuleCreationOptions.Builder()
            .target(getTargetHttpProxyUrl(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME))
            .portRange("80-80")
            .ipProtocol(IPProtocol.TCP)
            .description("jclodus-test")
            .build();
      assertGlobalForwardingRuleEquals(forwardingRule, expected);
   }

   @Test(groups = "live", dependsOnMethods = "testGetGlobalForwardingRule")
   public void testSetGlobalForwardingRuleTarget() {
      assertOperationDoneSuccessfully(api.targetHttpProxies()
                                           .create(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME + "-2",
                                                   getUrlMapUrl(GLOBAL_FORWARDING_RULE_URL_MAP_NAME)));
      assertOperationDoneSuccessfully(api().setTarget(GLOBAL_FORWARDING_RULE_NAME,
            getTargetHttpProxyUrl(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME + "-2")));
   }

   @Test(groups = "live", dependsOnMethods = "testSetGlobalForwardingRuleTarget")
   public void testListGlobalForwardingRule() {
      ListPage<ForwardingRule> forwardingRules = api().list(filter("name eq " + GLOBAL_FORWARDING_RULE_NAME)).next();
      assertEquals(forwardingRules.size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = "testListGlobalForwardingRule")
   public void testDeleteGlobalForwardingRule() {
      assertOperationDoneSuccessfully(api().delete(GLOBAL_FORWARDING_RULE_NAME));

      // Teardown other created resources
      assertOperationDoneSuccessfully(api.targetHttpProxies()
                                              .delete(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME));

      assertOperationDoneSuccessfully(api.targetHttpProxies()
                                           .delete(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME + "-2"));

      assertOperationDoneSuccessfully(api.urlMaps()
                                           .delete(GLOBAL_FORWARDING_RULE_URL_MAP_NAME));

      assertOperationDoneSuccessfully(api.backendServices()
                                           .delete(GLOBAL_FORWARDING_RULE_BACKEND_SERVICE_NAME));

      assertOperationDoneSuccessfully(api.httpHeathChecks().delete(GLOBAL_FORWARDING_RULE_HEALTH_CHECK_NAME));

   }

   private void assertGlobalForwardingRuleEquals(ForwardingRule result, ForwardingRuleCreationOptions expected) {
      assertEquals(result.target(), expected.target());
      assertEquals(result.ipProtocol(), expected.ipProtocol());
      assertEquals(result.description(), expected.description());
      assertEquals(result.portRange(), expected.portRange());
      assertTrue(result.ipAddress() != null);
   }

}
