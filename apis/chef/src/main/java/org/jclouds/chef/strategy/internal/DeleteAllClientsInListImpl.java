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

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Maps.newHashMap;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.strategy.DeleteAllClientsInList;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class DeleteAllClientsInListImpl implements DeleteAllClientsInList {

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
   DeleteAllClientsInListImpl(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor, ChefApi getAllApi,
         ChefAsyncApi ablobstore) {
      this.userExecutor = userExecutor;
      this.chefAsyncApi = ablobstore;
      this.chefApi = getAllApi;
   }

   @Override
   public void execute(Iterable<String> names) {
      Map<String, Exception> exceptions = newHashMap();
      Map<String, Future<?>> responses = newHashMap();
      for (String name : names) {
         responses.put(name, chefAsyncApi.deleteClient(name));
      }
      try {
         exceptions = awaitCompletion(responses, userExecutor, maxTime, logger,
               String.format("deleting apis: %s", names));
      } catch (TimeoutException e) {
         propagate(e);
      }
      if (exceptions.size() > 0)
         throw new RuntimeException(String.format("errors deleting clients: %s: %s", names, exceptions));
   }
}
