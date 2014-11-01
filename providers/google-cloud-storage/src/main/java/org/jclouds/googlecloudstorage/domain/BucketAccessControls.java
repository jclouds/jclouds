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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Represents a BucketAccessControls Resource
 *
 * @see <a href= "https://developers.google.com/storage/docs/json_api/v1/bucketAccessControls" />
 */
@AutoValue
public abstract class BucketAccessControls {

   public enum Role {
      READER, WRITER, OWNER
   }

   // TODO: ensure this is actually needed on input.
   public abstract String kind();

   public abstract String id();

   public abstract String bucket();

   public abstract String entity();

   @Nullable public abstract String entityId();

   public abstract Role role();

   @Nullable public abstract String email();

   @Nullable public abstract String domain();

   @Nullable public abstract ProjectTeam projectTeam();

   @SerializedNames({ "kind", "id", "bucket", "entity", "entityId", "role", "email", "domain", "projectTeam" })
   static BucketAccessControls create(String kind, String id, String bucket, String entity, String entityId, Role role,
         String email, String domain, ProjectTeam projectTeam) {
      return new AutoValue_BucketAccessControls(kind,
            id == null ? (bucket + "/" + entity) : id, bucket, entity, entityId, role, email, domain, projectTeam);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String id;
      private String bucket;
      private String entity;
      private String entityId;
      private Role role;
      private String email;
      private String domain;
      private ProjectTeam projectTeam;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder bucket(String bucket) {
         this.bucket = bucket;
         return this;
      }

      public Builder entity(String entity) {
         this.entity = entity;
         return this;
      }

      public Builder entityId(String entityId) {
         this.entityId = entityId;
         return this;
      }

      public Builder role(Role role) {
         this.role = role;
         return this;
      }

      public Builder email(String email) {
         this.email = email;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder projectTeam(ProjectTeam projectTeam) {
         this.projectTeam = projectTeam;
         return this;
      }

      public BucketAccessControls build() {
         return BucketAccessControls
               .create("storage#bucketAccessControl", id, bucket, entity, entityId, role, email, domain, projectTeam);
      }
   }
}
