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
package org.jclouds.docker.compute.config;

import org.jclouds.docker.compute.functions.LoginPortForContainer;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;

public class LoginPortLookupModule extends AbstractModule {

   @Override
   protected void configure() {
      // Declare it to initialize the binder allowing duplicates. Users may
      // provide different functions for the same image in different modules, or
      // we could provide predefined functions for known images. This allows
      // users to set their own ones too.
      loginPortLookupBinder(binder());
   }

   public static MapBinder<String, LoginPortForContainer> loginPortLookupBinder(Binder binder) {
      return MapBinder.newMapBinder(binder, String.class, LoginPortForContainer.class).permitDuplicates();
   }

}
