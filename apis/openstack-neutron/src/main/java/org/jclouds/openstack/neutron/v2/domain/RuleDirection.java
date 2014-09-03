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
 * The direction in which the security group rule is applied.
 */
public enum RuleDirection {
   /**
    * For a compute instance, an ‘ingress’ security group rule matches traffic that is incoming (ingress) for that instance.
    */
   INGRESS("ingress"),
   /**
    * An ‘egress’ rule is applied to traffic leaving the instance.
    */
   EGRESS("egress"),
   /**
    * Used by jclouds when the service returns an unknown value other than null.
    */
   UNRECOGNIZED("unrecognized");

   private String name;

   private RuleDirection(String name) {
      this.name = name;
   }

   public String toString() {
      return name;
   }

   /*
    * This provides GSON enum support in jclouds.
    * */
   public static RuleDirection fromValue(String name){
      if (name != null) {
         for (RuleDirection value : RuleDirection.values()) {
            if (name.equalsIgnoreCase(value.name)) {
               return value;
            }
         }
         return UNRECOGNIZED;
      }
      return null;
   }
}
