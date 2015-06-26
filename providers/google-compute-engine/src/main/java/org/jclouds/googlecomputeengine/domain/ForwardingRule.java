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

import java.net.URI;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ForwardingRule {

   public enum IPProtocol {
      /** IP Authentication Header protocol. */
      AH,
      /** IP Encapsulating Security Payload protocol. */
      ESP,
      /** Stream Control Transmission Protocol. */
      SCTP,
      /** Transmission Control Protocol. */
      TCP,
      /** Specifies the User Datagram Protocol. */
      UDP
   }

   public abstract String id();

   public abstract URI selfLink();

   public abstract String name();

   @Nullable public abstract String description();

   public abstract Date creationTimestamp();

   /** null when representing a GlobalForwardingRules */
   @Nullable public abstract URI region();

   /**
    * The external IP address that this forwarding rule is serving on behalf of. If this is a reserved
    * address, the address must live in the same region as the forwarding rule. By default,
    * this field is empty and  an ephemeral IP is assigned to the ForwardingRule.
    */
   @Nullable public abstract String ipAddress();

   public abstract IPProtocol ipProtocol();

   /**
    * If IPProtocol is TCP or UDP, packets addressed to ports in the specified range will be forwarded to
    * backend. By default, this is empty and all ports are allowed.
    */
   @Nullable public abstract String portRange();

   /**
    * The URL of the target resource to receive the matched traffic. The target resource must live in the
    * same region as this forwarding rule.
    */
   public abstract URI target();

   @SerializedNames(
         { "id", "selfLink", "name", "description", "creationTimestamp", "region", "IPAddress", "IPProtocol", "portRange", "target" })
   public static ForwardingRule create(String id, URI selfLink, String name, String description, Date creationTimestamp, URI region,
         String ipAddress, IPProtocol ipProtocol, String portRange, URI target) {
      return new AutoValue_ForwardingRule(id, selfLink, name, description, creationTimestamp, region, ipAddress,
            ipProtocol == null ? IPProtocol.TCP : ipProtocol, portRange, target);
   }

   ForwardingRule() {
   }

}
