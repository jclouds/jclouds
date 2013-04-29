/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef.strategy.internal;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.internal.BaseChefLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code GetNodesImpl} strategies
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "GetNodesImplLiveTest")
public class GetNodesImplLiveTest extends BaseChefLiveTest<ChefApi> {

   private ListNodesImpl strategy;
   private CreateNodeAndPopulateAutomaticAttributesImpl creater;

   @Override
   protected void initialize() {
      super.initialize();
      this.creater = injector.getInstance(CreateNodeAndPopulateAutomaticAttributesImpl.class);
      this.strategy = injector.getInstance(ListNodesImpl.class);
      creater.execute(prefix, ImmutableSet.<String> of());
      creater.execute(prefix + 1, ImmutableSet.<String> of());
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
      assert size(strategy.execute()) > 0;
   }

   @Test
   public void testExecutePredicateOfString() {
      assertEquals(size(strategy.execute(new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            return input.startsWith(prefix);
         }

      })), 2);
   }

   @Test
   public void testExecuteIterableOfString() {
      assertEquals(size(strategy.execute(ImmutableSet.of(prefix, prefix + 1))), 2);
   }

}
