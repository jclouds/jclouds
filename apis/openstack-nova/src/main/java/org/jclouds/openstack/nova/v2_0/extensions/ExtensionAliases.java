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
package org.jclouds.openstack.nova.v2_0.extensions;

/**
 * OpenStack Nova Extension Aliases
 */
public final class ExtensionAliases {
   public static final String FLOATING_IPS = "os-floating-ips";
   public static final String ATTACH_INTERFACES = "os-attach-interfaces";
   public static final String AVAILABILITY_ZONE = "os-availability-zone";
   public static final String CONSOLES = "os-consoles";
   public static final String CREATESERVEREXT = "os-create-server-ext";
   public static final String FLAVOR_EXTRA_SPECS = "os-flavor-extra-specs";
   public static final String HOST_ADMINISTRATION = "os-hosts";
   public static final String HOST_AGGREGATE = "os-aggregates";
   public static final String HYPERVISORS = "os-hypervisors";
   public static final String KEYPAIRS = "os-keypairs";
   public static final String QUOTAS = "os-quota-sets";
   public static final String SECURITY_GROUPS = "os-security-groups";
   public static final String SERVER_ADMIN = "os-admin-actions";
   public static final String VIRTUAL_INTERFACES = "os-virtual-interfaces";
   public static final String SIMPLE_TENANT_USAGE = "os-simple-tenant-usage";
   public static final String VOLUME_ATTACHMENT = "os-volumes";
   public static final String FLOATING_IP_POOLS = "os-floating-ip-pools";
   public static final String VOLUMES = "os-volumes";

   private ExtensionAliases() {
      throw new AssertionError("intentionally unimplemented");
   }
}
