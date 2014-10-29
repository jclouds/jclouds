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

import static org.jclouds.googlecloudstorage.internal.NullSafeCopies.copyOf;

import java.util.Date;
import java.util.List;

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * The Bucket represents a bucket in Google Cloud Storage. There is a single global namespace shared by all buckets.
 *
 * @see <a href = " https://developers.google.com/storage/docs/json_api/v1/buckets"/>
 */
@AutoValue
public abstract class Bucket {
   @AutoValue
   public abstract static class Cors {
      public abstract List<String> origin();

      public abstract List<String> method();

      public abstract List<String> responseHeader();

      public abstract Integer maxAgeSeconds();

      @SerializedNames({ "origin", "method", "responseHeader", "maxAgeSeconds" })
      public static Cors create(List<String> origin, List<String> method, List<String> responseHeader,
            Integer maxAgeSeconds) {
         return new AutoValue_Bucket_Cors(copyOf(origin), copyOf(method), copyOf(responseHeader), maxAgeSeconds);
      }
   }

   @AutoValue
   public abstract static class Logging {
      public abstract String logBucket();

      @Nullable public abstract String logObjectPrefix();

      @SerializedNames({ "logBucket", "logObjectPrefix" })
      public static Logging create(String logBucket, String logObjectPrefix) {
         return new AutoValue_Bucket_Logging(logBucket, logObjectPrefix);
      }
   }

   @AutoValue
   public abstract static class LifeCycle {

      @AutoValue
      public abstract static class Rule {

         @AutoValue
         public abstract static class Action {
            public abstract String type();

            @SerializedNames("type")
            public static Action create(String type) {
               return new AutoValue_Bucket_LifeCycle_Rule_Action(type);
            }
         }

         @AutoValue
         public abstract static class Condition {
            @Nullable public abstract Integer age();

            @Nullable public abstract Date createdBefore();

            @Nullable public abstract Boolean isLive();

            @Nullable public abstract Integer numNewerVersions();

            @SerializedNames({ "age", "createdBefore", "isLive", "numNewerVersions" })
            public static Condition create(Integer age, Date createdBefore, Boolean isLive, Integer numNewerVersions) {
               return new AutoValue_Bucket_LifeCycle_Rule_Condition(age, createdBefore, isLive, numNewerVersions);
            }
         }

         public abstract Action action();

         public abstract Condition condition();

         @SerializedNames({ "action", "condition" })
         public static Rule create(Action action, Condition condition) {
            return new AutoValue_Bucket_LifeCycle_Rule(action, condition);
         }
      }

      public abstract List<Rule> rules();

      @SerializedNames("rules")
      public static LifeCycle create(List<Rule> rules) {
         return new AutoValue_Bucket_LifeCycle(copyOf(rules));
      }
   }

   @AutoValue
   public abstract static class Website {
      @Nullable public abstract String mainPageSuffix();

      @Nullable public abstract String notFoundPage();

      @SerializedNames({ "mainPageSuffix", "notFoundPage" })
      public static Website create(String mainPageSuffix, String notFoundPage) {
         return new AutoValue_Bucket_Website(mainPageSuffix, notFoundPage);
      }
   }

   @AutoValue
   public abstract static class Versioning {
      public abstract Boolean enabled();

      @SerializedNames("enabled")
      public static Versioning create(Boolean enabled) {
         return new AutoValue_Bucket_Versioning(enabled);
      }
   }

   public abstract String id();

   public abstract String name();

   @Nullable public abstract Long projectNumber();

   public abstract Date timeCreated();

   public abstract Long metageneration();

   public abstract List<BucketAccessControls> acl();

   public abstract List<ObjectAccessControls> defaultObjectAcl();

   public abstract Owner owner();

   @Nullable public abstract Location location();

   @Nullable public abstract Website website();

   @Nullable public abstract Logging logging();

   @Nullable public abstract Versioning versioning();

   public abstract List<Cors> cors();

   @Nullable public abstract LifeCycle lifeCycle();

   @Nullable public abstract StorageClass storageClass();

   @SerializedNames(
         { "id", "name", "projectNumber", "timeCreated", "metageneration", "acl", "defaultObjectAcl", "owner",
               "location", "website", "logging", "versioning", "cors", "lifeCycle", "storageClass" })
   public static Bucket create(String id, String name, Long projectNumber, Date timeCreated, Long metageneration,
         List<BucketAccessControls> acl, List<ObjectAccessControls> defaultObjectAcl, Owner owner,
         Location location, Website website, Logging logging, Versioning versioning, List<Cors> cors,
         LifeCycle lifeCycle, StorageClass storageClass) {
      return new AutoValue_Bucket(id, name, projectNumber, timeCreated, metageneration, copyOf(acl),
            copyOf(defaultObjectAcl), owner, location, website, logging, versioning, copyOf(cors), lifeCycle,
            storageClass);
   }
}
