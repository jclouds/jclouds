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
package org.jclouds.softlayer.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

public class VirtualDiskImageSoftware {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVirtualDiskImageSoftware(this);
   }

   public static class Builder {

      protected int id;
      protected int softwareDescriptionId;
      protected SoftwareDescription softwareDescription;

      /**
       * @see VirtualDiskImageSoftware#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see VirtualDiskImageSoftware#getSoftwareDescriptionId()
       */
      public Builder softwareDescriptionId(int softwareDescriptionId) {
         this.softwareDescriptionId = softwareDescriptionId;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.VirtualDiskImageSoftware#getSoftwareDescription()
       */
      public Builder softwareDescription(SoftwareDescription softwareDescription) {
         this.softwareDescription = softwareDescription;
         return this;
      }

      public VirtualDiskImageSoftware build() {
         return new VirtualDiskImageSoftware(id, softwareDescriptionId, softwareDescription);
      }

      public Builder fromVirtualDiskImageSoftware(VirtualDiskImageSoftware in) {
         return this
                 .id(in.getId())
                 .softwareDescriptionId(in.getSoftwareDescriptionId())
                 .softwareDescription(in.getSoftwareDescription());
      }
   }

   private final int id;
   private final int softwareDescriptionId;
   private final SoftwareDescription softwareDescription;

   @ConstructorProperties({"id", "softwareDescriptionId", "softwareDescription"})
   public VirtualDiskImageSoftware(int id, int softwareDescriptionId, @Nullable SoftwareDescription softwareDescription) {
      this.id = id;
      this.softwareDescriptionId = softwareDescriptionId;
      this.softwareDescription = softwareDescription;
   }

   public int getId() {
      return this.id;
   }

   public int getSoftwareDescriptionId() {
      return this.softwareDescriptionId;
   }

   @Nullable
   public SoftwareDescription getSoftwareDescription() {
      return this.softwareDescription;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, softwareDescriptionId, softwareDescription);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualDiskImageSoftware that = (VirtualDiskImageSoftware) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.softwareDescriptionId, that.softwareDescriptionId) &&
              Objects.equal(this.softwareDescription, that.softwareDescription);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("softwareDescriptionId", softwareDescriptionId)
              .add("softwareDescription", softwareDescription)
              .toString();
   }
}
