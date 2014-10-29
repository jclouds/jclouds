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

package org.jclouds.googlecloudstorage.domain.templates;

import java.util.List;

import org.jclouds.googlecloudstorage.domain.Bucket.Cors;
import org.jclouds.googlecloudstorage.domain.Bucket.LifeCycle;
import org.jclouds.googlecloudstorage.domain.Bucket.Logging;
import org.jclouds.googlecloudstorage.domain.Bucket.Versioning;
import org.jclouds.googlecloudstorage.domain.Bucket.Website;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.Owner;

import com.google.common.collect.Lists;

public class BucketTemplate {

   private String name;
   private Long projectNumber;
   private List<BucketAccessControls> acl = Lists.newArrayList();
   private List<ObjectAccessControls> defaultObjectAccessControls = Lists.newArrayList();
   private Owner owner;
   private Location location;
   private Website website;
   private Logging logging;
   private Versioning versioning;
   private List<Cors> cors = Lists.newArrayList();
   private LifeCycle lifeCycle;
   private StorageClass storageClass;

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

   public BucketTemplate lifeCycle(LifeCycle lifeCycle) {
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

   public BucketTemplate acl(List<BucketAccessControls> acl) {
      this.acl.addAll(acl);
      return this;
   }

   public BucketTemplate addDefaultObjectAccessControls(ObjectAccessControls oac) {
      this.defaultObjectAccessControls.add(oac);
      return this;
   }

   public BucketTemplate defaultObjectAccessControls(List<ObjectAccessControls> defaultObjectAcl) {
      this.defaultObjectAccessControls.addAll(defaultObjectAcl);
      return this;
   }

   public BucketTemplate addCORS(Cors cors) {
      this.cors.add(cors);
      return this;
   }

   public BucketTemplate cors(List<Cors> cors) {
      this.cors.addAll(cors);
      return this;
   }

   public Long projectNumber() {
      return projectNumber;
   }

   public String name() {
      return name;
   }

   public List<BucketAccessControls> acl() {
      return acl;
   }

   public List<ObjectAccessControls> defaultObjectAccessControls() {
      return defaultObjectAccessControls;
   }

   public Owner owner() {
      return owner;
   }

   public Location location() {
      return location;
   }

   public Website website() {
      return website;
   }

   public Logging logging() {
      return logging;
   }

   public Versioning versioning() {
      return versioning;
   }

   public List<Cors> cors() {
      return cors;
   }

   public LifeCycle lifeCycle() {
      return lifeCycle;
   }

   public StorageClass storageClass() {
      return storageClass;
   }
}
