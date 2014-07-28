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

import static com.google.common.collect.Iterables.transform;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.config.ChefProperties;
import org.jclouds.chef.domain.CookbookDefinition;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.strategy.ListCookbookVersionsInEnvironment;
import org.jclouds.logging.Logger;

import java.util.concurrent.ExecutorService;

@Singleton
public class ListCookbookVersionsInEnvironmentImpl extends BaseListCookbookVersionsImpl
      implements ListCookbookVersionsInEnvironment {

   @Resource
   @Named(ChefProperties.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   ListCookbookVersionsInEnvironmentImpl(ChefApi api) {
      super(api);
   }

   @Override
   public Iterable<? extends CookbookVersion> execute(String environmentName) {
      return super.execute(transform(api.listCookbooksInEnvironment(environmentName),
            new Function<CookbookDefinition, String>() {

               @Override
               public String apply(CookbookDefinition cookbookDefinition) {
                  return cookbookDefinition.getName();
               }
            }
      ));
   }

   @Override
   public Iterable<? extends CookbookVersion> execute(String environmentName, String numVersions) {
      return super.execute(transform(api.listCookbooksInEnvironment(environmentName, numVersions),
            new Function<CookbookDefinition, String>() {

               @Override
               public String apply(CookbookDefinition cookbookDefinition) {
                  return cookbookDefinition.getName();
               }
            }
      ));
   }

   @Override
   public Iterable<? extends CookbookVersion> execute(ExecutorService executor,
         String environmentName) {
      return this.executeConcurrently(MoreExecutors.listeningDecorator(executor), environmentName);
   }

   @Override
   public Iterable<? extends CookbookVersion> execute(ExecutorService executor,
         String environmentName, String numVersions) {
      return this.executeConcurrently(MoreExecutors.listeningDecorator(executor), environmentName, numVersions);
   }


   private Iterable<? extends CookbookVersion> executeConcurrently(ListeningExecutorService executor,
         String environmentName) {
      return super.execute(
            transform(api.listCookbooksInEnvironment(environmentName), new Function<CookbookDefinition, String>() {

               @Override
               public String apply(CookbookDefinition cookbookDefinition) {
                  return cookbookDefinition.getName();
               }
            })
      );
   }


   private Iterable<? extends CookbookVersion> executeConcurrently(ListeningExecutorService executor,
         String environmentName, String numVersions) {
      return super.execute(transform(api.listCookbooksInEnvironment(environmentName, numVersions),
            new Function<CookbookDefinition, String>() {

               @Override
               public String apply(CookbookDefinition cookbookDefinition) {
                  return cookbookDefinition.getName();
               }
            }
      ));
   }

}
