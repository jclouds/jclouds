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
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_ALGORITHM_HMAC_SHA256;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_CONTENT_SHA256_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_DATE_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_DECODED_CONTENT_LENGTH_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_SECURITY_TOKEN_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.CHUNK_SIGNATURE_HEADER;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.CLRF;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.CONTENT_ENCODING_HEADER_AWS_CHUNKED;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.SIGNATURE_LENGTH;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.STREAMING_BODY_SHA256;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_JCLOUDS_S3_CHUNKED_SIZE;
import static org.jclouds.util.Strings2.toInputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.ByteStreams.readBytes;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_MD5;
import static com.google.common.net.HttpHeaders.DATE;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Date;

import javax.inject.Named;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.io.Payload;
import org.jclouds.location.Provider;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.hash.HashCode;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteProcessor;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

/**
 * AWS4 signer sign 'chunked' uploads.
 */
public class Aws4SignerForChunkedUpload extends Aws4SignerBase {

   private final int userDataBlockSize;

   @Inject
   public Aws4SignerForChunkedUpload(SignatureWire signatureWire,
         @Named(PROPERTY_HEADER_TAG) String headerTag,
         @Named(PROPERTY_JCLOUDS_S3_CHUNKED_SIZE) int userDataBlockSize,
         @Provider Supplier<Credentials> creds, @TimeStamp Supplier<Date> timestampProvider,
         ServiceAndRegion serviceAndRegion, Crypto crypto) {
      super(signatureWire, headerTag, creds, timestampProvider, serviceAndRegion, crypto);
      this.userDataBlockSize = userDataBlockSize;
   }

   protected HttpRequest sign(HttpRequest request) throws HttpException {
      checkNotNull(request, "request is not ready to sign");
      checkNotNull(request.getEndpoint(), "request is not ready to sign, request.endpoint not present.");

      Payload payload = request.getPayload();
      // chunked upload required content-length.
      Long contentLength = payload.getContentMetadata().getContentLength();

      // check contentLength not null
      checkNotNull(contentLength, "request is not ready to sign, payload contentLength not present.");

      // get host from request endpoint.
      String host = request.getEndpoint().getHost();

      Date date = timestampProvider.get();
      String timestamp = timestampFormat.format(date);
      String datestamp = dateFormat.format(date);

      String service = serviceAndRegion.service();
      String region = serviceAndRegion.region(host);
      String credentialScope = Joiner.on('/').join(datestamp, region, service, "aws4_request");

      HttpRequest.Builder<?> requestBuilder = request.toBuilder() //
            .removeHeader(AUTHORIZATION) // remove Authorization
            .removeHeader(DATE) // remove Date
            .removeHeader(CONTENT_LENGTH); // remove Content-Length

      ImmutableMap.Builder<String, String> signedHeadersBuilder = ImmutableSortedMap.<String, String>naturalOrder();

      // content-encoding
      String contentEncoding = CONTENT_ENCODING_HEADER_AWS_CHUNKED;
      String originalContentEncoding = payload.getContentMetadata().getContentEncoding();
      if (originalContentEncoding != null) {
         contentEncoding += "," + originalContentEncoding;
      }
      requestBuilder.replaceHeader(HttpHeaders.CONTENT_ENCODING, contentEncoding);
      signedHeadersBuilder.put(HttpHeaders.CONTENT_ENCODING.toLowerCase(), contentEncoding);


      // x-amz-decoded-content-length
      requestBuilder.replaceHeader(AMZ_DECODED_CONTENT_LENGTH_HEADER, contentLength.toString());
      signedHeadersBuilder.put(AMZ_DECODED_CONTENT_LENGTH_HEADER.toLowerCase(), contentLength.toString());

      // how big is the overall request stream going to be once we add the signature
      // 'headers' to each chunk?
      long totalLength = calculateChunkedContentLength(contentLength, userDataBlockSize);
      requestBuilder.replaceHeader(CONTENT_LENGTH, Long.toString(totalLength));
      signedHeadersBuilder.put(CONTENT_LENGTH.toLowerCase(), Long.toString(totalLength));

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

      // Content Type
      // content-type is not a required signing param. However, examples use this, so we include it to ease testing.
      String contentType = getContentType(request);
      if (!Strings.isNullOrEmpty(contentType)) {
         requestBuilder.replaceHeader(HttpHeaders.CONTENT_TYPE, contentType);
         signedHeadersBuilder.put(HttpHeaders.CONTENT_TYPE.toLowerCase(), contentType);
      } else {
         requestBuilder.removeHeader(HttpHeaders.CONTENT_TYPE);
      }

      // host
      host = hostHeaderFor(request.getEndpoint());
      requestBuilder.replaceHeader(HttpHeaders.HOST, host);
      signedHeadersBuilder.put(HttpHeaders.HOST.toLowerCase(), host);

      // user-agent, not a required signing param
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
      String contentSha256 = getPayloadHash();
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

      // init hmacSHA256 processor for seed signature and chunked block signature
      ByteProcessor<byte[]> hmacSHA256;
      try {
         hmacSHA256 = hmacSHA256(crypto, signatureKey);
      } catch (InvalidKeyException e) {
         throw new ChunkedUploadException("invalid key", e);
      }

      // Calculating the Seed Signature
      String signature;
      try {
         signature = hex(readBytes(toInputStream(stringToSign), hmacSHA256));
      } catch (IOException e) {
         throw new ChunkedUploadException("hmac sha256 seed signature error", e);
      }

      StringBuilder authorization = new StringBuilder(AMZ_ALGORITHM_HMAC_SHA256).append(" ");
      authorization.append("Credential=").append(Joiner.on("/").join(credentials.identity, credentialScope))
            .append(", ");
      authorization.append("SignedHeaders=").append(Joiner.on(";").join(signedHeaders.keySet()))
            .append(", ");
      authorization.append("Signature=").append(signature);

      // replace request payload with chunked upload payload
      ChunkedUploadPayload chunkedPayload = new ChunkedUploadPayload(payload, userDataBlockSize, timestamp,
            credentialScope, hmacSHA256, signature);
      chunkedPayload.getContentMetadata().setContentEncoding(null);

      return requestBuilder
            .replaceHeader(HttpHeaders.AUTHORIZATION, authorization.toString())
            .payload(chunkedPayload)
            .build();

   }

