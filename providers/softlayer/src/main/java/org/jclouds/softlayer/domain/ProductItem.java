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

/**
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item"
 */
public class ProductItem {

   private final int id;
   private final String description;
   private final String softwareDescriptionId;
   private final SoftwareDescription softwareDescription;

   @ConstructorProperties({"id", "description", "softwareDescriptionId", "softwareDescription"})
   public ProductItem(int id, @Nullable String description, @Nullable String softwareDescriptionId,
                      @Nullable SoftwareDescription softwareDescription) {
      this.id = id;
      this.description = description;
      this.softwareDescriptionId = softwareDescriptionId;
      this.softwareDescription = softwareDescription;
   }

   public int getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public String getSoftwareDescriptionId() {
      return softwareDescriptionId;
   }

   public SoftwareDescription getSoftwareDescription() {
      return softwareDescription;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProductItem that = (ProductItem) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.description, that.description) &&
              Objects.equal(this.softwareDescriptionId, that.softwareDescriptionId) &&
              Objects.equal(this.softwareDescription, that.softwareDescription);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, description, softwareDescriptionId, softwareDescription);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("description", description)
              .add("softwareDescriptionId", softwareDescriptionId)
              .add("softwareDescription", softwareDescription)
              .toString();
   }
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromProductItem(this);
   }

   public static class Builder {
      private int id;
      private String description;
      private String softwareDescriptionId;
      private SoftwareDescription softwareDescription;

      /**
       * @see ProductItem#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.ProductItem#getDescription() ()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.ProductItem#getSoftwareDescriptionId() ()
       */
      public Builder softwareDescriptionId(String softwareDescriptionId) {
         this.softwareDescriptionId = softwareDescriptionId;
         return this;
      }

      /**
       * @see ProductItem#getSoftwareDescription()
       */
      public Builder softwareDescription(SoftwareDescription softwareDescription) {
         this.softwareDescription = softwareDescription;
         return this;
      }

      public ProductItem build() {
         return new ProductItem(id, description, softwareDescriptionId, softwareDescription);
      }

      public Builder fromProductItem(ProductItem in) {
         return this
                 .id(in.getId())
                 .description(in.getDescription())
                 .softwareDescriptionId(in.getSoftwareDescriptionId())
                 .softwareDescription(in.getSoftwareDescription());
      }
   }

}
