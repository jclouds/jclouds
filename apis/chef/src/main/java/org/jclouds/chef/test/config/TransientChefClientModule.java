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
package org.jclouds.chef.test.config;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.TransientApiMetadata;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.config.BaseChefRestClientModule;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.functions.ClientForTag;
import org.jclouds.chef.functions.RunListForTag;
import org.jclouds.chef.statements.InstallChefGems;
import org.jclouds.chef.test.TransientChefAsyncClient;
import org.jclouds.chef.test.TransientChefClient;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
public class TransientChefClientModule extends BaseChefRestClientModule<TransientChefClient, ChefAsyncClient> {

   public TransientChefClientModule() {
      super(TypeToken.of(TransientChefClient.class), TypeToken.of(ChefAsyncClient.class));
   }

   @Override
   protected void configure() {
      bind(TransientAsyncBlobStore.class).annotatedWith(Names.named("databags")).toInstance(
               ContextBuilder.newBuilder(new TransientApiMetadata()).modules(
                        ImmutableSet.<Module> of(new ExecutorServiceModule(MoreExecutors.sameThreadExecutor(),
                                 MoreExecutors.sameThreadExecutor()))).buildInjector().getInstance(
                        TransientAsyncBlobStore.class));
      bind(Statement.class).annotatedWith(Names.named("installChefGems")).to(InstallChefGems.class);
      super.configure();
   }

   @Override
   protected void bindAsyncClient() {
      bind(ChefAsyncClient.class).to(TransientChefAsyncClient.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ChefClient provideClient(TransientChefClient in) {
      return in;
   }

   @Provides
   @Singleton
   Map<String, List<String>> runListForTag(RunListForTag runListForTag) {
      return new MapMaker().makeComputingMap(runListForTag);
   }

   @Provides
   @Singleton
   Map<String, Client> tagToClient(ClientForTag tagToClient) {
      return new MapMaker().makeComputingMap(tagToClient);
   }

}