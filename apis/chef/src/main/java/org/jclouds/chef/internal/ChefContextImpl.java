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
package org.jclouds.chef.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.lifecycle.Closer;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.Utils;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Singleton
public class ChefContextImpl extends RestContextImpl<ChefApi, ChefAsyncApi> implements ChefContext {
   private final ChefService chefService;

   @Inject
   protected ChefContextImpl(ProviderMetadata providerMetadata, @Identity String identity, Utils utils, Closer closer,
            Injector injector, ChefService chefService) {
      super(providerMetadata, identity, utils, closer, injector, TypeLiteral.get(ChefApi.class), TypeLiteral
               .get(ChefAsyncApi.class));
      this.chefService = chefService;
   }

   @Override
   public ChefService getChefService() {
      return chefService;
   }

}
