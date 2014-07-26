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

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * The bucket's logging configuration, which defines the destination bucket and optional name prefix for the current
 * bucket's logs.
 */

public final class ProjectTeam {

   public enum Team {
      OWNERS, EDITORS, VIEWERS;
      
      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static Team fromValue(String team) {
         return valueOf(team.toUpperCase());       
      } 
   }

   private final String projectNumber;
   private final Team team;

   
   private ProjectTeam(String projectNumber, Team team) {
      this.projectNumber = projectNumber;
      this.team = team;
   }

   public String getProjectNumber() {
      return projectNumber;
   }

   public Team getTeam() {
      return team;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(projectNumber, team);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ProjectTeam that = ProjectTeam.class.cast(obj);
      return equal(this.projectNumber, that.projectNumber) && equal(this.team, that.team);
   }

   protected MoreObjects.ToStringHelper string() {
      return toStringHelper(this).add("projectNumber", projectNumber).add("team", team);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String projectNumber;
      private Team team;

      public Builder projectNumber(String projectNumber) {
         this.projectNumber = projectNumber;
         return this;
      }

      public Builder team(Team team) {
         this.team = team;
         return this;
      }

      public ProjectTeam build() {
         return new ProjectTeam(this.projectNumber, this.team);
      }

      public Builder fromProjectTeam(ProjectTeam in) {
         return this.projectNumber(in.getProjectNumber()).team(in.getTeam());
      }
   }
}
