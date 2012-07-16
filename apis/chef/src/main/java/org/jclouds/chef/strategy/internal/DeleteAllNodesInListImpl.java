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

import static com.google.common.collect.Maps.newHashMap;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.chef.strategy.DeleteAllNodesInList;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class DeleteAllNodesInListImpl implements DeleteAllNodesInList {

   protected final ChefApi chefApi;
   protected final ChefAsyncApi chefAsyncApi;
   protected final ExecutorService userExecutor;
   @Resource
   @Named(ChefConstants.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   DeleteAllNodesInListImpl(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
         ChefApi getAllNode, ChefAsyncApi ablobstore) {
      this.userExecutor = userExecutor;
      this.chefAsyncApi = ablobstore;
      this.chefApi = getAllNode;
   }

   @Override
   public void execute(Iterable<String> names) {
      Map<String, Exception> exceptions = newHashMap();
      Map<String, Future<?>> responses = newHashMap();
      for (String name : names) {
         responses.put(name, chefAsyncApi.deleteNode(name));
      }
      exceptions = awaitCompletion(responses, userExecutor, maxTime, logger, String.format(
            "deleting nodes: %s", names));
      if (exceptions.size() > 0)
         throw new RuntimeException(String.format("errors deleting nodes: %s: %s", names, exceptions));
   }
}
