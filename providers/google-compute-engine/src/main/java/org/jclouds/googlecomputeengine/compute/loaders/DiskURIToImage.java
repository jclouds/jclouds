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
package org.jclouds.googlecomputeengine.compute.loaders;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import org.jclouds.googlecomputeengine.compute.functions.Resources;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.logging.Logger;


@Singleton
public class DiskURIToImage extends CacheLoader<URI, Optional<Image>> {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Resources resources;

   @Inject
   DiskURIToImage(Resources resources) {
      this.resources = resources;
   }

   @Override
   public Optional<Image> load(URI key) throws ExecutionException {
      try {
         Disk disk = resources.disk(key);
         URI sourceImage = disk.sourceImage();
         Image image = sourceImage != null ? resources.image(sourceImage) : null;
         return Optional.fromNullable(image);
      } catch (Exception e) {
         throw new ExecutionException(message(key, e), e);
      }
   }

   public static String message(URI key, Exception e) {
      return String.format("could not find image for disk %s: %s", key.toString(), e.getMessage());
   }
}
