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
package org.jclouds.http.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.HOST;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static org.jclouds.http.HttpUtils.filterOutContentHeaders;
import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.jclouds.util.Closeables2.closeQuietly;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.jclouds.JcloudsVersion;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingOutputStream;
import com.google.inject.Inject;

@Singleton
public class JavaUrlHttpCommandExecutorService extends BaseHttpCommandExecutorService<HttpURLConnection> {

   public static final String DEFAULT_USER_AGENT = String.format("jclouds/%s java/%s", JcloudsVersion.get(),
         System.getProperty("java.version"));

   protected final Supplier<SSLContext> untrustedSSLContextProvider;
   protected final Function<URI, Proxy> proxyForURI;
   protected final HostnameVerifier verifier;
   @Inject(optional = true)
   protected Supplier<SSLContext> sslContextSupplier;

   @Inject
   public JavaUrlHttpCommandExecutorService(HttpUtils utils, ContentMetadataCodec contentMetadataCodec,
         DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
         DelegatingErrorHandler errorHandler, HttpWire wire, @Named("untrusted") HostnameVerifier verifier,
         @Named("untrusted") Supplier<SSLContext> untrustedSSLContextProvider, Function<URI, Proxy> proxyForURI) {
      super(utils, contentMetadataCodec, retryHandler, ioRetryHandler, errorHandler, wire);
      if (utils.getMaxConnections() > 0) {
         System.setProperty("http.maxConnections", String.valueOf(checkNotNull(utils, "utils").getMaxConnections()));
      }
      this.untrustedSSLContextProvider = checkNotNull(untrustedSSLContextProvider, "untrustedSSLContextProvider");
      this.verifier = checkNotNull(verifier, "verifier");
      this.proxyForURI = checkNotNull(proxyForURI, "proxyForURI");
   }

