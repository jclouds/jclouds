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

import org.jclouds.Context;
import org.jclouds.View;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Utility methods to allow {@link Context} and {@link View} linking between
 * contexts.
 * <p>
 * By using this module users can configure a context to be able to inject other
 * contexts or views by their provider id.
 */
public class ContextLinking {

   static final TypeLiteral<Supplier<Context>> CONTEXT_SUPPLIER = new TypeLiteral<Supplier<Context>>() {
   };

   static final TypeLiteral<Supplier<View>> VIEW_SUPPLIER = new TypeLiteral<Supplier<View>>() {
   };

   public static Module linkView(final String id, final Supplier<View> view) {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(VIEW_SUPPLIER).annotatedWith(Names.named(id)).toInstance(view);
            bind(CONTEXT_SUPPLIER).annotatedWith(Names.named(id)).toInstance(Suppliers.compose(ViewToContext, view));
         }
      };
   }

   public static Module linkContext(final String id, final Supplier<Context> context) {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(CONTEXT_SUPPLIER).annotatedWith(Names.named(id)).toInstance(context);
         }
      };
   }

   public static Module linkView(View view) {
      return linkView(view.unwrap().getId(), Suppliers.ofInstance(view));
   }

   public static Module linkContext(Context context) {
      return linkContext(context.getId(), Suppliers.ofInstance(context));
   }

   private static final Function<View, Context> ViewToContext = new Function<View, Context>() {
      @Override
      public Context apply(View input) {
         return input.unwrap();
      }
   };
}
