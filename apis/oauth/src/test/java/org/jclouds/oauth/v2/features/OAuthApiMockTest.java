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
package org.jclouds.oauth.v2.features;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64Url;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.domain.Claims.EXPIRATION_TIME;
import static org.jclouds.oauth.v2.domain.Claims.ISSUED_AT;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.oauth.v2.OAuthApi;
import org.jclouds.oauth.v2.OAuthApiMetadata;
import org.jclouds.oauth.v2.OAuthTestUtils;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.BaseEncoding;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "OAuthApiMockTest")
public class OAuthApiMockTest {

   private static final String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";

   private static final String claims = "{\"iss\":\"761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer" +
         ".gserviceaccount.com\"," +
         "\"scope\":\"https://www.googleapis.com/auth/prediction\",\"aud\":\"https://accounts.google" +
         ".com/o/oauth2/token\",\"exp\":1328573381,\"iat\":1328569781}";

   private static final Token TOKEN = Token.create("1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M", "Bearer", 3600);

   private static final Map<String, Object> CLAIMS = ImmutableMap.<String, Object>builder()
         .put("iss", "761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer.gserviceaccount.com")
         .put("scope", "https://www.googleapis.com/auth/prediction")
         .put("aud", "https://accounts.google.com/o/oauth2/token")
         .put(EXPIRATION_TIME, 1328573381)
         .put(ISSUED_AT, 1328569781).build();

   private static final Header HEADER = Header.create("RS256", "JWT");

   public void testGenerateJWTRequest() throws Exception {
      MockWebServer server = new MockWebServer();
      server.enqueue(new MockResponse().setBody("{\n" +
                  "  \"access_token\" : \"1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M\",\n" +
                  "  \"token_type\" : \"Bearer\",\n" +
                  "  \"expires_in\" : 3600\n" +
                  "}"));
      server.play();

      OAuthApi api = api(server.getUrl("/"));

      assertEquals(api.authenticate(TokenRequest.create(HEADER, CLAIMS)), TOKEN);

      RecordedRequest request = server.takeRequest();
      assertEquals(request.getMethod(), "POST");
      assertEquals(request.getHeader("Accept"), APPLICATION_JSON);
      assertEquals(request.getHeader("Content-Type"), "application/x-www-form-urlencoded");

      assertEquals(new String(request.getBody(), UTF_8), //
            "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&" +
                  // Base64 Encoded Header
                  "assertion=" +
                  Joiner.on('.').join(encoding.encode(header.getBytes(UTF_8)), encoding.encode(claims.getBytes(UTF_8)),
                  // Base64 encoded {header}.{claims} signature (using SHA256)
                  "W2Lesr_98AzVYiMbzxFqmwcOjpIWlwqkC6pNn1fXND9oSDNNnFhy-AAR6DKH-x9ZmxbY80" +
                  "R5fH-OCeWumXlVgceKN8Z2SmgQsu8ElTpypQA54j_5j8vUImJ5hsOUYPeyF1U2BUzZ3L5g" +
                  "03PXBA0YWwRU9E1ChH28dQBYuGiUmYw"));

   }

   private final BaseEncoding encoding = base64Url().omitPadding();

   private OAuthApi api(URL url) throws IOException {
      Properties overrides = new Properties();
      overrides.put(AUDIENCE, "https://accounts.google.com/o/oauth2/token");
      overrides.put(PROPERTY_MAX_RETRIES, "1");

      return ContextBuilder.newBuilder(new OAuthApiMetadata())
            .credentials("foo", toStringAndClose(OAuthTestUtils.class.getResourceAsStream("/testpk.pem")))
            .endpoint(url.toString())
            .overrides(overrides)
            .modules(ImmutableSet.<Module>of(new ExecutorServiceModule(sameThreadExecutor())))
            .buildApi(OAuthApi.class);
   }
}
