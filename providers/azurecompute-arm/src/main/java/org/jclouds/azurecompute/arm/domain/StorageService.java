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
import java.util.Date;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.azurecompute.arm.util.GetEnumValue;

@AutoValue
public abstract class StorageService {

   public enum AccountType {

      Standard_LRS,
      Standard_ZRS,
      Standard_GRS,
      Standard_RAGRS,
      Premium_LRS,
      UNRECOGNIZED;

       public static AccountType fromValue(final String text) {
           return (AccountType) GetEnumValue.fromValueOrDefault(text, AccountType.UNRECOGNIZED);
       }
   }

   public enum RegionStatus {

       Available,
       Unavailable,
       UNRECOGNIZED;

       public static RegionStatus fromValue(final String text) {
           return (RegionStatus) GetEnumValue.fromValueOrDefault(text, RegionStatus.UNRECOGNIZED);
       }

   }

   public enum Status {

       Creating,
       Created,
       Deleting,
       Deleted,
       Changing,
       ResolvingDns,
       Succeeded,
       UNRECOGNIZED;

       public static Status fromValue(final String text) {
           return (Status) GetEnumValue.fromValueOrDefault(text, Status.UNRECOGNIZED);
       }
   }

   @AutoValue
   public abstract static class StorageServiceProperties {

       /**
        * Specifies whether the account supports locally-redundant storage, geo-redundant storage, zone-redundant
        * storage, or read access geo-redundant storage.
        */
       public abstract AccountType accountType();

       /**
        * Specifies the time that the storage account was created.
        */
       @Nullable
       public abstract Date creationTime();

       /**
        * Specifies the endpoints of the storage account.
        */
       @Nullable
       public abstract Map<String, String> primaryEndpoints();

       /**
       * A primaryLocation for the storage account.
       */
      @Nullable
      public abstract String primaryLocation();

      /**
       * provisioningState for the storage group
       */
      @Nullable
      public abstract Status provisioningState();

       /**
        * Specifies the secondary endpoints of the storage account.
        */
       @Nullable
       public abstract Map<String, String> secondaryEndpoints();

      /**
       * Secondary location for the storage group
       */
      @Nullable
      public abstract String secondaryLocation();

      /**
       * The status of primary endpoints
       */
      @Nullable
      public abstract RegionStatus statusOfPrimary();

      /**
       * The secondary status of the storage account.
       */
      @Nullable
      public abstract RegionStatus statusOfSecondary();


      @SerializedNames({"accountType", "creationTime", "primaryEndpoints",  "primaryLocation",
              "provisioningState", "secondaryEndpoints", "secondaryLocation", "statusOfPrimary", "statusOfSecondary"})
      public static StorageServiceProperties create(final AccountType accountType, final Date creationTime,
              final Map<String, String> primaryEndpoints, final String primaryLocation, final Status provisioningState,
              final Map<String, String> secondaryEndpoints, final String secondaryLocation,
              final RegionStatus statusOfPrimary, final RegionStatus statusOfSecondary) {

         StorageServiceProperties.Builder builder = StorageServiceProperties.builder()
                 .accountType(accountType)
                 .creationTime(creationTime)
                 .primaryLocation(primaryLocation)
                 .provisioningState(provisioningState)
                 .secondaryLocation(secondaryLocation)
                 .statusOfPrimary(statusOfPrimary)
                 .statusOfSecondary(statusOfSecondary);


         builder.primaryEndpoints(primaryEndpoints != null ? ImmutableMap.copyOf(primaryEndpoints) : null);
         builder.secondaryEndpoints(secondaryEndpoints != null ? ImmutableMap.copyOf(secondaryEndpoints) : null);

         return  builder.build();
      }
      
      public abstract Builder toBuilder();
      
      public static Builder builder() {
         return new AutoValue_StorageService_StorageServiceProperties.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder accountType(AccountType accountType);
         public abstract Builder creationTime(Date creationTime);
         public abstract Builder primaryEndpoints(Map<String, String> primaryEndpoints);
         public abstract Builder primaryLocation(String primaryLocation);
         public abstract Builder provisioningState(Status provisioningState);
         public abstract Builder secondaryEndpoints(Map<String, String> secondaryEndpoints);
         public abstract Builder secondaryLocation(String secondaryLocation);
         public abstract Builder statusOfPrimary(RegionStatus statusOfPrimary);
         public abstract Builder statusOfSecondary(RegionStatus statusOfSecondary);

         abstract Map<String, String>  primaryEndpoints();
         abstract Map<String, String>  secondaryEndpoints();

         abstract StorageServiceProperties autoBuild();

         public StorageServiceProperties build() {
            primaryEndpoints(primaryEndpoints() != null ? ImmutableMap.copyOf(primaryEndpoints()) : null);
            secondaryEndpoints(secondaryEndpoints() != null ? ImmutableMap.copyOf(secondaryEndpoints()) : null);
            return autoBuild();
         }
      }
   }

   /**
    * Specifies the id of the storage account.
    */
   @Nullable
   public abstract String id();

   /**
    * Specifies the name of the storage account. This name is the DNS prefix name and can be used to access blobs,
    * queues, and tables in the storage account.
    */
   @Nullable
   public abstract String name();

   /**
    * Specifies the location of the storage account.
    */
   public abstract String location();

   /**
    * Specifies the tags of the storage account.
    */
   @Nullable
   public abstract Map<String, String> tags();

   /**
    * Specifies the type of the storage account.
    */
   @Nullable
   public abstract String type();

   /**
    * Specifies the properties of the storage account.
    */
   public abstract StorageServiceProperties storageServiceProperties();


   @SerializedNames({"id", "name", "location", "tags", "type", "properties"})
   public static StorageService create(final String id,  final String name,  final String location,
                                       final Map<String, String> tags,  final String type,
                                       final StorageServiceProperties storageServiceProperties) {
      return new AutoValue_StorageService(id,  name,  location,  tags == null ? null : ImmutableMap.copyOf(tags), type, storageServiceProperties);
   }
}
