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

import java.io.IOException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.TransientApiMetadata;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.functions.ClientForTag;
import org.jclouds.chef.functions.RunListForTag;
import org.jclouds.chef.statements.InstallChefGems;
import org.jclouds.chef.test.TransientChefApi;
import org.jclouds.chef.test.TransientChefAsyncApi;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.io.InputSuppliers;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.config.BinderUtils;
import org.jclouds.rest.config.RestModule;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.inject.AbstractModule;
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
      // forward all requests from TransientChefApi to ChefAsyncApi.  needs above binding as cannot proxy a class
      BinderUtils.bindClient(binder(), TransientChefApi.class, ChefAsyncApi.class, ImmutableMap.<Class<?>, Class<?>>of());
      bind(ChefApi.class).to(TransientChefApi.class);

      bind(TransientAsyncBlobStore.class).annotatedWith(Names.named("databags")).toInstance(
               ContextBuilder.newBuilder(new TransientApiMetadata()).modules(
                        ImmutableSet.<Module> of(new ExecutorServiceModule(MoreExecutors.sameThreadExecutor(),
                                 MoreExecutors.sameThreadExecutor()))).buildInjector().getInstance(
                        TransientAsyncBlobStore.class));
      
      bind(Statement.class).annotatedWith(Names.named("installChefGems")).to(InstallChefGems.class);
   }
   
   @Provides
   @Singleton
   public PrivateKey provideKey(Crypto crypto, @Credential String pem) throws InvalidKeySpecException,
            IOException {
        return crypto.rsaKeyFactory().generatePrivate(Pems.privateKeySpec(InputSuppliers.of(pem)));
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
