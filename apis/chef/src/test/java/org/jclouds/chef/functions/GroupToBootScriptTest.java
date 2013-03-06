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
package org.jclouds.chef.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.chef.config.ChefProperties.CHEF_UPDATE_GEMS;
import static org.jclouds.chef.config.ChefProperties.CHEF_UPDATE_GEM_SYSTEM;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.security.PrivateKey;

import org.jclouds.chef.ChefAsyncApi;
import org.jclouds.chef.config.ChefBootstrapModule;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.crypto.PemsTest;
import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.domain.Statement;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "GroupToBootScriptTest")
public class GroupToBootScriptTest {

   Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
         bind(String.class).annotatedWith(ApiVersion.class).toInstance(ChefAsyncApi.VERSION);
         bind(String.class).annotatedWith(Names.named(CHEF_UPDATE_GEM_SYSTEM)).toInstance("true");
         bind(String.class).annotatedWith(Names.named(CHEF_UPDATE_GEMS)).toInstance("true");
      }
   }, new ChefParserModule(), new GsonModule(), new ChefBootstrapModule());

   Json json = injector.getInstance(Json.class);
   Statement installChefGems = injector.getInstance(Key.get(Statement.class, Names.named("installChefGems")));
   Optional<String> validatorName = Optional.<String> of("chef-validator");
   Optional<PrivateKey> validatorCredential = Optional.<PrivateKey> of(createMock(PrivateKey.class));

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMustHaveValidatorName() {
      GroupToBootScript fn = new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")), json,
            CacheLoader.from(Functions.forMap(ImmutableMap.<String, DatabagItem> of())), installChefGems,
            Optional.<String> absent(), validatorCredential);
      fn.apply("foo");
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMustHaveValidatorCredential() {
      GroupToBootScript fn = new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")), json,
            CacheLoader.from(Functions.forMap(ImmutableMap.<String, DatabagItem> of())), installChefGems,
            validatorName, Optional.<PrivateKey> absent());
      fn.apply("foo");
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Key 'foo' not present in map")
   public void testMustHaveRunScriptsName() {
      GroupToBootScript fn = new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")), json,
            CacheLoader.from(Functions.forMap(ImmutableMap.<String, DatabagItem> of())), installChefGems,
            validatorName, validatorCredential);
      fn.apply("foo");
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "null value in entry: foo=null")
   public void testMustHaveRunScriptsValue() {
      GroupToBootScript fn = new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")), json,
            CacheLoader.from(Functions.forMap(ImmutableMap.<String, DatabagItem> of("foo", (DatabagItem) null))),
            installChefGems, validatorName, validatorCredential);
      fn.apply("foo");
   }

   public void testOneRecipe() throws IOException {
      GroupToBootScript fn = new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")), json,
            CacheLoader.from(Functions.forMap(ImmutableMap.<String, JsonBall> of("foo", new JsonBall(
                  "{\"tomcat6\":{\"ssl_port\":8433},\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"]}")))),
            installChefGems, validatorName, validatorCredential);

      PrivateKey validatorKey = validatorCredential.get();
      expect(validatorKey.getEncoded()).andReturn(PemsTest.PRIVATE_KEY.getBytes());
      replay(validatorKey);

      assertEquals(
            fn.apply("foo").render(OsFamily.UNIX),
            Resources.toString(Resources.getResource("test_install_ruby." + ShellToken.SH.to(OsFamily.UNIX)),
                  Charsets.UTF_8)
                  + Resources.toString(
                        Resources.getResource("test_install_rubygems." + ShellToken.SH.to(OsFamily.UNIX)),
                        Charsets.UTF_8)
                  + "gem install chef --no-rdoc --no-ri\n"
                  + Resources.toString(Resources.getResource("bootstrap.sh"), Charsets.UTF_8));

      verify(validatorKey);
   }
}
