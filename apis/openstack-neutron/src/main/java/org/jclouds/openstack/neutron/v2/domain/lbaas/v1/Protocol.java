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

package org.jclouds.openstack.neutron.v2.domain.lbaas.v1;

/**
 * Enumerates supported protocols.
 * Protocol must be specified for the front-end (see {@link VIP}) and for the back-end instances (see {@link Pool}).
 */
public enum Protocol {
   /**
    * Use TCP for routing traffic.
    */
   TCP("TCP"),
   /**
    * Use HTTP for routing traffic.
    */
   HTTP("HTTP"),
   /**
    * Use HTTPS for routing traffic.
    */
   HTTPS("HTTPS"),
   /**
    * Used by jclouds when the service returns an unknown value other than null.
    */
   UNRECOGNIZED("unrecognized");

   private String name;

   private Protocol(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

   /*
    * This provides GSON enum support in jclouds.
    * */
   public static Protocol fromValue(String name){
      if (name != null) {
         for (Protocol value : Protocol.values()) {
            if (name.equalsIgnoreCase(value.name)) {
               return value;
            }
         }
         return UNRECOGNIZED;
      }
      return null;
   }
}
