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

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

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

   @SerializedNames({ "publisher", "offer", "sku", "version", "location"})
   public static VMImage create(String publisher, String offer, String sku, String version, String location) {

      return new AutoValue_VMImage(publisher, offer, sku, version, location, false, null, null, null, null, null, false);
   }

   @SerializedNames({ "group", "storage", "vhd1", "vhd2", "name", "offer", "location"})
   public static VMImage create(String group, String storage, String vhd1, String vhd2, String name, String offer, String location) {

      return new AutoValue_VMImage(null, offer, null, null, location, false, group, storage, vhd1, vhd2, name, true);
   }
}
