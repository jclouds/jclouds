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

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefApiMetadata;
import org.jclouds.chef.filters.SignedHeaderAuthTest;
import org.jclouds.chef.util.RunListBuilder;
import org.jclouds.domain.JsonBall;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Unit tests for the <code>BaseChefService</code> class.
 * 
 * @author Ignasi Barrera
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

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "runList must not be null")
   public void testBuildBootstrapConfigurationWithNullRunlist() {
      chefService.buildBootstrapConfiguration(null, null);
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "jsonAttributes must not be null")
   public void testBuildBootstrapConfigurationWithNullJsonAttributes() {
      chefService.buildBootstrapConfiguration(ImmutableList.<String> of(), null);
   }

   public void testBuildBootstrapConfigurationWithEmptyRunlist() {
      String config = chefService
            .buildBootstrapConfiguration(ImmutableList.<String> of(), Optional.<JsonBall> absent());
      assertEquals(config, "{\"run_list\":[]}");
   }

   public void testBuildBootstrapConfigurationWithRunlist() {
      List<String> runlist = new RunListBuilder().addRecipe("apache2").addRole("webserver").build();
      String config = chefService.buildBootstrapConfiguration(runlist, Optional.<JsonBall> absent());
      assertEquals(config, "{\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"]}");
   }

   public void testBuildBootstrapConfigurationWithRunlistAndEmptyAttributes() {
      List<String> runlist = new RunListBuilder().addRecipe("apache2").addRole("webserver").build();
      String config = chefService.buildBootstrapConfiguration(runlist, Optional.of(new JsonBall("{}")));
      assertEquals(config, "{\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"]}");
   }

   public void testBuildBootstrapConfigurationWithRunlistAndAttributes() {
      List<String> runlist = new RunListBuilder().addRecipe("apache2").addRole("webserver").build();
      String config = chefService.buildBootstrapConfiguration(runlist,
            Optional.of(new JsonBall("{\"tomcat6\":{\"ssl_port\":8433}}")));
      assertEquals(config, "{\"tomcat6\":{\"ssl_port\":8433},\"run_list\":[\"recipe[apache2]\",\"role[webserver]\"]}");
   }

}
