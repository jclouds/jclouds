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

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.config.ComputeServiceAdapterContextModule.AddDefaultCredentialsToImage;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.suppliers.ImageCacheSupplier;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.assistedinject.Assisted;

/**
 * Delegates to the provider specific {@link ImageExtension} and takes care of
 * propagating the changes made to the images to the image cache.
 */
@Beta
public class DelegatingImageExtension implements ImageExtension {

   public interface Factory {
      DelegatingImageExtension create(ImageCacheSupplier imageCache, ImageExtension delegate);
   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ImageCacheSupplier imageCache;
   private final ImageExtension delegate;
   private final AddDefaultCredentialsToImage addDefaultCredentialsToImage;
   private final Map<String, Credentials> credentialStore;

   @Inject
   DelegatingImageExtension(@Assisted ImageCacheSupplier imageCache, @Assisted ImageExtension delegate,
         AddDefaultCredentialsToImage addDefaultCredentialsToImage, Map<String, Credentials> credentialStore) {
      this.imageCache = imageCache;
      this.delegate = delegate;
      this.addDefaultCredentialsToImage = addDefaultCredentialsToImage;
      this.credentialStore = credentialStore;
   }

   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      return delegate.buildImageTemplateFromNode(name, id);
   }

   public ListenableFuture<Image> createImage(final ImageTemplate template) {
      ListenableFuture<Image> future = delegate.createImage(template);

      // Populate the default image credentials, if missing
      future = Futures.transform(future, new Function<Image, Image>() {
         @Override
         public Image apply(Image input) {
            if (input.getDefaultCredentials() != null) {
               return input;
            }

            // If the image has been created by cloning a node, then try to
            // populate the known node credentials as the default image
            // credentials
            if (template instanceof CloneImageTemplate) {
               final CloneImageTemplate cloneImageTemplate = (CloneImageTemplate) template;

               Credentials nodeCredentials = credentialStore.get("node#" + cloneImageTemplate.getSourceNodeId());
               if (nodeCredentials != null) {
                  logger.info(">> Adding node(%s) credentials to image(%s)...", cloneImageTemplate.getSourceNodeId(),
                        cloneImageTemplate.getName());
                  return ImageBuilder.fromImage(input)
                        .defaultCredentials(LoginCredentials.fromCredentials(nodeCredentials)).build();
               }
            }

            // If no credentials are known for the node, populate the default
            // credentials using the defined strategy
            logger.info(">> Adding default image credentials to image(%s)...", template.getName());
            return addDefaultCredentialsToImage.apply(input);
         }
      });

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
