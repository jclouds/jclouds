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
package org.jclouds.chef.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.chef.config.ChefProperties.CHEF_UPDATE_GEMS;
import static org.jclouds.chef.config.ChefProperties.CHEF_UPDATE_GEM_SYSTEM;
import static org.jclouds.chef.config.ChefProperties.CHEF_USE_OMNIBUS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.security.PrivateKey;

import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.config.ChefBootstrapModule;
import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.config.InstallChef;
import org.jclouds.chef.domain.BootstrapConfig;
import org.jclouds.chef.domain.BootstrapConfig.SSLVerifyMode;
import org.jclouds.chef.util.RunListBuilder;
import org.jclouds.crypto.PemsTest;
import org.jclouds.domain.JsonBall;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.domain.Statement;
import org.testng.annotations.BeforeClass;
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

@Test(groups = "unit", testName = "GroupToBootScriptTest")
public class GroupToBootScriptTest {

   private Statement installChefGems;
   private Statement installChefOmnibus;
   private Optional<String> validatorName;

   @BeforeClass
   public void setup() {
      Injector injectorGems = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(String.class).annotatedWith(ApiVersion.class).toInstance(ChefApiMetadata.DEFAULT_API_VERSION);
            bind(String.class).annotatedWith(Names.named(CHEF_UPDATE_GEM_SYSTEM)).toInstance("true");
            bind(String.class).annotatedWith(Names.named(CHEF_UPDATE_GEMS)).toInstance("true");
            bind(String.class).annotatedWith(Names.named(CHEF_USE_OMNIBUS)).toInstance("false");
         }
      }, new ChefParserModule(), new GsonModule(), new ChefBootstrapModule());

      Injector injectorOmnibus = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(String.class).annotatedWith(ApiVersion.class).toInstance(ChefApiMetadata.DEFAULT_API_VERSION);
            bind(String.class).annotatedWith(Names.named(CHEF_UPDATE_GEM_SYSTEM)).toInstance("true");
            bind(String.class).annotatedWith(Names.named(CHEF_UPDATE_GEMS)).toInstance("true");
            bind(String.class).annotatedWith(Names.named(CHEF_USE_OMNIBUS)).toInstance("true");
         }
      }, new ChefParserModule(), new GsonModule(), new ChefBootstrapModule());

      installChefGems = injectorGems.getInstance(Key.get(Statement.class, InstallChef.class));
      installChefOmnibus = injectorOmnibus.getInstance(Key.get(Statement.class, InstallChef.class));
      validatorName = Optional.<String> of("chef-validator");
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMustHaveValidatorName() {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      GroupToBootScript fn = new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")),
            CacheLoader.from(Functions.forMap(ImmutableMap.of("foo", BootstrapConfig.builder().build()))),
            installChefOmnibus, Optional.<String> absent(), validatorCredential);
      fn.apply("foo", null);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMustHaveValidatorCredential() {
      GroupToBootScript fn = new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")),
            CacheLoader.from(Functions.forMap(ImmutableMap.of("foo", BootstrapConfig.builder().build()))),
            installChefOmnibus, validatorName, Optional.<PrivateKey> absent());
      fn.apply("foo", null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Key 'foo' not present in map")
   public void testMustHaveRunScriptsName() {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      GroupToBootScript fn = new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")),
            CacheLoader.from(Functions.forMap(ImmutableMap.<String, BootstrapConfig> of())), installChefOmnibus,
            validatorName, validatorCredential);
      fn.apply("foo", null);
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "null value in entry: foo=null")
   public void testMustHaveRunScriptsValue() {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      GroupToBootScript fn = new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")),
            CacheLoader.from(Functions.forMap(ImmutableMap.of("foo", (BootstrapConfig) null))), installChefOmnibus,
            validatorName, validatorCredential);
      fn.apply("foo", null);
   }

   public void testOneRecipe() throws IOException {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      BootstrapConfig config = BootstrapConfig.builder().attributes(new JsonBall("{\"tomcat6\":{\"ssl_port\":8433}}"))
            .runList(new RunListBuilder().addRecipe("apache2").addRole("webserver").build()).build();

      GroupToBootScript fn = groupToBootScriptFor(config, validatorCredential, false);

      PrivateKey validatorKey = validatorCredential.get();
      expect(validatorKey.getEncoded()).andReturn(PemsTest.PRIVATE_KEY.getBytes());
      replay(validatorKey);

      assertEquals(
            fn.apply("foo", null).render(OsFamily.UNIX),
            exitInsteadOfReturn(OsFamily.UNIX, readContent("test_install_ruby.sh")
                  + readContent("test_install_rubygems.sh"))
                  + "gem install chef --no-rdoc --no-ri\n" + readContent("bootstrap.sh"));

      verify(validatorKey);
   }

   public void testOneRecipeAndEnvironment() throws IOException {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      BootstrapConfig config = BootstrapConfig.builder().attributes(new JsonBall("{\"tomcat6\":{\"ssl_port\":8433}}"))
            .environment("env").runList(new RunListBuilder().addRecipe("apache2").addRole("webserver").build()).build();

      GroupToBootScript fn = groupToBootScriptFor(config, validatorCredential, false);

      PrivateKey validatorKey = validatorCredential.get();
      expect(validatorKey.getEncoded()).andReturn(PemsTest.PRIVATE_KEY.getBytes());
      replay(validatorKey);

      assertEquals(
            fn.apply("foo", null).render(OsFamily.UNIX),
            exitInsteadOfReturn(OsFamily.UNIX, readContent("test_install_ruby.sh")
                  + readContent("test_install_rubygems.sh") + "gem install chef --no-rdoc --no-ri\n"
                  + readContent("bootstrap-env.sh")));

      verify(validatorKey);
   }

   public void testOneRecipeOmnibus() throws IOException {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      BootstrapConfig config = BootstrapConfig.builder().attributes(new JsonBall("{\"tomcat6\":{\"ssl_port\":8433}}"))
            .runList(new RunListBuilder().addRecipe("apache2").addRole("webserver").build()).build();

      GroupToBootScript fn = groupToBootScriptFor(config, validatorCredential, true);

      PrivateKey validatorKey = validatorCredential.get();
      expect(validatorKey.getEncoded()).andReturn(PemsTest.PRIVATE_KEY.getBytes());
      replay(validatorKey);

      assertEquals(fn.apply("foo", null).render(OsFamily.UNIX),
            "setupPublicCurl || exit 1\ncurl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 "
                  + "-X GET  https://www.opscode.com/chef/install.sh |(bash)\n" + readContent("bootstrap.sh"));

      verify(validatorKey);
   }

   public void testOneRecipeAndEnvironmentOmnibus() throws IOException {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      BootstrapConfig config = BootstrapConfig.builder().attributes(new JsonBall("{\"tomcat6\":{\"ssl_port\":8433}}"))
            .environment("env").runList(new RunListBuilder().addRecipe("apache2").addRole("webserver").build()).build();

      GroupToBootScript fn = groupToBootScriptFor(config, validatorCredential, true);

      PrivateKey validatorKey = validatorCredential.get();
      expect(validatorKey.getEncoded()).andReturn(PemsTest.PRIVATE_KEY.getBytes());
      replay(validatorKey);

      assertEquals(fn.apply("foo", null).render(OsFamily.UNIX),
            "setupPublicCurl || exit 1\ncurl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 "
                  + "-X GET  https://www.opscode.com/chef/install.sh |(bash)\n" + readContent("bootstrap-env.sh"));

      verify(validatorKey);
   }

   public void testNoAttribtues() throws IOException {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      BootstrapConfig config = BootstrapConfig.builder()
            .environment("env").runList(new RunListBuilder().addRecipe("apache2").addRole("webserver").build()).build();

      GroupToBootScript fn = groupToBootScriptFor(config, validatorCredential, true);

      PrivateKey validatorKey = validatorCredential.get();
      expect(validatorKey.getEncoded()).andReturn(PemsTest.PRIVATE_KEY.getBytes());
      replay(validatorKey);

      assertEquals(fn.apply("foo", null).render(OsFamily.UNIX),
            "setupPublicCurl || exit 1\ncurl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 "
                  + "-X GET  https://www.opscode.com/chef/install.sh |(bash)\n" + readContent("bootstrap-env-noattrs.sh"));

      verify(validatorKey);
   }

   public void testCustomNodeName() throws IOException {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      BootstrapConfig config = BootstrapConfig.builder().attributes(new JsonBall("{\"tomcat6\":{\"ssl_port\":8433}}"))
            .environment("env").runList(new RunListBuilder().addRecipe("apache2").addRole("webserver").build()).build();

      GroupToBootScript fn = groupToBootScriptFor(config, validatorCredential, true);

      PrivateKey validatorKey = validatorCredential.get();
      expect(validatorKey.getEncoded()).andReturn(PemsTest.PRIVATE_KEY.getBytes());
      replay(validatorKey);

      assertEquals(fn.apply("foo", "bar").render(OsFamily.UNIX),
            "setupPublicCurl || exit 1\ncurl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 "
                  + "-X GET  https://www.opscode.com/chef/install.sh |(bash)\n" + readContent("bootstrap-node-env.sh"));

      verify(validatorKey);
   }

   public void testCustomSecurityOptions() throws IOException {
      Optional<PrivateKey> validatorCredential = Optional.of(createMock(PrivateKey.class));
      BootstrapConfig config = BootstrapConfig.builder().attributes(new JsonBall("{\"tomcat6\":{\"ssl_port\":8433}}"))
            .runList(new RunListBuilder().addRecipe("apache2").addRole("webserver").build())
            .sslCAFile(readContent("chef.crt")).sslCAPath("/etc/chef").sslVerifyMode(SSLVerifyMode.PEER)
            .verifyApiCert(true).build();

      GroupToBootScript fn = groupToBootScriptFor(config, validatorCredential, true);

      PrivateKey validatorKey = validatorCredential.get();
      expect(validatorKey.getEncoded()).andReturn(PemsTest.PRIVATE_KEY.getBytes());
      replay(validatorKey);

      assertEquals(fn.apply("foo", null).render(OsFamily.UNIX),
            "setupPublicCurl || exit 1\ncurl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 "
                  + "-X GET  https://www.opscode.com/chef/install.sh |(bash)\n" + readContent("bootstrap-ssl.sh"));

      verify(validatorKey);
   }

   private static String exitInsteadOfReturn(OsFamily family, String input) {
      return input.replaceAll(ShellToken.RETURN.to(family), ShellToken.EXIT.to(family));
   }

   private GroupToBootScript groupToBootScriptFor(BootstrapConfig config, Optional<PrivateKey> validatorCredential,
         boolean useOmnibus) {
      return new GroupToBootScript(Suppliers.ofInstance(URI.create("http://localhost:4000")),
            CacheLoader.from(Functions.forMap(ImmutableMap.of("foo", config))), useOmnibus ? installChefOmnibus
                  : installChefGems, validatorName, validatorCredential);
   }

   private static String readContent(String resource) throws IOException {
      return Resources.toString(Resources.getResource(resource), Charsets.UTF_8);
   }
}
