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
package org.jclouds.s3.filters;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_ALGORITHM_PARAM;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_CONTENT_SHA256_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_CREDENTIAL_PARAM;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_DATE_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_DATE_PARAM;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_EXPIRES_PARAM;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_SECURITY_TOKEN_PARAM;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_SIGNATURE_PARAM;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_SIGNEDHEADERS_PARAM;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AUTHORIZATION_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.UNSIGNED_PAYLOAD;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base16;

import java.util.Date;

import javax.inject.Named;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.Uris;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.location.Provider;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.inject.Inject;

/**
 * AWS4 signer sign requests to Amazon S3 using query string parameters.
 */
public class Aws4SignerForQueryString extends Aws4SignerBase {
   @Inject
   public Aws4SignerForQueryString(SignatureWire signatureWire,
         @Named(PROPERTY_S3_VIRTUAL_HOST_BUCKETS) boolean isVhostStyle,
         @Named(PROPERTY_HEADER_TAG) String headerTag,
         @Provider Supplier<Credentials> creds, @TimeStamp Supplier<Date> timestampProvider,
         ServiceAndRegion serviceAndRegion, Crypto crypto) {
      super(signatureWire, headerTag, creds, timestampProvider, serviceAndRegion, crypto);
   }


   protected HttpRequest sign(HttpRequest request, long timeInSeconds) throws HttpException {
      checkNotNull(request, "request is not ready to sign");
      checkNotNull(request.getEndpoint(), "request is not ready to sign, request.endpoint not present.");

      // get host from request endpoint.
      String host = request.getEndpoint().getHost();

      Date date = timestampProvider.get();
      String timestamp = timestampFormat.format(date);
      String datestamp = dateFormat.format(date);

      String service = serviceAndRegion.service();
      String region = serviceAndRegion.region(host);
      String credentialScope = Joiner.on('/').join(datestamp, region, service, "aws4_request");

      // different with signature with Authorization header
      HttpRequest.Builder<?> requestBuilder = request.toBuilder() //
            // sign for temporary access use query string parameter:
            // X-Amz-Algorithm, X-Amz-Credential, X-Amz-Date, X-Amz-Expires, X-Amz-SignedHeaders, X-Amz-Signature
            // remove Authorization, x-amz-content-sha256, X-Amz-Date headers
            .removeHeader(AUTHORIZATION_HEADER)
            .removeHeader(AMZ_CONTENT_SHA256_HEADER)
            .removeHeader(AMZ_DATE_HEADER);

      ImmutableMap.Builder<String, String> signedHeadersBuilder = ImmutableSortedMap.<String, String>naturalOrder(); //
      Uris.UriBuilder endpointBuilder = Uris.uriBuilder(request.getEndpoint());


      // Canonical Headers
      // must include the HTTP host header.
      // If you plan to include any of the x-amz-* headers, these headers must also be added for signature calculation.
      // You can optionally add all other headers that you plan to include in your request.
      // For added security, you should sign as many headers as possible.

      // HOST
      host = hostHeaderFor(request.getEndpoint());
      signedHeadersBuilder.put("host", host);
      ImmutableMap<String, String> signedHeaders = signedHeadersBuilder.build();

      Credentials credentials = creds.get();

      if (credentials instanceof SessionCredentials) {
         String token = SessionCredentials.class.cast(credentials).getSessionToken();
         // different with signature with Authorization header
         endpointBuilder.replaceQuery(AMZ_SECURITY_TOKEN_PARAM, token);
      }

      // X-Amz-Algorithm=HMAC-SHA256
      endpointBuilder.replaceQuery(AMZ_ALGORITHM_PARAM, AwsSignatureV4Constants.AMZ_ALGORITHM_HMAC_SHA256);

      // X-Amz-Credential=<your-access-key-id>/<date>/<AWS-region>/<AWS-service>/aws4_request.
      String credential = Joiner.on("/").join(credentials.identity, credentialScope);
      endpointBuilder.replaceQuery(AMZ_CREDENTIAL_PARAM, credential);

      // X-Amz-Date=ISO 8601 format, for example, 20130721T201207Z
      endpointBuilder.replaceQuery(AMZ_DATE_PARAM, timestamp);

      // X-Amz-Expires=time in seconds
      endpointBuilder.replaceQuery(AMZ_EXPIRES_PARAM, String.valueOf(timeInSeconds));

      // X-Amz-SignedHeaders=HTTP host header is required.
      endpointBuilder.replaceQuery(AMZ_SIGNEDHEADERS_PARAM, Joiner.on(';').join(signedHeaders.keySet()));

      String stringToSign = createStringToSign(request.getMethod(), endpointBuilder.build(), signedHeaders, timestamp, credentialScope,
            getPayloadHash());

      signatureWire.getWireLog().debug("<< " + stringToSign);


      byte[] signatureKey = signatureKey(credentials.credential, datestamp, region, service);
      String signature = base16().lowerCase().encode(hmacSHA256(stringToSign, signatureKey));

      // X-Amz-Signature=Signature
      endpointBuilder.replaceQuery(AMZ_SIGNATURE_PARAM, signature);

      return requestBuilder.endpoint(endpointBuilder.build()).build();
   }

   protected String getPayloadHash() {
      return UNSIGNED_PAYLOAD;
   }
}
