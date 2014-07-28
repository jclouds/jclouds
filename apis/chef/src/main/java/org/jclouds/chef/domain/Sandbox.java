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
package org.jclouds.chef.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.chef.util.CollectionUtils.copyOfOrEmpty;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * Sandbox object.
 */
public class Sandbox {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String rev;
      private boolean isCompleted;
      private Date createTime;
      private ImmutableSet.Builder<String> checksums = ImmutableSet.builder();
      private String name;
      private String guid;

      public Builder rev(String rev) {
         this.rev = checkNotNull(rev, "rev");
         return this;
      }

      public Builder isCompleted(boolean isCompleted) {
         this.isCompleted = isCompleted;
         return this;
      }

      public Builder createTime(Date createTime) {
         this.createTime = createTime;
         return this;
      }

      public Builder checksum(String checksum) {
         this.checksums.add(checkNotNull(checksum, "checksum"));
         return this;
      }

      public Builder checksums(Iterable<String> checksums) {
         this.checksums.addAll(checkNotNull(checksums, "checksums"));
         return this;
      }

      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Builder guid(String guid) {
         this.guid = checkNotNull(guid, "guid");
         return this;
      }

      public Sandbox build() {
         return new Sandbox(rev, isCompleted, createTime, checksums.build(), name, guid);
      }
   }

   @SerializedName("_rev")
   private final String rev;
   @SerializedName("is_completed")
   private final boolean isCompleted;
   @SerializedName("create_time")
   private final Date createTime;
   private final Set<String> checksums;
   private final String name;
   private final String guid;

   // internal
   @SerializedName("json_class")
   private final String _jsonClass = "Chef::Sandbox";
   @SerializedName("chef_type")
   private final String _chefType = "sandbox";

   @ConstructorProperties({ "_rev", "is_completed", "create_time", "checksums", "name", "guid" })
   protected Sandbox(String rev, boolean isCompleted, Date createTime, @Nullable Set<String> checksums, String name,
         String guid) {
      this.rev = rev;
      this.isCompleted = isCompleted;
      this.createTime = createTime;
      this.checksums = copyOfOrEmpty(checksums);
      this.name = name;
      this.guid = guid;
   }

   public String getRev() {
      return rev;
   }

   public boolean isCompleted() {
      return isCompleted;
   }

   public Date getCreateTime() {
      return createTime;
   }

   public Set<String> getChecksums() {
      return checksums;
   }

   public String getName() {
      return name;
   }

   public String getGuid() {
      return guid;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((checksums == null) ? 0 : checksums.hashCode());
      result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
      result = prime * result + (isCompleted ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((rev == null) ? 0 : rev.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Sandbox other = (Sandbox) obj;
      if (checksums == null) {
         if (other.checksums != null)
            return false;
      } else if (!checksums.equals(other.checksums))
         return false;
      if (createTime == null) {
         if (other.createTime != null)
            return false;
      } else if (!createTime.equals(other.createTime))
         return false;
      if (guid == null) {
         if (other.guid != null)
            return false;
      } else if (!guid.equals(other.guid))
         return false;
      if (isCompleted != other.isCompleted)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (rev == null) {
         if (other.rev != null)
            return false;
      } else if (!rev.equals(other.rev))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Sandbox [checksums=" + checksums + ", createTime=" + createTime + ", guid=" + guid + ", isCompleted="
            + isCompleted + ", name=" + name + ", rev=" + rev + "]";
   }
}
