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
import com.google.common.base.Joiner;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.Node;
import org.jclouds.logging.Logger;

import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;

public abstract class BaseListNodesImpl {

   protected final ChefApi api;

   protected Logger logger = Logger.NULL;

   BaseListNodesImpl(ChefApi api) {
      this.api = checkNotNull(api, "api");
   }

   protected Iterable<? extends Node> execute(Iterable<String> toGet) {
      Iterable<? extends Node> nodes = transform(toGet, new Function<String, Node>() {
               @Override
               public Node apply(final String input) {
                  return api.getNode(input);
               }
            }
      );

      logger.trace(String.format("getting nodes: %s", Joiner.on(',').join(toGet)));
      return nodes;

   }

   protected Iterable<? extends Node> executeConcurrently(final ListeningExecutorService executor,
         Iterable<String> toGet) {
      ListenableFuture<List<Node>> futures = allAsList(transform(toGet, new Function<String, ListenableFuture<Node>>() {
         @Override
         public ListenableFuture<Node> apply(final String input) {
            return executor.submit(new Callable<Node>() {
               @Override
               public Node call() throws Exception {
                  return api.getNode(input);
               }
            });
         }
      }));

      logger.trace(String.format("getting nodes: %s", Joiner.on(',').join(toGet)));
      return getUnchecked(futures);
   }

}
