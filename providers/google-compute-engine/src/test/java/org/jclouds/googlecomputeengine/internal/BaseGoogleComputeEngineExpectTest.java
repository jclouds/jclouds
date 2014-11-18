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
package org.jclouds.googlecomputeengine.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.BaseEncoding.base64Url;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.googlecloud.config.GoogleCloudProperties.PROJECT_NAME;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.jclouds.googlecomputeengine.GoogleComputeEngineProviderMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

import com.google.common.base.Joiner;
import com.google.inject.Module;

public class BaseGoogleComputeEngineExpectTest<T> extends BaseRestApiExpectTest<T> {
   protected static final String COMPUTE_SCOPE = "https://www.googleapis.com/auth/compute";
   protected static final String COMPUTE_READONLY_SCOPE = "https://www.googleapis.com/auth/compute.readonly";
   protected static final String BASE_URL = "https://www.googleapis.com/compute/v1/projects";

   private static final String header = "{\"alg\":\"none\",\"typ\":\"JWT\"}";

   private static final String CLAIMS_TEMPLATE = "{" +
           "\"iss\":\"%s\"," +
           "\"scope\":\"%s\"," +
           "\"aud\":\"https://accounts.google.com/o/oauth2/token\"," +
           "\"exp\":3600," +
           "\"iat\":0}";

   protected static final String TOKEN = "1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M";

   protected static final HttpResponse TOKEN_RESPONSE = HttpResponse.builder().statusCode(200).payload(
           payloadFromString("{\n" +
                   "  \"access_token\" : \"" + TOKEN + "\",\n" +
                   "  \"token_type\" : \"Bearer\",\n" +
                   "  \"expires_in\" : 3600\n" +
                   "}")).build();

   protected BaseGoogleComputeEngineExpectTest() {
      provider = "google-compute-engine";
      identity = "761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer.gserviceaccount.com";
   }

   @Override protected ProviderMetadata createProviderMetadata(){
      return new GoogleComputeEngineProviderMetadata();
   }

   @Override protected Module createModule() {
      return GoogleComputeEngineTestModule.INSTANCE;
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(PROJECT_NAME, "party");
      // use no sig algorithm for expect tests (means no credential is required either)
      props.put(JWS_ALG, "none");
      return props;
   }

   @Override
   protected HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      HttpRequestComparisonType reqType = HttpRequestComparisonType.DEFAULT;
      if (input.getPayload() != null) {
         if (input.getPayload().getContentMetadata().getContentType().equals(APPLICATION_JSON)) {
            reqType = HttpRequestComparisonType.JSON;
         }
      }
      return reqType;
   }

   protected HttpRequest requestForScopes(String... scopes) {
      String claims = String.format(CLAIMS_TEMPLATE, identity, Joiner.on(",").join(scopes));

      String payload = "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&" +
              // Base64 Encoded Header
              "assertion=" + base64Url().omitPadding().encode(header.getBytes(UTF_8)) + "." +
              // Base64 Encoded Claims
              base64Url().omitPadding().encode(claims.getBytes(UTF_8)) + ".";

      return HttpRequest.builder()
              .method("POST")
              .endpoint(URI.create("https://accounts.google.com/o/oauth2/token"))
              .addHeader("Accept", APPLICATION_JSON)
              .payload(payloadFromStringWithContentType(payload, "application/x-www-form-urlencoded"))
              .build();
   }

   protected static Payload staticPayloadFromResource(String resource) {
      try {
         return payloadFromString(
               toStringAndClose(BaseGoogleComputeEngineExpectTest.class.getResourceAsStream(resource)));
      } catch (IOException e) {
         throw propagate(e);
      }
   }
}