   // for seed signature, value: STREAMING-AWS4-HMAC-SHA256-PAYLOAD
   protected String getPayloadHash() {
      return STREAMING_BODY_SHA256;
   }

   /**
    * Calculates the expanded payload size of our data when it is chunked
    *
    * @param originalLength The true size of the data payload to be uploaded
    * @param chunkSize     The size of each chunk we intend to send; each chunk will be
    *                  prefixed with signed header data, expanding the overall size
    *                  by a determinable amount
    * @return The overall payload size to use as content-length on a chunked
    * upload
    */
   public static long calculateChunkedContentLength(long originalLength, long chunkSize) {
      checkArgument(originalLength > 0, "Nonnegative content length expected.");

      long maxSizeChunks = originalLength / chunkSize;
      long remainingBytes = originalLength % chunkSize;
      return maxSizeChunks * calculateChunkHeaderLength(chunkSize)
            + (remainingBytes > 0 ? calculateChunkHeaderLength(remainingBytes) : 0)
            + calculateChunkHeaderLength(0);
   }

   /**
    * Returns the size of a chunk header, which only varies depending on the
    * selected chunk size
    *
    * @param chunkDataSize The intended size of each chunk; this is placed into the chunk
    *                 header
    * @return The overall size of the header that will prefix the user data in
    * each chunk
    */
   private static long calculateChunkHeaderLength(long chunkDataSize) {
      return Long.toHexString(chunkDataSize).length()
            + CHUNK_SIGNATURE_HEADER.length()
            + SIGNATURE_LENGTH
            + CLRF.length()
            + chunkDataSize
            + CLRF.length();
   }


}
