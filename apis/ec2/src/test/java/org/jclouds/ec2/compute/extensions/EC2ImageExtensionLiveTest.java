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
package org.jclouds.ec2.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.internal.BaseImageExtensionLiveTest;
import org.jclouds.compute.suppliers.ImageCacheSupplier;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Live test for ec2 {@link ImageExtension} implementation
 */
@Test(groups = "live", singleThreaded = true, testName = "EC2ImageExtensionLiveTest")
public class EC2ImageExtensionLiveTest extends BaseImageExtensionLiveTest {
   protected TemplateBuilderSpec ebsTemplate;

   public EC2ImageExtensionLiveTest() {
      provider = "ec2";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      String ebsSpec = checkNotNull(setIfTestSystemPropertyPresent(overrides, provider + ".ebs-template"), provider
            + ".ebs-template");
      ebsTemplate = TemplateBuilderSpec.parse(ebsSpec);
      return overrides;
   }

   // Getting an image from the cache is tricky in EC2, as images are filtered
   // by default by owner, and the image being created will be owned by the
   // current user. If the cache needs to refresh the list, the just added image
   // will be removed as the image list will be refreshed (potentially) without
   // taking into account the current user owner id. Instead of using the
   // TempalteBuilder, just inspect the ImageCacheSupplier directly
   @SuppressWarnings("unchecked")
   @Override
   protected Optional<Image> findImageWithNameInCache(String name) {
      ImageCacheSupplier imageCache = (ImageCacheSupplier) context.utils().injector()
            .getInstance(Key.get(new TypeLiteral<Supplier<Set<? extends Image>>>() {
            }, Memoized.class));

      try {
         Field field = imageCache.getClass().getDeclaredField("imageCache");
         field.setAccessible(true);
         LoadingCache<String, Image> cache = (LoadingCache<String, Image>) field.get(imageCache);

         return Optional.fromNullable(getOnlyElement(filter(cache.asMap().values(), new Predicate<Image>() {
            @Override
            public boolean apply(Image input) {
               return imageGroup.equals(input.getName());
            }
         }), null));
      } catch (NoSuchFieldException ex) {
         throw Throwables.propagate(ex);
      } catch (IllegalAccessException ex) {
         throw Throwables.propagate(ex);
      }
   }

   @Override
   public Template getNodeTemplate() {
      return view.getComputeService().templateBuilder().from(ebsTemplate).build();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}
