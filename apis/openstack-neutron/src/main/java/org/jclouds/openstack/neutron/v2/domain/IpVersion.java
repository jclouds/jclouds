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

public enum IpVersion {

   IPV4(4),
   IPV6(6),
   UNRECOGNIZED(Integer.MAX_VALUE);;

   private final int version;

   IpVersion(int version) {
      this.version = version;
   }

   public int version() {
      return this.version;
   }

   public static IpVersion fromValue(String value) {
      try {
         int statusCode = Integer.parseInt(value);
         switch (statusCode) {
            case 4:
               return IpVersion.IPV4;
            case 6:
               return IpVersion.IPV6;
            default:
               return IpVersion.IPV4;
         }
      } catch (NumberFormatException e) {
         return IpVersion.UNRECOGNIZED;
      }
   }
}
