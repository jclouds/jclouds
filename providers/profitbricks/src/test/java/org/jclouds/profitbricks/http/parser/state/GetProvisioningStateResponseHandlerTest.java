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
package org.jclouds.profitbricks.http.parser.state;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "GetProvisioningStateResponseHandlerTest")
public class GetProvisioningStateResponseHandlerTest extends BaseResponseHandlerTest<ProvisioningState> {

   @Override
   protected ParseSax<ProvisioningState> createParser() {
      return factory.create(injector.getInstance(GetProvisioningStateResponseHandler.class));
   }

   @Test
   public void testParseResponseFromGetProvisioningState() {
      ParseSax<ProvisioningState> parser = createParser();

      for (Map.Entry<ProvisioningState, String> pair : sampleResponses.entrySet()) {
         ProvisioningState actual = parser.parse(pair.getValue());
         assertNotNull(actual, "Parsed content returned null");

         assertEquals(pair.getKey(), actual);
      }

   }

   private final Map<ProvisioningState, String> sampleResponses = new LinkedHashMap<ProvisioningState, String>() {
      {
         put(ProvisioningState.INACTIVE,
                 "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.profitbricks.com/\">\n"
                 + "   <soapenv:Header/>\n"
                 + "   <soapenv:Body>\n"
                 + "      <ws:getDataCenterStateResponse>\n"
                 + "         <return>INACTIVE</return>\n"
                 + "      </ws:getDataCenterStateResponse>\n"
                 + "   </soapenv:Body>\n"
                 + "</soapenv:Envelope>");
         put(ProvisioningState.INPROCESS,
                 "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.profitbricks.com/\">\n"
                 + "   <soapenv:Header/>\n"
                 + "   <soapenv:Body>\n"
                 + "      <ws:getDataCenterStateResponse>\n"
                 + "         <return>INPROCESS</return>\n"
                 + "      </ws:getDataCenterStateResponse>\n"
                 + "   </soapenv:Body>\n"
                 + "</soapenv:Envelope>");
         put(ProvisioningState.AVAILABLE,
                 "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.profitbricks.com/\">\n"
                 + "   <soapenv:Header/>\n"
                 + "   <soapenv:Body>\n"
                 + "      <ws:getDataCenterStateResponse>\n"
                 + "         <return>AVAILABLE</return>\n"
                 + "      </ws:getDataCenterStateResponse>\n"
                 + "   </soapenv:Body>\n"
                 + "</soapenv:Envelope>");
         put(ProvisioningState.DELETED,
                 "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.profitbricks.com/\">\n"
                 + "   <soapenv:Header/>\n"
                 + "   <soapenv:Body>\n"
                 + "      <ws:getDataCenterStateResponse>\n"
                 + "         <return>DELETED</return>\n"
                 + "      </ws:getDataCenterStateResponse>\n"
                 + "   </soapenv:Body>\n"
                 + "</soapenv:Envelope>");
         put(ProvisioningState.ERROR,
                 "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.profitbricks.com/\">\n"
                 + "   <soapenv:Header/>\n"
                 + "   <soapenv:Body>\n"
                 + "      <ws:getDataCenterStateResponse>\n"
                 + "         <return>ERROR</return>\n"
                 + "      </ws:getDataCenterStateResponse>\n"
                 + "   </soapenv:Body>\n"
                 + "</soapenv:Envelope>");
         put(ProvisioningState.UNRECOGNIZED,
                 "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.api.profitbricks.com/\">\n"
                 + "   <soapenv:Header/>\n"
                 + "   <soapenv:Body>\n"
                 + "      <ws:getDataCenterStateResponse>\n"
                 + "         <return>MEH</return>\n"
                 + "      </ws:getDataCenterStateResponse>\n"
                 + "   </soapenv:Body>\n"
                 + "</soapenv:Envelope>");
      }
   };

}
