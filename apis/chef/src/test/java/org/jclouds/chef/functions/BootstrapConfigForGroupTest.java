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
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.rest.annotations.Api;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BootstrapConfigForGroupTest")
public class BootstrapConfigForGroupTest {

   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenNoDatabagItem() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Client client = createMock(Client.class);

      BootstrapConfigForGroup fn = new BootstrapConfigForGroup("jclouds", chefApi);

      expect(chefApi.getDatabagItem("jclouds", "foo")).andReturn(null);

      replay(client);
      replay(chefApi);

      fn.apply("foo");

      verify(client);
      verify(chefApi);
   }

   @Test
   public void testReturnsItem() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Api api = createMock(Api.class);

      BootstrapConfigForGroup fn = new BootstrapConfigForGroup("jclouds", chefApi);
      DatabagItem config = new DatabagItem("foo",
            "{\"tomcat6\":{\"ssl_port\":8433},\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"]}");

      expect(chefApi.getDatabagItem("jclouds", "foo")).andReturn(config);

      replay(api);
      replay(chefApi);

      assertEquals(fn.apply("foo"), config);

      verify(api);
      verify(chefApi);
   }

}
