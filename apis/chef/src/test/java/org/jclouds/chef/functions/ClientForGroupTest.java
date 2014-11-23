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
import java.security.PrivateKey;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.Client;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "ClientForGroupTest")
public class ClientForGroupTest {

   public void testWhenNoClientsInList() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Client client = createMock(Client.class);
      PrivateKey privateKey = createMock(PrivateKey.class);

      ClientForGroup fn = new ClientForGroup(chefApi);

      expect(chefApi.listClients()).andReturn(ImmutableSet.<String> of());
      expect(chefApi.createClient("foo-client-00")).andReturn(client);
      expect(client.getPrivateKey()).andReturn(privateKey);

      replay(client);
      replay(chefApi);

      Client compare = fn.apply("foo");
      assertEquals(compare.getClientname(), "foo-client-00");
      assertEquals(compare.getName(), "foo-client-00");
      assertEquals(compare.getPrivateKey(), privateKey);

      verify(client);
      verify(chefApi);
   }

   public void testWhenClientsInListAddsToEnd() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Client client = createMock(Client.class);
      PrivateKey privateKey = createMock(PrivateKey.class);

      ClientForGroup fn = new ClientForGroup(chefApi);

      expect(chefApi.listClients()).andReturn(
            ImmutableSet.<String> of("foo-client-00", "foo-client-01", "foo-client-02"));
      expect(chefApi.createClient("foo-client-03")).andReturn(client);
      expect(client.getPrivateKey()).andReturn(privateKey);

      replay(client);
      replay(chefApi);

      Client compare = fn.apply("foo");
      assertEquals(compare.getClientname(), "foo-client-03");
      assertEquals(compare.getName(), "foo-client-03");
      assertEquals(compare.getPrivateKey(), privateKey);

      verify(client);
      verify(chefApi);
   }

   public void testWhenClientsInListReplacesMissing() throws IOException {
      ChefApi chefApi = createMock(ChefApi.class);
      Client client = createMock(Client.class);
      PrivateKey privateKey = createMock(PrivateKey.class);

      ClientForGroup fn = new ClientForGroup(chefApi);

      expect(chefApi.listClients()).andReturn(ImmutableSet.<String> of("foo-client-00", "foo-client-02"));
      expect(chefApi.createClient("foo-client-01")).andReturn(client);
      expect(client.getPrivateKey()).andReturn(privateKey);

      replay(client);
      replay(chefApi);

      Client compare = fn.apply("foo");
      assertEquals(compare.getClientname(), "foo-client-01");
      assertEquals(compare.getName(), "foo-client-01");
      assertEquals(compare.getPrivateKey(), privateKey);

      verify(client);
      verify(chefApi);
   }
}
