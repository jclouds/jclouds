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

import java.util.List;

import org.jclouds.aws.ec2.domain.Route;
import org.jclouds.aws.ec2.domain.Route.RouteState;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.Lists;

public class RouteSetHandler extends ParseSax.HandlerForGeneratedRequestWithResult<List<Route>> {

   private StringBuilder currentText = new StringBuilder();
   List<Route> results = Lists.newArrayList();

   Route.Builder builder;

   @Override
   public List<Route> getResult() {
      try {
         return results;
      } finally {
         results = Lists.newArrayList();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      currentText.setLength(0);
      if (qName.equalsIgnoreCase("item")) {
         builder = Route.builder();
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (builder == null) {
         return;
      }
      if (equalsOrSuffix(qName, "item")) {
         results.add(builder.build());
         builder = null;
      } else if (equalsOrSuffix(qName, "destinationCidrBlock")) {
         builder.destinationCidrBlock(currentText.toString());
      } else if (equalsOrSuffix(qName, "gatewayId")) {
         builder.gatewayId(currentText.toString());
      } else if (equalsOrSuffix(qName, "state")) {
         builder.state(RouteState.fromValue(currentText.toString()));
      } else if (equalsOrSuffix(qName, "origin")) {
         builder.origin(currentText.toString());
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      currentText.append(ch, start, length);
   }
}
