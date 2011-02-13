/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.chef.internal;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.domain.Credentials;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.Utils;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.location.Provider;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Singleton
public class ChefContextImpl extends RestContextImpl<ChefClient, ChefAsyncClient> implements ChefContext {
   private final ChefService chefService;

   @Inject
   protected ChefContextImpl(Closer closer, Map<String, Credentials> credentialStore, Utils utils, Injector injector,
         TypeLiteral<ChefClient> syncApi, TypeLiteral<ChefAsyncClient> asyncApi, @Provider URI endpoint,
         @Provider String provider, @Identity String identity, @ApiVersion String apiVersion, ChefService chefService) {
      super(closer, credentialStore, utils, injector, syncApi, asyncApi, endpoint, provider, identity, apiVersion, null ); // Not sure what needs to go here for Chef
      this.chefService = chefService;
   }

   @Override
   public ChefService getChefService() {
      return chefService;
   }

}
