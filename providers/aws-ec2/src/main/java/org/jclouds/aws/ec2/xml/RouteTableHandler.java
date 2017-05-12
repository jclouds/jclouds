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

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.RouteTable;
import org.jclouds.ec2.xml.TagSetHandler;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

public class RouteTableHandler extends ParseSax.HandlerWithResult<RouteTable> {

   RouteTable.Builder builder = RouteTable.builder();
   private StringBuilder currentText = new StringBuilder();
   private RouteSetHandler routeSetHandler;
   private RouteTableAssociationSetHandler routeTableAssociationSetHandler;
   private TagSetHandler tagSetHandler;
   boolean inRouteSet;
   boolean inRouteTableAssociationSet;
   boolean inTagSet;
   // TODO propagatingVgwSetHandler


   @Inject
   RouteTableHandler(TagSetHandler tagSetHandler, RouteSetHandler routeSetHandler,
                     RouteTableAssociationSetHandler routeTableAssociationSetHandler) {
      this.tagSetHandler = tagSetHandler;
      this.routeSetHandler = routeSetHandler;
      this.routeTableAssociationSetHandler = routeTableAssociationSetHandler;
   }

   @Override
   public RouteTable getResult() {
      try {
         return builder.build();
      } finally {
         builder = RouteTable.builder();
      }
   }


   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) {
      currentText.setLength(0);
      if (equalsOrSuffix(qName, "routeSet")) {
         inRouteSet = true;
      } else if (equalsOrSuffix(qName, "associationSet")) {
         inRouteTableAssociationSet = true;
      } else if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = true;
      }

      if (inTagSet) {
         tagSetHandler.startElement(uri, name, qName, attrs);
      } else if (inRouteTableAssociationSet) {
         routeTableAssociationSetHandler.startElement(uri, name, qName, attrs);
      } else if (inRouteSet) {
         routeSetHandler.startElement(uri, name, qName, attrs);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
         builder.tags(tagSetHandler.getResult());
      } else if (equalsOrSuffix(qName, "routeSet")) {
         inRouteSet = false;
         builder.routeSet(routeSetHandler.getResult());
      } else if (equalsOrSuffix(qName, "associationSet")) {
         inRouteTableAssociationSet = false;
         builder.associationSet(routeTableAssociationSetHandler.getResult());
      } else if (inRouteSet) {
         routeSetHandler.endElement(uri, name, qName);
      } else if (inRouteTableAssociationSet) {
         routeTableAssociationSetHandler.endElement(uri, name, qName);
      } else if (inTagSet) {
         tagSetHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "vpcId")) {
         builder.vpcId(currentText.toString());
      } else if (equalsOrSuffix(qName, "routeTableId")) {
         builder.id(currentText.toString());
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (inRouteSet) {
         routeSetHandler.characters(ch, start, length);
      } else if (inRouteTableAssociationSet) {
         routeTableAssociationSetHandler.characters(ch, start, length);
      } else if (inTagSet) {
         tagSetHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
