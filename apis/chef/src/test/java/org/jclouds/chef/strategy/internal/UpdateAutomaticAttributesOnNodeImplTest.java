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
package org.jclouds.chef.strategy.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Map;

import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.Node;
import org.jclouds.domain.JsonBall;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code UpdateAutomaticAttributesOnNodeImpl}
 */
@Test(groups = { "unit" })
public class UpdateAutomaticAttributesOnNodeImplTest {

   @Test
   public void test() {
      ChefApi chef = createMock(ChefApi.class);

      Map<String, JsonBall> automatic = ImmutableMap.<String, JsonBall> of();
      Supplier<Map<String, JsonBall>> automaticSupplier = Suppliers.<Map<String, JsonBall>> ofInstance(automatic);

      Node node = Node.builder().name("name").environment("_default").build();
      Node nodeWithAutomatic = Node.builder().name("name").environment("_default").automaticAttributes(automatic)
            .build();

      expect(chef.getNode("name")).andReturn(node);
      expect(chef.updateNode(nodeWithAutomatic)).andReturn(null);

      replay(chef);

      UpdateAutomaticAttributesOnNodeImpl updater = new UpdateAutomaticAttributesOnNodeImpl(chef, automaticSupplier);

      updater.execute("name");
      verify(chef);

   }
}
