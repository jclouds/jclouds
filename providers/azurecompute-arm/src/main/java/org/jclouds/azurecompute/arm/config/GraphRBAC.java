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

import static org.jclouds.azurecompute.arm.config.AzureComputeHttpApiModule.IS_CHINA_ENDPOINT;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import com.google.common.base.Supplier;

/**
 * Provides the Graph RBAC API endpoint for the current tenant.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Qualifier
public @interface GraphRBAC {

   String STANDARD_ENDPOINT = "https://graph.windows.net/";
   String CHINA_ENDPOINT = "https://graph.chinacloudapi.cn/";

   static class GraphRBACForTenant implements Supplier<URI> {
      private final String tenantId;
      private final boolean isChinaEndpoint;

      @Inject
      GraphRBACForTenant(@Tenant String tenantId, @Named(IS_CHINA_ENDPOINT) boolean isChinaEndpoint) {
         this.tenantId = tenantId;
         this.isChinaEndpoint = isChinaEndpoint;
      }

      @Override
      public URI get() {
         return URI.create((isChinaEndpoint ? CHINA_ENDPOINT : STANDARD_ENDPOINT) + tenantId);
      }

   }
}
