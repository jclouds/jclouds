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
 * Options used to control what account information is returned
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/3.0.6/api_3.0.6/root_admin/listProjects.html"
 *      />
 */
public class ListProjectsOptions extends AccountInDomainOptions {

   public static final ListProjectsOptions NONE = new ListProjectsOptions();

   /**
    * @param id
    *           list projects by project ID
    */
   public ListProjectsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id));
      return this;
   }

   /**
    * @param name
    *           list project by project name
    */
   public ListProjectsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param state
    *           list projects by state. Valid states are enabled, disabled, and
    *           locked.
    */
   public ListProjectsOptions state(String state) {
      this.queryParameters.replaceValues("state", ImmutableSet.of(state));
      return this;
   }

   /**
    * @param displayText
    *           list projects by displayText.
    */
   public ListProjectsOptions displayText(String displayText) {
      this.queryParameters.replaceValues("displaytext", ImmutableSet.of(displayText));
      return this;
   }

   /**
    * @param keyword
    *           list projects by keyword.
    */
   public ListProjectsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param recursive
    *           defaults to false, but if true, lists all projects from the
    *           parent specified by the domain id till leaves.
    */
   public ListProjectsOptions recursive(boolean recursive) {
      this.queryParameters.replaceValues("isrecursive", ImmutableSet.of(recursive + ""));
      return this;
   }

   /**
    * @param tags
    *           Key/value pairs for tags that need to be matched.
    */
   public ListProjectsOptions tags(Map<String, String> tags) {
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
       * @see org.jclouds.cloudstack.options.ListProjectsOptions#accountInDomain(String, String)
       */
      public static ListProjectsOptions accountInDomain(String project, String domain) {
         ListProjectsOptions options = new ListProjectsOptions();
         return options.accountInDomain(project, domain);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListProjectsOptions#domainId
       */
      public static ListProjectsOptions domainId(String domainId) {
         ListProjectsOptions options = new ListProjectsOptions();
         return options.domainId(domainId);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListProjectsOptions#id
       */
      public static ListProjectsOptions id(String id) {
         ListProjectsOptions options = new ListProjectsOptions();
         return options.id(id);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListProjectsOptions#name
       */
      public static ListProjectsOptions name(String name) {
         ListProjectsOptions options = new ListProjectsOptions();
         return options.name(name);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListProjectsOptions#state
       */
      public static ListProjectsOptions state(String state) {
         ListProjectsOptions options = new ListProjectsOptions();
         return options.state(state);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListProjectsOptions#displayText(String)
       */
      public static ListProjectsOptions displayText(String displayText) {
         ListProjectsOptions options = new ListProjectsOptions();
         return options.displayText(displayText);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListProjectsOptions#keyword(String)
       */
      public static ListProjectsOptions keyword(String keyword) {
         ListProjectsOptions options = new ListProjectsOptions();
         return options.keyword(keyword);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListProjectsOptions#recursive
       */
      public static ListProjectsOptions recursive(boolean recursive) {
         ListProjectsOptions options = new ListProjectsOptions();
         return options.recursive(recursive);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListProjectsOptions#tags
       */
      public static ListProjectsOptions tags(Map<String, String> tags) {
         ListProjectsOptions options = new ListProjectsOptions();
         return options.tags(tags);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListProjectsOptions accountInDomain(String account, String domain) {
      return ListProjectsOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListProjectsOptions domainId(String domainId) {
      return ListProjectsOptions.class.cast(super.domainId(domainId));
   }
}
