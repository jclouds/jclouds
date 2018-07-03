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
package org.jclouds.azurecompute.arm.domain.vpn;

import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnection.Status;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TunnelConnectionHealth {

   public abstract Status connectionStatus();
   public abstract int egressBytesTransferred();
   public abstract int ingressBytesTransferred();
   @Nullable public abstract String lastConnectionEstablishedUtcTime();
   public abstract String tunnel();

   TunnelConnectionHealth() {

   }

   @SerializedNames({ "connectionStatus", "egressBytesTransferred", "ingressBytesTransferred",
         "lastConnectionEstablishedUtcTime", "tunnel" })
   public static TunnelConnectionHealth create(Status connectionStatus, int egressBytesTransferred,
         int ingressBytesTransferred, String lastConnectionEstablishedUtcTime, String tunnel) {
      return new AutoValue_TunnelConnectionHealth(connectionStatus, egressBytesTransferred, ingressBytesTransferred,
            lastConnectionEstablishedUtcTime, tunnel);
   }
}
