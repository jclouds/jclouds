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
package org.jclouds.azurecompute.arm.compute;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.inject.Module;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.RESOURCE_GROUP_NAME;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.options.TemplateOptions.Builder.overrideLoginCredentials;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "AzureComputeServiceContextLiveTest")
public class AzureComputeServiceContextLiveTest extends BaseComputeServiceContextLiveTest {

   public String azureGroup;
   protected static final int RAND = new Random().nextInt(999);

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override protected Properties setupProperties() {
      azureGroup = "jc" + RAND;

      Properties properties = super.setupProperties();
      long scriptTimeout = TimeUnit.MILLISECONDS.convert(60, TimeUnit.MINUTES);
      properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_RUNNING, scriptTimeout + "");

      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");

      properties.put(RESOURCE_GROUP_NAME, azureGroup);
      return properties;
   }

   public AzureComputeServiceContextLiveTest() {
      provider = "azurecompute-arm";
   }

   @Test
   public void testDefault() throws RunNodesException {

      final String groupName = this.azureGroup;
      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      templateBuilder.osFamily(OsFamily.UBUNTU);
      templateBuilder.osVersionMatches("14.04");
      templateBuilder.hardwareId("Standard_A0");
      templateBuilder.locationId("westus");

      final Template template = templateBuilder.build();

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, template);
         assertThat(nodes).hasSize(1);
      } finally {
// Do not destroy         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

   private LoginCredentials getLogin() {
      Credentials credentials = new Credentials("jclouds", "Password1!");
      LoginCredentials login = LoginCredentials.fromCredentials(credentials);
      return login;
   }

   @Test(dependsOnMethods = "testDefault")
   public void testExec() throws RunScriptOnNodesException {
      final String groupName = this.azureGroup;
      String command = "echo hello";

      Map<? extends NodeMetadata, ExecResponse> responses = view.getComputeService().runScriptOnNodesMatching(//
              inGroup(groupName), // predicate used to select nodes
              exec(command), // what you actually intend to run
              overrideLoginCredentials(getLogin()) // use my local user &
                      // ssh key
                      .runAsRoot(false) // don't attempt to run as root (sudo)
                      .wrapInInitScript(false)); // run command directly

      assertTrue(responses.size() > 0);
   }

   public static Predicate<ComputeMetadata> nameStartsWith(final String prefix) {
      Preconditions.checkNotNull(prefix, "prefix must be defined");

      return new Predicate<ComputeMetadata>() {
         @Override
         public boolean apply(ComputeMetadata computeMetadata) {
            return computeMetadata.getName().startsWith(prefix);
         }

         @Override
         public String toString() {
            return "nameStartsWith(" + prefix + ")";
         }
      };
   }

   @Test(dependsOnMethods = "testExec")
   public void testStop() throws RunScriptOnNodesException {
      final String groupName = this.azureGroup;
      Set<? extends NodeMetadata> nodes = view.getComputeService().suspendNodesMatching(inGroup(groupName));
      assertTrue(nodes.size() > 0);

      boolean allStopped = false;
      while (!allStopped) {
         nodes = view.getComputeService().listNodesDetailsMatching(nameStartsWith(groupName));
         for (NodeMetadata node : nodes) {
            if (node.getStatus() != NodeMetadata.Status.SUSPENDED)
            {
               // Not stopped yet
               allStopped = false;
               try {
                  Thread.sleep(15 * 1000);
               } catch (InterruptedException e) {
               }
               continue;
            }
            else
            {
               allStopped = true;
            }
         }
      }
      assertTrue(allStopped);
   }

   @Test(dependsOnMethods = "testStop")
   public void testStart() throws RunScriptOnNodesException {
      final String groupName = this.azureGroup;
      Set<? extends NodeMetadata> nodes = view.getComputeService().resumeNodesMatching(inGroup(groupName));
      assertTrue(nodes.size() > 0);

      boolean allStarted = false;
      while (!allStarted) {
         nodes = view.getComputeService().listNodesDetailsMatching(nameStartsWith(groupName));
         for (NodeMetadata node : nodes) {
            if (node.getStatus() != NodeMetadata.Status.RUNNING)
            {
               // Not started yet
               allStarted = false;
               try {
                  Thread.sleep(15 * 1000);
               } catch (InterruptedException e) {
               }
               continue;
            }
            else
            {
               allStarted = true;
            }
         }
      }
      assertTrue(allStarted);
   }

   @Test(dependsOnMethods = "testStart")
   public void testRestart() throws RunScriptOnNodesException {
      final String groupName = this.azureGroup;
      Set<? extends NodeMetadata> nodes = view.getComputeService().rebootNodesMatching(inGroup(groupName));
      assertTrue(nodes.size() > 0);

      boolean allRestarted = false;
      while (!allRestarted) {
         nodes = view.getComputeService().listNodesDetailsMatching(nameStartsWith(groupName));
         for (NodeMetadata node : nodes) {
            if (node.getStatus() != NodeMetadata.Status.RUNNING)
            {
               // Not started yet
               allRestarted = false;
               try {
                  Thread.sleep(30 * 1000);
               } catch (InterruptedException e) {
               }
               continue;
            }
            else
            {
               allRestarted = true;
            }
         }
      }
      assertTrue(allRestarted);

      view.getComputeService().destroyNodesMatching(inGroup(groupName));
   }

   @Test(dependsOnMethods = "testRestart")
   public void testLinuxNode() throws RunNodesException {
      final String groupName = this.azureGroup;
      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      templateBuilder.osFamily(OsFamily.UBUNTU);
      templateBuilder.osVersionMatches("14.04");
      templateBuilder.hardwareId("Standard_A0");
      templateBuilder.locationId("westus");
      final Template template = templateBuilder.build();

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, template);
         assertThat(nodes).hasSize(1);
      } finally {
         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

   @Test(dependsOnMethods = "testLinuxNode")
   public void testWindowsNode() throws RunNodesException {
      final String groupName = this.azureGroup;
      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      templateBuilder.imageId("global/MicrosoftWindowsServer/WindowsServer/Windows-Server-Technical-Preview");
      templateBuilder.hardwareId("Standard_A0");
      templateBuilder.locationId("westus");
      final Template template = templateBuilder.build();

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, template);
         assertThat(nodes).hasSize(1);
      } finally {
         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      AzureComputeProviderMetadata pm = AzureComputeProviderMetadata.builder().build();
      return pm;
   }

   protected String setIfTestSystemPropertyPresent(Properties overrides, String key) {
      if (System.getProperties().containsKey("test." + key)) {
         String val = System.getProperty("test." + key);
         overrides.setProperty(key, val);
         return val;
      } else {
         return null;
      }
   }

}
