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

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.InternetGateway;
import org.jclouds.ec2.xml.TagSetHandler;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

/**
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_InternetGateway.html">InternetGateway docs</a>
 */
public class InternetGatewayHandler extends ParseSax.HandlerForGeneratedRequestWithResult<InternetGateway> {
   private StringBuilder currentText = new StringBuilder();
   private InternetGateway.Builder builder = InternetGateway.builder();
   private final TagSetHandler tagSetHandler;
   private final InternetGatewayAttachmentSetHandler attachmentSetHandler;
   private boolean inTagSet;
   private boolean inAttachmentSet;


   @Inject
   InternetGatewayHandler(TagSetHandler tagSetHandler, InternetGatewayAttachmentSetHandler attachmentHandler) {
      this.tagSetHandler = tagSetHandler;
      this.attachmentSetHandler = attachmentHandler;
   }

   @Override
   public InternetGateway getResult() {
      try {
         return builder.build();
      } finally {
         builder = InternetGateway.builder();
      }
   }

   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) {
      currentText.setLength(0);
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = true;
      }
      if (equalsOrSuffix(qName, "attachmentSet")) {
         inAttachmentSet = true;
      }
      if (inTagSet) {
         tagSetHandler.startElement(uri, name, qName, attrs);
      } else if (inAttachmentSet) {
         attachmentSetHandler.startElement(uri, name, qName, attrs);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
         builder.tags(tagSetHandler.getResult());
      } else if (equalsOrSuffix(qName, "attachmentSet")) {
         inAttachmentSet = false;
         builder.attachmentSet(attachmentSetHandler.getResult());
      } else if (inTagSet) {
         tagSetHandler.endElement(uri, name, qName);
      } else if (inAttachmentSet) {
         attachmentSetHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "internetGatewayId")) {
         builder.id(currentOrNull(currentText));
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (inTagSet) {
         tagSetHandler.characters(ch, start, length);
      } else if (inAttachmentSet) {
         attachmentSetHandler.characters(ch, start, length);
      } else {
        currentText.append(ch, start, length);
      }
   }
}
