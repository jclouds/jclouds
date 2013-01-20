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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.Api;
import org.jclouds.rest.annotations.ApiVersion;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RunListForGroupTest")
public class RunListForGroupTest {
   Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
         bind(String.class).annotatedWith(ApiVersion.class).toInstance(ChefAsyncApi.VERSION);
      }
   }, new ChefParserModule(), new GsonModule());

   Json json = injector.getInstance(Json.class);

   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenNoDatabagItem() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Client client = createMock(Client.class);

      RunListForGroup fn = new RunListForGroup("jclouds", chefApi, json);

      expect(chefApi.getDatabagItem("jclouds", "foo")).andReturn(null);

      replay(client);
      replay(chefApi);

      fn.apply("foo");

      verify(client);
      verify(chefApi);
   }

   @Test
   public void testOneRecipe() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Api api = createMock(Api.class);

      RunListForGroup fn = new RunListForGroup("jclouds", chefApi, json);

      expect(chefApi.getDatabagItem("jclouds", "foo")).andReturn(
            new DatabagItem("foo", "{\"run_list\":[\"recipe[apache2]\"]}"));

      replay(api);
      replay(chefApi);

      assertEquals(fn.apply("foo"), ImmutableList.of("recipe[apache2]"));

      verify(api);
      verify(chefApi);
   }

   @Test
   public void testTwoRecipes() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Api api = createMock(Api.class);

      RunListForGroup fn = new RunListForGroup("jclouds", chefApi, json);

      expect(chefApi.getDatabagItem("jclouds", "foo")).andReturn(
            new DatabagItem("foo", "{\"run_list\":[\"recipe[apache2]\",\"recipe[mysql]\"]}"));

      replay(api);
      replay(chefApi);

      assertEquals(fn.apply("foo"), ImmutableList.of("recipe[apache2]", "recipe[mysql]"));

      verify(api);
      verify(chefApi);
   }

}
