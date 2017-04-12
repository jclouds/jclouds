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


import static org.testng.Assert.assertEquals;

import static org.jclouds.cloudstack.options.ListProjectsOptions.Builder.displayText;
import static org.jclouds.cloudstack.options.ListProjectsOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListProjectsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListProjectsOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListProjectsOptions.Builder.name;
import static org.jclouds.cloudstack.options.ListProjectsOptions.Builder.page;
import static org.jclouds.cloudstack.options.ListProjectsOptions.Builder.pageSize;
import static org.jclouds.cloudstack.options.ListProjectsOptions.Builder.state;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;



/**
 * Tests behavior of {@code ListProjectsOptions}
 */
@Test(groups = "unit")
public class ListProjectsOptionsTest {

   public void testId() {
      ListProjectsOptions options = new ListProjectsOptions().id("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListProjectsOptions options = id("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("id"));
   }

   public void testKeyword() {
      ListProjectsOptions options = new ListProjectsOptions().keyword("Enabled");
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListProjectsOptions options = keyword("Enabled");
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("keyword"));
   }

   public void testName() {
      ListProjectsOptions options = new ListProjectsOptions().name("Project Name");
      assertEquals(ImmutableList.of("Project Name"), options.buildQueryParameters().get("name"));
   }

   public void testNameStatic() {
      ListProjectsOptions options = name("Project Name");
      assertEquals(ImmutableList.of("Project Name"), options.buildQueryParameters().get("name"));
   }

   public void testPage() {
      ListProjectsOptions options = new ListProjectsOptions().page(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("page"));
   }

   public void testPageStatic() {
      ListProjectsOptions options = page(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("page"));
   }

   public void testPageSize() {
      ListProjectsOptions options = new ListProjectsOptions().pageSize(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("pagesize"));
   }

   public void testPageSizeStatic() {
      ListProjectsOptions options = pageSize(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("pagesize"));
   }

   public void testState() {
      ListProjectsOptions options = new ListProjectsOptions().state("Up");
      assertEquals(ImmutableList.of("Up"), options.buildQueryParameters().get("state"));
   }

   public void testStateStatic() {
      ListProjectsOptions options = state("Up");
      assertEquals(ImmutableList.of("Up"), options.buildQueryParameters().get("state"));
   }
   
   public void testDisplayText() {
      ListProjectsOptions options = new ListProjectsOptions().displayText("My Project");
      assertEquals(ImmutableList.of("My Project"), options.buildQueryParameters().get("displaytext"));
   }

   public void testDisplayTextStatic() {
      ListProjectsOptions options = displayText("My Project");
      assertEquals(ImmutableList.of("My Project"), options.buildQueryParameters().get("displaytext"));
   }
           
   public void testDomainId() {
      ListProjectsOptions options = new ListProjectsOptions().domainId("111-111-111");
      assertEquals(ImmutableList.of("111-111-111"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListProjectsOptions options = domainId("111-111-111");
      assertEquals(ImmutableList.of("111-111-111"), options.buildQueryParameters().get("domainid"));
   }

}
