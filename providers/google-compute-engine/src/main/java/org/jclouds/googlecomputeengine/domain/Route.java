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
package org.jclouds.googlecomputeengine.domain;

import static org.jclouds.googlecloud.internal.NullSafeCopies.copyOf;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Route {

   public abstract String id();

   public abstract Date creationTimestamp();

   public abstract URI selfLink();

   public abstract String name();

   @Nullable public abstract String description();

   public abstract URI network();

   /** The set of instance items to which this route applies. */
   public abstract List<String> tags();

   /** The destination range of outgoing packets that this route applies to. */
   public abstract String destRange();

   /**
    * The priority of this route. Priority is used to break ties in the case
    * where there is more than one matching route of maximum length. A lower value
    * is higher priority; a priority of 100 is higher than 200.
    */
   public abstract int priority();

   /** The fully-qualified URL to an instance that should handle matching packets. */
   @Nullable public abstract URI nextHopInstance();

   /** The network IP address of an instance that should handle matching packets. */
   @Nullable public abstract String nextHopIp();

   /** The local network if it should handle matching packets. */
   @Nullable public abstract URI nextHopNetwork();

   /** The gateway that should handle matching packets. Currently, this is only the internet gateway. */
   @Nullable public abstract URI nextHopGateway();

   /** Potential misconfigurations are detected for this route. */
   public abstract List<Warning> warnings();

   /** The URL to a VpnTunnel that should handle matching packets. */
   @Nullable public abstract URI nextHopVpnTunnel();

   @SerializedNames(
         { "id", "creationTimestamp", "selfLink", "name", "description", "network", "tags", "destRange", "priority", "nextHopInstance",
               "nextHopIp", "nextHopNetwork", "nextHopGateway", "warnings", "nextHopVpnTunnel" })
   public static Route create(String id, Date creationTimestamp, URI selfLink, String name, String description, URI network, List<String> tags,
         String destRange, int priority, URI nextHopInstance, String nextHopIp, URI nextHopNetwork, URI nextHopGateway,
         List<Warning> warnings, URI nextHopVpnTunnel) {
      return new AutoValue_Route(id, creationTimestamp, selfLink, name, description, network, copyOf(tags), destRange, priority,
            nextHopInstance, nextHopIp, nextHopNetwork, nextHopGateway, copyOf(warnings), nextHopVpnTunnel);
   }

   Route() {
   }
}
