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

import static com.google.common.util.concurrent.Futures.getUnchecked;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.jclouds.concurrent.config.WithSubmissionTrace;
import org.jclouds.logging.Logger;

import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Delegates {@link Job} to single-threaded executor services based on it's group.
 *
 */
public final class ProvisioningManager implements Closeable {

   @Resource
   private Logger logger = Logger.NULL;

   private final Map<String, ListeningExecutorService> workers
           = new ConcurrentHashMap<String, ListeningExecutorService>(1);

   private final AtomicBoolean terminated = new AtomicBoolean(false);

   public Object provision(ProvisioningJob job) {
      if (terminated.get()) {
         logger.warn("Job(%s) submitted but the provisioning manager is already closed", job);
         return null;
      }

      logger.debug("Job(%s) submitted to group '%s'", job, job.getGroup());
      ListeningExecutorService workerGroup = getWorkerGroup(job.getGroup());
      return getUnchecked(workerGroup.submit(job));
   }

   protected ListeningExecutorService newExecutorService() {
      return WithSubmissionTrace.wrap(listeningDecorator(Executors.newSingleThreadExecutor()));
   }

   private void newWorkerGroupIfAbsent(String name) {
      if (!workers.containsKey(name))
         workers.put(name, newExecutorService());
   }

   private ListeningExecutorService getWorkerGroup(String name) {
      newWorkerGroupIfAbsent(name);
      return workers.get(name);
   }

   @Override
   public void close() throws IOException {
      terminated.set(true); // Do not allow to enqueue more jobs
      Collection<ListeningExecutorService> executors = workers.values();
      for (ListeningExecutorService executor : executors) {
         List<Runnable> runnables = executor.shutdownNow();
         if (!runnables.isEmpty())
            logger.warn("when shutting down executor %s, runnables outstanding: %s", executor, runnables);
      }
   }

}
