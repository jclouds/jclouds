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

import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.compute.options.TemplateOptions.Builder.runAsRoot;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;

import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.docker.DockerApi;
import org.jclouds.docker.compute.functions.LoginPortForContainer;
import org.jclouds.docker.compute.options.DockerTemplateOptions;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.domain.ImageSummary;
import org.jclouds.docker.options.BuildOptions;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * This class tests configuring custom SSH port for Docker images.
 */
@Test(groups = "live", testName = "SshToCustomPortLiveTest", singleThreaded = true)
public class SshToCustomPortLiveTest extends BaseComputeServiceContextLiveTest {

   private static final int SSH_PORT = 8822;
   private static final String IMAGE_REPOSITORY = "jclouds/testrepo";
   private static final String IMAGE_TAG_1 = "testtag";
   private static final String IMAGE_TAG_2 = "second";

   private Image image;

   public SshToCustomPortLiveTest() {
      provider = "docker";
   }

   /**
    * Asserts that the new image exists and tags were created successfully in
    * the test preparation phase ({@link #setupContext()} method).
    */
   @Test
   public void testImageCreated() {
      assertNotNull(image);

      final String imageId = image.id();
      assertNotNull(imageId);

      List<ImageSummary> listImages = api().getImageApi().listImages();
      assertNotNull(listImages);
      ImageSummary testImage = Iterables.find(listImages, new Predicate<ImageSummary>() {
         @Override
         public boolean apply(ImageSummary input) {
            return imageId.equals(input.id());
         }
      });

      assertEquals(testImage.repoTags().size(), 2, "Unexpected number of tags on the image.");
      assertThat(testImage.repoTags()).contains(toTag(IMAGE_REPOSITORY, IMAGE_TAG_1),
            toTag(IMAGE_REPOSITORY, IMAGE_TAG_2));
   }

   /**
    * Start a node from the newly created image. The dropbear SSH server running
    * on custom port ( {@value #SSH_PORT}). The Docker networkMode used is
    * "host". Execute a command through the SSH connection and check the result.
    * Destroy the node when finished.
    * 
    * @throws RunNodesException
    */
   @Test(dependsOnMethods = "testImageCreated")
   public void testCustomPortSsh() throws RunNodesException {
      final DockerTemplateOptions options = DockerTemplateOptions.Builder
            .commands("/usr/sbin/dropbear", "-E", "-F", "-p", String.valueOf(SSH_PORT)).overrideLoginUser("root")
            .overrideLoginPassword("screencast").blockOnPort(SSH_PORT, 30).networkMode("host");

      final Template template = view.getComputeService().templateBuilder().imageId(image.id()).options(options).build();

      String nodeId = null;
      try {
         NodeMetadata node = Iterables
               .getOnlyElement(view.getComputeService().createNodesInGroup("ssh-test", 1, template));

         nodeId = node.getId();
         ExecResponse response = view.getComputeService().runScriptOnNode(nodeId, "echo hello",
               runAsRoot(false).wrapInInitScript(false));
         assertThat(response.getOutput().trim()).endsWith("hello");
      } finally {
         if (nodeId != null)
            view.getComputeService().destroyNode(nodeId);
      }
   }

   /**
    * Build a new image with 2 tags on it in the test preparation phase.
    * 
    * @see org.jclouds.apis.BaseContextLiveTest#setupContext()
    */
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();

      final String tag = toTag(IMAGE_REPOSITORY, IMAGE_TAG_1);
      BuildOptions options = BuildOptions.Builder.tag(tag).verbose(false).nocache(false);
      InputStream buildImageStream;
      try {
         buildImageStream = api().getMiscApi().build(BaseDockerApiLiveTest.tarredDockerfile(), options);
         consumeStreamSilently(buildImageStream);
      } catch (IOException e) {
         throw new RuntimeException("Error occured during building Docker image.", e);
      }
      image = api().getImageApi().inspectImage(tag);
      api().getImageApi().tagImage(image.id(), IMAGE_REPOSITORY, IMAGE_TAG_2, true);
   }

   /**
    * After the test remove created image (with all tags).
    * 
    * @see #setupContext()
    */
   @AfterClass(alwaysRun = true)
   protected void tearDown() {
      consumeStreamSilently(api().getImageApi().deleteImage(toTag(IMAGE_REPOSITORY, IMAGE_TAG_1)));
      consumeStreamSilently(api().getImageApi().deleteImage(toTag(IMAGE_REPOSITORY, IMAGE_TAG_2)));
   }

   /**
    * Configure used modules. A custom {@link LoginPortForContainer} binding is
    * added among logging and SSH module.
    * 
    * @see org.jclouds.compute.internal.BaseGenericComputeServiceContextLiveTest#setupModules()
    */
   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule(), new SshjSshClientModule(), new AbstractModule() {
         @Override
         protected void configure() {
            bind(LoginPortForContainer.class).toInstance(new LoginPortForContainer() {
               @Override
               public Optional<Integer> apply(Container input) {
                  return Optional.of(SSH_PORT);
               }
            });
         }
      });
   }

   /**
    * Return DockerApi for current Context.
    * 
    * @return
    */
   private DockerApi api() {
      return view.unwrapApi(DockerApi.class);
   }

   /**
    * Read all data from given InputStream and throw away all the bits.
    * 
    * @param is
    */
   private static void consumeStreamSilently(InputStream is) {
      char[] tmpBuff = new char[8 * 1024];
      // throw everything away
      InputStreamReader isr = new InputStreamReader(is);

      try {
         try {
            while (isr.read(tmpBuff) > -1) {
               // empty
            }
         } finally {
            isr.close();
         }
      } catch (IOException e) {
         java.util.logging.Logger.getAnonymousLogger().log(Level.WARNING, "Error ocured during reading InputStream.", e);
      }
   }

   /**
    * Concatenate repository and tag name (if provided) in Docker format.
    * 
    * @param repo
    * @param tag
    * @return
    */
   private static String toTag(String repo, String tag) {
      return repo + (tag != null ? ":" + tag : "");
   }
}