   @Override
   protected HttpResponse invoke(HttpURLConnection connection) throws IOException, InterruptedException {
      HttpResponse.Builder<?> builder = HttpResponse.builder();
      InputStream in = null;
      try {
         in = connection.getInputStream();
      } catch (IOException e) {
         in = connection.getErrorStream();
      } catch (RuntimeException e) {
         closeQuietly(in);
         throw e;
      }

      int responseCode = connection.getResponseCode();
      if (responseCode == 204) {
         closeQuietly(in);
         in = null;
      }
      builder.statusCode(responseCode);
      builder.message(connection.getResponseMessage());

      Builder<String, String> headerBuilder = ImmutableMultimap.builder();
      for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
         String header = entry.getKey();
         // HTTP message comes back as a header without a key
         if (header != null)
            headerBuilder.putAll(header, entry.getValue());
      }
      ImmutableMultimap<String, String> headers = headerBuilder.build();
      if (in != null) {
         Payload payload = newInputStreamPayload(in);
         contentMetadataCodec.fromHeaders(payload.getContentMetadata(), headers);
         builder.payload(payload);
      }
      builder.headers(filterOutContentHeaders(headers));
      return builder.build();
   }

   @Override
   protected HttpURLConnection convert(HttpRequest request) throws IOException, InterruptedException {
      boolean chunked = "chunked".equals(request.getFirstHeaderOrNull("Transfer-Encoding"));

      HttpURLConnection connection = initConnection(request);
      connection.setConnectTimeout(utils.getConnectionTimeout());
      connection.setReadTimeout(utils.getSocketOpenTimeout());
      connection.setAllowUserInteraction(false);
      // do not follow redirects since https redirects don't work properly
      // ex. Caused by: java.io.IOException: HTTPS hostname wrong: should be
      // <adriancole.s3int0.s3-external-3.amazonaws.com>
      connection.setInstanceFollowRedirects(false);

      setRequestMethodBypassingJREMethodLimitation(connection, request.getMethod());
      configureRequestHeaders(connection, request);

      String host = request.getEndpoint().getHost();
      if (request.getEndpoint().getPort() != -1) {
         host += ":" + request.getEndpoint().getPort();
      }
      connection.setRequestProperty(HOST, host);
      if (connection.getRequestProperty(USER_AGENT) == null) {
          connection.setRequestProperty(USER_AGENT, DEFAULT_USER_AGENT);
      }
      Payload payload = request.getPayload();
      if (payload != null) {
         MutableContentMetadata md = payload.getContentMetadata();
         for (Map.Entry<String, String> entry : contentMetadataCodec.toHeaders(md).entries()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
         }
         if (chunked) {
            connection.setChunkedStreamingMode(8196);
            writePayloadToConnection(payload, "streaming", connection);
         } else {
            Long length = checkNotNull(md.getContentLength(), "payload.getContentLength");
            if (length > 0) {
               connection.setRequestProperty(CONTENT_LENGTH, length.toString());
               if (length <= Integer.MAX_VALUE) {
                  connection.setFixedLengthStreamingMode(length.intValue());
               } else {
                  setFixedLengthStreamingMode(connection, length);
               }
               writePayloadToConnection(payload, length, connection);
            } else {
               writeNothing(connection);
            }
         }
      } else {
         writeNothing(connection);
      }
      return connection;
   }

   /** Uses {@link HttpURLConnection#setFixedLengthStreamingMode(long)} if possible or throws if not. */
   private static void setFixedLengthStreamingMode(HttpURLConnection connection, long length) {
      try { // Not caching method as invocation is literally sending > 2GB, which means reflection isn't a limiter!
         HttpURLConnection.class.getMethod("setFixedLengthStreamingMode", long.class).invoke(connection, length);
      } catch (Exception e) {
         throw new IllegalArgumentException("Cannot transfer 2 GB or larger chunks due to JDK 1.6 limitations." +
               " Use chunked encoding or multi-part upload, if possible, or use a different http driver." +
               " For more information: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6755625");
      }
   }

   /**
    * Creates and initializes the connection.
    */
   protected HttpURLConnection initConnection(HttpRequest request) throws IOException {
      URL url = request.getEndpoint().toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxyForURI.apply(request.getEndpoint()));
      if (connection instanceof HttpsURLConnection) {
         HttpsURLConnection sslCon = (HttpsURLConnection) connection;
         if (utils.relaxHostname())
            sslCon.setHostnameVerifier(verifier);
         if (sslContextSupplier != null) {
             // used for providers which e.g. use certs for authentication (like FGCP)
             // Provider provides SSLContext impl (which inits context with key manager)
             sslCon.setSSLSocketFactory(sslContextSupplier.get().getSocketFactory());
         } else if (utils.trustAllCerts()) {
             sslCon.setSSLSocketFactory(untrustedSSLContextProvider.get().getSocketFactory());
         }
      }
      return connection;
   }

   /**
    * Configure the HTTP request headers in the connection.
    */
   protected void configureRequestHeaders(HttpURLConnection connection, HttpRequest request) {
      for (Map.Entry<String, String> entry : request.getHeaders().entries()) {
         connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
   }

   /**
    * Workaround for a bug in <code>HttpURLConnection.setRequestMethod(String)</code>
    * The implementation of Sun Microsystems is throwing a <code>ProtocolException</code>
    * when the method is other than the HTTP/1.1 default methods. So
    * to use PATCH and others, we must apply this workaround.
    *
    * See issue http://java.net/jira/browse/JERSEY-639
    */
   private void setRequestMethodBypassingJREMethodLimitation(final HttpURLConnection httpURLConnection, final String method) {
      try {
         httpURLConnection.setRequestMethod(method);
         // If the JRE does not support the given method, set it using reflection
      } catch (final ProtocolException pe) {
         Class<?> connectionClass = httpURLConnection.getClass();
         Field delegateField = null;
         try {
            // SSL connections may have the HttpURLConnection wrapped inside
            delegateField = connectionClass.getDeclaredField("delegate");
            delegateField.setAccessible(true);
            HttpURLConnection delegateConnection = (HttpURLConnection) delegateField.get(httpURLConnection);
            setRequestMethodBypassingJREMethodLimitation(delegateConnection, method);
         } catch (NoSuchFieldException e) {
            // Ignore for now, keep going
         } catch (IllegalArgumentException e) {
            logger.error(e, "could not set request method: ", method);
            propagate(e);
         } catch (IllegalAccessException e) {
            logger.error(e, "could not set request method: ", method);
            propagate(e);
         }
         try {
            Field methodField = null;
            while (connectionClass != null) {
               try {
                  methodField = connectionClass.getDeclaredField("method");
               } catch (NoSuchFieldException e) {
                  connectionClass = connectionClass.getSuperclass();
                  continue;
               }
               methodField.setAccessible(true);
               methodField.set(httpURLConnection, method);
               break;
            }
         } catch (final Exception e) {
            logger.error(e, "could not set request method: ", method);
            propagate(e);
         }
      }
   }

   protected void writeNothing(HttpURLConnection connection) {
      if (!HttpRequest.NON_PAYLOAD_METHODS.contains(connection.getRequestMethod())) {
         connection.setRequestProperty(CONTENT_LENGTH, "0");
         // HttpUrlConnection strips Content-Length: 0 without setDoOutput(true)
         String method = connection.getRequestMethod();
         if ("POST".equals(method) || "PUT".equals(method)) {
            connection.setFixedLengthStreamingMode(0);
            connection.setDoOutput(true);
         }
      }
   }

   /**
    * @param payload
    *           payload to write
    * @param lengthDesc
    *           what to use in error log when an IOException occurs
    * @param connection
    *           connection to write to
    */
   void writePayloadToConnection(Payload payload, Object lengthDesc, HttpURLConnection connection) throws IOException {
      connection.setDoOutput(true);
      CountingOutputStream out = new CountingOutputStream(connection.getOutputStream());
      InputStream is = payload.openStream();
      try {
         ByteStreams.copy(is, out);
      } catch (IOException e) {
         logger.error(e, "error after writing %d/%s bytes to %s", out.getCount(), lengthDesc, connection.getURL());
         throw e;
      } finally {
         closeQuietly(is);
      }
   }

   @Override
   protected void cleanup(HttpURLConnection connection) {
      if (connection != null)
         connection.disconnect();
   }
}
