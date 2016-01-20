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
package org.jclouds.digitalocean2.features;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.digitalocean2.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiMockTest;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

@Test(groups = "unit", testName = "ActionApiMockTest", singleThreaded = true)
public class ActionApiMockTest extends BaseDigitalOcean2ApiMockTest {

   public void testListActions() throws InterruptedException {
      server.enqueue(jsonResponse("/actions-first.json"));
      server.enqueue(jsonResponse("/actions-last.json"));

      Iterable<Action> actions = api.actionApi().list().concat();

      assertEquals(size(actions), 8); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/actions");
      assertSent(server, "GET", "/actions?page=2&per_page=5");
   }

   public void testListActionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Action> actions = api.actionApi().list().concat();

      assertTrue(isEmpty(actions));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/actions");
   }

   public void testListActionsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/actions-first.json"));

      Iterable<Action> actions = api.actionApi().list(page(1).perPage(5));

      assertEquals(size(actions), 5);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/actions?page=1&per_page=5");
   }

   public void testListActionsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Action> actions = api.actionApi().list(page(1).perPage(5));

      assertTrue(isEmpty(actions));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/actions?page=1&per_page=5");
   }
   
   public void testGetAction() throws InterruptedException {
      server.enqueue(jsonResponse("/action.json"));

      Action action = api.actionApi().get(1);

      assertEquals(action, actionFromResource("/action.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/actions/1");
   }

   public void testGetActionReturns404() throws InterruptedException {
      server.enqueue(response404());

      Action action = api.actionApi().get(1);

      assertNull(action);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/actions/1");
   }
   
   private Action actionFromResource(String resource) {
      return onlyObjectFromResource(resource, new TypeToken<Map<String, Action>>() {
         private static final long serialVersionUID = 1L;
      }); 
   }
}
