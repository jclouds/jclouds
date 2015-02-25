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
package org.jclouds.profitbricks.http.parser.firewall.rule;

import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.Firewall.Protocol;

import com.google.inject.Inject;

import org.jclouds.profitbricks.http.parser.BaseProfitBricksResponseHandler;

public abstract class BaseFirewallRuleResponseHandler<T> extends BaseProfitBricksResponseHandler<T> {

   protected Firewall.Rule.Builder builder;

   @Inject
   BaseFirewallRuleResponseHandler() {
      this.builder = Firewall.Rule.builder();
   }

   @Override
   protected void setPropertyOnEndTag(String qName) {
      if ("firewallRuleId".equals(qName))
         builder.id(textToStringValue());
      else if ("name".equals(qName))
         builder.name(textToStringValue());
      else if ("portRangeEnd".equals(qName))
         builder.portRangeEnd(textToIntValue());
      else if ("portRangeStart".equals(qName))
         builder.portRangeStart(textToIntValue());
      else if ("protocol".equals(qName))
         builder.protocol(Protocol.fromValue(textToStringValue()));
      else if ("sourceIp".equals(qName))
         builder.sourceIp(textToStringValue());
      else if ("sourceMac".equals(qName))
         builder.sourceMac(textToStringValue());
      else if ("targetIp".equals(qName))
         builder.targetIp(textToStringValue());
   }
}
