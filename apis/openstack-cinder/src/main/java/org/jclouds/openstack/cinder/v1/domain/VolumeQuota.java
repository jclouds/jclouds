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

import static com.google.common.base.Preconditions.checkNotNull;

public class VolumeQuota {

   private final String id;
   private final int volumes;
   private final int gigabytes;
   private final int snapshots;

   protected VolumeQuota(String id, int volumes, int gigabytes, int snapshots) {
      this.id = checkNotNull(id, "id");
      this.volumes = volumes;
      this.gigabytes = gigabytes;
      this.snapshots = snapshots;
   }

   /**
    * The id of the tenant this set of limits applies to
    */
   public String getId() {
      return this.id;
   }

   /**
    * The limit of the number of volumes that can be created for the tenant
    */
   public int getVolumes() {
      return this.volumes;
   }

   /**
    * The limit of the total size of all volumes for the tenant
    */
   public int getGigabytes() {
      return this.gigabytes;
   }

   /**
    * The limit of the number of snapshots that can be used by the tenant
    */
   public int getSnapshots() {
      return this.snapshots;
   }


   @Override
   public int hashCode() {
      return Objects.hashCode(id, volumes, gigabytes, snapshots);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      VolumeQuota that = VolumeQuota.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.volumes, that.volumes)
            && Objects.equal(this.gigabytes, that.gigabytes)
            && Objects.equal(this.snapshots, that.snapshots);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("volumes", volumes).add("gigabytes", gigabytes).add("snapshots", snapshots);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVolumeQuota(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected int volumes;
      protected int gigabytes;
      protected int snapshots;


      /**
       * @see VolumeQuota#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see VolumeQuota#getVolumes()
       */
      public T volumes(int volumes) {
         this.volumes = volumes;
         return self();
      }

      /**
       * @see VolumeQuota#getGigabytes()
       */
      public T gigabytes(int gigabytes) {
         this.gigabytes = gigabytes;
         return self();
      }

      /**
       * @see VolumeQuota#getSnapshots()
       */
      public T snapshots(int snapshots) {
         this.snapshots = snapshots;
         return self();
      }


      public VolumeQuota build() {
         return new VolumeQuota(id, volumes, gigabytes, snapshots);
      }

      public T fromVolumeQuota(VolumeQuota in) {
         return this
               .id(in.getId())
               .volumes(in.getVolumes())
               .gigabytes(in.getGigabytes())
               .snapshots(in.getSnapshots());

      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
