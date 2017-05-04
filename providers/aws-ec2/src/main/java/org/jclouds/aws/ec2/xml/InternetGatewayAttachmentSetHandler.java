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


import java.util.List;

import org.jclouds.aws.ec2.domain.InternetGatewayAttachment;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;

public class InternetGatewayAttachmentSetHandler extends ParseSax.HandlerWithResult<List<InternetGatewayAttachment>> {

   private StringBuilder currentText = new StringBuilder();
   private List<InternetGatewayAttachment> result = Lists.newArrayList();
   private InternetGatewayAttachment.Builder itemBuilder;

   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) {
      currentText.setLength(0);
      if (qName.equalsIgnoreCase("item")) {
         itemBuilder = InternetGatewayAttachment.builder();
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (itemBuilder == null) {
         return;
      }
      if (qName.equalsIgnoreCase("item")) {
         result.add(itemBuilder.build());
         itemBuilder = null;
      } else if (qName.equalsIgnoreCase("vpcId")) {
         itemBuilder.vpcId(currentText.toString());
      } else if (qName.equalsIgnoreCase("state")) {
         itemBuilder.state(InternetGatewayAttachment.State.valueOf(currentText.toString().toUpperCase()));
      }
   }

   @Override
   public List<InternetGatewayAttachment> getResult() {
      try {
         return result;
      } finally {
         result = Lists.newArrayList();
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      currentText.append(ch, start, length);
   }
}
