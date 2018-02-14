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
package org.jclouds.openstack.nova.v2_0.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;

@AutoValue
public abstract class FloatingIpForServer {

   public abstract RegionAndId serverId();
   public abstract String floatingIpId();
   public abstract String ip();

   public static FloatingIpForServer create(RegionAndId serverId,
                               String floatingIpId,
                               String ip
   ) {
      return new AutoValue_FloatingIpForServer(serverId, floatingIpId, ip);
   }

   FloatingIpForServer() {
   }
}
