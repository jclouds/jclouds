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
package org.jclouds.openstack.nova.v2_0.config;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "unit")
public class ImageAdapterTest {

   private Gson gson;

   @BeforeTest
   public void setup() {
      Injector injector = Guice.createInjector(new GsonModule(), new NovaParserModule());
      gson = injector.getInstance(Gson.class);
   }

   public void testDeserializeWithBlockDeviceMappingAndMetadata() throws Exception {
      ImageContainer container = gson.fromJson(stringFromResource("image_details_with_block_device_mapping.json"), ImageContainer.class);

      // Note that the block device mapping keys are removed from the metadata by the adapter.
      assertNotNull(container.image.getMetadata());
      assertEquals(container.image.getMetadata().size(), 2);
      assertEquals("Gold", container.image.getMetadata().get("ImageType"));
      assertEquals("1.5", container.image.getMetadata().get("ImageVersion"));

      assertNotNull(container.image.getBlockDeviceMapping());
      assertEquals(container.image.getBlockDeviceMapping().size(), 1);
      assertEquals(Integer.valueOf(2), getOnlyElement(container.image.getBlockDeviceMapping()).getBootIndex());
      assertEquals("snapshot", getOnlyElement(container.image.getBlockDeviceMapping()).getSourceType());
   }

   public void testDeserializeWithoutBlockDeviceMapping() throws Exception {
      ImageContainer container = gson.fromJson(stringFromResource("image_details.json"), ImageContainer.class);

      assertNotNull(container.image.getMetadata());
      assertEquals(container.image.getMetadata().size(), 2);
      assertEquals("Gold", container.image.getMetadata().get("ImageType"));
      assertEquals("1.5", container.image.getMetadata().get("ImageVersion"));

      assertNotNull(container.image.getBlockDeviceMapping());
      assertEquals(0, container.image.getBlockDeviceMapping().size());
   }

   public void testDeserializeWithoutBlockDeviceMappingOrMetadata() throws Exception {
      ImageContainer container = gson.fromJson(stringFromResource("image_details_without_metadata.json"), ImageContainer.class);

      assertNotNull(container.image.getMetadata());
      assertEquals(container.image.getMetadata().size(), 0);
      assertNotNull(container.image.getBlockDeviceMapping());
      assertEquals(0, container.image.getBlockDeviceMapping().size());
   }

   private String stringFromResource(String resource) throws IOException {
      return Resources.toString(Resources.getResource(resource), Charsets.UTF_8);
   }

   // Note that the ImageApi methods use the "@SelectJson" annotation to unwrap the object inside the "image" key
   // We use this container to deserialize the Image object to simulate that behavior and use a *real* json
   // in the tests.
   public static class ImageContainer {
      public Image image;
   }
}
