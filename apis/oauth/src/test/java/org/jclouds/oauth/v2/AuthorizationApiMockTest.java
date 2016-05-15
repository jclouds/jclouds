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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64Url;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.oauth.v2.config.CredentialType.P12_PRIVATE_KEY_CREDENTIALS;
import static org.jclouds.oauth.v2.config.CredentialType.CLIENT_CREDENTIALS_SECRET;
import static org.jclouds.oauth.v2.config.CredentialType.CLIENT_CREDENTIALS_P12_AND_CERTIFICATE;
import static org.jclouds.oauth.v2.config.OAuthProperties.CERTIFICATE;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.oauth.v2.config.OAuthProperties.RESOURCE;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.oauth.v2.config.CredentialType;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.config.OAuthScopes.SingleScope;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.ClientCredentialsClaims;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.rest.AnonymousHttpApiMetadata;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.BaseEncoding;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "OAuthApiMockTest")
public class AuthorizationApiMockTest {
   private static final String SCOPE = "https://www.googleapis.com/auth/prediction";

   private static final String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";

   private static final String claims = "{\"iss\":\"761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer"
         + ".gserviceaccount.com\",\"scope\":\"" + SCOPE + "\",\"aud\":\"https://accounts.google"
         + ".com/o/oauth2/token\",\"exp\":1328573381,\"iat\":1328569781}";

   private static final Token TOKEN = Token.create("1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M", "Bearer", 3600);

   private static final Claims CLAIMS = Claims.create(
         "761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer.gserviceaccount.com", // iss
         SCOPE, // scope
         "https://accounts.google.com/o/oauth2/token", // aud
         1328573381, // exp
         1328569781 // iat
         );

   private static final String clientCredentialsHeader = "{\"alg\":\"RS256\",\"typ\":\"JWT\",\"x5t\":\"RZk6mx4gGECvF6XWZWkK9qaGdHk=\"}";
   private static final String clientCredentialsClaims = "{\"iss\":\"a242b44e-2c2a-3bdd-b094-6152da263c54\"," +
         "\"sub\":\"a242b44e-2c2a-3bdd-b094-6152da263c54\",\"aud\":" +
         "\"https://login.microsoftonline.com/a242ccee-1a1a-3bdd-b094-6152da263c54/oauth2/token\"" +
         ",\"exp\":1328573381,\"nbf\":1328569781,\"jti\":\"abcdefgh\"}";

   private static final ClientCredentialsClaims CLIENT_CREDENTIALS_CLAIMS = ClientCredentialsClaims.create(
           "a242b44e-2c2a-3bdd-b094-6152da263c54", // iss
           "a242b44e-2c2a-3bdd-b094-6152da263c54", // sub
           "https://login.microsoftonline.com/a242ccee-1a1a-3bdd-b094-6152da263c54/oauth2/token", //aud
           1328573381, // exp
           1328569781, // nbf
           "abcdefgh" // jti
   );

   public void testGenerateJWTRequest() throws Exception {
      MockWebServer server = new MockWebServer();

      try {
         server.enqueue(new MockResponse().setBody("{\n"
                 + "  \"access_token\" : \"1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M\",\n"
                 + "  \"token_type\" : \"Bearer\",\n" + "  \"expires_in\" : 3600\n" + "}"));
         server.play();

         AuthorizationApi api = api(server.getUrl("/"), P12_PRIVATE_KEY_CREDENTIALS);

         assertEquals(api.authorize(CLAIMS), TOKEN);

         RecordedRequest request = server.takeRequest();
         assertEquals(request.getMethod(), "POST");
         assertEquals(request.getHeader("Accept"), APPLICATION_JSON);
         assertEquals(request.getHeader("Content-Type"), "application/x-www-form-urlencoded");

         assertEquals(
                 new String(request.getBody(), UTF_8), //
                 "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&"
                         +
                         // Base64 Encoded Header
                         "assertion="
                         + Joiner.on('.').join(encoding.encode(header.getBytes(UTF_8)),
                         encoding.encode(claims.getBytes(UTF_8)),
                         // Base64 encoded {header}.{claims} signature (using
                         // SHA256)
                         "W2Lesr_98AzVYiMbzxFqmwcOjpIWlwqkC6pNn1fXND9oSDNNnFhy-AAR6DKH-x9ZmxbY80"
                                 + "R5fH-OCeWumXlVgceKN8Z2SmgQsu8ElTpypQA54j_5j8vUImJ5hsOUYPeyF1U2BUzZ3L5g"
                                 + "03PXBA0YWwRU9E1ChH28dQBYuGiUmYw"));
      } finally {
         server.shutdown();
      }
   }

   public void testAuthorizationExceptionIsPopulatedOn4xx() throws Exception {
      MockWebServer server = new MockWebServer();
      try {
         server.enqueue(new MockResponse().setResponseCode(400));
         server.play();

         AuthorizationApi api = api(server.getUrl("/"), P12_PRIVATE_KEY_CREDENTIALS);
         api.authorize(CLAIMS);
         fail("An AuthorizationException should have been raised");
      } catch (AuthorizationException ex) {
         // Success
      } finally {
         server.shutdown();
      }
   }

