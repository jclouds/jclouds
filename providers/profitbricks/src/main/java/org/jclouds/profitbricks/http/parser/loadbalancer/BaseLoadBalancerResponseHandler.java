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
package org.jclouds.profitbricks.http.parser.loadbalancer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import org.jclouds.date.DateService;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.domain.LoadBalancer.Algorithm;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.http.parser.BaseProfitBricksResponseHandler;
import org.jclouds.profitbricks.http.parser.firewall.FirewallListResponseHandler;
import org.jclouds.profitbricks.http.parser.server.ServerListResponseHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class BaseLoadBalancerResponseHandler<T> extends BaseProfitBricksResponseHandler<T> {

   protected final ServerListResponseHandler balancedServerResponseHandler;
   protected final FirewallListResponseHandler firewallListResponseHandler;

   protected LoadBalancer.Builder builder;
   protected DataCenter.Builder dataCenterBuilder;

   protected final DateService dateService;

   protected boolean useBalancedServerParser = false;
   protected boolean useFirewallParser = false;

   protected BaseLoadBalancerResponseHandler(DateService dateService,
           ServerListResponseHandler balancedServerResponseHandler, FirewallListResponseHandler firewallResponseHandler) {

      checkNotNull(dateService, "DateService cannot be null");
      checkNotNull(balancedServerResponseHandler, "BalancedServerResponseHandler cannot be null");
      checkNotNull(firewallResponseHandler, "FirewallListResponseHandler cannot be null");

      this.dateService = dateService;
      this.builder = LoadBalancer.builder();
      this.dataCenterBuilder = DataCenter.builder();

      this.balancedServerResponseHandler = balancedServerResponseHandler;
      this.firewallListResponseHandler = firewallResponseHandler;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if ("balancedServers".equals(qName))
         useBalancedServerParser = true;
      if ("firewall".equals(qName))
         useFirewallParser = true;

      if (useBalancedServerParser)
         balancedServerResponseHandler.startElement(uri, localName, qName, attributes);
      else if (useFirewallParser)
         firewallListResponseHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (useBalancedServerParser)
         balancedServerResponseHandler.characters(ch, start, length);
      else if (useFirewallParser)
         firewallListResponseHandler.characters(ch, start, length);
      else
         super.characters(ch, start, length);
   }

   protected final Date textToIso8601Date() {
      return dateService.iso8601DateOrSecondsDateParse(textToStringValue());
   }

   @Override
   protected void setPropertyOnEndTag(String qName) {
      if ("loadBalancerId".equals(qName))
         builder.id(textToStringValue());
      else if ("loadBalancerName".equals(qName))
         builder.name(textToStringValue());
      else if ("loadBalancerAlgorithm".equals(qName))
         builder.algorithm(Algorithm.fromValue(textToStringValue()));
      else if ("dataCenterId".equals(qName))
         dataCenterBuilder.id(textToStringValue());
      else if ("dataCenterVersion".equals(qName))
         dataCenterBuilder.version(textToIntValue());
      else if ("internetAccess".equals(qName))
         builder.internetAccess(textToBooleanValue());
      else if ("ip".equals(qName))
         builder.ip(textToStringValue());
      else if ("lanId".equals(qName))
         builder.lanId(textToIntValue());
      else if ("provisioningState".equals(qName))
         builder.state(ProvisioningState.fromValue(textToStringValue()));
      else if ("creationTime".equals(qName))
         builder.creationTime(textToIso8601Date());
      else if ("lastModificationTime".equals(qName))
         builder.lastModificationTime(textToIso8601Date());
   }
}
