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

import static java.lang.String.format;

import java.util.List;

import org.jclouds.profitbricks.binder.BaseProfitBricksRequestBinder;

import com.google.common.base.Strings;

public abstract class FirewallBinder extends BaseProfitBricksRequestBinder<List<String>> {

   protected final StringBuilder requestBuilder;

   FirewallBinder() {
      super("ids");
      this.requestBuilder = new StringBuilder(128);
   }

   protected void bindListWithTag(List<String> ids, String tag) {
      if (ids == null || ids.isEmpty() || Strings.isNullOrEmpty(tag))
         return;
      for (String id : ids)
         requestBuilder.append(format("<%s>%s</%s>", tag, id, tag));
   }

   public static class ActivateFirewallRequestBinder extends FirewallBinder {

      @Override
      protected String createPayload(List<String> payload) {
         requestBuilder.append("<ws:activateFirewalls>");
         bindListWithTag(payload, "firewallIds");
         requestBuilder.append("</ws:activateFirewalls>");

         return requestBuilder.toString();
      }

   }

   public static class DeactivateFirewallRequestBinder extends FirewallBinder {

      @Override
      protected String createPayload(List<String> payload) {
         requestBuilder.append("<ws:deactivateFirewalls>");
         bindListWithTag(payload, "firewallIds");
         requestBuilder.append("</ws:deactivateFirewalls>");

         return requestBuilder.toString();
      }

   }

   public static class DeleteFirewallRequestBinder extends FirewallBinder {

      @Override
      protected String createPayload(List<String> payload) {
         requestBuilder.append("<ws:deleteFirewalls>");
         bindListWithTag(payload, "firewallIds");
         requestBuilder.append("</ws:deleteFirewalls>");

         return requestBuilder.toString();
      }

   }

   public static class RemoveFirewallRuleRequestBinder extends FirewallBinder {

      @Override
      protected String createPayload(List<String> payload) {
         requestBuilder.append("<ws:removeFirewallRules>");
         bindListWithTag(payload, "firewallRuleIds");
         requestBuilder.append("</ws:removeFirewallRules>");

         return requestBuilder.toString();
      }

   }

}
