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
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.oauth.v2.config.OAuthProperties.RESOURCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.CERTIFICATE;
import static org.jclouds.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;
import static org.jclouds.providers.AnonymousProviderMetadata.forApiOnEndpoint;
import static org.jclouds.utils.TestUtils.NO_INVOCATIONS;
import static org.jclouds.utils.TestUtils.SINGLE_NO_ARG_INVOCATION;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.UUID;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.oauth.v2.config.CredentialType;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.config.OAuthScopes.SingleScope;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.ClientCredentialsClaims;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.DataProvider;
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
   private String credentialType;
   private String resource;
   private String certificate;

   public AuthorizationApiLiveTest() {
      provider = "oauth";
   }

   @DataProvider
   public Object[][] onlyRunForP12PrivateKeyCredentials() {
      return (CredentialType.fromValue(credentialType) == CredentialType.P12_PRIVATE_KEY_CREDENTIALS) ?
            SINGLE_NO_ARG_INVOCATION : NO_INVOCATIONS;
   }

   @DataProvider
   public Object[][] onlyRunForClientCredentialsSecret() {
      return (CredentialType.fromValue(credentialType) == CredentialType.CLIENT_CREDENTIALS_SECRET) ?
              SINGLE_NO_ARG_INVOCATION : NO_INVOCATIONS;
   }

   @DataProvider
   public Object[][] onlyRunForClientCredentialsP12() {
      return (CredentialType.fromValue(credentialType) == CredentialType.CLIENT_CREDENTIALS_P12_AND_CERTIFICATE) ?
              SINGLE_NO_ARG_INVOCATION : NO_INVOCATIONS;
   }

   @Test(dataProvider = "onlyRunForP12PrivateKeyCredentials")
   public void authenticateP12PrivateKeyCredentialsTest() throws Exception {
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

   @Test(dataProvider = "onlyRunForClientCredentialsSecret")
   public void authenticateClientCredentialsSecretTest() throws Exception {
      Token token = api.authorizeClientSecret(identity, credential, resource, scope);

      assertNotNull(token, "no token when authorizing " + identity);
   }

   @Test(dataProvider = "onlyRunForClientCredentialsSecret")
   public void authenticateClientCredentialsSecretNullScopeTest() throws Exception {
      Token token = api.authorizeClientSecret(identity, credential, resource, null);

      assertNotNull(token, "no token when authorizing " + identity);
   }

   @Test(dataProvider = "onlyRunForClientCredentialsP12")
   public void authenticateClientCredentialsP12Test() throws Exception {
      long now = System.currentTimeMillis() / 1000;
      ClientCredentialsClaims claims = ClientCredentialsClaims.create(
              identity, // iss
              identity, // sub
              audience, // aud
              now + 3600, // exp
              now, // iat
              UUID.randomUUID().toString()
      );

      Token token = api.authorize(identity, claims, resource, null);

      assertNotNull(token, "no token when authorizing " + claims);
   }

   /** OAuth isn't registered as a provider intentionally, so we fake one. */
   @Override protected ProviderMetadata createProviderMetadata() {
      return forApiOnEndpoint(AuthorizationApi.class, endpoint).toBuilder().id("oauth").build();
   }

   @Override protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(JWS_ALG, jwsAlg);

      // scope is required for P12_PRIVATE_KEY_CREDENTIALS, optional for CLIENT_CREDENTIALS_SECRET.
      // Moved the not-NULL check to P12_PRIVATE_KEY_CREDENTIALS specific parameters.
      scope = setIfTestSystemPropertyPresent(props, "jclouds.oauth.scope");

      // Determine which type of Credential to use, default to P12_PRIVATE_KEY_CREDENTIALS
      credentialType = setIfTestSystemPropertyPresent(props, CREDENTIAL_TYPE);
      if (credentialType == null) {
         credentialType = CredentialType.P12_PRIVATE_KEY_CREDENTIALS.toString();
         props.setProperty(CREDENTIAL_TYPE, credentialType);
      }

      // Set the credential specific properties.
      if (CredentialType.fromValue(credentialType) == CredentialType.CLIENT_CREDENTIALS_SECRET) {
         resource = checkNotNull(setIfTestSystemPropertyPresent(props, RESOURCE), "test." + RESOURCE);
      } else if (CredentialType.fromValue(credentialType) == CredentialType.CLIENT_CREDENTIALS_P12_AND_CERTIFICATE) {
         audience = checkNotNull(setIfTestSystemPropertyPresent(props, AUDIENCE), "test.jclouds.oauth.audience");
         resource = checkNotNull(setIfTestSystemPropertyPresent(props, RESOURCE), "test." + RESOURCE);
         certificate = setCredential(props, CERTIFICATE);
         credential = setCredential(props, "oauth.credential");
      } else if (CredentialType.fromValue(credentialType) == CredentialType.P12_PRIVATE_KEY_CREDENTIALS) {
         audience = checkNotNull(setIfTestSystemPropertyPresent(props, AUDIENCE), "test.jclouds.oauth.audience");
         credential = setCredential(props, "oauth.credential");
         checkNotNull(scope, "test.jclouds.oauth.scope");
      }

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

