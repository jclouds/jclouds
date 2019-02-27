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
package org.jclouds.azure.storage.filters;

import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.readBytes;
import static org.jclouds.crypto.Macs.asByteProcessor;
import static org.jclouds.util.Patterns.NEWLINE_PATTERN;
import static org.jclouds.util.Strings2.toInputStream;
import org.jclouds.http.Uris.UriBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.jclouds.http.Uris;
import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.jclouds.Constants;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.Logger;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Multimaps;
import com.google.common.io.ByteProcessor;
import com.google.common.net.HttpHeaders;

/**
 * Signs the Azure Storage request.
 * 
 * @see <a href= "http://msdn.microsoft.com/en-us/library/dd179428.aspx" />
 */
@Singleton
public class SharedKeyLiteAuthentication implements HttpRequestFilter {
   private static final Collection<String> FIRST_HEADERS_TO_SIGN = ImmutableList.of(HttpHeaders.DATE);
   private final SignatureWire signatureWire;
   private final Supplier<Credentials> creds;
   private final Provider<String> timeStampProvider;
   private final Crypto crypto;
   private final String credential;
   private final String identity;
   private final HttpUtils utils;
   private final URI storageUrl;
   private final boolean isSAS;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   @Inject
   public SharedKeyLiteAuthentication(SignatureWire signatureWire,
         @org.jclouds.location.Provider Supplier<Credentials> creds, @TimeStamp Provider<String> timeStampProvider,
         Crypto crypto, HttpUtils utils, @Named("sasAuth") boolean sasAuthentication) {
      this.crypto = crypto;
      this.utils = utils;
      this.signatureWire = signatureWire;
      this.storageUrl = URI.create("https://" + creds.get().identity + ".blob.core.windows.net/");
      this.creds = creds;
      this.identity = creds.get().identity;
      this.credential = creds.get().credential;
      this.timeStampProvider = timeStampProvider;
      this.isSAS = sasAuthentication;
   }
   
   /** 
    * this is an updated filter method, which decides whether the SAS or SharedKeyLite 
    * is used and applies the right filtering.  
    */
   public HttpRequest filter(HttpRequest request) throws HttpException {
      request = this.isSAS ? filterSAS(request, this.credential) : filterKey(request);
      utils.logRequest(signatureLog, request, "<<");
      return request;
   }
   
   /** 
    * this filter method is applied only for the cases with SAS Authentication. 
    */
   public HttpRequest filterSAS(HttpRequest request, String credential) throws HttpException, IllegalArgumentException {
      URI requestUri = request.getEndpoint();
      String formattedCredential = credential.startsWith("?") ? credential.substring(1) : credential;
      String initialQuery = requestUri.getQuery();
      String finalQuery = initialQuery == null ? formattedCredential : initialQuery + "&" + formattedCredential;
      String[] parametersArray = cutUri(requestUri); 
      String containerName = parametersArray[1]; 
      UriBuilder endpoint = Uris.uriBuilder(storageUrl).appendPath(containerName);
      if (parametersArray.length == 3) {
         endpoint.appendPath(parametersArray[2]).query(finalQuery);
      } else {
         endpoint.query("restype=container&" + finalQuery);
      }
      return removeAuthorizationHeader(
         replaceDateHeader(request.toBuilder()
            .endpoint(endpoint.build())
            .build()));
   }
   
   /**
    * this is a 'standard' filter method, applied when SharedKeyLite authentication is used. 
    */
   public HttpRequest filterKey(HttpRequest request) throws HttpException {
      request = replaceDateHeader(request);
      String signature = calculateSignature(createStringToSign(request));
      return replaceAuthorizationHeader(request, signature);
   }
   
   HttpRequest replaceAuthorizationHeader(HttpRequest request, String signature) {
      return request.toBuilder()
            .replaceHeader(HttpHeaders.AUTHORIZATION, "SharedKeyLite " + creds.get().identity + ":" + signature)
            .build();
   }
   
   /**
    * this method removes Authorisation header, since it is not needed for SAS Authentication 
    */
   HttpRequest removeAuthorizationHeader(HttpRequest request) {
      return request.toBuilder()
            .removeHeader(HttpHeaders.AUTHORIZATION)
            .build();
   }

   HttpRequest replaceDateHeader(HttpRequest request) {
      Builder<String, String> builder = ImmutableMap.builder();
      String date = timeStampProvider.get();
      builder.put(HttpHeaders.DATE, date);
      request = request.toBuilder().replaceHeaders(Multimaps.forMap(builder.build())).build();
      return request;
   }
   
