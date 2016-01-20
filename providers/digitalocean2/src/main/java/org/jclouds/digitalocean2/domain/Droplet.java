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
package org.jclouds.digitalocean2.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class Droplet {
   
   public enum Status {
      NEW, ACTIVE, ARCHIVE, OFF;

      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }
   }
   
   public abstract int id();
   public abstract String name();
   public abstract int memory();
   public abstract int vcpus();
   public abstract int disk();
   public abstract boolean locked();
   public abstract Date createdAt();
   public abstract Status status();
   public abstract List<Integer> backupsIds();
   public abstract List<Integer> snapshotIds();
   public abstract List<String> features();
   @Nullable public abstract Region region();
   @Nullable public abstract Image image();
   @Nullable public abstract Size size();
   public abstract String sizeSlug();
   @Nullable  public abstract Networks networks();
   @Nullable public abstract Kernel kernel();

   @SerializedNames({ "id", "name", "memory", "vcpus", "disk", "locked", "created_at", "status", "backup_ids",
         "snapshot_ids", "features", "region", "image", "size", "size_slug", "networks", "kernel" })
   public static Droplet create(int id, String name, int memory, int vcpus, int disk, boolean locked, Date createdAt,
         Status status, List<Integer> backupIds, List<Integer> snapshotIds, List<String> features, Region region,
         Image image, Size size, String sizeSlug, Networks network, Kernel kernel) {
      return new AutoValue_Droplet(id, name, memory, vcpus, disk, locked, createdAt, status, 
            backupIds == null ? ImmutableList.<Integer> of() : copyOf(backupIds),
            snapshotIds == null ? ImmutableList.<Integer> of() : copyOf(snapshotIds), copyOf(features), region, image,
            size, sizeSlug, network, kernel);
   }

   public Set<Networks.Address> getPublicAddresses() {
      return FluentIterable.from(concat(networks().ipv4(), networks().ipv6()))
            .filter(Networks.Predicates.publicNetworks())
            .toSet();
   }

   public Set<Networks.Address> getPrivateAddresses() {
      return FluentIterable.from(concat(networks().ipv4(), networks().ipv6()))
            .filter(Networks.Predicates.privateNetworks())
            .toSet();
   }

   Droplet() {}
}
