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

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions.Builder(TARGETPOOL_NAME).build();
      assertOperationDoneSuccessfully(targetPoolApi().create(targetPoolCreationOptions));
      targetPool = targetPoolApi().get(TARGETPOOL_NAME);

      targetPoolCreationOptions = new TargetPoolCreationOptions.Builder(TARGETPOOL_NAME_NEW).build();
      assertOperationDoneSuccessfully(targetPoolApi().create(targetPoolCreationOptions));
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
      ForwardingRuleCreationOptions forwardingRuleCreationOptions = new ForwardingRuleCreationOptions.Builder()
                                                                           .description(DESCRIPTION)
                                                                           .ipAddress(address.address())
                                                                           .ipProtocol(ForwardingRule.IPProtocol.TCP)
                                                                           .target(targetPool.selfLink())
                                                                           .build();
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

}
