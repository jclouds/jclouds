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
package org.jclouds.http.okhttp;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static org.jclouds.http.HttpUtils.filterOutContentHeaders;
import static org.jclouds.io.Payloads.newInputStreamPayload;

import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import org.jclouds.JcloudsVersion;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.inject.Inject;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public final class OkHttpCommandExecutorService extends BaseHttpCommandExecutorService<Request> {

   private static final String DEFAULT_USER_AGENT = String.format("jclouds-okhttp/%s java/%s", JcloudsVersion.get(),
         System.getProperty("java.version"));

   private final Function<URI, Proxy> proxyForURI;
   private final OkHttpClient globalClient;

   @Inject
   OkHttpCommandExecutorService(HttpUtils utils, ContentMetadataCodec contentMetadataCodec,
         DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
         DelegatingErrorHandler errorHandler, HttpWire wire, Function<URI, Proxy> proxyForURI, OkHttpClient okHttpClient) {
      super(utils, contentMetadataCodec, retryHandler, ioRetryHandler, errorHandler, wire);
      this.proxyForURI = proxyForURI;
      this.globalClient = okHttpClient;
   }

   @Override
   protected Request convert(HttpRequest request) throws IOException, InterruptedException {
      Request.Builder builder = new Request.Builder();

      builder.url(request.getEndpoint().toString());
      populateHeaders(request, builder);

      RequestBody body = null;
      Payload payload = request.getPayload();

      if (payload != null) {
         Long length = checkNotNull(payload.getContentMetadata().getContentLength(), "payload.getContentLength");
         if (length > 0) {
            body = generateRequestBody(request, payload);
         }
      }

      builder.method(request.getMethod(), body);

      return builder.build();
   }

   protected void populateHeaders(HttpRequest request, Request.Builder builder) {
      // OkHttp does not set the Accept header if not present in the request.
      // Make sure we send a flexible one.
      if (request.getFirstHeaderOrNull(ACCEPT) == null) {
         builder.addHeader(ACCEPT, "*/*");
      }
      if (request.getFirstHeaderOrNull(USER_AGENT) == null) {
         builder.addHeader(USER_AGENT, DEFAULT_USER_AGENT);
      }
      for (Map.Entry<String, String> entry : request.getHeaders().entries()) {
         builder.addHeader(entry.getKey(), entry.getValue());
      }
      if (request.getPayload() != null) {
         MutableContentMetadata md = request.getPayload().getContentMetadata();
         for (Map.Entry<String, String> entry : contentMetadataCodec.toHeaders(md).entries()) {
            builder.addHeader(entry.getKey(), entry.getValue());
         }
      }
   }

   protected RequestBody generateRequestBody(final HttpRequest request, final Payload payload) {
      checkNotNull(payload.getContentMetadata().getContentType(), "payload.getContentType");
      return new RequestBody() {
         @Override
         public void writeTo(BufferedSink sink) throws IOException {
            Source source = Okio.source(payload.openStream());
            try {
               sink.writeAll(source);
            } catch (IOException ex) {
               logger.error(ex, "error writing bytes to %s", request.getEndpoint());
               throw ex;
            } finally {
               source.close();
            }
         }

         @Override
         public MediaType contentType() {
            return MediaType.parse(payload.getContentMetadata().getContentType());
         }
      };
   }

   @Override
   protected HttpResponse invoke(Request nativeRequest) throws IOException, InterruptedException {
      OkHttpClient requestScopedClient = globalClient.clone();
      requestScopedClient.setProxy(proxyForURI.apply(nativeRequest.uri()));

      Response response = requestScopedClient.newCall(nativeRequest).execute();

      HttpResponse.Builder<?> builder = HttpResponse.builder();
      builder.statusCode(response.code());
      builder.message(response.message());

      Builder<String, String> headerBuilder = ImmutableMultimap.builder();
      Headers responseHeaders = response.headers();
      for (String header : responseHeaders.names()) {
         headerBuilder.putAll(header, responseHeaders.values(header));
      }

      ImmutableMultimap<String, String> headers = headerBuilder.build();

      if (response.code() == 204 && response.body() != null) {
         response.body().close();
      } else {
         Payload payload = newInputStreamPayload(response.body().byteStream());
         contentMetadataCodec.fromHeaders(payload.getContentMetadata(), headers);
         builder.payload(payload);
      }

      builder.headers(filterOutContentHeaders(headers));

      return builder.build();
   }

   @Override
   protected void cleanup(Request nativeResponse) {

   }

}
