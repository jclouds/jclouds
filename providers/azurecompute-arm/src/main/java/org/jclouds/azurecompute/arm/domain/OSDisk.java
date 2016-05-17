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
public abstract class OSDisk {
   /**
    * The OS type of the os disk
    */
   @Nullable
   public abstract String osType();

   /**
    * The name of the os disk
    */
   @Nullable
   public abstract String name();

   /**
    * The vhd of the os disk
    */
   @Nullable
   public abstract VHD vhd();

   /**
    * The caching mode of the os disk
    */
   @Nullable
   public abstract String caching();

   /**
    * The create options of the os disk
    */
   @Nullable
   public abstract String createOption();

   /**
    * The url of the custom image
    */
   @Nullable
   public abstract VHD image();

   @SerializedNames({"osType", "name", "vhd", "caching", "createOption", "image"})
   public static OSDisk create(final String osType, final String name, final VHD vhd,
                               final String caching, final String createOption, final VHD image) {
      return builder()
            .osType(osType)
            .name(name)
            .vhd(vhd)
            .caching(caching)
            .createOption(createOption)
            .image(image)
            .build();
   }

   public static Builder builder() {
      return new AutoValue_OSDisk.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder osType(String osType);
      public abstract Builder name(String name);
      public abstract Builder caching(String caching);
      public abstract Builder createOption(String createOption);
      public abstract Builder vhd(VHD vhd);
      public abstract Builder image(VHD image);
      public abstract OSDisk build();
   }
}
