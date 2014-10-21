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

import static org.jclouds.cloudstack.options.DeleteTagsOptions.Builder.resourceIds;
import static org.jclouds.cloudstack.options.DeleteTagsOptions.Builder.resourceType;
import static org.jclouds.cloudstack.options.DeleteTagsOptions.Builder.tags;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.domain.Tag;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code DeleteTagsOptions}
 */
@Test(groups = "unit")
public class DeleteTagsOptionsTest {

   public void testResourceIds() {
      DeleteTagsOptions options = new DeleteTagsOptions().resourceIds("1", "2", "3");
      assertEquals(ImmutableList.of("1,2,3"), options.buildQueryParameters().get("resourceids"));
   }

   public void testResourceIdsStatic() {
      DeleteTagsOptions options = resourceIds("1", "2", "3");
      assertEquals(ImmutableList.of("1,2,3"), options.buildQueryParameters().get("resourceids"));
   }

   public void testResourceIdsAsSet() {
      DeleteTagsOptions options = new DeleteTagsOptions().resourceIds(ImmutableSet.of("1", "2", "3"));
      assertEquals(ImmutableList.of("1,2,3"), options.buildQueryParameters().get("resourceids"));
   }

   public void testResourceIdsAsSetStatic() {
      DeleteTagsOptions options = resourceIds(ImmutableSet.of("1", "2", "3"));
      assertEquals(ImmutableList.of("1,2,3"), options.buildQueryParameters().get("resourceids"));
   }

   public void testResourceType() {
      DeleteTagsOptions options = new DeleteTagsOptions().resourceType(Tag.ResourceType.TEMPLATE);
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testResourceTypeStatic() {
      DeleteTagsOptions options = resourceType(Tag.ResourceType.TEMPLATE);
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testResourceTypeAsString() {
      DeleteTagsOptions options = new DeleteTagsOptions().resourceType("Template");
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testResourceTypeAsStringStatic() {
      DeleteTagsOptions options = resourceType("Template");
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testTags() {
      DeleteTagsOptions options = new DeleteTagsOptions().tags(ImmutableMap.of("tag1", "val1", "tag2", "val2"));
      assertEquals(ImmutableList.of("tag1"), options.buildQueryParameters().get("tags[0].key"));
      assertEquals(ImmutableList.of("tag2"), options.buildQueryParameters().get("tags[1].key"));
      assertEquals(ImmutableList.of("val1"), options.buildQueryParameters().get("tags[0].value"));
      assertEquals(ImmutableList.of("val2"), options.buildQueryParameters().get("tags[1].value"));
   }

   public void testTagsStatic() {
      DeleteTagsOptions options = tags(ImmutableMap.of("tag1", "val1", "tag2", "val2"));
      assertEquals(ImmutableList.of("tag1"), options.buildQueryParameters().get("tags[0].key"));
      assertEquals(ImmutableList.of("tag2"), options.buildQueryParameters().get("tags[1].key"));
      assertEquals(ImmutableList.of("val1"), options.buildQueryParameters().get("tags[0].value"));
      assertEquals(ImmutableList.of("val2"), options.buildQueryParameters().get("tags[1].value"));
   }
}
