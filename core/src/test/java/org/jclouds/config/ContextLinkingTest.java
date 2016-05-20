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
package org.jclouds.config;

import static org.jclouds.config.ContextLinking.CONTEXT_SUPPLIER;
import static org.jclouds.config.ContextLinking.VIEW_SUPPLIER;
import static org.jclouds.config.ContextLinking.linkContext;
import static org.jclouds.config.ContextLinking.linkView;
import static org.jclouds.providers.AnonymousProviderMetadata.forApiOnEndpoint;
import static org.testng.Assert.assertNotNull;

import java.io.Closeable;

import org.jclouds.Context;
import org.jclouds.ContextBuilder;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.internal.BaseView;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

@Test(groups = "unit", testName = "ContextLinkingTest")
public class ContextLinkingTest {

   @Test
   public void testLinkedViewBindsViewAndContextSuppliers() {
      Injector injector = Guice.createInjector(linkView(new DummyView(contextFor(IntegrationTestClient.class))));

      assertNotNull(injector.getExistingBinding(Key.get(CONTEXT_SUPPLIER, Names.named("IntegrationTestClient"))));
      assertNotNull(injector.getExistingBinding(Key.get(VIEW_SUPPLIER, Names.named("IntegrationTestClient"))));
   }

   @Test
   public void testLinkedContextBindsContextSupplier() {
      Injector injector = Guice.createInjector(linkContext(contextFor(IntegrationTestClient.class)));

      assertNotNull(injector.getExistingBinding(Key.get(CONTEXT_SUPPLIER, Names.named("IntegrationTestClient"))));
   }

   private static class DummyView extends BaseView {
      protected DummyView(Context context) {
         super(context, new TypeToken<Context>() {
            private static final long serialVersionUID = 1L;
         });
      }
   }

   private static Context contextFor(Class<? extends Closeable> apiClass) {
      return ContextBuilder.newBuilder(forApiOnEndpoint(apiClass, "http://localhost")).build();
   }
}
