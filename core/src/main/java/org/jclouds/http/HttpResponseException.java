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

import org.jclouds.io.Payload;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.Strings2;

import java.io.IOException;

/**
 * Represents an error obtained from an HttpResponse.
 */
public class HttpResponseException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   protected final HttpCommand command;
   protected final HttpResponse response;
   private String content;

   public HttpResponseException(String message, HttpCommand command, @Nullable HttpResponse response, Throwable cause) {
      super(message, cause);
      this.command = command;
      this.response = response;
   }

   public HttpResponseException(String message, HttpCommand command, @Nullable HttpResponse response,
         String content, Throwable cause) {
      super(message, cause);
      this.command = command;
      this.response = response;
      this.content = content;
   }

   public HttpResponseException(HttpCommand command, HttpResponse response, Throwable cause) {
      this(String.format("command: %1$s failed with response: %2$s", command.getCurrentRequest().getRequestLine(),
            response.getStatusLine()), command, response, cause);
   }

   public HttpResponseException(HttpCommand command, HttpResponse response, String content, Throwable cause) {
      this(String.format("command: %1$s failed with response: %2$s; content: [%3$s]", command.getCurrentRequest()
            .getRequestLine(), response.getStatusLine(), content), command, response, content, cause);
   }

   public HttpResponseException(String message, HttpCommand command, @Nullable HttpResponse response) {
      super(message);
      this.command = command;
      this.response = response;
   }

   public HttpResponseException(String message, HttpCommand command, @Nullable HttpResponse response, String content) {
      super(message);
      this.command = command;
      this.response = response;
      this.content = content;
   }

   public HttpResponseException(HttpCommand command, HttpResponse response) {
      this(command, response, false);
   }
   public HttpResponseException(HttpCommand command, HttpResponse response, boolean logSensitiveInformation) {
      this(String.format("request: %s %sfailed with response: %s", command.getCurrentRequest().getRequestLine(),
            requestPayloadIfStringOrFormIfNotReturnEmptyString(command.getCurrentRequest(), logSensitiveInformation),
            response.getStatusLine()), command, response);
   }

   static String requestPayloadIfStringOrFormIfNotReturnEmptyString(HttpRequest request) {
      return requestPayloadIfStringOrFormIfNotReturnEmptyString(request, false);
   }

   static String requestPayloadIfStringOrFormIfNotReturnEmptyString(HttpRequest request, boolean logSensitiveInformation) {
      Payload payload = request.getPayload();
      if (payload != null
            && ("application/x-www-form-urlencoded".equals(payload.getContentMetadata().getContentType()) || payload instanceof StringPayload)
            && payload.getContentMetadata().getContentLength() != null
            && payload.getContentMetadata().getContentLength() < 1024) {
         try {
            String logStatement;
            if (payload.isSensitive() && !logSensitiveInformation) {
               logStatement = "Sensitive data in payload, use PROPERTY_LOGGER_WIRE_LOG_SENSITIVE_INFO override to enable logging this data.";
            } else if (payload instanceof StringPayload) {
               logStatement = payload.getRawContent().toString();
            } else {
               logStatement = Strings2.toStringAndClose(payload.openStream());
            }
            return String.format(" [%s] ", logStatement);
         } catch (IOException e) {
         }
      }
      return "";
   }

   public HttpResponseException(HttpCommand command, HttpResponse response, String content) {
      this(String.format("command: %s failed with response: %s; content: [%s]", command.getCurrentRequest()
            .getRequestLine(), response.getStatusLine(), content), command, response, content);
   }

   public HttpCommand getCommand() {
      return command;
   }

   @Nullable
   public HttpResponse getResponse() {
      return response;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getContent() {
      return content;
   }

}
