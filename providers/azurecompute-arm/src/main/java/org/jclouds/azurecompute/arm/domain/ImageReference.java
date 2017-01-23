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
public abstract class ImageReference {

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

   @SerializedNames({"publisher", "offer", "sku", "version"})
   public static ImageReference create(final String publisher,
                                       final String offer,
                                       final String sku,
                                       final String version) {

      return builder()
              .publisher(publisher)
              .offer(offer)
              .sku(sku)
              .version(version)
              .build();
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_ImageReference.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder publisher(String publisher);

      public abstract Builder offer(String offer);

      public abstract Builder sku(String sku);

      public abstract Builder version(String version);

      public abstract ImageReference build();
   }
}
