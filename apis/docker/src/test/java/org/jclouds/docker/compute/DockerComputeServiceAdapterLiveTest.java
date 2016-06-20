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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Properties;
import java.util.Random;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.docker.DockerApi;
import org.jclouds.docker.compute.options.DockerTemplateOptions;
import org.jclouds.docker.compute.strategy.DockerComputeServiceAdapter;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.Image;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", singleThreaded = true, testName = "DockerComputeServiceAdapterLiveTest")
public class DockerComputeServiceAdapterLiveTest extends BaseDockerApiLiveTest {

   private static final String SSHABLE_IMAGE = "kwart/alpine-ext";
   private static final String SSHABLE_IMAGE_TAG = "3.3-ssh";
   private Image defaultImage;

   private DockerComputeServiceAdapter adapter;
   private TemplateBuilder templateBuilder;
   private NodeAndInitialCredentials<Container> guest;
   private static final String CHUANWEN_COWSAY = "chuanwen/cowsay";

   @BeforeClass
   protected void init() {
      super.initialize();
      String imageName = SSHABLE_IMAGE + ":" + SSHABLE_IMAGE_TAG;
      defaultImage = adapter.getImage(imageName);
      assertNotNull(defaultImage);
   }

   @AfterClass(alwaysRun = true)
   protected void tearDown() {
      if (guest != null) {
         adapter.destroyNode(guest.getNode().id() + "");
      }
      if (api.getImageApi().inspectImage(CHUANWEN_COWSAY) != null) {
         api.getImageApi().deleteImage(CHUANWEN_COWSAY);
      }
      super.tearDown();
   }

   @Override
   protected DockerApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      adapter = injector.getInstance(DockerComputeServiceAdapter.class);
      templateBuilder = injector.getInstance(TemplateBuilder.class);
      return injector.getInstance(DockerApi.class);
   }

   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials() {
      String group = "foo";
      String name = "container" + new Random().nextInt();

      Template template = templateBuilder.imageId(defaultImage.id()).build();

      DockerTemplateOptions options = template.getOptions().as(DockerTemplateOptions.class);
      options.env(ImmutableList.of("ROOT_PASSWORD=password"));
      guest = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      assertEquals(guest.getNodeId(), guest.getNode().id());
   }

   public void testListHardwareProfiles() {
      Iterable<Hardware> profiles = adapter.listHardwareProfiles();
      assertFalse(Iterables.isEmpty(profiles));

      for (Hardware profile : profiles) {
         assertNotNull(profile);
      }
   }

   public void testGetImageNotHiddenByCache() {

      //Ensure image to be tested is unknown to jclouds and docker and that cache is warm
      assertNull(findImageFromListImages(CHUANWEN_COWSAY));
      assertNull(api.getImageApi().inspectImage(CHUANWEN_COWSAY));

      // Get new image
      adapter.getImage(CHUANWEN_COWSAY);

      assertNotNull(findImageFromListImages(CHUANWEN_COWSAY), "New image is not available from listImages presumably due to caching");
   }

   private Image findImageFromListImages(final String image) {
      return Iterables.find(adapter.listImages(), new Predicate<Image>() {
        @Override
        public boolean apply(Image input) {
           for (String tag : input.repoTags()) {
              if (tag.equals(image) || tag.equals(CHUANWEN_COWSAY + ":latest")) {
                 return true;
              }
           }
           return false;
        }
     }, null);
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module>of(getLoggingModule(), new SshjSshClientModule());
   }

}
