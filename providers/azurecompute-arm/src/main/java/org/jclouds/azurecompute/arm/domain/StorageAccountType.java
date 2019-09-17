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
package org.jclouds.azurecompute.arm.domain;

public enum StorageAccountType {
   /** Enum value Standard HDD. */
   STANDARD_LRS("Standard_LRS"),

   /** Enum value Standard SSD. */
   STANDARD_SSD_LRS("StandardSSD_LRS"),

   /** Enum value Premium SSD. */
   PREMIUM_LRS("Premium_LRS"),

   /** Enum value Ultra SSD (Available only if your subscription is enabled for ultra disks). */
   ULTRA_SSD_LRS("UltraSSD_LRS");

   /** The actual serialized value for a StorageAccountTypes instance. */
   private String value;

   StorageAccountType(String value) {
      this.value = value;
   }

   public static StorageAccountType fromString(String value) {
      StorageAccountType[] items = StorageAccountType.values();
      for (StorageAccountType item : items) {
         if (item.toString().equalsIgnoreCase(value)) {
            return item;
         }
      }
      return null;
   }

   @Override
   public String toString() {
      return this.value;
   }
}
