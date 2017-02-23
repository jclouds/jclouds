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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ManagedDiskParameters {

   public enum StorageAccountTypes {
      /** Enum value Standard_LRS. */
      STANDARD_LRS("Standard_LRS"),

      /** Enum value Premium_LRS. */
      PREMIUM_LRS("Premium_LRS");

      /** The actual serialized value for a StorageAccountTypes instance. */
      private String value;

      StorageAccountTypes(String value) {
         this.value = value;
      }

      public static StorageAccountTypes fromString(String value) {
         StorageAccountTypes[] items = StorageAccountTypes.values();
         for (StorageAccountTypes item : items) {
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

   @Nullable public abstract String id();

   public abstract StorageAccountTypes storageAccountType();

   @SerializedNames({"id", "storageAccountType"})
   public static ManagedDiskParameters create(final String id, final String storageAccountType) {
      return new AutoValue_ManagedDiskParameters(id, StorageAccountTypes.fromString(storageAccountType));
   }
}
