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

import com.google.inject.Inject;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.profitbricks.domain.LoadBalancer;
import org.jclouds.profitbricks.http.parser.firewall.FirewallListResponseHandler;
import org.jclouds.profitbricks.http.parser.server.ServerListResponseHandler;
import org.xml.sax.SAXException;

public class LoadBalancerResponseHandler extends BaseLoadBalancerResponseHandler<LoadBalancer> {

   private boolean done = false;

   @Inject
   LoadBalancerResponseHandler(DateCodecFactory dateCodec, ServerListResponseHandler serverListResponseHandler, FirewallListResponseHandler firewallListResponseHandler) {
      super(dateCodec, serverListResponseHandler, firewallListResponseHandler);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (done)
         return;

      if (useBalancedServerParser)
         balancedServerResponseHandler.endElement(uri, localName, qName);
      else if (useFirewallParser)
         firewallListResponseHandler.endElement(uri, localName, qName);
      else {
         setPropertyOnEndTag(qName);
         if ("return".equals(qName)) {
            done = true;
            builder.balancedServers(balancedServerResponseHandler.getResult());
            builder.firewalls(firewallListResponseHandler.getResult());
         }
         clearTextBuffer();
      }

      if ("balancedServers".equals(qName))
         useBalancedServerParser = false;
      else if ("firewall".equals(qName))
         useFirewallParser = false;

   }

   @Override
   public LoadBalancer getResult() {
      return builder.build();
   }
}
