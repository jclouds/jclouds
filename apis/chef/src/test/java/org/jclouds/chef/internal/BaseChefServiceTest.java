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

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.domain.BootstrapConfig;
import org.jclouds.chef.filters.SignedHeaderAuthTest;
import org.jclouds.chef.util.RunListBuilder;
import org.jclouds.domain.JsonBall;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Unit tests for the <code>BaseChefService</code> class.
 */
@Test(groups = "unit", testName = "BaseChefServiceTest")
public class BaseChefServiceTest {

   private BaseChefService chefService;

   @BeforeClass
   public void setup() {
      Injector injector = ContextBuilder.newBuilder(new ChefApiMetadata())
            .credentials(SignedHeaderAuthTest.USER_ID, SignedHeaderAuthTest.PRIVATE_KEY)
            .modules(ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule())).buildInjector();

      chefService = injector.getInstance(BaseChefService.class);
   }

   public void testBuildBootstrapConfigurationWithEmptyRunlist() {
      BootstrapConfig bootstrapConfig = BootstrapConfig.builder().runList(ImmutableList.<String> of()).build();
      String config = chefService.buildBootstrapConfiguration(bootstrapConfig);
      assertEquals(config, "{\"run_list\":[]}");
   }

   public void testBuildBootstrapConfigurationWithRunlist() {
      List<String> runlist = new RunListBuilder().addRecipe("apache2").addRole("webserver").build();
      BootstrapConfig bootstrapConfig = BootstrapConfig.builder().runList(runlist).build();
      String config = chefService.buildBootstrapConfiguration(bootstrapConfig);
      assertEquals(config, "{\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"]}");
   }

   public void testBuildBootstrapConfigurationWithRunlistAndEmptyAttributes() {
      List<String> runlist = new RunListBuilder().addRecipe("apache2").addRole("webserver").build();
      BootstrapConfig bootstrapConfig = BootstrapConfig.builder().runList(runlist).attributes(new JsonBall("{}"))
            .build();
      String config = chefService.buildBootstrapConfiguration(bootstrapConfig);
      assertEquals(config, "{\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"],\"attributes\":{}}");
   }

   public void testBuildBootstrapConfigurationWithRunlistAndAttributes() {
      List<String> runlist = new RunListBuilder().addRecipe("apache2").addRole("webserver").build();
      BootstrapConfig bootstrapConfig = BootstrapConfig.builder().runList(runlist)
            .attributes(new JsonBall("{\"tomcat6\":{\"ssl_port\":8433}}")).build();
      String config = chefService.buildBootstrapConfiguration(bootstrapConfig);
      assertEquals(config,
            "{\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"],\"attributes\":{\"tomcat6\":{\"ssl_port\":8433}}}");
   }

   public void testBuildBootstrapConfigurationWithRunlistAndAttributesAndEnvironment() {
      List<String> runlist = new RunListBuilder().addRecipe("apache2").addRole("webserver").build();
      BootstrapConfig bootstrapConfig = BootstrapConfig.builder().runList(runlist)
            .attributes(new JsonBall("{\"tomcat6\":{\"ssl_port\":8433}}")).environment("env").build();
      String config = chefService.buildBootstrapConfiguration(bootstrapConfig);
      assertEquals(config, "{\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"],\"environment\":\"env\","
            + "\"attributes\":{\"tomcat6\":{\"ssl_port\":8433}}}");
   }

}
