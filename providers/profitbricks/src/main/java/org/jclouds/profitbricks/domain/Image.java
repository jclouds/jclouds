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
package org.jclouds.profitbricks.domain;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;

import org.jclouds.javax.annotation.Nullable;

@AutoValue
public abstract class Image implements Provisionable {

   public enum Type {

      HDD, CDROM, UNRECOGNIZED;

      public static Type fromValue(String v) {
         return Enums.getIfPresent(Type.class, v).or(UNRECOGNIZED);
      }
   }

   public abstract String id();

   public abstract String name();

   public abstract float size(); // MB

   public abstract Location location();

   public abstract OsType osType();

   public abstract Type type();

   @Nullable
   public abstract Boolean isPublic();

   @Nullable
   public abstract Boolean isWriteable();

   @Nullable
   public abstract Boolean isBootable();

   @Nullable
   public abstract Boolean isCpuHotPlug();

   @Nullable
   public abstract Boolean isCpuHotUnPlug();

   @Nullable
   public abstract Boolean isRamHotPlug();

   @Nullable
   public abstract Boolean isRamHotUnPlug();

   @Nullable
   public abstract Boolean isNicHotPlug();

   @Nullable
   public abstract Boolean isNicHotUnPlug();

   @Nullable
   public abstract Boolean isDiscVirtioHotPlug();

   @Nullable
   public abstract Boolean isDiscVirtioHotUnPlug();

   public static Builder builder() {
      return new AutoValue_Image.Builder();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);

      public abstract Builder name(String name);

      public abstract Builder size(float size);

      public abstract Builder location(Location location);

      public abstract Builder osType(OsType osType);

      public abstract Builder type(Type type);

      public abstract Builder isPublic(Boolean isPublic);

      public abstract Builder isWriteable(Boolean isWriteable);

      public abstract Builder isBootable(Boolean isBootable);

      public abstract Builder isCpuHotPlug(Boolean isCpuHotPlug);

      public abstract Builder isCpuHotUnPlug(Boolean isCpuHotUnPlug);

      public abstract Builder isRamHotPlug(Boolean isRamHotPlug);

      public abstract Builder isRamHotUnPlug(Boolean isRamHotUnPlug);

      public abstract Builder isNicHotPlug(Boolean isNicHotPlug);

      public abstract Builder isNicHotUnPlug(Boolean isNicHotUnPlug);

      public abstract Builder isDiscVirtioHotPlug(Boolean isDiscVirtioHotPlug);

      public abstract Builder isDiscVirtioHotUnPlug(Boolean isDiscVirtioHotUnPlug);

      public abstract Image build();

   }

}
