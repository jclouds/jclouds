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

import static com.google.common.collect.Iterables.filter;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.strategy.ListNodes;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class ListNodesImpl implements ListNodes {

   protected final ChefApi chefApi;
   protected final ChefAsyncApi chefAsyncApi;
   protected final ExecutorService userExecutor;
   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   ListNodesImpl(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor, ChefApi getAllNode,
            ChefAsyncApi ablobstore) {
      this.userExecutor = userExecutor;
      this.chefAsyncApi = ablobstore;
      this.chefApi = getAllNode;
   }

   @Override
   public Iterable<? extends Node> execute() {
      return execute(chefApi.listNodes());
   }

   @Override
   public Iterable<? extends Node> execute(Predicate<String> nodeNameSelector) {
      return execute(filter(chefApi.listNodes(), nodeNameSelector));
   }

   @Override
   public Iterable<? extends Node> execute(Iterable<String> toGet) {
      return transformParallel(toGet, new Function<String, Future<? extends Node>>() {

         @Override
         public Future<Node> apply(String from) {
            return chefAsyncApi.getNode(from);
         }

      }, userExecutor, maxTime, logger, "getting nodes");

   }

}
