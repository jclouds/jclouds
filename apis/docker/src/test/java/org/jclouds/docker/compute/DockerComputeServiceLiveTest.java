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
package org.jclouds.docker.compute;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * Live tests for the {@link org.jclouds.compute.ComputeService} integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "DockerComputeServiceLiveTest")
public class DockerComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   private static final String DEFAULT_JCLOUDS_IMAGE = "jclouds/default";
   private Image defaultImage;

   public DockerComputeServiceLiveTest() {
      provider = "docker";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected void initializeContext() {
      super.initializeContext();
      Optional<? extends Image> optionalImage = Iterables.tryFind(client.listImages(), new Predicate<Image>() {
         @Override
         public boolean apply(Image image) {
            return image.getName().equals(DEFAULT_JCLOUDS_IMAGE);
         }
      });
      if (optionalImage.isPresent()) {
         defaultImage = optionalImage.get();
      } else {
         Assert.fail("Please create an ssh-able image called " + DEFAULT_JCLOUDS_IMAGE);
      }
   }

   @Override
   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.imageId(defaultImage.getId()).build();
   }

   @Override
   public void testOptionToNotBlock() throws Exception {
      // Docker ComputeService implementation has to block until the node
      // is provisioned, to be able to return it.
   }

   @Override
   protected void checkTagsInNodeEquals(NodeMetadata node, ImmutableSet<String> tags) {
      // Docker does not support tags
   }

   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      // Docker does not support user metadata
   }

   @Override
   public void testCreateAndRunAService() throws Exception {
      // Docker does not support blockOnPort
   }

   @Override
   @Test(enabled = true, dependsOnMethods = { "testCompareSizes" })
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      super.testAScriptExecutionAfterBootWithBasicTemplate();
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testReboot", expectedExceptions = UnsupportedOperationException.class)
   public void testSuspendResume() throws Exception {
      super.testSuspendResume();
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testGetNodesWithDetails() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodes() throws Exception {
      super.testListNodes();
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodesByIds() throws Exception {
      super.testListNodesByIds();
   }

   @Override
   @Test(enabled = true, dependsOnMethods = { "testListNodes", "testGetNodesWithDetails", "testListNodesByIds" })
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }

   @Test(enabled = true, expectedExceptions = NullPointerException.class)
   public void testCorrectExceptionRunningNodesNotFound() throws Exception {
      super.testCorrectExceptionRunningNodesNotFound();
   }

   @Test(enabled = true, expectedExceptions = NullPointerException.class)
   public void testCorrectAuthException() throws Exception {
      // Docker does not support authentication yet
      super.testCorrectAuthException();
   }

}
