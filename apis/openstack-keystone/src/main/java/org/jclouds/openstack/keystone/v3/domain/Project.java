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
package org.jclouds.openstack.keystone.v3.domain;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class Project {

   public abstract boolean isDomain();
   @Nullable public abstract String description();
   @Nullable public abstract String domainId();
   @Nullable public abstract String domainName();
   public abstract boolean enabled();
   @Nullable public abstract String id();
   public abstract String name();
   @Nullable public abstract String parentId();
   @Nullable public abstract List<String> tags();
   @Nullable public abstract Link link();

   @SerializedNames({ "is_domain", "description", "domain_id", "domain_name", "enabled", "id", "name", "parent_id",
         "tags", "links" })
   public static Project create(boolean isDomain, String description, String domainId, String domainName,
         boolean enabled, String id, String name, String parentId, List<String> tags, Link link) {
      return builder().isDomain(isDomain).description(description).domainId(domainId).domainName(domainName)
            .enabled(enabled).id(id).name(name).parentId(parentId).tags(tags).link(link).build();
   }

   Project() {
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_Project.Builder().isDomain(false).enabled(true);
   }
   
   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder isDomain(boolean isDomain);
      public abstract Builder description(String description);
      public abstract Builder domainId(String domainId);
      public abstract Builder domainName(String domainName);
      public abstract Builder enabled(boolean enabled);
      public abstract Builder id(String id);
      public abstract Builder name(String name);
      public abstract Builder parentId(String parentId);
      public abstract Builder tags(List<String> tags);
      public abstract Builder link(Link link);
      
      abstract List<String> tags();
      abstract Project autoBuild();
      
      public Project build() {
         tags(tags() == null ? null : ImmutableList.copyOf(tags()));
         return autoBuild();
      }
   }
}
