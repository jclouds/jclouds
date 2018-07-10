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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.io.ByteStreams.readBytes;
import static org.jclouds.crypto.Macs.asByteProcessor;
import static org.jclouds.http.utils.Queries.queryParser;
import static org.jclouds.util.Strings2.toInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;

import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.escape.Escaper;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;
import com.google.common.net.PercentEscaper;
import com.google.inject.ImplementedBy;
import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.io.Payload;
import org.jclouds.providers.ProviderMetadata;

/**
 * Common methods and properties for all AWS4 signer variants
 */
public abstract class Aws4SignerBase {
   private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
   protected final DateFormat timestampFormat;
   protected final DateFormat dateFormat;

   // Do not URL-encode any of the unreserved characters that RFC 3986 defines:
   // A-Z, a-z, 0-9, hyphen (-), underscore (_), period (.), and tilde (~).
   private static final Escaper AWS_URL_PARAMETER_ESCAPER = new PercentEscaper("-_.~", false);

   private static final Escaper AWS_PATH_ESCAPER = new PercentEscaper("/-_.~", false);

   // Specifying a default for how to parse the service and region in this way allows
   // tests or other downstream services to not have to use guice overrides.
   @ImplementedBy(ServiceAndRegion.AWSServiceAndRegion.class)
   public interface ServiceAndRegion {
      String service();

      String region(String host);

      final class AWSServiceAndRegion implements ServiceAndRegion {
         private final String service;

         @Inject
         AWSServiceAndRegion(ProviderMetadata provider) {
            this(provider.getEndpoint());
         }

         AWSServiceAndRegion(String endpoint) {
            this.service = AwsHostNameUtils.parseServiceName(URI.create(checkNotNull(endpoint, "endpoint")));
         }

         @Override
         public String service() {
            return service;
         }

         @Override
         public String region(String host) {
            return AwsHostNameUtils.parseRegionName(host, service());
         }
      }
   }

   protected final String headerTag;
   protected final ServiceAndRegion serviceAndRegion;
   protected final SignatureWire signatureWire;
   protected final Supplier<Credentials> creds;
   protected final Supplier<Date> timestampProvider;
   protected final Crypto crypto;


   protected Aws4SignerBase(SignatureWire signatureWire, String headerTag,
         Supplier<Credentials> creds, Supplier<Date> timestampProvider,
         ServiceAndRegion serviceAndRegion, Crypto crypto) {
      this.signatureWire = signatureWire;
      this.headerTag = headerTag;
      this.creds = creds;
      this.timestampProvider = timestampProvider;
      this.serviceAndRegion = serviceAndRegion;
      this.crypto = crypto;
      this.timestampFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
      timestampFormat.setTimeZone(GMT);
      this.dateFormat = new SimpleDateFormat("yyyyMMdd");
      dateFormat.setTimeZone(GMT);
   }

   protected static String hostHeaderFor(URI endpoint) {
      String scheme = endpoint.getScheme();
      String host = endpoint.getHost();
      int port = endpoint.getPort();

      // if the port is defined and doesn't match the URI scheme
      if (port != -1) {
         if (("http".equalsIgnoreCase(scheme) && port != 80) ||
                 ("https".equalsIgnoreCase(scheme) && port != 443)) {
            host += ":" + port; // append the port number to the hostname
         }
      }

      return host; // else just use the original hostname
   }

