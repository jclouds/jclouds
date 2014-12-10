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
package org.jclouds.aws.filters;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.hash.Hashing.sha256;
import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.google.common.net.HttpHeaders.HOST;
import static org.jclouds.aws.reference.FormParameters.ACTION;
import static org.jclouds.aws.reference.FormParameters.VERSION;
import static org.jclouds.http.utils.Queries.queryParser;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.location.Provider;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.inject.ImplementedBy;

public final class FormSignerV4 implements FormSigner {

   // Specifying a default for how to parse the service and region in this way allows
   // tests or other downstream services to not have to use guice overrides.
   @ImplementedBy(ServiceAndRegion.AWSServiceAndRegion.class)
   public interface ServiceAndRegion {
      String service();
      String region(String host);

      static final class AWSServiceAndRegion implements ServiceAndRegion {
         private final String service;

         @Inject AWSServiceAndRegion(ProviderMetadata provider) {
            this(provider.getEndpoint());
         }

         AWSServiceAndRegion(String endpoint) {
            this.service = parseServiceAndRegion(URI.create(checkNotNull(endpoint, "endpoint")).getHost()).get(0);
         }

         @Override public String service() {
            return service;
         }

         @Override public String region(String host) {
            return parseServiceAndRegion(host).get(1);
         }

         /** This will only work for amazon deployments, and perhaps not all of them. */
         private static List<String> parseServiceAndRegion(String host) {
            checkArgument(host.endsWith(".amazonaws.com"), "Only AWS endpoints currently supported %s", host);
            return Splitter.on('.').splitToList(host);
         }
      }
   }

   private final String apiVersion;
   private final Supplier<Credentials> creds;
   private final javax.inject.Provider<String> iso8601Timestamp;
   private final ServiceAndRegion serviceAndRegion;

   @Inject FormSignerV4(@ApiVersion String apiVersion, @Provider Supplier<Credentials> creds,
         @TimeStamp javax.inject.Provider<String> iso8601Timestamp, ServiceAndRegion serviceAndRegion) {
      this.apiVersion = apiVersion;
      this.creds = creds;
      this.iso8601Timestamp = iso8601Timestamp;
      this.serviceAndRegion = serviceAndRegion;
   }

   @Override public HttpRequest filter(HttpRequest request) throws HttpException {
      checkArgument(request.getHeaders().containsKey(HOST), "request is not ready to sign; host not present");
      String host = request.getFirstHeaderOrNull(HOST);
      String form = request.getPayload().getRawContent().toString();
      Multimap<String, String> decodedParams = queryParser().apply(form);
      checkArgument(decodedParams.containsKey(ACTION), "request is not ready to sign; Action not present %s", form);

      String timestamp = iso8601Timestamp.get();
      String datestamp = timestamp.substring(0, 8);

      String service = serviceAndRegion.service();
      String region = serviceAndRegion.region(host);
      String credentialScope = Joiner.on('/').join(datestamp, region, service, "aws4_request");

      // content-type is not a required signing param. However, examples use this, so we include it to ease testing.
      ImmutableMap.Builder<String, String> signedHeadersBuilder = ImmutableMap.<String, String> builder() //
            .put("content-type", request.getPayload().getContentMetadata().getContentType()) //
            .put("host", host) //
            .put("x-amz-date", timestamp);

      HttpRequest.Builder<?> requestBuilder = request.toBuilder() //
            .removeHeader(AUTHORIZATION) //
            .replaceHeader("X-Amz-Date", timestamp);

      if (!decodedParams.containsKey(VERSION)) {
         requestBuilder.addFormParam(VERSION, apiVersion);
      }

      Credentials credentials = creds.get();

      if (credentials instanceof SessionCredentials) {
         String token = SessionCredentials.class.cast(credentials).getSessionToken();
         requestBuilder.replaceHeader("X-Amz-Security-Token", token);
         signedHeadersBuilder.put("x-amz-security-token", token);
      }

      ImmutableMap<String, String> signedHeaders = signedHeadersBuilder.build();

      String stringToSign = createStringToSign(requestBuilder.build(), signedHeaders, credentialScope);
      byte[] signatureKey = signatureKey(credentials.credential, datestamp, region, service);
      String signature = base16().lowerCase().encode(hmacSHA256(stringToSign, signatureKey));

      StringBuilder authorization = new StringBuilder("AWS4-HMAC-SHA256 ");
      authorization.append("Credential=").append(credentials.identity).append('/').append(credentialScope).append(", ");
      authorization.append("SignedHeaders=").append(Joiner.on(';').join(signedHeaders.keySet())).append(", ");
      authorization.append("Signature=").append(signature);

      return requestBuilder.addHeader(AUTHORIZATION, authorization.toString()).build();
   }

   static byte[] signatureKey(String secretKey, String datestamp, String region, String service) {
      byte[] kSecret = ("AWS4" + secretKey).getBytes(UTF_8);
      byte[] kDate = hmacSHA256(datestamp, kSecret);
      byte[] kRegion = hmacSHA256(region, kDate);
      byte[] kService = hmacSHA256(service, kRegion);
      byte[] kSigning = hmacSHA256("aws4_request", kService);
      return kSigning;
   }

   static byte[] hmacSHA256(String data, byte[] key) {
      try {
         String algorithm = "HmacSHA256";
         Mac mac = Mac.getInstance(algorithm);
         mac.init(new SecretKeySpec(key, algorithm));
         return mac.doFinal(data.getBytes(UTF_8));
      } catch (GeneralSecurityException e) {
         throw new HttpException(e);
      }
   }

   static String createStringToSign(HttpRequest request, Map<String, String> signedHeaders, String credentialScope) {
      StringBuilder canonicalRequest = new StringBuilder();
      // HTTPRequestMethod + '\n' +
      canonicalRequest.append(request.getMethod()).append("\n");
      // CanonicalURI + '\n' +
      canonicalRequest.append(request.getEndpoint().getPath()).append("\n");
      // CanonicalQueryString + '\n' +
      checkArgument(request.getEndpoint().getQuery() == null, "Query parameters not yet supported %s", request);
      canonicalRequest.append("\n");
      // CanonicalHeaders + '\n' +
      for (Map.Entry<String, String> entry : signedHeaders.entrySet()) {
         canonicalRequest.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
      }
      canonicalRequest.append("\n");

      // SignedHeaders + '\n' +
      canonicalRequest.append(Joiner.on(';').join(signedHeaders.keySet())).append('\n');

      // HexEncode(Hash(Payload))
      String payload = request.getPayload().getRawContent().toString();
      canonicalRequest.append(base16().lowerCase().encode(sha256().hashString(payload, UTF_8).asBytes()));

      StringBuilder toSign = new StringBuilder();
      // Algorithm + '\n' +
      toSign.append("AWS4-HMAC-SHA256").append('\n');
      // RequestDate + '\n' +
      toSign.append(signedHeaders.get("x-amz-date")).append('\n');
      // CredentialScope + '\n' +
      toSign.append(credentialScope).append('\n');
      // HexEncode(Hash(CanonicalRequest))
      toSign.append(base16().lowerCase().encode(sha256().hashString(canonicalRequest.toString(), UTF_8).asBytes()));

      return toSign.toString();
   }
}
