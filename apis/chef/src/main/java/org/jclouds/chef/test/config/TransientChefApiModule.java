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

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.rest.config.BinderUtils.bindBlockingApi;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.inject.Singleton;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.LocalAsyncBlobStore;
import org.jclouds.blobstore.TransientApiMetadata;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.config.Validator;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.functions.ClientForGroup;
import org.jclouds.chef.functions.BootstrapConfigForGroup;
import org.jclouds.chef.functions.RunListForGroup;
import org.jclouds.chef.test.TransientChefApi;
import org.jclouds.chef.test.TransientChefAsyncApi;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.crypto.Crypto;
import org.jclouds.domain.JsonBall;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestModule;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.statements.chef.InstallChefGems;

import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class TransientChefApiModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new RestModule());
      bind(ChefAsyncApi.class).to(TransientChefAsyncApi.class).asEagerSingleton();
      // forward all requests from TransientChefApi to ChefAsyncApi. needs above
      // binding as cannot proxy a class
      bindBlockingApi(binder(), TransientChefApi.class, ChefAsyncApi.class);
      bind(ChefApi.class).to(TransientChefApi.class);

      bind(LocalAsyncBlobStore.class).annotatedWith(Names.named("databags"))
            .toInstance(
                  ContextBuilder
                        .newBuilder(new TransientApiMetadata())
                        .modules(
                              ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(),
                                    sameThreadExecutor()))).buildInjector().getInstance(LocalAsyncBlobStore.class));
      bind(Statement.class).annotatedWith(Names.named("installChefGems")).to(InstallChefGems.class);
   }

   @Provides
   @Singleton
   CacheLoader<String, List<String>> runListForGroup(RunListForGroup runListForGroup) {
      return CacheLoader.from(runListForGroup);
   }

   @Provides
   @Singleton
   CacheLoader<String, ? extends JsonBall> bootstrapConfigForGroup(BootstrapConfigForGroup bootstrapConfigForGroup) {
      return CacheLoader.from(bootstrapConfigForGroup);
   }

   @Provides
   @Singleton
   CacheLoader<String, Client> groupToClient(ClientForGroup clientForGroup) {
      return CacheLoader.from(clientForGroup);
   }

   @Provides
   @Singleton
   @Validator
   public Optional<String> provideValidatorName(Injector injector) {
      return Optional.absent();
   }

   @Provides
   @Singleton
   @Validator
   public Optional<PrivateKey> provideValidatorCredential(Crypto crypto, Injector injector)
         throws InvalidKeySpecException, IOException {
      return Optional.absent();
   }

}
