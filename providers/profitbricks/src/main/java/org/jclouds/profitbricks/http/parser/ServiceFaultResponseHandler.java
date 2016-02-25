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
package org.jclouds.profitbricks.http.parser;

import org.jclouds.profitbricks.domain.ServiceFault;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ServiceFaultResponseHandler extends BaseProfitBricksResponseHandler<ServiceFault> {

   private final ServiceFault.Builder builder;
   private ServiceFault.Details.Builder detailsBuilder;
   private boolean done = false;

   ServiceFaultResponseHandler() {
      this.builder = ServiceFault.builder();
   }
   
   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if ("detail".equals(qName)) {
         detailsBuilder = ServiceFault.Details.builder();
      }
   }

   @Override
   protected void setPropertyOnEndTag(String qName) {
      if ("faultcode".equals(qName))
         builder.faultCode(textToStringValue());
      else if ("faultstring".equals(qName))
         builder.faultString(textToStringValue());
      else if ("faultCode".equals(qName))
         detailsBuilder.faultCode(ServiceFault.Details.FaultCode.fromValue(textToStringValue()));
      else if ("httpCode".equals(qName))
         detailsBuilder.httpCode(textToIntValue());
      else if ("message".equals(qName))
         detailsBuilder.message(textToStringValue());
      else if ("requestId".equals(qName))
         detailsBuilder.requestId(textToIntValue());
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (done)
         return;
      setPropertyOnEndTag(qName);
      if ("S:Fault".equals(qName))
         done = true;
      if ("detail".equals(qName))
         builder.details(detailsBuilder.build());
      clearTextBuffer();
   }

   @Override
   public ServiceFault getResult() {
      return builder.build();
   }

}
