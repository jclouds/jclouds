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
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.domain.Tag;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to control how tags are created.
 *
 * @see <a
 *      href="http://cloudstack.apache.org/docs/api/apidocs-4.3/root_admin/createTags.html"
 *      />
 */
public class CreateTagsOptions extends BaseHttpRequestOptions {

   public static final CreateTagsOptions NONE = new CreateTagsOptions();

   /**
    * Customer - optional
    */
   public CreateTagsOptions customer(String customer) {
      this.queryParameters.replaceValues("customer", ImmutableSet.of(customer));
      return this;
   }

   /**
    * Resource ID(s) to tag
    */
   public CreateTagsOptions resourceIds(Set<String> resourceIds) {
      this.queryParameters.replaceValues("resourceids", ImmutableSet.of(Joiner.on(",").join(resourceIds)));
      return this;
   }

   /**
    * Resource ID(s) to tag
    */
   public CreateTagsOptions resourceIds(String... resourceIds) {
      this.queryParameters.replaceValues("resourceids", ImmutableSet.of(Joiner.on(",").join(resourceIds)));
      return this;
   }

   /**
    * Resource type
    */
   public CreateTagsOptions resourceType(String resourceType) {
      this.queryParameters.replaceValues("resourcetype", ImmutableSet.of(resourceType));
      return this;
   }

   /**
    * Resource type
    */
   public CreateTagsOptions resourceType(Tag.ResourceType resourceType) {
      this.queryParameters.replaceValues("resourcetype", ImmutableSet.of(resourceType.toString()));
      return this;
   }

   /**
    * Tags to create
    */
   public CreateTagsOptions tags(Map<String, String> tags) {
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

      public static CreateTagsOptions customer(String customer) {
         CreateTagsOptions options = new CreateTagsOptions();
         return options.customer(customer);
      }

      public static CreateTagsOptions resourceIds(Set<String> resourceIds) {
         CreateTagsOptions options = new CreateTagsOptions();
         return options.resourceIds(resourceIds);
      }

      public static CreateTagsOptions resourceIds(String... resourceIds) {
         CreateTagsOptions options = new CreateTagsOptions();
         return options.resourceIds(resourceIds);
      }

      public static CreateTagsOptions resourceType(String resourceType) {
         CreateTagsOptions options = new CreateTagsOptions();
         return options.resourceType(resourceType);
      }

      public static CreateTagsOptions resourceType(Tag.ResourceType resourceType) {
         CreateTagsOptions options = new CreateTagsOptions();
         return options.resourceType(resourceType);
      }

      public static CreateTagsOptions tags(Map<String, String> tags) {
         CreateTagsOptions options = new CreateTagsOptions();
         return options.tags(tags);
      }
   }
}
