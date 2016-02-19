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
 * OpenStack Nova Extension Names
 */
public final class ExtensionNames {
   public static final String FLOATING_IPS = "FloatingIps";
   public static final String ATTACH_INTERFACES = "AttachInterfaces";
   public static final String AVAILABILITY_ZONE = "AvailabilityZone";
   public static final String CONSOLES = "Consoles";
   public static final String CREATESERVEREXT = "Createserverext";
   public static final String FLAVOR_EXTRA_SPECS = "FlavorExtraSpecs";
   public static final String HOST_ADMINISTRATION = "Hosts";
   public static final String HOST_AGGREGATE = "Aggregates";
   public static final String HYPERVISORS = "Hypervisors";
   public static final String KEYPAIRS = "Keypairs";
   public static final String QUOTAS = "Quotas";
   public static final String SECURITY_GROUPS = "SecurityGroups";
   public static final String SERVER_ADMIN = "AdminActions";
   public static final String VIRTUAL_INTERFACES = "VirtualInterfaces";
   public static final String SIMPLE_TENANT_USAGE = "SimpleTenantUsage";
   public static final String VOLUME_ATTACHMENT = "VolumeAttachmentUpdate";
   public static final String FLOATING_IP_POOLS = "FloatingIpPools";
   public static final String VOLUMES = "Volumes";

   private ExtensionNames() {
      throw new AssertionError("intentionally unimplemented");
   }
}
