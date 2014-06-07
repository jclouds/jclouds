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
package org.jclouds.chef.strategy.internal;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.internal.BaseChefLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CleanupStaleNodesAndClientsImpl} strategies
 */
@Test(groups = "live", testName = "CleanupStaleNodesAndClientsImplLiveTest")
public class CleanupStaleNodesAndClientsImplLiveTest extends BaseChefLiveTest<ChefApi> {

   private CreateNodeAndPopulateAutomaticAttributesImpl creator;
   private CleanupStaleNodesAndClientsImpl strategy;

   @Override
   protected void initialize() {
      super.initialize();
      this.creator = injector.getInstance(CreateNodeAndPopulateAutomaticAttributesImpl.class);
      this.strategy = injector.getInstance(CleanupStaleNodesAndClientsImpl.class);
   }

   @Test
   public void testExecute() throws InterruptedException {
      try {
         creator.execute(prefix, ImmutableSet.<String> of());
         // http://tickets.corp.opscode.com/browse/PL-522
         // assert chef.nodeExists(prefix);
         assertNotNull(api.getNode(prefix));
         strategy.execute(prefix, 10);
         assertNotNull(api.getNode(prefix));
         Thread.sleep(1000);
         strategy.execute(prefix, 1);
         assertNull(api.getNode(prefix));
      } finally {
         api.deleteNode(prefix);
      }
   }

}
