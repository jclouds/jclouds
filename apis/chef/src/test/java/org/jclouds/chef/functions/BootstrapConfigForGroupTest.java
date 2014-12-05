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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.BootstrapConfig;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "unit", testName = "BootstrapConfigForGroupTest")
public class BootstrapConfigForGroupTest {

   private Json json;

   @BeforeClass
   public void setup() {
      Injector injector = Guice.createInjector(new GsonModule());
      json = injector.getInstance(Json.class);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenNoDatabagItem() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Client client = createMock(Client.class);

      BootstrapConfigForGroup fn = new BootstrapConfigForGroup("jclouds", chefApi, json);

      expect(chefApi.getDatabagItem("jclouds", "foo")).andReturn(null);
      replay(client, chefApi);

      fn.apply("foo");

      verify(client, chefApi);
   }

   @Test
   public void testReturnsItem() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Client client = createMock(Client.class);

      BootstrapConfigForGroup fn = new BootstrapConfigForGroup("jclouds", chefApi, json);
      DatabagItem databag = new DatabagItem("foo",
            "{\"environment\":\"development\",\"ssl_ca_file\":\"/etc/certs/chef-server.crt\","
                  + "\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"],"
                  + "\"attributes\":{\"tomcat6\":{\"ssl_port\":8433}}}");

      expect(chefApi.getDatabagItem("jclouds", "foo")).andReturn(databag);
      replay(client, chefApi);

      BootstrapConfig config = fn.apply("foo");
      assertEquals(config.getEnvironment(), "development");
      assertEquals(config.getSslCAFile(), "/etc/certs/chef-server.crt");
      assertEquals(config.getRunList().get(0), "recipe[apache2]");
      assertEquals(config.getRunList().get(1), "role[webserver]");
      assertEquals(config.getAttributes().toString(), "{\"tomcat6\":{\"ssl_port\":8433}}");

      verify(client, chefApi);
   }

}
