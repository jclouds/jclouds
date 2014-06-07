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

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertTrue;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.internal.BaseChefLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ListNodesInEnvironmentImpl} strategies
 */
@Test(groups = "live", testName = "ListNodesInEnvironmentImplLiveTest")
public class ListNodesInEnvironmentImplLiveTest extends BaseChefLiveTest<ChefApi> {

   private ListNodesInEnvironmentImpl strategy;
   private CreateNodeAndPopulateAutomaticAttributesImpl creator;

   @Override
   protected void initialize() {
      super.initialize();
      this.creator = injector.getInstance(CreateNodeAndPopulateAutomaticAttributesImpl.class);
      this.strategy = injector.getInstance(ListNodesInEnvironmentImpl.class);
      creator.execute(prefix, ImmutableSet.<String> of());
      creator.execute(prefix + 1, ImmutableSet.<String> of());
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      api.deleteNode(prefix);
      api.deleteNode(prefix + 1);
      super.tearDown();
   }

   @Test
   public void testExecute() {
      assertTrue(size(strategy.execute("_default")) > 0, "Expected one or more elements");
   }
}
