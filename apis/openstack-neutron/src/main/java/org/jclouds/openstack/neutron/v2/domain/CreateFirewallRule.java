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
 * Representation of creation options for an OpenStack Neutron Firewall Rule.
 *
 * @see <a
 *      href="http://docs.openstack.org/admin-guide-cloud/content/fwaas_api_abstractions.html">api
 *      doc</a>
 */
@AutoValue
public abstract class CreateFirewallRule {

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
    * @see Builder#firewallPolicyId(String)
    */
   @Nullable public abstract String getFirewallPolicyId();
   /**
    * @see Builder#shared(Boolean)
    */
   @Nullable public abstract Boolean getShared();
   /**
    * @see Builder#protocol(String)
    */
   @Nullable public abstract String getProtocol();
   /**
    * @see Builder#ipVersion(IpVersion)
    */
   @Nullable public abstract IpVersion getIpVersion();
   /**
    * @see Builder#sourceIpAddress(String)
    */
   @Nullable public abstract String getSourceIpAddress();
   /**
    * @see Builder#destinationIpAddress(String)
    */
   @Nullable public abstract String getDestinationIpAddress();
   /**
    * @see Builder#sourcePort(String)
    */
   @Nullable public abstract String getSourcePort();
   /**
    * @see Builder#destinationPort(String)
    */
   @Nullable public abstract String getDestinationPort();
   /**
    * see Builder#position(Integer)
    */
   @Nullable public abstract Integer getPosition();
   /**
    * @see Builder#action(String)
    */
   @Nullable public abstract String getAction();
   /**
    * @see Builder#enabled(Boolean)
    */
   @Nullable public abstract Boolean getEnabled();

   @SerializedNames({ "tenant_id", "name", "description", "firewall_policy_id", "shared", "protocol", "ip_version", "source_ip_address",
           "destination_ip_address", "source_port", "destination_port", "position", "action", "enabled"})
   public static CreateFirewallRule create(String tenantId, String name, String description, String firewallPolicyId, Boolean shared, String protocol,
                                     IpVersion ipVersion, String sourceIpAddress, String destinationIpAddress, String sourcePort, String destinationPort, int position,
                                     String action, Boolean enabled) {
      return builder().tenantId(tenantId).name(name).description(description).firewallPolicyId(firewallPolicyId).shared(shared)
              .protocol(protocol).ipVersion(ipVersion).sourceIpAddress(sourceIpAddress).destinationIpAddress(destinationIpAddress).sourcePort(sourcePort)
              .destinationPort(destinationPort).position(position).action(action).enabled(enabled).build();
   }

   public static Builder builder() {
      return new AutoValue_CreateFirewallRule.Builder();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {

      /**
       *
       * @param tenantId Owner of the firewall. Only admin users can specify a tenant_id other than its own.
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder tenantId(String tenantId);

      /**
       *
       * @param name Human readable name for the firewall (255 characters limit).
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder name(String name);

      /**
       *
       * @param description Human readable description for the firewall (1024 characters limit).
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder description(String description);

      /**
       *
       * @param shared When set to True makes this firewall policy visible to tenants other than its owner and
       *               can be used to associate with firewalls not owned by its tenant.
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder shared(Boolean shared);

      /**
       *
       * @param firewallPolicyId This is a read-only attribute that gets populated with the uuid of the firewall policy when this firewall rule is associated
       *                         with a firewall policy. A firewall rule can be associated with only one firewall policy at a time. However, the association
       *                         can be changed to a different firewall policy.
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder firewallPolicyId(String firewallPolicyId);

      /**
       *
       * @param protocol IP protocol (icmp, tcp, udp, None).
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder protocol(String protocol);

      /**
       *
       * @param ipVersion IP version (4, 6).
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder ipVersion(IpVersion ipVersion);

      /**
       *
       * @param sourceIpAddress Source IP address or CIDR.
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder sourceIpAddress(String sourceIpAddress);

      /**
       *
       * @param destinationIpAddress Destination IP address or CIDR.
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder destinationIpAddress(String destinationIpAddress);

      /**
       *
       * @param sourcePort Source port number or a range.
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder sourcePort(String sourcePort);

      /**
       *
       * @param destinationPort Destination port number or a range.
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder destinationPort(String destinationPort);

      /**
       *
       * @param position This is a read-only attribute that gets assigned to this rule when the rule is associated with a firewall policy. It indicates the
       *                 position of this rule in that firewall policy.
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder position(Integer position);

      /**
       *
       * @param action Action to be performed on the traffic matching the rule (allow, deny).
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder action(String action);

      /**
       *
       * @param enabled When set to False, disables this rule in the firewall policy. Facilitates selectively turning off rules without having to
       *                disassociate the rule from the firewall policy.
       * @return The CreateFirewallRule builder.
       */
      public abstract Builder enabled(Boolean enabled);

      @Nullable public abstract String getTenantId();
      @Nullable public abstract String getName();
      @Nullable public abstract String getDescription();
      @Nullable public abstract Boolean getShared();
      @Nullable public abstract String getFirewallPolicyId();
      @Nullable public abstract String getProtocol();
      @Nullable public abstract IpVersion getIpVersion();
      @Nullable public abstract String getSourceIpAddress();
      @Nullable public abstract String getDestinationIpAddress();
      @Nullable public abstract String getSourcePort();
      @Nullable public abstract String getDestinationPort();
      @Nullable public abstract Integer getPosition();
      @Nullable public abstract String getAction();
      @Nullable public abstract Boolean getEnabled();

      public abstract CreateFirewallRule build();
   }

}
