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
package org.jclouds.chef.internal;

import java.util.Map;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.config.ChefBootstrapModule;
import org.jclouds.chef.config.ChefHttpApiModule;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.domain.JsonBall;
import org.jclouds.ohai.AutomaticSupplier;
import org.jclouds.ohai.config.ConfiguresOhai;
import org.jclouds.ohai.config.OhaiModule;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = "live")
@Deprecated
public class BaseStubbedOhaiLiveTest extends BaseChefLiveTest<ChefApi> {

   @ConfiguresOhai
   static class TestOhaiModule extends OhaiModule {

      @Override
      protected Supplier<Map<String, JsonBall>> provideAutomatic(AutomaticSupplier in) {
         return Suppliers.<Map<String, JsonBall>> ofInstance(ImmutableMap.of("foo", new JsonBall("bar")));
      }
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new ChefApiMetadata()
            .toBuilder()
            .defaultModules(
                  ImmutableSet.<Class<? extends Module>> of(ChefHttpApiModule.class, ChefParserModule.class,
                        ChefBootstrapModule.class, TestOhaiModule.class)).build();
   }
}
