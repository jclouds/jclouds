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

import static org.jclouds.digitalocean2.domain.options.ImageListOptions.Builder.page;
import static org.jclouds.digitalocean2.domain.options.ImageListOptions.Builder.type;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.digitalocean2.domain.Image;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

@Test(groups = "live", testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseDigitalOcean2ApiLiveTest {

   public void testListImages() {
      final AtomicInteger found = new AtomicInteger(0);
      // DigitalOcean return 25 records per page by default. Inspect at most 2 pages
      assertTrue(api().list().concat().limit(50).allMatch(new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.name());
         }
      }), "All images must have the 'name' field populated");
      assertTrue(found.get() > 0, "Expected some images to be returned");
   }
   
   public void testListImagesOnePage() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(api().list(page(1).perPage(5)).allMatch(new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.name());
         }
      }), "All images must have the 'name' field populated");
      assertTrue(found.get() > 0, "Expected some images to be returned");
   }
   
   public void testListImagesByType() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(api().list(type("distribution").perPage(5)).allMatch(new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.distribution());
         }
      }), "All images must have the 'distribution' field populated");
      assertTrue(found.get() > 0, "Expected some images to be returned");
   }
   
   public void testGetImage() {
      Optional<Image> first = api().list().concat().first();
      assertTrue(first.isPresent(), "At least one image was expected to exist");
      assertNotNull(api().get(first.get().id()));
   }
   
   public void testGetImageBySlug() {
      Optional<Image> first = api().list().concat().firstMatch(new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            return !isNullOrEmpty(input.slug());
         }
      });
      
      assertTrue(first.isPresent(), "At least one image with the 'slug' field set was expected to exist");
      assertNotNull(api().get(first.get().slug()));
   }
   
   // TODO: Delete live test once the create/transfer operations are implemented
   
   private ImageApi api() {
      return api.imageApi();
   }
}
