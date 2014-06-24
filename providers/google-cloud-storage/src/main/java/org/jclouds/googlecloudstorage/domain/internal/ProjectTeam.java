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
package org.jclouds.googlecloudstorage.domain.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;

/**
 * The bucket's logging configuration, which defines the destination bucket and optional name prefix for the current
 * bucket's logs.
 */

public final class ProjectTeam {

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