   public void testGenerateClientSecretRequest() throws Exception {
      MockWebServer server = new MockWebServer();

      String credential = "password";
      String identity = "user";
      String resource = "http://management.azure.com/";
      String encoded_resource = "http%3A//management.azure.com/";

      try {
         server.enqueue(new MockResponse().setBody("{\n"
                 + "  \"access_token\" : \"1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M\",\n"
                 + "  \"token_type\" : \"Bearer\",\n" + "  \"expires_in\" : 3600\n" + "}"));
         server.play();

         AuthorizationApi api = api(server.getUrl("/"), CLIENT_CREDENTIALS_SECRET);

         assertEquals(api.authorizeClientSecret(identity, credential, resource, null), TOKEN);

         RecordedRequest request = server.takeRequest();
         assertEquals(request.getMethod(), "POST");
         assertEquals(request.getHeader("Accept"), APPLICATION_JSON);
         assertEquals(request.getHeader("Content-Type"), "application/x-www-form-urlencoded");

         assertEquals(
                 new String(request.getBody(), UTF_8), //
                 "grant_type=client_credentials&client_id=" + identity
                         + "&client_secret=" + credential
                         + "&resource=" + encoded_resource);
      } finally {
         server.shutdown();
      }
   }

   public void testGenerateClientCredentialsJWTRequest() throws Exception {
      MockWebServer server = new MockWebServer();

      String identity = "user";
      String resource = "http://management.azure.com/";
      String encoded_resource = "http%3A//management.azure.com/";

      try {
         server.enqueue(new MockResponse().setBody("{\n"
                 + "  \"access_token\" : \"1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M\",\n"
                 + "  \"token_type\" : \"Bearer\",\n" + "  \"expires_in\" : 3600\n" + "}"));
         server.play();

         AuthorizationApi api = api(server.getUrl("/"), CLIENT_CREDENTIALS_P12_AND_CERTIFICATE);
         assertEquals(api.authorize(identity, CLIENT_CREDENTIALS_CLAIMS, resource, null), TOKEN);

         RecordedRequest request = server.takeRequest();
         assertEquals(request.getMethod(), "POST");
         assertEquals(request.getHeader("Accept"), APPLICATION_JSON);
         assertEquals(request.getHeader("Content-Type"), "application/x-www-form-urlencoded");

         assertEquals(
                 new String(request.getBody(), UTF_8),
                 "grant_type=client_credentials&" +
                         "client_assertion_type=urn%3Aietf%3Aparams%3Aoauth%3Aclient-assertion-type%3Ajwt-bearer&" +
                         "client_id=" + identity + "&" +
                         "client_assertion=" +
                         Joiner.on(".").join(encoding.encode(clientCredentialsHeader.getBytes(UTF_8)),
                                 encoding.encode(clientCredentialsClaims.getBytes(UTF_8)),
                                 "ip3i0YLlunb4iq8sUMlpYDKnEuzmvlLpF4NQvn_aiysO5cuT5QHuGREq" +
                                         "gyEa-UMhfZoW49ggUWjS7YBT00r64cFE3dovaNMiZYZuVWu_" +
                                         "FpqO2QlwV7uXqhaRIE0cyabbKG44YJwA-NE4rtFZedFMo94F" +
                                         "6aOz2FN3en8zS9UVqmM"
                                 ) + "&" +
                         "resource=" + encoded_resource);
      } finally {
         server.shutdown();
      }
   }

   private final BaseEncoding encoding = base64Url().omitPadding();

   private AuthorizationApi api(URL url, CredentialType credentialType) throws IOException {
      Properties overrides = new Properties();
      overrides.setProperty("oauth.endpoint", url.toString());
      overrides.setProperty(JWS_ALG, "RS256");
      overrides.setProperty(CREDENTIAL_TYPE, credentialType.toString());
      overrides.setProperty(AUDIENCE, "https://accounts.google.com/o/oauth2/token");
      overrides.setProperty(RESOURCE, "https://management.azure.com/");
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      overrides.setProperty(CERTIFICATE, toStringAndClose(OAuthTestUtils.class.getResourceAsStream("/testcert.pem")));

      return ContextBuilder.newBuilder(AnonymousHttpApiMetadata.forApi(AuthorizationApi.class))
            .credentials("foo", toStringAndClose(OAuthTestUtils.class.getResourceAsStream("/testpk.pem")))
            .endpoint(url.toString())
            .overrides(overrides)
            .modules(ImmutableSet.of(new ExecutorServiceModule(sameThreadExecutor()), new OAuthModule(), new Module() {
               @Override public void configure(Binder binder) {
                  binder.bind(OAuthScopes.class).toInstance(SingleScope.create(SCOPE));
               }
            }))
            .buildApi(AuthorizationApi.class);
   }
}
