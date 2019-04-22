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
package org.jclouds.profitbricks.http;

import static org.jclouds.Constants.PROPERTY_IDEMPOTENT_METHODS;
import static org.jclouds.Constants.PROPERTY_OUTPUT_SOCKET_BUFFER_SIZE;
import static org.jclouds.Constants.PROPERTY_USER_AGENT;
import static org.jclouds.util.Closeables2.closeQuietly;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.util.regex.Pattern;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.profitbricks.domain.ServiceFault;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;

/**
 * Custom implementation of the HTTP driver to read actual http status and message from SOAP Fault.
 * <br/>
 * ProfitBricks API errors are always returned with 500 HTTP code. This class parses and reads the SOAP response to map
 * the actual http code and message
 */
@Singleton
public class ResponseStatusFromPayloadHttpCommandExecutorService extends JavaUrlHttpCommandExecutorService {

   private final ParseSax<ServiceFault> faultHandler;

   private static final Pattern endSoapTag = Pattern.compile("</.+:Envelope>$");

   @Inject
   ResponseStatusFromPayloadHttpCommandExecutorService(HttpUtils utils, ContentMetadataCodec contentMetadataCodec,
           DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
           DelegatingErrorHandler errorHandler, HttpWire wire, @Named("untrusted") HostnameVerifier verifier,
           @Named("untrusted") Supplier<SSLContext> untrustedSSLContextProvider, Function<URI, Proxy> proxyForURI,
           ParseSax<ServiceFault> faultHandler,
           @Named(PROPERTY_IDEMPOTENT_METHODS) String idempotentMethods,
           @Named(PROPERTY_OUTPUT_SOCKET_BUFFER_SIZE) int outputSocketBufferSize,
           @Named(PROPERTY_USER_AGENT) String userAgent) {
      super(utils, contentMetadataCodec, retryHandler, ioRetryHandler, errorHandler, wire, verifier, untrustedSSLContextProvider, proxyForURI,
            idempotentMethods, outputSocketBufferSize, userAgent);
      this.faultHandler = faultHandler;
   }

   @Override
   protected HttpResponse invoke(HttpURLConnection connection) throws IOException, InterruptedException {
      HttpResponse originalResponse = super.invoke(connection);
      HttpResponse.Builder<?> responseBuilder = originalResponse.toBuilder();

      if (hasServerError(originalResponse) && hasPayload(originalResponse)) {
         // As we need to read the response body to determine if there are errors, but we may need to process the body
         // again later in the response parsers if everything is OK, we buffer the body into an InputStream we can reset
         InputStream in = null;
         InputStream originalInputStream = originalResponse.getPayload().openStream();

         if (originalInputStream instanceof ByteArrayInputStream)
            in = originalInputStream;
         else
            try {
               in = new ByteArrayInputStream(ByteStreams.toByteArray(originalInputStream));
            } finally {
               closeQuietly(originalInputStream);
            }
         try {
            if (isSoapPayload(in)) {
               ServiceFault fault = faultHandler.parse(in);
               if (fault != null) {
                  if (fault.details() != null) {
                     responseBuilder.statusCode(fault.details().httpCode()).message(fault.details().message());
                  } else {
                     responseBuilder.message(fault.faultString());
                  }
               }
            }
         } catch (Exception ex) {
            // ignore
         } finally {
            // Reset the input stream and set the payload, so it can be read again
            // by the response and error parsers
            if (in != null) {
               in.reset();
               Payload payload = Payloads.newInputStreamPayload(in);
               contentMetadataCodec.fromHeaders(payload.getContentMetadata(), originalResponse.getHeaders());
               responseBuilder.payload(payload);
            }
         }
      }

      return responseBuilder.build();
   }

   private static boolean hasServerError(final HttpResponse response) {
      return response.getStatusCode() >= 500;
   }

   private static boolean hasPayload(final HttpResponse response) {
      return response.getPayload() != null && response.getPayload().getRawContent() != null;
   }

   private static boolean isSoapPayload(final InputStream is) throws IOException {
      int size = is.available();
      char[] chars = new char[size];
      byte[] bytes = new byte[size];

      ByteStreams.readFully(is, bytes);
      for (int i = 0; i < size;)
         chars[i] = (char) (bytes[i++] & 0xff);

      is.reset(); // throws premature end of file w/o this

      return endSoapTag.matcher(new String(chars)).find();
   }
}
