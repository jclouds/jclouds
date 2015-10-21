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
package org.jclouds.profitbricks.http.parser.nic;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.http.parser.BaseProfitBricksResponseHandler;
import org.jclouds.profitbricks.http.parser.firewall.FirewallResponseHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class BaseNicResponseHandler<T> extends BaseProfitBricksResponseHandler<T> {

   protected final FirewallResponseHandler firewallResponseHandler;

   protected boolean useFirewallParser = false;
   protected Nic.Builder builder;
   protected List<String> ips;

   @Inject
   BaseNicResponseHandler(FirewallResponseHandler firewallResponseHandler) {
      this.builder = Nic.builder();
      this.firewallResponseHandler = firewallResponseHandler;
      this.ips = new ArrayList<String>();
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if ("firewall".equals(qName))
         useFirewallParser = true;
      if (useFirewallParser)
         firewallResponseHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (useFirewallParser)
         firewallResponseHandler.characters(ch, start, length);
      else
         super.characters(ch, start, length);
   }

   @Override
   protected void setPropertyOnEndTag(String qName) {
      if ("dataCenterId".equals(qName))
         builder.dataCenterId(textToStringValue());
      else if ("nicName".equals(qName))
         builder.name(textToStringValue());
      else if ("nicId".equals(qName))
         builder.id(textToStringValue());
      else if ("lanId".equals(qName))
         builder.lanId(textToIntValue());
      else if ("internetAccess".equals(qName))
         builder.internetAccess(textToBooleanValue());
      else if ("serverId".equals(qName))
         builder.serverId(textToStringValue());
      else if ("ips".equals(qName))
         ips.add(textToStringValue());
      else if ("macAddress".equals(qName))
         builder.macAddress(textToStringValue());
      else if ("dhcpActive".equals(qName))
         builder.dhcpActive(textToBooleanValue());
      else if ("gatewayIp".equals(qName))
         builder.gatewayIp(textToStringValue());
      else if ("provisioningState".equals(qName))
         builder.state(ProvisioningState.fromValue(textToStringValue()));
   }
}
