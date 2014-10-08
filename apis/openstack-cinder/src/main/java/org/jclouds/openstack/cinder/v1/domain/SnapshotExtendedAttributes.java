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

package org.jclouds.openstack.cinder.v1.domain;

import com.google.common.base.Objects;
import org.jclouds.javax.annotation.Nullable;

import javax.inject.Named;
import java.beans.ConstructorProperties;

/**
 * Additional attributes delivered by Extended Snapshot Attributes extensions
 */
public class SnapshotExtendedAttributes {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromExtendedAttributes(this);
   }

   public static class Builder {

      protected String projectId;
      protected String progress;

      /**
       * @see SnapshotExtendedAttributes#getProjectId()
       */
      public Builder projectId(String projectId) {
         this.projectId = projectId;
         return self();
      }

      /**
       * @see SnapshotExtendedAttributes#getProgress()
       */
      public Builder progress(String progress) {
         this.progress = progress;
         return self();
      }

      public SnapshotExtendedAttributes build() {
         return new SnapshotExtendedAttributes(projectId, progress);
      }

      public Builder fromExtendedAttributes(SnapshotExtendedAttributes in) {
         return this
               .projectId(in.getProjectId())
               .progress(in.getProgress());
      }

      protected Builder self() {
         return this;
      }
   }

   @Named("os-extended-snapshot-attributes:project_id")
   private final String projectId;
   private final String progress;

   @ConstructorProperties({"os-extended-snapshot-attributes:project_id", "os-extended-snapshot-attributes:progress"})
   protected SnapshotExtendedAttributes(@Nullable String projectId, @Nullable String progress) {
      this.projectId = projectId;
      this.progress = progress;
   }

   /**
    * @return the project id of this snapshot
    */
   @Nullable
   public String getProjectId() {
      return this.projectId;
   }

   /**
    * @return the progress of this snapshot
    */
   @Nullable
   public String getProgress() {
      return this.progress;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(projectId, progress);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SnapshotExtendedAttributes that = SnapshotExtendedAttributes.class.cast(obj);
      return Objects.equal(this.projectId, that.projectId)
            && Objects.equal(this.progress, that.progress);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("projectId", projectId).add("progress", progress);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
