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

import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.ConstructorProperties;

import com.google.common.base.Objects;

/**
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item_Price"
 */
public class ProductItemPrice {

   private final int id;
   private final float hourlyRecurringFee;
   private final String recurringFee;
   private final ProductItem item;

   @ConstructorProperties({"id", "hourlyRecurringFee", "recurringFee", "item"})
   public ProductItemPrice(int id, float hourlyRecurringFee, String recurringFee, ProductItem item) {
      this.id = id;
      this.hourlyRecurringFee = hourlyRecurringFee;
      this.recurringFee = checkNotNull(recurringFee, "recurringFee");
      this.item = checkNotNull(item, "item");
   }

   public int getId() {
      return id;
   }

   public float getHourlyRecurringFee() {
      return hourlyRecurringFee;
   }

   public String getRecurringFee() {
      return recurringFee;
   }

   public ProductItem getItem() {
      return item;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProductItemPrice that = (ProductItemPrice) o;

      return Objects.equal(this.id, that.id) &&
              Objects.equal(this.hourlyRecurringFee, that.hourlyRecurringFee) &&
              Objects.equal(this.recurringFee, that.recurringFee) &&
              Objects.equal(this.item, that.item);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, hourlyRecurringFee, recurringFee, item);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("id", id)
              .add("hourlyRecurringFee", hourlyRecurringFee)
              .add("recurringFee", recurringFee)
              .add("item", item)
              .toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromProductItemPrice(this);
   }

   public static class Builder {
      private int id;
      private float hourlyRecurringFee;
      private String recurringFee;
      private ProductItem item;

      /**
       * @see ProductItemPrice#getId()
       */
      public Builder id(int id) {
         this.id = id;
         return this;
      }

      /**
       * @see ProductItemPrice#getHourlyRecurringFee()
       */
      public Builder hourlyRecurringFee(float hourlyRecurringFee) {
         this.hourlyRecurringFee = hourlyRecurringFee;
         return this;
      }

      /**
       * @see ProductItemPrice#getRecurringFee()
       */
      public Builder recurringFee(String recurringFee) {
         this.recurringFee = recurringFee;
         return this;
      }

      /**
       * @see org.jclouds.softlayer.domain.ProductItemPrice#getItem()
       */
      public Builder item(ProductItem item) {
         this.item = item;
         return this;
      }

      public ProductItemPrice build() {
         return new ProductItemPrice(id, hourlyRecurringFee, recurringFee, item);
      }

      public Builder fromProductItemPrice(ProductItemPrice in) {
         return this
                 .id(in.getId())
                 .hourlyRecurringFee(in.getHourlyRecurringFee())
                 .recurringFee(in.getRecurringFee())
                 .item(in.getItem());
      }
   }
}
