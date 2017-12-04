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
package org.jclouds.azurecompute.arm.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;

import javax.inject.Inject;
import javax.inject.Qualifier;

import com.google.common.base.Supplier;

/**
 * Provides the Graph RBAC API endpoint for the current tenant.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Qualifier
public @interface GraphRBAC {

   String ENDPOINT = "https://graph.windows.net/";

   static class GraphRBACForTenant implements Supplier<URI> {
      private final String tenantId;

      @Inject
      GraphRBACForTenant(@Tenant String tenantId) {
         this.tenantId = tenantId;
      }

      @Override
      public URI get() {
         return URI.create(GraphRBAC.ENDPOINT + tenantId);
      }

   }
}
