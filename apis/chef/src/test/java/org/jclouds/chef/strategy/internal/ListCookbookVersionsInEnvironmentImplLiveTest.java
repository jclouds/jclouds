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
package org.jclouds.chef.strategy.internal;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.ChecksumStatus;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.Metadata;
import org.jclouds.chef.domain.Resource;
import org.jclouds.chef.domain.Sandbox;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.chef.internal.BaseChefLiveTest;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.FilePayload;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.primitives.Bytes;

/**
 * Tests behavior of {@code ListCookbookVersionsInEnvironmentImpl} strategies
 */
@Test(groups = "live", testName = "ListCookbookVersionsInEnvironmentImplLiveTest")
public class ListCookbookVersionsInEnvironmentImplLiveTest extends BaseChefLiveTest<ChefApi> {
   public static final String PREFIX = "jcloudstest-strategy-" + System.getProperty("user.name");

   private ListCookbookVersionsInEnvironmentImpl strategy;
   private CreateNodeAndPopulateAutomaticAttributesImpl creator;

   private ExecutorService testExecutorService;
   private ListeningExecutorService testListeningExecutorService;

   @Override
   protected void initialize() {
      super.initialize();

      try {
         createCookbooksWithMultipleVersions(PREFIX);
         createCookbooksWithMultipleVersions(PREFIX + 1);
      } catch (Exception e) {
         fail("Could not create cookbooks", e);
      }

      this.strategy = injector.getInstance(ListCookbookVersionsInEnvironmentImpl.class);
      this.testExecutorService = Executors.newFixedThreadPool(5);
      this.testListeningExecutorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5));
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      api.deleteCookbook(PREFIX, "0.0.0");
      api.deleteCookbook(PREFIX, "1.0.0");
      api.deleteCookbook(PREFIX + 1, "0.0.0");
      api.deleteCookbook(PREFIX + 1, "1.0.0");

      this.testExecutorService.shutdown();
      this.testListeningExecutorService.shutdown();

      super.tearDown();
   }

   @Test
   public void testExecute() {
      assertTrue(size(strategy.execute("_default")) > 0, "Expected one or more elements");
   }

   @Test
   public void testExecuteConcurrentlyWithExecutorService() {
      assertTrue(size(strategy.execute(testExecutorService, "_default")) > 0,
            "Expected one or more elements");
   }

   @Test
   public void testExecuteConcurrentlyWithListeningExecutorService() {
      assertTrue(size(strategy.execute(testListeningExecutorService, "_default")) > 0,
            "Expected one or more elements");
   }

   @Test
   public void testExecuteWithNumVersions() {
      assertTrue(size(strategy.execute("_default", "2")) > 0, "Expected one or more elements");
   }

   @Test
   public void testExecuteConcurrentlyWithNumVersionsAndExecutorService() {
      assertTrue(size(strategy.execute(testExecutorService, "_default", "2")) > 0,
            "Expected one or more elements");
   }

   @Test
   public void testExecuteConcurrentlyWithNumVersionsAndListeningExecutorService() {
      assertTrue(size(strategy.execute(testListeningExecutorService, "_default", "2")) > 0,
            "Expected one or more elements");
   }

   @Test
   public void testExecuteWithNumVersionsAll() {
      assertTrue(size(strategy.execute("_default", "all")) > 0, "Expected one or more elements");
   }

   @Test
   public void testExecuteConcurrentlyWithNumVersionsAllAndExecutorService() {
      assertTrue(size(strategy.execute(testExecutorService, "_default", "all")) > 0,
            "Expected one or more elements");
   }

   @Test
   public void testExecuteConcurrentlyWithNumVersionsAllAndListeningExecutorService() {
      assertTrue(size(strategy.execute(testListeningExecutorService, "_default", "all")) > 0,
            "Expected one or more elements");
   }

   private FilePayload uploadContent(String fileName) throws Exception {
      // Define the file you want in the cookbook
      File file = new File(System.getProperty("user.dir"), fileName);
      FilePayload content = Payloads.newFilePayload(file);
      content.getContentMetadata().setContentType("application/x-binary");

      // Get an md5 so that you can see if the server already has it or not
      content.getContentMetadata().setContentMD5(Files.asByteSource(file).hash(Hashing.md5()).asBytes());

      // Note that java collections cannot effectively do equals or hashcodes on
      // byte arrays, so let's convert to a list of bytes.
      List<Byte> md5 = Bytes.asList(content.getContentMetadata().getContentMD5());

      // Request an upload site for this file
      UploadSandbox site = api.createUploadSandboxForChecksums(ImmutableSet.of(md5));
      assertTrue(site.getChecksums().containsKey(md5), md5 + " not in " + site.getChecksums());

      try {
         // Upload the file contents, if still not uploaded
         ChecksumStatus status = site.getChecksums().get(md5);
         if (status.needsUpload()) {
            api.uploadContent(status.getUrl(), content);
         }
         Sandbox sandbox = api.commitSandbox(site.getSandboxId(), true);
         assertTrue(sandbox.isCompleted(), "Sandbox should be completed after uploading");
      } catch (RuntimeException e) {
         api.commitSandbox(site.getSandboxId(), false);
         fail("Could not upload content", e);
      }

      return content;
   }

   private void createCookbooksWithMultipleVersions(String cookbookName) throws Exception {
      FilePayload v0content = uploadContent("pom.xml");
      FilePayload v1content = uploadContent("../README.md");

      // Create the metadata of the cookbook
      Metadata metadata = Metadata.builder() //
            .name(cookbookName) //
            .version("0.0.0") //
            .description("Jclouds test uploaded cookbook") //
            .maintainer("jclouds") //
            .maintainerEmail("someone@jclouds.org") //
            .license("Apache 2.0") //
            .build();

      // Create new cookbook version
      CookbookVersion cookbook = CookbookVersion.builder(cookbookName, "0.0.0") //
            .metadata(metadata) //
            .rootFile(Resource.builder().fromPayload(v0content).build()) //
            .build();

      // upload the cookbook to the remote server
      api.updateCookbook(cookbookName, "0.0.0", cookbook);

      // Create the metadata of the cookbook
      metadata = Metadata.builder() //
            .name(cookbookName) //
            .version("1.0.0") //
            .description("Jclouds test uploaded cookbook") //
            .maintainer("jclouds") //
            .maintainerEmail("someone@jclouds.org") //
            .license("Apache 2.0") //
            .build();

      // Create a new cookbook version
      cookbook = CookbookVersion.builder(cookbookName, "1.0.0") //
            .metadata(metadata) //
            .rootFile(Resource.builder().fromPayload(v1content).build()) //
            .build();

      // upload the cookbook to the remote server
      api.updateCookbook(cookbookName, "1.0.0", cookbook);
   }
}
