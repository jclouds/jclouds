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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.CookbookDefinition;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.ApiVersion;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = {"unit"}, singleThreaded = true)
public class ParseCookbookDefinitionListFromJsonv10Test {

   private ParseCookbookDefinitionListFromJsonv10 handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(String.class).annotatedWith(ApiVersion.class).toInstance(ChefApiMetadata.DEFAULT_API_VERSION);
         }
      }, new ChefParserModule(), new GsonModule());

      handler = injector.getInstance(ParseCookbookDefinitionListFromJsonv10.class);
   }

   public void testCookbokDefinitionListParsing() throws URISyntaxException {
      CookbookDefinition.Version v510 = CookbookDefinition.Version.builder()
            .url(new URI("http://localhost:4000/cookbooks/apache2/5.1.0")).version("5.1.0").build();
      CookbookDefinition.Version v420 = CookbookDefinition.Version.builder()
            .url(new URI("http://localhost:4000/cookbooks/apache2/4.2.0")).version("4.2.0").build();
      CookbookDefinition apache2 = CookbookDefinition.builder()
            .name("apache2").url(new URI("http://localhost:4000/cookbooks/apache2")).version(v510).version(v420).build();
      
      CookbookDefinition.Version v100 = CookbookDefinition.Version.builder()
            .url(new URI("http://localhost:4000/cookbooks/nginx/1.0.0")).version("1.0.0").build();
      CookbookDefinition.Version v030 = CookbookDefinition.Version.builder()
            .url(new URI("http://localhost:4000/cookbooks/nginx/0.3.0")).version("0.3.0").build();
      CookbookDefinition nginx = CookbookDefinition.builder()
            .name("nginx").url(new URI("http://localhost:4000/cookbooks/nginx")).version(v100).version(v030).build();
      
      assertEquals(handler.apply(HttpResponse
            .builder()
            .statusCode(200)
            .message("ok")
            .payload(
                  "{" + "\"apache2\" => {" + "\"url\" => \"http://localhost:4000/cookbooks/apache2\","
                        + "\"versions\" => [" + "{\"url\" => \"http://localhost:4000/cookbooks/apache2/5.1.0\","
                        + "\"version\" => \"5.1.0\"},"
                        + "{\"url\" => \"http://localhost:4000/cookbooks/apache2/4.2.0\","
                        + "\"version\" => \"4.2.0\"}" + "]" + "},"
                        + "\"nginx\" => {"
                        + "\"url\" => \"http://localhost:4000/cookbooks/nginx\","
                        + "\"versions\" => ["
                        + "{\"url\" => \"http://localhost:4000/cookbooks/nginx/1.0.0\","
                        + "\"version\" => \"1.0.0\"},"
                        + "{\"url\" => \"http://localhost:4000/cookbooks/nginx/0.3.0\","
                        + "\"version\" => \"0.3.0\"}"
                        + "]}" +
                        "}").build()),
            ImmutableSet.of(apache2, nginx));
   }
}
