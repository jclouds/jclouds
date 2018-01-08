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

import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Map;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Secret {

   @AutoValue
   public abstract static class SecretAttributes {
      @Nullable
      public abstract Boolean enabled();

      @Nullable
      public abstract Integer created();

      @Nullable
      public abstract Integer expires();

      @Nullable
      public abstract Integer notBefore();

      @Nullable
      public abstract String recoveryLevel();

      @Nullable
      public abstract Integer updated();

      @SerializedNames({"enabled", "created", "expires", "notBefore", "recoveryLevel", "updated"})
      public static SecretAttributes create(final Boolean enabled,
                                            final Integer created,
                                            final Integer expires,
                                            final Integer notBefore,
                                            final String recoveryLevel,
                                            final Integer updated) {
         return new AutoValue_Secret_SecretAttributes(enabled, created, expires, notBefore, recoveryLevel, updated);
      }

      SecretAttributes() {
      }
   }

   @AutoValue
   public abstract static class DeletedSecretBundle {
      @Nullable
      public abstract SecretAttributes attributes();

      @Nullable
      public abstract String contentType();

      @Nullable
      public abstract String deletedDate();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract String kid();

      @Nullable
      public abstract Boolean managed();

      @Nullable
      public abstract String recoveryId();

      @Nullable
      public abstract String scheduledPurgeDate();

      @Nullable
      public abstract Map<String, String> tags();

      @Nullable
      public abstract String value();

      @SerializedNames({"attributes", "contentType", "deletedDate", "id", "kid", "managed", "recoveryId", "scheduledPurgeDate", "tags", "value"})
      public static DeletedSecretBundle create(final SecretAttributes attributes,
                                               final String contentType,
                                               final String deletedDate,
                                               final String id,
                                               final String kid,
                                               final Boolean managed,
                                               final String recoveryId,
                                               final String scheduledPurgeDate,
                                               final Map<String, String> tags,
                                               String value) {
         return new AutoValue_Secret_DeletedSecretBundle(
                 attributes, contentType, deletedDate,
                 id, kid, managed, recoveryId, scheduledPurgeDate,
                 tags != null ? ImmutableMap.copyOf(tags) : null,
                 value);
      }
   }

   @AutoValue
   public abstract static class SecretBundle {
      @Nullable
      public abstract SecretAttributes attributes();

      @Nullable
      public abstract String contentType();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract String kid();

      @Nullable
      public abstract Boolean managed();

      @Nullable
      public abstract Map<String, String> tags();

      @Nullable
      public abstract String value();

      @SerializedNames({"attributes", "contentType", "id", "kid", "managed", "tags", "value"})
      public static SecretBundle create(final SecretAttributes attributes,
                                        final String contentType,
                                        final String id,
                                        final String kid,
                                        final Boolean managed,
                                        final Map<String, String> tags,
                                        String value) {
         return new AutoValue_Secret_SecretBundle(
                 attributes, contentType, id,
                 kid, managed,
                 tags != null ? ImmutableMap.copyOf(tags) : null,
                 value);
      }
   }

   @Nullable
   public abstract SecretAttributes attributes();

   @Nullable
   public abstract String contentType();

   @Nullable
   public abstract String id();

   @Nullable
   public abstract Boolean managed();

   @Nullable
   public abstract Map<String, String> tags();

   @SerializedNames({"attributes", "contentType", "id", "managed", "tags"})
   public static Secret create(final SecretAttributes attributes,
                               final String contentType,
                               final String id,
                               final Boolean managed,
                               final Map<String, String> tags) {
      return new AutoValue_Secret(
              attributes,
              contentType,
              id,
              managed,
              tags != null ? ImmutableMap.copyOf(tags) : null
      );
   }

   Secret() {
   }
}
