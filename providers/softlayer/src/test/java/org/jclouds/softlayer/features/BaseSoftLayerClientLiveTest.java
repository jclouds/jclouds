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
package org.jclouds.softlayer.features;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.*;

/**
 * Tests behavior of {@code SoftLayerClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseSoftLayerClientLiveTest extends BaseApiLiveTest<SoftLayerClient> {

   public BaseSoftLayerClientLiveTest() {
      this.provider = "softlayer";
   }

   public void checkProductItem(ProductItem item) {
      assertTrue(item.getId() > 0, "item id must be more than 0");
      assertNotNull(item.getDescription(), "item description must be not null");
      checkCategories(item.getCategories());
      // units and capacity may be null
      assertFalse(item.getPrices().isEmpty());
      for (ProductItemPrice price : item.getPrices()) {
         checkPrice(price);
      }
   }

   public void checkCategories(Set<ProductItemCategory> categories) {
      for (ProductItemCategory category : categories) {
         assertTrue(category.getId() > 0, "category id must be more than 0");
         assertNotNull(category.getName(), "category name must be not null");
         assertNotNull(category.getCategoryCode(), "category code must be not null");
      }
   }

   public void checkPrice(ProductItemPrice price) {
      assertTrue(price.getId() > 0, "price id must be more than 0");
      assertTrue(price.getItemId() > 0, "price itemId must be more than 0");
      assertTrue(price.getRecurringFee() != null || price.getHourlyRecurringFee() != null,
              "price.getRecurringFee() must be not null OR price.getHourlyRecurringFee() must be not null");
   }

}
