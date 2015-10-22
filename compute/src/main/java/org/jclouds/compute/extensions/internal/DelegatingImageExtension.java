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
package org.jclouds.compute.extensions.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.suppliers.ImageCacheSupplier;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Delegates to the provider specific {@link ImageExtension} and takes care of
 * propagating the changes made to the images to the image cache.
 */
@Beta
public class DelegatingImageExtension implements ImageExtension {

   private final ImageCacheSupplier imageCache;
   private final ImageExtension delegate;

   public DelegatingImageExtension(ImageCacheSupplier imageCache, ImageExtension delegate) {
      this.imageCache = checkNotNull(imageCache, "imageCache");
      this.delegate = checkNotNull(delegate, "delegate");
   }

   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      return delegate.buildImageTemplateFromNode(name, id);
   }

   public ListenableFuture<Image> createImage(ImageTemplate template) {
      ListenableFuture<Image> future = delegate.createImage(template);
      Futures.addCallback(future, new FutureCallback<Image>() {
         @Override
         public void onSuccess(Image result) {
            imageCache.registerImage(result);
         }

         @Override
         public void onFailure(Throwable t) {

         }
      });
      return future;
   }

   public boolean deleteImage(String id) {
      boolean success = delegate.deleteImage(id);
      if (success) {
         imageCache.removeImage(id);
      }
      return success;
   }

}
