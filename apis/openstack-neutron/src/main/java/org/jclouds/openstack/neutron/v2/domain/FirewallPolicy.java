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

/**
 * A firewall policy
 *
 * @see <a
 *      href="http://docs.openstack.org/admin-guide-cloud/content/fwaas_api_abstractions.html">api doc</a>
 */
@AutoValue
public abstract class FirewallPolicy {

   public abstract String getId();
   public abstract String getTenantId();
   public abstract String getName();
   @Nullable public abstract String getDescription();
   public abstract boolean isShared();
   @Nullable public abstract List<String> getFirewallRules();
   public abstract boolean isAudited();

   @SerializedNames({"id", "tenant_id", "name", "description", "shared", "firewall_rules", "audited"})
   private static FirewallPolicy create(String id, String tenantId, String name, String description, boolean shared, List<String> firewallRules, boolean audited) {
      return new AutoValue_FirewallPolicy(id, tenantId, name, description, shared, firewallRules, audited);
   }

}
