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
package org.jclouds.azurecompute.arm.domain;

import org.jclouds.azurecompute.arm.domain.Version.VersionProperties;
import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class VMImage {
   /**
    * The publisher of the image reference.
    */
   @Nullable
   public abstract String publisher();

   /**
    * The offer of the image reference.
    */
   @Nullable
   public abstract String offer();

   /**
    * The sku of the image reference.
    */
   @Nullable
   public abstract String sku();

   /**
    * The version of the image reference.
    */
   @Nullable
   public abstract String version();

   /**
    * The location from where Image was fetched
    */
   @Nullable
   public abstract String location();

   /**
    * Specifies if this image is globally available
    */
   public abstract boolean globallyAvailable();

   /**
    * The group of the custom image
    */
   @Nullable
   public abstract String group();

   /**
    * The storage of the custom image.
    */
   @Nullable
   public abstract String storage();

   /**
    * The vhd1 of the custom image
    */
   @Nullable
   public abstract String vhd1();

   /**
    * The vhd2 of the custom image.
    */
   @Nullable
   public abstract String vhd2();

   /**
    * The name of the custom image template.
    */
   @Nullable
   public abstract String name();
   
   /**
    * True if custom image
    */
   public abstract boolean custom();

   /**
    * The id of the custom image template.
    */
   @Nullable
   public abstract String customImageId();

   /**
    * Extended version properties.
    */
   @Nullable
   public abstract VersionProperties versionProperties();
   
   public static Builder builder() {
      return new AutoValue_VMImage.Builder();
   }
   
   public static Builder azureImage() {
      return builder().globallyAvailable(false).custom(false);
   }
   
   public static Builder customImage() {
      return builder().globallyAvailable(false).custom(true);
   }
   
   VMImage() {
      
   }
   
   public abstract Builder toBuilder();
   
   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder customImageId(String id);
      public abstract Builder publisher(String published);
      public abstract Builder offer(String offer);
      public abstract Builder sku(String sku);
      public abstract Builder version(String version);
      public abstract Builder location(String location);
      public abstract Builder globallyAvailable(boolean globallyAvailable);
      public abstract Builder group(String group);
      public abstract Builder storage(String storage);
      public abstract Builder vhd1(String vhd1);
      public abstract Builder vhd2(String vhd2);
      public abstract Builder name(String name);
      public abstract Builder custom(boolean custom);
      public abstract Builder versionProperties(VersionProperties versionProperties);
      
      public abstract VMImage build();
   }
}
