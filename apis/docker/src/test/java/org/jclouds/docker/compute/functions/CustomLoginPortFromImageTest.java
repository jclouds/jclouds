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
package org.jclouds.docker.compute.functions;

import static org.jclouds.docker.compute.config.LoginPortLookupModule.loginPortLookupBinder;
import static org.testng.Assert.assertEquals;

import org.jclouds.docker.compute.config.LoginPortLookupModule;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.MapBinder;

@Test(groups = "unit")
public class CustomLoginPortFromImageTest {

   private CustomLoginPortFromImage customLoginPortFromImage;

   @BeforeClass
   public void setup() {
      Injector i = Guice.createInjector(new LoginPortLookupModule(), new AbstractModule() {
         @Override
         protected void configure() {
            MapBinder<String, LoginPortForContainer> imageToFunction = loginPortLookupBinder(binder());
            imageToFunction.addBinding(".*alpine-ext.*").toInstance(LoginPortFromEnvVar);
            imageToFunction.addBinding(".*ubuntu.*").toInstance(AlwaysPort22);
            imageToFunction.addBinding(".*ubuntu:12\\.04.*").toInstance(AlwaysPort8080);
         }
      });
      customLoginPortFromImage = i.getInstance(CustomLoginPortFromImage.class);
   }

   public void testPortFromEnvironmentVariables() {
      Config config = Config.builder().image("alpine-ext:3.2").env(ImmutableList.of("FOO=bar", "SSH_PORT=2345"))
            .build();
      Container container = Container.builder().id("id").config(config).build();

      assertEquals(customLoginPortFromImage.apply(container).get().intValue(), 2345);
   }

   public void testMostSpecificImageIsPicked() {
      Config config = Config.builder().image("ubuntu:12.04").build();
      Container container = Container.builder().id("id").config(config).build();

      assertEquals(customLoginPortFromImage.apply(container).get().intValue(), 8080);
   }

   public void testNoImageFoundInMap() {
      Config config = Config.builder().image("unexisting").build();
      Container container = Container.builder().id("id").config(config).build();

      assertEquals(customLoginPortFromImage.apply(container), Optional.absent());
   }

   private static final LoginPortForContainer LoginPortFromEnvVar = new LoginPortForContainer() {
      @Override
      public Optional<Integer> apply(Container input) {
         Optional<String> portVariable = Iterables.tryFind(input.config().env(), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
               String[] var = input.split("=");
               return var[0].equals("SSH_PORT");
            }
         });
         return portVariable.isPresent() ? Optional.of(Integer.valueOf(portVariable.get().split("=")[1])) : Optional
               .<Integer> absent();
      }
   };

   private static final LoginPortForContainer AlwaysPort22 = new LoginPortForContainer() {
      @Override
      public Optional<Integer> apply(Container input) {
         return Optional.of(22);
      }
   };

   private static final LoginPortForContainer AlwaysPort8080 = new LoginPortForContainer() {
      @Override
      public Optional<Integer> apply(Container input) {
         return Optional.of(8080);
      }
   };
}
