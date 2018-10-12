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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Group {

   public abstract String id();
   public abstract String name();
   @Nullable public abstract String description();
   @Nullable public abstract String domainId();
   @Nullable public abstract Link link();

   @SerializedNames({ "id", "name", "description", "domain_id", "links" })
   public static Group create(String id, String name, String description, String domainId, Link link) {
      return builder().id(id).name(name).description(description).domainId(domainId).link(link).build();
   }

   Group() {
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_Group.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder name(String string);
      public abstract Builder description(String description);
      public abstract Builder domainId(String domainId);
      public abstract Builder link(Link link);
      public abstract Group build();
   }
}
