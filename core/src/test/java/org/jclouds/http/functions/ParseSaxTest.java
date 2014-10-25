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
package org.jclouds.http.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.Test;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * Tests behavior of {@code ParseSax}
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ParseSaxTest")
public class ParseSaxTest extends BaseHandlerTest {
   public static class TestHandler extends ParseSax.HandlerWithResult<String> {
      @Override
      public String getResult() {
         return "";
      }
   }

   ParseSax<String> createParser() {
      return factory.create(injector.getInstance(TestHandler.class));
   }
   
   @Test
   public void testAddDetailsAndPropagateOkWhenRequestWithNoDataAndRuntimeExceptionThrowsOriginalException() {

      ParseSax<String> parser = createParser();
      Exception input = new RuntimeException("foo");

      try {
         parser.addDetailsAndPropagate(null, input);
      } catch (RuntimeException e) {
         assertEquals(e, input);
      }
   }

   @Test
   public void testAddDetailsAndPropagateOkWhenRequestWithNoDataAndExceptionPropagates() {

      ParseSax<String> parser = createParser();
      Exception input = new Exception("foo");

      try {
         parser.addDetailsAndPropagate(null, input);
      } catch (RuntimeException e) {
         assertEquals(e.getMessage(), "java.lang.Exception: foo");
         assertEquals(e.getCause(), input);
      }
   }

   @Test
   public void testAddDetailsAndPropagateOkWhenRequestIsNotNullAndResponseIsNull() {

      ParseSax<String> parser = createParser();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://foohost").build(); 
      Exception input = new Exception("foo");

      try {
         parser.setContext(request);
         parser.addDetailsAndPropagate(null, input);
      } catch (RuntimeException e) {
         assertEquals(e.getMessage(), "request: GET http://foohost HTTP/1.1; cause: java.lang.Exception: foo");
         assertEquals(e.getCause(), input);
      }
   }
   
   @Test
   public void testAddDetailsAndPropagateOkWithValidRequestResponse() {

      ParseSax<String> parser = createParser();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://foohost").build();
      HttpResponse response = HttpResponse.builder().statusCode(304).message("Not Modified").build();
      Exception input = new Exception("foo");

      try {
         parser.setContext(request);
         parser.addDetailsAndPropagate(response, input);
      } catch (RuntimeException e) {
         assertEquals(e.getMessage(), "request: GET http://foohost HTTP/1.1; response: HTTP/1.1 304 Not Modified; cause: java.lang.Exception: foo");
         assertEquals(e.getCause(), input);
      }
   }

   @Test
   public void testAddDetailsAndPropagateOkWithValidRequestResponseWithSAXParseException() {

      ParseSax<String> parser = createParser();
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://foohost").build();
      HttpResponse response = HttpResponse.builder().statusCode(304).message("Not Modified").build();
      Locator locator = createMock(Locator.class);
      expect(locator.getColumnNumber()).andReturn(1);
      expect(locator.getLineNumber()).andReturn(1);
      expect(locator.getPublicId()).andReturn("publicId");
      expect(locator.getSystemId()).andReturn("systemId");
      replay(locator);
      Exception input = new SAXParseException("foo", locator);
      verify(locator);

      try {
         parser.setContext(request);
         parser.addDetailsAndPropagate(response, input);
      } catch (RuntimeException e) {
         assertEquals(e.getMessage(), TestUtils.isJava6()
               ? "request: GET http://foohost HTTP/1.1; response: HTTP/1.1 304 Not Modified; error at 1:1 in document systemId; cause: org.xml.sax.SAXParseException: foo"
               : "request: GET http://foohost HTTP/1.1; response: HTTP/1.1 304 Not Modified; error at 1:1 in document systemId; cause: org.xml.sax.SAXParseExceptionpublicId: publicId; systemId: systemId; lineNumber: 1; columnNumber: 1; foo");
         assertEquals(e.getCause(), input);
      }
   }
}
