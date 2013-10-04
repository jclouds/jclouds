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
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.domain.CookbookDefinition;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.strategy.ListCookbookVersionsInEnvironment;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 *
 * @author Noorul Islam K M
 */
@Singleton
public class ListCookbookVersionsInEnvironmentImpl implements ListCookbookVersionsInEnvironment {

   protected final ChefApi api;
   protected final ListeningExecutorService userExecutor;
   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   ListCookbookVersionsInEnvironmentImpl(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, ChefApi api) {
      this.userExecutor = checkNotNull(userExecutor, "userExecuor");
      this.api = checkNotNull(api, "api");
   }

   @Override
   public Iterable<? extends CookbookVersion> execute(String environmentName) {
      return execute(userExecutor, environmentName);
   }

   @Override
   public Iterable<? extends CookbookVersion> execute(String environmentName, String numVersions) {
      return execute(userExecutor, environmentName, numVersions);
   }

   public Iterable<? extends CookbookVersion> execute(ListeningExecutorService executor, String environmentName) {
      return execute(executor, api.listCookbooksInEnvironment(environmentName));
   }

   @Override
   public Iterable<? extends CookbookVersion> execute(ListeningExecutorService executor, String environmentName, String numVersions) {
      return execute(executor, api.listCookbooksInEnvironment(environmentName, numVersions));
   }

   private Iterable<? extends CookbookVersion> execute(final ListeningExecutorService executor,
         Iterable<CookbookDefinition> cookbookDefs) {
      return concat(transform(cookbookDefs, new Function<CookbookDefinition, Iterable<? extends CookbookVersion>>() {

         @Override
         public Iterable<? extends CookbookVersion> apply(final CookbookDefinition cookbookDef) {
            // TODO getting each version could also go parallel
            Set<CookbookDefinition.Version> cookbookVersions = cookbookDef.getVersions();
            ListenableFuture<List<CookbookVersion>> futures = allAsList(transform(cookbookVersions,
                  new Function<CookbookDefinition.Version, ListenableFuture<CookbookVersion>>() {
                     @Override
                     public ListenableFuture<CookbookVersion> apply(final CookbookDefinition.Version version) {
                        return executor.submit(new Callable<CookbookVersion>() {
                           @Override
                           public CookbookVersion call() throws Exception {
                              return api.getCookbook(cookbookDef.getName(), version.getVersion());
                           }
                        });
                     }
                  }));

            logger.trace(String.format("getting versions of cookbook %s: ", cookbookDef.getName()));
            return getUnchecked(futures);
         }
      }));
   }
}
