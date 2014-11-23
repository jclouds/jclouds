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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;

import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.strategy.DeleteAllClientsInList;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * Concurrently delete all given clients.
 */
@Singleton
public class DeleteAllClientsInListImpl implements DeleteAllClientsInList {

   protected final ChefApi api;
   protected final ListeningExecutorService userExecutor;
   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   DeleteAllClientsInListImpl(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, ChefApi api) {
      this.userExecutor = checkNotNull(userExecutor, "userExecuor");
      this.api = checkNotNull(api, "api");
   }

   @Override
   public void execute(Iterable<String> names) {
      execute(userExecutor, names);
   }

   @Override
   public void execute(final ListeningExecutorService executor, Iterable<String> names) {
      ListenableFuture<List<Client>> futures = allAsList(transform(names,
            new Function<String, ListenableFuture<Client>>() {
               @Override
               public ListenableFuture<Client> apply(final String input) {
                  return executor.submit(new Callable<Client>() {
                     @Override
                     public Client call() throws Exception {
                        return api.deleteClient(input);
                     }
                  });
               }
            }));

      logger.trace(String.format("deleting clients: %s", Joiner.on(',').join(names)));
      getUnchecked(futures);
   }
}
