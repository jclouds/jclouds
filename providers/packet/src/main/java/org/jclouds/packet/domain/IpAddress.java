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
package org.jclouds.packet.domain;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class IpAddress {

    public abstract String id();
    public abstract Integer addressFamily();
    public abstract String netmask();
    public abstract Boolean publicAddress();
    public abstract Integer cidr();
    public abstract Boolean management();
    public abstract Boolean manageable();
    public abstract Href assignedTo();
    public abstract String network();
    public abstract String address();
    public abstract String gateway();
    public abstract String href();

    @SerializedNames({"id", "address_family", "netmask", "public", "cidr", "management", "manageable", "assigned_to", "network", "address", "gateway", "href"})
    public static IpAddress create(
            String id,
            Integer addressFamily,
            String netmask,
            Boolean publicAddress,
            Integer cidr,
            Boolean management,
            Boolean manageable,
            Href assignedTo,
            String network,
            String address,
            String gateway,
            String href
    ) {
        return new AutoValue_IpAddress(id, addressFamily, netmask, publicAddress, cidr, management, manageable, assignedTo, network, address, gateway, href
        );
    }

    IpAddress() {}
}
