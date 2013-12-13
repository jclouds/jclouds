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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.jclouds.io.ContentMetadataCodec;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.squareup.okhttp.OkHttpClient;

/**
 * Implementation of the <code>HttpCommandExecutorService</code> that uses the
 * OkHttp client to support modern HTTP methods such as PATCH.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class OkHttpCommandExecutorService extends JavaUrlHttpCommandExecutorService {

   @Inject
   public OkHttpCommandExecutorService(HttpUtils utils, ContentMetadataCodec contentMetadataCodec,
         @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor,
         DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
         DelegatingErrorHandler errorHandler, HttpWire wire, @Named("untrusted") HostnameVerifier verifier,
         @Named("untrusted") Supplier<SSLContext> untrustedSSLContextProvider, Function<URI, Proxy> proxyForURI)
         throws SecurityException, NoSuchFieldException {
      super(utils, contentMetadataCodec, ioExecutor, retryHandler, ioRetryHandler, errorHandler, wire, verifier,
            untrustedSSLContextProvider, proxyForURI);
   }

   @Override
   protected HttpURLConnection initConnection(HttpRequest request) throws IOException {
      OkHttpClient client = new OkHttpClient();
      URL url = request.getEndpoint().toURL();
      client.setProxy(proxyForURI.apply(request.getEndpoint()));
      if (url.getProtocol().equalsIgnoreCase("https")) {
         if (utils.relaxHostname()) {
            client.setHostnameVerifier(verifier);
         }
         if (sslContextSupplier != null) {
            // used for providers which e.g. use certs for authentication (like
            // FGCP) Provider provides SSLContext impl (which inits context with
            // key manager)
            client.setSslSocketFactory(sslContextSupplier.get().getSocketFactory());
         } else if (utils.trustAllCerts()) {
            client.setSslSocketFactory(untrustedSSLContextProvider.get().getSocketFactory());
         }
      }
      return client.open(url);
   }

   @Override
   protected void configureRequestHeaders(HttpURLConnection connection, HttpRequest request) {
      super.configureRequestHeaders(connection, request);
      // OkHttp does not set the Accept header if not present in the request.
      // Make sure we send a flexible one.
      if (request.getFirstHeaderOrNull(HttpHeaders.ACCEPT) == null) {
         connection.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
      }
   }

}
