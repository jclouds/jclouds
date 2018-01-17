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
 * Enumerates supported Neutron LBaaS v1 resources status.
 */
public enum LBaaSStatus {
   /**
    * The LBaaS v1 resource is ready and active.
    */
   ACTIVE("active"),
   /**
    * The LBaaS v1 resource is being created.
    */
   PENDING_CREATE("pending_create"),
   /**
    * The LBaaS v1 resource is being updated.
    */
   PENDING_UPDATE("pending_update"),
   /**
    * The LBaaS v1 resource is going to be deleted.
    */
   PENDING_DELETE("pending_delete"),
   /**
    * The LBaaS v1 resource is created but not active.
    */
   INACTIVE("inactive"),
   /**
    * The LBaaS v1 resource is in an error state.
    */
   ERROR("error"),
   /**
    * Used by jclouds when the service returns an unknown value other than null.
    */
   UNRECOGNIZED("unrecognized");

   private String name;

   private LBaaSStatus(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

   /*
    * This provides GSON enum support in jclouds.
    * */
   public static LBaaSStatus fromValue(String name){
      if (name != null) {
         for (LBaaSStatus value : LBaaSStatus.values()) {
            if (name.equalsIgnoreCase(value.name)) {
               return value;
            }
         }
         return UNRECOGNIZED;
      }
      return null;
   }
}
