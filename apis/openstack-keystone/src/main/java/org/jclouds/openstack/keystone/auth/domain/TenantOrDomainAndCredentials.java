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
package org.jclouds.openstack.keystone.auth.domain;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Keystone credentials with tenant. Configure the tenant properties to the
 * configured context credentials.
 */
@AutoValue
public abstract class TenantOrDomainAndCredentials<T> {

   @Nullable public abstract String tenantOrDomainId();
   @Nullable public abstract String tenantOrDomainName();
   @Nullable public abstract String scope();
   public abstract T credentials();

   TenantOrDomainAndCredentials() {

   }

   public static <T> Builder<T> builder() {
      return new AutoValue_TenantOrDomainAndCredentials.Builder<T>();
   }

   @AutoValue.Builder
   public abstract static class Builder<T> {
      public abstract Builder<T> tenantOrDomainId(String tenantId);
      public abstract Builder<T> tenantOrDomainName(String tenantName);
      public abstract Builder<T> scope(String scope);
      public abstract Builder<T> credentials(T credentials);

      public abstract TenantOrDomainAndCredentials<T> build();
   }
}
