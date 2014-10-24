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
package org.jclouds.openstack.neutron.v2.domain.lbaas.v1;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * A Neutron LBaaS v1 HealthMonitor.
 */
public class HealthMonitor {

   // Mandatory attributes when creating
   @Named("tenant_id")
   private String tenantId;
   private ProbeType type;
   // Mandatory attributes that can be updated
   private Integer delay;
   private Integer timeout;
   @Named("max_retries")
   private Integer maxRetries;
   // Optional attributes that can be updated
   @Named("http_method")
   private HttpMethod httpMethod;
   @Named("url_path")
   private String urlPath;
   @Named("expected_codes")
   private String expectedCodes;
   @Named("admin_state_up")
   private Boolean adminStateUp;
   // Read-only attributes
   private String id;
   private ImmutableList<PoolStatus> pools;
   private LBaaSStatus status;
   @Named("status_description")
   private String statusDescription;

   /**
    * Deserialization constructor
    */
   @ConstructorProperties({ "id", "tenant_id", "type", "delay", "timeout", "max_retries", "http_method", "url_path",
         "expected_codes", "pools", "admin_state_up", "status", "status_description" })
   private HealthMonitor(String id, String tenantId, ProbeType type, Integer delay, Integer timeout,
         Integer maxRetries, HttpMethod httpMethod, String urlPath, String expectedCodes,
         ImmutableList<PoolStatus> pools, Boolean adminStateUp, LBaaSStatus status, String statusDescription) {
      this.id = id;
      this.tenantId = tenantId;
      this.type = type;
      this.delay = delay;
      this.timeout = timeout;
      this.maxRetries = maxRetries;
      this.httpMethod = httpMethod;
      this.urlPath = urlPath;
      this.expectedCodes = expectedCodes;
      this.pools = pools;
      this.adminStateUp = adminStateUp;
      this.status = status;
      this.statusDescription = statusDescription;
   }

   /**
    * Default constructor.
    */
   private HealthMonitor() {
   }

   /**
    * Copy constructor.
    *
    * @param healthMonitor the HealthMonitor to copy from.
    */
   private HealthMonitor(HealthMonitor healthMonitor) {
      this(healthMonitor.id, healthMonitor.tenantId, healthMonitor.type, healthMonitor.delay, healthMonitor.timeout,
            healthMonitor.maxRetries, healthMonitor.httpMethod, healthMonitor.urlPath, healthMonitor.expectedCodes,
            healthMonitor.pools, healthMonitor.adminStateUp, healthMonitor.status, healthMonitor.statusDescription);
   }

   /**
    * @return the id of the HealthMonitor.
    */
   @Nullable
   public String getId() {
      return id;
   }

   /**
    * @return the tenant id of the HealthMonitor.
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   /**
    * @return the probe type for this HealthMonitor.
    */
   @Nullable
   public ProbeType getType() {
      return type;
   }

   /**
    * @return the delay for this HealthMonitor.
    */
   @Nullable
   public Integer getDelay() {
      return delay;
   }

   /**
    * @return the timeout for this HealthMonitor.
    */
   @Nullable
   public Integer getTimeout() {
      return timeout;
   }

   /**
    * @return the max retries for this HealthMonitor.
    */
   @Nullable
   public Integer getMaxRetries() {
      return maxRetries;
   }

   /**
    * @return the HTTP method for this HealthMonitor.
    */
   @Nullable
   public HttpMethod getHttpMethod() {
      return httpMethod;
   }

   /**
    * @return the URL path for this HealthMonitor.
    */
   @Nullable
   public String getUrlPath() {
      return urlPath;
   }

   /**
    * @return the expected codes for this HealthMonitor.
    */
   @Nullable
   public String getExpectedCodes() {
      return expectedCodes;
   }

   /**
    * @return the pools for this HealthMonitor.
    */
   @Nullable
   public ImmutableList<PoolStatus> getPools() {
      return pools;
   }

   /**
    * @return the administrative state for this HealthMonitor.
    */
   @Nullable
   public Boolean getAdminStateUp() {
      return adminStateUp;
   }

   /**
    * @return the status for this HealthMonitor.
    */
   @Nullable
   public LBaaSStatus getStatus() {
      return status;
   }

