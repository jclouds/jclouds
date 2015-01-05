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
package org.jclouds.openstack.swift.v1.features;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.jclouds.openstack.swift.v1.options.CreateContainerOptions;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.openstack.swift.v1.options.UpdateContainerOptions;
import org.jclouds.openstack.swift.v1.reference.SwiftHeaders;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Provides live tests for the {@link ContainerApi}.
 */
@Test(groups = "live", testName = "ContainerApiLiveTest")
public class ContainerApiLiveTest extends BaseSwiftApiLiveTest<SwiftApi> {

   private String name = getClass().getSimpleName();

   public void testCreateWithOptions() throws Exception {
      for (String regionId : regions) {
         ImmutableMultimap<String, String> headers =
               ImmutableMultimap.of(SwiftHeaders.STATIC_WEB_INDEX, "__index.html",
                                    SwiftHeaders.STATIC_WEB_ERROR, "__error.html");
         CreateContainerOptions opts = new CreateContainerOptions().headers(headers);

         assertNotNull(api.getContainerApi(regionId).create(name, opts));

         Container container = api.getContainerApi(regionId).get(name);
         assertNotNull(container);
         assertEquals(container.getName(), name);
         assertEquals(container.getMetadata().size(), 2);
         assertEquals(container.getMetadata().get("web-index"), "__index.html");
         assertEquals(container.getMetadata().get("web-error"), "__error.html");

         assertTrue(api.getContainerApi(regionId).deleteIfEmpty(name));
      }
   }

   public void testCreateWithSpacesAndSpecialCharacters() throws Exception {
      final String nameWithSpaces = "container # ! special";

      for (String regionId : regions) {
         assertTrue(api.getContainerApi(regionId).create(nameWithSpaces));
         Container container = api.getContainerApi(regionId).get(nameWithSpaces);
         assertNotNull(container);
         assertEquals(container.getName(), nameWithSpaces);

         assertTrue(api.getContainerApi(regionId).deleteIfEmpty(nameWithSpaces));
      }
   }

   public void testList() throws Exception {
      for (String regionId : regions) {
         ContainerApi containerApi = api.getContainerApi(regionId);
         FluentIterable<Container> response = containerApi.list();
         assertNotNull(response);
         for (Container container : response) {
            assertNotNull(container.getName());
            assertTrue(container.getObjectCount() >= 0);
            assertTrue(container.getBytesUsed() >= 0);
         }
      }
   }

   public void testListWithOptions() throws Exception {
      String lexicographicallyBeforeName = name.substring(0, name.length() - 1);
      for (String regionId : regions) {
         ListContainerOptions options = ListContainerOptions.Builder.marker(lexicographicallyBeforeName);
         Container container = api.getContainerApi(regionId).list(options).get(0);
         assertEquals(container.getName(), name);
         assertTrue(container.getObjectCount() == 0);
         assertTrue(container.getBytesUsed() == 0);
      }
   }

   public void testUpdate() throws Exception {
      for (String regionId : regions) {
         ImmutableMultimap<String, String> headers =
               ImmutableMultimap.of(SwiftHeaders.STATIC_WEB_INDEX, "__index.html",
                                    SwiftHeaders.STATIC_WEB_ERROR, "__error.html");
         UpdateContainerOptions opts = new UpdateContainerOptions().headers(headers);

         assertNotNull(api.getContainerApi(regionId).create(name));

         Container container = api.getContainerApi(regionId).get(name);
         assertNotNull(container);
         assertEquals(container.getName(), name);
         assertTrue(container.getMetadata().isEmpty());

         assertNotNull(api.getContainerApi(regionId).update(name, opts));

         Container updatedContainer = api.getContainerApi(regionId).get(name);
         assertNotNull(updatedContainer);
         assertEquals(updatedContainer.getName(), name);
         assertEquals(updatedContainer.getMetadata().size(), 2);
         assertEquals(updatedContainer.getMetadata().get("web-index"), "__index.html");
         assertEquals(updatedContainer.getMetadata().get("web-error"), "__error.html");

         assertTrue(api.getContainerApi(regionId).deleteIfEmpty(name));
      }
   }

   public void testUpdateContainer() throws Exception {
      for (String regionId : regions) {
         ContainerApi containerApi = api.getContainerApi(regionId);
         assertThat(containerApi.create(name)).isTrue();

         assertThat(containerApi.get(name).getAnybodyRead().get()).isFalse();

         assertThat(containerApi.update(name, new UpdateContainerOptions().anybodyRead())).isTrue();
         assertThat(containerApi.get(name).getAnybodyRead().get()).isTrue();

         assertThat(containerApi.deleteIfEmpty(name)).isTrue();
      }
   }

   public void testGet() throws Exception {
      for (String regionId : regions) {
         Container container = api.getContainerApi(regionId).get(name);
         assertEquals(container.getName(), name);
         assertTrue(container.getObjectCount() == 0);
         assertTrue(container.getBytesUsed() == 0);
      }
   }

   public void testUpdateMetadata() throws Exception {
      Map<String, String> meta = ImmutableMap.of("MyAdd1", "foo", "MyAdd2", "bar");

      for (String regionId : regions) {
         ContainerApi containerApi = api.getContainerApi(regionId);
         assertTrue(containerApi.updateMetadata(name, meta));
         containerHasMetadata(containerApi, name, meta);
      }
   }

   public void testDeleteMetadata() throws Exception {
      Map<String, String> meta = ImmutableMap.of("MyDelete1", "foo", "MyDelete2", "bar");

      for (String regionId : regions) {
         ContainerApi containerApi = api.getContainerApi(regionId);
         // update
         assertTrue(containerApi.updateMetadata(name, meta));
         containerHasMetadata(containerApi, name, meta);
         // delete
         assertTrue(containerApi.deleteMetadata(name, meta));
         Container container = containerApi.get(name);
         for (Entry<String, String> entry : meta.entrySet()) {
            // note keys are returned in lower-case!
            assertFalse(container.getMetadata().containsKey(entry.getKey().toLowerCase()));
         }
      }
   }

   static void containerHasMetadata(ContainerApi containerApi, String name, Map<String, String> meta) {
      Container container = containerApi.get(name);
      for (Entry<String, String> entry : meta.entrySet()) {
         // note keys are returned in lower-case!
         assertEquals(container.getMetadata().get(entry.getKey().toLowerCase()), entry.getValue(),
               container + " didn't have metadata: " + entry);
      }
   }

   @Override
   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      for (String regionId : regions) {
         api.getContainerApi(regionId).create(name);
      }
   }

   @Override
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : regions) {
         api.getContainerApi(regionId).deleteIfEmpty(name);
      }
      super.tearDown();
   }
}
