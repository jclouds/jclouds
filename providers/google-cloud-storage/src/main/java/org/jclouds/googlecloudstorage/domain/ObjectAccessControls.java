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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.internal.ProjectTeam;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Represents a Object Access Control Resource.
 *
 * @see <a href= "https://developers.google.com/storage/docs/json_api/v1/objectAccessControls"/>
 */
public class ObjectAccessControls extends Resource {

   protected final String bucket;
   protected final String entity;
   protected final String object;
   protected final Long generation;
   protected final ObjectRole role;
   protected final String email;
   protected final String entityId;
   protected final String domain;
   protected final ProjectTeam projectTeam;

   protected ObjectAccessControls(@Nullable String id, @Nullable URI selfLink, @Nullable String etag, String bucket,
            @Nullable String object, @Nullable Long generation, String entity, @Nullable String entityId,
            ObjectRole role, @Nullable String email, @Nullable String domain, @Nullable ProjectTeam projectTeam) {
      super(Kind.OBJECT_ACCESS_CONTROL, id, selfLink, etag);

      this.bucket = bucket;
      this.entity = checkNotNull(entity, "entity");
      this.object = object;
      this.generation = generation;
      this.entityId = entityId;
      this.role = checkNotNull(role, "role");
      this.email = email;
      this.domain = domain;
      this.projectTeam = projectTeam;
   }

   public String getBucket() {
      return bucket;
   }

   public String getEntity() {
      return entity;
   }

   public ObjectRole getRole() {
      return role;
   }

   public String getEmail() {
      return email;
   }

   public String getObject() {
      return object;
   }

   public Long getGeneration() {
      return generation;
   }

   public String getDomain() {
      return domain;
   }

   public String getEntityId() {
      return entityId;
   }

   public ProjectTeam getProjectTeam() {
      return projectTeam;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ObjectAccessControls that = ObjectAccessControls.class.cast(obj);
      return equal(this.kind, that.kind) && equal(this.bucket, that.bucket) && equal(this.object, that.object)
               && equal(this.entity, that.entity) && equal(this.id , that.id);
   }

   protected MoreObjects.ToStringHelper string() {
      return super.string().omitNullValues().add("bucket", bucket).add("entity", entity).add("entityId", entityId)
               .add("object", object).add("generation", generation).add("role", role).add("email", email)
               .add("domain", domain).add("projectTeam", projectTeam);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(kind, bucket, object, entity);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromObjectAccessControls(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      protected String object;
      protected Long generation;
      protected String bucket;
      protected String entity;
      protected String entityId;
      protected ObjectRole role;
      protected String email;
      protected String domain;
      protected ProjectTeam projectTeam;

      public Builder bucket(String bucket) {
         this.bucket = bucket;
         return this;
      }

      public Builder object(String object) {
         this.object = object;
         return this;
      }

      public Builder generation(Long generation) {
         this.generation = generation;
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

      public Builder role(ObjectRole role) {
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

      public ObjectAccessControls build() {
         return new ObjectAccessControls(super.id, super.selfLink, super.etag, bucket, object, generation, entity,
                  entityId, role, email, domain, projectTeam);
      }

      public Builder fromObjectAccessControls(ObjectAccessControls in) {
         return super.fromResource(in).bucket(in.getBucket()).entity(in.getEntity()).entityId(in.getEntityId())
                  .role(in.getRole()).email(in.getEmail()).domain(in.getDomain()).object(in.getObject())
                  .generation(in.getGeneration()).projectTeam(in.getProjectTeam());
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
