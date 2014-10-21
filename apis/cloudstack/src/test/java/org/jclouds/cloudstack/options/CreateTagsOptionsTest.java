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

import static org.jclouds.cloudstack.options.CreateTagsOptions.Builder.customer;
import static org.jclouds.cloudstack.options.CreateTagsOptions.Builder.resourceIds;
import static org.jclouds.cloudstack.options.CreateTagsOptions.Builder.resourceType;
import static org.jclouds.cloudstack.options.CreateTagsOptions.Builder.tags;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.domain.Tag;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code CreateTagsOptions}
 */
@Test(groups = "unit")
public class CreateTagsOptionsTest {

   public void testCustomer() {
      CreateTagsOptions options = new CreateTagsOptions().customer("some-customer");
      assertEquals(ImmutableList.of("some-customer"), options.buildQueryParameters().get("customer"));
   }

   public void testCustomerStatic() {
      CreateTagsOptions options = customer("some-customer");
      assertEquals(ImmutableList.of("some-customer"), options.buildQueryParameters().get("customer"));
   }

   public void testResourceIds() {
      CreateTagsOptions options = new CreateTagsOptions().resourceIds("1", "2", "3");
      assertEquals(ImmutableList.of("1,2,3"), options.buildQueryParameters().get("resourceids"));
   }

   public void testResourceIdsStatic() {
      CreateTagsOptions options = resourceIds("1", "2", "3");
      assertEquals(ImmutableList.of("1,2,3"), options.buildQueryParameters().get("resourceids"));
   }

   public void testResourceIdsAsSet() {
      CreateTagsOptions options = new CreateTagsOptions().resourceIds(ImmutableSet.of("1", "2", "3"));
      assertEquals(ImmutableList.of("1,2,3"), options.buildQueryParameters().get("resourceids"));
   }

   public void testResourceIdsAsSetStatic() {
      CreateTagsOptions options = resourceIds(ImmutableSet.of("1", "2", "3"));
      assertEquals(ImmutableList.of("1,2,3"), options.buildQueryParameters().get("resourceids"));
   }

   public void testResourceType() {
      CreateTagsOptions options = new CreateTagsOptions().resourceType(Tag.ResourceType.TEMPLATE);
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testResourceTypeStatic() {
      CreateTagsOptions options = resourceType(Tag.ResourceType.TEMPLATE);
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testResourceTypeAsString() {
      CreateTagsOptions options = new CreateTagsOptions().resourceType("Template");
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testResourceTypeAsStringStatic() {
      CreateTagsOptions options = resourceType("Template");
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testTags() {
      CreateTagsOptions options = new CreateTagsOptions().tags(ImmutableMap.of("tag1", "val1", "tag2", "val2"));
      assertEquals(ImmutableList.of("tag1"), options.buildQueryParameters().get("tags[0].key"));
      assertEquals(ImmutableList.of("tag2"), options.buildQueryParameters().get("tags[1].key"));
      assertEquals(ImmutableList.of("val1"), options.buildQueryParameters().get("tags[0].value"));
      assertEquals(ImmutableList.of("val2"), options.buildQueryParameters().get("tags[1].value"));
   }

   public void testTagsStatic() {
      CreateTagsOptions options = tags(ImmutableMap.of("tag1", "val1", "tag2", "val2"));
      assertEquals(ImmutableList.of("tag1"), options.buildQueryParameters().get("tags[0].key"));
      assertEquals(ImmutableList.of("tag2"), options.buildQueryParameters().get("tags[1].key"));
      assertEquals(ImmutableList.of("val1"), options.buildQueryParameters().get("tags[0].value"));
      assertEquals(ImmutableList.of("val2"), options.buildQueryParameters().get("tags[1].value"));
   }
}
