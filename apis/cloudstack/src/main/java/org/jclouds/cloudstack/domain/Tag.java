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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Class Tag
 */
public class Tag {

   /**
    * Type of resource to update.
    */
   public enum ResourceType {
      USER_VM("UserVm"),
      TEMPLATE("Template"),
      ISO("ISO"),
      VOLUME("Volume"),
      SNAPSHOT("Snapshot"),
      NETWORK("Network"),
      NIC("Nic"),
      LOAD_BALANCER("LoadBalancer"),
      PORT_FORWARDING_RULE("PortForwardingRule"),
      FIREWALL_RULE("FirewallRule"),
      SECURITY_GROUP("SecurityGroup"),
      PUBLIC_IP_ADDRESS("PublicIpAddress"),
      PROJECT("Project"),
      VPC("Vpc"),
      NETWORK_ACL("NetworkACL"),
      STATIC_ROUTE("StaticRoute"),
      VM_SNAPSHOT("VMSnapshot"),
      REMOTE_ACCESS_VPN("RemoteAccessVpn"),
      ZONE("Zone"),
      SERVICE_OFFERING("ServiceOffering"),
      STORAGE("Storage"),
      PRIVATE_GATEWAY("PrivateGateway"),
      NETWORK_ACL_LIST("NetworkACLList"),
      VPN_GATEWAY("VpnGateway"),
      CUSTOMER_GATEWAY("CustomerGateway"),
      VPN_CONNECTION("VpnConnection"),
      UNRECOGNIZED("");

      private String code;

      private static final Map<String, ResourceType> INDEX = Maps.uniqueIndex(ImmutableSet.copyOf(ResourceType.values()),
              new Function<ResourceType, String>() {

                 @Override
                 public String apply(ResourceType input) {
                    return input.code;
                 }

              });

      ResourceType(String code) {
         this.code = code;
      }

      public String getCode() {
         return code;
      }

      @Override
      public String toString() {
         return code;
      }

      public static ResourceType fromValue(String resourceType) {
         String code = checkNotNull(resourceType, "resourcetype");
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromTag(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String account;
      protected String customer;
      protected String domain;
      protected String domainId;
      protected String key;
      protected String project;
      protected String projectId;
      protected String resourceId;
      protected ResourceType resourceType;
      protected String value;

      /**
       * @see Tag#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see Tag#getCustomer()
       */
      public T customer(String customer) {
         this.customer = customer;
         return self();
      }

      /**
       * @see Tag#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see Tag#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see Tag#getKey()
       */
      public T key(String key) {
         this.key = key;
         return self();
      }

      /**
       * @see Tag#getProject()
       */
      public T project(String project) {
         this.project = project;
         return self();
      }

      /**
       * @see Tag#getProjectId()
       */
      public T projectId(String projectId) {
         this.projectId = projectId;
         return self();
      }

      /**
       * @see Tag#getResourceId()
       */
      public T resourceId(String resourceId) {
         this.resourceId = resourceId;
         return self();
      }

      /**
       * @see Tag#getResourceType()
       */
      public T resourceType(ResourceType resourceType) {
         this.resourceType = resourceType;
         return self();
      }

      /**
       * @see Tag#getValue()
       */
      public T value(String value) {
         this.value = value;
         return self();
      }

      public Tag build() {
         return new Tag(account, customer, domain, domainId, key, project,
                 projectId, resourceId, resourceType, value);
      }

      public T fromTag(Tag in) {
         return this
                 .account(in.getAccount())
                 .customer(in.getCustomer())
                 .domain(in.getDomain())
                 .domainId(in.getDomainId())
                 .key(in.getKey())
                 .project(in.getProject())
                 .projectId(in.getProjectId())
                 .resourceId(in.getResourceId())
                 .resourceType(in.getResourceType())
                 .value(in.getValue());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String account;
   private final String customer;
   private final String domain;
   private final String domainId;
   private final String key;
   private final String project;
   private final String projectId;
   private final String resourceId;
   private final ResourceType resourceType;
   private final String value;

   @ConstructorProperties({
           "account", "customer", "domain", "domainid", "key", "project", "projectid", "resourceid",
            "resourcetype", "value"
   })
   protected Tag(@Nullable String account, @Nullable String customer, @Nullable String domain,
                 @Nullable String domainId, @Nullable String key, @Nullable String project,
                 @Nullable String projectId, @Nullable String resourceId,
                 @Nullable ResourceType resourceType, @Nullable String value) {
      this.account = account;
      this.customer = customer;
      this.domain = domain;
      this.domainId = domainId;
      this.key = key;
      this.project = project;
      this.projectId = projectId;
      this.resourceId = resourceId;
      this.resourceType = resourceType;
      this.value = value;
   }

   @Nullable
   public String getAccount() {
      return this.account;
   }

   @Nullable
   public String getCustomer() {
      return this.customer;
   }

   @Nullable
   public String getDomain() {
      return this.domain;
   }

   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   @Nullable
   public String getKey() {
      return this.key;
   }

   @Nullable
   public String getProject() {
      return this.project;
   }

   @Nullable
   public String getProjectId() {
      return this.projectId;
   }

   @Nullable
   public String getResourceId() {
      return this.resourceId;
   }

   @Nullable
   public ResourceType getResourceType() {
      return this.resourceType;
   }

   @Nullable
   public String getValue() {
      return this.value;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(account, customer, domain, domainId, key, project, projectId,
              resourceId, resourceType, value);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Tag that = Tag.class.cast(obj);
      return Objects.equal(this.account, that.account)
              && Objects.equal(this.customer, that.customer)
              && Objects.equal(this.domain, that.domain)
              && Objects.equal(this.domainId, that.domainId)
              && Objects.equal(this.key, that.key)
              && Objects.equal(this.project, that.project)
              && Objects.equal(this.projectId, that.projectId)
              && Objects.equal(this.resourceId, that.resourceId)
              && Objects.equal(this.resourceType, that.resourceType)
              && Objects.equal(this.value, that.value);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
              .add("account", account)
              .add("customer", customer)
              .add("domain", domain)
              .add("domainId", domainId)
              .add("key", key)
              .add("project", project)
              .add("projectId", projectId)
              .add("resourceId", resourceId)
              .add("resourceType", resourceType)
              .add("value", value);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
