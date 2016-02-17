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
package org.jclouds.compute.suppliers;

import static com.google.common.collect.Iterables.any;
import static org.jclouds.compute.predicates.ImagePredicates.idEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.strategy.GetImageStrategy;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.util.Providers;

/**
 * Unit tests for the {@link ImageCacheSupplier} class.
 */
@Test(groups = "unit", testName = "ImageCacheSupplierTest")
public class ImageCacheSupplierTest {

   private Location location = new LocationBuilder().scope(LocationScope.PROVIDER).id("location")
         .description("location").build();

   private OperatingSystem os = OperatingSystem.builder().name("osName").version("osVersion")
         .description("osDescription").arch("X86_32").build();

   private Image image = new ImageBuilder().id("imageId").providerId("imageId").name("imageName")
         .description("imageDescription").version("imageVersion").operatingSystem(os).status(Image.Status.AVAILABLE)
         .location(location).build();

   private Set<? extends Image> images = ImmutableSet.of(image);

   private GetImageStrategy getImageStrategy = new GetImageStrategy() {
      @Override
      public Image getImage(String id) {
         return new ImageBuilder().id(id).providerId(id).name("imageName-" + id).description("imageDescription")
               .version("imageVersion").operatingSystem(os).status(Image.Status.AVAILABLE).location(location).build();
      }
   };

   @Test(expectedExceptions = NullPointerException.class)
   public void testRegisterNullImageIsNotAllowed() {
      ImageCacheSupplier imageCache = new ImageCacheSupplier(Suppliers.<Set<? extends Image>> ofInstance(images), 60,
            Atomics.<AuthorizationException> newReference(), Providers.of(getImageStrategy));
      imageCache.registerImage(null);
   }

   @Test
   public void testRegisterImageIgnoresDuplicates() {
      ImageCacheSupplier imageCache = new ImageCacheSupplier(Suppliers.<Set<? extends Image>> ofInstance(images), 60,
            Atomics.<AuthorizationException> newReference(), Providers.of(getImageStrategy));
      assertEquals(imageCache.get().size(), 1);

      imageCache.registerImage(image);

      assertEquals(imageCache.get().size(), 1);
   }

   @Test
   public void testRegisterNewImage() {
      ImageCacheSupplier imageCache = new ImageCacheSupplier(Suppliers.<Set<? extends Image>> ofInstance(images), 60,
            Atomics.<AuthorizationException> newReference(), Providers.of(getImageStrategy));
      assertEquals(imageCache.get().size(), 1);

      imageCache.registerImage(ImageBuilder.fromImage(image).id("newimage").build());

      assertEquals(imageCache.get().size(), 2);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testRemoveNullImageIsNotAllowed() {
      ImageCacheSupplier imageCache = new ImageCacheSupplier(Suppliers.<Set<? extends Image>> ofInstance(images), 60,
            Atomics.<AuthorizationException> newReference(), Providers.of(getImageStrategy));
      imageCache.removeImage(null);
   }

   @Test
   public void testRemoveImage() {
      ImageCacheSupplier imageCache = new ImageCacheSupplier(Suppliers.<Set<? extends Image>> ofInstance(images), 60,
            Atomics.<AuthorizationException> newReference(), Providers.of(getImageStrategy));
      assertEquals(imageCache.get().size(), 1);

      imageCache.removeImage(image.getId());

      assertEquals(imageCache.get().size(), 0);
   }

   @Test
   public void testLoadImage() {
      ImageCacheSupplier imageCache = new ImageCacheSupplier(Suppliers.<Set<? extends Image>> ofInstance(images), 60,
            Atomics.<AuthorizationException> newReference(), Providers.of(getImageStrategy));
      assertEquals(imageCache.get().size(), 1);

      Optional<? extends Image> image = imageCache.get("foo");

      assertTrue(image.isPresent());
      assertEquals(image.get().getName(), "imageName-foo");
      assertEquals(imageCache.get().size(), 2);
   }

   @Test
   public void testSupplierExpirationReloadsTheCache() {
      ImageCacheSupplier imageCache = new ImageCacheSupplier(Suppliers.<Set<? extends Image>> ofInstance(images), 3,
            Atomics.<AuthorizationException> newReference(), Providers.of(getImageStrategy));
      assertEquals(imageCache.get().size(), 1);

      Optional<? extends Image> image = imageCache.get("foo");

      // Load an image into the cache
      assertTrue(image.isPresent());
      assertEquals(image.get().getName(), "imageName-foo");
      assertEquals(imageCache.get().size(), 2);

      // Once the supplier expires, reloading it will load the initial values
      // (it is a hardcoded supplier), so the just loaded image should be gone
      Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
      assertEquals(imageCache.get().size(), 1);
      assertFalse(any(imageCache.get(), idEquals("foo")));
   }
}
