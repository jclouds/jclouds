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

import com.google.common.base.Enums;

public enum Location {

   DE_FKB("de/fkb", "Germany, Karlsruhe"),
   DE_FRA("de/fra", "Germany, Frankfurt (M)"),
   US_LAS("us/las", "USA, Las Vegas"),
   US_LASDEV("us/lasdev", "USA Developer cluster"),
   UNRECOGNIZED("unrecognized", "Unrecognized location");

   private final String id;
   private final String description;

   Location(String id, String description) {
      this.id = id;
      this.description = description;
   }

   public String getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public static Location fromValue(String v) {
      return Enums.getIfPresent(Location.class, v).or(UNRECOGNIZED);
   }

   public static Location fromId(String id) {
      for (Location location : values())
         if (location.id.equals(id))
            return location;
      return UNRECOGNIZED;
   }

   @Override
   public String toString() {
      return id;
   }
}
