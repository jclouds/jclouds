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

import org.jclouds.profitbricks.domain.internal.Provisionable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Image implements Provisionable {

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

   public abstract Type type();

   public abstract boolean isPublic();

   public abstract boolean isWriteable();

   public abstract boolean isBootable();

   public static Image create(String id, String name, float size, Type type, Location location, OsType osType,
           boolean isPublic, Boolean isWriteable, Boolean isBootable, Boolean cpuHotPlug, Boolean cpuHotUnPlug,
           Boolean ramHotPlug, Boolean ramHotUnPlug, Boolean nicHotPlug, Boolean nicHotUnPlug,
           Boolean discVirtioHotPlug, Boolean discVirtioHotUnPlug) {
      return new AutoValue_Image(cpuHotPlug, cpuHotUnPlug, ramHotPlug, ramHotUnPlug, nicHotPlug, nicHotUnPlug,
              discVirtioHotPlug, discVirtioHotUnPlug, id, name, size, location, osType, type, isPublic, isWriteable,
              isBootable);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromImage(this);
   }

   public static class Builder extends Provisionable.Builder<Builder, Image> {

      private Type type;
      private boolean isPublic;
      private boolean isWriteable;
      private boolean isBootable;

      public Builder type(Type type) {
         this.type = type;
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

      @Override
      public Image build() {
         return Image.create(id, name, size, type, location, osType, isPublic, isWriteable, isBootable, cpuHotPlug, cpuHotUnPlug,
                 ramHotPlug, ramHotUnPlug, nicHotPlug, nicHotUnPlug, discVirtioHotPlug, discVirtioHotUnPlug);
      }

      public Builder fromImage(Image in) {
         return this.id(in.id()).isBootable(in.isBootable()).isCpuHotPlug(in.isCpuHotPlug()).isCpuHotUnPlug(in.isCpuHotUnPlug())
                 .isDiscVirtioHotPlug(in.isDiscVirtioHotPlug()).isDiscVirtioHotUnPlug(in.isDiscVirtioHotUnPlug())
                 .isNicHotPlug(in.isNicHotPlug()).isNicHotUnPlug(in.isNicHotUnPlug()).isPublic(in.isPublic())
                 .isRamHotPlug(in.isRamHotPlug()).isRamHotUnPlug(in.isRamHotUnPlug()).isWriteable(in.isWriteable())
                 .location(in.location()).name(in.name()).osType(in.osType()).size(in.size()).type(in.type());
      }

      @Override
      public Builder self() {
         return this;
      }

   }

}
