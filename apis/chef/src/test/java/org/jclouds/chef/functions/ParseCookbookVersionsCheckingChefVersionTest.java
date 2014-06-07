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
package org.jclouds.chef.functions;

import static org.testng.Assert.assertTrue;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.ApiVersion;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseCookbookVersionsCheckingChefVersion}.
 */
@Test(groups = { "unit" }, singleThreaded = true)
public class ParseCookbookVersionsCheckingChefVersionTest {

   public void testParserFor09() {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(String.class).annotatedWith(ApiVersion.class).toInstance("0.9.8");
         }
      }, new ChefParserModule(), new GsonModule());

      ParseCookbookVersionsCheckingChefVersion parser = injector
            .getInstance(ParseCookbookVersionsCheckingChefVersion.class);
      assertTrue(parser.parser instanceof ParseCookbookVersionsV09FromJson);
   }

   public void testParserFor010() {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(String.class).annotatedWith(ApiVersion.class).toInstance("0.10.8");
         }
      }, new ChefParserModule(), new GsonModule());

      ParseCookbookVersionsCheckingChefVersion parser = injector
            .getInstance(ParseCookbookVersionsCheckingChefVersion.class);
      assertTrue(parser.parser instanceof ParseCookbookVersionsV10FromJson);
   }

}