   /**
    * @return the status description for this HealthMonitor.
    */
   @Nullable
   public String getStatusDescription() {
      return statusDescription;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      HealthMonitor that = (HealthMonitor) o;

      return Objects.equal(this.id, that.id) && Objects.equal(this.tenantId, that.tenantId)
            && Objects.equal(this.type, that.type) && Objects.equal(this.delay, that.delay)
            && Objects.equal(this.timeout, that.timeout) && Objects.equal(this.maxRetries, that.maxRetries)
            && Objects.equal(this.httpMethod, that.httpMethod) && Objects.equal(this.urlPath, that.urlPath)
            && Objects.equal(this.expectedCodes, that.expectedCodes) && Objects.equal(this.pools, that.pools)
            && Objects.equal(this.adminStateUp, that.adminStateUp) && Objects.equal(this.status, that.status)
            && Objects.equal(this.statusDescription, that.statusDescription);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, tenantId, type, delay, timeout, maxRetries, httpMethod, urlPath, expectedCodes,
            pools, adminStateUp, status, statusDescription);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("tenantId", tenantId).add("type", type)
            .add("delay", delay).add("timeout", timeout).add("maxRetries", maxRetries).add("httpMethod", httpMethod)
            .add("urlPath", urlPath).add("expectedCodes", expectedCodes).add("pools", pools)
            .add("adminStateUp", adminStateUp).add("status", status).add("statusDescription", statusDescription)
            .toString();
   }

   /*
    * Methods to get the Create and Update builders follow.
    */

   /**
    * @return the Builder for creating a new HealthMonitor.
    */
   public static CreateBuilder createBuilder(ProbeType type, Integer delay, Integer timeout, Integer maxRetries) {
      return new CreateBuilder(type, delay, timeout, maxRetries);
   }

   /**
    * @return the Builder for updating a HealthMonitor.
    */
   public static UpdateBuilder updateBuilder() {
      return new UpdateBuilder();
   }

   private abstract static class Builder<ParameterizedBuilderType> {
      protected HealthMonitor healthMonitor;

      /**
       * Default constructor.
       */
      private Builder() {
         healthMonitor = new HealthMonitor();
      }

      protected abstract ParameterizedBuilderType self();

      /**
       * Provides the delay for this HealthMonitor's Builder.
       *
       * @return the Builder.
       * @see HealthMonitor#getDelay()
       */
      public ParameterizedBuilderType delay(Integer delay) {
         healthMonitor.delay = delay;
         return self();
      }

      /**
       * Provides the timeout for this HealthMonitor's Builder.
       *
       * @return the Builder.
       * @see HealthMonitor#getTimeout()
       */
      public ParameterizedBuilderType timeout(Integer timeout) {
         healthMonitor.timeout = timeout;
         return self();
      }

      /**
       * Provides the max retries for this HealthMonitor's Builder.
       *
       * @return the Builder.
       * @see HealthMonitor#getMaxRetries()
       */
      public ParameterizedBuilderType maxRetries(Integer maxRetries) {
         healthMonitor.maxRetries = maxRetries;
         return self();
      }

      /**
       * Provides the HTTP method for this HealthMonitor's Builder.
       *
       * @return the Builder.
       * @see HealthMonitor#getHttpMethod()
       */
      public ParameterizedBuilderType httpMethod(HttpMethod httpMethod) {
         healthMonitor.httpMethod = httpMethod;
         return self();
      }

      /**
       * Provides the URL path for this HealthMonitor's Builder.
       *
       * @return the Builder.
       * @see HealthMonitor#getUrlPath()
       */
      public ParameterizedBuilderType urlPath(String urlPath) {
         healthMonitor.urlPath = urlPath;
         return self();
      }

      /**
       * Provides the expected codes for this HealthMonitor's Builder.
       *
       * @return the Builder.
       * @see HealthMonitor#getExpectedCodes()
       */
      public ParameterizedBuilderType expectedCodes(String expectedCodes) {
         healthMonitor.expectedCodes = expectedCodes;
         return self();
      }

      /**
       * Provides the administrative state for this HealthMonitor's Builder.
       *
       * @return the Builder.
       * @see HealthMonitor#getAdminStateUp()
       */
      public ParameterizedBuilderType adminStateUp(Boolean adminStateUp) {
         healthMonitor.adminStateUp = adminStateUp;
         return self();
      }
   }

   /**
    * Create builder (inheriting from Builder).
    */
   public static class CreateBuilder extends Builder<CreateBuilder> {
      /**
       * Supply required properties for creating a HealthMonitor's CreateBuilder.
       *
       * @param type the probe type.
       * @param delay the delay.
       * @param timeout the timeout.
       * @param maxRetries the max retries.
       */
      private CreateBuilder(ProbeType type, Integer delay, Integer timeout, Integer maxRetries) {
         type(type).delay(delay).timeout(timeout).maxRetries(maxRetries);
      }

      /**
       * Provides the tenantId for this HealthMonitor's CreateBuilder. Admin-only.
       * When keystone is enabled, it is not mandatory to specify tenant_id for resources in create requests, as the
       * tenant identifier will be derived from the Authentication token. Please note that the default authorization
       * settings only allow administrative users to create resources on behalf of a different tenant.
       *
       * @return the Builder.
       * @see HealthMonitor#getTenantId()
       */
      public CreateBuilder tenantId(String tenantId) {
         healthMonitor.tenantId = tenantId;
         return self();
      }

      /**
       * Provides the probe type for this HealthMonitor's Builder.
       *
       * @return the Builder.
       * @see HealthMonitor#getType()
       */
      public CreateBuilder type(ProbeType type) {
         healthMonitor.type = type;
         return self();
      }

      /**
       * @return a CreateHealthMonitor constructed with this Builder.
       */
      public CreateHealthMonitor build() {
         return new CreateHealthMonitor(healthMonitor);
      }

      @Override
      protected CreateBuilder self() {
         return this;
      }
   }

   /**
    * Update builder (inheriting from Builder).
    */
   public static class UpdateBuilder extends Builder<UpdateBuilder> {
      /**
       * Supply required properties for creating a HealthMonitor's UpdateBuilder.
       */
      private UpdateBuilder() {
      }

      /**
       * @return an UpdateHealthMonitor constructed with this Builder.
       */
      public UpdateHealthMonitor build() {
         return new UpdateHealthMonitor(healthMonitor);
      }

      @Override
      protected UpdateBuilder self() {
         return this;
      }
   }

   /**
    * Create options - extend the domain class, passed to API create calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class CreateHealthMonitor extends HealthMonitor {
      /**
       * Copy constructor.
       *
       * @param healthMonitor the HealthMonitor to copy from.
       */
      private CreateHealthMonitor(HealthMonitor healthMonitor) {
         super(healthMonitor);
      }
   }

   /**
    * Update options - extend the domain class, passed to API update calls.
    * Essentially the same as the domain class. Ensure validation and safe typing.
    */
   public static class UpdateHealthMonitor extends HealthMonitor {
      /**
       * Copy constructor.
       *
       * @param healthMonitor the HealthMonitor to copy from.
       */
      private UpdateHealthMonitor(HealthMonitor healthMonitor) {
         super(healthMonitor);
      }
   }

}
