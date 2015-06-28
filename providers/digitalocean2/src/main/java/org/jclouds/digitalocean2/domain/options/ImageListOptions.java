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
package org.jclouds.digitalocean2.domain.options;

/**
 * Custom options to filter the list of images.
 */
public class ImageListOptions extends ListOptions {
   public static final String TYPE_PARAM = "type";
   public static final String PRIVATE_PARAM = "private";
   
   /**
    * Configures the type of the images to be retrieved.
    */
   public ImageListOptions type(String type) {
      queryParameters.put(TYPE_PARAM, type);
      return this;
   }
   
   /**
    * Get the images of the current user.
    */
   public ImageListOptions privateImages(boolean privateImages) {
      queryParameters.put(PRIVATE_PARAM, String.valueOf(privateImages));
      return this;
   }
   
   @Override public ImageListOptions perPage(int perPage) {
      super.perPage(perPage);
      return this;
   }

   @Override public ImageListOptions page(int page) {
      super.page(page);
      return this;
   }
   
   public static final class Builder {
      
      /**
       * @see {@link ImageListOptions#type(String)}
       */
      public static ImageListOptions type(String type) {
         return new ImageListOptions().type(type);
      }
      
      /**
       * @see {@link ImageListOptions#privateImages(boolean)}
       */
      public static ImageListOptions privateImages(boolean privateImages) {
         return new ImageListOptions().privateImages(privateImages);
      }
      /**
       * @see {@link ImageListOptions#page(int)}
       */
      public static ImageListOptions page(int page) {
         return new ImageListOptions().page(page);
      }
   }
}
