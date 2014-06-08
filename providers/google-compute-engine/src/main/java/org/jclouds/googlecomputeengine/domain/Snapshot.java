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
package org.jclouds.googlecomputeengine.domain;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * A Persistent Disk Snapshot resource.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/snapshots"/>
 */
@Beta
public final class Snapshot extends AbstractDisk {

   private final Optional<URI> sourceDisk;
   private final String sourceDiskId;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "diskSizeGb",
           "status", "sourceDisk", "sourceDiskId"
   })
   private Snapshot(String id, Date creationTimestamp, URI selfLink, String name, String description,
                    Integer sizeGb, String status, URI sourceDisk, String sourceDiskId) {
      super(Kind.SNAPSHOT, id, creationTimestamp, selfLink, name, description, sizeGb, status);
      this.sourceDisk = fromNullable(sourceDisk);
      this.sourceDiskId = checkNotNull(sourceDiskId, "sourceDiskId of %s", name);
   }

   /**
    * @return The source disk used to create this snapshot. Once the source disk
    *   has been deleted from the system, this field will be cleared, and will
    *   not be set even if a disk with the same name has been re-created (output only).
    */
   public Optional<URI> getSourceDisk() {
      return sourceDisk;
   }

   /**
    * @return The ID value of the disk used to create this snapshot. This value
    *   may be used to determine whether the snapshot was taken from the current
    *   or a previous instance of a given disk name.
    */
   public String getSourceDiskId() {
      return sourceDiskId;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("sourceDisk", sourceDisk.orNull())
              .add("sourceDiskId", sourceDiskId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromSnapshot(this);
   }

   public static final class Builder extends AbstractDisk.Builder<Builder> {

      private URI sourceDisk;
      private String sourceDiskId;

      /**
       * @see Snapshot#getSourceDisk()
       */
      public Builder sourceDisk(URI sourceDisk) {
         this.sourceDisk = sourceDisk;
         return this;
      }

      /**
       * @see Snapshot#getSourceDiskId()
       */
      public Builder sourceDiskId(String sourceDiskId) {
         this.sourceDiskId = sourceDiskId;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Snapshot build() {
         return new Snapshot(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, super.sizeGb, super.status, sourceDisk, sourceDiskId);
      }

      public Builder fromSnapshot(Snapshot in) {
         return super.fromAbstractDisk(in)
                 .sourceDisk(in.getSourceDisk().orNull())
                 .sourceDiskId(in.getSourceDiskId());
      }

   }

}
