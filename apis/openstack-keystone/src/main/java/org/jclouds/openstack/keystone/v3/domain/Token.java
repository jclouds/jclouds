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
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import org.jclouds.openstack.keystone.auth.domain.AuthInfo;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class Token implements AuthInfo {

   @Nullable public abstract String id();
   public abstract List<String> methods();
   @Nullable public abstract Date expiresAt();
   @Nullable public abstract Object extras();
   @Nullable public abstract List<Catalog> catalog();
   @Nullable public abstract List<String> auditIds();
   public abstract User user();
   public abstract Date issuedAt();
   
   @Override
   public String getAuthToken() {
      return id();
   }
   
   @SerializedNames({ "id", "methods", "expires_at", "extras", "catalog", "audit_ids", "user", "issued_at" })
   private static Token create(String id, List<String> methods, Date expiresAt, Object extras, List<Catalog> catalog,
         List<String> auditIds, User user, Date issuedAt) {
      return builder().id(id).methods(methods).expiresAt(expiresAt).extras(extras).catalog(catalog).auditIds(auditIds)
            .user(user).issuedAt(issuedAt).build();
   }

   Token() {
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_Token.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {

      public abstract Builder id(String id);
      public abstract Builder methods(List<String> methods);
      public abstract Builder expiresAt(Date expiresAt);
      public abstract Builder extras(Object extras);
      public abstract Builder catalog(List<Catalog> catalog);
      public abstract Builder auditIds(List<String> auditIds);
      public abstract Builder user(User user);
      public abstract Builder issuedAt(Date issuedAt);

      abstract List<Catalog> catalog();
      abstract List<String> methods();
      abstract List<String> auditIds();

      abstract Token autoBuild();

      public Token build() {
         return catalog(catalog() != null ? ImmutableList.copyOf(catalog()) : ImmutableList.<Catalog>of())
                 .methods(methods() != null ? ImmutableList.copyOf(methods()) : ImmutableList.<String>of())
                 .auditIds(auditIds() != null ? ImmutableList.copyOf(auditIds()) : ImmutableList.<String>of())
                 .autoBuild();
      }
   }
}
