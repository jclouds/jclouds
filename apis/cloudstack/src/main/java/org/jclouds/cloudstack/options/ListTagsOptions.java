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

import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.domain.Tag;

/**
 * Options used to control what tags are returned
 * 
 * @see <a href=
 *      "http://cloudstack.apache.org/docs/api/apidocs-4.3/root_admin/listTags.html"
 *      />
 */
public class ListTagsOptions extends AccountInDomainOptions {

   public static final ListTagsOptions NONE = new ListTagsOptions();

   /**
    * @param customer
    *    list by customer
    */
   public ListTagsOptions customer(String customer) {
      this.queryParameters.replaceValues("customer", ImmutableSet.of(customer));
      return this;
   }

   /**
    * @param isRecursive
    *           Should we recurse on this search?
    */
   public ListTagsOptions isRecursive(boolean isRecursive) {
      this.queryParameters.replaceValues("isrecursive", ImmutableSet.of(isRecursive + ""));
      return this;
   }

   /**
    * @param key
    *    list by key
    */
   public ListTagsOptions key(String key) {
      this.queryParameters.replaceValues("key", ImmutableSet.of(key));
      return this;
   }

   /**
    * @param keyword
    *    list by keyword
    */
   public ListTagsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param projectId
    *    list by project
    */
   public ListTagsOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId));
      return this;
   }

   /**
    * @param resourceId
    *    list by resource ID
    */
   public ListTagsOptions resourceId(String resourceId) {
      this.queryParameters.replaceValues("resourceid", ImmutableSet.of(resourceId));
      return this;
   }

   /**
    * @param resourceType
    *    list by resource type
    */
   public ListTagsOptions resourceType(String resourceType) {
      this.queryParameters.replaceValues("resourcetype", ImmutableSet.of(resourceType));
      return this;
   }

   /**
    * @param resourceType
    *    list by resource type
    */
   public ListTagsOptions resourceType(Tag.ResourceType resourceType) {
      this.queryParameters.replaceValues("resourcetype", ImmutableSet.of(resourceType.toString()));
      return this;
   }

   /**
    * @param value
    *    list by value
    */
   public ListTagsOptions value(String value) {
      this.queryParameters.replaceValues("value", ImmutableSet.of(value));
      return this;
   }

   public ListTagsOptions page(long page) {
      this.queryParameters.replaceValues("page", ImmutableSet.of(page + ""));
      return this;
   }

   public ListTagsOptions pageSize(long pageSize) {
      this.queryParameters.replaceValues("pagesize", ImmutableSet.of(pageSize + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#accountInDomain(String, String)
       */
      public static ListTagsOptions accountInDomain(String account, String domain) {
         ListTagsOptions options = new ListTagsOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#accountInDomain(String, String)
       */
      public static ListTagsOptions domainId(String domainId) {
         ListTagsOptions options = new ListTagsOptions();
         return options.domainId(domainId);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#customer(String)
       */
      public static ListTagsOptions customer(String customer) {
         ListTagsOptions options = new ListTagsOptions();
         return options.customer(customer);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#isRecursive(boolean)
       */
      public static ListTagsOptions isRecursive(boolean isRecursive) {
         ListTagsOptions options = new ListTagsOptions();
         return options.isRecursive(isRecursive);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#key(String)
       */
      public static ListTagsOptions key(String key) {
         ListTagsOptions options = new ListTagsOptions();
         return options.key(key);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#keyword
       */
      public static ListTagsOptions keyword(String keyword) {
         ListTagsOptions options = new ListTagsOptions();
         return options.keyword(keyword);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#projectId(String)
       */
      public static ListTagsOptions projectId(String projectId) {
         ListTagsOptions options = new ListTagsOptions();
         return options.projectId(projectId);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#resourceId(String)
       */
      public static ListTagsOptions resourceId(String resourceId) {
         ListTagsOptions options = new ListTagsOptions();
         return options.resourceId(resourceId);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#resourceType(String)
       */
      public static ListTagsOptions resourceType(String resourceType) {
         ListTagsOptions options = new ListTagsOptions();
         return options.resourceType(resourceType);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#resourceType(org.jclouds.cloudstack.domain.Tag.ResourceType)
       */
      public static ListTagsOptions resourceType(Tag.ResourceType resourceType) {
         ListTagsOptions options = new ListTagsOptions();
         return options.resourceType(resourceType);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#value(String)
       */
      public static ListTagsOptions value(String value) {
         ListTagsOptions options = new ListTagsOptions();
         return options.value(value);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#page
       */
      public static ListTagsOptions page(long page) {
         ListTagsOptions options = new ListTagsOptions();
         return options.page(page);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListTagsOptions#pageSize
       */
      public static ListTagsOptions pageSize(long pageSize) {
         ListTagsOptions options = new ListTagsOptions();
         return options.pageSize(pageSize);
      }
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public ListTagsOptions accountInDomain(String account, String domain) {
      return ListTagsOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListTagsOptions domainId(String domainId) {
      return ListTagsOptions.class.cast(super.domainId(domainId));
   }
}
