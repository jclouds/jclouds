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

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.RouteTable;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

/**
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_RouteTable.html">RouteTable docs</a>
 */
public class CreateRouteTableResponseHandler extends ParseSax.HandlerForGeneratedRequestWithResult<RouteTable> {

   private RouteTableHandler routeTableHandler;

   @Inject
   CreateRouteTableResponseHandler(RouteTableHandler routeTableHandler) {
      this.routeTableHandler = routeTableHandler;
   }

   public RouteTable getResult() {
      return routeTableHandler.getResult();
   }

   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) {
      routeTableHandler.startElement(uri, name, qName, attrs);
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      routeTableHandler.endElement(uri, name, qName);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      routeTableHandler.characters(ch, start, length);
   }
}
