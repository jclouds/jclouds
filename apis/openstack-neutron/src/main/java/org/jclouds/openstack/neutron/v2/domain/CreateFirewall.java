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
 * Representation of creation options for an OpenStack Neutron Firewall.
 *
 * @see <a
 *      href="http://docs.openstack.org/admin-guide-cloud/content/fwaas_api_abstractions.html">api
 *      doc</a>
 */
@AutoValue
public abstract class CreateFirewall {

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
    * @see Builder#adminStateUp(Boolean)
    */
   @Nullable public abstract Boolean getAdminStateUp();

   /**
    * @see Builder#firewallPolicyId(String)
    */
   @Nullable public abstract String getFirewallPolicyId();

   @SerializedNames({ "tenant_id", "name", "description", "admin_state_up", "firewall_policy_id"})
   public static CreateFirewall create(String tenantId, String name, String description, Boolean adminStateUp, String firewallPolicyId) {
      return builder().tenantId(tenantId).name(name).description(description).adminStateUp(adminStateUp)
              .firewallPolicyId(firewallPolicyId).build();
   }

   public static Builder builder() {
      return new AutoValue_CreateFirewall.Builder();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      /**
       *
       * @param tenantId Owner of the firewall. Only admin users can specify a tenant_id other than its own.
       * @return The CreateFirewall builder.
       */
      public abstract Builder tenantId(String tenantId);

      /**
       *
       * @param name Human readable name for the firewall (255 characters limit).
       * @return The CreateFirewall builder.
       */
      public abstract Builder name(String name);

      /**
       *
       * @param description Human readable description for the firewall (1024 characters limit).
       * @return The CreateFirewall builder.
       */
      public abstract Builder description(String description);

      /**
       *
       * @param adminStateUp The administrative state of the firewall. If False (down), the firewall does not forward any packets.
       * @return The CreateFirewall builder.
       */
      public abstract Builder adminStateUp(Boolean adminStateUp);

      /**
       *
       * @param firewallPolicyId The firewall policy uuid that this firewall is associated with. This firewall implements the rules contained in the
       *                         firewall policy represented by this uuid.
       *
       * @return The CreateFirewall builder.
       */
      public abstract Builder firewallPolicyId(String firewallPolicyId);

      @Nullable public abstract String getTenantId();
      @Nullable public abstract String getName();
      @Nullable public abstract String getDescription();
      @Nullable public abstract Boolean getAdminStateUp();
      @Nullable public abstract String getFirewallPolicyId();

      public abstract CreateFirewall build();
   }

}
