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
package org.jclouds.profitbricks.http.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;

/**
 * Filters {@link HttpRequest} request and wraps request body into SOAP envelope.
 */
public class ProfitBricksSoapMessageEnvelope implements HttpRequestFilter {

   private final String SOAP_PREFIX
           = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.profitbricks.com/\">"
           + "<soapenv:Header/>"
           + "<soapenv:Body>";

   private final String SOAP_SUFFIX = "</soapenv:Body></soapenv:Envelope>";

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      checkNotNull(request.getPayload(), "HTTP Request must contain payload message.");
      return createSoapRequest(request);
   }

   private HttpRequest createSoapRequest(HttpRequest request) {
      Payload oldPayload = request.getPayload();
      ContentMetadata oldMetadata = oldPayload.getContentMetadata();

      String body = SOAP_PREFIX.concat(oldPayload.getRawContent().toString()).concat(SOAP_SUFFIX);
      Payload newPayload = Payloads.newStringPayload(body);
      HttpUtils.copy(oldMetadata, newPayload.getContentMetadata());
      newPayload.getContentMetadata().setContentLength(Long.valueOf(body.getBytes().length)); // resize, add prefix/suffix length

      return request.toBuilder().payload(newPayload).build();
   }

}
