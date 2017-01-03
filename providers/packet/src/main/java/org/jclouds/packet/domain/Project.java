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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class Project {

    public abstract String id();
    public abstract String name();
    public abstract Date createdAt();
    public abstract Date updatedAt();
    public abstract Map<String, Object> maxDevices();
    public abstract List<Href> members();
    public abstract List<Href> memberships();
    public abstract List<Href> invitations();
    public abstract Href paymentMethod();
    public abstract List<Href> devices();
    public abstract List<Href> sshKeys();
    public abstract List<Href> volumes();
    public abstract String href();

    @SerializedNames({"id", "name", "created_at", "updated_at", "max_devices", "members", "memberships", "invitations", "payment_method", "devices", "ssh_keys", "volumes", "href"})
    public static Project create(String id, String name, Date createdAt, Date updatedAt, Map<String, Object> maxDevices,
                                 List<Href> members, List<Href> memberships, List<Href> invitations, Href paymentMethod,
                                 List<Href> devices,
                                 List<Href> sshKeys,
                                 List<Href> volumes,
                                 String href) {
        return new AutoValue_Project(id, name, createdAt, updatedAt,
                maxDevices == null ? ImmutableMap.<String, Object> of() : ImmutableMap.copyOf(maxDevices),
                members == null ? ImmutableList.<Href> of() : ImmutableList.copyOf(members),
                memberships == null ? ImmutableList.<Href> of() : ImmutableList.copyOf(memberships),
                invitations == null ? ImmutableList.<Href> of() : ImmutableList.copyOf(invitations),
                paymentMethod,
                devices == null ? ImmutableList.<Href> of() : ImmutableList.copyOf(devices),
                sshKeys == null ? ImmutableList.<Href> of() : ImmutableList.copyOf(sshKeys),
                volumes == null ? ImmutableList.<Href> of() : ImmutableList.copyOf(volumes),
                href);
    }

    Project() {}
}
