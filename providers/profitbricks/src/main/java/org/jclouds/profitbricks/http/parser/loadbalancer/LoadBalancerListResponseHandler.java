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

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import java.util.List;

import org.jclouds.date.DateService;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.http.parser.firewall.FirewallListResponseHandler;
import org.jclouds.profitbricks.http.parser.server.ServerListResponseHandler;
import org.xml.sax.SAXException;

public class LoadBalancerListResponseHandler extends BaseLoadBalancerResponseHandler<List<LoadBalancer>> {

   private final List<LoadBalancer> loadBalancers;

   @Inject
   LoadBalancerListResponseHandler(DateService dateService, ServerListResponseHandler balancedServerResponseHandler, FirewallListResponseHandler firewallListResponseHandler) {
      super(dateService, balancedServerResponseHandler, firewallListResponseHandler);
      this.loadBalancers = Lists.newArrayList();
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (useBalancedServerParser)
         balancedServerResponseHandler.endElement(uri, localName, qName);
      else if (useFirewallParser)
         firewallListResponseHandler.endElement(uri, localName, qName);
      else {
         setPropertyOnEndTag(qName);
         if ("return".equals(qName)) {
            loadBalancers.add(builder
                    .dataCenter(dataCenterBuilder.build())
                    .firewalls(firewallListResponseHandler.getResult())
                    .balancedServers(balancedServerResponseHandler.getResult())
                    .build());

            balancedServerResponseHandler.reset();
            firewallListResponseHandler.reset();

            builder = LoadBalancer.builder();
         }
         clearTextBuffer();
      }
      if ("firewall".equals(qName))
         useFirewallParser = false;
      else if ("balancedServers".equals(qName))
         useBalancedServerParser = false;

   }

   @Override
   public void reset() {
      this.dataCenterBuilder = DataCenter.builder();
   }

   @Override
   public List<LoadBalancer> getResult() {
      return loadBalancers;
   }
}
