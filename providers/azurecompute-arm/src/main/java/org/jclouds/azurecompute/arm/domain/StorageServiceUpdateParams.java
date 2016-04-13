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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import java.util.Map;

@AutoValue
public abstract class StorageServiceUpdateParams {

   @AutoValue
   public abstract static class StorageServiceUpdateProperties {

       /**
        * Specifies whether the account supports locally-redundant storage, geo-redundant storage, zone-redundant
        * storage, or read access geo-redundant storage.
        * Note: This implementation is for version 2015-10-01 and earlier.
        * For version 2016-01-01 or later, refer to https://msdn.microsoft.com/en-us/library/mt163639.aspx
        */
       @Nullable
       public abstract StorageService.AccountType accountType();


      @SerializedNames({"accountType"})
      public static StorageServiceUpdateProperties create(final StorageService.AccountType accountType) {

         return new AutoValue_StorageServiceUpdateParams_StorageServiceUpdateProperties(accountType);
      }
   }

   /**
    * Specifies the tags of the storage account.
    */
   public abstract Map<String, String> tags();

   /**
    * Specifies the properties of the storage account.
    */
   public abstract StorageServiceUpdateProperties storageServiceProperties();


   @SerializedNames({"tags", "properties"})
   public static StorageServiceUpdateParams create(final Map<String, String> tags,
                                                   final StorageServiceUpdateProperties storageServiceProperties) {
      return new AutoValue_StorageServiceUpdateParams(tags == null ? ImmutableMap.<String, String>builder().build() : ImmutableMap.copyOf(tags), storageServiceProperties);
   }
}
