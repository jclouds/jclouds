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

@AutoValue
public abstract class FirewallRule {

   public abstract String getId();
   public abstract String getTenantId();
   public abstract String getName();
   @Nullable public abstract String getDescription();
   @Nullable public abstract String getFirewallPolicyId();
   public abstract boolean isShared();
   @Nullable public abstract String getProtocol();
   @Nullable public abstract IpVersion getIpVersion();
   @Nullable public abstract String getSourceIpAddress();
   @Nullable public abstract String getDestinationIpAddress();
   @Nullable public abstract String getSourcePort();
   @Nullable public abstract String getDestinationPort();
   @Nullable public abstract Integer getPosition(); // for AutoValue.builder
   @Nullable public abstract String getAction();
   public abstract boolean isEnabled();

   @SerializedNames({"id", "tenant_id", "name", "description", "firewall_policy_id", "shared", "protocol", "ip_version", "source_ip_address",
           "destination_ip_address", "source_port", "destination_port", "position", "action", "enabled"})
   public static FirewallRule create(String id, String tenantId, String name, String description, String firewallPolicyId, boolean shared, String protocol,
                          IpVersion ipVersion, String sourceIpAddress, String destinationIpAddress, String sourcePort, String destinationPort, int position,
                          String action, boolean enabled) {
      return new AutoValue_FirewallRule(id, tenantId, name, description, firewallPolicyId, shared, protocol, ipVersion, sourceIpAddress,
              destinationIpAddress, sourcePort, destinationPort, position, action, enabled);
   }

}
