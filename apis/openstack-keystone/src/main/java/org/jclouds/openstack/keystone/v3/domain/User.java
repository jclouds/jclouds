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

import java.util.Date;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class User {

   @AutoValue
   public abstract static class Domain {
      public abstract String id();
      public abstract String name();

      @SerializedNames({ "id", "name" })
      public static Domain create(String id, String name) {
         return new AutoValue_User_Domain(id, name);
      }
   }

   public abstract String id();
   public abstract String name();
   @Nullable public abstract Date passwordExpiresAt();
   @Nullable public abstract Domain domain();
   @Nullable public abstract String domainId();
   @Nullable public abstract String defaultProjectId();
   @Nullable public abstract Boolean enabled();
   @Nullable public abstract Link link();

   @SerializedNames({ "id", "name", "password_expires_at", "domain", "domain_id", "default_project_id", "enabled", "links" })
   public static User create(String id, String name, Date passwordExpiresAt, Domain domain, String domainId,
         String defaultProjectId, Boolean enabled, Link link) {
      return builder().id(id).name(name).passwordExpiresAt(passwordExpiresAt).domain(domain).domainId(domainId)
            .defaultProjectId(defaultProjectId).enabled(enabled).link(link).build();
   }

   User() {
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_User.Builder();
   }
   
   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder id(String id);
      public abstract Builder name(String name);
      public abstract Builder passwordExpiresAt(Date passwordExpiresAt);
      public abstract Builder domain(Domain domain);
      public abstract Builder domainId(String domainId);
      public abstract Builder defaultProjectId(String defaultProjectId);
      public abstract Builder enabled(Boolean enabled);
      public abstract Builder link(Link link);
      public abstract User build();
   }
}
