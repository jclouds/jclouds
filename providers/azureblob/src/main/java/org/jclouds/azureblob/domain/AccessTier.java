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
package org.jclouds.azureblob.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.blobstore.domain.Tier;

import com.google.common.base.CaseFormat;

public enum AccessTier {
   HOT(Tier.STANDARD),
   COOL(Tier.INFREQUENT),
   ARCHIVE(Tier.ARCHIVE);

   private final Tier tier;

   private AccessTier(Tier tier) {
      this.tier = checkNotNull(tier);
   }

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
   }

   @Override
   public String toString() {
      return value();
   }

   // TODO: call valueOf instead like GCS?
   public static AccessTier fromValue(String tier) {
      return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(tier, "tier")));
   }

   public static AccessTier fromTier(Tier tier) {
      switch (tier) {
      case STANDARD: return AccessTier.HOT;
      case INFREQUENT: return AccessTier.COOL;
      case ARCHIVE: return AccessTier.ARCHIVE;
      }
      throw new IllegalArgumentException("invalid tier: " + tier);
   }

   public Tier toTier() {
      return tier;
   }
}
