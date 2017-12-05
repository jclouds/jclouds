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
package org.jclouds.openstack.keystone.auth.config;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.location.Provider;
import org.jclouds.openstack.keystone.auth.AuthenticationApi;
import org.jclouds.openstack.keystone.auth.domain.AuthInfo;
import org.jclouds.openstack.keystone.auth.functions.AuthenticateApiAccessKeyCredentials;
import org.jclouds.openstack.keystone.auth.functions.AuthenticatePasswordCredentials;
import org.jclouds.openstack.keystone.auth.functions.AuthenticateTokenCredentials;
import org.jclouds.openstack.keystone.auth.handlers.RetryOnRenew;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.keystone.v2_0.auth.V2AuthenticationApi;
import org.jclouds.openstack.keystone.v3.auth.V3AuthenticationApi;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;

public class AuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(RetryOnRenew.class);
      bindHttpApi(binder(), V2AuthenticationApi.class);
      bindHttpApi(binder(), V3AuthenticationApi.class);
   }

   @Provides
   @Singleton
   protected final AuthenticationApi provideAuthenticationApi(Injector i,
         @Named(KeystoneProperties.KEYSTONE_VERSION) String keystoneVersion) {
      return authenticationApis(i).get(keystoneVersion);
   }

   protected Map<String, AuthenticationApi> authenticationApis(Injector i) {
      Map<String, AuthenticationApi> authenticationApis = Maps.newHashMap();
      authenticationApis.put("2", i.getInstance(V2AuthenticationApi.class));
      authenticationApis.put("3", i.getInstance(V3AuthenticationApi.class));
      return authenticationApis;
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @Singleton
   @Authentication
   protected final Supplier<String> provideAuthenticationTokenCache(final Supplier<AuthInfo> supplier)
         throws InterruptedException, ExecutionException, TimeoutException {
      return new Supplier<String>() {
         @Override
         public String get() {
            return supplier.get().getAuthToken();
         }
      };
   }

   @Provides
   @Singleton
   protected final Map<String, Function<Credentials, AuthInfo>> provideAuthenticationMethods(Injector i) {
      return authenticationMethods(i);
   }

   protected Map<String, Function<Credentials, AuthInfo>> authenticationMethods(Injector i) {
      Builder<Function<Credentials, AuthInfo>> fns = ImmutableSet.builder();
      fns.add(i.getInstance(AuthenticatePasswordCredentials.class));
      fns.add(i.getInstance(AuthenticateApiAccessKeyCredentials.class));
      fns.add(i.getInstance(AuthenticateTokenCredentials.class));
      return CredentialTypes.indexByCredentialType(fns.build());
   }

   @Provides
   @Singleton
   protected final Function<Credentials, AuthInfo> authenticationMethodForCredentialType(
         @Named(KeystoneProperties.CREDENTIAL_TYPE) String credentialType,
         Map<String, Function<Credentials, AuthInfo>> authenticationMethods) {
      checkArgument(authenticationMethods.containsKey(credentialType), "credential type %s not in supported list: %s",
            credentialType, authenticationMethods.keySet());
      return authenticationMethods.get(credentialType);
   }

   // TODO: what is the timeout of the session token? modify default accordingly
   // PROPERTY_SESSION_INTERVAL is default to 60 seconds, but we have this here
   // at 11 hours for now.
   @Provides
   @Singleton
   public final LoadingCache<Credentials, AuthInfo> provideAuthInfoCache(Function<Credentials, AuthInfo> getAccess) {
      return CacheBuilder.newBuilder().expireAfterWrite(11, TimeUnit.HOURS).build(CacheLoader.from(getAccess));
   }

   // Temporary conversion of a cache to a supplier until there is a
   // single-element cache
   // http://code.google.com/p/guava-libraries/issues/detail?id=872
   @Provides
   @Singleton
   protected final Supplier<AuthInfo> provideAuthInfoSupplier(final LoadingCache<Credentials, AuthInfo> cache,
         @Provider final Supplier<Credentials> creds) {
      return new Supplier<AuthInfo>() {
         @Override
         public AuthInfo get() {
            return cache.getUnchecked(creds.get());
         }
      };
   }
}
