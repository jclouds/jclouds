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
package org.jclouds.profitbricks.config;

import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.handlers.ProfitBricksHttpErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.SSLModule;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.ServiceFault;
import org.jclouds.profitbricks.http.ResponseStatusFromPayloadHttpCommandExecutorService;
import org.jclouds.profitbricks.http.parser.ServiceFaultResponseHandler;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the ProfitBricks connection.
 */
@ConfiguresHttpApi
public class ProfitBricksHttpApiModule extends HttpApiModule<ProfitBricksApi> {

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ProfitBricksHttpErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ProfitBricksHttpErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ProfitBricksHttpErrorHandler.class);
   }

   @ConfiguresHttpCommandExecutorService
   public static class ProfitBricksHttpCommandExecutorServiceModule extends AbstractModule {

      @Override
      protected void configure() {
	 install(new SSLModule());
	 bind(HttpCommandExecutorService.class).to(ResponseStatusFromPayloadHttpCommandExecutorService.class)
		 .in(Scopes.SINGLETON);
      }

      @Provides
      public ParseSax<ServiceFault> serviceFaultParser(ParseSax.Factory factory, Injector injector) {
	 return factory.create(injector.getInstance(ServiceFaultResponseHandler.class));
      }

   }

}
