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

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class Facility {

    public abstract String id();
    public abstract String name();
    public abstract String code();
    public abstract List<String> features();
    @Nullable
    public abstract Href address();

    @SerializedNames({"id", "name", "code", "features", "address"})
    public static Facility create(final String id, String name, String code, List<String> features, Href address) {
        return new AutoValue_Facility(id, name, code,
                features == null ? ImmutableList.<String> of() : ImmutableList.copyOf(features),
                address);
    }

    Facility() {}
}
