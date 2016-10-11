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

import org.jclouds.aws.ec2.domain.VPC;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_DescribeVpcs.html">xml</a>
 */
public class DescribeVPCsResponseHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<VPC>> {
   private final VPCHandler vpcHandler;

   private StringBuilder currentText = new StringBuilder();
   private boolean inVpcSet;
   private boolean inTagSet;
   private Builder<VPC> vpcs = ImmutableSet.builder();

   @Inject
   public DescribeVPCsResponseHandler(VPCHandler vpcHandler) {
      this.vpcHandler = vpcHandler;
   }

   @Override
   public FluentIterable<VPC> getResult() {
      return FluentIterable.from(vpcs.build());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "vpcSet")) {
         inVpcSet = true;
      } else if (inVpcSet) {
         if (equalsOrSuffix(qName, "tagSet")) {
            inTagSet = true;
         }
         vpcHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "vpcSet")) {
         inVpcSet = false;
      } else if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
         vpcHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "item") && !inTagSet) {
         vpcs.add(vpcHandler.getResult());
      } else if (inVpcSet) {
         vpcHandler.endElement(uri, name, qName);
      }

      currentText.setLength(0);
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inVpcSet) {
         vpcHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
