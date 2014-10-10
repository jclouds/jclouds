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

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.internal.BaseChefLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tests behavior of {@code ListNodesImpl} strategies
 */
@Test(groups = "live", testName = "ListNodesImplLiveTest")
public class ListNodesImplLiveTest extends BaseChefLiveTest<ChefApi> {

   private ListNodesImpl strategy;
   private CreateNodeAndPopulateAutomaticAttributesImpl creator;

   private ExecutorService testExecutorService;
   private ListeningExecutorService testListeningExecutorService;

   @Override
   protected void initialize() {
      super.initialize();
      this.creator = injector.getInstance(CreateNodeAndPopulateAutomaticAttributesImpl.class);
      this.strategy = injector.getInstance(ListNodesImpl.class);
      creator.execute(prefix, ImmutableSet.<String> of());
      creator.execute(prefix + 1, ImmutableSet.<String> of());

      this.testExecutorService = Executors.newFixedThreadPool(5);
      this.testListeningExecutorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5));
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      api.deleteNode(prefix);
      api.deleteNode(prefix + 1);

      this.testExecutorService.shutdown();
      this.testListeningExecutorService.shutdown();

      super.tearDown();
   }

   @Test
   public void testExecute() {
      assertTrue(size(strategy.execute()) > 0, "Expected one or more elements");
   }

   public void testExecuteConcurrentlyWithExecutorService() {
      assertTrue(size(strategy.execute(testExecutorService)) > 0, "Expected one or more elements");
   }

   public void testExecuteConcurrentlyWithListeningExecutorService() {
      assertTrue(size(strategy.execute(testListeningExecutorService)) > 0, "Expected one or more elements");
   }
}
