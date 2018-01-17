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
 * Representation of update options for an OpenStack Neutron Firewall Policy.
 *
 * @see <a
 *      href="http://docs.openstack.org/admin-guide-cloud/content/fwaas_api_abstractions.html">api
 *      doc</a>
 */
@AutoValue
public abstract class UpdateFirewallPolicy {

   @Nullable public abstract String getTenantId();
   @Nullable public abstract String getName();
   @Nullable public abstract String getDescription();
   @Nullable public abstract Boolean getShared();
   @Nullable public abstract List<String> getFirewallRules();
   @Nullable public abstract Boolean getAudited();

   @SerializedNames({"tenant_id", "name", "description", "shared", "firewall_rules", "audited"})
   private static UpdateFirewallPolicy create(String tenantId, String name, String description, Boolean shared, List<String> firewallRules, Boolean audited) {
      return builder().tenantId(tenantId).name(name).description(description).shared(shared).firewallRules(firewallRules).audited(audited).build();
   }

   public static Builder builder() {
      return new AutoValue_UpdateFirewallPolicy.Builder().shared(false).audited(false);
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder tenantId(String tenantId);
      public abstract Builder name(String name);
      public abstract Builder description(String description);
      public abstract Builder shared(Boolean shared);
      public abstract Builder firewallRules(List<String> firewallRules);
      public abstract Builder audited(Boolean audited);

      abstract UpdateFirewallPolicy autoBuild();

      @Nullable public abstract String getTenantId();
      @Nullable public abstract String getName();
      @Nullable public abstract String getDescription();
      @Nullable public abstract Boolean getShared();
      @Nullable public abstract List<String> getFirewallRules();
      @Nullable public abstract Boolean getAudited();

      public UpdateFirewallPolicy build() {
         firewallRules(getFirewallRules() != null ? ImmutableList.copyOf(getFirewallRules()) : ImmutableList.<String>of());
         return autoBuild();
      }

   }

}
