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

import java.util.Set;

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.internal.BucketCors;
import org.jclouds.googlecloudstorage.domain.internal.BucketLifeCycle;
import org.jclouds.googlecloudstorage.domain.internal.Logging;
import org.jclouds.googlecloudstorage.domain.internal.Owner;
import org.jclouds.googlecloudstorage.domain.internal.Versioning;
import org.jclouds.googlecloudstorage.domain.internal.Website;

import com.google.common.collect.Sets;

public class BucketTemplate {

   protected String name;
   protected Long projectNumber;
   protected Set<BucketAccessControls> acl = Sets.newHashSet();
   protected Set<DefaultObjectAccessControls> defaultObjectAccessControls = Sets.newHashSet();
   protected Owner owner;
   protected Location location;
   protected Website website;
   protected Logging logging;
   protected Versioning versioning;
   protected Set<BucketCors> cors = Sets.newHashSet();
   protected BucketLifeCycle lifeCycle;
   protected StorageClass storageClass;

   public BucketTemplate name(String name) {
      this.name = name;
      return this;
   }

   public BucketTemplate projectNumber(Long projectNumber) {
      this.projectNumber = projectNumber;
      return this;
   }

   public BucketTemplate owner(Owner owner) {
      this.owner = owner;
      return this;
   }

   public BucketTemplate location(Location location) {
      this.location = location;
      return this;
   }

   public BucketTemplate website(Website website) {
      this.website = website;
      return this;
   }

   public BucketTemplate logging(Logging logging) {
      this.logging = logging;
      return this;
   }

   public BucketTemplate versioning(Versioning versioning) {
      this.versioning = versioning;
      return this;
   }

   public BucketTemplate lifeCycle(BucketLifeCycle lifeCycle) {
      this.lifeCycle = lifeCycle;
      return this;
   }

   public BucketTemplate storageClass(StorageClass storageClass) {
      this.storageClass = storageClass;
      return this;
   }

   public BucketTemplate addAcl(BucketAccessControls bucketAccessControls) {
      this.acl.add(bucketAccessControls);
      return this;
   }

   public BucketTemplate acl(Set<BucketAccessControls> acl) {

      this.acl.addAll(acl);
      return this;
   }

   public BucketTemplate addDefaultObjectAccessControls(DefaultObjectAccessControls oac) {
      this.defaultObjectAccessControls.add(oac);
      return this;
   }

   public BucketTemplate defaultObjectAccessControls(Set<DefaultObjectAccessControls> defaultObjectAcl) {
      this.defaultObjectAccessControls.addAll(defaultObjectAcl);
      return this;
   }

   public BucketTemplate addCORS(BucketCors cors) {
      this.cors.add(cors);
      return this;
   }

   public BucketTemplate cors(Set<BucketCors> cors) {
      this.cors.addAll(cors);
      return this;
   }

   public Long getProjectNumber() {
      return projectNumber;
   }

   public String getName() {
      return name;
   }

   public Set<BucketAccessControls> getAcl() {
      return acl;
   }

   public Set<DefaultObjectAccessControls> getDefaultObjectAccessControls() {
      return defaultObjectAccessControls;
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

   public static Builder builder() {
      return new Builder();
   }

   public static BucketTemplate fromBucketsTemplate(BucketTemplate bucketTemplate) {
      return Builder.fromBucketsTemplate(bucketTemplate);
   }

   public static class Builder {

      public static BucketTemplate fromBucketsTemplate(BucketTemplate in) {
         return new BucketTemplate().name(in.getName()).projectNumber(in.getProjectNumber()).acl(in.getAcl())
                  .defaultObjectAccessControls(in.getDefaultObjectAccessControls()).owner(in.getOwner())
                  .location(in.getLocation()).website(in.getWebsite()).logging(in.getLogging())
                  .versioning(in.getVersioning()).cors(in.getCors()).lifeCycle(in.getLifeCycle())
                  .storageClass(in.getStorageClass());
      }

   }
}
