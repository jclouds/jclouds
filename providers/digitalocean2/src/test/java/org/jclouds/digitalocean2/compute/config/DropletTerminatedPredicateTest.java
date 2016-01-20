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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Date;

import org.easymock.EasyMock;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.compute.config.DigitalOcean2ComputeServiceContextModule.DropletTerminatedPredicate;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.features.DropletApi;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "DropletTerminatedPredicateTest")
public class DropletTerminatedPredicateTest {

   public void testDropletTerminated() {
      DropletApi dropletApi = EasyMock.createMock(DropletApi.class);
      DigitalOcean2Api api = EasyMock.createMock(DigitalOcean2Api.class);

      expect(dropletApi.get(1)).andReturn(mockDroplet());
      expect(dropletApi.get(2)).andReturn(null);
      expect(api.dropletApi()).andReturn(dropletApi).times(2);
      replay(dropletApi, api);

      DropletTerminatedPredicate predicate = new DropletTerminatedPredicate(api);
      assertFalse(predicate.apply(1));
      assertTrue(predicate.apply(2));
   }

   private static Droplet mockDroplet() {
      return Droplet.create(1, "foo", 1024, 1, 20, false, new Date(), Droplet.Status.ACTIVE,
            ImmutableList.<Integer> of(), ImmutableList.<Integer> of(), ImmutableList.<String> of(), null, null, null,
            "", null, null);
   }
}
