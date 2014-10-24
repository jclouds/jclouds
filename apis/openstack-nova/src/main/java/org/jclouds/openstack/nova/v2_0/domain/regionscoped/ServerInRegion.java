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
package org.jclouds.openstack.nova.v2_0.domain.regionscoped;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.openstack.nova.v2_0.domain.Server;

import com.google.common.base.Objects.ToStringHelper;

public class ServerInRegion extends RegionAndId {
   protected final Server server;

   public ServerInRegion(Server server, String regionId) {
      super(regionId, checkNotNull(server, "server").getId());
      this.server = server;
   }

   public Server getServer() {
      return server;
   }

   // superclass hashCode/equals are good enough, and help us use RegionAndId and ServerInRegion
   // interchangeably as Map keys

   @Override
   protected ToStringHelper string() {
      return super.string().add("server", server);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
