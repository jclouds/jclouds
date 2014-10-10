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

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.internal.BaseChefLiveTest;
import org.jclouds.ohai.config.OhaiModule.CurrentUserProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code UpdateAutomaticAttributesOnNodeImpl} strategies
 */
@Test(groups = "live", testName = "UpdateAutomaticAttributesOnNodeImplLiveTest")
public class UpdateAutomaticAttributesOnNodeImplLiveTest extends BaseChefLiveTest<ChefApi> {

   private CurrentUserProvider currentUserProvider;
   private UpdateAutomaticAttributesOnNodeImpl strategy;

   @Override
   protected void initialize() {
      super.initialize();
      this.currentUserProvider = injector.getInstance(CurrentUserProvider.class);
      this.strategy = injector.getInstance(UpdateAutomaticAttributesOnNodeImpl.class);
   }

   @Test
   public void testExecute() {
      Set<String> runList = ImmutableSet.of("role[" + prefix + "]");
      try {
         api.createNode(Node.builder().name(prefix).runList(runList).environment("_default").build());
         strategy.execute(prefix);
         Node node = api.getNode(prefix);
         assertEquals(node.getName(), prefix);
         assertEquals(node.getRunList(), runList);
         assertEquals(node.getAutomaticAttributes().get("current_user").toString(), currentUserProvider.get().toString());
      } finally {
         api.deleteNode(prefix);
      }
   }
}
