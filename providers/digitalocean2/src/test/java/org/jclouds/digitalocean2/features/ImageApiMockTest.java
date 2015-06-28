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
package org.jclouds.digitalocean2.features;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.digitalocean2.domain.options.ImageListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.digitalocean2.domain.Image;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiMockTest;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

@Test(groups = "unit", testName = "ImageApiMockTest", singleThreaded = true)
public class ImageApiMockTest extends BaseDigitalOcean2ApiMockTest {

   public void testListImages() throws InterruptedException {
      server.enqueue(jsonResponse("/images-first.json"));
      server.enqueue(jsonResponse("/images-last.json"));

      Iterable<Image> images = api.imageApi().list().concat();

      assertEquals(size(images), 10); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/images");
      assertSent(server, "GET", "/images?page=2&per_page=5&type=distribution");
   }

   public void testListImagesReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Image> images = api.imageApi().list().concat();

      assertTrue(isEmpty(images));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images");
   }

   public void testListImagesWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/images-first.json"));

      Iterable<Image> images = api.imageApi().list(page(1).perPage(5).type("distribution"));

      assertEquals(size(images), 5);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/images?page=1&per_page=5&type=distribution");
   }

   public void testListImagesWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Image> images = api.imageApi().list(page(1).perPage(5).type("distribution"));

      assertTrue(isEmpty(images));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images?page=1&per_page=5&type=distribution");
   }
   
   public void testGetImage() throws InterruptedException {
      server.enqueue(jsonResponse("/image.json"));

      Image image = api.imageApi().get(1);

      assertEquals(image, imageFromResource("/image.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images/1");
   }

   public void testGetImageReturns404() throws InterruptedException {
      server.enqueue(response404());

      Image image = api.imageApi().get(1);

      assertNull(image);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images/1");
   }
   
   public void testGetImageUsingSlug() throws InterruptedException {
      server.enqueue(jsonResponse("/image.json"));

      Image image = api.imageApi().get("foo");

      assertEquals(image, imageFromResource("/image.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images/foo");
   }

   public void testGetImageUsingSlugReturns404() throws InterruptedException {
      server.enqueue(response404());

      Image image = api.imageApi().get("foo");

      assertNull(image);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/images/foo");
   }
   
   public void testDeleteImage() throws InterruptedException {
      server.enqueue(response204());

      api.imageApi().delete(1);
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/images/1");
   }

   public void testDeleteImageReturns404() throws InterruptedException {
      server.enqueue(response404());

      api.imageApi().delete(1);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/images/1");
   }
   
   private Image imageFromResource(String resource) {
      return onlyObjectFromResource(resource, new TypeToken<Map<String, Image>>() {
         private static final long serialVersionUID = 1L;
      }); 
   }
}
