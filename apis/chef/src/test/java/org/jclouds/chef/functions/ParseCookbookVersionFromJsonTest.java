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

import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.Metadata;
import org.jclouds.chef.domain.Resource;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.ApiVersion;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseCookbookVersionFromJson}
 */
@Test(groups = { "unit" }, singleThreaded = true)
public class ParseCookbookVersionFromJsonTest {

   private ParseJson<CookbookVersion> handler;
   private Injector injector;
   private Json json;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(String.class).annotatedWith(ApiVersion.class).toInstance(ChefApiMetadata.DEFAULT_API_VERSION);
         }
      }, new ChefParserModule(), new GsonModule());

      json = injector.getInstance(Json.class);
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<CookbookVersion>>() {
      }));
   }

   public void testBrew() throws IOException {
      CookbookVersion cookbook = handler.apply(HttpResponse.builder().statusCode(200).message("ok")
            .payload(ParseCookbookVersionFromJsonTest.class.getResourceAsStream("/brew-cookbook.json")).build());

      assertEquals(cookbook,
            handler.apply(HttpResponse.builder().statusCode(200).message("ok").payload(json.toJson(cookbook)).build()));
   }

   public void testTomcat() {
      CookbookVersion cookbook = handler.apply(HttpResponse.builder().statusCode(200).message("ok")
            .payload(ParseCookbookVersionFromJsonTest.class.getResourceAsStream("/tomcat-cookbook.json")).build());

      assertEquals(cookbook,
            handler.apply(HttpResponse.builder().statusCode(200).message("ok").payload(json.toJson(cookbook)).build()));
   }

   public void testMysql() throws IOException {
      CookbookVersion cookbook = handler.apply(HttpResponse.builder().statusCode(200).message("ok")
            .payload(ParseCookbookVersionFromJsonTest.class.getResourceAsStream("/mysql-cookbook.json")).build());

      assertEquals(cookbook,
            handler.apply(HttpResponse.builder().statusCode(200).message("ok").payload(json.toJson(cookbook)).build()));
   }

   public void testApache() {
      CookbookVersion fromJson = handler.apply(HttpResponse.builder().statusCode(200).message("ok")
            .payload(ParseCookbookVersionFromJsonTest.class.getResourceAsStream("/apache-chef-demo-cookbook.json"))
            .build());

      CookbookVersion expected = CookbookVersion
            .builder("apache-chef-demo", "0.0.0")
            .metadata(Metadata.builder() //
                  .license("Apache v2.0") //
                  .maintainer("Your Name") //
                  .maintainerEmail("youremail@example.com") //
                  .description("A fabulous new cookbook") //
                  .version("0.0.0").name("apache-chef-demo") //
                  .longDescription("") //
                  .build())
            .rootFile(
                  Resource
                        .builder()
                        .name("README")
                        .path("README")
                        .checksum(base16().lowerCase().decode("11637f98942eafbf49c71b7f2f048b78"))
                        .url(URI
                              .create("https://s3.amazonaws.com/opscode-platform-production-data/organization-486ca3ac66264fea926aa0b4ff74341c/checksum-11637f98942eafbf49c71b7f2f048b78?AWSAccessKeyId=AKIAJOZTD2N26S7W6APA&Expires=1277766181&Signature=zgpNl6wSxjTNovqZu2nJq0JztU8%3D")) //
                        .build())
            .rootFile(
                  Resource
                        .builder()
                        .name("Rakefile")
                        .path("Rakefile")
                        .checksum(base16().lowerCase().decode("ebcf925a1651b4e04b9cd8aac2bc54eb"))
                        .url(URI
                              .create("https://s3.amazonaws.com/opscode-platform-production-data/organization-486ca3ac66264fea926aa0b4ff74341c/checksum-ebcf925a1651b4e04b9cd8aac2bc54eb?AWSAccessKeyId=AKIAJOZTD2N26S7W6APA&Expires=1277766181&Signature=EFzzDSKKytTl7b%2FxrCeNLh05zj4%3D"))
                        .build()) //
            .build();

      assertEquals(fromJson, expected);
   }
}
