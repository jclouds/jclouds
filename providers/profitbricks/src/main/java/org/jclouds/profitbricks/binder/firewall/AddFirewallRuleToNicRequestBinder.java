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
package org.jclouds.profitbricks.binder.firewall;

import org.jclouds.profitbricks.binder.BaseProfitBricksRequestBinder;
import org.jclouds.profitbricks.domain.Firewall;
import static java.lang.String.format;

public class AddFirewallRuleToNicRequestBinder extends BaseProfitBricksRequestBinder<Firewall.Request.AddRulePayload> {

   private final StringBuilder requestBuilder;

   AddFirewallRuleToNicRequestBinder() {
      super("firewall");
      this.requestBuilder = new StringBuilder(128);
   }

   @Override
   protected String createPayload(Firewall.Request.AddRulePayload payload) {
      requestBuilder.append("<ws:addFirewallRulesToNic>")
              .append(format("<nicId>%s</nicId>", payload.nicId()));
      for (Firewall.Rule rule : payload.rules())
         requestBuilder
                 .append("<request>")
                 .append(formatIfNotEmpty("<icmpCode>%s</icmpCode>", rule.icmpCode()))
                 .append(formatIfNotEmpty("<icmpType>%s</icmpType>", rule.icmpType()))
                 .append(formatIfNotEmpty("<name>%s</name>", rule.name()))
                 .append(formatIfNotEmpty("<portRangeEnd>%s</portRangeEnd>", rule.portRangeEnd()))
                 .append(formatIfNotEmpty("<portRangeStart>%s</portRangeStart>", rule.portRangeStart()))
                 .append(formatIfNotEmpty("<protocol>%s</protocol>", rule.protocol()))
                 .append(formatIfNotEmpty("<sourceIp>%s</sourceIp>", rule.sourceIp()))
                 .append(formatIfNotEmpty("<sourceMac>%s</sourceMac>", rule.sourceMac()))
                 .append(formatIfNotEmpty("<targetIp>%s</targetIp>", rule.targetIp()))
                 .append("</request>");
      requestBuilder.append("</ws:addFirewallRulesToNic>");
      return requestBuilder.toString();
   }

}
