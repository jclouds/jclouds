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

import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.customer;
import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.isRecursive;
import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.key;
import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.projectId;
import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.resourceId;
import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.resourceType;
import static org.jclouds.cloudstack.options.ListTagsOptions.Builder.value;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.jclouds.cloudstack.domain.Tag;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ListTagsOptions}
 */
@Test(groups = "unit")
public class ListTagsOptionsTest {

   public void testCustomer() {
      ListTagsOptions options = new ListTagsOptions().customer("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("customer"));
   }

   public void testCustomerStatic() {
      ListTagsOptions options = customer("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("customer"));
   }

   public void testDomainId() {
      ListTagsOptions options = new ListTagsOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListTagsOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainId() {
      ListTagsOptions options = new ListTagsOptions().accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainIdStatic() {
      ListTagsOptions options = accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testIsRecursive() {
      ListTagsOptions options = new ListTagsOptions().isRecursive(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isrecursive"));
   }

   public void testIsRecursiveStatic() {
      ListTagsOptions options = isRecursive(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isrecursive"));
   }

   public void testKey() {
      ListTagsOptions options = new ListTagsOptions().key("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("key"));
   }

   public void testKeyStatic() {
      ListTagsOptions options = key("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("key"));
   }

   public void testKeyword() {
      ListTagsOptions options = new ListTagsOptions().keyword("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListTagsOptions options = keyword("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("keyword"));
   }

   public void testProjectId() {
      ListTagsOptions options = new ListTagsOptions().projectId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("projectid"));
   }

   public void testProjectIdStatic() {
      ListTagsOptions options = projectId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("projectid"));
   }

   public void testResourceId() {
      ListTagsOptions options = new ListTagsOptions().resourceId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("resourceid"));
   }

   public void testResourceIdStatic() {
      ListTagsOptions options = resourceId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("resourceid"));
   }

   public void testResourceType() {
      ListTagsOptions options = new ListTagsOptions().resourceType(Tag.ResourceType.TEMPLATE);
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testResourceTypeStatic() {
      ListTagsOptions options = resourceType(Tag.ResourceType.TEMPLATE);
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testResourceTypeAsString() {
      ListTagsOptions options = new ListTagsOptions().resourceType("Template");
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testResourceTypeAsStringStatic() {
      ListTagsOptions options = resourceType("Template");
      assertEquals(ImmutableList.of("Template"), options.buildQueryParameters().get("resourcetype"));
   }

   public void testValue() {
      ListTagsOptions options = new ListTagsOptions().value("some-value");
      assertEquals(ImmutableList.of("some-value"), options.buildQueryParameters().get("value"));
   }

   public void testValueStatic() {
      ListTagsOptions options = value("some-value");
      assertEquals(ImmutableList.of("some-value"), options.buildQueryParameters().get("value"));
   }
}
