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

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.ForwardingRuleOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.HashSet;
import java.util.List;

import org.jclouds.collect.PagedIterable;

import com.google.common.collect.Lists;

public class ForwardingRuleApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String FORWARDING_RULE_NAME = "forwarding-rule-api-live-test";
   private static final String TARGETPOOL_NAME = "forwarding-rule-api-live-test-targetpool";
   private static final String TARGETPOOL_NAME_NEW = "forwarding-rule-api-live-test-new-targetpool";
   private static final String DESCRIPTION = "Forwarding rule api live test forwarding rule.";
   private static final String ADDRESS_NAME = "forwarding-rule-api-address";
   private TargetPool targetPool;
   private TargetPool newTargetPool;
   private Address address;

   /**
    * The API under test
    * @return
    */
   private ForwardingRuleApi api() {
      return api.forwardingRulesInRegion(DEFAULT_REGION_NAME);
   }

   private TargetPoolApi targetPoolApi() {
      return api.targetPoolsInRegion(DEFAULT_REGION_NAME);
   }

   private AddressApi addressApi(){
      return  api.addressesInRegion(DEFAULT_REGION_NAME);
   }

   @BeforeClass
   public void init() {
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions();
      assertOperationDoneSuccessfully(targetPoolApi().create(TARGETPOOL_NAME, targetPoolCreationOptions));
      targetPool = targetPoolApi().get(TARGETPOOL_NAME);

      assertOperationDoneSuccessfully(targetPoolApi().create(TARGETPOOL_NAME_NEW, targetPoolCreationOptions));
      newTargetPool = targetPoolApi().get(TARGETPOOL_NAME_NEW);

      assertOperationDoneSuccessfully(addressApi().create(ADDRESS_NAME));
      address = addressApi().get(ADDRESS_NAME);
   }

   @AfterClass
   public void tearDown() {
      assertOperationDoneSuccessfully(targetPoolApi().delete(TARGETPOOL_NAME));
      assertOperationDoneSuccessfully(targetPoolApi().delete(TARGETPOOL_NAME_NEW));
      assertOperationDoneSuccessfully(addressApi().delete(ADDRESS_NAME));
   }

   @Test(groups = "live")
   public void testInsertForwardingRule() {
      ForwardingRuleCreationOptions forwardingRuleCreationOptions = new ForwardingRuleCreationOptions()
                                                                           .description(DESCRIPTION)
                                                                           .ipAddress(address.address())
                                                                           .ipProtocol(ForwardingRule.IPProtocol.TCP)
                                                                           .target(targetPool.selfLink());
      assertOperationDoneSuccessfully(api().create(FORWARDING_RULE_NAME, forwardingRuleCreationOptions));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertForwardingRule")
   public void testGetForwardingRule() {
      ForwardingRule forwardingRule = api().get(FORWARDING_RULE_NAME);
      assertNotNull(forwardingRule);
      assertEquals(forwardingRule.name(), FORWARDING_RULE_NAME);
      assertEquals(forwardingRule.description(), DESCRIPTION);
      assertEquals(forwardingRule.ipAddress(), address.address());
      assertEquals(forwardingRule.ipProtocol(), ForwardingRule.IPProtocol.TCP);
      assertEquals(forwardingRule.target(), targetPool.selfLink());
   }

   @Test(groups = "live", dependsOnMethods = "testGetForwardingRule")
   public void testSetTargetForwardingRule(){
      assertOperationDoneSuccessfully(api().setTarget(FORWARDING_RULE_NAME, newTargetPool.selfLink()));
      ForwardingRule forwardingRule = api().get(FORWARDING_RULE_NAME);
      assertNotNull(forwardingRule);
      assertEquals(forwardingRule.name(), FORWARDING_RULE_NAME);
      assertEquals(forwardingRule.target(), newTargetPool.selfLink());
   }

   @Test(groups = "live", dependsOnMethods = "testInsertForwardingRule")
   public void testListForwardingRule() {
      ListPage<ForwardingRule> forwardingRule = api().list(filter("name eq " + FORWARDING_RULE_NAME)).next();
      assertEquals(forwardingRule.size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = {"testListForwardingRule", "testSetTargetForwardingRule"}, alwaysRun = true)
   public void testDeleteForwardingRule() {
      assertOperationDoneSuccessfully(api().delete(FORWARDING_RULE_NAME));
   }


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
      return api.getForwardingRuleApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertGlobalForwardingRule() {
      String project = userProject.get();

      // TODO: (ashmrtnz) create httpHealthCheck here once it is merged into project
      HashSet<URI> healthChecks = new HashSet<URI>();
      healthChecks.add(getHealthCheckUrl(project, GLOBAL_FORWARDING_RULE_HEALTH_CHECK_NAME));
      BackendServiceOptions b = new BackendServiceOptions().name(GLOBAL_FORWARDING_RULE_BACKEND_SERVICE_NAME)
                                                           .healthChecks(healthChecks);
      assertGlobalOperationDoneSucessfully(api.getBackendServiceApiForProject(project)
                                              .create(GLOBAL_FORWARDING_RULE_BACKEND_SERVICE_NAME, b), TIME_WAIT);

      UrlMapOptions map = new UrlMapOptions().name(GLOBAL_FORWARDING_RULE_URL_MAP_NAME)
                                             .description("simple url map")
                                             .defaultService(getBackendServiceUrl(project,
                                                                         GLOBAL_FORWARDING_RULE_BACKEND_SERVICE_NAME));
      assertGlobalOperationDoneSucessfully(api.getUrlMapApiForProject(project)
                                              .create(GLOBAL_FORWARDING_RULE_URL_MAP_NAME,
                                                      map),
                                           TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api.getTargetHttpProxyApiForProject(project)
                                              .create(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME,
                                                      getUrlMapUrl(project, GLOBAL_FORWARDING_RULE_URL_MAP_NAME)),
                                           TIME_WAIT);
      assertGlobalOperationDoneSucessfully(
            api().create(GLOBAL_FORWARDING_RULE_NAME,
                         new ForwardingRuleOptions().target(getTargetHttpProxyUrl(userProject.get(),
                                                                    GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME))
                                                    .portRange(PORT_RANGE)),
                         TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertGlobalForwardingRule")
   public void testGetGlobalForwardingRule() {
      ForwardingRule forwardingRule = api().get(GLOBAL_FORWARDING_RULE_NAME);
      assertNotNull(forwardingRule);
      ForwardingRuleOptions expected = new ForwardingRuleOptions()
            .target(getTargetHttpProxyUrl(userProject.get(),
                                          GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME))
            .portRange("80-80")
            .ipProtocol("TCP")
            .name(GLOBAL_FORWARDING_RULE_NAME);
      assertGlobalForwardingRuleEquals(forwardingRule, expected);
   }

   @Test(groups = "live", dependsOnMethods = "testGetGlobalForwardingRule")
   public void testSetGlobalForwardingRuleTarget() {
      assertGlobalOperationDoneSucessfully(api.getTargetHttpProxyApiForProject(userProject.get())
                                           .create(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME + "-2",
                                                   getUrlMapUrl(userProject.get(),
                                                                GLOBAL_FORWARDING_RULE_URL_MAP_NAME)),
                                        TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api().setTarget(GLOBAL_FORWARDING_RULE_NAME,
            getTargetHttpProxyUrl(userProject.get(),
                                  GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME + "-2")),
            TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testSetGlobalForwardingRuleTarget")
   public void testListGlobalForwardingRule() {

      PagedIterable<ForwardingRule> forwardingRules = api().list(new ListOptions.Builder()
              .filter("name eq " + GLOBAL_FORWARDING_RULE_NAME));

      List<ForwardingRule> forwardingRulesAsList = Lists.newArrayList(forwardingRules.concat());

      assertEquals(forwardingRulesAsList.size(), 1);

   }

   @Test(groups = "live", dependsOnMethods = "testListGlobalForwardingRule")
   public void testDeleteGlobalForwardingRule() {
      assertGlobalOperationDoneSucessfully(api().delete(GLOBAL_FORWARDING_RULE_NAME), TIME_WAIT);

      // Teardown other created resources
      String project = userProject.get();
      assertGlobalOperationDoneSucessfully(api.getTargetHttpProxyApiForProject(project)
                                              .delete(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME),
                                           TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api.getTargetHttpProxyApiForProject(project)
                                           .delete(GLOBAL_FORWARDING_RULE_TARGET_HTTP_PROXY_NAME + "-2"),
                                        TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api.getUrlMapApiForProject(project)
                                           .delete(GLOBAL_FORWARDING_RULE_URL_MAP_NAME),
                                        TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api.getBackendServiceApiForProject(project)
                                           .delete(GLOBAL_FORWARDING_RULE_BACKEND_SERVICE_NAME),
                                        TIME_WAIT);
      // TODO: (ashmrtnz) delete health check once it is merged into project
   }

   private void assertGlobalForwardingRuleEquals(ForwardingRule result, ForwardingRuleOptions expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getTarget(), expected.getTarget());
      assertEquals(result.getIpProtocol().orNull(), expected.getIpProtocol());
      assertEquals(result.getDescription().orNull(), expected.getDescription());
      assertEquals(result.getPortRange(), expected.getPortRange());
      assertTrue(result.getIpAddress().isPresent());
   }
}
