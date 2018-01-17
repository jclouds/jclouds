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
package org.jclouds.openstack.neutron.v2.domain;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * Representation of creation options for an OpenStack Neutron Firewall Policy.
 *
 * @see <a
 *      href="http://docs.openstack.org/admin-guide-cloud/content/fwaas_api_abstractions.html">api
 *      doc</a>
 */
@AutoValue
public abstract class CreateFirewallPolicy {

   /**
    * @see Builder#tenantId(String)
    */
   @Nullable public abstract String getTenantId();

   /**
    * @see Builder#name(String)
    */
   @Nullable public abstract String getName();

   /**
    * @see Builder#description(String)
    */
   @Nullable public abstract String getDescription();

   /**
    * @see Builder#shared(Boolean)
    */
   @Nullable public abstract Boolean getShared();

   /**
    * @see Builder#firewallRules(java.util.List)
    */
   @Nullable public abstract List<String> getFirewallRules();

   /**
    * @see Builder#audited(Boolean)
    */
   @Nullable public abstract Boolean getAudited();

   @SerializedNames({"tenant_id", "name", "description", "shared", "firewall_rules", "audited"})
   private static CreateFirewallPolicy create(String tenantId, String name, String description, Boolean shared, List<String> firewallRules, Boolean audited) {
      return builder().tenantId(tenantId).name(name).description(description).shared(shared).firewallRules(firewallRules).audited(audited).build();
   }

   public static Builder builder() {
      return new AutoValue_CreateFirewallPolicy.Builder().shared(false).audited(false);
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      /**
       *
       * @param tenantId Owner of the firewall. Only admin users can specify a tenant_id other than its own.
       * @return The CreateFirewallPolicy builder.
       */
      public abstract Builder tenantId(String tenantId);

      /**
       *
       * @param name Human readable name for the firewall (255 characters limit).
       * @return The CreateFirewallPolicy builder.
       */
      public abstract Builder name(String name);

      /**
       *
       * @param description Human readable description for the firewall (1024 characters limit).
       * @return The CreateFirewallPolicy builder.
       */
      public abstract Builder description(String description);

      /**
       *
       * @param shared When set to True makes this firewall policy visible to tenants other than its owner and
       *               can be used to associate with firewalls not owned by its tenant.
       * @return The CreateFirewallPolicy builder.
       */
      public abstract Builder shared(Boolean shared);

      /**
       *
       * @param firewallRules This is an ordered list of firewall rule uuids.
       *                      The firewall applies the rules in the order in which they appear in this list.
       * @return The CreateFirewallPolicy builder.
       */
      public abstract Builder firewallRules(List<String> firewallRules);

      /**
       *
       * @param audited When set to True by the policy owner indicates that the firewall policy has been audited. This attribute is meant to aid in the
       *                firewall policy audit work flows. Each time the firewall policy or the associated firewall rules are changed, this attribute is set
       *                to False and must be explicitly set to True through an update operation.
       * @return The CreateFirewallPolicy builder.
       */
      public abstract Builder audited(Boolean audited);

      @Nullable public abstract String getTenantId();
      @Nullable public abstract String getName();
      @Nullable public abstract String getDescription();
      @Nullable public abstract Boolean getShared();
      @Nullable public abstract List<String> getFirewallRules();
      @Nullable public abstract Boolean getAudited();

      abstract CreateFirewallPolicy autoBuild();

      public CreateFirewallPolicy build() {
         firewallRules(getFirewallRules() != null ? ImmutableList.copyOf(getFirewallRules()) : ImmutableList.<String>of());
         return autoBuild();
      }
   }

}
