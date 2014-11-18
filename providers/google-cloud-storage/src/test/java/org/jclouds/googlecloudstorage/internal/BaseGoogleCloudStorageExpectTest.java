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
package org.jclouds.googlecloudstorage.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.BaseEncoding.base64Url;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.jclouds.crypto.Pems.privateKeySpec;
import static org.jclouds.crypto.Pems.publicKeySpec;
import static org.jclouds.crypto.PemsTest.PRIVATE_KEY;
import static org.jclouds.crypto.PemsTest.PUBLIC_KEY;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;

import java.io.IOException;
import java.net.URI;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.MediaType;

import org.jclouds.crypto.Crypto;
import org.jclouds.googlecloudstorage.GoogleCloudStorageProviderMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.jclouds.oauth.v2.filters.JWTBearerTokenFlow;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class BaseGoogleCloudStorageExpectTest<T> extends BaseRestApiExpectTest<T> {
   protected static final String STORAGE_READONLY_SCOPE = "https://www.googleapis.com/auth/devstorage.read_only";

   protected static final String STORAGE_READWRITE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

   protected static final String STORAGE_FULLCONTROL_SCOPE = "https://www.googleapis.com/auth/devstorage.full_control";

   private static final String header = "{\"alg\":\"none\",\"typ\":\"JWT\"}";

   private static final String CLAIMS_TEMPLATE = "{" + "\"iss\":\"JcloudTest\"," + "\"scope\":\"%s\","
            + "\"aud\":\"https://accounts.google.com/o/oauth2/token\"," + "\"exp\":3600," + "\"iat\":0}";

   protected static final String TOKEN = "1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M";

   protected static final HttpResponse TOKEN_RESPONSE = HttpResponse
            .builder()
            .statusCode(200)
            .payload(payloadFromString("{\n" + "  \"access_token\" : \"" + TOKEN + "\",\n"
                     + "  \"token_type\" : \"Bearer\",\n" + "  \"expires_in\" : 3600\n" + "}")).build();

   protected BaseGoogleCloudStorageExpectTest() {
      provider = "google-cloud-storage";
   }

   @Override protected Module createModule() {
      return new Module() {
         @Override
         public void configure(Binder binder) {
            // Predictable time
            binder.bind(JWTBearerTokenFlow.class).to(JWTBearerTokenFlow.TestJWTBearerTokenFlow.class);
            try {
               KeyFactory keyfactory = KeyFactory.getInstance("RSA");
               PrivateKey privateKey = keyfactory.generatePrivate(privateKeySpec(ByteSource.wrap(PRIVATE_KEY
                        .getBytes(UTF_8))));
               PublicKey publicKey = keyfactory
                        .generatePublic(publicKeySpec(ByteSource.wrap(PUBLIC_KEY.getBytes(UTF_8))));
               KeyPair keyPair = new KeyPair(publicKey, privateKey);
               final Crypto crypto = createMock(Crypto.class);
               KeyPairGenerator rsaKeyPairGenerator = createMock(KeyPairGenerator.class);
               final SecureRandom secureRandom = createMock(SecureRandom.class);
               expect(crypto.rsaKeyPairGenerator()).andReturn(rsaKeyPairGenerator).anyTimes();
               rsaKeyPairGenerator.initialize(2048, secureRandom);
               expectLastCall().anyTimes();
               expect(rsaKeyPairGenerator.genKeyPair()).andReturn(keyPair).anyTimes();
               replay(crypto, rsaKeyPairGenerator, secureRandom);
               binder.bind(Crypto.class).toInstance(crypto);
               binder.bind(SecureRandom.class).toInstance(secureRandom);
            } catch (NoSuchAlgorithmException e) {
               propagate(e);
            } catch (InvalidKeySpecException e) {
               propagate(e);
            } catch (IOException e) {
               propagate(e);
            }
            // predictable node names
            final AtomicInteger suffix = new AtomicInteger();
            binder.bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(new Supplier<String>() {
               @Override
               public String get() {
                  return suffix.getAndIncrement() + "";
               }
            });
         }
      };
   }

   @Override protected Properties setupProperties() {
      Properties props = super.setupProperties();
      // use no sig algorithm for expect tests (means no credential is required either)
      props.put(JWS_ALG, "none");
      return props;
   }

   @Override protected ProviderMetadata createProviderMetadata(){
      return new GoogleCloudStorageProviderMetadata();
   }

   @Override
   protected HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      HttpRequestComparisonType reqType = HttpRequestComparisonType.DEFAULT;
      if (input.getPayload() != null) {
         if (MediaType.APPLICATION_JSON.toString().equals(input.getPayload().getContentMetadata().getContentType())) {
            reqType = HttpRequestComparisonType.JSON;
         }
      }
      return reqType;
   }

   protected HttpRequest requestForScopes(String... scopes) {
      String claims = String.format(CLAIMS_TEMPLATE, Joiner.on(",").join(scopes));

      String payload = "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&" +
      // Base64 Encoded Header
               "assertion=" + base64Url().omitPadding().encode(header.getBytes(UTF_8)) + "." +
               // Base64 Encoded Claims
               base64Url().omitPadding().encode(claims.getBytes(UTF_8)) + ".";

      return HttpRequest.builder().method("POST").endpoint(URI.create("https://accounts.google.com/o/oauth2/token"))
               .addHeader("Accept", MediaType.APPLICATION_JSON)
               .payload(payloadFromStringWithContentType(payload, "application/x-www-form-urlencoded")).build();
   }

   protected Payload staticPayloadFromResource(String resource) {
      return new ByteSourcePayload(Resources.asByteSource(Resources.getResource(getClass(), resource)));
   }
}
