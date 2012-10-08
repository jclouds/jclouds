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
package org.jclouds.chef.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.chef.config.ChefProperties.CHEF_VALIDATOR_CREDENTIAL;
import static org.jclouds.chef.config.ChefProperties.CHEF_VALIDATOR_NAME;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.handlers.ChefApiErrorRetryHandler;
import org.jclouds.chef.handlers.ChefErrorHandler;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.io.InputSuppliers;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * Configures the Chef connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class BaseChefRestClientModule<S, A> extends RestClientModule<S, A> {

   protected BaseChefRestClientModule(TypeToken<S> syncApiType, TypeToken<A> asyncApiType) {
      super(syncApiType, asyncApiType);
   }

   protected BaseChefRestClientModule(TypeToken<S> syncApiType, TypeToken<A> asyncApiType,
            Map<Class<?>, Class<?>> delegates) {
      super(syncApiType, asyncApiType, delegates);
   }

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
      return Suppliers.memoizeWithExpiration(new Supplier<String>() {
         public String get() {
            return dateService.iso8601SecondsDateFormat();
         }
      }, seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   public PrivateKey provideKey(Crypto crypto, @Credential String pem) throws InvalidKeySpecException,
            IOException {
        return crypto.rsaKeyFactory().generatePrivate(Pems.privateKeySpec(InputSuppliers.of(pem)));
   }
   
   @Provides
   @Singleton
   @Validator
   public Optional<String> provideValidatorName(Injector injector) {
	   // Named properties can not be injected as optional here, so let's use the injector to bypass it
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
	   // Named properties can not be injected as optional here, so let's use the injector to bypass it
	   Key<String> key = Key.get(String.class, Names.named(CHEF_VALIDATOR_CREDENTIAL));
	   try {
		   String validatorCredential = injector.getInstance(key);
		   PrivateKey validatorKey = crypto.rsaKeyFactory().generatePrivate(
				   Pems.privateKeySpec(InputSuppliers.of(validatorCredential)));
		   return Optional.<PrivateKey> of(validatorKey);
	   } catch (ConfigurationException ex) {
		   return Optional.<PrivateKey> absent();
	   }
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
