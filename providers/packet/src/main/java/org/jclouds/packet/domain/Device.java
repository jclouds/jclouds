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
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkArgument;

@AutoValue
public abstract class Device {

    public enum State {
        PROVISIONING, QUEUED, ACTIVE, REBOOTING, POWERING_OFF, POWERING_ON, INACTIVE;

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
    public abstract List<Href> sshKeys();
    public abstract Href projectLite();
    public abstract List<Object> volumes();
    public abstract List<IpAddress> ipAddresses();
    public abstract List<ProvisioningEvent> provisioningEvents();
    public abstract Plan plan();
    @Nullable public abstract String rootPassword();
    public abstract String userdata();
    public abstract String href();

    @SerializedNames({"id", "short_id", "hostname", "description", "state", "tags", "billing_cycle", "user", "iqn",
            "locked", "bonding_mode", "created_at", "updated_at", "operating_system", "facility", "project", "ssh_keys",
            "project_lite", "volumes", "ip_addresses", "provisioning_events", "plan", "root_password", "userdata", "href"})
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
                                List<Href> sshKeys,
                                Href projectLite,
                                List<Object> volumes,
                                List<IpAddress> ipAddresses,
                                List<ProvisioningEvent> provisioningEvents,
                                Plan plan,
                                String rootPassword,
                                String userdata,
                                String href
    ) {
        return new AutoValue_Device(id, shortId, hostname, description, state,
                tags == null ? ImmutableList.<String> of() : ImmutableList.copyOf(tags),
                billingCycle, user, iqn, locked, bondingMode, createdAt, updatedAt, operatingSystem, facility, project,
                sshKeys == null ? ImmutableList.<Href> of() : ImmutableList.copyOf(sshKeys),
                projectLite,
                volumes == null ? ImmutableList.of() : ImmutableList.copyOf(volumes),
                ipAddresses == null ? ImmutableList.<IpAddress>of() : ImmutableList.copyOf(ipAddresses),
                provisioningEvents == null ? ImmutableList.<ProvisioningEvent> of() : ImmutableList.copyOf(provisioningEvents),
                plan,
                rootPassword, userdata, href
        );
    }

    Device() {
    }

    @AutoValue
    public abstract static class CreateDevice {

        public abstract String hostname();
        public abstract String plan();
        public abstract String billingCycle();
        public abstract String facility();
        public abstract Map<String, String> features();
        public abstract String operatingSystem();
        public abstract Boolean locked();
        public abstract String userdata();
        public abstract Set<String> tags();

        @SerializedNames({"hostname", "plan", "billing_cycle", "facility", "features", "operating_system",
                "locked", "userdata", "tags" })
        private static CreateDevice create(final String hostname, final String plan, final String billingCycle,
                                          final String facility, final Map<String, String> features, final String operatingSystem,
                                          final Boolean locked, final String userdata,
                                          final Set<String> tags) {
            return builder()
                    .hostname(hostname)
                    .plan(plan)
                    .billingCycle(billingCycle)
                    .facility(facility)
                    .features(features)
                    .operatingSystem(operatingSystem)
                    .locked(locked)
                    .userdata(userdata)
                    .tags(tags)
                    .build();
        }

        public static Builder builder() {
            return new AutoValue_Device_CreateDevice.Builder();
        }

        @AutoValue.Builder
        public abstract static class Builder {

            public abstract Builder hostname(String hostname);
            public abstract Builder plan(String plan);
            public abstract Builder billingCycle(String billingCycle);
            public abstract Builder facility(String facility);
            public abstract Builder features(Map<String, String> features);
            public abstract Builder operatingSystem(String operatingSystem);
            public abstract Builder locked(Boolean locked);
            public abstract Builder userdata(String userdata);
            public abstract Builder tags(Set<String> tags);

           abstract Map<String, String> features();
           abstract Set<String> tags();

           abstract CreateDevice autoBuild();

           public CreateDevice build() {
              return tags(tags() != null ? ImmutableSet.copyOf(tags()) : ImmutableSet.<String> of())
                      .features(features() != null ? ImmutableMap.copyOf(features()) : ImmutableMap.<String, String> of())
                      .autoBuild();
           }
        }
    }

}
