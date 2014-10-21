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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.cloudstack.domain.Tag;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class ListTagsResponseTest extends BaseSetParserTest<Tag> {

   @Override
   protected Injector injector() {
      return Guice.createInjector(new GsonModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }

      });
   }

   @Override
   public String resource() {
      return "/listtagsresponse.json";
   }

   @Override
   @SelectJson("tag")
   public Set<Tag> expected() {
      return ImmutableSet.<Tag>of(
            Tag.builder()
                  .account("admin")
                  .domain("ROOT")
                  .domainId("79dc06c4-4432-11e4-b70d-000c29e19aa0")
                  .key("test-tag")
                  .resourceId("54fe1d53-5d73-4184-8b62-948b9d8e08fb")
                  .resourceType(Tag.ResourceType.TEMPLATE)
                  .value("true").build()
      );
   }

}
