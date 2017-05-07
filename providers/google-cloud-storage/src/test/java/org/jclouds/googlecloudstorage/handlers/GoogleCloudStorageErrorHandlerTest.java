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
package org.jclouds.googlecloudstorage.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "GoogleStorageErrorHandlerTest")
public class GoogleCloudStorageErrorHandlerTest {

   @Test
   public void test409MakesIllegalStateException() {
      assertCodeMakes("POST", URI.create("https://www.googleapis.com/storage/v1"), 409, "HTTP/1.1 409 Conflict",
               "\"{\"code\":\"InvalidState\",\"message\":\"An incompatible transition has already been queued for this"
                        + " resource\"}\"", IllegalStateException.class);
   }

   @Test
   public void test401MakesAuthorizationException() {
      assertCodeMakes("POST", URI.create("https://www.googleapis.com/storage/v1"), 401, "HTTP/1.1 401 Unauthorized",
               "Login Required", AuthorizationException.class);
   }

   @Test
   public void test403MakesAuthorizationException() {
      assertCodeMakes("POST", URI.create("https://www.googleapis.com/storage/v1"), 403, "HTTP/1.1 403 Forbidden",
               "Login Required", AuthorizationException.class);
   }

   @Test
   public void test404MakesResourceNotFoundException() {
      assertCodeMakes("POST", URI.create("https://www.googleapis.com/storage/v1"), 404, "HTTP/1.1 404 Not Found",
               "Not Found", ResourceNotFoundException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
            Class<? extends Exception> expected) {
      assertCodeMakes(method, uri, statusCode, message, "application/json", content, expected);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
            String content, Class<? extends Exception> expected) {

      GoogleCloudStorageErrorHandler function = new GoogleCloudStorageErrorHandler();

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = HttpRequest.builder().method(method).endpoint(uri).build();
      HttpResponse response = HttpResponse.builder().statusCode(statusCode).message(message).payload(content).build();
      response.getPayload().getContentMetadata().setContentType(contentType);

      expect(command.getCurrentRequest()).andReturn(request).atLeastOnce();
      command.setException(classEq(expected));

      replay(command);

      function.handleError(command, response);

      verify(command);
   }

   public static Exception classEq(final Class<? extends Exception> in) {
      reportMatcher(new IArgumentMatcher() {

         @Override
         public void appendTo(StringBuffer buffer) {
            buffer.append("classEq(");
            buffer.append(in);
            buffer.append(")");
         }

         @Override
         public boolean matches(Object arg) {
            return arg.getClass() == in;
         }

      });
      return null;
   }

}
