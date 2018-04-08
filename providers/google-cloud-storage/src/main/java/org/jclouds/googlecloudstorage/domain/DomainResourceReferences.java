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
package org.jclouds.googlecloudstorage.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.blobstore.domain.Tier;

import com.google.common.annotations.Beta;
import com.google.common.base.CaseFormat;

public final class DomainResourceReferences {

   private DomainResourceReferences() {
   }

   public enum ObjectRole {
      READER, OWNER
   }

   public enum Location {

     /*
      *  Multi-Regional
      */
      ASIA, EU, US,

      /*
       *  Regional
       */
      ASIA_EAST1,
      ASIA_NORTHEAST1,
      ASIA_SOUTH1,
      ASIA_SOUTHEAST1,

      NORTHAMERICA_NORTHEAST1,
      US_CENTRAL1,
      US_EAST1,
      US_EAST4,
      US_WEST1,

      SOUTHAMERICA_EAST1,

      EUROPE_WEST1,
      EUROPE_WEST2,
      EUROPE_WEST3,
      EUROPE_WEST4,

      AUSTRALIA_SOUTHEAST1,

      /*
       *  Alpha locations
       */
      @Beta
      US_CENTRAL2;

      public String value() {
         return name().replace('_', '-');
      }

      @Override
      public String toString() {
         return value();
      }

      public static Location fromValue(String location) {
         return valueOf(location.replace('-', '_'));
      }
   }

   public enum StorageClass {
      COLDLINE(Tier.ARCHIVE),
      DURABLE_REDUCED_AVAILABILITY(Tier.STANDARD),
      MULTI_REGIONAL(Tier.STANDARD),
      NEARLINE(Tier.INFREQUENT),
      STANDARD(Tier.STANDARD);

      private final Tier tier;

      private StorageClass(Tier tier) {
         this.tier = checkNotNull(tier, "tier");
      }

      public static StorageClass fromTier(Tier tier) {
         switch (tier) {
         case STANDARD: return StorageClass.STANDARD;
         case INFREQUENT: return StorageClass.NEARLINE;
         case ARCHIVE: return StorageClass.COLDLINE;
         }
         throw new IllegalArgumentException("invalid tier: " + tier);
      }

      public Tier toTier() {
         return tier;
      }
   }

   public enum Projection {
      NO_ACL, FULL;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Projection fromValue(String projection) {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, projection));
      }
   }

   public enum PredefinedAcl {
      AUTHENTICATED_READ, PRIVATE, PROJEECT_PRIVATE, PUBLIC_READ, PUBLIC_READ_WRITE;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static PredefinedAcl fromValue(String predefinedAcl) {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, predefinedAcl));
      }
   }

   public enum DestinationPredefinedAcl {
      AUTHENTICATED_READ, BUCKET_OWNER_FULLCONTROL, BUCKET_OWNER_READ, PRIVATE, PROJECT_PRIVATE, PUBLIC_READ;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static DestinationPredefinedAcl fromValue(String destinationPredefinedAcl) {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, destinationPredefinedAcl));
      }
   }
}