   protected String getContentType(HttpRequest request) {
      Payload payload = request.getPayload();

      // Default Content Type
      String contentType = request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE);
      if (payload != null
            && payload.getContentMetadata() != null
            && payload.getContentMetadata().getContentType() != null) {
         contentType = payload.getContentMetadata().getContentType();
      }
      return contentType;
   }

   protected String getContentLength(HttpRequest request) {
      Payload payload = request.getPayload();

      // Default Content Type
      String contentLength = request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      if (payload != null
            && payload.getContentMetadata() != null
            && payload.getContentMetadata().getContentType() != null) {
         Long length = payload.getContentMetadata().getContentLength();
         contentLength =
               length == null ? contentLength : String.valueOf(payload.getContentMetadata().getContentLength());
      }
      return contentLength;
   }

   // append all of 'x-amz-*' headers
   protected void appendAmzHeaders(HttpRequest request,
         ImmutableMap.Builder<String, String> signedHeadersBuilder) {
      for (Map.Entry<String, String> header : request.getHeaders().entries()) {
         String key = header.getKey();
         if (key.startsWith("x-" + headerTag + "-")) {
            signedHeadersBuilder.put(key.toLowerCase(), header.getValue());
         }
      }
   }

   /**
    * caluclate AWS signature key.
    * <p>
    * <code>
    * DateKey = hmacSHA256(datestamp, "AWS4"+ secretKey)
    * <br>
    * DateRegionKey = hmacSHA256(region, DateKey)
    * <br>
    * DateRegionServiceKey = hmacSHA256(service, DateRegionKey)
    * <br>
    * SigningKey = hmacSHA256("aws4_request", DateRegionServiceKey)
    * <br>
    * <p/>
    * </code>
    * </p>
    *
    * @param secretKey AWS access secret key
    * @param datestamp date yyyyMMdd
    * @param region   AWS region
    * @param service   AWS service
    * @return SigningKey
    */
   protected byte[] signatureKey(String secretKey, String datestamp, String region, String service) {
      byte[] kSecret = ("AWS4" + secretKey).getBytes(UTF_8);
      byte[] kDate = hmacSHA256(datestamp, kSecret);
      byte[] kRegion = hmacSHA256(region, kDate);
      byte[] kService = hmacSHA256(service, kRegion);
      byte[] kSigning = hmacSHA256("aws4_request", kService);
      return kSigning;
   }

   /**
    * hmac sha256
    *
    * @param toSign string to sign
    * @param key   hash key
    */
   protected byte[] hmacSHA256(String toSign, byte[] key) {
      try {
         return readBytes(toInputStream(toSign), hmacSHA256(crypto, key));
      } catch (IOException e) {
         throw new HttpException("read sign error", e);
      } catch (InvalidKeyException e) {
         throw new HttpException("invalid key", e);
      }
   }

   public static ByteProcessor<byte[]> hmacSHA256(Crypto crypto, byte[] signatureKey) throws InvalidKeyException {
      return asByteProcessor(crypto.hmacSHA256(signatureKey));
   }

   /**
    * hash input with sha256
    *
    * @param input
    * @return hash result
    * @throws HttpException
    */
   public static byte[] hash(InputStream input) throws HttpException {
      HashingInputStream his = new HashingInputStream(Hashing.sha256(), input);
      try {
         ByteStreams.copy(his, ByteStreams.nullOutputStream());
         return his.hash().asBytes();
      } catch (IOException e) {
         throw new HttpException("Unable to compute hash while signing request: " + e.getMessage(), e);
      }
   }

   /**
    * hash input with sha256
    *
    * @param bytes input bytes
    * @return hash result
    * @throws HttpException
    */
   public static byte[] hash(byte[] bytes) throws HttpException {
      try {
         return ByteSource.wrap(bytes).hash(Hashing.sha256()).asBytes();
      } catch (IOException e) {
         throw new HttpException("Unable to compute hash while signing request: " + e.getMessage(), e);
      }
   }

   /**
    * hash string (encoding UTF_8) with sha256
    *
    * @param input input stream
    * @return hash result
    * @throws HttpException
    */
   public static byte[] hash(String input) throws HttpException {
      return hash(new ByteArrayInputStream(input.getBytes(UTF_8)));
   }

   /**
    * Examines the specified query string parameters and returns a
    * canonicalized form.
    * <p/>
    * The canonicalized query string is formed by first sorting all the query
    * string parameters, then URI encoding both the key and value and then
    * joining them, in order, separating key value pairs with an '&'.
    *
    * @param queryString The query string parameters to be canonicalized.
    * @return A canonicalized form for the specified query string parameters.
    */
   protected String getCanonicalizedQueryString(String queryString) {
      Multimap<String, String> params = queryParser().apply(queryString);
      SortedMap<String, String> sorted = Maps.newTreeMap();
      if (params == null) {
         return "";
      }
      Iterator<Map.Entry<String, String>> pairs = params.entries().iterator();
      while (pairs.hasNext()) {
         Map.Entry<String, String> pair = pairs.next();
         String key = pair.getKey();
         String value = pair.getValue();
         sorted.put(urlEncode(key), urlEncode(value));
      }

      return Joiner.on("&").withKeyValueSeparator("=").join(sorted);
   }

   /**
    * Encode a string for use in the path of a URL; uses URLEncoder.encode,
    * (which encodes a string for use in the query portion of a URL), then
    * applies some postfilters to fix things up per the RFC. Can optionally
    * handle strings which are meant to encode a path (ie include '/'es
    * which should NOT be escaped).
    *
    * @param value the value to encode
    * @return the encoded value
    */
   public static String urlEncode(final String value) {
      if (value == null) {
         return "";
      }
      return AWS_URL_PARAMETER_ESCAPER.escape(value);
   }

   /**
    * Lowercase base 16 encoding.
    *
    * @param bytes bytes
    * @return base16 lower case hex string.
    */
   public static String hex(final byte[] bytes) {
      return base16().lowerCase().encode(bytes);
   }

   /**
    * Create a Canonical Request to sign
    * <h4>Canonical Request</h4>
    * <p>
    * <code>
    * &lt;HTTPMethod>\n
    * <br>
    * &lt;CanonicalURI>\n
    * <br>
    * &lt;CanonicalQueryString>\n
    * <br>
    * &lt;CanonicalHeaders>\n
    * <br>
    * &lt;SignedHeaders>\n
    * <br>
    * &lt;HashedPayload>
    * </code>
    * </p>
    * <p><b>HTTPMethod</b> is one of the HTTP methods, for example GET, PUT, HEAD, and DELETE.</p>
    * <p><b>CanonicalURI</b> is the URI-encoded version of the absolute path component of the URI—everything starting
    * with the "/" that follows the domain name and up to the end of the string or to the question mark character ('?')
    * if you have query string parameters.</p>
    * <p><b>CanonicalQueryString</b> specifies the URI-encoded query string parameters. You URI-encode name and values
    * individually. You must also sort the parameters in the canonical query string alphabetically by key name.
    * The sorting occurs after encoding.</p>
    * <p><b>CanonicalHeaders</b> is a list of request headers with their values. Individual header name and value pairs are
    * separated by the newline character ("\n"). Header names must be in lowercase. Header value must be trim space.
    * <br>
    * The <b>CanonicalHeaders</b> list must include the following:
    * HTTP host header.
    * If the Content-Type header is present in the request, it must be added to the CanonicalHeaders list.
    * Any x-amz-* headers that you plan to include in your request must also be added.</p>
    * <p><b>SignedHeaders</b> is an alphabetically sorted, semicolon-separated list of lowercase request header names.
    * The request headers in the list are the same headers that you included in the CanonicalHeaders string.</p>
    * <p><b>HashedPayload</b> is the hexadecimal value of the SHA256 hash of the request payload. </p>
    * <p>If there is no payload in the request, you compute a hash of the empty string as follows:
    * <code>Hex(SHA256Hash(""))</code> The hash returns the following value:
    * e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855  </p>
    *
    * @param method           http request method
    * @param endpoint         http request endpoing
    * @param signedHeaders    signed headers
    * @param timestamp        ISO8601 timestamp
    * @param credentialScope  credential scope
    * @return string to sign
    */
   protected String createStringToSign(String method, URI endpoint, Map<String, String> signedHeaders,
         String timestamp, String credentialScope, String hashedPayload) {

      // lower case header keys
      Map<String, String> lowerCaseHeaders = lowerCaseNaturalOrderKeys(signedHeaders);

      StringBuilder canonicalRequest = new StringBuilder();

      // HTTPRequestMethod + '\n' +
      canonicalRequest.append(method).append("\n");

      // CanonicalURI + '\n' +
      canonicalRequest.append(AWS_PATH_ESCAPER.escape(endpoint.getPath())).append("\n");

      // CanonicalQueryString + '\n' +
      if (endpoint.getQuery() != null) {
         canonicalRequest.append(getCanonicalizedQueryString(endpoint.getRawQuery()));
      }
      canonicalRequest.append("\n");

      // CanonicalHeaders + '\n' +
      for (Map.Entry<String, String> entry : lowerCaseHeaders.entrySet()) {
         canonicalRequest.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
      }
      canonicalRequest.append("\n");

      // SignedHeaders + '\n' +
      canonicalRequest.append(Joiner.on(';').join(lowerCaseHeaders.keySet())).append('\n');

      // HexEncode(Hash(Payload))
      canonicalRequest.append(hashedPayload);

      signatureWire.getWireLog().debug("<< " + canonicalRequest);

      // Create a String to Sign
      StringBuilder toSign = new StringBuilder();
      // Algorithm + '\n' +
      toSign.append("AWS4-HMAC-SHA256").append('\n');
      // RequestDate + '\n' +
      toSign.append(timestamp).append('\n');
      // CredentialScope + '\n' +
      toSign.append(credentialScope).append('\n');
      // HexEncode(Hash(CanonicalRequest))
      toSign.append(hex(hash(canonicalRequest.toString())));

      return toSign.toString();
   }

   /**
    * change the keys but keep the values in-tact.
    *
    * @param in input map to transform
    * @return immutableSortedMap with the new lowercase keys.
    */
   protected static Map<String, String> lowerCaseNaturalOrderKeys(Map<String, String> in) {
      checkNotNull(in, "input map");
      ImmutableSortedMap.Builder<String, String> returnVal = ImmutableSortedMap.<String, String>naturalOrder();
      for (Map.Entry<String, String> entry : in.entrySet())
         returnVal.put(entry.getKey().toLowerCase(Locale.US), entry.getValue());
      return returnVal.build();
   }

}
