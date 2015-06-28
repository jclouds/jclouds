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
package org.jclouds.digitalocean2.compute.config;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Date;

import org.easymock.EasyMock;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.compute.config.DigitalOcean2ComputeServiceContextModule.ActionDonePredicate;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.features.ActionApi;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ActionDonePredicateTest")
public class ActionDonePredicateTest {

   public void testActionStatusOk() {
      ActionApi actionApi = EasyMock.createMock(ActionApi.class);
      DigitalOcean2Api api = EasyMock.createMock(DigitalOcean2Api.class);

      expect(actionApi.get(1)).andReturn(action(Action.Status.COMPLETED));
      expect(actionApi.get(2)).andReturn(action(Action.Status.IN_PROGRESS));
      expect(api.actionApi()).andReturn(actionApi).times(2);
      replay(actionApi, api);

      ActionDonePredicate predicate = new ActionDonePredicate(api);
      assertTrue(predicate.apply(1));
      assertFalse(predicate.apply(2));
   }

   public void testActionStatusError() {
      ActionApi actionApi = EasyMock.createMock(ActionApi.class);
      DigitalOcean2Api api = EasyMock.createMock(DigitalOcean2Api.class);

      expect(actionApi.get(anyInt())).andReturn(action(Action.Status.ERRORED));
      expect(api.actionApi()).andReturn(actionApi);
      replay(actionApi, api);

      ActionDonePredicate predicate = new ActionDonePredicate(api);

      try {
         predicate.apply(1);
         fail("Method should have thrown an IllegalStateException");
      } catch (IllegalStateException ex) {
         assertEquals(ex.getMessage(), "Resource is in invalid status: ERRORED");
      }
   }

   private static Action action(Action.Status status) {
      return Action.create(1, status, "foo", new Date(), new Date(), 1, "", null, "");
   }
}
