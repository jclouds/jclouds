/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.ApiVersion;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseCookbookVersionsV10FromJson}
 * 
 * @author Ignasi Barrera
 */
@Test(groups = { "unit" }, singleThreaded = true)
public class ParseCookbookVersionsV10FromJsonTest {

   private ParseCookbookVersionsV10FromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
       Injector injector = Guice.createInjector(new AbstractModule() {
           @Override
           protected void configure()
           {
               bind(String.class).annotatedWith(ApiVersion.class).toInstance(ChefAsyncClient.VERSION);
           }
       }, new ChefParserModule(), new GsonModule());
   
      handler = injector.getInstance(ParseCookbookVersionsV10FromJson.class);
   }

   public void testRegex() {
       assertEquals(
           handler
                 .apply(new HttpResponse(
                       200,
                       "ok",
                       Payloads
                             .newStringPayload("{" +
                                 "\"apache2\" => {" +
                                     "\"url\" => \"http://localhost:4000/cookbooks/apache2\"," +
                                     "\"versions\" => [" +
                                         "{\"url\" => \"http://localhost:4000/cookbooks/apache2/5.1.0\"," +
                                         "\"version\" => \"5.1.0\"}," +
                                         "{\"url\" => \"http://localhost:4000/cookbooks/apache2/4.2.0\"," +
                                         "\"version\" => \"4.2.0\"}" +
                                     "]" +
                                 "}" +
                             "}"))),
            ImmutableSet.of("5.1.0", "4.2.0"));
   }
}
