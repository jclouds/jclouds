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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;


/**
 * Representation of update options for an OpenStack Neutron Firewall.
 *
 * @see <a
 * href="http://docs.openstack.org/admin-guide-cloud/content/fwaas_api_abstractions.html">api
 * doc</a>
 */
@AutoValue
public abstract class UpdateFirewall {

   @Nullable public abstract String getTenantId();
   @Nullable public abstract String getName();
   @Nullable public abstract String getDescription();
   @Nullable public abstract Boolean getAdminStateUp();
   @Nullable public abstract String getFirewallPolicyId();

   @SerializedNames({"tenant_id", "name", "description", "admin_state_up", "firewall_policy_id"})
   public static UpdateFirewall create(String tenantId, String name, String description, Boolean adminStateUp, String firewallPolicyId) {
      return builder().tenantId(tenantId).name(name).description(description).adminStateUp(adminStateUp).firewallPolicyId(firewallPolicyId).build();
   }

   public static Builder builder() {
      return new AutoValue_UpdateFirewall.Builder();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder tenantId(String tenantId);
      public abstract Builder name(String name);
      public abstract Builder description(String description);
      public abstract Builder adminStateUp(Boolean adminStateUp);
      public abstract Builder firewallPolicyId(String firewallPolicyId);

      @Nullable public abstract String getTenantId();
      @Nullable public abstract String getName();
      @Nullable public abstract String getDescription();
      @Nullable public abstract Boolean getAdminStateUp();
      @Nullable public abstract String getFirewallPolicyId();

      public abstract UpdateFirewall build();
   }
}
