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

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.hash.HashCode;
import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.io.Payload;
import org.jclouds.location.Provider;
import org.jclouds.util.Closeables2;

import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.google.common.net.HttpHeaders.CONTENT_MD5;
import static com.google.common.net.HttpHeaders.DATE;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_ALGORITHM_HMAC_SHA256;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_CONTENT_SHA256_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_DATE_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_SECURITY_TOKEN_HEADER;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

/**
 * AWS4 signer sign requests to Amazon S3 using an 'Authorization' header.
 */
public class Aws4SignerForAuthorizationHeader extends Aws4SignerBase {
   @Inject
   public Aws4SignerForAuthorizationHeader(SignatureWire signatureWire,
         @Named(PROPERTY_S3_VIRTUAL_HOST_BUCKETS) boolean isVhostStyle,
         @Named(PROPERTY_HEADER_TAG) String headerTag,
         @Provider Supplier<Credentials> creds, @TimeStamp Supplier<Date> timestampProvider,
         ServiceAndRegion serviceAndRegion, Crypto crypto) {
      super(signatureWire, headerTag, creds, timestampProvider, serviceAndRegion, crypto);
   }

   protected HttpRequest sign(HttpRequest request) throws HttpException {
      checkNotNull(request, "request is not ready to sign");
      checkNotNull(request.getEndpoint(), "request is not ready to sign, request.endpoint not present.");

      Payload payload = request.getPayload();

      // get host & port from request endpoint.
      String host = request.getEndpoint().getHost();

      Date date = timestampProvider.get();
      String timestamp = timestampFormat.format(date);
      String datestamp = dateFormat.format(date);

      String service = serviceAndRegion.service();
      String region = serviceAndRegion.region(host);
      String credentialScope = Joiner.on('/').join(datestamp, region, service, "aws4_request");

      HttpRequest.Builder<?> requestBuilder = request.toBuilder() //
            .removeHeader(AUTHORIZATION) // remove Authorization
            .removeHeader(DATE); // remove date

      ImmutableMap.Builder<String, String> signedHeadersBuilder = ImmutableSortedMap.<String, String>naturalOrder();

      // Content Type
      // content-type is not a required signing param. However, examples use this, so we include it to ease testing.
      String contentType = getContentType(request);
      if (!Strings.isNullOrEmpty(contentType)) {
         requestBuilder.replaceHeader(HttpHeaders.CONTENT_TYPE, contentType);
         signedHeadersBuilder.put(HttpHeaders.CONTENT_TYPE.toLowerCase(), contentType);
      }

      // Content-Length for PUT or POST request http method
      String contentLength = getContentLength(request);
      if (!Strings.isNullOrEmpty(contentLength)) {
         requestBuilder.replaceHeader(HttpHeaders.CONTENT_LENGTH, contentLength);
         signedHeadersBuilder.put(HttpHeaders.CONTENT_LENGTH.toLowerCase(), contentLength);
      }

      // Content MD5
      String contentMD5 = request.getFirstHeaderOrNull(CONTENT_MD5);
      if (payload != null) {
         HashCode md5 = payload.getContentMetadata().getContentMD5AsHashCode();
         if (md5 != null) {
            contentMD5 = BaseEncoding.base64().encode(md5.asBytes());
         }
      }
      if (contentMD5 != null) {
         requestBuilder.replaceHeader(CONTENT_MD5, contentMD5);
         signedHeadersBuilder.put(CONTENT_MD5.toLowerCase(), contentMD5);
      }

      // host
      host = hostHeaderFor(request.getEndpoint());
      requestBuilder.replaceHeader(HttpHeaders.HOST, host);
      signedHeadersBuilder.put(HttpHeaders.HOST.toLowerCase(), host);

      // user-agent
      if (request.getHeaders().containsKey(HttpHeaders.USER_AGENT)) {
         signedHeadersBuilder.put(HttpHeaders.USER_AGENT.toLowerCase(),
               request.getFirstHeaderOrNull(HttpHeaders.USER_AGENT));
      }

      // all x-amz-* headers
      appendAmzHeaders(request, signedHeadersBuilder);

      // x-amz-security-token
      Credentials credentials = creds.get();
      if (credentials instanceof SessionCredentials) {
         String token = SessionCredentials.class.cast(credentials).getSessionToken();
         requestBuilder.replaceHeader(AMZ_SECURITY_TOKEN_HEADER, token);
         signedHeadersBuilder.put(AMZ_SECURITY_TOKEN_HEADER.toLowerCase(), token);
      }

      // x-amz-content-sha256
      String contentSha256 = getPayloadHash(request);
      requestBuilder.replaceHeader(AMZ_CONTENT_SHA256_HEADER, contentSha256);
      signedHeadersBuilder.put(AMZ_CONTENT_SHA256_HEADER.toLowerCase(), contentSha256);

      // put x-amz-date
      requestBuilder.replaceHeader(AMZ_DATE_HEADER, timestamp);
      signedHeadersBuilder.put(AMZ_DATE_HEADER.toLowerCase(), timestamp);

      ImmutableMap<String, String> signedHeaders = signedHeadersBuilder.build();

      String stringToSign = createStringToSign(request.getMethod(), request.getEndpoint(), signedHeaders, timestamp,
            credentialScope, contentSha256);
      signatureWire.getWireLog().debug("<< " + stringToSign);

      byte[] signatureKey = signatureKey(credentials.credential, datestamp, region, service);
      String signature = base16().lowerCase().encode(hmacSHA256(stringToSign, signatureKey));

      StringBuilder authorization = new StringBuilder(AMZ_ALGORITHM_HMAC_SHA256).append(" ");
      authorization.append("Credential=").append(Joiner.on("/").join(credentials.identity, credentialScope))
            .append(", ");
      authorization.append("SignedHeaders=").append(Joiner.on(";").join(signedHeaders.keySet()))
            .append(", ");
      authorization.append("Signature=").append(signature);
      return requestBuilder.replaceHeader(HttpHeaders.AUTHORIZATION, authorization.toString()).build();
   }

   protected String getPayloadHash(HttpRequest request) {
      Payload payload = request.getPayload();
      if (payload == null) {
         // when payload is null.
         return getEmptyPayloadContentHash();
      }
      return calculatePayloadContentHash(payload);
   }

   /**
    * The hash returns the following value: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    */
   protected String getEmptyPayloadContentHash() {
      return base16().lowerCase().encode(hash(new ByteArrayInputStream(new byte[0])));
   }

   /**
    * in this time, payload ContentMetadata provided content hash md5, but aws required sha256.
    */
   protected String calculatePayloadContentHash(Payload payload) {
      // use payload stream calculate content sha256
      InputStream payloadStream;
      try {
         payloadStream = payload.openStream();
      } catch (IOException e) {
         throw new HttpException("unable to open payload stream to calculate AWS4 signature.");
      }
      try {
         return base16().lowerCase().encode(hash(payloadStream));
      } finally {
         closeOrResetPayloadStream(payloadStream, payload.isRepeatable());
      }
   }

   // some times, when use Multipart Payload and a part can not be repeatable, will happen some error...
   void closeOrResetPayloadStream(InputStream payloadStream, boolean repeatable) {
      // if payload stream can repeatable.
      if (repeatable) {
         Closeables2.closeQuietly(payloadStream);
      } else {
         try {
            // reset unrepeatable payload stream
            payloadStream.reset();
         } catch (IOException e) {
            // reset payload stream
            throw new HttpException(
                  "unable to reset unrepeatable payload stream after calculating AWS4 signature.");
         }
      }
   }

}
