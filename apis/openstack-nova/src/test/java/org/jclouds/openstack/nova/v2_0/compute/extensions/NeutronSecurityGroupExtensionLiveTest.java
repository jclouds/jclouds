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
package org.jclouds.openstack.nova.v2_0.compute.extensions;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import javax.annotation.Resource;
import javax.inject.Named;

import com.google.common.base.Optional;
import org.jclouds.Context;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.extensions.internal.BaseSecurityGroupExtensionLiveTest;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.config.ContextLinking;
import org.jclouds.logging.Logger;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.rest.ApiContext;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

import static org.testng.Assert.assertTrue;

/**
 * Live test for openstack-neutron {@link SecurityGroupExtension} implementation.
 */
@Test(groups = "live", singleThreaded = true, testName = "NeutronSecurityGroupExtensionLiveTest")
public class NeutronSecurityGroupExtensionLiveTest extends BaseSecurityGroupExtensionLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.CONSOLE;

   private Context neutronApiContext;

   public NeutronSecurityGroupExtensionLiveTest() {
      provider = "openstack-nova";

      Properties overrides = setupProperties();
      neutronApiContext = ContextBuilder.newBuilder("openstack-neutron")
               .endpoint(setIfTestSystemPropertyPresent(overrides,
               "openstack-nova.endpoint"))
               .credentials(setIfTestSystemPropertyPresent(overrides,
               "openstack-nova.identity"),
               setIfTestSystemPropertyPresent(overrides, "openstack-nova.credential"))
              .modules(ImmutableSet.<Module>of(
                      new SshjSshClientModule(),
                      new SLF4JLoggingModule())
              )
              .build(new TypeToken<ApiContext<NeutronApi>>() {});
   }


   @Test(groups = { "integration", "live" }, singleThreaded = true)
   public void testListSecurityGroups() throws RunNodesException, InterruptedException, ExecutionException {
      skipIfSecurityGroupsNotSupported();
      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security extension was not present");

      for (SecurityGroup securityGroup : securityGroupExtension.get().listSecurityGroups()) {
         logger.info(securityGroup.toString());
      }

   }

   @Test(groups = { "integration", "live" }, singleThreaded = true)
   public void testListSecurityGroupsForNode() throws RunNodesException, InterruptedException, ExecutionException {
      skipIfSecurityGroupsNotSupported();
      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security extension was not present");

      for (SecurityGroup securityGroup : securityGroupExtension.get().listSecurityGroupsForNode("uk-1/97374b9f-c706-4c4a-ae5a-48b6d2e58db9")) {
         logger.info(securityGroup.toString());
      }

   }

   @AfterClass
   @Override
   protected void tearDownContext() {
      super.tearDownContext();
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(ContextLinking.linkContext(neutronApiContext), getLoggingModule(), credentialStoreModule, getSshModule());
   }

}
