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
 * Enumerates supported HTTP methods used by probes of type HTTP/HTTPS that are sent by health monitor to verify member state.
 */
public enum HttpMethod {
   /**
    * Health monitor sends a GET HTTP request to the member.
    */
   GET("GET"),
   /**
    * Health monitor sends a POST HTTP request to the member.
    */
   POST("POST"),
   /**
    * Health monitor sends a PUT HTTP request to the member.
    */
   PUT("PUT"),
   /**
    * Health monitor sends a DELETE HTTP request to the member.
    */
   DELETE("DELETE"),
   /**
    * Health monitor sends a HEAD HTTP request to the member.
    */
   HEAD("HEAD"),
   /**
    * Used by jclouds when the service returns an unknown value other than null.
    */
   UNRECOGNIZED("unrecognized");

   private String name;

   private HttpMethod(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

   /*
    * This provides GSON enum support in jclouds.
    * */
   public static HttpMethod fromValue(String name){
      if (name != null) {
         for (HttpMethod value : HttpMethod.values()) {
            if (name.equalsIgnoreCase(value.name)) {
               return value;
            }
         }
         return UNRECOGNIZED;
      }
      return null;
   }
}
