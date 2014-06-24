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
import java.util.Date;
import java.util.Set;

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.internal.BucketCors;
import org.jclouds.googlecloudstorage.domain.internal.BucketLifeCycle;
import org.jclouds.googlecloudstorage.domain.internal.Logging;
import org.jclouds.googlecloudstorage.domain.internal.Owner;
import org.jclouds.googlecloudstorage.domain.internal.Versioning;
import org.jclouds.googlecloudstorage.domain.internal.Website;



import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * The Bucket represents a bucket in Google Cloud Storage There is a single global namespace shared by all buckets
 *
 * @see <a href = " https://developers.google.com/storage/docs/json_api/v1/buckets"/>
 */
public class Bucket extends Resource {

   private final String name;
   private final Long projectNumber;
   private final Date timeCreated;
   private final Long metageneration;
   private final Set<BucketAccessControls> acl;
   private final Set<DefaultObjectAccessControls> defaultObjectAcl;
   private final Owner owner;
   private final Location location;
   private final Website website;
   private final Logging logging;
   private final Versioning versioning;
   private final Set<BucketCors> cors;
   private final BucketLifeCycle lifeCycle;
   private final StorageClass storageClass;

   public Bucket(String id, URI selfLink, String name, String etag, Long projectNumber, Date timeCreated,
            Long metageneration, Set<BucketAccessControls> acl, Set<DefaultObjectAccessControls> defaultObjectAcl,
            Owner owner, Location location, Website website, Logging logging, Versioning versioning, Set<BucketCors> cors,
            BucketLifeCycle lifeCycle, StorageClass storageClass) {

      super(Kind.BUCKET, id, selfLink, etag);
      this.projectNumber = projectNumber;
      this.timeCreated = checkNotNull(timeCreated, "timeCreated");
      this.metageneration = checkNotNull(metageneration, "metageneration");
      this.acl = acl.isEmpty() ? null : acl;
      this.defaultObjectAcl = defaultObjectAcl.isEmpty() ? null : defaultObjectAcl;
      this.owner = checkNotNull(owner, "Owner");
      this.location = checkNotNull(location, "location");
      this.website = website;
      this.logging = logging;
      this.versioning = versioning;
      this.cors = cors.isEmpty() ? null : cors;
      this.lifeCycle = lifeCycle;
      this.storageClass = storageClass;
      this.name = checkNotNull(name, "name");
   }

   public Long getProjectNumber() {
      return projectNumber;
   }

   public String getName() {
      return name;
   }

   public Date getTimeCreated() {
      return timeCreated;
   }

   public Long getMetageneration() {
      return metageneration;
   }

   public Set<BucketAccessControls> getAcl() {
      return acl;
   }

   public Set<DefaultObjectAccessControls> getDefaultObjectAcl() {
      return defaultObjectAcl;
   }

   public Owner getOwner() {
      return owner;
   }

   public Location getLocation() {
      return location;
   }

   public Website getWebsite() {
      return website;
   }

   public Logging getLogging() {
      return logging;
   }

   public Versioning getVersioning() {
      return versioning;
   }

   public Set<BucketCors> getCors() {
      return cors;
   }

   public BucketLifeCycle getLifeCycle() {
      return lifeCycle;
   }

   public StorageClass getStorageClass() {
      return storageClass;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Bucket that = Bucket.class.cast(obj);
      return equal(this.kind, that.kind) && equal(this.name, that.name)
               && equal(this.projectNumber, that.projectNumber);

   }

   protected Objects.ToStringHelper string() {
      return super.string().omitNullValues().add("name", name).add("timeCreated", timeCreated)
               .add("projectNumber", projectNumber).add("metageneration", metageneration).add("acl", acl)
               .add("defaultObjectAcl", defaultObjectAcl).add("owner", owner).add("location", location)
               .add("website", website).add("logging", logging).add("versioning", versioning).add("cors", cors)
               .add("lifeCycle", lifeCycle).add("storageClass", storageClass);

   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromBucket(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private String name;
      private Long projectNumber;
      private Date timeCreated;
      private Long metageneration;
      private ImmutableSet.Builder<BucketAccessControls> acl = ImmutableSet.builder();
      private ImmutableSet.Builder<DefaultObjectAccessControls> defaultObjectAcl = ImmutableSet.builder();
      private Owner owner;
      private Location location;
      private Website website;
      private Logging logging;
      private Versioning versioning;
      private ImmutableSet.Builder<BucketCors> cors = ImmutableSet.builder();
      private BucketLifeCycle lifeCycle;
      private StorageClass storageClass;

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder projectNumber(Long projectNumber) {
         this.projectNumber = projectNumber;
         return this;

      }

      public Builder timeCreated(Date timeCreated) {
         this.timeCreated = timeCreated;
         return this;
      }

      public Builder metageneration(Long metageneration) {
         this.metageneration = metageneration;
         return this;
      }

      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      public Builder location(Location location) {
         this.location = location;
         return this;
      }

      public Builder website(Website website) {
         this.website = website;
         return this;
      }

      public Builder logging(Logging logging) {
         this.logging = logging;
         return this;
      }

      public Builder versioning(Versioning versioning) {
         this.versioning = versioning;
         return this;
      }

      public Builder lifeCycle(BucketLifeCycle lifeCycle) {
         this.lifeCycle = lifeCycle;
         return this;
      }

      public Builder storageClass(StorageClass storageClass) {
         this.storageClass = storageClass;
         return this;
      }

      public Builder addAcl(BucketAccessControls bucketAccessControls) {
         this.acl.add(bucketAccessControls);
         return this;
      }

      public Builder acl(Set<BucketAccessControls> acl) {
         this.acl.addAll(acl);
         return this;
      }

      public Builder addDefaultObjectAcl(DefaultObjectAccessControls defaultObjectAccessControls) {
         this.defaultObjectAcl.add(defaultObjectAccessControls);
         return this;
      }

      public Builder defaultObjectAcl(Set<DefaultObjectAccessControls> defaultObjectAcl) {
         this.defaultObjectAcl.addAll(defaultObjectAcl);
         return this;
      }

      public Builder addCORS(BucketCors cors) {
         this.cors.add(cors);
         return this;
      }

      public Builder cors(Set<BucketCors> cors) {
         this.cors.addAll(cors);
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Bucket build() {
         return new Bucket(super.id, super.selfLink, name, super.etag, projectNumber, timeCreated, metageneration,
                  acl.build(), defaultObjectAcl.build(), owner, location, website, logging, versioning, cors.build(),
                  lifeCycle, storageClass);
      }

      public Builder fromBucket(Bucket in) {
         return super.fromResource(in).name(in.getName()).projectNumber(in.getProjectNumber())
                  .timeCreated(in.getTimeCreated()).metageneration(in.getMetageneration()).acl(in.getAcl())
                  .defaultObjectAcl(in.getDefaultObjectAcl()).owner(in.getOwner()).location(in.getLocation())
                  .website(in.getWebsite()).logging(in.getLogging()).versioning(in.getVersioning()).cors(in.getCors())
                  .lifeCycle(in.getLifeCycle()).storageClass(in.getStorageClass());
      }
   }

}
