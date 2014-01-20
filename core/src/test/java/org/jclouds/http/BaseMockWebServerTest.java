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
package org.jclouds.http;

import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.jclouds.ContextBuilder;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.testng.annotations.BeforeClass;

import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import com.google.inject.Module;
import com.squareup.okhttp.internal.SslContextBuilder;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.QueueDispatcher;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Base class for integration tests that use {@link MockWebServer} to verify the
 * behavior of the HTTP workflows.
 * 
 * @author Ignasi Barrera
 */
public abstract class BaseMockWebServerTest {

   protected SSLContext sslContext;

   @BeforeClass(groups = "integration")
   protected void setupSSL() {
      try {
         sslContext = new SslContextBuilder(InetAddress.getLocalHost().getHostName()).build();
      } catch (GeneralSecurityException ex) {
         throw new RuntimeException(ex);
      } catch (UnknownHostException ex) {
         throw new RuntimeException(ex);
      }
   }

   protected static class GlobalChecksRequestDispatcher extends QueueDispatcher {
      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
         MockResponse response = responseQueue.take();
         if (!HttpRequest.NON_PAYLOAD_METHODS.contains(request.getMethod())
               && request.getHeader(HttpHeaders.CONTENT_LENGTH) == null) {
            response.setResponseCode(500);
            response.setBody("No content length!");
         }
         return response;
      }
   }

   /**
    * Creates a {@link MockWebServer} that uses the
    * {@link GlobalChecksRequestDispatcher}.
    */
   protected static MockWebServer mockWebServer(MockResponse... responses) throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      server.setDispatcher(new GlobalChecksRequestDispatcher());
      for (MockResponse response : responses) {
         server.enqueue(response);
      }
      return server;
   }

   /**
    * Creates a {@link MockWebServer} that uses the given {@link Dispatcher}.
    */
   protected static MockWebServer mockWebServer(Dispatcher dispatcher) throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      server.setDispatcher(dispatcher);
      return server;
   }

   /**
    * Creates a test api for the given class and URL.
    */
   protected <T extends Closeable> T api(Class<T> apiClass, String url) {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_TRUST_ALL_CERTS, "true");
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, "true");
      addOverrideProperties(properties);
      return ContextBuilder.newBuilder(AnonymousProviderMetadata.forApiOnEndpoint(apiClass, url))
            .modules(ImmutableSet.<Module> of(createConnectionModule())).overrides(properties).buildApi(apiClass);
   }

   /**
    * Add the connection properties used to configure the tests.
    */
   protected abstract void addOverrideProperties(Properties props);

   /**
    * Return the connection module that provides the HTTP driver to use in the
    * tests.
    * <p>
    * Unless a concrete HTTP is required, subclasses may want to use the
    * {@link JavaUrlHttpCommandExecutorServiceModule}.
    */
   protected abstract Module createConnectionModule();

}
