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

/**
 * Enumerates supported Network types.
 */
public enum NetworkType {
   /**
    * Used to describe a local network.
    */
   LOCAL("local"),
   /**
    * Used to describe a flat network.
    */
   FLAT("flat"),
   /**
    * Used to describe a VLAN network. NetworkSegment might have to be set.
    */
   VLAN("vlan"),
   /**
    * Used to describe a Virtual Extensible LAN (VXLAN) network.
    * It uses a VLAN-like encapsulation technique to encapsulate MAC-based
    * OSI layer 2 Ethernet frames within layer 4 UDP packets.
    */
   VXLAN("vxlan"),
   /**
    * Used to describe a GRE tunnel network. A virtual network realized as
    * packets encapsulated using Generic Routing Encapsulation. GRE tunnel
    * packets are routed by the compute node hosts, so GRE tunnels are not
    * associated by the openvswitch plugin with specific physical networks.
    */
   GRE("gre"),
   /**
    * Used by jclouds when the service returns an unknown value other than null.
    */
   UNRECOGNIZED("unrecognized");

   private String name;

   private NetworkType(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

   /*
    * This provides GSON enum support in jclouds.
    * @param name The string representation of this enum value.
    * @return The corresponding enum value.
    */
   public static NetworkType fromValue(String name) {
      if (name != null) {
         for (NetworkType value : NetworkType.values()) {
           if (name.equalsIgnoreCase(value.name)) {
             return value;
           }
         }
         return UNRECOGNIZED;
       }
       return null;
   }
}
