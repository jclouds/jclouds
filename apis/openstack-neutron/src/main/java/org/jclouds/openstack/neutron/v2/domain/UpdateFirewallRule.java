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
 * Representation of update options for an OpenStack Neutron Firewall Rule.
 *
 * @see <a
 *      href="http://docs.openstack.org/admin-guide-cloud/content/fwaas_api_abstractions.html">api
 *      doc</a>
 */
@AutoValue
public abstract class UpdateFirewallRule {

   @Nullable public abstract String getTenantId();
   @Nullable public abstract String getName();
   @Nullable public abstract String getDescription();
   @Nullable public abstract String getFirewallPolicyId();
   @Nullable public abstract Boolean getShared();
   @Nullable public abstract String getProtocol();
   @Nullable public abstract IpVersion getIpVersion();
   @Nullable public abstract String getSourceIpAddress();
   @Nullable public abstract String getDestinationIpAddress();
   @Nullable public abstract String getSourcePort();
   @Nullable public abstract String getDestinationPort();
   @Nullable public abstract Integer getPosition();
   @Nullable public abstract String getAction();
   @Nullable public abstract Boolean getEnabled();

   @SerializedNames({ "tenant_id", "name", "description", "firewall_policy_id", "shared", "protocol", "ip_version", "source_ip_address",
           "destination_ip_address", "source_port", "destination_port", "position", "action", "enabled"})
   public static UpdateFirewallRule create(String tenantId, String name, String description, String firewallPolicyId, Boolean shared, String protocol,
                                           IpVersion ipVersion, String sourceIpAddress, String destinationIpAddress, String sourcePort, String destinationPort, int position,
                                           String action, Boolean enabled) {
      return builder().tenantId(tenantId).name(name).description(description).firewallPolicyId(firewallPolicyId).shared(shared)
              .protocol(protocol).ipVersion(ipVersion).sourceIpAddress(sourceIpAddress).destinationIpAddress(destinationIpAddress).sourcePort(sourcePort)
              .destinationPort(destinationPort).position(position).action(action).enabled(enabled).build();
   }

   public static Builder builder() {
      return new AutoValue_UpdateFirewallRule.Builder().shared(false).enabled(false).position(null);
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder tenantId(String tenantId);
      public abstract Builder name(String name);
      public abstract Builder description(String description);
      public abstract Builder firewallPolicyId(String firewallPolicyId);
      public abstract Builder shared(Boolean shared);
      public abstract Builder protocol(String protocol);
      public abstract Builder ipVersion(IpVersion ipVersion);
      public abstract Builder sourceIpAddress(String sourceIpAddress);
      public abstract Builder destinationIpAddress(String destinationIpAddress);
      public abstract Builder sourcePort(String sourcePort);
      public abstract Builder destinationPort(String destinationPort);
      public abstract Builder position(Integer position);
      public abstract Builder action(String action);
      public abstract Builder enabled(Boolean enabled);

      @Nullable public abstract String getTenantId();
      @Nullable public abstract String getName();
      @Nullable public abstract String getDescription();
      @Nullable public abstract String getFirewallPolicyId();
      @Nullable public abstract Boolean getShared();
      @Nullable public abstract String getProtocol();
      @Nullable public abstract IpVersion getIpVersion();
      @Nullable public abstract String getSourceIpAddress();
      @Nullable public abstract String getDestinationIpAddress();
      @Nullable public abstract String getSourcePort();
      @Nullable public abstract String getDestinationPort();
      @Nullable public abstract Integer getPosition();
      @Nullable public abstract String getAction();
      @Nullable public abstract Boolean getEnabled();

      public abstract UpdateFirewallRule build();
   }

}
