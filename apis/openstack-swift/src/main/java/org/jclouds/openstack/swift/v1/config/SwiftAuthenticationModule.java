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
package org.jclouds.openstack.swift.v1.config;

import static org.jclouds.http.HttpUtils.releasePayload;
import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.openstack.v2_0.ServiceType.OBJECT_STORE;
import static org.jclouds.openstack.v2_0.reference.AuthHeaders.AUTH_TOKEN;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.io.Closeable;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;

import org.jclouds.ContextBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.AuthenticationApi;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.VirtualHost;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.name.Named;

/**
 * When {@link org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties#CREDENTIAL_TYPE} is set to {@code
 * tempAuthCredentials}, do not use Keystone. Instead, bridge TempAuth to Keystone by faking a service catalog out of
 * the storage url. The {@link ContextBuilder#endpoint(String) endpoint} must be set to the TempAuth url, usually ending
 * in {@code auth/v1.0/}.
 */
public final class SwiftAuthenticationModule extends KeystoneAuthenticationModule {
   private static final String STORAGE_USER = "X-Storage-User";
   private static final String STORAGE_PASS = "X-Storage-Pass";
   private static final String STORAGE_URL = "X-Storage-Url";

   @Override
   protected void configure() {
      super.configure();
      bindHttpApi(binder(), AuthenticationApi.class);
      bindHttpApi(binder(), TempAuthApi.class);
   }

   @Override protected Map<String, Function<Credentials, Access>> authenticationMethods(Injector i) {
      return ImmutableMap.<String, Function<Credentials, Access>>builder()
                         .putAll(super.authenticationMethods(i))
                         .put("tempAuthCredentials", i.getInstance(TempAuth.class)).build();
   }

   static final class TempAuth implements Function<Credentials, Access> {
      private final TempAuthApi delegate;

      @Inject TempAuth(TempAuthApi delegate) {
         this.delegate = delegate;
      }

      @Override public Access apply(Credentials input) {
         return delegate.auth(input.identity, input.credential);
      }
   }

   @VirtualHost
   interface TempAuthApi  extends Closeable {

      @Named("TempAuth")
      @GET
      @Consumes
      @ResponseParser(AdaptTempAuthResponseToAccess.class)
      Access auth(@HeaderParam(STORAGE_USER) String user, @HeaderParam(STORAGE_PASS) String key);
   }

   static final class AdaptTempAuthResponseToAccess
         implements Function<HttpResponse, Access>, InvocationContext<AdaptTempAuthResponseToAccess> {

      private final String apiVersion;

      private String host;
      private String username;

      @Inject AdaptTempAuthResponseToAccess(@ApiVersion String apiVersion) {
         this.apiVersion = apiVersion;
      }

      @Override public Access apply(HttpResponse from) {
         releasePayload(from);
         URI storageUrl = null;
         String authToken = null;
         for (Map.Entry<String, String> entry : from.getHeaders().entries()) {
            String header = entry.getKey();
            if (header.equalsIgnoreCase(STORAGE_URL)) {
               storageUrl = getURI(entry.getValue());
            } else if (header.equalsIgnoreCase(AUTH_TOKEN)) {
               authToken = entry.getValue();
            }
         }
         if (storageUrl == null || authToken == null) {
            throw new AuthorizationException("Invalid headers in TempAuth response " + from);
         }
         // For portability with keystone, based on common knowledge that these tokens tend to expire in 24 hours
         // http://docs.openstack.org/api/openstack-object-storage/1.0/content/authentication-object-dev-guide.html
         Date expires = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24));
         return Access.builder()
               .user(User.builder().id(username).name(username).build())
               .token(Token.builder().id(authToken).expires(expires).build())
               .service(Service.builder().name("Object Storage").type(OBJECT_STORE)
               .endpoint(Endpoint.builder().publicURL(storageUrl).id(apiVersion).region(storageUrl.getHost()).build())
               .build()).build();
      }

      // TODO: find the swift configuration or bug related to returning localhost
      private URI getURI(String headerValue) {
         if (headerValue == null)
            return null;
         URI toReturn = URI.create(headerValue);
         if (!"127.0.0.1".equals(toReturn.getHost()))
            return toReturn;
         return uriBuilder(toReturn).host(host).build();
      }

      @Override
      public AdaptTempAuthResponseToAccess setContext(HttpRequest request) {
         String host = request.getEndpoint().getHost();
         this.host = host;
         this.username = request.getFirstHeaderOrNull(STORAGE_USER);
         return this;
      }
   }
}