   /**
    * this is the method to parse container name and blob name from the HttpRequest. 
    */ 
   public String[] cutUri(URI uri) throws IllegalArgumentException {
      String path = uri.getPath();
      String[] result = path.split("/");
      if (result.length < 2) {
         throw new IllegalArgumentException("there is neither ContainerName nor BlobName in the URI path");
      }
      return result;
   } 

   public String createStringToSign(HttpRequest request) {
      utils.logRequest(signatureLog, request, ">>");
      StringBuilder buffer = new StringBuilder();
      // re-sign the request
      appendMethod(request, buffer);
      appendPayloadMetadata(request, buffer);
      appendHttpHeaders(request, buffer);
      appendCanonicalizedHeaders(request, buffer);
      appendCanonicalizedResource(request, buffer);
      if (signatureWire.enabled())
         signatureWire.output(buffer.toString());
      return buffer.toString();
   }

   private void appendPayloadMetadata(HttpRequest request, StringBuilder buffer) {
      buffer.append(
            HttpUtils.nullToEmpty(request.getPayload() == null ? null : request.getPayload().getContentMetadata()
                  .getContentMD5())).append("\n");
      buffer.append(
            Strings.nullToEmpty(request.getPayload() == null ? null : request.getPayload().getContentMetadata()
                  .getContentType())).append("\n");
   }

   public String calculateSignature(String toSign) throws HttpException {
      String signature = signString(toSign);
      if (signatureWire.enabled())
         signatureWire.input(Strings2.toInputStream(signature));
      return signature;
   }

   public String signString(String toSign) {
      try {
         ByteProcessor<byte[]> hmacSHA256 = asByteProcessor(crypto.hmacSHA256(base64().decode(creds.get().credential)));
         return base64().encode(readBytes(toInputStream(toSign), hmacSHA256));
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
   }

   private void appendMethod(HttpRequest request, StringBuilder toSign) {
      toSign.append(request.getMethod()).append("\n");
   }

   private void appendCanonicalizedHeaders(HttpRequest request, StringBuilder toSign) {
      // TreeMap == Sort the headers alphabetically.
      Map<String, String> headers = Maps.newTreeMap();
      Multimap<String, String> requestHeaders = request.getHeaders();
      for (String header : requestHeaders.keySet()) {
         if (header.startsWith("x-ms-")) {
            String value = Joiner.on(",").join(Iterables.transform(requestHeaders.get(header),
                new Function<String, Object>()
                {
                   @Override
                   public Object apply(final String value) {
                      return NEWLINE_PATTERN.matcher(value).replaceAll("");
                   }
                })
            );
            headers.put(header.toLowerCase(), value);
         }
      }
      for (Entry<String, String> entry : headers.entrySet()) {
         toSign.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
      }
   }

   private void appendHttpHeaders(HttpRequest request, StringBuilder toSign) {
      for (String header : FIRST_HEADERS_TO_SIGN)
         toSign.append(HttpUtils.nullToEmpty(request.getHeaders().get(header))).append("\n");
   }

   @VisibleForTesting
   void appendCanonicalizedResource(HttpRequest request, StringBuilder toSign) {
      // 1. Beginning with an empty string (""), append a forward slash (/), followed by the name of
      // the identity that owns the resource being accessed.
      toSign.append("/").append(creds.get().identity);
      appendUriPath(request, toSign);
   }

   @VisibleForTesting
   void appendUriPath(HttpRequest request, StringBuilder toSign) {
      // 2. Append the resource's encoded URI path
      toSign.append(request.getEndpoint().getRawPath());

      // If the request URI addresses a component of the
      // resource, append the appropriate query string. The query string should include the question
      // mark and the comp parameter (for example, ?comp=metadata). No other parameters should be
      // included on the query string.
      if (request.getEndpoint().getQuery() != null) {
         StringBuilder paramsToSign = new StringBuilder("?");

         String[] params = request.getEndpoint().getQuery().split("&");
         for (String param : params) {
            String[] paramNameAndValue = param.split("=");

            if ("comp".equals(paramNameAndValue[0])) {
               paramsToSign.append(param);
            }
         }

         if (paramsToSign.length() > 1) {
            toSign.append(paramsToSign);
         }
      }
   }

}
