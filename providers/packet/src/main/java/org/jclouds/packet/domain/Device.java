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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkArgument;

@AutoValue
public abstract class Device {

    public enum State {
        PROVISIONING, QUEUED, ACTIVE;

        public static State fromValue(String value) {
            Optional<State> state = Enums.getIfPresent(State.class, value.toUpperCase());
            checkArgument(state.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(State.values()), value);
            return state.get();
        }
    }

    public abstract String id();
    public abstract String shortId();
    public abstract String hostname();
    @Nullable
    public abstract String description();
    public abstract State state();
    public abstract List<String> tags();
    public abstract String billingCycle();
    public abstract String user();
    public abstract String iqn();
    public abstract Boolean locked();
    public abstract String bondingMode();
    public abstract Date createdAt();
    public abstract Date updatedAt();
    public abstract OperatingSystem operatingSystem();
    public abstract Facility facility();
    public abstract Href project();
    public abstract Href projectLite();
    public abstract List<Object> volumes();
    public abstract List<IpAddress> ipAddresses();
    public abstract Plan plan();
    public abstract String rootPassword();
    public abstract String userdata();
    public abstract String href();

    @SerializedNames({"id", "short_id", "hostname", "description", "state", "tags", "billing_cycle", "user", "iqn", "locked", "bonding_mode", "created_at", "updated_at", "operating_system", "facility", "project", "project_lite", "volumes", "ip_addresses", "plan", "root_password", "userdata", "href"})
    public static Device create(String id,
                                String shortId,
                                String hostname,
                                String description,
                                State state,
                                List<String> tags,
                                String billingCycle,
                                String user,
                                String iqn,
                                Boolean locked,
                                String bondingMode,
                                Date createdAt,
                                Date updatedAt,
                                OperatingSystem operatingSystem,
                                Facility facility,
                                Href project,
                                Href projectLite,
                                List<Object> volumes,
                                List<IpAddress> ipAddresses,
                                Plan plan,
                                String rootPassword,
                                String userdata,
                                String href
    ) {
        return new AutoValue_Device(id, shortId, hostname, description, state,
                tags == null ? ImmutableList.<String> of() : ImmutableList.copyOf(tags),
                billingCycle, user, iqn, locked, bondingMode, createdAt, updatedAt, operatingSystem, facility, project, projectLite,
                volumes == null ? ImmutableList.of() : ImmutableList.copyOf(volumes),
                ipAddresses == null ? ImmutableList.<IpAddress>of() : ImmutableList.copyOf(ipAddresses),
                plan, rootPassword, userdata, href
        );
    }

    Device() {
    }

}
