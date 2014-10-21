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
package org.jclouds.cloudstack.options;

import java.util.Map;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what egress firewall rules are returned
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/3.0.6/api_3.0.6/root_admin/listEgressFirewallRules.html"
 *      />
 */
public class ListEgressFirewallRulesOptions extends AccountInDomainOptions {

   public static final ListEgressFirewallRulesOptions NONE = new ListEgressFirewallRulesOptions();

   /**
    * @param id
    *    firewall rule ID
    */
   public ListEgressFirewallRulesOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param networkId
    *    the id of network of the firewall services
    */
   public ListEgressFirewallRulesOptions networkId(String networkId) {
      this.queryParameters.replaceValues("networkid", ImmutableSet.of(networkId + ""));
      return this;
   }

   /**
    * @param ipAddressId
    *    the id of IP address of the firewall services
    */
   public ListEgressFirewallRulesOptions ipAddressId(String ipAddressId) {
      this.queryParameters.replaceValues("ipaddressid", ImmutableSet.of(ipAddressId + ""));
      return this;
   }

   /**
    * @param projectId
    *    List firewall rules in this project.
    */
   public ListEgressFirewallRulesOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;
   }

   /**
    * @param keyword
    *    list by keyword
    */
   public ListEgressFirewallRulesOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   public ListEgressFirewallRulesOptions page(long page) {
      this.queryParameters.replaceValues("page", ImmutableSet.of(page + ""));
      return this;
   }

   public ListEgressFirewallRulesOptions pageSize(long pageSize) {
      this.queryParameters.replaceValues("pagesize", ImmutableSet.of(pageSize + ""));
      return this;
   }

   /**
    * @param tags
    *           Key/value pairs for tags that need to be matched.
    */
   public ListEgressFirewallRulesOptions tags(Map<String, String> tags) {
      int count = 0;
      for (Map.Entry<String, String> entry : tags.entrySet()) {
         this.queryParameters.replaceValues(String.format("tags[%d].key", count), ImmutableSet.of(entry.getKey()));
         this.queryParameters.replaceValues(String.format("tags[%d].value", count),
               ImmutableSet.of(entry.getValue()));
         count += 1;
      }
      return this;
   }


   public static class Builder {

      /**
       * @see ListEgressFirewallRulesOptions#id
       */
      public static ListEgressFirewallRulesOptions id(String id) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.id(id);
      }

      /**
       * @see ListEgressFirewallRulesOptions#networkId
       */
      public static ListEgressFirewallRulesOptions networkId(String networkId) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.networkId(networkId);
      }

      /**
       * @see ListEgressFirewallRulesOptions#ipAddressId
       */
      public static ListEgressFirewallRulesOptions ipAddressId(String ipAddressId) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.ipAddressId(ipAddressId);
      }

      /**
       * @see ListEgressFirewallRulesOptions#projectId(String)
       */
      public static ListEgressFirewallRulesOptions projectId(String projectId) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.projectId(projectId);
      }

      /**
       * @see ListEgressFirewallRulesOptions#keyword
       */
      public static ListEgressFirewallRulesOptions keyword(String keyword) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.keyword(keyword);
      }

      /**
       * @see ListEgressFirewallRulesOptions#page
       */
      public static ListEgressFirewallRulesOptions page(long page) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.page(page);
      }

      /**
       * @see ListEgressFirewallRulesOptions#pageSize
       */
      public static ListEgressFirewallRulesOptions pageSize(long pageSize) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.pageSize(pageSize);
      }

      /**
       * @see ListEgressFirewallRulesOptions#accountInDomain
       */
      public static ListEgressFirewallRulesOptions accountInDomain(String account, String domain) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListEgressFirewallRulesOptions#domainId
       */
      public static ListEgressFirewallRulesOptions domainId(String id) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.domainId(id);
      }

      /**
       * @see ListEgressFirewallRulesOptions#tags
       */
      public static ListEgressFirewallRulesOptions tags(Map<String, String> tags) {
         ListEgressFirewallRulesOptions options = new ListEgressFirewallRulesOptions();
         return options.tags(tags);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListEgressFirewallRulesOptions accountInDomain(String account, String domain) {
      return ListEgressFirewallRulesOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListEgressFirewallRulesOptions domainId(String domainId) {
      return ListEgressFirewallRulesOptions.class.cast(super.domainId(domainId));
   }
}
