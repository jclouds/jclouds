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

import org.jclouds.chef.ChefClient;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.internal.BaseChefContextLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * Tests behavior of {@code CleanupStaleNodesAndClientsImpl} strategies
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "CleanupStaleNodesAndClientsImplLiveTest")
public class CleanupStaleNodesAndClientsImplLiveTest extends BaseChefContextLiveTest<ChefContext> {

   private CreateNodeAndPopulateAutomaticAttributesImpl creater;
   private CleanupStaleNodesAndClientsImpl strategy;
   
   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      this.creater = context.utils().injector().getInstance(CreateNodeAndPopulateAutomaticAttributesImpl.class);
      this.strategy = context.utils().injector().getInstance(CleanupStaleNodesAndClientsImpl.class);
   }

   @Test
   public void testExecute() throws InterruptedException {
      try {
         creater.execute(prefix, ImmutableSet.<String> of());
         // http://tickets.corp.opscode.com/browse/PL-522
         // assert chef.nodeExists(prefix);
         assert context.getApi().getNode(prefix) != null;
         strategy.execute(prefix, 10);
         assert context.getApi().getNode(prefix) != null;
         Thread.sleep(1000);
         strategy.execute(prefix, 1);
         assert context.getApi().getNode(prefix) == null;
      } finally {
         context.getApi().deleteNode(prefix);
      }
   }
   
   @Override
   protected ChefClient getChefClient(ChefContext context)
   {
       return context.getApi();
   }
   
   @Override
   protected TypeToken<ChefContext> contextType()
   {
       return TypeToken.of(ChefContext.class);
   }

}
