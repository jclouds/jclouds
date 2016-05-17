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

package org.jclouds.azurecompute.arm.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class ResourceDefinition {

    public abstract String name();

    public abstract String type();

    public abstract String location();

    public abstract String apiVersion();

    @Nullable
    public abstract List<String> dependsOn();

    @Nullable
    public abstract Map<String, String> tags();

    @Nullable
    public abstract Object properties();

    @SerializedNames({"name", "type", "location", "apiVersion", "dependsOn", "tags", "properties"})
    public static ResourceDefinition create(final String name,
                                            final String type,
                                            final String location,
                                            final String apiVersion,
                                            final List<String> dependsOn,
                                            final Map<String, String> tags,
                                            final Object properties) {
        ResourceDefinition.Builder builder = ResourceDefinition.builder()
                .name(name)
                .type(type)
                .location(location)
                .apiVersion(apiVersion)
                .properties(properties);

        builder.dependsOn(dependsOn == null ? null : ImmutableList.copyOf(dependsOn));

        builder.tags(tags == null ? null : ImmutableMap.copyOf(tags));

        return  builder.build();
    }

    public static Builder builder() {
        return new AutoValue_ResourceDefinition.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder name(String name);

        public abstract Builder type(String type);

        public abstract Builder location(String location);

        public abstract Builder apiVersion(String apiVersion);

        public abstract Builder dependsOn(List<String> dependencies);

        public abstract Builder tags(Map<String, String> tags);

        public abstract Builder properties(Object properties);

        abstract List<String>  dependsOn();
        abstract Map<String, String>  tags();

        abstract ResourceDefinition autoBuild();

        public ResourceDefinition build() {
            dependsOn(dependsOn() != null ? ImmutableList.copyOf(dependsOn()) : null);
            tags(tags() != null ? ImmutableMap.copyOf(tags()) : null);
            return autoBuild();
        }
    }

}
