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
package org.jclouds.openstack.neutron.v2.extensions;

/**
 * Extension Namespaces for OpenStack Networking (Neutron).
 */
public final class ExtensionNamespaces {
   /**
    * Neutron Layer-3 Router Extension
    */
   public static final String L3_ROUTER = "http://docs.openstack.org/ext/neutron/router/api/v1.0";
   /**
    * Neutron Security Groups Extension
    */
   public static final String SECURITY_GROUPS = "http://docs.openstack.org/ext/securitygroups/api/v2.0";

   private ExtensionNamespaces() {
      throw new AssertionError("intentionally unimplemented");
   }
}
