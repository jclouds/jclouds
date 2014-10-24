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
package org.jclouds.elasticstack.domain;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

public class WellKnownImage {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String loginUser;
      private String uuid;
      private String description;
      private OsFamily osFamily;
      private String osVersion;
      private int size;
      private boolean is64bit;

      public Builder loginUser(String loginUser) {
         this.loginUser = loginUser;
         return this;
      }

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder osFamily(OsFamily osFamily) {
         this.osFamily = osFamily;
         return this;
      }

      public Builder osVersion(String osVersion) {
         this.osVersion = osVersion;
         return this;
      }

      public Builder size(int size) {
         this.size = size;
         return this;
      }

      public Builder is64bit(boolean is64bit) {
         this.is64bit = is64bit;
         return this;
      }

      public WellKnownImage build() {
         return new WellKnownImage(loginUser, uuid, description, osFamily, osVersion, size, is64bit);
      }
   }

   public static final String DEFAULT_USER = "toor";

   private final String loginUser;
   private final String uuid;
   private final String description;
   private final OsFamily osFamily;
   private final String osVersion;
   private final int size;
   private final boolean is64bit;

   public WellKnownImage(@Nullable String loginUser, String uuid, String description, OsFamily osFamily,
         @Nullable String osVersion, int size, @Nullable Boolean is64bit) {
      this.loginUser = firstNonNull(loginUser, DEFAULT_USER);
      this.uuid = checkNotNull(uuid, "uuid cannot be null");
      this.description = checkNotNull(description, "description cannot be null");
      this.osFamily = checkNotNull(osFamily, "osFamily cannot be null");
      this.osVersion = osVersion;
      this.size = size;
      this.is64bit = firstNonNull(is64bit, Boolean.TRUE);
   }

   public String getUuid() {
      return uuid;
   }

   public String getDescription() {
      return description;
   }

   public OsFamily getOsFamily() {
      return osFamily;
   }

   public String getOsVersion() {
      return osVersion;
   }

   public int getSize() {
      return size;
   }

   public boolean is64bit() {
      return is64bit;
   }

   public String getLoginUser() {
      return loginUser;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(uuid, description, osFamily, osVersion, size, is64bit, loginUser);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null)
         return false;
      if (!(obj instanceof WellKnownImage)){
         return false;
      }
      WellKnownImage that = (WellKnownImage) obj;
      return Objects.equal(this.loginUser, that.loginUser)
         && Objects.equal(this.uuid, that.uuid)
         && Objects.equal(this.description, that.description)
         && Objects.equal(this.osFamily, that.osFamily)
         && Objects.equal(this.osVersion, that.osVersion)
         && Objects.equal(this.size, that.size)
         && Objects.equal(this.is64bit, that.is64bit);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("uuid", uuid).add("description", description)
            .add("osFamily", osFamily).add("osVersion", osVersion).add("size", size).add("is64bit", is64bit)
            .add("loginUser", loginUser).toString();
   }

}
