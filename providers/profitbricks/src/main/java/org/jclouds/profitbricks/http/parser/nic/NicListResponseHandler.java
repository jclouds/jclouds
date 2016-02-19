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

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import org.jclouds.profitbricks.domain.Nic;
import org.xml.sax.SAXException;

import java.util.List;

import org.jclouds.profitbricks.http.parser.firewall.FirewallResponseHandler;

public class NicListResponseHandler extends BaseNicResponseHandler<List<Nic>> {

   private List<Nic> nics;

   @Inject
   public NicListResponseHandler(FirewallResponseHandler firewallResponseHandler) {
      super(firewallResponseHandler);
      this.nics = Lists.newArrayList();
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (useFirewallParser)
         firewallResponseHandler.endElement(uri, localName, qName);
      else {
         setPropertyOnEndTag(qName);
         if ("return".equals(qName) || "nics".equals(qName)) {
            nics.add(builder
                    .ips(ips)
                    .firewall(firewallResponseHandler.getResult())
                    .build());
            builder = Nic.builder();
            ips = new ArrayList<String>();
            firewallResponseHandler.reset();
         }
         clearTextBuffer();
      }

      if ("firewall".equals(qName))
         useFirewallParser = false;
   }

   @Override
   public void reset() {
      this.ips = new ArrayList<String>();
      this.nics = Lists.newArrayList();
   }

   @Override
   public List<Nic> getResult() {
      return nics;
   }
}
