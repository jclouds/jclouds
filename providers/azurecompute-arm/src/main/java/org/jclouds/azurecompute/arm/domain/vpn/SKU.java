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
package org.jclouds.azurecompute.arm.domain.vpn;

import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SKU {

   public static enum SKUName {
      Basic, HighPerformance, Standard, UltraPerformance, VpnGw1, VpnGw2, VpnGw3, Unrecognized;

      public static SKUName fromValue(final String text) {
         return (SKUName) GetEnumValue.fromValueOrDefault(text, SKUName.Unrecognized);
      }
   }

   public static enum SKUTier {
      Basic, HighPerformance, Standard, UltraPerformance, VpnGw1, VpnGw2, VpnGw3, Unrecognized;

      public static SKUTier fromValue(final String text) {
         return (SKUTier) GetEnumValue.fromValueOrDefault(text, SKUTier.Unrecognized);
      }
   }

   public abstract int capacity();
   public abstract SKUName name();
   public abstract SKUTier tier();

   SKU() {

   }

   @SerializedNames({ "capacity", "name", "tier" })
   public static SKU create(int capacity, SKUName name, SKUTier tier) {
      return new AutoValue_SKU(capacity, name, tier);
   }
}
