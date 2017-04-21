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

package org.jclouds.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.Uninterruptibles;

public class DynamicThreadPoolExecutorTest {

   @Test
   public void testTasksAreEnqueuedIfQueueFull() throws InterruptedException, ExecutionException, TimeoutException {
      DynamicThreadPoolExecutor executor = newExecutor(new DynamicThreadPoolExecutor.ForceQueuePolicy());
      try {
         List<Task> tasks = ImmutableList.of(new Task(2), new Task(2), new Task(2), new Task(2));
         List<Future<?>> futures = new ArrayList<Future<?>>();
         for (Task task : tasks) {
            futures.add(executor.submit(task));
         }

         for (Future<?> future : futures) {
            future.get(5, TimeUnit.SECONDS);
         }
      } finally {
         executor.shutdownNow();
      }
   }

   @Test(expectedExceptions = RejectedExecutionException.class)
   public void testTasksAreRejectedIfQueueFull() throws InterruptedException, ExecutionException, TimeoutException {
      DynamicThreadPoolExecutor executor = newExecutor(new ThreadPoolExecutor.AbortPolicy());
      try {
         for (int i = 0; i < executor.getMaximumPoolSize() + 4; i++) {
            executor.submit(new Task(2));
         }
      } finally {
         executor.shutdownNow();
      }
   }

   @Test
   public void testTasksWaitForSpaceIfQueueFull() throws InterruptedException, ExecutionException, TimeoutException {
      DynamicThreadPoolExecutor executor = newExecutor(new DynamicThreadPoolExecutor.TimedBlockingPolicy(5000));
      try {
         List<Task> tasks = ImmutableList.of(new Task(2), new Task(2), new Task(2), new Task(2));
         List<Future<?>> futures = new ArrayList<Future<?>>();
         for (Task task : tasks) {
            futures.add(executor.submit(task));
         }

         for (Future<?> future : futures) {
            future.get(5, TimeUnit.SECONDS);
         }
      } finally {
         executor.shutdownNow();
      }
   }

   @Test(expectedExceptions = RejectedExecutionException.class)
   public void testTasksAreRejectedIfExecutorIsShutdownAndPolicyIsForce() throws InterruptedException,
         ExecutionException, TimeoutException {
      DynamicThreadPoolExecutor executor = newExecutor(new DynamicThreadPoolExecutor.ForceQueuePolicy());
      try {
         executor.submit(new Task(2));
         executor.shutdown();
         executor.submit(new Task(2));
      } finally {
         executor.shutdownNow();
      }
   }

   @Test(expectedExceptions = RejectedExecutionException.class)
   public void testTasksAreRejectedIfExecutorIsShutdownAndPolicyIsWait() throws InterruptedException,
         ExecutionException, TimeoutException {
      DynamicThreadPoolExecutor executor = newExecutor(new DynamicThreadPoolExecutor.TimedBlockingPolicy(5000));
      try {
         executor.submit(new Task(2));
         executor.shutdown();
         executor.submit(new Task(2));
      } finally {
         executor.shutdownNow();
      }
   }

   private static class Task implements Runnable {
      private final AtomicInteger executions;

      public Task(int executions) {
         this.executions = new AtomicInteger(executions);
      }

      @Override
      public void run() {
         while (executions.decrementAndGet() >= 0) {
            Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
         }
      }
   }

   public DynamicThreadPoolExecutor newExecutor(RejectedExecutionHandler rejectionPolicy) {
      DynamicThreadPoolExecutor.DynamicQueue<Runnable> queue = new DynamicThreadPoolExecutor.DynamicQueue<Runnable>();
      DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(1, 1, 60000, TimeUnit.MILLISECONDS, queue,
            namedThreadFactory("dyn-pool-test"));
      executor.setRejectedExecutionHandler(rejectionPolicy);
      queue.setThreadPoolExecutor(executor);
      return executor;
   }

   private ThreadFactory namedThreadFactory(String name) {
      return new ThreadFactoryBuilder().setNameFormat(name).setThreadFactory(Executors.defaultThreadFactory()).build();
   }
}
