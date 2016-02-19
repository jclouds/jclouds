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

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link ProfitBricksSoapMessageEnvelope} class.
 */
@Test(groups = "unit", testName = "ProfitBricksSoapMessageEnvelopeTest")
public class ProfitBricksSoapMessageEnvelopeTest {

   private final String SOAP_PREFIX
           = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.profitbricks.com/\">"
           + "<soapenv:Header/>"
           + "<soapenv:Body>";
   private final String SOAP_SUFFIX = "</soapenv:Body></soapenv:Envelope>";
   private final String endpoint = "https://api.profitbricks.com/1.3";

   @Test
   public void testPayloadEnclosedWithSoapTags() {
      String requestBody = "<ws:getAllDataCenters/>";
      String expectedPayload = SOAP_PREFIX.concat(requestBody).concat(SOAP_SUFFIX);

      HttpRequest request = HttpRequest.builder().method("POST").endpoint(endpoint).payload(requestBody).build();

      ProfitBricksSoapMessageEnvelope soapEnvelope = new ProfitBricksSoapMessageEnvelope();
      HttpRequest filtered = soapEnvelope.filter(request);

      assertEquals(filtered.getPayload().getRawContent(), expectedPayload);
      assertEquals(filtered.getPayload().getContentMetadata().getContentLength(), Long.valueOf(expectedPayload.getBytes().length));
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = ".*must contain payload message.*")
   public void testNullRequest() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(endpoint).build();
      new ProfitBricksSoapMessageEnvelope().filter(request);
   }

}
