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
package org.jclouds.profitbricks.compute.concurrent;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;

@Test(groups = "unit", testName = "ProvisioningManagerTest")
public class ProvisioningManagerTest {

   @Test
   public void testProvision() throws IOException {
      ProvisioningManager manager = new ProvisioningManager();
      AtomicInteger completedJobs = new AtomicInteger(0);

      try {
         for (int i = 0; i < 5; i++) {
            manager.provision(new MockJob(200, "slow", completedJobs));
            manager.provision(new MockJob(0, "fast", completedJobs));
            manager.provision(new MockJob(100, "normal", completedJobs));
         }
      } finally {
         manager.close();
      }

      assertEquals(completedJobs.get(), 15);
   }

   @Test
   public void testProvisionInterrupted() {
      ProvisioningManager manager = new ProvisioningManager();
      AtomicInteger completedJobs = new AtomicInteger(0);

      manager.provision(new ShutdownExecutorJob(manager, completedJobs));
      manager.provision(new MockJob(0, "rejected", completedJobs));

      assertEquals(completedJobs.get(), 1);
   }

   private static class MockJob extends ProvisioningJob {

      private final long delay;
      private final AtomicInteger completedJobs;

      public MockJob(long delay, String group, AtomicInteger completedJobs) {
         super(sleepPredicate(delay), group, Suppliers.ofInstance((Object) 0));
         this.delay = delay;
         this.completedJobs = completedJobs;
      }

      @Override
      public Integer call() throws Exception {
         getAnonymousLogger().info("ProvisioningManagerTest: Starting " + this);
         super.call();
         getAnonymousLogger().info("ProvisioningManagerTest: Completed " + this);
         return completedJobs.incrementAndGet();
      }

      @Override
      public String toString() {
         return "MockJob [id=" + hashCode() + ", group=" + getGroup() + ", delay=" + delay + "]";
      }
   }

   private static class ShutdownExecutorJob extends ProvisioningJob {

      public ShutdownExecutorJob(final ProvisioningManager manager, final AtomicInteger completedJobs) {
         super(Predicates.<String>alwaysTrue(), "shutdown", new Supplier<Object>() {
            @Override
            public Integer get() {
               try {
                  manager.close();
                  return completedJobs.incrementAndGet();
               } catch (IOException ex) {
                  throw Throwables.propagate(ex);
               }
            }
         });
      }
   }

   private static Predicate<String> sleepPredicate(final long delay) {
      return new Predicate<String>() {
         @Override
         public boolean apply(String input) {
            sleepUninterruptibly(delay, TimeUnit.MILLISECONDS);
            return true;
         }
      };
   }
}
