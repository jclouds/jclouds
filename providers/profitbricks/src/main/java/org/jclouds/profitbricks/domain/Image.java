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

@AutoValue
public abstract class Image {

   public enum Type {

      HDD, CDROM, UNRECOGNIZED;

      public static Type fromValue(String v) {
         try {
            return valueOf(v);
         } catch (IllegalArgumentException ex) {
            return UNRECOGNIZED;
         }
      }
   }

   public abstract String id();

   public abstract String name();

   public abstract float size(); // MB

   public abstract Type type();

   public abstract Location location();

   public abstract OsType osType();

   public abstract boolean isPublic();

   public abstract boolean isWriteable();

   public abstract boolean isBootable();

   public abstract boolean isCpuHotPlug();

   public abstract boolean isCpuHotUnPlug();

   public abstract boolean isRamHotPlug();

   public abstract boolean isRamHotUnPlug();

   public abstract boolean isNicHotPlug();

   public abstract boolean isNicHotUnPlug();

   public abstract boolean isDiscVirtioHotPlug();

   public abstract boolean isDiscVirtioHotUnPlug();

   public static Image create(String id, String name, float size, Type type, Location location, OsType osType,
           boolean isPublic, boolean isWriteable, boolean isBootable, boolean cpuHotPlug, boolean cpuHotUnPlug,
           boolean ramHotPlug, boolean ramHotUnPlug, boolean nicHotPlug, boolean nicHotUnPlug,
           boolean discVirtioHotPlug, boolean discVirtioHotUnPlug) {
      return new AutoValue_Image(id, name, size, type, location, osType, isPublic, isWriteable,
              isBootable, cpuHotPlug, cpuHotUnPlug, ramHotPlug, ramHotUnPlug, nicHotPlug, nicHotUnPlug, discVirtioHotPlug, discVirtioHotUnPlug);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromImage(this);
   }

   public static class Builder {

      private String id;
      private String name;
      private float size;
      private Type type;
      private Location location;
      private OsType osType;
      private boolean isPublic;
      private boolean isWriteable;
      private boolean isBootable;
      private boolean cpuHotPlug;
      private boolean cpuHotUnPlug;
      private boolean ramHotPlug;
      private boolean ramHotUnPlug;
      private boolean nicHotPlug;
      private boolean nicHotUnPlug;
      private boolean discVirtioHotPlug;
      private boolean discVirtioHotUnPlug;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder size(float size) {
         this.size = size;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder osType(OsType osType) {
         this.osType = osType;
         return this;
      }

      public Builder location(Location location) {
         this.location = location;
         return this;
      }

      public Builder isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return this;
      }

      public Builder isWriteable(boolean isWriteable) {
         this.isWriteable = isWriteable;
         return this;
      }

      public Builder isBootable(boolean isBootable) {
         this.isBootable = isBootable;
         return this;
      }

      public Builder isCpuHotPlug(boolean cpuHotPlug) {
         this.cpuHotPlug = cpuHotPlug;
         return this;
      }

      public Builder isCpuHotUnPlug(boolean cpuHotUnPlug) {
         this.cpuHotUnPlug = cpuHotUnPlug;
         return this;
      }

      public Builder isRamHotPlug(boolean ramHotPlug) {
         this.ramHotPlug = ramHotPlug;
         return this;
      }

      public Builder isRamHotUnPlug(boolean ramHotUnPlug) {
         this.ramHotUnPlug = ramHotUnPlug;
         return this;
      }

      public Builder isNicHotPlug(boolean nicHotPlug) {
         this.nicHotPlug = nicHotPlug;
         return this;
      }

      public Builder isNicHotUnPlug(boolean nicHotUnPlug) {
         this.nicHotUnPlug = nicHotUnPlug;
         return this;
      }

      public Builder isDiscVirtioHotPlug(boolean discVirtioHotPlug) {
         this.discVirtioHotPlug = discVirtioHotPlug;
         return this;
      }

      public Builder isDiscVirtioHotUnPlug(boolean discVirtioHotUnPlug) {
         this.discVirtioHotUnPlug = discVirtioHotUnPlug;
         return this;
      }

      public Image build() {
         return Image.create(id, name, size, type, location, osType, isPublic, isWriteable, isBootable, cpuHotPlug, cpuHotUnPlug,
                 ramHotPlug, ramHotUnPlug, nicHotPlug, nicHotUnPlug, discVirtioHotPlug, discVirtioHotUnPlug);
      }

      public Builder fromImage(Image in) {
         return this.id(in.id()).isBootable(in.isBootable()).isCpuHotPlug(in.isCpuHotPlug()).isCpuHotUnPlug(in.isCpuHotUnPlug())
                 .isDiscVirtioHotPlug(in.isDiscVirtioHotPlug()).isDiscVirtioHotUnPlug(in.isDiscVirtioHotUnPlug())
                 .isNicHotPlug(in.isNicHotPlug()).isNicHotUnPlug(in.isNicHotUnPlug()).isPublic(in.isPublic())
                 .isRamHotPlug(in.isRamHotPlug()).isRamHotUnPlug(in.isRamHotUnPlug()).isWriteable(in.isWriteable())
                 .location(in.location()).name(in.name()).osType(in.osType()).size(in.size());
      }

   }

}
