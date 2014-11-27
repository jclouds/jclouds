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
import com.google.inject.Module;
import com.squareup.okhttp.internal.SslContextBuilder;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Base class for integration tests that use {@link MockWebServer} to verify the
 * behavior of the HTTP workflows.
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

   /**
    * Creates a {@link MockWebServer} that uses the
    * {@link GlobalChecksRequestDispatcher}.
    */
   protected static MockWebServer mockWebServer(MockResponse... responses) throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
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
      return api(apiClass, url, createConnectionModule());
   }

   /**
    * Creates a test api for the given class, URI and Module.
    */
   protected <T extends Closeable> T api(Class<T> apiClass, String url, Module... connectionModules) {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_TRUST_ALL_CERTS, "true");
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, "true");
      addOverrideProperties(properties);
      return ContextBuilder.newBuilder(AnonymousProviderMetadata.forApiOnEndpoint(apiClass, url))
            .modules(ImmutableSet.copyOf(connectionModules)).overrides(properties).buildApi(apiClass);
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
    * {@link org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule}.
    */
   protected abstract Module createConnectionModule();

}
