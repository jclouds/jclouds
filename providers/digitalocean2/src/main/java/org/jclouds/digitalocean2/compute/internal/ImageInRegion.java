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
package org.jclouds.digitalocean2.compute.internal;

import org.jclouds.digitalocean2.domain.Image;

import com.google.auto.value.AutoValue;

/**
 * Scopes an image to a particular region.
 */
@AutoValue
public abstract class ImageInRegion {

   public abstract Image image();
   public abstract String region();

   public static ImageInRegion create(Image image, String region) {
      return new AutoValue_ImageInRegion(image, region);
   }

   public static String encodeId(ImageInRegion imageInRegion) {
      // Private images don't have a slug
      return String.format("%s/%s", imageInRegion.region(), slugOrId(imageInRegion.image()));
   }

   public static String extractRegion(String imageId) {
      return imageId.substring(0, imageId.indexOf('/'));
   }

   public static String extractImageId(String imageId) {
      return imageId.substring(imageId.indexOf('/') + 1);
   }

   private static String slugOrId(Image image) {
      return image.slug() != null ? image.slug() : String.valueOf(image.id());
   }

   ImageInRegion() { }
}
