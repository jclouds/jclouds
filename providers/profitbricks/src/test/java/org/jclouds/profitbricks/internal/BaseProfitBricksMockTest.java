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
package org.jclouds.profitbricks.internal;

import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.ContextBuilder;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.profitbricks.ProfitBricksApi;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Base class for all ProfitBricks mock test
 */
public class BaseProfitBricksMockTest {

   protected static final String authHeader = BasicAuthentication.basic("username", "password");
   protected static final String provider = "profitbricks";
   protected static final String rootUrl = "/1.3";
   
   private static final String SOAP_PREFIX
	   = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.profitbricks.com/\">"
	   + "<soapenv:Header/>"
	   + "<soapenv:Body>";

   private static final String SOAP_SUFFIX = "</soapenv:Body></soapenv:Envelope>";

   private final Set<Module> modules = ImmutableSet.<Module>of();

   public BaseProfitBricksMockTest() {
   }

   public ProfitBricksApi api(URL url) {
      return ContextBuilder.newBuilder(provider)
              .credentials("username", "password")
              .endpoint(url.toString())
              .modules(modules)
              .overrides(setupProperties())
              .buildApi(ProfitBricksApi.class);
   }

   protected Properties setupProperties() {
      return new Properties();
   }

   public static MockWebServer mockWebServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      return server;
   }
   
   public byte[] payloadFromResource(String resource) {
      try {
         return toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
   
   protected static String payloadSoapWithBody(String body){
      return SOAP_PREFIX.concat( body ).concat( SOAP_SUFFIX );
   }

   protected static void assertRequestHasCommonProperties(final RecordedRequest request) {
      assertEquals(request.getMethod(), "POST");
      assertEquals(request.getPath(), rootUrl);
      assertEquals(request.getHeader(HttpHeaders.AUTHORIZATION), authHeader);
      assertEquals(request.getHeader(HttpHeaders.ACCEPT), MediaType.TEXT_XML);
   }
   
   protected static void assertRequestHasCommonProperties(final RecordedRequest request, String content ){
      assertEquals( new String( request.getBody() ), payloadSoapWithBody( content ) );
      assertRequestHasCommonProperties( request );
   }
}
