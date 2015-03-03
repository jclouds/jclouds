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
package org.jclouds.profitbricks.http.parser.ipblock;

import com.google.common.collect.Lists;
import java.util.List;
import org.jclouds.profitbricks.domain.IpBlock;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.http.parser.BaseProfitBricksResponseHandler;
import org.jclouds.profitbricks.http.parser.publicip.PublicIpListResponseHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class BaseIpBlockResponseHandler<T> extends BaseProfitBricksResponseHandler<T> {

   protected final PublicIpListResponseHandler publicIpListResponseHandler;
   protected final List<IpBlock.PublicIp> publicIps = Lists.newArrayList();
   protected List<String> ips;

   protected IpBlock.Builder builder;
   protected boolean usePublicIpListParser = false;

   BaseIpBlockResponseHandler(PublicIpListResponseHandler publicIpListResponseHandler) {
      this.builder = IpBlock.builder();
      this.publicIpListResponseHandler = publicIpListResponseHandler;
      ips = Lists.newArrayList();
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if ("publicIps".equals(qName))
         usePublicIpListParser = true;
      if (usePublicIpListParser)
         publicIpListResponseHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (usePublicIpListParser)
         publicIpListResponseHandler.characters(ch, start, length);
      else
         super.characters(ch, start, length);
   }

   @Override
   protected void setPropertyOnEndTag(String qName) {
      if ("blockId".equals(qName))
         builder.id(textToStringValue());
      else if ("location".equals(qName))
         builder.location(Location.fromId(textToStringValue()));
      else if ("ips".equals(qName))
         ips.add(textToStringValue());
   }
}
