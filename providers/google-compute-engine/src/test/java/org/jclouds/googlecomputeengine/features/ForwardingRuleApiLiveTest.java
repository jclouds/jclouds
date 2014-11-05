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

import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.ListPage;
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
   private static final int TIME_WAIT = 30;
   private TargetPool targetPool;
   private TargetPool newTargetPool;
   private Address address;

   /**
    * The API under test
    * @return
    */
   private ForwardingRuleApi api() {
      return api.getForwardingRuleApi(userProject.get(), DEFAULT_REGION_NAME);
   }

   private TargetPoolApi targetPoolApi() {
      return api.getTargetPoolApi(userProject.get(), DEFAULT_REGION_NAME);
   }

   private AddressApi addressApi(){
      return  api.getAddressApi(userProject.get(), DEFAULT_REGION_NAME);
   }

   @BeforeClass
   public void init() {
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions();
      assertRegionOperationDoneSucessfully(targetPoolApi().create(TARGETPOOL_NAME, targetPoolCreationOptions), TIME_WAIT);
      targetPool = targetPoolApi().get(TARGETPOOL_NAME);

      assertRegionOperationDoneSucessfully(targetPoolApi().create(TARGETPOOL_NAME_NEW, targetPoolCreationOptions), TIME_WAIT);
      newTargetPool = targetPoolApi().get(TARGETPOOL_NAME_NEW);

      assertRegionOperationDoneSucessfully(addressApi().create(ADDRESS_NAME), TIME_WAIT);
      address = addressApi().get(ADDRESS_NAME);
   }

   @AfterClass
   public void tearDown() {
      assertRegionOperationDoneSucessfully(targetPoolApi().delete(TARGETPOOL_NAME), TIME_WAIT);
      assertRegionOperationDoneSucessfully(targetPoolApi().delete(TARGETPOOL_NAME_NEW), TIME_WAIT);
      assertRegionOperationDoneSucessfully(addressApi().delete(ADDRESS_NAME), TIME_WAIT);
   }

   @Test(groups = "live")
   public void testInsertForwardingRule() {
      ForwardingRuleCreationOptions forwardingRuleCreationOptions = new ForwardingRuleCreationOptions()
                                                                           .description(DESCRIPTION)
                                                                           .ipAddress(address.address())
                                                                           .ipProtocol(ForwardingRule.IPProtocol.TCP)
                                                                           .target(targetPool.selfLink());
      assertRegionOperationDoneSucessfully(api().create(FORWARDING_RULE_NAME, forwardingRuleCreationOptions), TIME_WAIT);
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
      assertRegionOperationDoneSucessfully(api().setTarget(FORWARDING_RULE_NAME, newTargetPool.selfLink()), TIME_WAIT);
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
      assertRegionOperationDoneSucessfully(api().delete(FORWARDING_RULE_NAME), TIME_WAIT);
   }
}
