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
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;

import org.jclouds.googlecloudstorage.features.ApiResourceRefferences.ObjectRole;

import com.google.common.base.Objects;

/**
 * Represents a DefaultObjectAccessControls Resource
 *
 * @see <a href= "https://developers.google.com/storage/docs/json_api/v1/defaultObjectAccessControls"/>
 */
public class DefaultObjectAccessControls extends Resource {

   protected final String bucket;
   protected final String entity;
   protected final ObjectRole role;
   protected final String email;
   protected final String entityId;
   protected final String domain;
   protected final ProjectTeam projectTeam;

   protected DefaultObjectAccessControls(String id, URI selfLink, String etag, String bucket, String entity,
            String entityId, ObjectRole role, String email, String domain, ProjectTeam projectTeam) {
      super(Kind.OBJECT_ACCESS_CONTROL, id, selfLink, etag);

      this.bucket = bucket;
      this.entity = checkNotNull(entity, "entity");
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

   public String getDomain() {
      return domain;
   }

   public String getEntityId() {
      return entityId;
   }

   public ProjectTeam getProjectTeam() {
      return projectTeam;
   }

   public static class ProjectTeam {

      public enum Team {
         owners, editors, viewers;
      }

      private final String projectId;
      private final Team team;

      @ConstructorProperties({ "projectId", "team" })
      public ProjectTeam(String projectId, Team team) {
         this.projectId = projectId;
         this.team = team;
      }

      public String getProjectId() {
         return projectId;
      }

      public Team getTeam() {
         return team;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(projectId, team);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         ProjectTeam that = ProjectTeam.class.cast(obj);
         return equal(this.projectId, that.projectId) && equal(this.team, that.team);
      }

      protected Objects.ToStringHelper string() {
         return toStringHelper(this).add("projectId", projectId).add("team", team);
      }

      @Override
      public String toString() {
         return string().toString();
      }

      public static class Builder {

         private String projectId;
         private Team team;

         public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
         }

         public Builder team(Team team) {
            this.team = team;
            return this;
         }

         public ProjectTeam build() {
            return new ProjectTeam(this.projectId, this.team);
         }

         public Builder fromProjectTeam(ProjectTeam in) {
            return this.projectId(in.getProjectId()).team(in.getTeam());
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      DefaultObjectAccessControls that = DefaultObjectAccessControls.class.cast(obj);
      return equal(this.kind, that.kind) && equal(this.entity, that.entity) && equal(this.role, that.role);
   }

   protected Objects.ToStringHelper string() {
      return super.string().omitNullValues().add("bucket", bucket).add("entity", entity).add("entityId", entityId)
               .add("role", role).add("email", email).add("domain", domain);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(kind, entity);
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

      public DefaultObjectAccessControls build() {
         return new DefaultObjectAccessControls(super.id, super.selfLink, super.etag, bucket, entity, entityId, role,
                  email, domain, projectTeam);
      }

      public Builder fromObjectAccessControls(DefaultObjectAccessControls in) {
         return super.fromResource(in).bucket(in.getBucket()).entity(in.getEntity()).entityId(in.getEntityId())
                  .role(in.getRole()).email(in.getEmail()).domain(in.getDomain()).projectTeam(in.getProjectTeam());
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
