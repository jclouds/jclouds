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

import com.google.common.base.CaseFormat;

public final class DomainResourceRefferences {

   private DomainResourceRefferences() {
   }

   public enum Role {
      READER, WRITER, OWNER
   }

   public enum ObjectRole {
      READER, OWNER
   }

   public enum Location {
      ASIA, EU, US, ASIA_EAST1, US_CENTRAL1, US_CENTRAL2, US_EAST1, US_EAST2, US_EAST3, US_WEST1;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name()).toUpperCase();
      }

      @Override
      public String toString() {
         return value().toUpperCase();
      }

      public static Location fromValue(String location) {
         return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, location.toLowerCase()));
      }
   }

   public enum StorageClass {
      STANDARD, DURABLE_REDUCED_AVAILABILITY;
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
}
