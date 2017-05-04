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
package org.jclouds.aws.ec2.xml;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.aws.ec2.domain.InternetGateway;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_DescribeVpcs.html">xml</a>
 */
public class DescribeInternetGatewaysResponseHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<InternetGateway>> {
   private final InternetGatewayHandler gatewayHandler;
   private boolean inAttachmentSet;
   private boolean inTagSet;
   private Builder<InternetGateway> gateways = ImmutableSet.builder();

   @Inject
   DescribeInternetGatewaysResponseHandler(InternetGatewayHandler gatewayHandler) {
      this.gatewayHandler = gatewayHandler;
   }

   @Override
   public FluentIterable<InternetGateway> getResult() {
      try {
         return FluentIterable.from(gateways.build());
      } finally {
         gateways = ImmutableSet.builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "attachmentSet")) {
         inAttachmentSet = true;
      } else if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = true;
      }
      gatewayHandler.startElement(url, name, qName, attributes);
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "attachmentSet")) {
         inAttachmentSet = false;
         gatewayHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
         gatewayHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "item") && !inTagSet && !inAttachmentSet) {
         gateways.add(gatewayHandler.getResult());
      } else {
         gatewayHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      gatewayHandler.characters(ch, start, length);
   }

}
