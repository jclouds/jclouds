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
package org.jclouds.chef.config;

import static com.google.common.base.Suppliers.compose;
import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.chef.config.ChefProperties.CHEF_VALIDATOR_CREDENTIAL;
import static org.jclouds.chef.config.ChefProperties.CHEF_VALIDATOR_NAME;
import static org.jclouds.crypto.Pems.privateKeySpec;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.domain.BootstrapConfig;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.functions.BootstrapConfigForGroup;
import org.jclouds.chef.functions.ClientForGroup;
import org.jclouds.chef.handlers.ChefApiErrorRetryHandler;
import org.jclouds.chef.handlers.ChefErrorHandler;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteSource;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * Configures the Chef connection.
 */
@ConfiguresHttpApi
public abstract class BaseChefHttpApiModule<S> extends HttpApiModule<S> {

   @Provides
   @TimeStamp
   protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
      return cache.get();
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @TimeStamp
   Supplier<String> provideTimeStampCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds, final DateService dateService) {
      return memoizeWithExpiration(new Supplier<String>() {
         @Override
         public String get() {
            return dateService.iso8601SecondsDateFormat();
         }
      }, seconds, TimeUnit.SECONDS);
   }

   // TODO: potentially change this
   @Provides
   @Singleton
   public Supplier<PrivateKey> supplyKey(final LoadingCache<Credentials, PrivateKey> keyCache,
         @org.jclouds.location.Provider final Supplier<Credentials> creds) {
      return compose(new Function<Credentials, PrivateKey>() {
         @Override
         public PrivateKey apply(Credentials in) {
            return keyCache.getUnchecked(in);
         }
      }, creds);
   }

   @Provides
   @Singleton
   LoadingCache<Credentials, PrivateKey> privateKeyCache(PrivateKeyForCredentials loader) {
      // throw out the private key related to old credentials
      return CacheBuilder.newBuilder().maximumSize(2).build(loader);
   }

   /**
    * it is relatively expensive to extract a private key from a PEM. cache the
    * relationship between current credentials so that the private key is only
    * recalculated once.
    */
   @VisibleForTesting
   @Singleton
   private static class PrivateKeyForCredentials extends CacheLoader<Credentials, PrivateKey> {
      private final Crypto crypto;

      @Inject
      private PrivateKeyForCredentials(Crypto crypto) {
         this.crypto = crypto;
      }

      @Override
      public PrivateKey load(Credentials in) {
         try {
            return crypto.rsaKeyFactory().generatePrivate(
                  privateKeySpec(ByteSource.wrap(in.credential.getBytes(Charsets.UTF_8))));
         } catch (InvalidKeySpecException e) {
            throw propagate(e);
         } catch (IOException e) {
            throw propagate(e);
         }
      }
   }

   @Provides
   @Singleton
   @Validator
   public Optional<String> provideValidatorName(Injector injector) {
      // Named properties can not be injected as optional here, so let's use the
      // injector to bypass it
      Key<String> key = Key.get(String.class, Names.named(CHEF_VALIDATOR_NAME));
      try {
         return Optional.<String> of(injector.getInstance(key));
      } catch (ConfigurationException ex) {
         return Optional.<String> absent();
      }
   }

   @Provides
   @Singleton
   @Validator
   public Optional<PrivateKey> provideValidatorCredential(Crypto crypto, Injector injector)
         throws InvalidKeySpecException, IOException {
      // Named properties can not be injected as optional here, so let's use the
      // injector to bypass it
      Key<String> key = Key.get(String.class, Names.named(CHEF_VALIDATOR_CREDENTIAL));
      try {
         String validatorCredential = injector.getInstance(key);
         PrivateKey validatorKey = crypto.rsaKeyFactory().generatePrivate(
               Pems.privateKeySpec(ByteSource.wrap(validatorCredential.getBytes(Charsets.UTF_8))));
         return Optional.<PrivateKey> of(validatorKey);
      } catch (ConfigurationException ex) {
         return Optional.<PrivateKey> absent();
      }
   }

   @Provides
   @Singleton
   CacheLoader<String, BootstrapConfig> bootstrapConfigForGroup(BootstrapConfigForGroup bootstrapConfigForGroup) {
      return CacheLoader.from(bootstrapConfigForGroup);
   }

   @Provides
   @Singleton
   CacheLoader<String, Client> groupToClient(ClientForGroup clientForGroup) {
      return CacheLoader.from(clientForGroup);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ChefErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ChefErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ChefErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(ChefApiErrorRetryHandler.class);
   }

}
