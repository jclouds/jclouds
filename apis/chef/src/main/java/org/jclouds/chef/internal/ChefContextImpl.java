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

import java.net.URI;
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
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.rest.Utils;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.base.Supplier;
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
            @Provider Supplier<URI> endpoint, @Provider String provider, @Identity String identity,
            @ApiVersion String apiVersion, @BuildVersion String buildVersion, @Iso3166 Set<String> iso3166Codes,
            ChefService chefService) {
      super(closer, credentialStore, utils, injector, TypeLiteral.get(ChefClient.class), TypeLiteral
               .get(ChefAsyncClient.class), endpoint, buildVersion, buildVersion, buildVersion, buildVersion,
               iso3166Codes);
      this.chefService = chefService;
   }

   @Override
   public ChefService getChefService() {
      return chefService;
   }

}
