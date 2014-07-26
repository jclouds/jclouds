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
package org.jclouds.openstack.trove.v1.domain;

import org.jclouds.http.Uris;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.testng.annotations.Test;
import com.google.common.collect.ImmutableList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

@Test(groups = "unit", testName = "FlavorTest")
public class FlavorTest {
   public void testFlavorForId() {
      Flavor flavor1 = forId(1);
      Flavor flavor2 = forId(2);
      assertEquals(flavor1.getId(), 1);
      assertEquals(flavor1.getName(), "small");
      assertFalse(flavor1.equals(flavor2));
   }

   /**
    * Creates a dummy Flavor when you need a Flavor with just the flavorId.
    *
    * 1. name  = small
    * 2. ram   = 512
    * 3. links = self, bookmark
    */
   public static Flavor forId(int flavorId) {
       return Flavor.builder()
               .id(flavorId)
               .name("small")
               .ram(512)
               .links(
                       ImmutableList.of(
                               Link.create(Relation.SELF, Uris.uriBuilder("http://test1").build() ),
                               Link.create(Relation.BOOKMARK, Uris.uriBuilder("http://test2").build() )
                               ) ).build();
   }
}
