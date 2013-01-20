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

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.strategy.ListCookbookVersions;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class ListCookbookVersionsImpl implements ListCookbookVersions {

   protected final ChefApi chefApi;
   protected final ChefAsyncApi chefAsyncApi;
   protected final ListeningExecutorService userExecutor;
   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   ListCookbookVersionsImpl(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         ChefApi getAllCookbookVersion, ChefAsyncApi ablobstore) {
      this.userExecutor = userExecutor;
      this.chefAsyncApi = ablobstore;
      this.chefApi = getAllCookbookVersion;
   }

   @Override
   public Iterable<? extends CookbookVersion> execute() {
      return execute(chefApi.listCookbooks());
   }

   @Override
   public Iterable<? extends CookbookVersion> execute(Predicate<String> cookbookNameSelector) {
      return execute(filter(chefApi.listCookbooks(), cookbookNameSelector));
   }

   @Override
   public Iterable<? extends CookbookVersion> execute(Iterable<String> toGet) {
      return concat(transform(toGet, new Function<String, Iterable<? extends CookbookVersion>>() {

         @Override
         public Iterable<? extends CookbookVersion> apply(final String cookbook) {
            // TODO getting each version could also go parallel
            return transformParallel(chefApi.getVersionsOfCookbook(cookbook),
                  new Function<String, ListenableFuture<? extends CookbookVersion>>() {
                     public ListenableFuture<CookbookVersion> apply(String version) {
                        return chefAsyncApi.getCookbook(cookbook, version);
                     }
                  }, userExecutor, maxTime, logger, "getting versions of cookbook " + cookbook);
         }

      }));
   }

}
