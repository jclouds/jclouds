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

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.logging.Logger;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;

public abstract class BaseListCookbookVersionsImpl {

   protected final ChefApi api;

   protected Logger logger = Logger.NULL;

   BaseListCookbookVersionsImpl(ChefApi api) {
      this.api = checkNotNull(api, "api");
   }

   protected Iterable<? extends CookbookVersion> execute(Iterable<String> toGet) {
      return concat(transform(toGet, new Function<String, Iterable<? extends CookbookVersion>>() {

         @Override
         public Iterable<? extends CookbookVersion> apply(final String cookbook) {
            // TODO getting each version could also go parallel
            Set<String> cookbookVersions = api.listVersionsOfCookbook(cookbook);
            Iterable<? extends CookbookVersion> cookbooksVersions = transform(cookbookVersions,
                  new Function<String, CookbookVersion>() {
                     @Override
                     public CookbookVersion apply(final String version) {
                        return api.getCookbook(cookbook, version);
                     }
                  }
            );

            logger.trace(String.format("getting versions of cookbook: %s", cookbook));
            return cookbooksVersions;
         }
      }));

   }

   protected Iterable<? extends CookbookVersion> executeConcurrently(final ListeningExecutorService executor,
         Iterable<String> cookbookNames) {
      return concat(transform(cookbookNames, new Function<String, Iterable<? extends CookbookVersion>>() {

         @Override
         public Iterable<? extends CookbookVersion> apply(final String cookbook) {
            // TODO getting each version could also go parallel
            Set<String> cookbookVersions = api.listVersionsOfCookbook(cookbook);
            ListenableFuture<List<CookbookVersion>> futures = allAsList(transform(cookbookVersions,
                  new Function<String, ListenableFuture<CookbookVersion>>() {
                     @Override
                     public ListenableFuture<CookbookVersion> apply(final String version) {
                        return executor.submit(new Callable<CookbookVersion>() {
                           @Override
                           public CookbookVersion call() throws Exception {
                              return api.getCookbook(cookbook, version);
                           }
                        });
                     }
                  }
            ));

            logger.trace(String.format("getting versions of cookbook: %s", cookbook));
            return getUnchecked(futures);
         }
      }));
   }

}
