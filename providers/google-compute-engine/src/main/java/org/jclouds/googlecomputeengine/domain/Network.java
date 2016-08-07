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
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;

/**
 * Represents a network used to enable instance communication.
 */
@AutoValue
public abstract class Network {

   public enum NetworkType {
      LegacyNetwork,
      AutoSubnetwork,
      CustomNetwork,
   }

   public abstract String id();

   public abstract Date creationTimestamp();

   public abstract URI selfLink();

   public abstract String name();

   public abstract NetworkType type();

   @Nullable public abstract String description();

   /**
    * The range of internal addresses that are legal on this network. This range is a CIDR
    * specification, for example: {@code 192.168.0.0/16}.
    */
   @Nullable public abstract String rangeIPv4();

   /**
    * This must be within the range specified by IPv4Range, and is typically the first usable address in that range.
    * If not specified, the default value is the first usable address in IPv4Range.
    */
   @Nullable public abstract String gatewayIPv4();

   @Nullable public abstract List<URI> subnetworks();

   public static Network create(String id, Date creationTimestamp, URI selfLink, String name, String description, String rangeIPv4,
                                String gatewayIPv4) {
      return new AutoValue_Network(id, creationTimestamp, selfLink, name, NetworkType.LegacyNetwork, description,
              rangeIPv4, gatewayIPv4, null);
   }

   @SerializedNames({ "id", "creationTimestamp", "selfLink", "name", "description", "IPv4Range", "gatewayIPv4", "autoCreateSubnetworks", "subnetworks" })
   public static Network create(String id, Date creationTimestamp, URI selfLink, String name, String description, String rangeIPv4,
         String gatewayIPv4, String autoCreateSubnetworks, List<URI> subnetworks) {
      NetworkType type;
      type = !Strings.isNullOrEmpty(rangeIPv4) ? NetworkType.LegacyNetwork
              : (autoCreateSubnetworks.equals("true") ? NetworkType.AutoSubnetwork
                  : NetworkType.CustomNetwork);
      return new AutoValue_Network(id, creationTimestamp, selfLink, name, type, description, rangeIPv4, gatewayIPv4,
              subnetworks);
   }

   Network() {
   }
}
