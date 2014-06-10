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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Image supplier that allows new images to be registered.
 * <p>
 * This is a wrapper for the memoized image supplier (the actual image cache), to provide a way to register new images as
 * needed. Once a new image is created by the {@link org.jclouds.compute.extensions.ImageExtension}, or discovered by
 * other means (see https://issues.apache.org/jira/browse/JCLOUDS-570) this supplier will allow the image to be appended
 * to the cached list, so it can be properly used normally.
 */
@Singleton
public class ImageCacheSupplier implements Supplier<Set<? extends Image>> {

   private final Supplier<Set<? extends Image>> imageCache;

   private final Cache<String, Image> uncachedImages;

   @Inject
   public ImageCacheSupplier(@Named("imageCache") Supplier<Set<? extends Image>> imageCache,
         @Named(PROPERTY_SESSION_INTERVAL) long sessionIntervalSeconds) {
      this.imageCache = checkNotNull(imageCache, "imageCache");
      // We use a cache to let the entries in the "uncached" set expire as soon as the image cache expires. We want the
      // uncached set to be regenerated when the original cache is also regenerated.
      this.uncachedImages = CacheBuilder.newBuilder().expireAfterWrite(sessionIntervalSeconds, TimeUnit.SECONDS)
            .build();
   }

   @Override
   public Set<? extends Image> get() {
      return ImmutableSet.copyOf(concat(imageCache.get(), uncachedImages.asMap().values()));
   }

   /**
    * Registers a new image in the image cache.
    * <p>
    * This method should be called to register new images into the image cache, when some image that is known to exist
    * in the provider is still not cached. For example, this can happen when an image is created after the image cache
    * has been populated for the first time.
    * <p>
    * Note that this method does not check if the image is already cached, to avoid loading all images if the image
    * cache is still not populated.
    *
    * @param image The image to be registered to the cache.
    */
   public void registerImage(Image image) {
      checkNotNull(image, "image");
      uncachedImages.put(image.getId(), image);
   }

}
