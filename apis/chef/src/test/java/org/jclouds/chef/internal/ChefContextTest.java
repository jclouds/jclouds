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
package org.jclouds.chef.internal;

import static com.google.common.base.Throwables.propagate;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.filters.SignedHeaderAuthTest;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests the access to the underlying API from the context.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "ChefContextTest")
public class ChefContextTest {

   private ChefContext context;

   @BeforeClass
   public void setup() {
      context = ContextBuilder.newBuilder(new ChefApiMetadata())
            .credentials(SignedHeaderAuthTest.USER_ID, SignedHeaderAuthTest.PRIVATE_KEY)
            .modules(ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule())) //
            .buildView(ChefContext.class);
   }

   public void testCanAccessChefApi() {
      ChefApi api = context.getApi(ChefApi.class);
      assertNotNull(api);
   }

   @AfterClass
   public void tearDown() {
      try {
         context.close();
      } catch (IOException e) {
         throw propagate(e);
      }
   }
}
