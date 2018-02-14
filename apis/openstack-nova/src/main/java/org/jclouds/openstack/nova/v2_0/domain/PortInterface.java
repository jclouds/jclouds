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
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.jclouds.json.SerializedNames;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@AutoValue
public abstract class PortInterface {

    public enum State {
        PROVISIONING, QUEUED, ACTIVE;

        public static State fromValue(String value) {
            Optional<State> state = Enums.getIfPresent(State.class, value.toUpperCase());
            checkArgument(state.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(State.values()), value);
            return state.get();
        }
    }

    public abstract String portId();
    public abstract String netId();
    public abstract State portState();
    public abstract List<FixedIP> fixedIPS();

    @SerializedNames({"port_id", "net_id", "port_state", "fixed_ips"})
    public static PortInterface create(String portId, String netId, State portState, List<FixedIP> fixedIPS) {
        return new AutoValue_PortInterface(portId, netId, portState,
                fixedIPS == null ? ImmutableList.<FixedIP> of() : ImmutableList.copyOf(fixedIPS)
        );
    }

    PortInterface() {}

}
