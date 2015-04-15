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
package org.jclouds.oauth.v2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.oauth.v2.OAuthTestUtils.setCredential;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.oauth.v2.config.OAuthScopes.SingleScope;
import static org.jclouds.providers.AnonymousProviderMetadata.forApiOnEndpoint;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

@Test(groups = "live", singleThreaded = true)
public class AuthorizationApiLiveTest extends BaseApiLiveTest<AuthorizationApi> {

   private final String jwsAlg = "RS256";
   private String scope;
   private String audience;

   public AuthorizationApiLiveTest() {
      provider = "oauth";
   }

   public void authenticateJWTToken() throws Exception {
      long now = System.currentTimeMillis() / 1000;
      Claims claims = Claims.create(
            identity, // iss
            scope, // scope
            audience, // aud
            now + 3600, // exp
            now // iat
      );

      Token token = api.authorize(claims);

      assertNotNull(token, "no token when authorizing " + claims);
   }

   /** OAuth isn't registered as a provider intentionally, so we fake one. */
   @Override protected ProviderMetadata createProviderMetadata() {
      return forApiOnEndpoint(AuthorizationApi.class, endpoint).toBuilder().id("oauth").build();
   }

   @Override protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(JWS_ALG, jwsAlg);
      credential = setCredential(props, "oauth.credential");
      audience = checkNotNull(setIfTestSystemPropertyPresent(props, AUDIENCE), "test.jclouds.oauth.audience");
      scope = checkNotNull(setIfTestSystemPropertyPresent(props, "jclouds.oauth.scope"), "test.jclouds.oauth.scope");
      return props;
   }

   @Override protected Iterable<Module> setupModules() {
      return ImmutableList.<Module>builder() //
            .add(new OAuthModule()) //
            .add(new Module() {
               @Override public void configure(Binder binder) {
                  // ContextBuilder erases oauth.endpoint, as that's the same name as the provider key.
                  binder.bindConstant().annotatedWith(Names.named("oauth.endpoint")).to(endpoint);
                  binder.bind(OAuthScopes.class).toInstance(SingleScope.create(scope));
               }
            }).addAll(super.setupModules()).build();
   }
}

