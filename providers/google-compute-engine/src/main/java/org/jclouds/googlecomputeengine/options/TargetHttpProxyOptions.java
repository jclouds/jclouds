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
package org.jclouds.googlecomputeengine.options;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

public final class TargetHttpProxyOptions {

   private String name;
   @Nullable private String description;
   private URI urlMap;

   /**
    * Name of the TargetHttpProxy resource.
    * @return name, provided by the client.
    */
   public String getName(){
      return name;
   }

   /**
    * @see TargetHttpProxyOptions#getName()
    */
   public TargetHttpProxyOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * An optional textual description of the TargetHttpProxy.
    * @return description, provided by the client.
    */
   public String getDescription(){
      return description;
   }

   /**
    * @see TargetHttpProxyOptions#getDescription()
    */
   public TargetHttpProxyOptions description(String description) {
      this.description = description;
      return this;
   }

   /**
    * URL to the UrlMap resource that defines the mapping from URL to the BackendService.
    */
   public URI getUrlMap() {
      return urlMap;
   }

   /**
    * @see TargetHttpProxyOptions#getUrlMap()
    */
   public TargetHttpProxyOptions urlMap(URI urlMap) {
      this.urlMap = urlMap;
      return this;
   }
}
