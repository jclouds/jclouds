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

package org.jclouds.digitalocean2.domain;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Date;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.CaseFormat;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

@AutoValue
public abstract class Action {
   
   public enum Status {
      COMPLETED, IN_PROGRESS, ERRORED;

      Status() {}

      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         if (!status.isPresent()) {
            String upperCamelValue = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, value.toLowerCase());
            status = Enums.getIfPresent(Status.class, upperCamelValue);
         }
         checkArgument(status.isPresent(), "Expected one of %s but was", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }
   }

   public abstract int id();
   public abstract Status status();
   public abstract String type();
   public abstract Date startedAt();
   @Nullable public abstract Date completedAt();
   public abstract Integer resourceId();
   public abstract String resourceType();
   @Nullable public abstract Region region();
   @Nullable public abstract String regionSlug();

   @SerializedNames({ "id", "status", "type", "started_at", "completed_at", "resource_id", "resource_type",
      "region", "region_slug" })
   public static Action create(int id, Status status, String type, Date startedAt, Date completedAt, int resourceId,
         String resourceType, Region region, String regionSlug) {
      return new AutoValue_Action(id, status, type, startedAt, completedAt, resourceId, resourceType, region,
            regionSlug);
   }

   Action() {}
}
