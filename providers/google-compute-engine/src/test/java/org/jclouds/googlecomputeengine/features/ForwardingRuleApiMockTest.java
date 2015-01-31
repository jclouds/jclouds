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
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertNull;

import java.net.URI;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseForwardingRuleListTest;
import org.jclouds.googlecomputeengine.parse.ParseForwardingRuleTest;
import org.jclouds.googlecomputeengine.parse.ParseRegionOperationTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ForwardingRuleApiMockTest", singleThreaded = true)
public class ForwardingRuleApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/forwardingrule_get.json"));

      assertEquals(forwardingRuleApi().get("test-forwarding-rule"),
            new ParseForwardingRuleTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions/us-central1/forwardingRules/test-forwarding-rule");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(forwardingRuleApi().get("test-forwarding-rule"));
      assertSent(server, "GET", "/projects/party/regions/us-central1/forwardingRules/test-forwarding-rule");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      ForwardingRuleCreationOptions forwardingRuleCreationOptions = new ForwardingRuleCreationOptions.Builder()
      .target(URI.create(url("/projects/party/regions/europe-west1/targetPools/test-target-pool"))).build();
      assertEquals(forwardingRuleApi().create("test-forwarding-rule", forwardingRuleCreationOptions),
            new ParseRegionOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/regions/us-central1/forwardingRules",
            stringFromResource("/forwardingrule_insert.json"));
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      assertEquals(forwardingRuleApi().delete("test-forwarding-rule"),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/regions/us-central1/forwardingRules/test-forwarding-rule");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(forwardingRuleApi().delete("test-forwarding-rule"));
      assertSent(server, "DELETE", "/projects/party/regions/us-central1/forwardingRules/test-forwarding-rule");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/forwardingrule_list.json"));

      assertEquals(forwardingRuleApi().list().next(), new ParseForwardingRuleListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions/us-central1/forwardingRules");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(forwardingRuleApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/regions/us-central1/forwardingRules");
   }

   public void setTarget() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      URI newTarget = URI.create(url("/projects/party/regions/europe-west1/targetPools/test-target-pool"));
      assertEquals(forwardingRuleApi().setTarget("testForwardingRule", newTarget), new ParseRegionOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/regions/us-central1/forwardingRules/testForwardingRule/setTarget",
            stringFromResource("/forwardingrule_set_target.json"));
   }

   public void setTarget_partialUrl() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      URI newTarget = URI.create("projects/project-id/regions/region/targetPools/target-pool");
      assertEquals(forwardingRuleApi().setTarget("testForwardingRule", newTarget), new ParseRegionOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/regions/us-central1/forwardingRules/testForwardingRule/setTarget",
            "{\"target\":\"projects/project-id/regions/region/targetPools/target-pool\"}");
   }

   ForwardingRuleApi forwardingRuleApi() {
      return api().forwardingRulesInRegion("us-central1");
   }
}
