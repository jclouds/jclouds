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
public abstract class Firewall {

   /** A protocol and port-range tuple that describes a permitted connection. */
   @AutoValue
   public abstract static class Rule {
      /** This can either be a well known protocol string (tcp, udp or icmp) or the IP protocol number. */
      public abstract String ipProtocol();

      /**
       * An optional list of ports which are allowed. This is only applicable for UDP or TCP protocol. Each entry must
       * be either an integer or a range (ex. {@code 12345-12349}). If not specified, connections through any port are
       * allowed.
       */
      @Nullable public abstract List<String> ports();

      @SerializedNames({ "IPProtocol", "ports" })
      public static Rule create(String ipProtocol, List<String> ports) {
         return new AutoValue_Firewall_Rule(ipProtocol, ports);
      }

      Rule() {
      }
   }

   public abstract String id();

   public abstract URI selfLink();

   public abstract Date creationTimestamp();

   public abstract String name();

   @Nullable public abstract String description();

   /**
    * @return URI of the network to which this firewall is applied; provided by the client when the firewall is created.
    */
   public abstract URI network();

   /**
    * One or both of sourceRanges and sourceTags may be set; an inbound connection is allowed if either the range or
    * the tag of the source matches.
    *
    * @return a list of IP address blocks expressed in CIDR format which this rule applies to.
    */
   public abstract List<String> sourceRanges();

   /**
    * @return a list of instance items which this rule applies to. One or both of sourceRanges and sourceTags may be
    * set; an inbound connection is allowed if either the range or the tag of the source matches.
    */
   public abstract List<String> sourceTags();

   /**
    * If no targetTags are specified, the firewall rule applies to all instances on the specified network.
    *
    * @return a list of instance items indicating sets of instances located on network which may make network
    * connections as specified in allowed.
    */
   public abstract List<String> targetTags();

   /**
    * Each rule specifies a protocol and port-range tuple that describes a permitted connection.
    *
    * @return the list of rules specified by this firewall.
    */
   public abstract List<Rule> allowed();

   @SerializedNames(
         { "id", "selfLink", "creationTimestamp", "name", "description", "network", "sourceRanges", "sourceTags", "targetTags", "allowed" })
   public static Firewall create(String id, URI selfLink, Date creationTimestamp, String name, String description, URI network,
         List<String> sourceRanges, List<String> sourceTags, List<String> targetTags, List<Rule> allowed) {
      return new AutoValue_Firewall(id, selfLink, creationTimestamp, name, description, network, copyOf(sourceRanges), copyOf(sourceTags),
            copyOf(targetTags), copyOf(allowed));
   }

   Firewall() {
   }
}
